package org.sagebionetworks.agora.gene.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionDocument;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
import org.sagebionetworks.agora.gene.api.model.mapper.RnaDifferentialExpressionMapper;
import org.sagebionetworks.agora.gene.api.model.repository.RnaDifferentialExpressionRepository;
// import org.sagebionetworks.agora.gene.api.model.dto.GeneDto;
// import org.sagebionetworks.agora.gene.api.model.dto.GenesPageDto;
// import org.sagebionetworks.agora.gene.api.model.mapper.GeneMapper;
// import org.sagebionetworks.agora.gene.api.model.repository.GeneRepository;
import org.springframework.stereotype.Service;

@Service
public class GenesService {

  private RnaDifferentialExpressionMapper rnaDifferentialExpressionMapper =
    new RnaDifferentialExpressionMapper();

  private final RnaDifferentialExpressionRepository rnaDifferentialExpressionRepository;

  public GenesService(RnaDifferentialExpressionRepository rnaDifferentialExpressionRepository) {
    this.rnaDifferentialExpressionRepository = rnaDifferentialExpressionRepository;
  }

  public GCTGenesListDto getComparisonGenes(String category, String subCategory) {
    // Use the default values from the web app during development.
    // TODO: Update the API description to set the default values for category and subCategory.
    category = "RNA - Differential Expression";
    subCategory = "AD Diagnosis (males and females)";

    // List<GeneDocument> geneDocuments = geneRepository.findAll();
    // List<GeneDto> genes = geneMapper.convertToDtoList(geneDocuments);

    GCTGenesListDto gctGenesListDto = null;

    if (category.equals("RNA - Differential Expression")) {
      gctGenesListDto = getRnaComparisonGenes(subCategory);
    } else if (category.equals("Protein - Differential Expression")) {
      gctGenesListDto = getProteinComparisonGenes(subCategory);
    } else {
      // TODO: better handle unexpected value
      gctGenesListDto = GCTGenesListDto.builder().build();
    }

    return gctGenesListDto;
  }

  private GCTGenesListDto getRnaComparisonGenes(String subCategory) {
    List<RnaDifferentialExpressionDocument> differentialExpression =
      rnaDifferentialExpressionRepository.findByModelSorted(subCategory);
    // if (differentialExpression != null && !differentialExpression.isEmpty()) {
    //   Map<String, GCTGeneDto> genes = new HashMap<>();
    //   Map<String, Gene> allGenes = getGenesMap();
    // }

    // List<GCTGeneDto> dtos = rnaDifferentialExpressionMapper.convertToDtoList(documents);

    // return GCTGenesListDto.builder().items(dtos).build();
    return GCTGenesListDto.builder().build();
  }

  private GCTGenesListDto getProteinComparisonGenes(String subCategory) {
    return GCTGenesListDto.builder().build();
  }
}
