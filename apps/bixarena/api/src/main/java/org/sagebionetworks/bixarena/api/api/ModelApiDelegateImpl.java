package org.sagebionetworks.bixarena.api.api;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.model.dto.ModelErrorCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelSearchQueryDto;
import org.sagebionetworks.bixarena.api.service.ModelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelApiDelegateImpl implements ModelApiDelegate {

  private final ModelService modelService;

  @Override
  public ResponseEntity<ModelPageDto> listModels(ModelSearchQueryDto modelSearchQuery) {
    return ResponseEntity.ok(modelService.listModels(modelSearchQuery));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ModelErrorDto> createModelError(
    String modelId,
    ModelErrorCreateRequestDto modelErrorCreateRequest
  ) {
    UUID modelUuid = UUID.fromString(modelId);
    ModelErrorDto createdError = modelService.createModelError(
      modelUuid,
      modelErrorCreateRequest
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(createdError);
  }
}
