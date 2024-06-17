package org.sagebionetworks.modelad.api.service;

import java.util.Collections;
import java.util.List;
import org.sagebionetworks.modelad.api.model.dto.GeneDto;
import org.sagebionetworks.modelad.api.model.dto.GenesPageDto;
import org.springframework.stereotype.Service;

@Service
public class GeneService {

  public GeneService() {}

  public GenesPageDto listGenes() {
    List<GeneDto> genes = Collections.emptyList();

    return GenesPageDto.builder()
        .genes(genes)
        .number(0)
        .size(genes.size())
        .totalElements(0L)
        .totalPages(1)
        .hasNext(false)
        .hasPrevious(false)
        .build();
  }
}
