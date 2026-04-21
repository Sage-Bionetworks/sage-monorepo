package org.sagebionetworks.agora.api.next.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class LinkedTargetDocument {

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  @Field("hgnc_symbol")
  private String hgncSymbol;
}
