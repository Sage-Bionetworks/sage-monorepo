package org.sagebionetworks.model.ad.api.next.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.service.ModelService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModelApiDelegateImpl implements ModelApiDelegate {

  private final ModelService modelService;

  @Override
  public ResponseEntity<ModelDto> getModelByName(String name) {
    log.debug("Fetching model by name: {}", name);
    ModelDto model = modelService.getModelByName(name);

    if (model == null) {
      log.debug("Model not found: {}", name);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    log.debug("Successfully retrieved model: {}", name);
    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(model);
  }
}
