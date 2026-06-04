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
import org.sagebionetworks.explorers.ComputedSortField;
import org.sagebionetworks.explorers.CtFilterConfig;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
   * {@code display_gene_symbol} (with the ensembl_gene_id fallback bundled as a prerequisite);
   * {@code name} routes through its nested {@code link_text} child.
   */
  @Override
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
    return Map.of(
      GENE_SYMBOL_FIELD,
      ComputedSortField.of(toLowerExpr(DISPLAY_GENE_SYMBOL_FIELD)).withPrerequisite(
        buildDisplayGeneSymbolField()
      ),
      "name",
      ComputedSortField.of(toLowerExpr("name.link_text")),
      "model_type",
      ComputedSortField.of(toLowerExpr("model_type")),
      "tissue",
      ComputedSortField.of(toLowerExpr("tissue")),
      "sex_cohort",
      ComputedSortField.of(toLowerExpr("sex_cohort")),
      "ensembl_gene_id",
      ComputedSortField.of(toLowerExpr("ensembl_gene_id")),
      "matched_control",
      ComputedSortField.of(toLowerExpr("matched_control")),
      "model_group",
      ComputedSortField.of(toLowerExpr("model_group"))
    );
  }

  private final CtFilterConfig<TranscriptomicsSearchQueryDto> filterConfig =
    CtFilterConfig.<TranscriptomicsSearchQueryDto>builder()
      .dataFilter("biodomains", TranscriptomicsSearchQueryDto::getBiodomains)
      .dataFilter("model_type", TranscriptomicsSearchQueryDto::getModelType)
      .dataFilter("name.link_text", TranscriptomicsSearchQueryDto::getName)
      .compositeItemFilter(item -> TranscriptomicsIdentifier.parse(item).toCriteria())
      .searchFilter(GENE_SYMBOL_FIELD)
      .build();

  @Override
  protected CtFilterConfig<TranscriptomicsSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public Page<TranscriptomicsDocument> findAll(
    Pageable pageable,
    TranscriptomicsSearchQueryDto query,
    List<String> items,
    String tissue,
    String sexCohort
  ) {
    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    boolean isInclude = filterType == ItemFilterTypeQueryDto.INCLUDE;
    Criteria matchCriteria = buildCtMatchCriteria(
      query,
      items,
      isInclude,
      query.getSearch(),
      getFilterConfig(),
      Criteria.where("tissue").is(tissue),
      Criteria.where("sex_cohort").is(sexCohort)
    );

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
   * Override search to add gene_symbol/ensembl_gene_id fallback logic.
   *
   * <p>Since {@code display_gene_symbol = gene_symbol ?? ensembl_gene_id}, the search matches:
   * (gene_symbol matches) OR (gene_symbol is null/empty AND ensembl_gene_id matches)
   *
   * <p><strong>NOTE:</strong> We cannot use {@code DISPLAY_GENE_SYMBOL_FIELD} here because it's a
   * computed field created by {@code $addFields} in the aggregation pipeline. The count query uses
   * {@code mongoTemplate.count()} for performance, which doesn't run the aggregation pipeline and
   * therefore has no access to the computed field. We must replicate the fallback logic using the
   * raw {@code gene_symbol} and {@code ensembl_gene_id} fields.
   */
  @Override
  protected Criteria buildSearchCriteria(String field, String trimmedSearch) {
    Criteria geneSymbolIsNullOrEmpty = new Criteria()
      .orOperator(
        Criteria.where(GENE_SYMBOL_FIELD).is(null),
        Criteria.where(GENE_SYMBOL_FIELD).is(""),
        Criteria.where(GENE_SYMBOL_FIELD).regex("^\\s*$")
      );

    if (trimmedSearch.contains(",")) {
      // Comma-separated list: exact match (case-insensitive)
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      Criteria geneSymbolMatches = Criteria.where(GENE_SYMBOL_FIELD).in(patterns);
      Criteria ensemblFallbackMatches = new Criteria()
        .andOperator(geneSymbolIsNullOrEmpty, Criteria.where("ensembl_gene_id").in(patterns));

      return new Criteria().orOperator(geneSymbolMatches, ensemblFallbackMatches);
    } else {
      // Single term: partial match (case-insensitive)
      String regex = Pattern.quote(trimmedSearch);
      Criteria geneSymbolMatches = Criteria.where(GENE_SYMBOL_FIELD).regex(regex, "i");
      Criteria ensemblFallbackMatches = new Criteria()
        .andOperator(geneSymbolIsNullOrEmpty, Criteria.where("ensembl_gene_id").regex(regex, "i"));

      return new Criteria().orOperator(geneSymbolMatches, ensemblFallbackMatches);
    }
  }
}
