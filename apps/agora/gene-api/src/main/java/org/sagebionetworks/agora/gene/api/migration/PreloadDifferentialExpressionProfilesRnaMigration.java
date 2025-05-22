package org.sagebionetworks.agora.gene.api.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.DifferentialExpressionProfileRnaDocument;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneDto;
import org.sagebionetworks.agora.gene.api.service.GCTGenesService;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(
  id = "preload-differential-expression-profiles-rna",
  order = "001",
  author = "tschaffter"
)
public class PreloadDifferentialExpressionProfilesRnaMigration {

  private final GCTGenesService gctGenesService;

  public PreloadDifferentialExpressionProfilesRnaMigration(GCTGenesService gctGenesService) {
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

    final List<DifferentialExpressionProfileRnaDocument> precomputed = getComparisonGenes
      .stream()
      .map(gctGene ->
        DifferentialExpressionProfileRnaDocument.builder()
          .ensemblGeneId(gctGene.getEnsemblGeneId())
          .hgncSymbol(gctGene.getHgncSymbol())
          .build()
      )
      .toList();

    mongoTemplate.dropCollection(DifferentialExpressionProfileRnaDocument.class);
    mongoTemplate.insertAll(precomputed);
  }

  @RollbackExecution
  public void rollback(MongoTemplate mongoTemplate) {
    mongoTemplate.dropCollection(DifferentialExpressionProfileRnaDocument.class);
  }
}
