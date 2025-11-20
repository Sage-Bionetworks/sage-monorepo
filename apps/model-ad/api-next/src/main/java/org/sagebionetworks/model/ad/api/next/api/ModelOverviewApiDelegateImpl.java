package org.sagebionetworks.model.ad.api.next.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.service.ModelOverviewService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelOverviewApiDelegateImpl implements ModelOverviewApiDelegate {

  private final ModelOverviewService modelOverviewService;

  @Override
  public ResponseEntity<ModelOverviewsPageDto> getModelOverviews(
    ModelOverviewSearchQueryDto modelOverviewSearchQuery
  ) {
    ModelOverviewsPageDto page = modelOverviewService.loadModelOverviews(modelOverviewSearchQuery);

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }
}
