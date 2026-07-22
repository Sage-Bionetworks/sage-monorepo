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
@Document(collection = "model_details")
public class MouseModelDocument {

  @Id
  private ObjectId id;

  private String name;

  @Field("matched_controls")
  private List<String> matchedControls;

  @Field("model_type")
  private String modelType;

  @Field("contributing_group")
  private String contributingGroup;

  @Field("study_synid")
  private String studySynid;

  private String rrid;

  @Field("jax_id")
  private String jaxId;

  @Field("alzforum_id")
  private String alzforumId;

  private String genotype;

  private List<String> aliases;

  private @Nullable String transcriptomics;

  @Field("disease_correlation")
  private @Nullable String diseaseCorrelation;

  @Field("spatial_transcriptomics")
  private @Nullable String spatialTranscriptomics;

  @Field("genetic_info")
  private List<GeneticInfo> geneticInfo;

  private List<ModelData> biomarkers;

  private List<ModelData> pathology;
}
