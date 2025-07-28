package org.sagebionetworks.openchallenges.challenge.service.model.dto.organization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Objects;

/**
 * The nature of a challenge participation.
 */

@Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen",
  comments = "Generator version: 7.14.0"
)
public enum ChallengeParticipationRoleDto {
  CHALLENGE_ORGANIZER("challenge_organizer"),

  DATA_CONTRIBUTOR("data_contributor"),

  SPONSOR("sponsor");

  private final String value;

  ChallengeParticipationRoleDto(String value) {
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
  public static ChallengeParticipationRoleDto fromValue(String value) {
    for (ChallengeParticipationRoleDto b : ChallengeParticipationRoleDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
