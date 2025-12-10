package org.sagebionetworks.model.ad.api.next.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "dataversion")
public class DataVersionDocument {

  @Id
  private ObjectId id;

  @Field("data_file")
  private String dataFile;

  @Field("data_version")
  private String dataVersion;
}
