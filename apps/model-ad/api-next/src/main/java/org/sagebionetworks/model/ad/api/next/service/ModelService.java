package org.sagebionetworks.model.ad.api.next.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.exception.ModelNotFoundException;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.MarmosetModelMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.MouseModelMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.MarmosetModelRepository;
import org.sagebionetworks.model.ad.api.next.model.repository.MouseModelRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.MODEL)
public class ModelService {

  private final MouseModelRepository mouseModelRepository;
  private final MouseModelMapper mouseModelMapper;
  private final MarmosetModelRepository marmosetModelRepository;
  private final MarmosetModelMapper marmosetModelMapper;

  @Cacheable(key = "#modelOrganism.toString() + '-' + #name")
  public ModelDto getModelByName(ModelOrganismDto modelOrganism, String name) {
    return switch (modelOrganism) {
      case MOUSE -> mouseModelMapper.toDto(
        mouseModelRepository
          .findByName(name)
          .orElseThrow(() -> new ModelNotFoundException(modelOrganism, name))
      );
      case MARMOSET -> marmosetModelMapper.toDto(
        marmosetModelRepository
          .findByName(name)
          .orElseThrow(() -> new ModelNotFoundException(modelOrganism, name))
      );
    };
  }
}
