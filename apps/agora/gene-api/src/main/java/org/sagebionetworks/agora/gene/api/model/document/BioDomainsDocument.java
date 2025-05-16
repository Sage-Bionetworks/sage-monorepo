package org.sagebionetworks.agora.gene.api.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "genesbiodomains")
public class BioDomainsDocument {

  @Id
  public String id;
}
