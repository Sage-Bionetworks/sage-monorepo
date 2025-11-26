package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.DiseaseCorrelationRepository;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.DISEASE_CORRELATION)
public class DiseaseCorrelationService {

  private final DiseaseCorrelationRepository repository;
  private final DiseaseCorrelationMapper diseaseCorrelationMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)" +
    ".buildCacheKey('diseaseCorrelation', #query.itemFilterType, #query.items, " +
    "#cluster, #query.pageNumber, #query.pageSize)"
  )
  public DiseaseCorrelationsPageDto loadDiseaseCorrelations(
    DiseaseCorrelationSearchQueryDto query,
    String cluster
  ) {
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );

    List<String> items = ApiHelper.sanitizeItems(query.getItems());
    PageRequest pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());
    Page<DiseaseCorrelationDocument> page;

    if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
      if (items.isEmpty()) {
        page = Page.empty(pageable);
      } else {
        List<ObjectId> objectIds = ApiHelper.parseObjectIds(items);
        page = repository.findByClusterAndIdIn(cluster, objectIds, pageable);
      }
    } else {
      if (items.isEmpty()) {
        page = repository.findByCluster(cluster, pageable);
      } else {
        List<ObjectId> objectIds = ApiHelper.parseObjectIds(items);
        page = repository.findByClusterAndIdNotIn(cluster, objectIds, pageable);
      }
    }

    List<DiseaseCorrelationDto> diseaseCorrelations = page
      .getContent()
      .stream()
      .map(diseaseCorrelationMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return DiseaseCorrelationsPageDto.builder()
      .diseaseCorrelations(diseaseCorrelations)
      .page(pageMetadata)
      .build();
  }
}
