package org.sagebionetworks.agora.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.configuration.CacheNames;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugsPageDto;
import org.sagebionetworks.agora.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.agora.api.next.model.mapper.NominatedDrugMapper;
import org.sagebionetworks.agora.api.next.model.repository.NominatedDrugRepository;
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
@CacheConfig(cacheNames = CacheNames.NOMINATED_DRUG)
public class NominatedDrugService {

  private final NominatedDrugRepository repository;
  private final NominatedDrugMapper nominatedDrugMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.agora.api.next.util.ApiHelper)" +
    ".buildCacheKey('nominatedDrug', #query.itemFilterType, " +
    "#query.items, #query.search, #query.principalInvestigators, #query.programs, " +
    "#query.totalNominations, #query.yearFirstNominated, #query.pageNumber, " +
    "#query.pageSize, #query.sortFields, #query.sortOrders)"
  )
  public NominatedDrugsPageDto loadNominatedDrugs(NominatedDrugSearchQueryDto query) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(NominatedDrugSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);

    // Use custom repository for all queries
    Page<NominatedDrugDocument> page = repository.findAll(pageable, query, items);

    List<NominatedDrugDto> dtos = page
      .getContent()
      .stream()
      .map(nominatedDrugMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return NominatedDrugsPageDto.builder().nominatedDrugs(dtos).page(pageMetadata).build();
  }
}
