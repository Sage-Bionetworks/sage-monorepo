package org.sagebionetworks.model.ad.api.next.model.document;

import java.util.List;
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
@Document(collection = "marmo_details")
public class MarmosetModelDocument {

  @Id
  private ObjectId id;

  private String name;

  @Field("model_type")
  private String modelType;

  @Field("study_synid")
  private String studySynid;

  @Field("genetic_info")
  private List<GeneticInfo> geneticInfo;

  private List<ModelData> biomarkers;
}
