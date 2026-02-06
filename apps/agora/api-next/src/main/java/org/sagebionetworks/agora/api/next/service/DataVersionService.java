package org.sagebionetworks.agora.api.next.service;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.agora.api.next.configuration.CacheNames;
import org.sagebionetworks.agora.api.next.model.document.DataVersionDocument;
import org.sagebionetworks.agora.api.next.model.dto.DataVersionDto;
import org.sagebionetworks.agora.api.next.model.mapper.DataVersionMapper;
import org.sagebionetworks.agora.api.next.model.repository.DataVersionRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = CacheNames.DATA_VERSION)
public class DataVersionService {

  private final DataVersionRepository repository;
  private final DataVersionMapper dataVersionMapper;

  @Cacheable(key = "'dataVersion'")
  public DataVersionDto loadDataVersion() {
    DataVersionDocument document = repository
      .findFirstBy()
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data version not found")
      );

    return dataVersionMapper.toDto(document);
  }
}
