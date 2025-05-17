package org.sagebionetworks.agora.gene.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.sagebionetworks.agora.gene.api.model.document.GeneDocument;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionDocument;
import org.sagebionetworks.agora.gene.api.model.dto.BioDomainsDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
import org.sagebionetworks.agora.gene.api.model.dto.GeneDto;
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
public class GCTGenesService {

  private static final Logger logger = LoggerFactory.getLogger(GCTGenesService.class);

  private GeneMapper geneMapper = new GeneMapper();
  private RnaDifferentialExpressionMapper rnaDifferentialExpressionMapper =
    new RnaDifferentialExpressionMapper();

  private final GeneRepository geneRepository;
  private final RnaDifferentialExpressionRepository rnaDifferentialExpressionRepository;

  private final TeamService teamService;
  private final OverallScoresService overallScoresService;
  private final BioDomainsService bioDomainsService;

  public GCTGenesService(
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

  private GCTGenesListDto getRnaComparisonGenes(String model) {
    List<RnaDifferentialExpressionDocument> differentialExpression =
      rnaDifferentialExpressionRepository.findByModelSorted(model);
    logger.info("differentialExpression: {}", differentialExpression.size());

    Map<String, GCTGeneDto> genes = new HashMap<>();
    if (differentialExpression != null && !differentialExpression.isEmpty()) {
      // Fetch data
      Map<String, GeneDocument> allGenes = getGenesMap();

      for (RnaDifferentialExpressionDocument exp : differentialExpression) {
        String ensemblGeneId = exp.getEnsemblGeneId();
        if (!genes.containsKey(ensemblGeneId)) {
          // GeneDto gene = allGenes.getOrDefault(
          //   ensemblGeneId,
          //   GeneDto.builder().ensemblGeneId(ensemblGeneId).hgncSymbol(exp.getHgncSymbol()).build()
          // );
          GeneDto gene = GeneDto.builder()
            .ensemblGeneId(ensemblGeneId)
            .hgncSymbol(exp.getHgncSymbol())
            .build();
          genes.put(ensemblGeneId, getComparisonGene(gene));
          // genes.put(ensemblGeneId, getComparisonGene(gene, teams, scores, allBiodomains));
        }
        // GCTGene gctGene = genes.get(ensemblGeneId);
        // gctGene
        //   .getTissues()
        //   .add(
        //     new Tissue(
        //       exp.getTissue(),
        //       exp.getLogfc(),
        //       exp.getAdjPVal(),
        //       exp.getCiL(),
        //       exp.getCiR()
        //     )
        //   );
      }
      // TODO: Using entity directly will be faster (next PR).
      // TODO: Use the verb list for list/array and get for single object.
      // List<TeamDto> teams = teamService.getTeams();
      // List<OverallScoresDto> scores = overallScoresService.getOverallScores();
      // List<BioDomainsDto> allBiodomains = bioDomainsService.getBioDomains();

      // logger.info("teams: {}", teams.size());
      // logger.info("scores: {}", scores.size());
      // logger.info("allBiodomains: {}", allBiodomains.size());
    }

    List<GCTGeneDto> geneList = new ArrayList<>(genes.values());
    return GCTGenesListDto.builder().items(geneList).build();
  }

  private GCTGenesListDto getProteinComparisonGenes(String subCategory) {
    return GCTGenesListDto.builder().build();
  }

  // Helper to build a map of all genes by ensembl_gene_id
  private Map<String, GeneDocument> getGenesMap() {
    return geneRepository
      .findAll()
      .stream()
      .filter(gene -> gene.getEnsemblGeneId() != null)
      .collect(Collectors.toMap(GeneDocument::getEnsemblGeneId, gene -> gene));
  }

  private GCTGeneDto getComparisonGene(GeneDto gene) {
    GCTGeneDto gctGene = GCTGeneDto.builder()
      .ensemblGeneId(gene.getEnsemblGeneId())
      .hgncSymbol(gene.getHgncSymbol())
      .build();
    // gctGene.setTeams(teams);
    // gctGene.setOverallScores(scores);
    // gctGene.setBioDomains(allBiodomains);
    return gctGene;
  }
}
