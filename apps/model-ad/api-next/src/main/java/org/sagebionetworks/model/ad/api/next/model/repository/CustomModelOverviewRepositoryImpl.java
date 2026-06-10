package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.explorers.ComputedSortField;
import org.sagebionetworks.explorers.CtFilterConfig;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
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
public class CustomModelOverviewRepositoryImpl
  extends ComparisonToolRepositorySupport<ModelOverviewDocument>
  implements CustomModelOverviewRepository {

  private static final String COLLECTION_NAME = "model_overview";

  public CustomModelOverviewRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<ModelOverviewDocument> getDocumentClass() {
    return ModelOverviewDocument.class;
  }

  @Override
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
    return Map.of(
      "matched_controls",
      ComputedSortField.of(arrayToLoweredStringExpr("matched_controls"))
    );
  }

  private final CtFilterConfig<ModelOverviewSearchQueryDto> filterConfig = CtFilterConfig.<
    ModelOverviewSearchQueryDto
  >builder()
    .dataFilter("available_data", ModelOverviewSearchQueryDto::getAvailableData)
    .dataFilter("center", ModelOverviewSearchQueryDto::getCenter)
    .dataFilter("model_type", ModelOverviewSearchQueryDto::getModelType)
    .dataFilter("modified_genes", ModelOverviewSearchQueryDto::getModifiedGenes)
    .simpleItemFilter("name")
    .searchFilter("name")
    .build();

  @Override
  protected CtFilterConfig<ModelOverviewSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public Page<ModelOverviewDocument> findAll(
    Pageable pageable,
    ModelOverviewSearchQueryDto query,
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
