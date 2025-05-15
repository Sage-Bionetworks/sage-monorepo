package org.sagebionetworks.agora.gene.api.api;

import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
import org.sagebionetworks.agora.gene.api.service.GeneService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GenesApiDelegateImpl implements GenesApiDelegate {

  private final GeneService geneService;

  public GenesApiDelegateImpl(GeneService geneService) {
    this.geneService = geneService;
  }

  @Override
  public ResponseEntity<GCTGenesListDto> getComparisonGenes(String category, String subCategory) {
    return ResponseEntity.ok(geneService.getComparisonGenes(category, subCategory));
  }
}
