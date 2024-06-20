package org.sagebionetworks.modelad.api.service;

import java.util.List;
import org.sagebionetworks.modelad.api.model.document.GeneDocument;
import org.sagebionetworks.modelad.api.model.dto.GeneDto;
import org.sagebionetworks.modelad.api.model.dto.GenesPageDto;
import org.sagebionetworks.modelad.api.model.mapper.GeneMapper;
import org.sagebionetworks.modelad.api.model.repository.GeneRepository;
import org.springframework.stereotype.Service;

@Service
public class GeneService {

  private GeneMapper geneMapper = new GeneMapper();

  private final GeneRepository geneRepository;

  public GeneService(GeneRepository geneRepository) {
    this.geneRepository = geneRepository;
  }

  public GenesPageDto listGenes() {

    List<GeneDocument> geneDocuments = geneRepository.findAll();
    List<GeneDto> genes = geneMapper.convertToDtoList(geneDocuments);

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
