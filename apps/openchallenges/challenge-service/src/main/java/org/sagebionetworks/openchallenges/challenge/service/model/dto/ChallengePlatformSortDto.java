package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** What to sort results by. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengePlatformSortDto {
  NAME("name"),

  RELEVANCE("relevance");

  private String value;

  ChallengePlatformSortDto(String value) {
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
  public static ChallengePlatformSortDto fromValue(String value) {
    for (ChallengePlatformSortDto b : ChallengePlatformSortDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
