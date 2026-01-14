package org.sagebionetworks.model.ad.api.next.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.service.ModelOverviewService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModelOverviewApiDelegateImpl implements ModelOverviewApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "items",
    "itemFilterType",
    "search",
    "availableData",
    "center",
    "modelType",
    "modifiedGenes",
    "sortFields",
    "sortOrders"
  );

  private final ModelOverviewService modelOverviewService;

  @Override
  public ResponseEntity<ModelOverviewsPageDto> getModelOverviews(
    ModelOverviewSearchQueryDto query
  ) {
    log.debug("Fetching model overviews with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    ModelOverviewsPageDto page = modelOverviewService.loadModelOverviews(query);

    log.debug("Successfully retrieved {} model overviews", page.getModelOverviews().size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }
}
