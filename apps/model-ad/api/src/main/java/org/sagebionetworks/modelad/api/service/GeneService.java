package org.sagebionetworks.modelad.api.service;

import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.modelad.api.model.document.GeneDocument;
import org.sagebionetworks.modelad.api.model.dto.GeneDto;
import org.sagebionetworks.modelad.api.model.dto.GenesPageDto;
import org.sagebionetworks.modelad.api.model.repository.GeneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GeneService {

  private static final Logger logger = LoggerFactory.getLogger(GeneService.class);

  private final GeneRepository geneRepository;

  public GeneService(GeneRepository geneRepository) {
    this.geneRepository = geneRepository;
  }

  public GenesPageDto listGenes() {

    List<GeneDocument> geneDocuments = geneRepository.findAll();
    logger.info("Gene documents: {}", geneDocuments);

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
