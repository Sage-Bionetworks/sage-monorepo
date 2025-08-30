package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OAuth2AuthorizeRequestDto
 */

@JsonTypeName("OAuth2AuthorizeRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OAuth2AuthorizeRequestDto {

  /**
   * OAuth2 provider
   */
  public enum ProviderEnum {
    GOOGLE("google"),
    
    SYNAPSE("synapse");

    private final String value;

    ProviderEnum(String value) {
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
    public static ProviderEnum fromValue(String value) {
      for (ProviderEnum b : ProviderEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ProviderEnum provider;

  private URI redirectUri;

  private @Nullable String state;

  public OAuth2AuthorizeRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OAuth2AuthorizeRequestDto(ProviderEnum provider, URI redirectUri) {
    this.provider = provider;
    this.redirectUri = redirectUri;
  }

  public OAuth2AuthorizeRequestDto provider(ProviderEnum provider) {
    this.provider = provider;
    return this;
  }

  /**
   * OAuth2 provider
   * @return provider
   */
  @NotNull 
  @Schema(name = "provider", example = "google", description = "OAuth2 provider", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("provider")
  public ProviderEnum getProvider() {
    return provider;
  }

  public void setProvider(ProviderEnum provider) {
    this.provider = provider;
  }

  public OAuth2AuthorizeRequestDto redirectUri(URI redirectUri) {
    this.redirectUri = redirectUri;
    return this;
  }

  /**
   * Client redirect URI after OAuth2 completion
   * @return redirectUri
   */
  @NotNull @Valid 
  @Schema(name = "redirectUri", example = "https://openchallenges.io/auth/callback", description = "Client redirect URI after OAuth2 completion", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("redirectUri")
  public URI getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(URI redirectUri) {
    this.redirectUri = redirectUri;
  }

  public OAuth2AuthorizeRequestDto state(@Nullable String state) {
    this.state = state;
    return this;
  }

  /**
   * State parameter for CSRF protection (optional)
   * @return state
   */
  
  @Schema(name = "state", example = "random_state_string", description = "State parameter for CSRF protection (optional)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    OAuth2AuthorizeRequestDto oauth2AuthorizeRequest = (OAuth2AuthorizeRequestDto) o;
    return Objects.equals(this.provider, oauth2AuthorizeRequest.provider) &&
        Objects.equals(this.redirectUri, oauth2AuthorizeRequest.redirectUri) &&
        Objects.equals(this.state, oauth2AuthorizeRequest.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(provider, redirectUri, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OAuth2AuthorizeRequestDto {\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    redirectUri: ").append(toIndentedString(redirectUri)).append("\n");
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

    private OAuth2AuthorizeRequestDto instance;

    public Builder() {
      this(new OAuth2AuthorizeRequestDto());
    }

    protected Builder(OAuth2AuthorizeRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OAuth2AuthorizeRequestDto value) { 
      this.instance.setProvider(value.provider);
      this.instance.setRedirectUri(value.redirectUri);
      this.instance.setState(value.state);
      return this;
    }

    public OAuth2AuthorizeRequestDto.Builder provider(ProviderEnum provider) {
      this.instance.provider(provider);
      return this;
    }
    
    public OAuth2AuthorizeRequestDto.Builder redirectUri(URI redirectUri) {
      this.instance.redirectUri(redirectUri);
      return this;
    }
    
    public OAuth2AuthorizeRequestDto.Builder state(String state) {
      this.instance.state(state);
      return this;
    }
    
    /**
    * returns a built OAuth2AuthorizeRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OAuth2AuthorizeRequestDto build() {
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
  public static OAuth2AuthorizeRequestDto.Builder builder() {
    return new OAuth2AuthorizeRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OAuth2AuthorizeRequestDto.Builder toBuilder() {
    OAuth2AuthorizeRequestDto.Builder builder = new OAuth2AuthorizeRequestDto.Builder();
    return builder.copyOf(this);
  }

}

