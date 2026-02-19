package org.sagebionetworks.agora.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.agora.api.next.util.ApiHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation using MongoDB aggregation pipeline.
 *
 * <p>Uses aggregation to support sorting by array fields. MongoDB cannot sort by multiple
 * array fields simultaneously ("parallel arrays"), so we compute scalar sort fields by
 * sorting array elements and concatenating them into a string for lexicographic comparison.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomNominatedTargetRepositoryImpl implements CustomNominatedTargetRepository {

  private static final String COLLECTION_NAME = "nominatedtargets";
  private static final String PRIMARY_FIELD = "hgnc_symbol";

  /** Array fields that need computed fields for custom sort handling */
  private static final Set<String> ARRAY_FIELDS = Set.of(
    "nominating_teams",
    "cohort_studies",
    "input_data",
    "programs"
  );

  private final MongoTemplate mongoTemplate;

  @Override
  public Page<NominatedTargetDocument> findAll(
    Pageable pageable,
    NominatedTargetSearchQueryDto query,
    List<String> items
  ) {
    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    String search = query.getSearch();

    // Build match criteria with all filters
    Criteria matchCriteria = buildMatchCriteria(query, items, filterType, search);

    // Count total using simple query (faster than aggregation)
    long total = mongoTemplate.count(new Query(matchCriteria), COLLECTION_NAME);

    // Build aggregation pipeline
    List<AggregationOperation> operations = new ArrayList<>();

    // $match first to filter documents
    operations.add(Aggregation.match(matchCriteria));

    // Add computed sort fields for arrays
    buildSortFields(operations, pageable.getSort());

    // Add $sort using computed fields
    addSortOperation(operations, pageable.getSort());

    // Add pagination
    long skipCount = (long) pageable.getPageNumber() * pageable.getPageSize();
    operations.add(Aggregation.skip(skipCount));
    operations.add(Aggregation.limit(pageable.getPageSize()));

    Aggregation aggregation = Aggregation.newAggregation(operations);

    log.debug("Executing aggregation pipeline: {}", aggregation);

    AggregationResults<NominatedTargetDocument> results = mongoTemplate.aggregate(
      aggregation,
      COLLECTION_NAME,
      NominatedTargetDocument.class
    );

    return new PageImpl<>(results.getMappedResults(), pageable, total);
  }

  /**
   * Builds match criteria combining all filters.
   */
  private Criteria buildMatchCriteria(
    NominatedTargetSearchQueryDto query,
    List<String> items,
    ItemFilterTypeQueryDto filterType,
    String search
  ) {
    List<Criteria> andCriteria = new ArrayList<>();

    // Add data filters (AND between fields, OR within field)
    addDataFilterCriteria(
      query.getCohortStudies(),
      query.getInputData(),
      query.getInitialNomination(),
      query.getNominatingTeams(),
      query.getPharosClass(),
      query.getPrograms(),
      query.getTotalNominations(),
      andCriteria
    );

    // Add hgnc_symbol filtering (items + itemFilterType)
    addHgncSymbolFilterCriteria(items, filterType, andCriteria);

    // Add search filtering (only when itemFilterType is EXCLUDE)
    addSearchFilterCriteria(search, filterType, andCriteria);

    if (andCriteria.isEmpty()) {
      return new Criteria();
    }
    return new Criteria().andOperator(andCriteria.toArray(new Criteria[0]));
  }

  /**
   * Builds $addFields operation to create sortable versions of fields.
   *
   * <p>For array fields: concatenates array elements into a single string
   * for proper lexicographic comparison. This enables multi-array sorting (avoiding MongoDB's
   * "parallel arrays" limitation).
   */
  private void buildSortFields(List<AggregationOperation> operations, Sort sort) {
    Document fields = new Document();

    for (Sort.Order order : sort) {
      String field = order.getProperty();

      if (ARRAY_FIELDS.contains(field)) {
        // For arrays: concatenate into string, lowercase for comparison
        fields.append(field + "_sort", buildArraySortField(field));
      }
    }

    if (!fields.isEmpty()) {
      operations.add(context -> new Document("$addFields", fields));
    }
  }

  /**
   * Builds a MongoDB expression to create a sortable string from an array field.
   *
   * <p>Formula: $toLower($reduce(field))
   *
   * <p>Example: ["Duke", "Emory", "Columbia"] → "duke\0emory\0columbia"
   *
   * @param field the array field name
   * @return Document representing the aggregation expression
   */
  private Document buildArraySortField(String field) {
    // $reduce: concatenate elements with null separator
    Document reduce = new Document(
      "$reduce",
      new Document()
        .append("input", "$" + field)
        .append("initialValue", "")
        .append("in", new Document("$concat", List.of("$$value", "\u0000", "$$this")))
    );

    // $toLower: case-insensitive comparison
    return new Document("$toLower", reduce);
  }

  /**
   * Adds $sort operation using computed sort fields where applicable.
   */
  private void addSortOperation(List<AggregationOperation> operations, Sort sort) {
    if (sort.isUnsorted()) {
      return;
    }

    Document sortDocument = new Document();
    for (Sort.Order order : sort) {
      String field = order.getProperty();
      int direction = order.isAscending() ? 1 : -1;

      // Use computed _sort field for arrays
      if (ARRAY_FIELDS.contains(field)) {
        sortDocument.append(field + "_sort", direction);
      } else {
        sortDocument.append(field, direction);
      }
    }

    if (!sortDocument.isEmpty()) {
      operations.add(context -> new Document("$sort", sortDocument));
    }
  }

  private void addDataFilterCriteria(
    List<String> cohortStudies,
    List<String> inputData,
    List<Integer> initialNomination,
    List<String> nominatingTeams,
    List<String> pharosClass,
    List<String> programs,
    List<Integer> totalNominations,
    List<Criteria> andCriteria
  ) {
    // cohort_studies: array field - use $in (matches if array contains any value)
    if (cohortStudies != null && !cohortStudies.isEmpty()) {
      andCriteria.add(Criteria.where("cohort_studies").in(cohortStudies));
    }

    // input_data: array field - use $in (matches if array contains any value)
    if (inputData != null && !inputData.isEmpty()) {
      andCriteria.add(Criteria.where("input_data").in(inputData));
    }

    // initialNomination: array field - use $in (matches if array contains any value)
    if (initialNomination != null && !initialNomination.isEmpty()) {
      andCriteria.add(Criteria.where("initial_nomination").in(initialNomination));
    }

    // nominatingTeams: array field - use $in (matches if array contains any value)
    if (nominatingTeams != null && !nominatingTeams.isEmpty()) {
      andCriteria.add(Criteria.where("nominating_teams").in(nominatingTeams));
    }

    // pharosClass: array field - use $in (matches if array contains any value)
    if (pharosClass != null && !pharosClass.isEmpty()) {
      andCriteria.add(Criteria.where("pharos_class").in(pharosClass));
    }

    // programs: array field - use $in (matches if array contains any value)
    if (programs != null && !programs.isEmpty()) {
      andCriteria.add(Criteria.where("programs").in(programs));
    }

    // totalNominations: array field - use $in (matches if array contains any value)
    if (totalNominations != null && !totalNominations.isEmpty()) {
      andCriteria.add(Criteria.where("total_nominations").in(totalNominations));
    }
  }

  private void addHgncSymbolFilterCriteria(
    List<String> items,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    if (items.isEmpty()) {
      // For INCLUDE mode with empty items, add impossible condition to return empty results
      if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
        andCriteria.add(Criteria.where("_id").is(null));
      }
      // For EXCLUDE mode with empty items, no filtering needed (return all)
      return;
    }

    if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
      andCriteria.add(Criteria.where(PRIMARY_FIELD).in(items));
    } else {
      andCriteria.add(Criteria.where(PRIMARY_FIELD).nin(items));
    }
  }

  private void addSearchFilterCriteria(
    String search,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    // Search only applies when itemFilterType is EXCLUDE
    if (filterType != ItemFilterTypeQueryDto.EXCLUDE || search == null || search.trim().isEmpty()) {
      return;
    }

    String trimmedSearch = search.trim();
    if (trimmedSearch.contains(",")) {
      // Comma-separated list: case-insensitive full matches
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      andCriteria.add(Criteria.where(PRIMARY_FIELD).in(patterns));
    } else {
      // Single term: case-insensitive partial match
      String quotedSearch = Pattern.quote(trimmedSearch);
      andCriteria.add(Criteria.where(PRIMARY_FIELD).regex(quotedSearch, "i"));
    }
  }
}
