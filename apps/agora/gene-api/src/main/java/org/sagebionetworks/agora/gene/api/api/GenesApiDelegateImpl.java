package org.sagebionetworks.agora.gene.api.api;

import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
import org.sagebionetworks.agora.gene.api.service.GenesService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GenesApiDelegateImpl implements GenesApiDelegate {

  private final GenesService genesService;

  public GenesApiDelegateImpl(GenesService genesService) {
    this.genesService = genesService;
  }

  @Override
  public ResponseEntity<GCTGenesListDto> getComparisonGenes(String category, String subCategory) {
    return ResponseEntity.ok(genesService.getComparisonGenes(category, subCategory));
  }
}
