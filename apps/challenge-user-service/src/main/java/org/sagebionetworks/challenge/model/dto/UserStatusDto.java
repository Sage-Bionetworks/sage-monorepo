package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The account status of a user */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum UserStatusDto {
  PENDING("pending"),

  APPROVED("approved"),

  DISABLED("disabled"),

  BLACKLIST("blacklist");

  private String value;

  UserStatusDto(String value) {
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
  public static UserStatusDto fromValue(String value) {
    for (UserStatusDto b : UserStatusDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
