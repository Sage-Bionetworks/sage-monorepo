package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.MarmosetModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.MarmosetModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.MarmosetModelOverviewRepository;
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
@CacheConfig(cacheNames = CacheNames.MARMOSET_MODEL_OVERVIEW)
public class MarmosetModelOverviewService {

  private final MarmosetModelOverviewRepository repository;
  private final MarmosetModelOverviewMapper marmosetModelOverviewMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.explorers.ApiHelper)" +
    ".buildCacheKey('marmosetModelOverview', #query.itemFilterType, " +
    "#query.items, #query.search, #query.availableData, #query.modelTypes, " +
    "#query.modifiedGenes, #query.pageNumber, #query.pageSize, " +
    "#query.sortFields, #query.sortOrders)"
  )
  public MarmosetModelOverviewsPageDto loadMarmosetModelOverviews(
    MarmosetModelOverviewSearchQueryDto query
  ) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(MarmosetModelOverviewSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);

    // Use custom repository for all queries
    Page<MarmosetModelOverviewDocument> page = repository.findAll(pageable, query, items);

    List<MarmosetModelOverviewDto> dtos = page
      .getContent()
      .stream()
      .map(marmosetModelOverviewMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return MarmosetModelOverviewsPageDto.builder()
      .marmosetModelOverviews(dtos)
      .page(pageMetadata)
      .build();
  }
}
