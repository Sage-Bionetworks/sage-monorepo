package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.explorers.CtFilterConfig;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.util.MouseEnsemblGeneId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation using MongoDB aggregation pipeline.
 *
 * <p>No computed sort fields are needed: {@code display_symbol} is stored on the document, and
 * case-insensitive ordering comes from pipeline-level collation. The pipeline scaffold (count,
 * $match, $addFields, $sort, $skip, $limit) lives in {@link ComparisonToolRepositorySupport}.
 *
 * <p>Search is customized so that a pasted list of identifiers matches whichever field each
 * identifier belongs to; see {@link #buildSearchCriteria(String, String)}.
 */
@Repository
@Slf4j
public class CustomProteomicsRepositoryImpl
  extends ComparisonToolRepositorySupport<ProteomicsDocument>
  implements CustomProteomicsRepository {

  private static final String COLLECTION_NAME = "protein_de_aggregate";
  private static final String DISPLAY_SYMBOL_FIELD = "display_symbol";
  private static final String ENSEMBL_GENE_ID_FIELD = "ensembl_gene_id";
  private static final String GENE_SYMBOL_FIELD = "gene_symbol";
  private static final String UNIPROTID_FIELD = "uniprotid";

  /**
   * The identifier fields a comma-separated search term is full-matched against. These are raw
   * Mongo field names, which is what the count query needs: {@code executePagedAggregation} counts
   * via {@code mongoTemplate.count(query, collectionName)}, which performs no {@code @Field} name
   * translation.
   */
  private static final List<String> FULL_MATCH_SEARCH_FIELDS = List.of(
    ENSEMBL_GENE_ID_FIELD,
    GENE_SYMBOL_FIELD,
    UNIPROTID_FIELD
  );

  public CustomProteomicsRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<ProteomicsDocument> getDocumentClass() {
    return ProteomicsDocument.class;
  }

  private final CtFilterConfig<ProteomicsSearchQueryDto> filterConfig = CtFilterConfig.<
    ProteomicsSearchQueryDto
  >builder()
    .dataFilter("biodomains", ProteomicsSearchQueryDto::getBiodomains)
    .dataFilter("model_type", ProteomicsSearchQueryDto::getModelType)
    .dataFilter("name.link_text", ProteomicsSearchQueryDto::getName)
    .dataFilter("sex", ProteomicsSearchQueryDto::getSex)
    .compositeItemFilter(item -> ProteomicsIdentifier.parse(item).toCriteria())
    .searchFilter(DISPLAY_SYMBOL_FIELD)
    .build();

  /**
   * Maps each heatmap time-point column to its nested {@code log2_fc} value
   * ({@code { log2_fc, adj_p_val }}). Keys must stay in sync with the
   * {@link ProteomicsDocument} heatmap fields.
   */
  @Override
  protected Map<String, String> getSortFieldAliases() {
    return Map.of(
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
  protected CtFilterConfig<ProteomicsSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public Page<ProteomicsDocument> findAll(
    Pageable pageable,
    ProteomicsSearchQueryDto query,
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
   * Override search so that every identifier a row carries is reachable.
   *
   * <p>Comma-separated terms are full, case-insensitive matches routed across all three identifier
   * fields ({@code ensembl_gene_id}, {@code gene_symbol}, {@code uniprotid}), so each term matches
   * wherever it belongs without the caller having to say which field it came from.
   *
   * <p>A lone term that is a complete Ensembl gene ID full-matches {@code ensembl_gene_id}. Any
   * other single term stays a partial match on {@code display_symbol}, which already covers gene
   * symbols and UniProt IDs because it embeds both.
   *
   * <p><strong>NOTE:</strong> the comma-separated path cannot use {@code display_symbol}. It is a
   * composite label ({@code "Ensa (P11934870)"}), so a full match against a bare identifier could
   * never succeed.
   */
  @Override
  protected Criteria buildSearchCriteria(String field, String trimmedSearch) {
    if (trimmedSearch.contains(",")) {
      return buildCommaSeparatedSearchCriteria(trimmedSearch);
    }
    if (MouseEnsemblGeneId.isFullId(trimmedSearch)) {
      return ApiHelper.equalsAnyIgnoringCase(ENSEMBL_GENE_ID_FIELD, List.of(trimmedSearch));
    }
    return Criteria.where(field).regex(Pattern.quote(trimmedSearch), "i");
  }

  private Criteria buildCommaSeparatedSearchCriteria(String trimmedSearch) {
    List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
    if (patterns.isEmpty()) {
      // Search was only commas: match nothing, as an empty $in would.
      return Criteria.where("_id").is(null);
    }
    return new Criteria()
      .orOperator(
        FULL_MATCH_SEARCH_FIELDS.stream()
          .map(searchField -> Criteria.where(searchField).in(patterns))
          .toList()
      );
  }
}
