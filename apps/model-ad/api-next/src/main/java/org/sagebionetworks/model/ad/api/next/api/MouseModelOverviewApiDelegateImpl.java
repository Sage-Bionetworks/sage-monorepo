package org.sagebionetworks.model.ad.api.next.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.service.MouseModelOverviewService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MouseModelOverviewApiDelegateImpl implements MouseModelOverviewApiDelegate {

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

  private final MouseModelOverviewService mouseModelOverviewService;

  @Override
  public ResponseEntity<MouseModelOverviewsPageDto> getMouseModelOverviews(
    MouseModelOverviewSearchQueryDto query
  ) {
    log.debug("Fetching mouse model overviews with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    MouseModelOverviewsPageDto page = mouseModelOverviewService.loadMouseModelOverviews(query);

    log.debug(
      "Successfully retrieved {} mouse model overviews",
      page.getMouseModelOverviews().size()
    );

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }
}
