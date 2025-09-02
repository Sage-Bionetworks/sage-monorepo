package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Permission scope defining specific access rights
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public enum AuthScopeDto {
  
  USER_PROFILE("user:profile"),
  
  USER_EMAIL("user:email"),
  
  USER_KEYS("user:keys"),
  
  READ_ORG("read:org"),
  
  WRITE_ORG("write:org"),
  
  DELETE_ORG("delete:org"),
  
  ADMIN_ORG("admin:org"),
  
  READ_CHALLENGE("read:challenge"),
  
  WRITE_CHALLENGE("write:challenge"),
  
  DELETE_CHALLENGE("delete:challenge"),
  
  ADMIN_CHALLENGE("admin:challenge"),
  
  ADMIN_AUTH("admin:auth"),
  
  ADMIN_ALL("admin:all");

  private final String value;

  AuthScopeDto(String value) {
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
  public static AuthScopeDto fromValue(String value) {
    for (AuthScopeDto b : AuthScopeDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

