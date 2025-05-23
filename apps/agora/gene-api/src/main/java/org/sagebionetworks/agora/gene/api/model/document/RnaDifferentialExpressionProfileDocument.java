package org.sagebionetworks.agora.gene.api.model.document;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Data
@Document(collection = "rna_differential_expression_profile")
public class RnaDifferentialExpressionProfileDocument {

  @Id
  public String id;

  private String model;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("hgnc_symbol")
  private String hgncSymbol;

  @Field("target_risk_score")
  private BigDecimal targetRiskScore;

  @Field("genetics_score")
  private BigDecimal geneticsScore;

  @Field("multi_omics_score")
  private BigDecimal multiOmicsScore;
}
