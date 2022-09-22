package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** Gets or Sets UserStatus */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum UserStatus {
  PENDING("PENDING"),

  APPROVED("APPROVED"),

  DISABLED("DISABLED"),

  BLACKLIST("BLACKLIST");

  private String value;

  UserStatus(String value) {
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
  public static UserStatus fromValue(String value) {
    for (UserStatus b : UserStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
