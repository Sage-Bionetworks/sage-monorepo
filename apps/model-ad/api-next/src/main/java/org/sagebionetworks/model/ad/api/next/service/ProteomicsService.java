package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ProteomicsMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ProteomicsRepository;
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
@CacheConfig(cacheNames = CacheNames.PROTEOMICS)
public class ProteomicsService {

  private final ProteomicsRepository repository;
  private final ProteomicsMapper proteomicsMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.explorers.ApiHelper)" +
    ".buildCacheKey('proteomics', #query.itemFilterType, #query.items, " +
    "#query.search, #query.biodomains, #query.modelType, #query.name, #query.sex, " +
    "#tissue, #query.pageNumber, #query.pageSize, " +
    "#query.sortFields, #query.sortOrders)"
  )
  public ProteomicsPageDto loadProteomics(ProteomicsSearchQueryDto query, String tissue) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(ProteomicsSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);

    // Use custom repository for all queries
    Page<ProteomicsDocument> page = repository.findAll(pageable, query, items, tissue);

    List<ProteomicsDto> proteomics = page
      .getContent()
      .stream()
      .map(proteomicsMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return ProteomicsPageDto.builder().proteomics(proteomics).page(pageMetadata).build();
  }
}
