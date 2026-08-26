package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.explorers.CtFilterConfig;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
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
 */
@Repository
@Slf4j
public class CustomProteomicsRepositoryImpl
  extends ComparisonToolRepositorySupport<ProteomicsDocument>
  implements CustomProteomicsRepository {

  private static final String COLLECTION_NAME = "protein_de_aggregate";
  private static final String DISPLAY_SYMBOL_FIELD = "display_symbol";

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
}
