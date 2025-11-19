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
    ".buildCacheKey('modelOverview', #filterType, #items, #pageNumber, #pageSize)"
  )
  public ModelOverviewsPageDto loadModelOverviews(
    Integer pageNumber,
    Integer pageSize,
    List<String> items,
    ItemFilterTypeQueryDto filterType
  ) {
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      filterType,
      ItemFilterTypeQueryDto.INCLUDE
    );

    int effectivePageNumber = Objects.requireNonNullElse(pageNumber, 0);
    int effectivePageSize = Objects.requireNonNullElse(pageSize, 100);

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

    PageMetadataDto pageMetadata = new PageMetadataDto(
      page.getNumber(),
      page.getSize(),
      page.getTotalElements(),
      page.getTotalPages(),
      page.hasNext(),
      page.hasPrevious()
    );

    return new ModelOverviewsPageDto().modelOverviews(dtos).page(pageMetadata);
  }
}
