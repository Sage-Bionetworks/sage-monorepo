package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The status of the challenge. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeStatusDto {
  UPCOMING("upcoming"),

  ACTIVE("active"),

  COMPLETED("completed");

  private String value;

  ChallengeStatusDto(String value) {
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
  public static ChallengeStatusDto fromValue(String value) {
    for (ChallengeStatusDto b : ChallengeStatusDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
