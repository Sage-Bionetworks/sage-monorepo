package org.sagebionetworks.agora.gene.api.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "genes")
public class GeneDocument {

  @Id
  public String id;

  public String model;
  public String study;
  public String tissue;
}
