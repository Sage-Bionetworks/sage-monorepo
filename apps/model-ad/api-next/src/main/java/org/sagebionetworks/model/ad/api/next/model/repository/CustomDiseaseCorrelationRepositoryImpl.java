package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.explorers.ComputedSortField;
import org.sagebionetworks.explorers.CtFilterConfig;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation using MongoDB aggregation pipeline.
 *
 * <p>Uses aggregation to support case-insensitive sorting and complex filtering logic.
 * All filtering logic is unified in a single implementation. The pipeline scaffold (count,
 * $match, $addFields, $sort, $skip, $limit) lives in {@link ComparisonToolRepositorySupport}.
 */
@Repository
@Slf4j
public class CustomDiseaseCorrelationRepositoryImpl
  extends ComparisonToolRepositorySupport<DiseaseCorrelationDocument>
  implements CustomDiseaseCorrelationRepository {

  private static final String COLLECTION_NAME = "disease_correlation";
  private static final String NAME_FIELD = "name";

  public CustomDiseaseCorrelationRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<DiseaseCorrelationDocument> getDocumentClass() {
    return DiseaseCorrelationDocument.class;
  }

  /**
   * Case-insensitive sort: each string field gets a lowercase {@code _sort} alias
   * (DocumentDB-compatible).
   */
  @Override
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
    return Map.of(
      "name",
      ComputedSortField.of(toLowerExpr("name")),
      "sex",
      ComputedSortField.of(toLowerExpr("sex")),
      "model_type",
      ComputedSortField.of(toLowerExpr("model_type")),
      "matched_control",
      ComputedSortField.of(toLowerExpr("matched_control")),
      "cluster",
      ComputedSortField.of(toLowerExpr("cluster"))
    );
  }

  /**
   * Maps {@code age} to its numeric companion field, and each heatmap column to its nested
   * {@code correlation} value. Without the heatmap aliases, {@code $sort} operates on the
   * full object ({@code { correlation, adj_p_val }}) and produces undefined ordering.
   */
  @Override
  protected Map<String, String> getSortFieldAliases() {
    return Map.ofEntries(
      Map.entry("age", "age_numeric"),
      Map.entry("CBE", "CBE.correlation"),
      Map.entry("DLPFC", "DLPFC.correlation"),
      Map.entry("FP", "FP.correlation"),
      Map.entry("IFG", "IFG.correlation"),
      Map.entry("PHG", "PHG.correlation"),
      Map.entry("STG", "STG.correlation"),
      Map.entry("TCX", "TCX.correlation")
    );
  }

  private final CtFilterConfig<DiseaseCorrelationSearchQueryDto> filterConfig =
    CtFilterConfig.<DiseaseCorrelationSearchQueryDto>builder()
      .dataFilter("age", DiseaseCorrelationSearchQueryDto::getAge)
      .dataFilter("model_type", DiseaseCorrelationSearchQueryDto::getModelType)
      .dataFilter("modified_genes", DiseaseCorrelationSearchQueryDto::getModifiedGenes)
      .dataFilter("name", DiseaseCorrelationSearchQueryDto::getName)
      .dataFilter("sex", DiseaseCorrelationSearchQueryDto::getSex)
      .compositeItemFilter(item -> DiseaseCorrelationIdentifier.parse(item).toCriteria())
      .searchFilter(NAME_FIELD)
      .build();

  @Override
  protected CtFilterConfig<DiseaseCorrelationSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public Page<DiseaseCorrelationDocument> findAll(
    Pageable pageable,
    DiseaseCorrelationSearchQueryDto query,
    List<String> items,
    String cluster
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
      Criteria.where("cluster").is(cluster)
    );

    return executePagedAggregation(matchCriteria, pageable);
  }
}
