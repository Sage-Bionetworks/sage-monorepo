package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The direction to sort the results by. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeInputDataTypeDirectionDto {
  ASC("asc"),

  DESC("desc");

  private String value;

  ChallengeInputDataTypeDirectionDto(String value) {
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
  public static ChallengeInputDataTypeDirectionDto fromValue(String value) {
    for (ChallengeInputDataTypeDirectionDto b : ChallengeInputDataTypeDirectionDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    return null;
  }
}
