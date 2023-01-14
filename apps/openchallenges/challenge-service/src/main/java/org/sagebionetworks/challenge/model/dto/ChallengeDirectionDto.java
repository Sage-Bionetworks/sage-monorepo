package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The direction to sort the results by. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeDirectionDto {
  ASC("asc"),

  DESC("desc");

  private String value;

  ChallengeDirectionDto(String value) {
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
  public static ChallengeDirectionDto fromValue(String value) {
    for (ChallengeDirectionDto b : ChallengeDirectionDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
