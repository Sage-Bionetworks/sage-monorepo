package org.sagebionetworks.modelad.api.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "gene")
public class GeneDocument {

  @Id public String id;
  public String name;
  public String description;
}
