package org.sagebionetworks.modelad.api.model.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gene")
public class GeneDocument {

  @Id public String id;
  public String name;
  public String description;
}
