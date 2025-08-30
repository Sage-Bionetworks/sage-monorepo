package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ValidateJwtRequestDto
 */

@JsonTypeName("ValidateJwtRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ValidateJwtRequestDto {

  private String token;

  public ValidateJwtRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ValidateJwtRequestDto(String token) {
    this.token = token;
  }

  public ValidateJwtRequestDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * JWT token to validate
   * @return token
   */
  @NotNull 
  @Schema(name = "token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "JWT token to validate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidateJwtRequestDto validateJwtRequest = (ValidateJwtRequestDto) o;
    return Objects.equals(this.token, validateJwtRequest.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidateJwtRequestDto {\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
  public static class Builder {

    private ValidateJwtRequestDto instance;

    public Builder() {
      this(new ValidateJwtRequestDto());
    }

    protected Builder(ValidateJwtRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ValidateJwtRequestDto value) { 
      this.instance.setToken(value.token);
      return this;
    }

    public ValidateJwtRequestDto.Builder token(String token) {
      this.instance.token(token);
      return this;
    }
    
    /**
    * returns a built ValidateJwtRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ValidateJwtRequestDto build() {
      try {
        return this.instance;
      } finally {
        // ensure that this.instance is not reused
        this.instance = null;
      }
    }

    @Override
    public String toString() {
      return getClass() + "=(" + instance + ")";
    }
  }

  /**
  * Create a builder with no initialized field (except for the default values).
  */
  public static ValidateJwtRequestDto.Builder builder() {
    return new ValidateJwtRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ValidateJwtRequestDto.Builder toBuilder() {
    ValidateJwtRequestDto.Builder builder = new ValidateJwtRequestDto.Builder();
    return builder.copyOf(this);
  }

}

