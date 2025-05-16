package org.sagebionetworks.agora.gene.api.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "genesoverallscores")
public class OverallScoresDocument {

  @Id
  public String id;
}
