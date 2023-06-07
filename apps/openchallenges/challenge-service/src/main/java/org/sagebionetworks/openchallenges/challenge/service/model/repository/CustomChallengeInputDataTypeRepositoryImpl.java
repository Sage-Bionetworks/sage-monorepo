package org.sagebionetworks.openchallenges.challenge.service.model.repository;

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
import org.sagebionetworks.openchallenges.challenge.service.exception.BadRequestException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeInputDataTypeDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeInputDataTypeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeInputDataTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomChallengeInputDataTypeRepositoryImpl
    implements CustomChallengeInputDataTypeRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Page<ChallengeInputDataTypeEntity> findAll(
      Pageable pageable, ChallengeInputDataTypeSearchQueryDto query, String... fields) {
    SearchResult<ChallengeInputDataTypeEntity> result = getSearchResult(pageable, query, fields);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchResult<ChallengeInputDataTypeEntity> getSearchResult(
      Pageable pageable, ChallengeInputDataTypeSearchQueryDto query, String[] fields) {
    SearchSession searchSession = Search.session(entityManager);
    SearchPredicateFactory pf = searchSession.scope(ChallengeInputDataTypeEntity.class).predicate();
    SearchSortFactory sf = searchSession.scope(ChallengeInputDataTypeEntity.class).sort();
    List<SearchPredicate> predicates = new ArrayList<>();

    if (query.getSearchTerms() != null && !query.getSearchTerms().isBlank()) {
      predicates.add(getSearchTermsPredicate(pf, query, fields));
    }

    SearchSort sort = getSearchSort(sf, query);
    SearchPredicate sortPredicate = getSearchSortPredicate(pf, query);
    if (sortPredicate != null) {
      predicates.add(sortPredicate);
    }

    SearchPredicate topLevelPredicate = buildTopLevelPredicate(pf, predicates);

    SearchResult<ChallengeInputDataTypeEntity> result =
        searchSession
            .search(ChallengeInputDataTypeEntity.class)
            .where(topLevelPredicate)
            .sort(sort)
            .fetch((int) pageable.getOffset(), pageable.getPageSize());
    return result;
  }

  private SearchPredicate getSearchTermsPredicate(
      SearchPredicateFactory pf, ChallengeInputDataTypeSearchQueryDto query, String[] fields) {
    return pf.simpleQueryString()
        .fields(fields)
        .matching(query.getSearchTerms())
        .defaultOperator(BooleanOperator.AND)
        .toPredicate();
  }

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

  private SearchSort getSearchSort(
      SearchSortFactory sf, ChallengeInputDataTypeSearchQueryDto query) {
    SortOrder orderWithDefaultAsc =
        query.getDirection() == ChallengeInputDataTypeDirectionDto.DESC
            ? SortOrder.DESC
            : SortOrder.ASC;
    SortOrder orderWithDefaultDesc =
        query.getDirection() == ChallengeInputDataTypeDirectionDto.ASC
            ? SortOrder.ASC
            : SortOrder.DESC;

    SearchSort nameSort = sf.field("name_sort").order(orderWithDefaultAsc).toSort();
    SearchSort scoreSort = sf.score().order(orderWithDefaultDesc).toSort();
    SearchSort relevanceSort =
        (query.getSearchTerms() == null || query.getSearchTerms().isBlank()) ? nameSort : scoreSort;

    switch (query.getSort()) {
      case NAME -> {
        return nameSort;
      }
      case RELEVANCE -> {
        return relevanceSort;
      }
      default -> {
        throw new BadRequestException(
            String.format("Unhandled sorting strategy '%s'", query.getSort()));
      }
    }
  }

  private SearchPredicate getSearchSortPredicate(
      SearchPredicateFactory pf, ChallengeInputDataTypeSearchQueryDto query) {
    switch (query.getSort()) {
      default -> {
        return null;
      }
    }
  }
}
