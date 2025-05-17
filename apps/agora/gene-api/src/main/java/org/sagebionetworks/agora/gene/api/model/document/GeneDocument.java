package org.sagebionetworks.agora.gene.api.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "genes")
public class GeneDocument {

  @Id
  public String id;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("hgnc_symbol")
  private String hgncSymbol;
}
