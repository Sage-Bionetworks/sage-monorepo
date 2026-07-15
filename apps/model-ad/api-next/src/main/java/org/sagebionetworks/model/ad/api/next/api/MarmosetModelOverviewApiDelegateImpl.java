package org.sagebionetworks.model.ad.api.next.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.service.MarmosetModelOverviewService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarmosetModelOverviewApiDelegateImpl implements MarmosetModelOverviewApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "items",
    "itemFilterType",
    "search",
    "availableData",
    "modelTypes",
    "modifiedGenes",
    "sortFields",
    "sortOrders"
  );

  private final MarmosetModelOverviewService marmosetModelOverviewService;

  @Override
  public ResponseEntity<MarmosetModelOverviewsPageDto> getMarmosetModelOverviews(
    MarmosetModelOverviewSearchQueryDto query
  ) {
    log.debug("Fetching marmoset model overviews with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    MarmosetModelOverviewsPageDto page = marmosetModelOverviewService.loadMarmosetModelOverviews(
      query
    );

    log.debug(
      "Successfully retrieved {} marmoset model overviews",
      page.getMarmosetModelOverviews().size()
    );

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }
}
