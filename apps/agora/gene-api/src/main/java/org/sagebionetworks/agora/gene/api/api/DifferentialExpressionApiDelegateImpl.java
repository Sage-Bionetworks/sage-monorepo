package org.sagebionetworks.agora.gene.api.api;

import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfilePageDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileSearchQueryDto;
import org.sagebionetworks.agora.gene.api.service.DifferentialExpressionProfileRnaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DifferentialExpressionApiDelegateImpl implements DifferentialExpressionApiDelegate {

  private final DifferentialExpressionProfileRnaService rnaDifferentialExpressionProfileService;

  public DifferentialExpressionApiDelegateImpl(
    DifferentialExpressionProfileRnaService rnaDifferentialExpressionProfileService
  ) {
    this.rnaDifferentialExpressionProfileService = rnaDifferentialExpressionProfileService;
  }

  @Override
  public ResponseEntity<
    RnaDifferentialExpressionProfilePageDto
  > listRnaDifferentialExpressionProfiles(RnaDifferentialExpressionProfileSearchQueryDto query) {
    return ResponseEntity.ok(
      rnaDifferentialExpressionProfileService.listRnaDifferentialExpressionProfiles(query)
    );
  }
}
