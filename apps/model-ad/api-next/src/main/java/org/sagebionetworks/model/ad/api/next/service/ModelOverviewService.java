package org.sagebionetworks.model.ad.api.next.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelOverviewRepository;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.MODEL_OVERVIEW)
public class ModelOverviewService {

  private final ModelOverviewRepository repository;
  private final ModelOverviewMapper modelOverviewMapper;
  private final MongoTemplate mongoTemplate;

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)" +
    ".buildCacheKey('modelOverview', #query.itemFilterType, " +
    "#query.items, #query.search, #query.availableData, #query.center, " +
    "#query.modelType, #query.modifiedGenes, #query.pageNumber, #query.pageSize, " +
    "#query.sortFields, #query.sortOrders)"
  )
  public ModelOverviewsPageDto loadModelOverviews(ModelOverviewSearchQueryDto query) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());
    String search = query.getSearch();
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(ModelOverviewSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);

    // Early return for INCLUDE with empty items and no filters
    if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE &&
        items.isEmpty() &&
        !hasActiveFilters(query)) {
      return buildEmptyResponse(pageable);
    }

    // Build and execute query using MongoTemplate
    Page<ModelOverviewDocument> page = fetchPageWithFilters(query, items, search, effectiveFilter, pageable);

    List<ModelOverviewDto> dtos = page
      .getContent()
      .stream()
      .map(modelOverviewMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return ModelOverviewsPageDto.builder().modelOverviews(dtos).page(pageMetadata).build();
  }

  private boolean hasActiveFilters(ModelOverviewSearchQueryDto query) {
    return (query.getAvailableData() != null && !query.getAvailableData().isEmpty()) ||
           (query.getCenter() != null && !query.getCenter().isEmpty()) ||
           (query.getModelType() != null && !query.getModelType().isEmpty()) ||
           (query.getModifiedGenes() != null && !query.getModifiedGenes().isEmpty());
  }

  private ModelOverviewsPageDto buildEmptyResponse(Pageable pageable) {
    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(pageable.getPageNumber())
      .size(pageable.getPageSize())
      .totalElements(0L)
      .totalPages(0)
      .hasNext(false)
      .hasPrevious(false)
      .build();

    return ModelOverviewsPageDto.builder()
      .modelOverviews(List.of())
      .page(pageMetadata)
      .build();
  }

  private Page<ModelOverviewDocument> fetchPageWithFilters(
    ModelOverviewSearchQueryDto query,
    List<String> items,
    String search,
    ItemFilterTypeQueryDto filterType,
    Pageable pageable
  ) {
    Query mongoQuery = new Query();
    List<Criteria> andCriteria = new ArrayList<>();

    // Add data filters (AND between fields, OR within field)
    addDataFilterCriteria(query, andCriteria);

    // Add name filtering (items + itemFilterType)
    addNameFilterCriteria(items, filterType, andCriteria);

    // Add search filtering (only when itemFilterType is EXCLUDE)
    addSearchFilterCriteria(search, filterType, andCriteria);

    // Combine all criteria with AND
    if (!andCriteria.isEmpty()) {
      mongoQuery.addCriteria(new Criteria().andOperator(
        andCriteria.toArray(new Criteria[0])
      ));
    }

    mongoQuery.with(pageable);

    // Execute query
    List<ModelOverviewDocument> results = mongoTemplate.find(
      mongoQuery,
      ModelOverviewDocument.class
    );

    // Count total for pagination (without limit/skip)
    long total = mongoTemplate.count(
      Query.of(mongoQuery).limit(-1).skip(-1),
      ModelOverviewDocument.class
    );

    return new PageImpl<>(results, pageable, total);
  }

  private void addDataFilterCriteria(ModelOverviewSearchQueryDto query, List<Criteria> andCriteria) {
    // available_data: array field - use $in (matches if array contains any value)
    if (query.getAvailableData() != null && !query.getAvailableData().isEmpty()) {
      andCriteria.add(Criteria.where("available_data").in(query.getAvailableData()));
    }

    // center: nested field - query center.link_text
    if (query.getCenter() != null && !query.getCenter().isEmpty()) {
      andCriteria.add(Criteria.where("center.link_text").in(query.getCenter()));
    }

    // model_type: string field - use $in (matches if value equals any)
    if (query.getModelType() != null && !query.getModelType().isEmpty()) {
      andCriteria.add(Criteria.where("model_type").in(query.getModelType()));
    }

    // modified_genes: array field - use $in (matches if array contains any value)
    if (query.getModifiedGenes() != null && !query.getModifiedGenes().isEmpty()) {
      andCriteria.add(Criteria.where("modified_genes").in(query.getModifiedGenes()));
    }
  }

  private void addNameFilterCriteria(
    List<String> items,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    if (items.isEmpty()) {
      return; // No name filtering needed
    }

    if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
      andCriteria.add(Criteria.where("name").in(items));
    } else {
      andCriteria.add(Criteria.where("name").nin(items));
    }
  }

  private void addSearchFilterCriteria(
    String search,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    // Search only applies when itemFilterType is EXCLUDE
    if (filterType != ItemFilterTypeQueryDto.EXCLUDE ||
        search == null ||
        search.trim().isEmpty()) {
      return;
    }

    String trimmedSearch = search.trim();
    if (trimmedSearch.contains(",")) {
      // Comma-separated list: case-insensitive full matches
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      andCriteria.add(Criteria.where("name").in(patterns));
    } else {
      // Single term: case-insensitive partial match
      String quotedSearch = Pattern.quote(trimmedSearch);
      andCriteria.add(Criteria.where("name").regex(quotedSearch, "i"));
    }
  }
}
