package org.sagebionetworks.model.ad.api.next.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.exception.ModelNotFoundException;
import org.sagebionetworks.model.ad.api.next.model.document.ModelDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.MODEL)
public class ModelService {

  private final ModelRepository repository;
  private final ModelMapper modelMapper;

  @Cacheable(key = "#name")
  public ModelDto getModelByName(String name) {
    ModelDocument document = repository.findByName(name)
      .orElseThrow(() -> new ModelNotFoundException(name));
    return modelMapper.toDto(document);
  }
}
