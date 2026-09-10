package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SearchResultDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.SearchResultMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelSearchRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = CacheNames.MODEL_SEARCH)
public class ModelSearchService {

  private final ModelSearchRepository modelSearchRepository;
  private final SearchResultMapper searchResultMapper;

  @Cacheable(key = "#query + '-' + #modelOrganisms")
  public List<SearchResultDto> searchModels(String query, List<ModelOrganismDto> modelOrganisms) {
    return modelSearchRepository.searchModels(query, modelOrganisms).stream()
        .map(searchResultMapper::toDto)
        .toList();
  }
}
