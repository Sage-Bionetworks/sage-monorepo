package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.search.engine.search.common.BooleanOperator;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengePlatformEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomChallengePlatformRepositoryImpl implements CustomChallengePlatformRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Page<ChallengePlatformEntity> findAll(
      Pageable pageable, ChallengePlatformSearchQueryDto query, String... fields) {
    SearchResult<ChallengePlatformEntity> result = getSearchResult(pageable, query, fields);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchResult<ChallengePlatformEntity> getSearchResult(
      Pageable pageable, ChallengePlatformSearchQueryDto query, String[] fields) {
    SearchSession searchSession = Search.session(entityManager);
    SearchPredicateFactory pf = searchSession.scope(ChallengePlatformEntity.class).predicate();
    List<SearchPredicate> predicates = new ArrayList<>();

    if (query.getSearchTerms() != null && !query.getSearchTerms().isBlank()) {
      predicates.add(getSearchTermsPredicate(pf, query, fields));
    }

    SearchPredicate topLevelPredicate = buildTopLevelPredicate(pf, predicates);

    SearchResult<ChallengePlatformEntity> result =
        searchSession
            .search(ChallengePlatformEntity.class)
            .where(topLevelPredicate)
            .fetch((int) pageable.getOffset(), pageable.getPageSize());
    return result;
  }

  /**
   * Searches the challenge platforms using the search terms specified.
   *
   * @param pf
   * @param query
   * @param fields
   * @return
   */
  private SearchPredicate getSearchTermsPredicate(
      SearchPredicateFactory pf, ChallengePlatformSearchQueryDto query, String[] fields) {
    return pf.simpleQueryString()
        .fields(fields)
        .matching(query.getSearchTerms())
        .defaultOperator(BooleanOperator.AND)
        .toPredicate();
  }

  /**
   * Combines the search predicates.
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
}
