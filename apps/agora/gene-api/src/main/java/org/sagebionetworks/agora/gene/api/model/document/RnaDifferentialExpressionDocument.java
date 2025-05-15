package org.sagebionetworks.agora.gene.api.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "genes")
public class RnaDifferentialExpressionDocument {

  @Id
  private String id;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("hgnc_symbol")
  private String hgncSymbol;

  private double logfc;
  private double fc;

  @Field("ci_l")
  private double ciL;

  @Field("ci_r")
  private double ciR;

  @Field("adj_p_val")
  private double adjPVal;

  private String tissue;
  private String study;
  private String model;
}
