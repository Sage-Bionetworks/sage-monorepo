package org.sagebionetworks.agora.api.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dataversion")
public class DataVersionEntity {

  @Id
  public String id;

  public String dataFile;
  public String dataVersion;
  public String teamImagesId;
}
