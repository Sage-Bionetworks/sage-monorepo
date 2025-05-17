package org.sagebionetworks.agora.gene.api.api;

import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
import org.sagebionetworks.agora.gene.api.service.GCTGenesService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GenesApiDelegateImpl implements GenesApiDelegate {

  private final GCTGenesService gctGenesService;

  public GenesApiDelegateImpl(GCTGenesService gctGenesService) {
    this.gctGenesService = gctGenesService;
  }

  @Override
  public ResponseEntity<GCTGenesListDto> getComparisonGenes(String category, String subCategory) {
    return ResponseEntity.ok(gctGenesService.getComparisonGenes(category, subCategory));
  }
}
