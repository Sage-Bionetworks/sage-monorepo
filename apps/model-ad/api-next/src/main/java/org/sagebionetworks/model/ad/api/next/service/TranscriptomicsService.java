package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.TranscriptomicsMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.TranscriptomicsRepository;
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
@CacheConfig(cacheNames = CacheNames.TRANSCRIPTOMICS)
public class TranscriptomicsService {

  private final TranscriptomicsRepository repository;
  private final TranscriptomicsMapper transcriptomicsMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)" +
    ".buildCacheKey('transcriptomics', #query.itemFilterType, #query.items, " +
    "#query.search, #query.biodomains, #query.modelType, #query.name, " +
    "#tissue, #sexCohort, #query.pageNumber, #query.pageSize, " +
    "#query.sortFields, #query.sortOrders)"
  )
  public TranscriptomicsPageDto loadTranscriptomics(
    TranscriptomicsSearchQueryDto query,
    String tissue,
    String sexCohort
  ) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(TranscriptomicsSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);

    // Use custom repository for all queries
    Page<TranscriptomicsDocument> page = repository.findAll(
      pageable,
      query,
      items,
      tissue,
      sexCohort
    );

    List<TranscriptomicsDto> transcriptomics = page
      .getContent()
      .stream()
      .map(transcriptomicsMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return TranscriptomicsPageDto.builder()
      .transcriptomics(transcriptomics)
      .page(pageMetadata)
      .build();
  }
}
