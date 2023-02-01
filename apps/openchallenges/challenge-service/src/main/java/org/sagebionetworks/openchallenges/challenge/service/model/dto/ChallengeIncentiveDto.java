package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The incentive type of the challenge. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ChallengeIncentiveDto {
  MONETARY("monetary"),

  PUBLICATION("publication"),

  SPEAKING_ENGAGEMENT("speaking_engagement"),

  OTHER("other");

  private String value;

  ChallengeIncentiveDto(String value) {
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
  public static ChallengeIncentiveDto fromValue(String value) {
    for (ChallengeIncentiveDto b : ChallengeIncentiveDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
