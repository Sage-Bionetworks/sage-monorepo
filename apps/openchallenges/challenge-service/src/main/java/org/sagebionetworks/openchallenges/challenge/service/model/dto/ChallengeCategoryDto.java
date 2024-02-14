package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The category of the challenge. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeCategoryDto {
  FEATURED("featured"),

  BENCHMARK("benchmark"),

  HACKATHON("hackathon"),

  STARTING_SOON("starting_soon"),

  ENDING_SOON("ending_soon"),

  RECENTLY_STARTED("recently_started"),

  RECENTLY_ENDED("recently_ended");

  private String value;

  ChallengeCategoryDto(String value) {
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
  public static ChallengeCategoryDto fromValue(String value) {
    for (ChallengeCategoryDto b : ChallengeCategoryDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
