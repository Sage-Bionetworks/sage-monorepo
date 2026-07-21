package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.MouseModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.MouseModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.MouseModelOverviewRepository;
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
@CacheConfig(cacheNames = CacheNames.MOUSE_MODEL_OVERVIEW)
public class MouseModelOverviewService {

  private final MouseModelOverviewRepository repository;
  private final MouseModelOverviewMapper mouseModelOverviewMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.explorers.ApiHelper)" +
    ".buildCacheKey('mouseModelOverview', #query.itemFilterType, " +
    "#query.items, #query.search, #query.availableData, #query.center, " +
    "#query.modelType, #query.modifiedGenes, #query.pageNumber, #query.pageSize, " +
    "#query.sortFields, #query.sortOrders)"
  )
  public MouseModelOverviewsPageDto loadMouseModelOverviews(
    MouseModelOverviewSearchQueryDto query
  ) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(MouseModelOverviewSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);

    // Use custom repository for all queries
    Page<MouseModelOverviewDocument> page = repository.findAll(pageable, query, items);

    List<MouseModelOverviewDto> dtos = page
      .getContent()
      .stream()
      .map(mouseModelOverviewMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return MouseModelOverviewsPageDto.builder()
      .mouseModelOverviews(dtos)
      .page(pageMetadata)
      .build();
  }
}
