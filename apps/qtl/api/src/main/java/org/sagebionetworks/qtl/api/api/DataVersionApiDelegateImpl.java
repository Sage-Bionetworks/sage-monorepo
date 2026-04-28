package org.sagebionetworks.qtl.api.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.qtl.api.model.dto.DataVersionDto;
import org.sagebionetworks.qtl.api.service.DataVersionService;
import org.sagebionetworks.qtl.api.util.ApiHelper;
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
