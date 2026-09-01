package org.sagebionetworks.model.ad.api.next.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.service.TranscriptomicsService;
import org.sagebionetworks.model.ad.api.next.util.DifferentialExpressionCategoryParser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptomicsApiDelegateImpl implements TranscriptomicsApiDelegate {

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

  private static final String MODALITY_TOKEN = "RNA";

  private final TranscriptomicsService transcriptomicsService;

  @Override
  public ResponseEntity<TranscriptomicsPageDto> getTranscriptomics(
    TranscriptomicsSearchQueryDto query
  ) {
    log.debug("Fetching transcriptomics data with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    String tissue = DifferentialExpressionCategoryParser.extractTissue(
      query.getCategories(),
      MODALITY_TOKEN
    );

    TranscriptomicsPageDto results = transcriptomicsService.loadTranscriptomics(query, tissue);

    log.debug(
      "Successfully retrieved {} transcriptomics data",
      results.getTranscriptomics().size()
    );

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }
}
