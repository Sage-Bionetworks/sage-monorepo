package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.GeneExpressionMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.GeneExpressionRepository;
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
@CacheConfig(cacheNames = CacheNames.GENE_EXPRESSION)
public class GeneExpressionService {

  private final GeneExpressionRepository repository;
  private final GeneExpressionMapper geneExpressionMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)" +
    ".buildCacheKey('geneExpression', #query.itemFilterType, #query.items, " +
    "#query.search, #query.biodomains, #query.modelType, #query.name, " +
    "#tissue, #sexCohort, #query.pageNumber, #query.pageSize, " +
    "#query.sortFields, #query.sortOrders)"
  )
  public GeneExpressionsPageDto loadGeneExpressions(
    GeneExpressionSearchQueryDto query,
    String tissue,
    String sexCohort
  ) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    List<Integer> sortOrders = query
      .getSortOrders()
      .stream()
      .map(GeneExpressionSearchQueryDto.SortOrdersEnum::getValue)
      .toList();
    Sort sort = ApiHelper.createSort(query.getSortFields(), sortOrders);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);

    // Use custom repository for all queries
    Page<GeneExpressionDocument> page = repository.findAll(
      pageable,
      query,
      items,
      tissue,
      sexCohort
    );

    List<GeneExpressionDto> geneExpressions = page
      .getContent()
      .stream()
      .map(geneExpressionMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return GeneExpressionsPageDto.builder()
      .geneExpressions(geneExpressions)
      .page(pageMetadata)
      .build();
  }
}
