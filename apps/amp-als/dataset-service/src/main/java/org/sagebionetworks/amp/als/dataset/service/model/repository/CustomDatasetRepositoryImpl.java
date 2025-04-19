package org.sagebionetworks.amp.als.dataset.service.model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.security.SecureRandom;
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
import org.sagebionetworks.amp.als.dataset.service.exception.BadRequestException;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDirectionDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetSearchQueryDto;
import org.sagebionetworks.amp.als.dataset.service.model.entity.DatasetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomDatasetRepositoryImpl implements CustomDatasetRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<DatasetEntity> findAll(
    Pageable pageable,
    DatasetSearchQueryDto query,
    String... fields
  ) {
    SearchResult<DatasetEntity> result = getSearchResult(pageable, query, fields);
    return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
  }

  private SearchResult<DatasetEntity> getSearchResult(
    Pageable pageable,
    DatasetSearchQueryDto query,
    String[] fields
  ) {
    SearchSession searchSession = Search.session(entityManager);
    SearchPredicateFactory pf = searchSession.scope(DatasetEntity.class).predicate();
    SearchSortFactory sf = searchSession.scope(DatasetEntity.class).sort();
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

    SearchResult<DatasetEntity> result = searchSession
      .search(DatasetEntity.class)
      .where(topLevelPredicate)
      .sort(sort)
      .fetch((int) pageable.getOffset(), pageable.getPageSize());
    return result;
  }

  /**
   * Searches the datasets using the search terms specified.
   *
   * @param pf
   * @param query
   * @param fields
   * @return
   */
  private SearchPredicate getSearchTermsPredicate(
    SearchPredicateFactory pf,
    DatasetSearchQueryDto query,
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
   * Combines the dataset search predicates.
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
   * Sorts the datasets according to the sort and direction values specified.
   *
   * @param sf
   * @param query
   * @return
   */
  private SearchSort getSearchSort(SearchSortFactory sf, DatasetSearchQueryDto query) {
    // SortOrder orderWithDefaultAsc = query.getDirection() == DatasetDirectionDto.DESC
    //   ? SortOrder.DESC
    //   : SortOrder.ASC;
    SortOrder orderWithDefaultDesc = query.getDirection() == DatasetDirectionDto.ASC
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
    DatasetSearchQueryDto query
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
