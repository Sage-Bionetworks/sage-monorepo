package org.sagebionetworks.openchallenges.organization.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The role of a challenge contributor. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeContributorRoleDto {
  CHALLENGE_ORGANIZER("challenge_organizer"),

  DATA_CONTRIBUTOR("data_contributor"),

  SPONSOR("sponsor");

  private String value;

  ChallengeContributorRoleDto(String value) {
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
  public static ChallengeContributorRoleDto fromValue(String value) {
    for (ChallengeContributorRoleDto b : ChallengeContributorRoleDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
