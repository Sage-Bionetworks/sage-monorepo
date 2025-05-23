package org.sagebionetworks.agora.gene.api.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.NominationsDocument;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionProfileDocument;
import org.sagebionetworks.agora.gene.api.model.document.TissueDocument;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneNominationsDto;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneTissueDto;
import org.sagebionetworks.agora.gene.api.service.GCTGenesService;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "preloadRnaDifferentialExpressionProfilesV4", order = "001", author = "tschaffter")
public class PreloadRnaDifferentialExpressionProfilesMigration {

  private final GCTGenesService gctGenesService;

  public PreloadRnaDifferentialExpressionProfilesMigration(GCTGenesService gctGenesService) {
    this.gctGenesService = gctGenesService;
  }

  @Execution
  public void execute(MongoTemplate mongoTemplate) {
    final String category = "RNA - Differential Expression";
    // XXX: For the proof of concept, we are precomputing the data for a single model only.
    final String subCategory = "AD Diagnosis (males and females)";
    final List<GCTGeneDto> getComparisonGenes = gctGenesService
      .getComparisonGenes(category, subCategory)
      .getItems();

    final List<RnaDifferentialExpressionProfileDocument> precomputed = getComparisonGenes
      .stream()
      // TODO: Open Jira ticket to suggest replacing blank value by null
      .filter(gctGene -> gctGene.getHgncSymbol() != null && !gctGene.getHgncSymbol().isBlank())
      .map(gctGene ->
        RnaDifferentialExpressionProfileDocument.builder()
          .ensemblGeneId(gctGene.getEnsemblGeneId())
          .hgncSymbol(gctGene.getHgncSymbol())
          .targetRiskScore(gctGene.getTargetRiskScore())
          .geneticsScore(gctGene.getGeneticsScore())
          .multiOmicsScore(gctGene.getMultiOmicsScore())
          .tissues(mapTissues(gctGene.getTissues()))
          .nominations(mapNominations(gctGene.getNominations()))
          .build()
      )
      .toList();

    mongoTemplate.dropCollection(RnaDifferentialExpressionProfileDocument.class);
    mongoTemplate.insertAll(precomputed);
  }

  private List<TissueDocument> mapTissues(List<GCTGeneTissueDto> tissueDtos) {
    return tissueDtos
      .stream()
      .map(tissueDto ->
        TissueDocument.builder()
          .name(tissueDto.getName())
          .logfc(tissueDto.getLogfc())
          .adjPVal(tissueDto.getAdjPVal())
          .ciL(tissueDto.getCiL())
          .ciR(tissueDto.getCiR())
          .build()
      )
      .toList();
  }

  private NominationsDocument mapNominations(GCTGeneNominationsDto nominationsDto) {
    if (nominationsDto == null) return null;
    return NominationsDocument.builder()
      .count(nominationsDto.getCount())
      .year(nominationsDto.getYear())
      .teams(nominationsDto.getTeams())
      .studies(nominationsDto.getStudies())
      .inputs(nominationsDto.getInputs())
      .programs(nominationsDto.getPrograms())
      .validations(nominationsDto.getValidations())
      .build();
  }

  @RollbackExecution
  public void rollback(MongoTemplate mongoTemplate) {
    mongoTemplate.dropCollection(RnaDifferentialExpressionProfileDocument.class);
  }
}
