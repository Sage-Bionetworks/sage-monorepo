package org.sagebionetworks.model.ad.api.next.model.document;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Getter
@Setter
public class GeneticInfo {

  @Field("modified_gene")
  private String modifiedGene;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  private String allele;

  @Field("allele_type")
  private String alleleType;

  @Field("mgi_allele_id")
  private BigDecimal mgiAlleleId;
}
