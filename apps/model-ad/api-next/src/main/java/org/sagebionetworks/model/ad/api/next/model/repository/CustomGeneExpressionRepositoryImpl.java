package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
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
 * <p>Uses aggregation to support computed field (display_gene_symbol) for proper sorting
 * when gene_symbol is null/blank. All filtering logic is unified in a single implementation.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomGeneExpressionRepositoryImpl implements CustomGeneExpressionRepository {

  private static final String COLLECTION_NAME = "rna_de_aggregate";
  private static final String DISPLAY_GENE_SYMBOL_FIELD = "display_gene_symbol";
  private static final String GENE_SYMBOL_FIELD = "gene_symbol";

  private final MongoTemplate mongoTemplate;

  @Override
  public Page<GeneExpressionDocument> findAll(
    Pageable pageable,
    GeneExpressionSearchQueryDto query,
    List<String> items,
    String tissue,
    String sexCohort
  ) {
    try {
      // Build match criteria with all filters (shared by count and data queries)
      Criteria matchCriteria = buildMatchCriteria(tissue, sexCohort, query, items);

      // OPTIMIZATION: Use mongoTemplate.count() for counting (faster than aggregation)
      // This uses indexes directly without loading documents or running $addFields
      final long total = mongoTemplate.count(new Query(matchCriteria), COLLECTION_NAME);

      List<AggregationOperation> operations = new ArrayList<>();

      // Add $match FIRST to filter documents before transformation
      operations.add(Aggregation.match(matchCriteria));

      // Add display_gene_symbol field (with fallback) if sorting by gene_symbol
      boolean sortsByGeneSymbol = pageable
        .getSort()
        .stream()
        .anyMatch(o -> GENE_SYMBOL_FIELD.equals(o.getProperty()));

      if (sortsByGeneSymbol) {
        operations.add(buildDisplayGeneSymbolField());
      }

      buildLowercaseSortFields(operations, pageable.getSort());

      // Add sorting (uses lowercase fields for case-insensitive sorting)
      addSortOperation(operations, pageable.getSort());

      // Add pagination
      long skipCount = (long) pageable.getPageNumber() * pageable.getPageSize();
      operations.add(Aggregation.skip(skipCount));
      operations.add(Aggregation.limit(pageable.getPageSize()));

      Aggregation aggregation = Aggregation.newAggregation(operations);

      AggregationResults<GeneExpressionDocument> results = mongoTemplate.aggregate(
        aggregation,
        COLLECTION_NAME,
        GeneExpressionDocument.class
      );

      return new PageImpl<>(results.getMappedResults(), pageable, total);
    } catch (Exception e) {
      log.error("Error executing gene expression query", e);
      throw e;
    }
  }

  /**
   * Builds $addFields operation to compute display_gene_symbol.
   * Formula: display_gene_symbol = gene_symbol ?? ensembl_gene_id
   * (uses ensembl_gene_id when gene_symbol is null, empty, or whitespace)
   */
  private AggregationOperation buildDisplayGeneSymbolField() {
    // Check if gene_symbol is null
    List<Object> eqNull = new ArrayList<>();
    eqNull.add("$gene_symbol");
    eqNull.add(null);

    // Check if gene_symbol is empty string
    List<Object> eqEmpty = new ArrayList<>();
    eqEmpty.add("$gene_symbol");
    eqEmpty.add("");

    // Check if gene_symbol is only whitespace
    List<Object> eqTrimmed = new ArrayList<>();
    eqTrimmed.add(new Document("$trim", new Document("input", "$gene_symbol")));
    eqTrimmed.add("");

    // Combine all null/empty/whitespace checks with $or
    List<Document> orConditions = new ArrayList<>();
    orConditions.add(new Document("$eq", eqNull));
    orConditions.add(new Document("$eq", eqEmpty));
    orConditions.add(new Document("$eq", eqTrimmed));

    // $cond: if (gene_symbol is null/empty/whitespace) then ensembl_gene_id else gene_symbol
    List<Object> condArgs = new ArrayList<>();
    condArgs.add(new Document("$or", orConditions));
    condArgs.add("$ensembl_gene_id");
    condArgs.add("$gene_symbol");

    Document addFieldsDoc = new Document(
      "$addFields",
      new Document(DISPLAY_GENE_SYMBOL_FIELD, new Document("$cond", condArgs))
    );

    return context -> addFieldsDoc;
  }

  /**
   * Builds $addFields operation to create lowercase versions of sort fields
   * for case-insensitive sorting (DocumentDB compatible).
   *
   * <p>Applies $toLower to string fields only.
   * Only adds the operation if there are string fields to transform.
   *
   * @param operations the list of aggregation operations to add to
   * @param sort the Sort object containing the fields to sort by
   */
  private void buildLowercaseSortFields(List<AggregationOperation> operations, Sort sort) {
    Document fields = new Document();

    for (Sort.Order order : sort) {
      String field = order.getProperty();

      if (GENE_SYMBOL_FIELD.equals(field)) {
        // For gene_symbol, use display_gene_symbol (which has fallback logic)
        fields.append(
          "gene_symbol_lower",
          new Document("$toLower", "$" + DISPLAY_GENE_SYMBOL_FIELD)
        );
      } else if ("name".equals(field)) {
        // For name (Link field), use name.link_text
        fields.append("name_lower", new Document("$toLower", "$name.link_text"));
      } else if (needsCaseInsensitiveSort(field)) {
        // For other string fields, apply lowercase transformation
        fields.append(field + "_lower", new Document("$toLower", "$" + field));
      }
    }

    // Only add $addFields if there are fields
    if (!fields.isEmpty()) {
      operations.add(context -> new Document("$addFields", fields));
    }
  }

  /**
   * Checks if a field needs case-insensitive sorting.
   * Non-string fields (like age columns with nested objects) are excluded.
   *
   * @param field the field name
   * @return true if the field needs case-insensitive sorting
   */
  private boolean needsCaseInsensitiveSort(String field) {
    return (
      "name".equals(field) ||
      "model_type".equals(field) ||
      "tissue".equals(field) ||
      "sex_cohort".equals(field) ||
      "ensembl_gene_id".equals(field) ||
      "matched_control".equals(field) ||
      "model_group".equals(field)
    );
  }

  /**
   * Builds match criteria combining all filters: tissue, sex_cohort, data filters,
   * composite identifiers, and gene symbol search.
   */
  private Criteria buildMatchCriteria(
    String tissue,
    String sexCohort,
    GeneExpressionSearchQueryDto query,
    List<String> items
  ) {
    List<Criteria> allCriteria = new ArrayList<>();

    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    String search = query.getSearch();

    // Base criteria for tissue and sex_cohort (required)
    allCriteria.add(Criteria.where("tissue").is(tissue));
    allCriteria.add(Criteria.where("sex_cohort").is(sexCohort));

    // Data filters: biodomains, modelType, name (OR within field, AND between fields)
    addDataFilterCriteria(
      query.getBiodomains(),
      query.getModelType(),
      query.getName(),
      allCriteria
    );

    // Composite identifier filtering (items + itemFilterType)
    addCompositeIdentifierCriteria(items, filterType, allCriteria);

    // Gene symbol search (only for EXCLUDE mode)
    addSearchCriteria(search, filterType, allCriteria);

    // Combine all criteria with AND
    return new Criteria().andOperator(allCriteria.toArray(new Criteria[0]));
  }

  private void addDataFilterCriteria(
    List<String> biodomains,
    List<String> modelType,
    List<String> name,
    List<Criteria> criteriaList
  ) {
    // biodomains: array field - use $in (matches if array contains any value)
    if (biodomains != null && !biodomains.isEmpty()) {
      criteriaList.add(Criteria.where("biodomains").in(biodomains));
    }

    // model_type: string field - use $in (matches if value equals any)
    if (modelType != null && !modelType.isEmpty()) {
      criteriaList.add(Criteria.where("model_type").in(modelType));
    }

    // name.link_text: nested field - use $in (matches if value equals any)
    if (name != null && !name.isEmpty()) {
      criteriaList.add(Criteria.where("name.link_text").in(name));
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

    // Parse composite identifiers (format: ensembl_gene_id~name)
    List<GeneExpressionIdentifier> identifiers = parseIdentifiers(items);

    // Build criteria for each identifier (must match BOTH ensembl_gene_id AND name.link_text)
    List<Criteria> compositeConditions = new ArrayList<>();
    for (GeneExpressionIdentifier identifier : identifiers) {
      Criteria idCondition = new Criteria()
        .andOperator(
          Criteria.where("ensembl_gene_id").is(identifier.getEnsemblGeneId()),
          Criteria.where("name.link_text").is(identifier.getName())
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
      criteriaList.add(Criteria.where(GENE_SYMBOL_FIELD).in(patterns));
    } else {
      // Single term: partial match (case-insensitive)
      String regex = Pattern.quote(trimmedSearch);
      criteriaList.add(Criteria.where(GENE_SYMBOL_FIELD).regex(regex, "i"));
    }
  }

  private List<GeneExpressionIdentifier> parseIdentifiers(List<String> items) {
    List<GeneExpressionIdentifier> identifiers = new ArrayList<>();
    for (String item : items) {
      identifiers.add(GeneExpressionIdentifier.parse(item));
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
      if (needsCaseInsensitiveSort(field) || GENE_SYMBOL_FIELD.equals(field)) {
        field = field + "_lower";
      }
      // Non-string fields (like age columns) are sorted directly

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
