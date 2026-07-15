package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.explorers.ComputedSortField;
import org.sagebionetworks.explorers.CtFilterConfig;
import org.sagebionetworks.model.ad.api.next.model.document.MarmosetModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation backed by the shared CT aggregation pipeline.
 *
 * <p>Uses aggregation to support sorting by array fields. MongoDB cannot sort by multiple
 * array fields simultaneously ("parallel arrays"), so we compute scalar sort fields for
 * lexicographic comparison. The pipeline scaffold (count, $match, $addFields, $sort, $skip,
 * $limit) lives in {@link ComparisonToolRepositorySupport}.
 */
@Repository
@Slf4j
public class CustomMarmosetModelOverviewRepositoryImpl
  extends ComparisonToolRepositorySupport<MarmosetModelOverviewDocument>
  implements CustomMarmosetModelOverviewRepository {

  private static final String COLLECTION_NAME = "marmo_overview";

  public CustomMarmosetModelOverviewRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<MarmosetModelOverviewDocument> getDocumentClass() {
    return MarmosetModelOverviewDocument.class;
  }

  @Override
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
    return Map.of(
      "modified_genes",
      ComputedSortField.of(arrayToLoweredStringExpr("modified_genes")),
      "available_data",
      ComputedSortField.of(arrayToLoweredStringExpr("available_data"))
    );
  }

  private final CtFilterConfig<MarmosetModelOverviewSearchQueryDto> filterConfig = CtFilterConfig.<
    MarmosetModelOverviewSearchQueryDto
  >builder()
    .dataFilter("available_data", MarmosetModelOverviewSearchQueryDto::getAvailableData)
    .dataFilter("model_type", MarmosetModelOverviewSearchQueryDto::getModelTypes)
    .dataFilter("modified_genes", MarmosetModelOverviewSearchQueryDto::getModifiedGenes)
    .simpleItemFilter("name")
    .searchFilter("name")
    .build();

  @Override
  protected CtFilterConfig<MarmosetModelOverviewSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public Page<MarmosetModelOverviewDocument> findAll(
    Pageable pageable,
    MarmosetModelOverviewSearchQueryDto query,
    List<String> items
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
      getFilterConfig()
    );

    return executePagedAggregation(matchCriteria, pageable);
  }
}
