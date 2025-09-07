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
  
  OPENID("openid"),
  
  PROFILE("profile"),
  
  EMAIL("email"),
  
  READ_PROFILE("read:profile"),
  
  UPDATE_PROFILE("update:profile"),
  
  READ_API_KEY("read:api-key"),
  
  CREATE_API_KEY("create:api-key"),
  
  UPDATE_API_KEY("update:api-key"),
  
  DELETE_API_KEY("delete:api-key"),
  
  ROTATE_API_KEY("rotate:api-key"),
  
  READ_ORGS("read:orgs"),
  
  CREATE_ORGS("create:orgs"),
  
  UPDATE_ORGS("update:orgs"),
  
  DELETE_ORGS("delete:orgs"),
  
  READ_CHALLENGES("read:challenges"),
  
  CREATE_CHALLENGES("create:challenges"),
  
  UPDATE_CHALLENGES("update:challenges"),
  
  DELETE_CHALLENGES("delete:challenges"),
  
  READ_CHALLENGES_ANALYTICS("read:challenges-analytics"),
  
  READ_CHALLENGE_PLATFORMS("read:challenge-platforms"),
  
  CREATE_CHALLENGE_PLATFORMS("create:challenge-platforms"),
  
  UPDATE_CHALLENGE_PLATFORMS("update:challenge-platforms"),
  
  DELETE_CHALLENGE_PLATFORMS("delete:challenge-platforms"),
  
  READ_EDAM_CONCEPTS("read:edam-concepts");

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

