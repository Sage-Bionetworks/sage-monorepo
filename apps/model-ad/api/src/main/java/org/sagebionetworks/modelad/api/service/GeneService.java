package org.sagebionetworks.modelad.api.service;

import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.modelad.api.model.dto.GeneDto;
import org.sagebionetworks.modelad.api.model.dto.GenesPageDto;
import org.springframework.stereotype.Service;

@Service
public class GeneService {

  public GeneService() {}

  public GenesPageDto listGenes() {
    final GeneDto gene = GeneDto.builder().id(1L).name("gene").description("a gene").build();
    List<GeneDto> genes = Arrays.asList(gene, gene);

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
