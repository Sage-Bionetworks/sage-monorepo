package org.sagebionetworks.model.ad.api.next.service;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.MODEL_OVERVIEW)
public class ModelOverviewService {

  private final ModelOverviewRepository repository;
  private final ModelOverviewMapper modelOverviewMapper;

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
    if (
      effectiveFilter == ItemFilterTypeQueryDto.INCLUDE &&
      items.isEmpty() &&
      !hasActiveFilters(query)
    ) {
      return buildEmptyResponse(pageable);
    }

    // Use custom repository method when filters are present, otherwise use repository methods
    Page<ModelOverviewDocument> page = hasActiveFilters(query)
      ? repository.findWithFilters(
          items,
          search,
          effectiveFilter,
          query.getAvailableData(),
          query.getCenter(),
          query.getModelType(),
          query.getModifiedGenes(),
          pageable
        )
      : executeUnfilteredQuery(items, search, effectiveFilter, pageable);

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
    return (
      (query.getAvailableData() != null && !query.getAvailableData().isEmpty()) ||
      (query.getCenter() != null && !query.getCenter().isEmpty()) ||
      (query.getModelType() != null && !query.getModelType().isEmpty()) ||
      (query.getModifiedGenes() != null && !query.getModifiedGenes().isEmpty())
    );
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

    return ModelOverviewsPageDto.builder().modelOverviews(List.of()).page(pageMetadata).build();
  }

  private Page<ModelOverviewDocument> executeUnfilteredQuery(
    List<String> items,
    String search,
    ItemFilterTypeQueryDto filterType,
    Pageable pageable
  ) {
    if (
      filterType == ItemFilterTypeQueryDto.EXCLUDE && search != null && !search.trim().isEmpty()
    ) {
      return fetchPageWithSearch(search.trim(), items, pageable);
    } else {
      if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
        return items.isEmpty() ? Page.empty(pageable) : repository.findByNameIn(items, pageable);
      } else {
        return items.isEmpty()
          ? repository.findAll(pageable)
          : repository.findByNameNotIn(items, pageable);
      }
    }
  }

  private Page<ModelOverviewDocument> fetchPageWithSearch(
    String search,
    List<String> excludedItems,
    Pageable pageable
  ) {
    if (search.contains(",")) {
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(search);
      return repository.findByNameInIgnoreCaseAndNameNotIn(patterns, excludedItems, pageable);
    } else {
      String quotedSearch = Pattern.quote(search);
      return repository.findByNameContainingIgnoreCaseAndNameNotIn(
        quotedSearch,
        excludedItems,
        pageable
      );
    }
  }

}
