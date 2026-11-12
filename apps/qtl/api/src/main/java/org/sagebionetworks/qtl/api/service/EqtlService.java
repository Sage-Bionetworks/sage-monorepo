package org.sagebionetworks.qtl.api.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.qtl.api.configuration.CacheNames;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.sagebionetworks.qtl.api.model.dto.EqtlDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlsPageDto;
import org.sagebionetworks.qtl.api.model.dto.PageMetadataDto;
import org.sagebionetworks.qtl.api.model.mapper.EqtlMapper;
import org.sagebionetworks.qtl.api.model.repository.EqtlRepository;
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
@CacheConfig(cacheNames = CacheNames.EQTL)
public class EqtlService {

  static final int DEFAULT_PAGE_NUMBER = 0;
  static final int DEFAULT_PAGE_SIZE = 100;

  private final EqtlRepository repository;
  private final EqtlMapper eqtlMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.explorers.ApiHelper)" +
    ".buildCacheKey('eqtl', #query.itemFilterType, #query.items, " +
    "#query.search, #query.variant, #query.gene, #query.cellClass, #query.cellSubclass, " +
    "#query.pageNumber, #query.pageSize, #query.remainingBudget, " +
    "#query.sortFields, #query.sortOrders)"
  )
  public EqtlsPageDto loadEqtls(EqtlSearchQueryDto query) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());

    int pageNumber = Objects.requireNonNullElse(query.getPageNumber(), DEFAULT_PAGE_NUMBER);
    int pageSize = Objects.requireNonNullElse(query.getPageSize(), DEFAULT_PAGE_SIZE);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(EqtlSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    Page<EqtlDocument> page = repository.findAll(pageable, query, items);

    List<EqtlDto> eqtls = page.getContent().stream().map(eqtlMapper::toDto).toList();

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return EqtlsPageDto.builder().eqtls(eqtls).page(pageMetadata).build();
  }
}
