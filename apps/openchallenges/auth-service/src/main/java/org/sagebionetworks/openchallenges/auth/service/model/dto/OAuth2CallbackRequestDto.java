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
 * OAuth2CallbackRequestDto
 */

@JsonTypeName("OAuth2CallbackRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OAuth2CallbackRequestDto {

  private String code;

  private String state;

  private @Nullable URI redirectUri;

  public OAuth2CallbackRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OAuth2CallbackRequestDto(String code, String state) {
    this.code = code;
    this.state = state;
  }

  public OAuth2CallbackRequestDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Authorization code from OAuth2 provider
   * @return code
   */
  @NotNull 
  @Schema(name = "code", example = "4/0AX4XfWjYZ1234567890abcdef", description = "Authorization code from OAuth2 provider", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public OAuth2CallbackRequestDto state(String state) {
    this.state = state;
    return this;
  }

  /**
   * State parameter for verification
   * @return state
   */
  @NotNull 
  @Schema(name = "state", example = "random_state_string", description = "State parameter for verification", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("state")
  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public OAuth2CallbackRequestDto redirectUri(@Nullable URI redirectUri) {
    this.redirectUri = redirectUri;
    return this;
  }

  /**
   * Original redirect URI used in authorization request
   * @return redirectUri
   */
  @Valid 
  @Schema(name = "redirectUri", example = "https://openchallenges.io/auth/callback", description = "Original redirect URI used in authorization request", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("redirectUri")
  public @Nullable URI getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(@Nullable URI redirectUri) {
    this.redirectUri = redirectUri;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OAuth2CallbackRequestDto oauth2CallbackRequest = (OAuth2CallbackRequestDto) o;
    return Objects.equals(this.code, oauth2CallbackRequest.code) &&
        Objects.equals(this.state, oauth2CallbackRequest.state) &&
        Objects.equals(this.redirectUri, oauth2CallbackRequest.redirectUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, state, redirectUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OAuth2CallbackRequestDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    redirectUri: ").append(toIndentedString(redirectUri)).append("\n");
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

    private OAuth2CallbackRequestDto instance;

    public Builder() {
      this(new OAuth2CallbackRequestDto());
    }

    protected Builder(OAuth2CallbackRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OAuth2CallbackRequestDto value) { 
      this.instance.setCode(value.code);
      this.instance.setState(value.state);
      this.instance.setRedirectUri(value.redirectUri);
      return this;
    }

    public OAuth2CallbackRequestDto.Builder code(String code) {
      this.instance.code(code);
      return this;
    }
    
    public OAuth2CallbackRequestDto.Builder state(String state) {
      this.instance.state(state);
      return this;
    }
    
    public OAuth2CallbackRequestDto.Builder redirectUri(URI redirectUri) {
      this.instance.redirectUri(redirectUri);
      return this;
    }
    
    /**
    * returns a built OAuth2CallbackRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OAuth2CallbackRequestDto build() {
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
  public static OAuth2CallbackRequestDto.Builder builder() {
    return new OAuth2CallbackRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OAuth2CallbackRequestDto.Builder toBuilder() {
    OAuth2CallbackRequestDto.Builder builder = new OAuth2CallbackRequestDto.Builder();
    return builder.copyOf(this);
  }

}

