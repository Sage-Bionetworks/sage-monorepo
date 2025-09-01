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
 * Oauth2RevokeToken200ResponseDto
 */

@JsonTypeName("oauth2RevokeToken_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class Oauth2RevokeToken200ResponseDto {

  private @Nullable String message;

  public Oauth2RevokeToken200ResponseDto message(@Nullable String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", example = "Token revoked successfully", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public @Nullable String getMessage() {
    return message;
  }

  public void setMessage(@Nullable String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Oauth2RevokeToken200ResponseDto oauth2RevokeToken200Response = (Oauth2RevokeToken200ResponseDto) o;
    return Objects.equals(this.message, oauth2RevokeToken200Response.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Oauth2RevokeToken200ResponseDto {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

    private Oauth2RevokeToken200ResponseDto instance;

    public Builder() {
      this(new Oauth2RevokeToken200ResponseDto());
    }

    protected Builder(Oauth2RevokeToken200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Oauth2RevokeToken200ResponseDto value) { 
      this.instance.setMessage(value.message);
      return this;
    }

    public Oauth2RevokeToken200ResponseDto.Builder message(String message) {
      this.instance.message(message);
      return this;
    }
    
    /**
    * returns a built Oauth2RevokeToken200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public Oauth2RevokeToken200ResponseDto build() {
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
  public static Oauth2RevokeToken200ResponseDto.Builder builder() {
    return new Oauth2RevokeToken200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public Oauth2RevokeToken200ResponseDto.Builder toBuilder() {
    Oauth2RevokeToken200ResponseDto.Builder builder = new Oauth2RevokeToken200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

