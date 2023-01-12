package org.sagebionetworks.challenge.model.repository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.search.engine.search.common.BooleanOperator;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.SearchSort;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.sagebionetworks.challenge.model.dto.ChallengeDifficultyDto;
import org.sagebionetworks.challenge.model.dto.ChallengeDirectionDto;
import org.sagebionetworks.challenge.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.challenge.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.challenge.model.dto.ChallengeSortDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.sagebionetworks.challenge.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomChallengeRepositoryImpl implements CustomChallengeRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Page<ChallengeEntity> findAll(
      Pageable pageable, ChallengeSearchQueryDto query, String... fields) {
    SearchResult<ChallengeEntity> result = getSearchResult(pageable, query, fields);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchResult<ChallengeEntity> getSearchResult(
      Pageable pageable, ChallengeSearchQueryDto query, String[] fields) {
    SearchSession searchSession = Search.session(entityManager);
    SearchPredicateFactory pf = searchSession.scope(ChallengeEntity.class).predicate();
    SearchSortFactory sf = searchSession.scope(ChallengeEntity.class).sort();
    List<SearchPredicate> predicates = new ArrayList<>();

    if (query.getSearchTerms() != null && !query.getSearchTerms().isBlank()) {
      predicates.add(getSearchTermsPredicate(pf, query, fields));
    }
    if (query.getStatus() != null && query.getStatus().size() > 0) {
      predicates.add(getChallengeStatusPredicate(pf, query));
    }
    if (query.getDifficulties() != null && query.getDifficulties().size() > 0) {
      predicates.add(getChallengeDifficultyPredicate(pf, query));
    }
    if (query.getPlatforms() != null && query.getPlatforms().size() > 0) {
      predicates.add(getChallengePlatformPredicate(pf, query));
    }
    if (query.getSubmissionTypes() != null && query.getSubmissionTypes().size() > 0) {
      predicates.add(getChallengeSubmissionTypesPredicate(pf, query));
    }
    if (query.getIncentives() != null && query.getIncentives().size() > 0) {
      predicates.add(getChallengeIncentivesPredicate(pf, query));
    }
    if (query.getMinStartDate() != null || query.getMaxStartDate() != null) {
      predicates.add(getChallengeStartDatePredicate(pf, query));
    }

    SearchPredicate topLevelPredicate = buildTopLevelPredicate(pf, predicates);
    SearchSort sort = getSearchSort(sf, query);

    SearchResult<ChallengeEntity> result =
        searchSession
            .search(ChallengeEntity.class) // Book.class
            .where(topLevelPredicate)
            .sort(sort)
            .fetch((int) pageable.getOffset(), pageable.getPageSize());
    return result;
  }

  /**
   * Searches the challenges using the search terms specified
   *
   * @param pf
   * @param query
   * @param fields
   * @return
   */
  private SearchPredicate getSearchTermsPredicate(
      SearchPredicateFactory pf, ChallengeSearchQueryDto query, String[] fields) {
    return pf.simpleQueryString()
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
      SearchPredicateFactory pf, ChallengeSearchQueryDto query) {
    return pf.bool(
            b -> {
              for (ChallengeStatusDto status : query.getStatus()) {
                b.should(pf.match().field("status").matching(status.toString()));
              }
            })
        .toPredicate();
  }

  /**
   * Matches the challenges whose difficulty is in the list of difficulties specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getChallengeDifficultyPredicate(
      SearchPredicateFactory pf, ChallengeSearchQueryDto query) {
    return pf.bool(
            b -> {
              for (ChallengeDifficultyDto difficulty : query.getDifficulties()) {
                b.should(pf.match().field("difficulty").matching(difficulty.toString()));
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
      SearchPredicateFactory pf, ChallengeSearchQueryDto query) {
    return pf.bool(
            b -> {
              for (String platform : query.getPlatforms()) {
                b.should(pf.match().field("platform.name").matching(platform));
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
      SearchPredicateFactory pf, ChallengeSearchQueryDto query) {
    return pf.bool(
            b -> {
              for (ChallengeSubmissionTypeDto submissionType : query.getSubmissionTypes()) {
                b.should(
                    pf.match().field("submission_types.name").matching(submissionType.toString()));
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
      SearchPredicateFactory pf, ChallengeSearchQueryDto query) {
    return pf.bool(
            b -> {
              for (ChallengeIncentiveDto incentive : query.getIncentives()) {
                b.should(pf.match().field("incentives.name").matching(incentive.toString()));
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
      SearchPredicateFactory pf, ChallengeSearchQueryDto query) {
    return pf.range()
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
      SearchPredicateFactory pf, List<SearchPredicate> predicates) {
    return pf.bool(
            b -> {
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
    SortOrder order =
        query.getDirection() == ChallengeDirectionDto.DESC ? SortOrder.DESC : SortOrder.ASC;

    SearchSort createdSort = sf.field("created_at").order(order).toSort();
    SearchSort scoreSort = sf.score().order(order).toSort();
    SearchSort relevanceSort = query.getSearchTerms().isBlank() ? createdSort : scoreSort;

    switch (query.getSort()) {
      case CREATED -> {
        return createdSort;
      }
      case RELEVANCE -> {
        return relevanceSort;
      }
      case STARRED -> {
        return sf.field("starred_count").order(order).toSort();
      }
      default -> {
        return relevanceSort;
      }
    }
  }
}
