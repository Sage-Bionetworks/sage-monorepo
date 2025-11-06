package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.DiseaseCorrelationRepository;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = "diseaseCorrelation")
public class DiseaseCorrelationService {

  private final DiseaseCorrelationRepository repository;
  private final DiseaseCorrelationMapper diseaseCorrelationMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)"
      + ".buildCacheKey('diseaseCorrelation', #filterType, #items, #cluster)"
  )
  public List<DiseaseCorrelationDto> loadDiseaseCorrelations(
    String cluster,
    List<String> items,
    ItemFilterTypeQueryDto filterType
  ) {
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      filterType,
      ItemFilterTypeQueryDto.INCLUDE
    );

    List<DiseaseCorrelationDocument> documents;

    if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
      if (items.isEmpty()) {
        return List.of();
      }
      List<ObjectId> objectIds = ApiHelper.parseObjectIds(items);
      documents = repository.findByClusterAndIdIn(cluster, objectIds);
    } else {
      if (items.isEmpty()) {
        documents = repository.findByCluster(cluster);
      } else {
        List<ObjectId> objectIds = ApiHelper.parseObjectIds(items);
        documents = repository.findByClusterAndIdNotIn(cluster, objectIds);
      }
    }

    return documents
      .stream()
      .map(diseaseCorrelationMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
  }
}
