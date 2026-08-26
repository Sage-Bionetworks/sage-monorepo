package org.sagebionetworks.model.ad.api.next.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.service.ProteomicsService;
import org.sagebionetworks.model.ad.api.next.util.DifferentialExpressionCategoryParser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProteomicsApiDelegateImpl implements ProteomicsApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "categories",
    "items",
    "itemFilterType",
    "search",
    "biodomains",
    "modelType",
    "name",
    "sex",
    "sortFields",
    "sortOrders"
  );

  private static final String MODALITY_TOKEN = "PROTEIN";

  private final ProteomicsService proteomicsService;

  @Override
  public ResponseEntity<ProteomicsPageDto> getProteomics(ProteomicsSearchQueryDto query) {
    log.debug("Fetching proteomics data with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    String tissue = DifferentialExpressionCategoryParser.extractTissue(
      query.getCategories(),
      MODALITY_TOKEN
    );

    ProteomicsPageDto results = proteomicsService.loadProteomics(query, tissue);

    log.debug("Successfully retrieved {} proteomics data", results.getProteomics().size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }
}
