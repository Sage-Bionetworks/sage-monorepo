package org.sagebionetworks.agora.api.next.model.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.explorers.ComputedSortField;
import org.sagebionetworks.explorers.CtFilterConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation using MongoDB aggregation pipeline.
 *
 * <p>Uses aggregation to support sorting by array fields. MongoDB cannot sort by multiple
 * array fields simultaneously ("parallel arrays"), so we compute scalar sort fields for
 * lexicographic comparison. The pipeline scaffold (count, $match, $addFields, $sort, $skip,
 * $limit) lives in {@link ComparisonToolRepositorySupport}.
 */
@Repository
@Slf4j
public class CustomNominatedTargetRepositoryImpl
  extends ComparisonToolRepositorySupport<NominatedTargetDocument>
  implements CustomNominatedTargetRepository {

  private static final String COLLECTION_NAME = "nominatedtargets";
  private static final String PRIMARY_FIELD = "hgnc_symbol";

  public CustomNominatedTargetRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<NominatedTargetDocument> getDocumentClass() {
    return NominatedTargetDocument.class;
  }

  /**
   * Array fields are reduced to a NUL-separated lowercase string so they can sort
   * alongside other array fields without hitting MongoDB's "parallel arrays" limit.
   */
  @Override
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
    return Map.of(
      "nominating_teams",
      ComputedSortField.of(arrayToLoweredStringExpr("nominating_teams")),
      "cohort_studies",
      ComputedSortField.of(arrayToLoweredStringExpr("cohort_studies")),
      "input_data",
      ComputedSortField.of(arrayToLoweredStringExpr("input_data")),
      "programs",
      ComputedSortField.of(arrayToLoweredStringExpr("programs"))
    );
  }

  private final CtFilterConfig<NominatedTargetSearchQueryDto> filterConfig =
    CtFilterConfig.<NominatedTargetSearchQueryDto>builder()
      .dataFilter("cohort_studies", NominatedTargetSearchQueryDto::getCohortStudies)
      .dataFilter("input_data", NominatedTargetSearchQueryDto::getInputData)
      .dataFilter("initial_nomination", NominatedTargetSearchQueryDto::getInitialNomination)
      .dataFilter("nominating_teams", NominatedTargetSearchQueryDto::getNominatingTeams)
      .dataFilter("pharos_class", NominatedTargetSearchQueryDto::getPharosClass)
      .dataFilter("programs", NominatedTargetSearchQueryDto::getPrograms)
      .dataFilter("total_nominations", NominatedTargetSearchQueryDto::getTotalNominations)
      .simpleItemFilter(PRIMARY_FIELD)
      .searchFilter(PRIMARY_FIELD)
      .build();

  @Override
  protected CtFilterConfig<NominatedTargetSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public Page<NominatedTargetDocument> findAll(
    Pageable pageable,
    NominatedTargetSearchQueryDto query,
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
