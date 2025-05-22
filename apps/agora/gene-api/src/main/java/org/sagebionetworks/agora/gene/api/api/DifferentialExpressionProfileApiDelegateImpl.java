package org.sagebionetworks.agora.gene.api.api;

import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfilesRnaPageDto;
import org.sagebionetworks.agora.gene.api.service.DifferentialExpressionProfileRnaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DifferentialExpressionProfileApiDelegateImpl
  implements DifferentialExpressionProfileApiDelegate {

  private final DifferentialExpressionProfileRnaService differentialExpressionProfileRnaService;

  public DifferentialExpressionProfileApiDelegateImpl(
    DifferentialExpressionProfileRnaService differentialExpressionProfileRnaService
  ) {
    this.differentialExpressionProfileRnaService = differentialExpressionProfileRnaService;
  }

  @Override
  public ResponseEntity<
    DifferentialExpressionProfilesRnaPageDto
  > listDifferentialExpressionProfilesRna(
    DifferentialExpressionProfileRnaSearchQueryDto differentialExpressionProfileRnaSearchQuery
  ) {
    return ResponseEntity.ok(
      differentialExpressionProfileRnaService.listDifferentialExpressionProfilesRna(
        differentialExpressionProfileRnaSearchQuery
      )
    );
  }
}
