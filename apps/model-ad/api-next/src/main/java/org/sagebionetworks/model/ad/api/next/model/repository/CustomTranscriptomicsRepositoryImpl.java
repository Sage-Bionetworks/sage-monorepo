package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation using MongoDB aggregation pipeline.
 *
 * <p>Uses aggregation to support a computed field ({@code display_gene_symbol}) for proper sorting
 * when {@code gene_symbol} is null/blank. All filtering logic is unified in a single
 * implementation. The pipeline scaffold (count, $match, $addFields, $sort, $skip, $limit) lives
 * in {@link ComparisonToolRepositorySupport}.
 */
@Repository
@Slf4j
public class CustomTranscriptomicsRepositoryImpl
  extends ComparisonToolRepositorySupport<TranscriptomicsDocument>
  implements CustomTranscriptomicsRepository {

  private static final String COLLECTION_NAME = "rna_de_aggregate";
  private static final String DISPLAY_GENE_SYMBOL_FIELD = "display_gene_symbol";
  private static final String GENE_SYMBOL_FIELD = "gene_symbol";

  public CustomTranscriptomicsRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<TranscriptomicsDocument> getDocumentClass() {
    return TranscriptomicsDocument.class;
  }

  /**
   * Case-insensitive sort. {@code gene_symbol} routes through the computed
   * {@code display_gene_symbol} (with the ensembl_gene_id fallback added by
   * {@link #getExtraSortFieldComputations(Sort)}); {@code name} routes through its nested
   * {@code link_text} child.
   */
  @Override
  protected Map<String, Object> getComputedSortFieldExpressions() {
    return Map.of(
      GENE_SYMBOL_FIELD, toLowerExpr(DISPLAY_GENE_SYMBOL_FIELD),
      "name", toLowerExpr("name.link_text"),
      "model_type", toLowerExpr("model_type"),
      "tissue", toLowerExpr("tissue"),
      "sex_cohort", toLowerExpr("sex_cohort"),
      "ensembl_gene_id", toLowerExpr("ensembl_gene_id"),
      "matched_control", toLowerExpr("matched_control"),
      "model_group", toLowerExpr("model_group")
    );
  }

  /**
   * When the user sorts by {@code gene_symbol}, we need to compute the
   * {@code display_gene_symbol} fallback first so the lowercase step ({@code $toLower}) can
   * reference it.
   */
  @Override
  protected List<AggregationOperation> getExtraSortFieldComputations(Sort sort) {
    boolean sortsByGeneSymbol = sort
      .stream()
      .anyMatch(o -> GENE_SYMBOL_FIELD.equals(o.getProperty()));
    if (!sortsByGeneSymbol) {
      return List.of();
    }
    return List.of(buildDisplayGeneSymbolField());
  }

  @Override
  public Page<TranscriptomicsDocument> findAll(
    Pageable pageable,
    TranscriptomicsSearchQueryDto query,
    List<String> items,
    String tissue,
    String sexCohort
  ) {
    // Build match criteria with all filters (shared by count and data queries)
    Criteria matchCriteria = buildMatchCriteria(tissue, sexCohort, query, items);

    return executePagedAggregation(matchCriteria, pageable);
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
   * Builds match criteria combining all filters: tissue, sex_cohort, data filters,
   * composite identifiers, and gene symbol search.
   */
  private Criteria buildMatchCriteria(
    String tissue,
    String sexCohort,
    TranscriptomicsSearchQueryDto query,
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
    List<TranscriptomicsIdentifier> identifiers = parseIdentifiers(items);

    // Build criteria for each identifier (must match BOTH ensembl_gene_id AND name.link_text)
    List<Criteria> compositeConditions = new ArrayList<>();
    for (TranscriptomicsIdentifier identifier : identifiers) {
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

  /**
   * Adds search criteria that matches the display_gene_symbol logic using raw fields.
   *
   * <p>Since display_gene_symbol = gene_symbol ?? ensembl_gene_id, the search matches:
   * (gene_symbol matches) OR (gene_symbol is null/empty AND ensembl_gene_id matches)
   *
   * <p>NOTE: We cannot use DISPLAY_GENE_SYMBOL_FIELD here because it's a computed field
   * created by $addFields in the aggregation pipeline. The count query uses
   * mongoTemplate.count() for performance, which doesn't run the aggregation pipeline
   * and therefore has no access to the computed field.
   */
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

    // Match if gene_symbol matches, OR if gene_symbol is null/empty and ensembl_gene_id matches
    Criteria geneSymbolIsNullOrEmpty = new Criteria()
      .orOperator(
        Criteria.where(GENE_SYMBOL_FIELD).is(null),
        Criteria.where(GENE_SYMBOL_FIELD).is(""),
        Criteria.where(GENE_SYMBOL_FIELD).regex("^\\s*$") // whitespace only
      );

    if (trimmedSearch.contains(",")) {
      // Comma-separated list: exact match (case-insensitive)
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      Criteria geneSymbolMatches = Criteria.where(GENE_SYMBOL_FIELD).in(patterns);
      Criteria ensemblFallbackMatches = new Criteria()
        .andOperator(geneSymbolIsNullOrEmpty, Criteria.where("ensembl_gene_id").in(patterns));

      criteriaList.add(new Criteria().orOperator(geneSymbolMatches, ensemblFallbackMatches));
    } else {
      // Single term: partial match (case-insensitive)
      String regex = Pattern.quote(trimmedSearch);
      Criteria geneSymbolMatches = Criteria.where(GENE_SYMBOL_FIELD).regex(regex, "i");
      Criteria ensemblFallbackMatches = new Criteria()
        .andOperator(geneSymbolIsNullOrEmpty, Criteria.where("ensembl_gene_id").regex(regex, "i"));

      criteriaList.add(new Criteria().orOperator(geneSymbolMatches, ensemblFallbackMatches));
    }
  }

  private List<TranscriptomicsIdentifier> parseIdentifiers(List<String> items) {
    List<TranscriptomicsIdentifier> identifiers = new ArrayList<>();
    for (String item : items) {
      identifiers.add(TranscriptomicsIdentifier.parse(item));
    }
    return identifiers;
  }
}
