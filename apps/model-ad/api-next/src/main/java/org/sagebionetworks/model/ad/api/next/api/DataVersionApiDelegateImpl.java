package org.sagebionetworks.model.ad.api.next.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.model.dto.DataVersionDto;
import org.sagebionetworks.model.ad.api.next.service.DataVersionService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataVersionApiDelegateImpl implements DataVersionApiDelegate {

  private final DataVersionService dataVersionService;

  @Override
  public ResponseEntity<DataVersionDto> getDataVersion() {
    log.debug("Fetching data version");

    DataVersionDto dataVersion = dataVersionService.loadDataVersion();

    log.debug("Successfully retrieved data version: {}", dataVersion);

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(dataVersion);
  }
}
