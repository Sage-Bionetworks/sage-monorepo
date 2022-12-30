package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The difficulty level of a challenge. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeDifficultyDto {
  GOOD_FOR_BEGINNERS("good_for_beginners"),

  INTERMEDIATE("intermediate"),

  ADVANCED("advanced");

  private String value;

  ChallengeDifficultyDto(String value) {
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
  public static ChallengeDifficultyDto fromValue(String value) {
    for (ChallengeDifficultyDto b : ChallengeDifficultyDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
