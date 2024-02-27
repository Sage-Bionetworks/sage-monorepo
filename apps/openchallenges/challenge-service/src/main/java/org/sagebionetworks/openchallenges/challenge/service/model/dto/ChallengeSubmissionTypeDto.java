package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The submission type of the challenge. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeSubmissionTypeDto {
  CONTAINER_IMAGE("container_image"),

  PREDICTION_FILE("prediction_file"),

  NOTEBOOK("notebook"),

  MLCUBE("mlcube"),

  OTHER("other");

  private String value;

  ChallengeSubmissionTypeDto(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ChallengeSubmissionTypeDto fromValue(String value) {
    for (ChallengeSubmissionTypeDto b : ChallengeSubmissionTypeDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
