package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The category of the challenge.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
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

