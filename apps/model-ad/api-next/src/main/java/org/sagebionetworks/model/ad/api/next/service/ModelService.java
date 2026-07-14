package org.sagebionetworks.model.ad.api.next.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.exception.ModelNotFoundException;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.OrganismDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.MarmoModelMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.MouseModelMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.MarmoModelRepository;
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
  private final MarmoModelRepository marmoModelRepository;
  private final MarmoModelMapper marmoModelMapper;

  @Cacheable(key = "#organism.toString() + '-' + #name")
  public ModelDto getModelByName(OrganismDto organism, String name) {
    return switch (organism) {
      case MOUSE -> mouseModelMapper.toDto(
        mouseModelRepository
          .findByName(name)
          .orElseThrow(() -> new ModelNotFoundException(organism, name))
      );
      case MARMOSET -> marmoModelMapper.toDto(
        marmoModelRepository
          .findByName(name)
          .orElseThrow(() -> new ModelNotFoundException(organism, name))
      );
    };
  }
}
