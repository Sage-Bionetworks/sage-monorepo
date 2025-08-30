package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OAuth2AuthorizeResponseDto
 */

@JsonTypeName("OAuth2AuthorizeResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OAuth2AuthorizeResponseDto {

  private @Nullable URI authorizationUrl;

  private @Nullable String state;

  public OAuth2AuthorizeResponseDto authorizationUrl(@Nullable URI authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
    return this;
  }

  /**
   * URL to redirect user for OAuth2 authorization
   * @return authorizationUrl
   */
  @Valid 
  @Schema(name = "authorizationUrl", example = "https://accounts.google.com/oauth2/authorize?client_id=...", description = "URL to redirect user for OAuth2 authorization", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("authorizationUrl")
  public @Nullable URI getAuthorizationUrl() {
    return authorizationUrl;
  }

  public void setAuthorizationUrl(@Nullable URI authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
  }

  public OAuth2AuthorizeResponseDto state(@Nullable String state) {
    this.state = state;
    return this;
  }

  /**
   * State parameter to include in authorization request
   * @return state
   */
  
  @Schema(name = "state", example = "random_state_string", description = "State parameter to include in authorization request", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("state")
  public @Nullable String getState() {
    return state;
  }

  public void setState(@Nullable String state) {
    this.state = state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OAuth2AuthorizeResponseDto oauth2AuthorizeResponse = (OAuth2AuthorizeResponseDto) o;
    return Objects.equals(this.authorizationUrl, oauth2AuthorizeResponse.authorizationUrl) &&
        Objects.equals(this.state, oauth2AuthorizeResponse.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authorizationUrl, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OAuth2AuthorizeResponseDto {\n");
    sb.append("    authorizationUrl: ").append(toIndentedString(authorizationUrl)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

    private OAuth2AuthorizeResponseDto instance;

    public Builder() {
      this(new OAuth2AuthorizeResponseDto());
    }

    protected Builder(OAuth2AuthorizeResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OAuth2AuthorizeResponseDto value) { 
      this.instance.setAuthorizationUrl(value.authorizationUrl);
      this.instance.setState(value.state);
      return this;
    }

    public OAuth2AuthorizeResponseDto.Builder authorizationUrl(URI authorizationUrl) {
      this.instance.authorizationUrl(authorizationUrl);
      return this;
    }
    
    public OAuth2AuthorizeResponseDto.Builder state(String state) {
      this.instance.state(state);
      return this;
    }
    
    /**
    * returns a built OAuth2AuthorizeResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OAuth2AuthorizeResponseDto build() {
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
  public static OAuth2AuthorizeResponseDto.Builder builder() {
    return new OAuth2AuthorizeResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OAuth2AuthorizeResponseDto.Builder toBuilder() {
    OAuth2AuthorizeResponseDto.Builder builder = new OAuth2AuthorizeResponseDto.Builder();
    return builder.copyOf(this);
  }

}

