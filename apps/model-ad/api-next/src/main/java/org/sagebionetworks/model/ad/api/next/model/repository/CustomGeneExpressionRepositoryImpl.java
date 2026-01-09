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
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
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
    boolean sortsByGeneSymbol = pageable
      .getSort()
      .stream()
      .anyMatch(order -> GENE_SYMBOL_FIELD.equals(order.getProperty()));

    log.debug(
      "Executing query: tissue={}, sexCohort={}, sortsByGeneSymbol={}",
      tissue,
      sexCohort,
      sortsByGeneSymbol
    );

    try {
      List<AggregationOperation> operations = new ArrayList<>();

      // Add computed field for gene_symbol fallback (only if sorting by gene_symbol)
      if (sortsByGeneSymbol) {
        operations.add(buildAddFieldsOperation());
      }

      // Build match criteria with all filters
      Criteria matchCriteria = buildMatchCriteria(tissue, sexCohort, query, items);
      operations.add(Aggregation.match(matchCriteria));

      // Count total before pagination
      final long total = countDocuments(operations);

      // Add sorting (with gene_symbol → display_gene_symbol substitution if needed)
      addSortOperation(operations, pageable.getSort(), sortsByGeneSymbol);

      // Add pagination
      long skipCount = (long) pageable.getPageNumber() * pageable.getPageSize();
      operations.add(Aggregation.skip(skipCount));
      operations.add(Aggregation.limit(pageable.getPageSize()));

      // Use collation for case-insensitive sorting
      AggregationOptions options = AggregationOptions.builder()
        .collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()))
        .build();

      Aggregation aggregation = Aggregation.newAggregation(operations).withOptions(options);

      AggregationResults<GeneExpressionDocument> results = mongoTemplate.aggregate(
        aggregation,
        COLLECTION_NAME,
        GeneExpressionDocument.class
      );

      log.debug("Query returned {} results, total={}", results.getMappedResults().size(), total);
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
  private AggregationOperation buildAddFieldsOperation() {
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

    // name: string field - use $in (matches if value equals any)
    if (name != null && !name.isEmpty()) {
      criteriaList.add(Criteria.where("name").in(name));
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

    // Build criteria for each identifier (must match BOTH ensembl_gene_id AND name)
    List<Criteria> compositeConditions = new ArrayList<>();
    for (GeneExpressionIdentifier identifier : identifiers) {
      Criteria idCondition = new Criteria()
        .andOperator(
          Criteria.where("ensembl_gene_id").is(identifier.getEnsemblGeneId()),
          Criteria.where("name").is(identifier.getName())
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
   * Adds $sort operation, replacing gene_symbol with display_gene_symbol if needed.
   */
  private void addSortOperation(
    List<AggregationOperation> operations,
    Sort sort,
    boolean sortsByGeneSymbol
  ) {
    if (sort.isUnsorted()) {
      return;
    }

    List<Document> sortFields = new ArrayList<>();
    for (Sort.Order order : sort) {
      String field = order.getProperty();

      // Replace gene_symbol with computed display_gene_symbol for sorting
      if (GENE_SYMBOL_FIELD.equals(field)) {
        field = DISPLAY_GENE_SYMBOL_FIELD;
      }

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

  /**
   * Counts total documents matching the criteria (before pagination).
   */
  private long countDocuments(List<AggregationOperation> baseOperations) {
    List<AggregationOperation> countOperations = new ArrayList<>(baseOperations);
    countOperations.add(Aggregation.count().as("total"));

    Aggregation countAggregation = Aggregation.newAggregation(countOperations);
    AggregationResults<Document> countResults = mongoTemplate.aggregate(
      countAggregation,
      COLLECTION_NAME,
      Document.class
    );

    List<Document> countList = countResults.getMappedResults();
    if (countList.isEmpty()) {
      return 0;
    }
    return countList.get(0).getInteger("total", 0);
  }
}
