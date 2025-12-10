package org.sagebionetworks.model.ad.api.next.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.DataVersionDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DataVersionDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DataVersionMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.DataVersionRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.DATA_VERSION)
public class DataVersionService {

  private final DataVersionRepository repository;
  private final DataVersionMapper dataVersionMapper;

  @Cacheable(key = "'dataVersion'")
  public DataVersionDto loadDataVersion() {
    log.debug("Fetching data version from database");

    DataVersionDocument document = repository.findFirstBy()
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data version not found"));

    return dataVersionMapper.toDto(document);
  }
}
