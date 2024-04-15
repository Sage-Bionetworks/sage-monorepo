package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The EDAM section (sub-ontology). */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum EdamSectionDto {
  DATA("data"),

  FORMAT("format"),

  IDENTIFIER("identifier"),

  OPERATION("operation"),

  TOPIC("topic");

  private String value;

  EdamSectionDto(String value) {
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
  public static EdamSectionDto fromValue(String value) {
    for (EdamSectionDto b : EdamSectionDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
