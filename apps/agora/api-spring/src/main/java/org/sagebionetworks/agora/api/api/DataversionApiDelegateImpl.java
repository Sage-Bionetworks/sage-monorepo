package org.sagebionetworks.agora.api.api;

import org.sagebionetworks.agora.api.model.dto.DataversionDto;
import org.sagebionetworks.agora.api.service.DataVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DataversionApiDelegateImpl implements DataversionApiDelegate {

  private final DataVersionService dataVersionService;

  public DataversionApiDelegateImpl(DataVersionService dataVersionService) {
    this.dataVersionService = dataVersionService;
  }

  @Override
  public ResponseEntity<DataversionDto> getDataversion() {
    return ResponseEntity.ok(dataVersionService.getDataVersion());
  }
}
