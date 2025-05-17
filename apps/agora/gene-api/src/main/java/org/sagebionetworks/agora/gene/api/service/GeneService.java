package org.sagebionetworks.agora.gene.api.service;

import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionDocument;
import org.sagebionetworks.agora.gene.api.model.dto.BioDomainsDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
import org.sagebionetworks.agora.gene.api.model.dto.OverallScoresDto;
import org.sagebionetworks.agora.gene.api.model.dto.TeamDto;
import org.sagebionetworks.agora.gene.api.model.mapper.GeneMapper;
import org.sagebionetworks.agora.gene.api.model.mapper.RnaDifferentialExpressionMapper;
import org.sagebionetworks.agora.gene.api.model.repository.GeneRepository;
import org.sagebionetworks.agora.gene.api.model.repository.RnaDifferentialExpressionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private final OverallScoresService overallScoresService;
  private final BioDomainsService bioDomainsService;

  public GeneService(
    GeneRepository geneRepository,
    RnaDifferentialExpressionRepository rnaDifferentialExpressionRepository,
    TeamService teamService,
    OverallScoresService overallScoresService,
    BioDomainsService bioDomainsService
  ) {
    this.geneRepository = geneRepository;
    this.rnaDifferentialExpressionRepository = rnaDifferentialExpressionRepository;
    this.teamService = teamService;
    this.overallScoresService = overallScoresService;
    this.bioDomainsService = bioDomainsService;
  }

  public GCTGenesListDto getComparisonGenes(String category, String subCategory) {
    // Use the default values from the web app during development.
    // TODO: Update the API description to set the default values for category and subCategory.
    category = "RNA - Differential Expression";
    subCategory = "AD Diagnosis (males and females)";

    GCTGenesListDto gctGenesListDto = null;

    // TODO: Consider different endpoints for RNA and Protein differential expression, which would allow more descriptive query parameters (model for RNA, method for Protein).
    if (category.equals("RNA - Differential Expression")) {
      logger.info("getRnaComparisonGenes");
      gctGenesListDto = getRnaComparisonGenes(subCategory);
    } else if (category.equals("Protein - Differential Expression")) {
      gctGenesListDto = getProteinComparisonGenes(subCategory);
    } else {
      // TODO: Return 400 Bad Request
      gctGenesListDto = GCTGenesListDto.builder().build();
    }

    return gctGenesListDto;
  }

  private GCTGenesListDto getRnaComparisonGenes(String subCategory) {
    List<RnaDifferentialExpressionDocument> differentialExpression =
      rnaDifferentialExpressionRepository.findByModelSorted(subCategory);
    logger.info("differentialExpression: {}", differentialExpression.size());
    if (differentialExpression != null && !differentialExpression.isEmpty()) {
      // TODO: Using entity directly will be faster (next PR).
      // TODO: Use the verb list for list/array and get for single object.
      List<TeamDto> teams = teamService.getTeams();
      List<OverallScoresDto> scores = overallScoresService.getOverallScores();
      List<BioDomainsDto> allBiodomains = bioDomainsService.getBioDomains();

      logger.info("teams: {}", teams.size());
      logger.info("scores: {}", scores.size());
      logger.info("allBiodomains: {}", allBiodomains.size());
    }

    return GCTGenesListDto.builder().build();
  }

  private GCTGenesListDto getProteinComparisonGenes(String subCategory) {
    return GCTGenesListDto.builder().build();
  }
}
