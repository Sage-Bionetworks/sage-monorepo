package org.sagebionetworks.openchallenges.organization.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The category of the organization. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum OrganizationCategoryDto {
  FEATURED("featured");

  private String value;

  OrganizationCategoryDto(String value) {
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
  public static OrganizationCategoryDto fromValue(String value) {
    for (OrganizationCategoryDto b : OrganizationCategoryDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
