package org.sagebionetworks.agora.gene.api.service;

import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
// import org.sagebionetworks.agora.gene.api.model.dto.GeneDto;
// import org.sagebionetworks.agora.gene.api.model.dto.GenesPageDto;
// import org.sagebionetworks.agora.gene.api.model.mapper.GeneMapper;
// import org.sagebionetworks.agora.gene.api.model.repository.GeneRepository;
import org.springframework.stereotype.Service;

@Service
public class GenesService {

  // private GeneMapper geneMapper = new GeneMapper();

  // private final GeneRepository geneRepository;

  public GenesService() {}

  public GCTGenesListDto getComparisonGenes(String category, String subCategory) {
    // List<GeneDocument> geneDocuments = geneRepository.findAll();
    // List<GeneDto> genes = geneMapper.convertToDtoList(geneDocuments);

    return GCTGenesListDto.builder()
      // .genes(genes)
      // .number(0)
      // .size(genes.size())
      // .totalElements(0L)
      // .totalPages(1)
      // .hasNext(false)
      // .hasPrevious(false)
      .build();
  }
}
