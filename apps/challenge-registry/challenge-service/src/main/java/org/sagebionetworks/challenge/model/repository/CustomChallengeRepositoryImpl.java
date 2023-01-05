package org.sagebionetworks.challenge.model.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
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
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.model.entity.QChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class CustomChallengeRepositoryImpl extends QuerydslRepositorySupport
    implements CustomChallengeRepository {

  @PersistenceContext private EntityManager entityManager;

  public CustomChallengeRepositoryImpl() {
    super(ChallengeEntity.class);
  }

  @Override
  public Page<ChallengeEntity> findAll(Pageable pageable, ChallengeFilter filter) {
    QChallengeEntity challenge = QChallengeEntity.challengeEntity;

    JPQLQuery<ChallengeEntity> query = from(challenge);

    if (filter.getStatus() != null && filter.getStatus().size() > 0) {
      query = query.where(challenge.status.in(filter.getStatus()));
    }
    if (filter.getPlatforms() != null && filter.getPlatforms().size() > 0) {
      query = query.where(challenge.platform.name.in(filter.getPlatforms()));
    }
    if (filter.getDifficulties() != null && filter.getDifficulties().size() > 0) {
      query = query.where(challenge.difficulty.in(filter.getDifficulties()));
    }
    if (filter.getSubmissionTypes() != null && filter.getSubmissionTypes().size() > 0) {
      query = query.where(challenge.submissionTypes.any().name.in(filter.getSubmissionTypes()));
    }
    if (filter.getIncentives() != null && filter.getIncentives().size() > 0) {
      query = query.where(challenge.incentives.any().name.in(filter.getIncentives()));
    }
    query = super.getQuerydsl().applyPagination(pageable, query);

    QueryResults<ChallengeEntity> results = query.fetchResults();
    return new PageImpl<>(results.getResults(), pageable, results.getTotal());
  }

  @Override
  public Page<ChallengeEntity> searchBy(
      Pageable pageable, ChallengeFilter filter, String text, String... fields) {
    SearchResult<ChallengeEntity> result = getSearchResult(pageable, filter, text, fields);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchResult<ChallengeEntity> getSearchResult(
      Pageable pageable, ChallengeFilter filter, String text, String[] fields) {
    SearchSession searchSession = Search.session(entityManager);
    SearchPredicateFactory pf = searchSession.scope(ChallengeEntity.class).predicate();
    List<SearchPredicate> predicates = new ArrayList<>();

    if (filter.getSearchTerms() != null && !filter.getSearchTerms().isBlank()) {
      predicates.add(
          pf.simpleQueryString()
              .fields(fields)
              .matching(filter.getSearchTerms())
              .defaultOperator(BooleanOperator.AND)
              .toPredicate());
    }

    if (filter.getStatus() != null && filter.getStatus().size() > 0) {
      predicates.add(pf.bool(b -> {
        for (String status: filter.getStatus()) {
          b.should(pf.match().field("status")
                  .matching(status));
        }
      }).toPredicate());
    }

    SearchPredicate topLevelPredicate =
        pf.bool(
                b -> {
                  b.must(f -> f.matchAll());
                  for (SearchPredicate predicate : predicates) {
                    b.must(predicate);
                  }
                })
            .toPredicate();

    SearchResult<ChallengeEntity> result =
        searchSession
            .search(ChallengeEntity.class) // Book.class
            .where(topLevelPredicate)
            // .where(f -> f.match().fields(fields).matching(text).fuzzy(2))
            // .sort( f -> f.field( "pageCount" ).desc())
            .fetch((int) pageable.getOffset(), pageable.getPageSize());
    return result;
  }
}
