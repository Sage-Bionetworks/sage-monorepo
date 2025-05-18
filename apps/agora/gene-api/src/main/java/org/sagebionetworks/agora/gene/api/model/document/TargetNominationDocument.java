package org.sagebionetworks.agora.gene.api.model.document;

import com.mongodb.lang.Nullable;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class TargetNominationDocument {

  @Field("initial_nomination")
  private int initialNomination;

  private String team;

  @Nullable
  private String study;

  @Field("input_data")
  private String inputData;

  @Nullable
  @Field("validation_study_details")
  private String validationStudyDetails;
}
