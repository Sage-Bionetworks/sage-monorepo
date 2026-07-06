package org.sagebionetworks.agora.api.next.model.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugIdentifier;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugSearchQueryDto;
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
public class CustomNominatedDrugRepositoryImpl
  extends ComparisonToolRepositorySupport<NominatedDrugDocument>
  implements CustomNominatedDrugRepository {

  private static final String COLLECTION_NAME = "nominateddrugs";
  private static final String SEARCH_FIELD = "common_name";

  public CustomNominatedDrugRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<NominatedDrugDocument> getDocumentClass() {
    return NominatedDrugDocument.class;
  }

  /**
   * Array fields are reduced to a NUL-separated lowercase string so they can sort
   * alongside other array fields without hitting MongoDB's "parallel arrays" limit.
   */
  @Override
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
    return Map.of(
      "principal_investigators",
      ComputedSortField.of(arrayToLoweredStringExpr("principal_investigators")),
      "programs",
      ComputedSortField.of(arrayToLoweredStringExpr("programs"))
    );
  }

  private final CtFilterConfig<NominatedDrugSearchQueryDto> filterConfig =
    CtFilterConfig.<NominatedDrugSearchQueryDto>builder()
      .dataFilter("principal_investigators", NominatedDrugSearchQueryDto::getPrincipalInvestigators)
      .dataFilter("programs", NominatedDrugSearchQueryDto::getPrograms)
      .dataFilter("total_nominations", NominatedDrugSearchQueryDto::getTotalNominations)
      .dataFilter("initial_nomination", NominatedDrugSearchQueryDto::getInitialNomination)
      .dataFilter("modality", NominatedDrugSearchQueryDto::getModality)
      .dataFilter(
        "maximum_clinical_trial_phase",
        NominatedDrugSearchQueryDto::getMaximumClinicalTrialPhase
      )
      .compositeItemFilter(item -> NominatedDrugIdentifier.parse(item).toCriteria())
      .searchFilter(SEARCH_FIELD)
      .build();

  @Override
  protected CtFilterConfig<NominatedDrugSearchQueryDto> getFilterConfig() {
    return filterConfig;
  }

  @Override
  public Page<NominatedDrugDocument> findAll(
    Pageable pageable,
    NominatedDrugSearchQueryDto query,
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
