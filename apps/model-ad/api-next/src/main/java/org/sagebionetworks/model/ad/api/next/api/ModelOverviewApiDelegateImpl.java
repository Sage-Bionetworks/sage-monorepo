package org.sagebionetworks.model.ad.api.next.api;

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

  private final ModelOverviewService modelOverviewService;

  @Override
  public ResponseEntity<ModelOverviewsPageDto> getModelOverviews(
    ModelOverviewSearchQueryDto query
  ) {
    log.debug("Fetching model overviews with query: {}", query);

    ModelOverviewsPageDto page = modelOverviewService.loadModelOverviews(query);

    log.debug("Successfully retrieved {} model overviews", page.getModelOverviews().size());
    ApiHelper.logHttpResponse(200);

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }
}
