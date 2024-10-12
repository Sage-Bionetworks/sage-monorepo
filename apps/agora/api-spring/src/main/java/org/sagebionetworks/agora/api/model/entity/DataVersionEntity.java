package org.sagebionetworks.agora.api.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "dataversion")
public class DataVersionEntity {

  @Id
  public String id;

  @Field("data_file")
  public String dataFile;

  @Field("data_version")
  public String dataVersion;

  @Field("team_images_id")
  public String teamImagesId;
}
