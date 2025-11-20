package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
    "#query.items, #query.pageNumber, #query.pageSize)"
  )
  public ModelOverviewsPageDto loadModelOverviews(ModelOverviewSearchQueryDto query) {
    List<String> items = ApiHelper.sanitizeItems(query.getItems());
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize);
    Page<ModelOverviewDocument> page;

    if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
      if (items.isEmpty()) {
        // Return empty page for include filter with no items
        page = Page.empty(pageable);
      } else {
        List<ObjectId> objectIds = ApiHelper.parseObjectIds(items);
        page = repository.findByIdIn(objectIds, pageable);
      }
    } else {
      if (items.isEmpty()) {
        page = repository.findAll(pageable);
      } else {
        List<ObjectId> objectIds = ApiHelper.parseObjectIds(items);
        page = repository.findByIdNotIn(objectIds, pageable);
      }
    }

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
}
