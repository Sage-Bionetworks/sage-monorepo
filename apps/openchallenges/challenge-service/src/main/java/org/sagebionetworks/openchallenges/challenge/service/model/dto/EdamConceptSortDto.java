package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** What to sort results by. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum EdamConceptSortDto {
  PREFERRED_LABEL("preferred_label"),

  RELEVANCE("relevance");

  private String value;

  EdamConceptSortDto(String value) {
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
  public static EdamConceptSortDto fromValue(String value) {
    for (EdamConceptSortDto b : EdamConceptSortDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
