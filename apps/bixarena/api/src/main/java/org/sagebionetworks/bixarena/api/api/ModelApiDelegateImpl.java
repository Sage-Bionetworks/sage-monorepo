package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.model.dto.ModelPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelSearchQueryDto;
import org.sagebionetworks.bixarena.api.service.ModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelApiDelegateImpl implements ModelApiDelegate {

  private final ModelService modelService;

  @Override
  public ResponseEntity<ModelPageDto> listModels(ModelSearchQueryDto modelSearchQuery) {
    return ResponseEntity.ok(modelService.listModels(modelSearchQuery));
  }
}
