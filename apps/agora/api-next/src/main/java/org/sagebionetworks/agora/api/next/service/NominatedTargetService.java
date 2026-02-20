package org.sagebionetworks.agora.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.configuration.CacheNames;
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetsPageDto;
import org.sagebionetworks.agora.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.agora.api.next.model.mapper.NominatedTargetMapper;
import org.sagebionetworks.agora.api.next.model.repository.NominatedTargetRepository;
import org.sagebionetworks.agora.api.next.util.ApiHelper;
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
@CacheConfig(cacheNames = CacheNames.NOMINATED_TARGET)
public class NominatedTargetService {

  private final NominatedTargetRepository repository;
  private final NominatedTargetMapper nominatedTargetMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.agora.api.next.util.ApiHelper)" +
    ".buildCacheKey('nominatedTarget', #query.itemFilterType, " +
    "#query.items, #query.search, #query.cohortStudies, #query.inputData, " +
    "#query.initialNomination, #query.nominatingTeams, #query.pharosClass, " +
    "#query.programs, #query.totalNominations, #query.pageNumber, " +
    "#query.pageSize, #query.sortFields, #query.sortOrders)"
  )
  public NominatedTargetsPageDto loadNominatedTargets(NominatedTargetSearchQueryDto query) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(NominatedTargetSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);

    // Use custom repository for all queries
    Page<NominatedTargetDocument> page = repository.findAll(pageable, query, items);

    List<NominatedTargetDto> dtos = page
      .getContent()
      .stream()
      .map(nominatedTargetMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return NominatedTargetsPageDto.builder().nominatedTargets(dtos).page(pageMetadata).build();
  }
}
