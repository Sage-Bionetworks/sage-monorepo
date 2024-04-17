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
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamSectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.EdamConceptEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomEdamConceptRepositoryImpl implements CustomEdamConceptRepository {

  private static final Logger LOG = LoggerFactory.getLogger(CustomEdamConceptRepositoryImpl.class);

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Page<EdamConceptEntity> findAll(
      Pageable pageable, EdamConceptSearchQueryDto query, String... fields) {
    SearchResult<EdamConceptEntity> result = getSearchResult(pageable, query, fields);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchResult<EdamConceptEntity> getSearchResult(
      Pageable pageable, EdamConceptSearchQueryDto query, String[] fields) {
    SearchSession searchSession = Search.session(entityManager);
    SearchPredicateFactory pf = searchSession.scope(EdamConceptEntity.class).predicate();
    List<SearchPredicate> predicates = new ArrayList<>();

    if (query.getSearchTerms() != null && !query.getSearchTerms().isBlank()) {
      predicates.add(getSearchTermsPredicate(pf, query, fields));
    }
    if (query.getSections() != null && !query.getSections().isEmpty()) {
      predicates.add(getEdamSectionsPredicate(pf, query));
    }

    SearchPredicate topLevelPredicate = buildTopLevelPredicate(pf, predicates);

    return searchSession
        .search(EdamConceptEntity.class)
        .where(topLevelPredicate)
        .fetch((int) pageable.getOffset(), pageable.getPageSize());
  }

  /**
   * Searches the EDAM concepts using the search terms specified.
   *
   * @param pf
   * @param query
   * @param fields
   * @return
   */
  private SearchPredicate getSearchTermsPredicate(
      SearchPredicateFactory pf, EdamConceptSearchQueryDto query, String[] fields) {
    return pf.simpleQueryString()
        .fields(fields)
        .matching(query.getSearchTerms())
        .defaultOperator(BooleanOperator.AND)
        .toPredicate();
  }

  /**
   * Searches the EDAM concepts whose section is in the list of sections specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getEdamSectionsPredicate(
      SearchPredicateFactory pf, EdamConceptSearchQueryDto query) {
    return pf.bool(
            b -> {
              for (EdamSectionDto section : query.getSections()) {
                LOG.info("Looking for section: {}", section.toString());
                b.should(pf.match().field("section").matching(section.toString()));
              }
            })
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
