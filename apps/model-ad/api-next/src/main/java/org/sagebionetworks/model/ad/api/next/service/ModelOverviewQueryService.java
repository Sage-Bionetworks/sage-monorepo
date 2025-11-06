package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelOverviewRepository;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "modelOverview")
public class ModelOverviewQueryService {

  private final ModelOverviewRepository repository;
  private final ModelOverviewMapper modelOverviewMapper;

  public ModelOverviewQueryService(
    ModelOverviewRepository repository,
    ModelOverviewMapper modelOverviewMapper
  ) {
    this.repository = repository;
    this.modelOverviewMapper = modelOverviewMapper;
  }

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)" +
    ".buildCacheKey('modelOverview', #filterType, #items)"
  )
  public List<ModelOverviewDto> loadModelOverviews(
    List<String> items,
    ItemFilterTypeQueryDto filterType
  ) {
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      filterType,
      ItemFilterTypeQueryDto.INCLUDE
    );

    List<ModelOverviewDocument> documents;

    if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
      if (items.isEmpty()) {
        return List.of();
      }
      List<ObjectId> objectIds = ApiHelper.parseObjectIds(items);
      documents = repository.findByIdIn(objectIds);
    } else {
      if (items.isEmpty()) {
        documents = repository.findAll();
      } else {
        List<ObjectId> objectIds = ApiHelper.parseObjectIds(items);
        documents = repository.findByIdNotIn(objectIds);
      }
    }

    return documents
      .stream()
      .map(modelOverviewMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
  }
}
