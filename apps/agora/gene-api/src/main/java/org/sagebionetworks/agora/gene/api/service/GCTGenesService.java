package org.sagebionetworks.agora.gene.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.sagebionetworks.agora.gene.api.model.document.BioDomainDocument;
import org.sagebionetworks.agora.gene.api.model.document.BioDomainsDocument;
import org.sagebionetworks.agora.gene.api.model.document.GeneDocument;
import org.sagebionetworks.agora.gene.api.model.document.OverallScoresDocument;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionDocument;
import org.sagebionetworks.agora.gene.api.model.document.TargetNominationDocument;
import org.sagebionetworks.agora.gene.api.model.document.TeamDocument;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneNominationsDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneTissueDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGenesListDto;
import org.sagebionetworks.agora.gene.api.model.dto.TeamDto;
import org.sagebionetworks.agora.gene.api.model.mapper.TeamMapper;
import org.sagebionetworks.agora.gene.api.model.repository.BioDomainsRepository;
import org.sagebionetworks.agora.gene.api.model.repository.GeneRepository;
import org.sagebionetworks.agora.gene.api.model.repository.OverallScoresRepository;
import org.sagebionetworks.agora.gene.api.model.repository.RnaDifferentialExpressionRepository;
import org.sagebionetworks.agora.gene.api.model.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GCTGenesService {

  private static final Logger logger = LoggerFactory.getLogger(GCTGenesService.class);

  private TeamMapper teamMapper = new TeamMapper();

  private final BioDomainsRepository bioDomainsRepository;
  private final GeneRepository geneRepository;
  private final OverallScoresRepository overallScoresRepository;
  private final RnaDifferentialExpressionRepository rnaDifferentialExpressionRepository;
  private final TeamRepository teamRepository;

  public GCTGenesService(
    BioDomainsRepository bioDomainsRepository,
    GeneRepository geneRepository,
    OverallScoresRepository overallScoresRepository,
    RnaDifferentialExpressionRepository rnaDifferentialExpressionRepository,
    TeamRepository teamRepository
  ) {
    this.bioDomainsRepository = bioDomainsRepository;
    this.geneRepository = geneRepository;
    this.overallScoresRepository = overallScoresRepository;
    this.rnaDifferentialExpressionRepository = rnaDifferentialExpressionRepository;
    this.teamRepository = teamRepository;
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

    Map<String, GCTGeneDto> genes = new HashMap<>();
    if (differentialExpression != null && !differentialExpression.isEmpty()) {
      // Fetch data
      Map<String, GeneDocument> allGenes = getGenesMap();
      List<TeamDocument> teams = teamRepository.findAll();
      List<OverallScoresDocument> scores = overallScoresRepository.findAll();
      List<BioDomainsDocument> allBiodomains = bioDomainsRepository.findAll();

      for (RnaDifferentialExpressionDocument exp : differentialExpression) {
        String ensemblGeneId = exp.getEnsemblGeneId();
        if (!genes.containsKey(ensemblGeneId)) {
          // Get the GeneDocument by ensemblGeneId or create a new one
          GeneDocument gene = allGenes.get(ensemblGeneId);
          if (gene == null) {
            gene = new GeneDocument();
            gene.setEnsemblGeneId(ensemblGeneId);
            gene.setHgncSymbol(exp.getHgncSymbol());
          }

          // Compute the GCTGeneDto and add it to the genes list
          genes.put(ensemblGeneId, getComparisonGene(gene, teams, scores, allBiodomains));
        }

        // Add tissue data to the gene
        GCTGeneTissueDto tissue = GCTGeneTissueDto.builder()
          .name(exp.getTissue())
          .logfc((float) exp.getLogfc())
          .adjPVal((float) exp.getAdjPVal())
          .ciL((float) exp.getCiL())
          .ciR((float) exp.getCiR())
          .build();

        genes.get(ensemblGeneId).addTissuesItem(tissue);
      }
    }

    // Prepare the list of GCT genes and sort them by their ensembl_gene_id
    List<GCTGeneDto> geneList = new ArrayList<>(genes.values());
    geneList.sort(Comparator.comparing(GCTGeneDto::getEnsemblGeneId));

    return GCTGenesListDto.builder().items(geneList).build();
  }

  private GCTGenesListDto getProteinComparisonGenes(String subCategory) {
    return GCTGenesListDto.builder().build();
  }

  // Helper to build a map of all genes by ensembl_gene_id
  // XXX: What are the assumption behind selecting the last occurrence of a gene?
  private Map<String, GeneDocument> getGenesMap() {
    return geneRepository
      .findAll()
      .stream()
      .filter(gene -> gene.getEnsemblGeneId() != null)
      .collect(
        Collectors.toMap(
          GeneDocument::getEnsemblGeneId,
          gene -> gene,
          (existing, replacement) -> replacement // keep the last occurrence as in the original code
        )
      );
  }

  private GCTGeneDto getComparisonGene(
    GeneDocument gene,
    List<TeamDocument> teams,
    List<OverallScoresDocument> scores,
    List<BioDomainsDocument> allBiodomains
  ) {
    // Find scores for this gene
    OverallScoresDocument geneScores = scores
      .stream()
      .filter(s -> s.getEnsemblGeneId().equals(gene.getEnsemblGeneId()))
      .findFirst()
      .orElse(null);

    // Find biodomains for this gene
    List<String> geneBiodomains = allBiodomains
      .stream()
      .filter(b -> b.getEnsemblGeneId().equals(gene.getEnsemblGeneId()))
      .findFirst()
      .map(b -> b.getGeneBioDomains().stream().map(BioDomainDocument::getBioDomain).toList())
      .orElse(null);

    // TODO: Remove after changing the type of associations in the API description
    List<BigDecimal> associations = getComparisonGeneAssociations(gene)
      .stream()
      .map(BigDecimal::valueOf)
      .collect(Collectors.toList());

    GCTGeneDto gctGene = GCTGeneDto.builder()
      .ensemblGeneId(gene.getEnsemblGeneId())
      .hgncSymbol(gene.getHgncSymbol())
      .nominations(getComparisonGeneNominations(gene, teams))
      .associations(associations)
      .targetRiskScore(geneScores != null ? geneScores.getTargetRiskScore() : null)
      .geneticsScore(geneScores != null ? geneScores.getGeneticsScore() : null)
      .multiOmicsScore(geneScores != null ? geneScores.getMultiOmicsScore() : null)
      .biodomains(geneBiodomains)
      .targetEnablingResources(getTargetEnablingResources(gene))
      .build();

    return gctGene;
  }

  private List<Integer> getComparisonGeneAssociations(GeneDocument gene) {
    // TODO: Use enum for the associations
    List<Integer> associations = new ArrayList<>();

    // Genetically Associated with LOAD
    if (Boolean.TRUE.equals(gene.isIgap())) {
      associations.add(1);
    }

    // eQTL in Brain
    if (Boolean.TRUE.equals(gene.isEqtl())) {
      associations.add(2);
    }

    // RNA Expression Changed in AD Brain
    if (
      Boolean.TRUE.equals(gene.isRnaBrainChangeStudied()) &&
      Boolean.TRUE.equals(gene.isAnyRnaChangedInAdBrain())
    ) {
      associations.add(3);
    }

    // Protein Expression Changed in AD Brain
    if (
      Boolean.TRUE.equals(gene.isProteinBrainChangeStudied()) &&
      Boolean.TRUE.equals(gene.isAnyProteinChangedInAdBrain())
    ) {
      associations.add(4);
    }

    return associations;
  }

  private List<String> getTargetEnablingResources(GeneDocument gene) {
    // TODO: Use enum for the resources
    List<String> resources = new ArrayList<>();
    if (Boolean.TRUE.equals(gene.getIsAdi())) {
      resources.add("AD Informer Set");
    }
    if (Boolean.TRUE.equals(gene.getIsTep())) {
      resources.add("Target Enabling Package");
    }
    return resources;
  }

  private GCTGeneNominationsDto getComparisonGeneNominations(
    GeneDocument gene,
    List<TeamDocument> teams
  ) {
    GCTGeneNominationsDto data = GCTGeneNominationsDto.builder()
      .count(gene.getTotalNominations() != null ? gene.getTotalNominations() : 0)
      .year(0)
      .teams(new ArrayList<>())
      .studies(new ArrayList<>())
      .inputs(new ArrayList<>())
      .programs(new ArrayList<>())
      .validations(new ArrayList<>())
      .build();

    if (gene.getTargetNominations() != null) {
      for (TargetNominationDocument n : gene.getTargetNominations()) {
        // Year
        // XXX: Can n.getInitialNomination() be zero?
        if (
          n.getInitialNomination() > 0 &&
          (data.getYear() == null || n.getInitialNomination() < data.getYear())
        ) {
          data.setYear(n.getInitialNomination());
        }

        // Team / Programs
        if (n.getTeam() != null) {
          TeamDto team = teams
            .stream()
            .filter(t -> n.getTeam().equals(t.getTeam()))
            .findFirst()
            .map(teamMapper::convertToDto)
            .orElse(null);

          if (
            team != null &&
            team.getProgram() != null &&
            !data.getPrograms().contains(team.getProgram())
          ) {
            data.addProgramsItem(team.getProgram());
          }

          data.addTeamsItem(n.getTeam());
        }

        // Studies
        if (n.getStudy() != null) {
          for (String item : n.getStudy().split(",\\s*")) {
            if (!data.getStudies().contains(item)) {
              data.addStudiesItem(item);
            }
          }
        }

        // Inputs
        if (n.getInputData() != null) {
          for (String item : n.getInputData().split(",\\s*")) {
            if (!data.getInputs().contains(item)) {
              data.addInputsItem(item);
            }
          }
        }

        // Validations
        if (n.getValidationStudyDetails() != null) {
          String validationStudyDetailClean = n.getValidationStudyDetails().trim().toLowerCase();
          if (!data.getValidations().contains(validationStudyDetailClean)) {
            data.addValidationsItem(validationStudyDetailClean);
          }
        }
      }
    }

    return data;
  }
}
