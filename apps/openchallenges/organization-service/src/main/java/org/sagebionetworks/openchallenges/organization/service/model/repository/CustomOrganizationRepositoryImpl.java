package org.sagebionetworks.openchallenges.organization.service.model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.search.engine.search.common.BooleanOperator;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.SearchSort;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.sagebionetworks.openchallenges.organization.service.exception.BadRequestException;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationCategoryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDirectionDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomOrganizationRepositoryImpl implements CustomOrganizationRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Optional<OrganizationEntity> findBySimpleNaturalId(String naturalId) {
    return entityManager
      .unwrap(Session.class)
      .bySimpleNaturalId(OrganizationEntity.class)
      .loadOptional(naturalId);
  }

  @Override
  public Page<OrganizationEntity> findAll(
    Pageable pageable,
    OrganizationSearchQueryDto query,
    String... fields
  ) {
    SearchResult<OrganizationEntity> result = getSearchResult(pageable, query, fields);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchResult<OrganizationEntity> getSearchResult(
    Pageable pageable,
    OrganizationSearchQueryDto query,
    String[] fields
  ) {
    SearchSession searchSession = Search.session(entityManager);
    SearchPredicateFactory pf = searchSession.scope(OrganizationEntity.class).predicate();
    SearchSortFactory sf = searchSession.scope(OrganizationEntity.class).sort();
    List<SearchPredicate> predicates = new ArrayList<>();

    if (query.getIds() != null && !query.getIds().isEmpty()) {
      predicates.add(getIdsPredicate(pf, query));
    }
    if (query.getSearchTerms() != null && !query.getSearchTerms().isBlank()) {
      predicates.add(getSearchTermsPredicate(pf, query, fields));
    }
    if (query.getCategories() != null && !query.getCategories().isEmpty()) {
      predicates.add(getCategoriesPredicate(pf, query));
    }
    if (
      query.getChallengeParticipationRoles() != null &&
      !query.getChallengeParticipationRoles().isEmpty()
    ) {
      predicates.add(getChallengeParticipationRolesPredicate(pf, query));
    }

    SearchSort sort = getSearchSort(sf, query);
    SearchPredicate sortPredicate = getSearchSortPredicate(pf, query);
    if (sortPredicate != null) {
      predicates.add(sortPredicate);
    }

    SearchPredicate topLevelPredicate = buildTopLevelPredicate(pf, predicates);

    SearchResult<OrganizationEntity> result = searchSession
      .search(OrganizationEntity.class)
      .where(topLevelPredicate)
      .sort(sort)
      .fetch((int) pageable.getOffset(), pageable.getPageSize());
    return result;
  }

  /**
   * Searches the organizations using the search terms specified.
   *
   * @param pf
   * @param query
   * @param fields
   * @return
   */
  private SearchPredicate getSearchTermsPredicate(
    SearchPredicateFactory pf,
    OrganizationSearchQueryDto query,
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
   * Matches the organization whose at least one of their contributor roles is in the list of
   * contributor roles specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getChallengeParticipationRolesPredicate(
    SearchPredicateFactory pf,
    OrganizationSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (ChallengeParticipationRoleDto role : query.getChallengeParticipationRoles()) {
          b.should(pf.match().field("challenge_participations.role").matching(role.toString()));
        }
      })
      .toPredicate();
  }

  /**
   * Matches the organization whose at least one of their id is in the list of ids specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getIdsPredicate(
    SearchPredicateFactory pf,
    OrganizationSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (Long id : query.getIds()) {
          b.should(pf.match().field("id").matching(id));
        }
      })
      .toPredicate();
  }

  /**
   * Matches the organization whose at least one of their categories is in the list of categories
   * specified.
   *
   * @param pf
   * @param query
   * @return
   */
  private SearchPredicate getCategoriesPredicate(
    SearchPredicateFactory pf,
    OrganizationSearchQueryDto query
  ) {
    return pf
      .bool(b -> {
        for (OrganizationCategoryDto category : query.getCategories()) {
          b.should(pf.match().field("categories.category").matching(category.toString()));
        }
      })
      .toPredicate();
  }

  /**
   * Combines the organization search predicates.
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
   * Sorts the organizations according to the sort and direction values specified.
   *
   * @param sf
   * @param query
   * @return
   */
  private SearchSort getSearchSort(SearchSortFactory sf, OrganizationSearchQueryDto query) {
    SortOrder orderWithDefaultAsc = query.getDirection() == OrganizationDirectionDto.DESC
      ? SortOrder.DESC
      : SortOrder.ASC;
    SortOrder orderWithDefaultDesc = query.getDirection() == OrganizationDirectionDto.ASC
      ? SortOrder.ASC
      : SortOrder.DESC;

    SearchSort challengeCountSort = sf
      .field("challenge_count")
      .order(orderWithDefaultDesc)
      .toSort();
    SearchSort createdSort = sf.field("created_at").order(orderWithDefaultDesc).toSort();
    SearchSort nameSort = sf.field("name_sort").order(orderWithDefaultAsc).toSort();
    SearchSort scoreSort = sf.score().order(orderWithDefaultDesc).toSort();
    SearchSort relevanceSort = (query.getSearchTerms() == null || query.getSearchTerms().isBlank())
      ? createdSort
      : scoreSort;

    switch (query.getSort()) {
      case CHALLENGE_COUNT -> {
        return challengeCountSort;
      }
      case CREATED -> {
        return createdSort;
      }
      case NAME -> {
        return nameSort;
      }
      case RELEVANCE -> {
        return relevanceSort;
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
    OrganizationSearchQueryDto query
  ) {
    switch (query.getSort()) {
      default -> {
        return null;
      }
    }
  }
}
