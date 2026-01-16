package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
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
 * <p>Uses aggregation to support case-insensitive sorting and complex filtering logic.
 * All filtering logic is unified in a single implementation.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomDiseaseCorrelationRepositoryImpl
  implements CustomDiseaseCorrelationRepository {

  private static final String COLLECTION_NAME = "disease_correlation";
  private static final String NAME_FIELD = "name";

  private final MongoTemplate mongoTemplate;

  @Override
  public Page<DiseaseCorrelationDocument> findAll(
    Pageable pageable,
    DiseaseCorrelationSearchQueryDto query,
    List<String> items,
    String cluster
  ) {
    try {
      // Build match criteria with all filters (shared by count and data queries)
      Criteria matchCriteria = buildMatchCriteria(cluster, query, items);

      // OPTIMIZATION: Use mongoTemplate.count() for counting (faster than aggregation)
      // This uses indexes directly without loading documents or running $addFields
      final long total = mongoTemplate.count(new Query(matchCriteria), COLLECTION_NAME);

      List<AggregationOperation> operations = new ArrayList<>();

      // Add $match FIRST to filter documents before transformation
      operations.add(Aggregation.match(matchCriteria));

      // Add lowercase versions of string sort fields for case-insensitive sorting
      operations.add(buildLowercaseSortFields(pageable.getSort()));

      // Add sorting (uses lowercase fields for case-insensitive sorting)
      addSortOperation(operations, pageable.getSort());

      // Add pagination
      long skipCount = (long) pageable.getPageNumber() * pageable.getPageSize();
      operations.add(Aggregation.skip(skipCount));
      operations.add(Aggregation.limit(pageable.getPageSize()));

      Aggregation aggregation = Aggregation.newAggregation(operations);

      AggregationResults<DiseaseCorrelationDocument> results = mongoTemplate.aggregate(
        aggregation,
        COLLECTION_NAME,
        DiseaseCorrelationDocument.class
      );

      return new PageImpl<>(results.getMappedResults(), pageable, total);
    } catch (Exception e) {
      log.error("Error executing disease correlation query", e);
      throw e;
    }
  }

  /**
   * Builds $addFields operation to create lowercase versions of sort fields
   * for case-insensitive sorting (DocumentDB compatible).
   *
   * <p>Only applies $toLower to string fields. Non-string fields (like correlation result
   * objects) are excluded to prevent aggregation errors.
   *
   * @param sort the Sort object containing the fields to sort by
   * @return AggregationOperation that adds lowercase versions of string sort fields
   */
  private AggregationOperation buildLowercaseSortFields(Sort sort) {
    Document fields = new Document();

    for (Sort.Order order : sort) {
      String field = order.getProperty();
      if (isStringField(field)) {
        // For string fields, apply lowercase transformation
        fields.append(field + "_lower", new Document("$toLower", "$" + field));
      }
    }

    return context -> new Document("$addFields", fields);
  }

  /**
   * Checks if a field is a string field that requires case-insensitive sorting.
   * Non-string fields (like correlation result objects with numeric values) are excluded.
   *
   * @param field the field name
   * @return true if the field is a string field
   */
  private boolean isStringField(String field) {
    return (
      "name".equals(field) ||
      "age".equals(field) ||
      "sex".equals(field) ||
      "model_type".equals(field) ||
      "matched_control".equals(field) ||
      "cluster".equals(field)
    );
  }

  /**
   * Builds match criteria combining all filters: cluster, data filters,
   * composite identifiers, and name search.
   */
  private Criteria buildMatchCriteria(
    String cluster,
    DiseaseCorrelationSearchQueryDto query,
    List<String> items
  ) {
    List<Criteria> allCriteria = new ArrayList<>();

    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    String search = query.getSearch();

    // Base criteria for cluster (required)
    allCriteria.add(Criteria.where("cluster").is(cluster));

    // Data filters: age, modelType, modifiedGenes, name, sex (OR within field, AND between fields)
    addDataFilterCriteria(
      query.getAge(),
      query.getModelType(),
      query.getModifiedGenes(),
      query.getName(),
      query.getSex(),
      allCriteria
    );

    // Composite identifier filtering (items + itemFilterType)
    addCompositeIdentifierCriteria(items, filterType, allCriteria);

    // Name search (only for EXCLUDE mode)
    addSearchCriteria(search, filterType, allCriteria);

    // Combine all criteria with AND
    return new Criteria().andOperator(allCriteria.toArray(new Criteria[0]));
  }

  private void addDataFilterCriteria(
    List<String> age,
    List<String> modelType,
    List<String> modifiedGenes,
    List<String> name,
    List<String> sex,
    List<Criteria> criteriaList
  ) {
    // age: string field - use $in (matches if value equals any)
    if (age != null && !age.isEmpty()) {
      criteriaList.add(Criteria.where("age").in(age));
    }

    // model_type: string field - use $in (matches if value equals any)
    if (modelType != null && !modelType.isEmpty()) {
      criteriaList.add(Criteria.where("model_type").in(modelType));
    }

    // modified_genes: array field - use $in (matches if array contains any value)
    if (modifiedGenes != null && !modifiedGenes.isEmpty()) {
      criteriaList.add(Criteria.where("modified_genes").in(modifiedGenes));
    }

    // name: string field - use $in (matches if value equals any)
    if (name != null && !name.isEmpty()) {
      criteriaList.add(Criteria.where("name").in(name));
    }

    // sex: string field - use $in (matches if value equals any)
    if (sex != null && !sex.isEmpty()) {
      criteriaList.add(Criteria.where("sex").in(sex));
    }
  }

  private void addCompositeIdentifierCriteria(
    List<String> items,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> criteriaList
  ) {
    if (items.isEmpty()) {
      // For INCLUDE mode with empty items, add impossible condition (return empty)
      if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
        criteriaList.add(Criteria.where("_id").is(null));
      }
      // For EXCLUDE mode with empty items, no filtering needed (return all)
      return;
    }

    // Parse composite identifiers (format: name~age~sex)
    List<DiseaseCorrelationIdentifier> identifiers = parseIdentifiers(items);

    // Build criteria for each identifier (must match ALL: name AND age AND sex)
    List<Criteria> compositeConditions = new ArrayList<>();
    for (DiseaseCorrelationIdentifier identifier : identifiers) {
      Criteria idCondition = new Criteria()
        .andOperator(
          Criteria.where("name").is(identifier.getName()),
          Criteria.where("age").is(identifier.getAge()),
          Criteria.where("sex").is(identifier.getSex())
        );
      compositeConditions.add(idCondition);
    }

    // Apply INCLUDE or EXCLUDE logic
    if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
      // Match ANY of the composite identifiers ($or)
      criteriaList.add(new Criteria().orOperator(compositeConditions.toArray(new Criteria[0])));
    } else {
      // Exclude ALL of the composite identifiers ($nor)
      criteriaList.add(new Criteria().norOperator(compositeConditions.toArray(new Criteria[0])));
    }
  }

  private void addSearchCriteria(
    String search,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> criteriaList
  ) {
    // Search only applies when itemFilterType is EXCLUDE
    if (search == null || search.trim().isEmpty() || filterType == ItemFilterTypeQueryDto.INCLUDE) {
      return;
    }

    String trimmedSearch = search.trim();

    // Comma-separated list: exact match (case-insensitive)
    if (trimmedSearch.contains(",")) {
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      criteriaList.add(Criteria.where(NAME_FIELD).in(patterns));
    } else {
      // Single term: partial match (case-insensitive)
      String regex = Pattern.quote(trimmedSearch);
      criteriaList.add(Criteria.where(NAME_FIELD).regex(regex, "i"));
    }
  }

  private List<DiseaseCorrelationIdentifier> parseIdentifiers(List<String> items) {
    List<DiseaseCorrelationIdentifier> identifiers = new ArrayList<>();
    for (String item : items) {
      identifiers.add(DiseaseCorrelationIdentifier.parse(item));
    }
    return identifiers;
  }

  /**
   * Adds $sort operation, using lowercase versions of string fields for
   * case-insensitive sorting (DocumentDB compatible).
   *
   * <p>Applies _lower suffix only to string fields.
   */
  private void addSortOperation(List<AggregationOperation> operations, Sort sort) {
    if (sort.isUnsorted()) {
      return;
    }

    List<Document> sortFields = new ArrayList<>();
    for (Sort.Order order : sort) {
      String field = order.getProperty();

      // Use lowercase version for string fields (case-insensitive sorting)
      if (isStringField(field)) {
        field = field + "_lower";
      }
      // Non-string fields (like correlation result objects) are sorted directly

      int direction = order.isAscending() ? 1 : -1;
      sortFields.add(new Document(field, direction));
    }

    if (!sortFields.isEmpty()) {
      Document sortDocument = new Document();
      for (Document sortField : sortFields) {
        sortDocument.putAll(sortField);
      }
      operations.add(context -> new Document("$sort", sortDocument));
    }
  }

}
