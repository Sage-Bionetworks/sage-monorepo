package org.sagebionetworks.agora.api.next.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.configuration.CacheNames;
import org.sagebionetworks.agora.api.next.exception.ComparisonToolConfigNotFoundException;
import org.sagebionetworks.agora.api.next.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.agora.api.next.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.agora.api.next.model.dto.ComparisonToolConfigPageDto;
import org.sagebionetworks.agora.api.next.model.mapper.ComparisonToolConfigMapper;
import org.sagebionetworks.agora.api.next.model.repository.ComparisonToolConfigRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.COMPARISON_TOOL_CONFIG)
public class ComparisonToolConfigService {

  private final ComparisonToolConfigRepository repository;
  private final ComparisonToolConfigMapper comparisonToolConfigMapper;

  @Cacheable(key = "#page")
  public List<ComparisonToolConfigDto> getConfigsByPage(ComparisonToolConfigPageDto page) {
    List<ComparisonToolConfigDocument> documents = repository.findByPage(page.getValue());

    if (documents == null || documents.isEmpty()) {
      throw new ComparisonToolConfigNotFoundException(page.getValue());
    }

    return documents.stream().map(comparisonToolConfigMapper::toDto).toList();
  }
}
