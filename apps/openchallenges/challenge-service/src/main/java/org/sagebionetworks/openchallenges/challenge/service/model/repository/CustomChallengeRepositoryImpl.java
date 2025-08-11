package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.search.backend.elasticsearch.ElasticsearchExtension;
import org.hibernate.search.engine.search.common.BooleanOperator;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.SearchSort;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.sagebionetworks.openchallenges.challenge.service.exception.BadRequestException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomChallengeRepositoryImpl implements CustomChallengeRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<ChallengeEntity> findAll(
    Pageable pageable,
    ChallengeSearchQueryDto query,
    String... fields
  ) {
    SearchResult<ChallengeEntity> result = getSearchResult(pageable, query, fields);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchResult<ChallengeEntity> getSearchResult(
    Pageable pageable,
    ChallengeSearchQueryDto query,
    String[] fields
  ) {
    SearchSession searchSession = Search.session(entityManager);
    SearchPredicateFactory pf = searchSession.scope(ChallengeEntity.class).predicate();
    SearchSortFactory sf = searchSession.scope(ChallengeEntity.class).sort();
    List<SearchPredicate> predicates = new ArrayList<>();

    if (query.getSearchTerms() != null && !query.getSearchTerms().isBlank()) {
      predicates.add(getSearchTermsPredicate(pf, query, fields));
    }
    if (query.getStatus() != null && !query.getStatus().isEmpty()) {
      predicates.add(getChallengeStatusPredicate(pf, query));
    }
    if (query.getPlatforms() != null && !query.getPlatforms().isEmpty()) {
      predicates.add(getChallengePlatformPredicate(pf, query));
    }
    if (query.getSubmissionTypes() != null && !query.getSubmissionTypes().isEmpty()) {
      predicates.add(getChallengeSubmissionTypesPredicate(pf, query));
    }
    if (query.getInputDataTypes() != null && !query.getInputDataTypes().isEmpty()) {
      predicates.add(getInputDataTypesPredicate(pf, query));
    }
    if (query.getIncentives() != null && !query.getIncentives().isEmpty()) {
      predicates.add(getChallengeIncentivesPredicate(pf, query));
    }
    if (query.getMinStartDate() != null || query.getMaxStartDate() != null) {
      predicates.add(getChallengeStartDatePredicate(pf, query));
    }
    if (query.getOrganizations() != null && !query.getOrganizations().isEmpty()) {
      predicates.add(getOrganizationsPredicate(pf, query));
    }
    if (query.getCategories() != null && !query.getCategories().isEmpty()) {
      predicates.add(getCategoriesPredicate(pf, query));
    }
    if (query.getOperations() != null && !query.getOperations().isEmpty()) {
      predicates.add(getChallengeOperationPredicate(pf, query));
    }

    SearchSort sort = getSearchSort(sf, query);
    SearchPredicate sortPredicate = getSearchSortPredicate(pf, query);
    if (sortPredicate != null) {
      predicates.add(sortPredicate);
    }

    SearchPredicate topLevelPredicate = buildTopLevelPredicate(pf, predicates);

    SearchResult<ChallengeEntity> result = searchSession
      .search(ChallengeEntity.class)
      .where(topLevelPredicate)
      .sort(sort)
      .fetch((int) pageable.getOffset(), pageable.getPageSize());
    return result;
  }

  /**
   * Searches the challenges using the search terms specified.
   *
   * @param pf
   * @param query
   * @param fields
   * @return
   */
  private SearchPredicate getSearchTermsPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query,
    String[] fields
  ) {
    return pf
      .simpleQueryString()
      .fields(fields)
      .matching(query.getSearchTerms())
      .defaultOperator(BooleanOperator.AND)
      .toPredicate();
  }

  /**
   * Matches the challenges whose status is in the list of status specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getChallengeStatusPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (ChallengeStatusDto status : query.getStatus()) {
          b.should(pf.match().field("status").matching(status.toString()));
        }
      })
      .toPredicate();
  }

  /**
   * Matches the challenges whose platform is in the list of platforms specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getChallengePlatformPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (String platform : query.getPlatforms()) {
          b.should(pf.match().field("platform.slug").matching(platform));
        }
      })
      .toPredicate();
  }

  private SearchPredicate getChallengeOperationPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (Integer operation : query.getOperations()) {
          b.should(pf.match().field("operation.id").matching(operation));
        }
      })
      .toPredicate();
  }

  /**
   * Matches the challenges whose at least one of their submission types is in the list of
   * submission types specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getChallengeSubmissionTypesPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (ChallengeSubmissionTypeDto submissionType : query.getSubmissionTypes()) {
          b.should(pf.match().field("submission_types.name").matching(submissionType.toString()));
        }
      })
      .toPredicate();
  }

  private SearchPredicate getInputDataTypesPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (Integer edamConceptId : query.getInputDataTypes()) {
          b.should(pf.match().field("input_data_types.id").matching(edamConceptId));
        }
      })
      .toPredicate();
  }

  /**
   * Matches the challenges whose at least one of their contributors is in the list of
   * contributors/organizations specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getOrganizationsPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (Long organizationId : query.getOrganizations()) {
          b.should(pf.match().field("contributions.organization_id").matching(organizationId));
        }
      })
      .toPredicate();
  }

  /**
   * Matches the challenges whose at least one of their submission types is in the list of
   * submission types specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getChallengeIncentivesPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (ChallengeIncentiveDto incentive : query.getIncentives()) {
          b.should(pf.match().field("incentives.name").matching(incentive.toString()));
        }
      })
      .toPredicate();
  }

  /**
   * This utility function creates a predicate clauses step for the challenge categories
   * RECENTLY_STARTED, RECENTLY_ENDED, STARTING_SOON, ENDING_SOON.
   */
  private SearchPredicate getStartEndDateAndStatusBooleanPredicateClausesStep(
    SearchPredicateFactory pf,
    String dateField,
    LocalDate minDate,
    LocalDate maxDate,
    ChallengeStatusDto status
  ) {
    SearchPredicate datePredicate = pf
      .range()
      .field(dateField)
      .between(minDate, maxDate)
      .toPredicate();
    SearchPredicate statusPredicate = pf
      .match()
      .field("status")
      .matching(status.toString())
      .toPredicate();
    return pf.bool(innerB -> innerB.must(datePredicate).must(statusPredicate)).toPredicate();
  }

  /**
   * Matches the challenges whose at least one of their categories is in the list of categories
   * specified.
   */
  private SearchPredicate getCategoriesPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    LocalDate now = LocalDate.now();
    LocalDate oneMonthLater = now.plusMonths(1);
    LocalDate threeMonthsAgo = now.minusMonths(3);

    return pf
      .bool(b -> {
        for (ChallengeCategoryDto category : query.getCategories()) {
          switch (category) {
            case RECENTLY_STARTED -> {
              b.should(
                getStartEndDateAndStatusBooleanPredicateClausesStep(
                  pf,
                  "start_date",
                  threeMonthsAgo,
                  now,
                  ChallengeStatusDto.ACTIVE
                )
              );
            }
            case RECENTLY_ENDED -> {
              b.should(
                getStartEndDateAndStatusBooleanPredicateClausesStep(
                  pf,
                  "end_date",
                  threeMonthsAgo,
                  now,
                  ChallengeStatusDto.COMPLETED
                )
              );
            }
            case STARTING_SOON -> {
              b.should(
                getStartEndDateAndStatusBooleanPredicateClausesStep(
                  pf,
                  "start_date",
                  now,
                  oneMonthLater,
                  ChallengeStatusDto.UPCOMING
                )
              );
            }
            case ENDING_SOON -> {
              b.should(
                getStartEndDateAndStatusBooleanPredicateClausesStep(
                  pf,
                  "end_date",
                  now,
                  oneMonthLater,
                  ChallengeStatusDto.ACTIVE
                )
              );
            }
            default -> {
              b.should(pf.match().field("categories.name").matching(category.toString()));
            }
          }
        }
      })
      .toPredicate();
  }

  /**
   * Matches the challenges whose start date is between the min and max start dates specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getChallengeStartDatePredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    return pf
      .range()
      .field("start_date")
      .between(query.getMinStartDate(), query.getMaxStartDate())
      .toPredicate();
  }

  /**
   * Combines the challenge search predicates.
   *
   * @param pf
   * @param predicates
   * @return
   */
  private SearchPredicate buildTopLevelPredicate(
    SearchPredicateFactory pf,
    List<SearchPredicate> predicates
  ) {
    return pf
      .bool(b -> {
        b.must(f -> f.matchAll());
        for (SearchPredicate predicate : predicates) {
          b.must(predicate);
        }
      })
      .toPredicate();
  }

  /**
   * Sorts the challenges according to the sort and direction values specified.
   *
   * @param sf
   * @param query
   * @return
   */
  private SearchSort getSearchSort(SearchSortFactory sf, ChallengeSearchQueryDto query) {
    SortOrder orderWithDefaultAsc = query.getDirection() == ChallengeDirectionDto.DESC
      ? SortOrder.DESC
      : SortOrder.ASC;
    SortOrder orderWithDefaultDesc = query.getDirection() == ChallengeDirectionDto.ASC
      ? SortOrder.ASC
      : SortOrder.DESC;

    SearchSort createdSort = sf.field("created_at").order(orderWithDefaultDesc).toSort();
    SearchSort scoreSort = sf.score().order(orderWithDefaultDesc).toSort();
    SearchSort relevanceSort = (query.getSearchTerms() == null || query.getSearchTerms().isBlank())
      ? createdSort
      : scoreSort;

    switch (query.getSort()) {
      case CREATED -> {
        return createdSort;
      }
      case RANDOM -> {
        return scoreSort;
      }
      case START_DATE -> {
        return sf.field("start_date").order(orderWithDefaultDesc).toSort();
      }
      case END_DATE -> {
        return sf.field("end_date").order(orderWithDefaultDesc).toSort();
      }
      case RELEVANCE -> {
        return relevanceSort;
      }
      case STARRED -> {
        return sf.field("starred_count").order(orderWithDefaultDesc).toSort();
      }
      default -> {
        throw new BadRequestException(
          String.format("Unhandled sorting strategy '%s'", query.getSort())
        );
      }
    }
  }

  private SearchPredicate getSearchSortPredicate(
    SearchPredicateFactory pf,
    ChallengeSearchQueryDto query
  ) {
    switch (query.getSort()) {
      case RANDOM -> {
        Integer seed = query.getSortSeed();
        if (seed == null) {
          SecureRandom rand = new SecureRandom();
          seed = rand.nextInt(Integer.MAX_VALUE);
        }
        return pf
          .extension(ElasticsearchExtension.get())
          .fromJson(
            "{" +
            "  \"function_score\": {" +
            "    \"random_score\": {" +
            "      \"seed\": " +
            seed +
            "," +
            "      \"field\": \"_seq_no\"" +
            "    }" +
            "  }" +
            "}"
          )
          .toPredicate();
      }
      default -> {
        return null;
      }
    }
  }
}
