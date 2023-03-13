package org.sagebionetworks.openchallenges.organization.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** What to sort results by. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum OrganizationSortDto {
  CHALLENGE_COUNT("challenge_count"),

  CREATED("created"),

  RELEVANCE("relevance");

  private String value;

  OrganizationSortDto(String value) {
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
  public static OrganizationSortDto fromValue(String value) {
    for (OrganizationSortDto b : OrganizationSortDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
