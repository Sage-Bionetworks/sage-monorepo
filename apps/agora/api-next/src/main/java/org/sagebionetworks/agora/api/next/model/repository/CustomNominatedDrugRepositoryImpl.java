package org.sagebionetworks.agora.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugIdentifier;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugSearchQueryDto;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation using MongoDB aggregation pipeline.
 *
 * <p>Uses aggregation to support sorting by array fields. MongoDB cannot sort by multiple
 * array fields simultaneously ("parallel arrays"), so we compute scalar sort fields for
 * lexicographic comparison. The pipeline scaffold (count, $match, $addFields, $sort, $skip,
 * $limit) lives in {@link ComparisonToolRepositorySupport}.
 */
@Repository
@Slf4j
public class CustomNominatedDrugRepositoryImpl
  extends ComparisonToolRepositorySupport<NominatedDrugDocument>
  implements CustomNominatedDrugRepository {

  private static final String COLLECTION_NAME = "nominateddrugs";
  private static final String SEARCH_FIELD = "common_name";

  public CustomNominatedDrugRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<NominatedDrugDocument> getDocumentClass() {
    return NominatedDrugDocument.class;
  }

  /**
   * Array fields are reduced to a NUL-separated lowercase string so they can sort
   * alongside other array fields without hitting MongoDB's "parallel arrays" limit.
   */
  @Override
  protected Map<String, Object> getComputedSortFieldExpressions() {
    return Map.of(
      "principal_investigators", arrayToLoweredStringExpr("principal_investigators"),
      "programs", arrayToLoweredStringExpr("programs")
    );
  }

  @Override
  public Page<NominatedDrugDocument> findAll(
    Pageable pageable,
    NominatedDrugSearchQueryDto query,
    List<String> items
  ) {
    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    String search = query.getSearch();

    // Build match criteria with all filters
    Criteria matchCriteria = buildMatchCriteria(query, items, filterType, search);

    return executePagedAggregation(matchCriteria, pageable);
  }

  /**
   * Builds match criteria combining all filters.
   */
  private Criteria buildMatchCriteria(
    NominatedDrugSearchQueryDto query,
    List<String> items,
    ItemFilterTypeQueryDto filterType,
    String search
  ) {
    List<Criteria> andCriteria = new ArrayList<>();

    // Add data filters (AND between fields, OR within field)
    addDataFilterCriteria(
      query.getPrincipalInvestigators(),
      query.getPrograms(),
      query.getTotalNominations(),
      query.getInitialNomination(),
      query.getModality(),
      andCriteria
    );

    // Add composite_id filtering (items + itemFilterType)
    addItemFilterCriteria(items, filterType, andCriteria);

    // Add search filtering (only when itemFilterType is EXCLUDE)
    addSearchFilterCriteria(search, filterType, andCriteria);

    if (andCriteria.isEmpty()) {
      return new Criteria();
    }
    return new Criteria().andOperator(andCriteria.toArray(new Criteria[0]));
  }

  private void addDataFilterCriteria(
    List<String> principalInvestigators,
    List<String> programs,
    List<Integer> totalNominations,
    List<Integer> initialNomination,
    List<String> modality,
    List<Criteria> andCriteria
  ) {
    // principal_investigators: array field - use $in (matches if array contains any value)
    if (principalInvestigators != null && !principalInvestigators.isEmpty()) {
      andCriteria.add(Criteria.where("principal_investigators").in(principalInvestigators));
    }

    // programs: array field - use $in (matches if array contains any value)
    if (programs != null && !programs.isEmpty()) {
      andCriteria.add(Criteria.where("programs").in(programs));
    }

    // totalNominations: scalar field - use $in
    if (totalNominations != null && !totalNominations.isEmpty()) {
      andCriteria.add(Criteria.where("total_nominations").in(totalNominations));
    }

    // initialNomination: scalar field - use $in
    if (initialNomination != null && !initialNomination.isEmpty()) {
      andCriteria.add(Criteria.where("initial_nomination").in(initialNomination));
    }

    // modality: scalar field - use $in
    if (modality != null && !modality.isEmpty()) {
      andCriteria.add(Criteria.where("modality").in(modality));
    }
  }

  private void addItemFilterCriteria(
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

    // Parse composite identifiers (format: chembl_id~combined_with)
    List<NominatedDrugIdentifier> identifiers = parseIdentifiers(items);

    // Build criteria for each identifier (must match BOTH chembl_id AND combined_with)
    List<Criteria> compositeConditions = new ArrayList<>();
    for (NominatedDrugIdentifier id : identifiers) {
      Criteria idCondition = new Criteria()
        .andOperator(
          Criteria.where("chembl_id").is(id.getChemblId()),
          Criteria.where("combined_with").is(id.getCombinedWith())
        );
      compositeConditions.add(idCondition);
    }

    // Apply INCLUDE or EXCLUDE logic
    if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
      // Match ANY of the composite identifiers ($or)
      andCriteria.add(new Criteria().orOperator(compositeConditions.toArray(new Criteria[0])));
    } else {
      // Exclude ALL of the composite identifiers ($nor)
      andCriteria.add(new Criteria().norOperator(compositeConditions.toArray(new Criteria[0])));
    }
  }

  private List<NominatedDrugIdentifier> parseIdentifiers(List<String> items) {
    List<NominatedDrugIdentifier> identifiers = new ArrayList<>();
    for (String item : items) {
      identifiers.add(NominatedDrugIdentifier.parse(item));
    }
    return identifiers;
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
      andCriteria.add(Criteria.where(SEARCH_FIELD).in(patterns));
    } else {
      // Single term: case-insensitive partial match
      String quotedSearch = Pattern.quote(trimmedSearch);
      andCriteria.add(Criteria.where(SEARCH_FIELD).regex(quotedSearch, "i"));
    }
  }
}
