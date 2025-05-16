package org.sagebionetworks.agora.gene.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sagebionetworks.agora.gene.api.GeneApiApplication;
import org.sagebionetworks.agora.gene.api.model.document.GeneDocument;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionDocument;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
import org.sagebionetworks.agora.gene.api.model.dto.GeneDto;
import org.sagebionetworks.agora.gene.api.model.mapper.GeneMapper;
import org.sagebionetworks.agora.gene.api.model.mapper.RnaDifferentialExpressionMapper;
import org.sagebionetworks.agora.gene.api.model.repository.GeneRepository;
import org.sagebionetworks.agora.gene.api.model.repository.RnaDifferentialExpressionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.sagebionetworks.agora.gene.api.model.dto.GeneDto;
// import org.sagebionetworks.agora.gene.api.model.dto.GenesPageDto;
// import org.sagebionetworks.agora.gene.api.model.mapper.GeneMapper;
// import org.sagebionetworks.agora.gene.api.model.repository.GeneRepository;
import org.springframework.stereotype.Service;

@Service
public class GeneService {

  private static final Logger logger = LoggerFactory.getLogger(GeneService.class);

  private GeneMapper geneMapper = new GeneMapper();
  private RnaDifferentialExpressionMapper rnaDifferentialExpressionMapper =
    new RnaDifferentialExpressionMapper();

  private final GeneRepository geneRepository;
  private final RnaDifferentialExpressionRepository rnaDifferentialExpressionRepository;

  private final TeamService teamService;

  public GeneService(
    GeneRepository geneRepository,
    RnaDifferentialExpressionRepository rnaDifferentialExpressionRepository,
    TeamService teamService
  ) {
    this.geneRepository = geneRepository;
    this.rnaDifferentialExpressionRepository = rnaDifferentialExpressionRepository;
    this.teamService = teamService;
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
      logger.info("getRnaComparisonGenes");
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
    logger.info("differentialExpression: {}", differentialExpression.size());
    if (differentialExpression != null && !differentialExpression.isEmpty()) {
      Map<String, GCTGeneDto> genes = new HashMap<>();
      Map<String, GeneDto> allGenes = getGenesMap();
      List<TeamDto> teams = teamService.getTeams();

      logger.info("allGenes: {}", allGenes.size());
      logger.info("teams: {}", teams.size());
    }

    // List<GCTGeneDto> dtos = rnaDifferentialExpressionMapper.convertToDtoList(documents);

    // return GCTGenesListDto.builder().items(dtos).build();
    return GCTGenesListDto.builder().build();
  }

  private GCTGenesListDto getProteinComparisonGenes(String subCategory) {
    return GCTGenesListDto.builder().build();
  }

  /**
   * Retrieves a map of all genes, keyed by ensembl_gene_id.
   * This is a Java equivalent of the TypeScript getGenesMap().
   * You should implement the logic to fetch all Gene objects from your data source.
   */
  private Map<String, GeneDto> getGenesMap() {
    List<GeneDocument> documents = geneRepository.findAll();
    logger.info("getGenesMap documents: {}", documents.size());
    List<GeneDto> allGenes = geneMapper.convertToDtoList(documents);
    Map<String, GeneDto> genesMap = new HashMap<>();
    for (GeneDto gene : allGenes) {
      if (gene.getEnsemblGeneId() != null) {
        genesMap.put(gene.getEnsemblGeneId(), gene);
      }
    }
    return genesMap;
  }
}
