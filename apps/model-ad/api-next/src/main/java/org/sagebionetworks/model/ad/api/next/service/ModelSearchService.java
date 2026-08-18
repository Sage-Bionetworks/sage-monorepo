package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SearchResultDto;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelSearchRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.MODEL_SEARCH)
public class ModelSearchService {

  private static final int MIN_QUERY_LENGTH = 1;
  private static final int MAX_QUERY_LENGTH = 100;

  private final ModelSearchRepository modelSearchRepository;

  @Cacheable(key = "#query + '-' + #modelOrganisms")
  public List<SearchResultDto> searchModels(String query, List<ModelOrganismDto> modelOrganisms) {
    if (query.length() < MIN_QUERY_LENGTH || query.length() > MAX_QUERY_LENGTH) {
      throw new IllegalArgumentException(
          "Query must be between " + MIN_QUERY_LENGTH + " and " + MAX_QUERY_LENGTH
              + " characters");
    }

    List<ModelOrganismDto> organisms = (modelOrganisms == null || modelOrganisms.isEmpty())
        ? List.of(ModelOrganismDto.MOUSE, ModelOrganismDto.MARMOSET)
        : modelOrganisms;

    log.debug("Searching models with query '{}' for organisms {}", query, organisms);
    return modelSearchRepository.searchModels(query, organisms);
  }
}
