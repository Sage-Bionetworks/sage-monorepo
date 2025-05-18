package org.sagebionetworks.agora.gene.api.model.document;

import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "genesbiodomains")
public class BioDomainsDocument {

  @Id
  public String id;

  @Field("ensembl_gene_id")
  private String ensemblGeneId;

  // TODO: Align the document and variable names
  // TODO: Be consistent with the conversion between camelCase and snake_case)
  @Field("gene_biodomains")
  private List<BioDomainDocument> geneBioDomains;
}
