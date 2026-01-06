package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ComparisonToolConfigMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ComparisonToolConfigRepository;
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
  public List<ComparisonToolConfigDto> getConfigsByPage(ComparisonToolPageDto page) {
    List<ComparisonToolConfigDocument> documents = repository.findByPage(page.getValue());
    return documents == null
      ? List.of()
      : documents.stream().map(comparisonToolConfigMapper::toDto).toList();
  }
}
