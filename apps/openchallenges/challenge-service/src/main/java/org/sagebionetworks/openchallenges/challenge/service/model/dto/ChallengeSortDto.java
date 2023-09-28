package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** What to sort results by. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeSortDto {
  CREATED("created"),

  RANDOM("random"),

  RELEVANCE("relevance"),

  STARRED("starred"),

  START_DATE("start_date"),

  END_DATE("end_date");

  private String value;

  ChallengeSortDto(String value) {
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
  public static ChallengeSortDto fromValue(String value) {
    for (ChallengeSortDto b : ChallengeSortDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
