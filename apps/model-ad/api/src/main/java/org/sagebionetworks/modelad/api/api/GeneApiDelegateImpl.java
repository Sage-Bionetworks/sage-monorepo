package org.sagebionetworks.modelad.api.api;

import org.sagebionetworks.modelad.api.model.dto.GenesPageDto;
import org.sagebionetworks.modelad.api.service.GeneService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GeneApiDelegateImpl implements GeneApiDelegate {

  private final GeneService geneService;

  public GeneApiDelegateImpl(GeneService geneService) {
    this.geneService = geneService;
  }

  @Override
  public ResponseEntity<GenesPageDto> listGenes() {
    return ResponseEntity.ok(geneService.listGenes());
  }
}
