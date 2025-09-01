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
 * Oauth2Introspect200ResponseDto
 */

@JsonTypeName("oauth2Introspect_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class Oauth2Introspect200ResponseDto {

  private Boolean active;

  private @Nullable String scope;

  private @Nullable String clientId;

  private @Nullable String username;

  private @Nullable String tokenType;

  private @Nullable Integer exp;

  private @Nullable Integer iat;

  private @Nullable String sub;

  private @Nullable String aud;

  private @Nullable String iss;

  public Oauth2Introspect200ResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Oauth2Introspect200ResponseDto(Boolean active) {
    this.active = active;
  }

  public Oauth2Introspect200ResponseDto active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Whether the token is active
   * @return active
   */
  @NotNull 
  @Schema(name = "active", example = "true", description = "Whether the token is active", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Oauth2Introspect200ResponseDto scope(@Nullable String scope) {
    this.scope = scope;
    return this;
  }

  /**
   * Space-separated list of scopes
   * @return scope
   */
  
  @Schema(name = "scope", example = "read:org user:profile", description = "Space-separated list of scopes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scope")
  public @Nullable String getScope() {
    return scope;
  }

  public void setScope(@Nullable String scope) {
    this.scope = scope;
  }

  public Oauth2Introspect200ResponseDto clientId(@Nullable String clientId) {
    this.clientId = clientId;
    return this;
  }

  /**
   * Client identifier
   * @return clientId
   */
  
  @Schema(name = "client_id", example = "l238j323ds-23ij4", description = "Client identifier", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_id")
  public @Nullable String getClientId() {
    return clientId;
  }

  public void setClientId(@Nullable String clientId) {
    this.clientId = clientId;
  }

  public Oauth2Introspect200ResponseDto username(@Nullable String username) {
    this.username = username;
    return this;
  }

  /**
   * Human-readable identifier for the resource owner
   * @return username
   */
  
  @Schema(name = "username", example = "jdoe", description = "Human-readable identifier for the resource owner", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("username")
  public @Nullable String getUsername() {
    return username;
  }

  public void setUsername(@Nullable String username) {
    this.username = username;
  }

  public Oauth2Introspect200ResponseDto tokenType(@Nullable String tokenType) {
    this.tokenType = tokenType;
    return this;
  }

  /**
   * Type of the token
   * @return tokenType
   */
  
  @Schema(name = "token_type", example = "Bearer", description = "Type of the token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token_type")
  public @Nullable String getTokenType() {
    return tokenType;
  }

  public void setTokenType(@Nullable String tokenType) {
    this.tokenType = tokenType;
  }

  public Oauth2Introspect200ResponseDto exp(@Nullable Integer exp) {
    this.exp = exp;
    return this;
  }

  /**
   * Token expiration timestamp
   * @return exp
   */
  
  @Schema(name = "exp", example = "1419356238", description = "Token expiration timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("exp")
  public @Nullable Integer getExp() {
    return exp;
  }

  public void setExp(@Nullable Integer exp) {
    this.exp = exp;
  }

  public Oauth2Introspect200ResponseDto iat(@Nullable Integer iat) {
    this.iat = iat;
    return this;
  }

  /**
   * Token issued at timestamp
   * @return iat
   */
  
  @Schema(name = "iat", example = "1419350238", description = "Token issued at timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iat")
  public @Nullable Integer getIat() {
    return iat;
  }

  public void setIat(@Nullable Integer iat) {
    this.iat = iat;
  }

  public Oauth2Introspect200ResponseDto sub(@Nullable String sub) {
    this.sub = sub;
    return this;
  }

  /**
   * Subject of the token
   * @return sub
   */
  
  @Schema(name = "sub", example = "Z5O3upPC88QrAjx00dis", description = "Subject of the token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sub")
  public @Nullable String getSub() {
    return sub;
  }

  public void setSub(@Nullable String sub) {
    this.sub = sub;
  }

  public Oauth2Introspect200ResponseDto aud(@Nullable String aud) {
    this.aud = aud;
    return this;
  }

  /**
   * Intended audience
   * @return aud
   */
  
  @Schema(name = "aud", example = "https://protected.example.net/resource", description = "Intended audience", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("aud")
  public @Nullable String getAud() {
    return aud;
  }

  public void setAud(@Nullable String aud) {
    this.aud = aud;
  }

  public Oauth2Introspect200ResponseDto iss(@Nullable String iss) {
    this.iss = iss;
    return this;
  }

  /**
   * Token issuer
   * @return iss
   */
  
  @Schema(name = "iss", example = "https://server.example.com/", description = "Token issuer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iss")
  public @Nullable String getIss() {
    return iss;
  }

  public void setIss(@Nullable String iss) {
    this.iss = iss;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Oauth2Introspect200ResponseDto oauth2Introspect200Response = (Oauth2Introspect200ResponseDto) o;
    return Objects.equals(this.active, oauth2Introspect200Response.active) &&
        Objects.equals(this.scope, oauth2Introspect200Response.scope) &&
        Objects.equals(this.clientId, oauth2Introspect200Response.clientId) &&
        Objects.equals(this.username, oauth2Introspect200Response.username) &&
        Objects.equals(this.tokenType, oauth2Introspect200Response.tokenType) &&
        Objects.equals(this.exp, oauth2Introspect200Response.exp) &&
        Objects.equals(this.iat, oauth2Introspect200Response.iat) &&
        Objects.equals(this.sub, oauth2Introspect200Response.sub) &&
        Objects.equals(this.aud, oauth2Introspect200Response.aud) &&
        Objects.equals(this.iss, oauth2Introspect200Response.iss);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, scope, clientId, username, tokenType, exp, iat, sub, aud, iss);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Oauth2Introspect200ResponseDto {\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
    sb.append("    clientId: ").append(toIndentedString(clientId)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
    sb.append("    exp: ").append(toIndentedString(exp)).append("\n");
    sb.append("    iat: ").append(toIndentedString(iat)).append("\n");
    sb.append("    sub: ").append(toIndentedString(sub)).append("\n");
    sb.append("    aud: ").append(toIndentedString(aud)).append("\n");
    sb.append("    iss: ").append(toIndentedString(iss)).append("\n");
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

    private Oauth2Introspect200ResponseDto instance;

    public Builder() {
      this(new Oauth2Introspect200ResponseDto());
    }

    protected Builder(Oauth2Introspect200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Oauth2Introspect200ResponseDto value) { 
      this.instance.setActive(value.active);
      this.instance.setScope(value.scope);
      this.instance.setClientId(value.clientId);
      this.instance.setUsername(value.username);
      this.instance.setTokenType(value.tokenType);
      this.instance.setExp(value.exp);
      this.instance.setIat(value.iat);
      this.instance.setSub(value.sub);
      this.instance.setAud(value.aud);
      this.instance.setIss(value.iss);
      return this;
    }

    public Oauth2Introspect200ResponseDto.Builder active(Boolean active) {
      this.instance.active(active);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder scope(String scope) {
      this.instance.scope(scope);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder clientId(String clientId) {
      this.instance.clientId(clientId);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder username(String username) {
      this.instance.username(username);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder tokenType(String tokenType) {
      this.instance.tokenType(tokenType);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder exp(Integer exp) {
      this.instance.exp(exp);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder iat(Integer iat) {
      this.instance.iat(iat);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder sub(String sub) {
      this.instance.sub(sub);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder aud(String aud) {
      this.instance.aud(aud);
      return this;
    }
    
    public Oauth2Introspect200ResponseDto.Builder iss(String iss) {
      this.instance.iss(iss);
      return this;
    }
    
    /**
    * returns a built Oauth2Introspect200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public Oauth2Introspect200ResponseDto build() {
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
  public static Oauth2Introspect200ResponseDto.Builder builder() {
    return new Oauth2Introspect200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public Oauth2Introspect200ResponseDto.Builder toBuilder() {
    Oauth2Introspect200ResponseDto.Builder builder = new Oauth2Introspect200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

