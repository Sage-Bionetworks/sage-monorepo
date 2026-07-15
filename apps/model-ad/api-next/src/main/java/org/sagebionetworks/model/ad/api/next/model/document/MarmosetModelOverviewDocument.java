package org.sagebionetworks.model.ad.api.next.model.document;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "marmo_overview")
public class MarmosetModelOverviewDocument {

  @Id
  private ObjectId id;

  private String name;

  @Field("model_type")
  private String modelType;

  @Field("biomarkers")
  private @Nullable Link biomarkers;

  @Field("study_data")
  private Link studyData;

  @Field("modified_genes")
  private List<String> modifiedGenes;

  @Field("available_data")
  private List<String> availableData;
}
