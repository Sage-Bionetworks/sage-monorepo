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
import org.sagebionetworks.model.ad.api.next.util.MouseEnsemblGeneId;
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
  private static final String ENSEMBL_GENE_ID_FIELD = "ensembl_gene_id";
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
   * {@code gene_symbol} routes through the computed {@code display_gene_symbol}
   * (gene_symbol ?? ensembl_gene_id fallback) bundled as a prerequisite.
   * Case-insensitive ordering is handled by pipeline-level collation.
   */
  @Override
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
    return Map.of(
      GENE_SYMBOL_FIELD,
      ComputedSortField.of("$" + DISPLAY_GENE_SYMBOL_FIELD).withPrerequisite(
        buildDisplayGeneSymbolField()
      )
    );
  }

  private final CtFilterConfig<TranscriptomicsSearchQueryDto> filterConfig = CtFilterConfig.<
    TranscriptomicsSearchQueryDto
  >builder()
    .dataFilter("biodomains", TranscriptomicsSearchQueryDto::getBiodomains)
    .dataFilter("model_type", TranscriptomicsSearchQueryDto::getModelType)
    .dataFilter("name.link_text", TranscriptomicsSearchQueryDto::getName)
    .dataFilter("sex", TranscriptomicsSearchQueryDto::getSex)
    .compositeItemFilter(item -> TranscriptomicsIdentifier.parse(item).toCriteria())
    .searchFilter(GENE_SYMBOL_FIELD)
    .build();

  /**
   * Maps each heatmap time-point column to its nested {@code log2_fc} value
   * ({@code { log2_fc, adj_p_val }}). Keys must stay in sync with the
   * {@link TranscriptomicsDocument} heatmap fields.
   *
   * <p>{@code gene_symbol} is aliased to {@code display_gene_symbol} so the isEmpty flag checks
   * the computed fallback value (gene_symbol ?? ensembl_gene_id) rather than the raw field.
   * Without this, rows where {@code gene_symbol} is blank but {@code ensembl_gene_id} is populated
   * would be treated as empty and incorrectly sorted to the tail.
   */
  @Override
  protected Map<String, String> getSortFieldAliases() {
    return Map.of(
      GENE_SYMBOL_FIELD,
      DISPLAY_GENE_SYMBOL_FIELD,
      "name",
      "name.link_text",
      "4 months",
      "4 months.log2_fc",
      "12 months",
      "12 months.log2_fc",
      "18 months",
      "18 months.log2_fc",
      "24 months",
      "24 months.log2_fc"
    );
  }

  @Override
  protected CtFilterConfig<TranscriptomicsSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public Page<TranscriptomicsDocument> findAll(
    Pageable pageable,
    TranscriptomicsSearchQueryDto query,
    List<String> items,
    String tissue
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
      Criteria.where("tissue").is(tissue)
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
    eqNull.add("$" + GENE_SYMBOL_FIELD);
    eqNull.add(null);

    // Check if gene_symbol is empty string
    List<Object> eqEmpty = new ArrayList<>();
    eqEmpty.add("$" + GENE_SYMBOL_FIELD);
    eqEmpty.add("");

    // Check if gene_symbol is only whitespace
    List<Object> eqTrimmed = new ArrayList<>();
    eqTrimmed.add(new Document("$trim", new Document("input", "$" + GENE_SYMBOL_FIELD)));
    eqTrimmed.add("");

    // Combine all null/empty/whitespace checks with $or
    List<Document> orConditions = new ArrayList<>();
    orConditions.add(new Document("$eq", eqNull));
    orConditions.add(new Document("$eq", eqEmpty));
    orConditions.add(new Document("$eq", eqTrimmed));

    // $cond: if (gene_symbol is null/empty/whitespace) then ensembl_gene_id else gene_symbol
    List<Object> condArgs = new ArrayList<>();
    condArgs.add(new Document("$or", orConditions));
    condArgs.add("$" + ENSEMBL_GENE_ID_FIELD);
    condArgs.add("$" + GENE_SYMBOL_FIELD);

    Document addFieldsDoc = new Document(
      "$addFields",
      new Document(DISPLAY_GENE_SYMBOL_FIELD, new Document("$cond", condArgs))
    );

    return context -> addFieldsDoc;
  }

  /**
   * Override search to route each term to the field it identifies.
   *
   * <p>A term that is a complete Ensembl gene ID matches {@code ensembl_gene_id} only. Every other
   * term matches {@code gene_symbol}, falling back to {@code ensembl_gene_id} when
   * {@code gene_symbol} is null/empty, mirroring
   * {@code display_gene_symbol = gene_symbol ?? ensembl_gene_id}. Terms in a comma-separated list
   * are routed independently.
   *
   * <p><strong>NOTE:</strong> We cannot use {@code DISPLAY_GENE_SYMBOL_FIELD} here because it's a
   * computed field created by {@code $addFields} in the aggregation pipeline. The count query uses
   * {@code mongoTemplate.count()} for performance, which doesn't run the aggregation pipeline and
   * therefore has no access to the computed field. We must replicate the fallback logic using the
   * raw {@code gene_symbol} and {@code ensembl_gene_id} fields.
   */
  @Override
  protected Criteria buildSearchCriteria(String field, String trimmedSearch) {
    if (trimmedSearch.contains(",")) {
      return buildCommaSeparatedSearchCriteria(trimmedSearch);
    }
    return buildSingleTermSearchCriteria(trimmedSearch);
  }

  /** Single term: case-insensitive exact match for a full Ensembl gene ID,
   * partial match otherwise. */
  private Criteria buildSingleTermSearchCriteria(String term) {
    if (MouseEnsemblGeneId.isFullId(term)) {
      return ApiHelper.equalsAnyIgnoringCase(ENSEMBL_GENE_ID_FIELD, List.of(term));
    }

    String regex = Pattern.quote(term);
    return new Criteria()
      .orOperator(
        Criteria.where(GENE_SYMBOL_FIELD).regex(regex, "i"),
        whenGeneSymbolIsBlank(Criteria.where(ENSEMBL_GENE_ID_FIELD).regex(regex, "i"))
      );
  }

  /** Comma-separated list: case-insensitive exact match, each term routed independently. */
  private Criteria buildCommaSeparatedSearchCriteria(String trimmedSearch) {
    List<String> fullEnsemblGeneIdTerms = new ArrayList<>();
    List<String> otherTerms = new ArrayList<>();
    for (String term : ApiHelper.splitSearchTerms(trimmedSearch)) {
      (MouseEnsemblGeneId.isFullId(term) ? fullEnsemblGeneIdTerms : otherTerms).add(term);
    }

    List<Criteria> branches = new ArrayList<>();
    if (!fullEnsemblGeneIdTerms.isEmpty()) {
      branches.add(ApiHelper.equalsAnyIgnoringCase(ENSEMBL_GENE_ID_FIELD, fullEnsemblGeneIdTerms));
    }
    if (!otherTerms.isEmpty()) {
      branches.add(ApiHelper.equalsAnyIgnoringCase(GENE_SYMBOL_FIELD, otherTerms));
      // Only reachable for an off-pattern ensembl_gene_id (versioned, non-mouse, malformed), which
      // the single-term path also matches. Without it, searching "A" finds such a row but "A,B"
      // would not.
      Criteria ensemblGeneIdFallback = ApiHelper.equalsAnyIgnoringCase(
        ENSEMBL_GENE_ID_FIELD,
        otherTerms
      );
      branches.add(whenGeneSymbolIsBlank(ensemblGeneIdFallback));
    }

    if (branches.isEmpty()) {
      // Search was only commas: match nothing, as an empty $in would.
      return Criteria.where("_id").is(null);
    }
    if (branches.size() == 1) {
      return branches.get(0);
    }
    return new Criteria().orOperator(branches);
  }

  private static Criteria whenGeneSymbolIsBlank(Criteria fallbackMatch) {
    Criteria geneSymbolIsBlank = new Criteria()
      .orOperator(
        Criteria.where(GENE_SYMBOL_FIELD).is(null),
        Criteria.where(GENE_SYMBOL_FIELD).is(""),
        Criteria.where(GENE_SYMBOL_FIELD).regex("^\\s*$")
      );
    return new Criteria().andOperator(geneSymbolIsBlank, fallbackMatch);
  }
}
