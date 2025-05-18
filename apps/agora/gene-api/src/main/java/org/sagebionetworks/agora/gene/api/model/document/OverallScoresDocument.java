package org.sagebionetworks.agora.gene.api.model.document;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "genesoverallscores")
public class OverallScoresDocument {

  @Id
  public String id;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("target_risk_score")
  private BigDecimal targetRiskScore;

  @Field("genetics_score")
  private BigDecimal geneticsScore;

  @Field("multi_omics_score")
  private BigDecimal multiOmicsScore;
}
