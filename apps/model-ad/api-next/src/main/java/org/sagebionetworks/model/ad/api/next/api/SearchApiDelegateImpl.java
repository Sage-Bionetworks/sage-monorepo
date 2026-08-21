package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SearchResultDto;
import org.sagebionetworks.model.ad.api.next.service.ModelSearchService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchApiDelegateImpl implements SearchApiDelegate {

  private final ModelSearchService modelSearchService;

  @Override
  public ResponseEntity<List<SearchResultDto>> searchModels(String q,
      List<ModelOrganismDto> modelOrganisms) {
    List<ModelOrganismDto> sortedOrganisms = modelOrganisms == null ? null
        : modelOrganisms.stream().sorted().collect(Collectors.toList());
    log.debug("Searching models with query: '{}', organisms: {}", q, sortedOrganisms);

    List<SearchResultDto> results = modelSearchService.searchModels(q, sortedOrganisms);

    log.debug("Search returned {} results", results.size());
    return ResponseEntity.ok()
        .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
        .body(results);
  }
}
