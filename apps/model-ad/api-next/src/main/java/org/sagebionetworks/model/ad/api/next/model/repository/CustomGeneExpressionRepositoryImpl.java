package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomGeneExpressionRepositoryImpl implements CustomGeneExpressionRepository {

  private static final String COLLECTION_NAME = "rna_de_aggregate";
  private static final String DISPLAY_GENE_SYMBOL_FIELD = "display_gene_symbol";
  private static final String GENE_SYMBOL_FIELD = "gene_symbol";

  private final MongoTemplate mongoTemplate;

  @Override
  public Page<GeneExpressionDocument> findWithComputedSort(
    String tissue,
    String sexCohort,
    @Nullable List<Map<String, Object>> compositeConditions,
    boolean isExclude,
    @Nullable String geneSymbolSearch,
    @Nullable List<Pattern> geneSymbolPatterns,
    Pageable pageable
  ) {
    log.info("findWithComputedSort: tissue={}, sexCohort={}, isExclude={}",
      tissue, sexCohort, isExclude);

    try {
      List<AggregationOperation> operations = new ArrayList<>();

      // Add computed field for sorting: display_gene_symbol
      // Uses gene_symbol if not null/blank, otherwise falls back to ensembl_gene_id
      operations.add(buildAddFieldsOperation());

      // Build match criteria
      Criteria matchCriteria = buildMatchCriteria(
        tissue,
        sexCohort,
        compositeConditions,
        isExclude,
        geneSymbolSearch,
        geneSymbolPatterns
      );
      operations.add(Aggregation.match(matchCriteria));

      // Count total before pagination (for Page metadata)
      final long total = countDocuments(operations);

      // Add sorting with computed field substitution
      addSortOperation(operations, pageable.getSort());

      // Add pagination
      long skipCount = (long) pageable.getPageNumber() * pageable.getPageSize();
      operations.add(Aggregation.skip(skipCount));
      operations.add(Aggregation.limit(pageable.getPageSize()));

      // Use collation for case-insensitive sorting (strength 2 = secondary = case-insensitive)
      AggregationOptions options = AggregationOptions.builder()
        .collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()))
        .build();

      Aggregation aggregation = Aggregation.newAggregation(operations).withOptions(options);

      AggregationResults<GeneExpressionDocument> results = mongoTemplate.aggregate(
        aggregation,
        COLLECTION_NAME,
        GeneExpressionDocument.class
      );

      log.info("Aggregation returned {} results, total={}",
        results.getMappedResults().size(), total);
      return new PageImpl<>(results.getMappedResults(), pageable, total);
    } catch (Exception e) {
      log.error("Error executing gene expression aggregation", e);
      throw e;
    }
  }

  private AggregationOperation buildAddFieldsOperation() {
    // Build the $cond expression using mutable ArrayLists for MongoDB compatibility
    List<Object> eqNull = new ArrayList<>();
    eqNull.add("$gene_symbol");
    eqNull.add(null);

    List<Object> eqEmpty = new ArrayList<>();
    eqEmpty.add("$gene_symbol");
    eqEmpty.add("");

    List<Object> eqTrimmed = new ArrayList<>();
    eqTrimmed.add(new Document("$trim", new Document("input", "$gene_symbol")));
    eqTrimmed.add("");

    List<Document> orConditions = new ArrayList<>();
    orConditions.add(new Document("$eq", eqNull));
    orConditions.add(new Document("$eq", eqEmpty));
    orConditions.add(new Document("$eq", eqTrimmed));

    List<Object> condArgs = new ArrayList<>();
    condArgs.add(new Document("$or", orConditions));
    condArgs.add("$ensembl_gene_id");
    condArgs.add("$gene_symbol");

    Document addFieldsDoc = new Document("$addFields",
      new Document(DISPLAY_GENE_SYMBOL_FIELD, new Document("$cond", condArgs)));

    return context -> addFieldsDoc;
  }

  private Criteria buildMatchCriteria(
    String tissue,
    String sexCohort,
    @Nullable List<Map<String, Object>> compositeConditions,
    boolean isExclude,
    @Nullable String geneSymbolSearch,
    @Nullable List<Pattern> geneSymbolPatterns
  ) {
    List<Criteria> allCriteria = new ArrayList<>();

    // Base criteria for tissue and sex_cohort
    allCriteria.add(Criteria.where("tissue").is(tissue));
    allCriteria.add(Criteria.where("sex_cohort").is(sexCohort));

    // Add composite identifier conditions
    if (compositeConditions != null && !compositeConditions.isEmpty()) {
      Criteria[] compositeCriteria = compositeConditions.stream()
        .map(this::compositeConditionToCriteria)
        .toArray(Criteria[]::new);

      if (isExclude) {
        allCriteria.add(new Criteria().norOperator(compositeCriteria));
      } else {
        allCriteria.add(new Criteria().orOperator(compositeCriteria));
      }
    }

    // Add gene symbol search
    if (geneSymbolSearch != null) {
      allCriteria.add(Criteria.where(GENE_SYMBOL_FIELD).regex(geneSymbolSearch, "i"));
    } else if (geneSymbolPatterns != null && !geneSymbolPatterns.isEmpty()) {
      allCriteria.add(Criteria.where(GENE_SYMBOL_FIELD).in(geneSymbolPatterns));
    }

    // Combine all criteria with AND
    return new Criteria().andOperator(allCriteria.toArray(new Criteria[0]));
  }

  @SuppressWarnings("unchecked")
  private Criteria compositeConditionToCriteria(Map<String, Object> condition) {
    List<Map<String, Object>> andConditions = (List<Map<String, Object>>) condition.get("$and");
    if (andConditions == null || andConditions.size() != 2) {
      throw new IllegalArgumentException("Invalid composite condition format");
    }

    String ensemblGeneId = (String) andConditions.get(0).get("ensembl_gene_id");
    String name = (String) andConditions.get(1).get("name");

    return new Criteria().andOperator(
      Criteria.where("ensembl_gene_id").is(ensemblGeneId),
      Criteria.where("name").is(name)
    );
  }

  private void addSortOperation(List<AggregationOperation> operations, Sort sort) {
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
