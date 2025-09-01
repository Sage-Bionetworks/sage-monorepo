package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Oauth2WellKnownOpenidConfiguration200ResponseDto
 */

@JsonTypeName("oauth2WellKnownOpenidConfiguration_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class Oauth2WellKnownOpenidConfiguration200ResponseDto {

  private URI issuer;

  private URI authorizationEndpoint;

  private URI tokenEndpoint;

  private @Nullable URI userinfoEndpoint;

  private @Nullable URI jwksUri;

  @Valid
  private List<String> scopesSupported = new ArrayList<>();

  @Valid
  private List<String> responseTypesSupported = new ArrayList<>();

  @Valid
  private List<String> grantTypesSupported = new ArrayList<>();

  @Valid
  private List<String> subjectTypesSupported = new ArrayList<>();

  @Valid
  private List<String> idTokenSigningAlgValuesSupported = new ArrayList<>();

  private @Nullable URI revocationEndpoint;

  private @Nullable URI introspectionEndpoint;

  @Valid
  private List<String> codeChallengeMethodsSupported = new ArrayList<>();

  @Valid
  private List<String> tokenEndpointAuthMethodsSupported = new ArrayList<>();

  public Oauth2WellKnownOpenidConfiguration200ResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Oauth2WellKnownOpenidConfiguration200ResponseDto(URI issuer, URI authorizationEndpoint, URI tokenEndpoint, List<String> responseTypesSupported, List<String> subjectTypesSupported, List<String> idTokenSigningAlgValuesSupported) {
    this.issuer = issuer;
    this.authorizationEndpoint = authorizationEndpoint;
    this.tokenEndpoint = tokenEndpoint;
    this.responseTypesSupported = responseTypesSupported;
    this.subjectTypesSupported = subjectTypesSupported;
    this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto issuer(URI issuer) {
    this.issuer = issuer;
    return this;
  }

  /**
   * Authorization server's issuer identifier
   * @return issuer
   */
  @NotNull @Valid 
  @Schema(name = "issuer", example = "https://api.openchallenges.io", description = "Authorization server's issuer identifier", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("issuer")
  public URI getIssuer() {
    return issuer;
  }

  public void setIssuer(URI issuer) {
    this.issuer = issuer;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto authorizationEndpoint(URI authorizationEndpoint) {
    this.authorizationEndpoint = authorizationEndpoint;
    return this;
  }

  /**
   * Authorization endpoint URL
   * @return authorizationEndpoint
   */
  @NotNull @Valid 
  @Schema(name = "authorization_endpoint", example = "https://api.openchallenges.io/oauth2/authorize", description = "Authorization endpoint URL", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("authorization_endpoint")
  public URI getAuthorizationEndpoint() {
    return authorizationEndpoint;
  }

  public void setAuthorizationEndpoint(URI authorizationEndpoint) {
    this.authorizationEndpoint = authorizationEndpoint;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto tokenEndpoint(URI tokenEndpoint) {
    this.tokenEndpoint = tokenEndpoint;
    return this;
  }

  /**
   * Token endpoint URL
   * @return tokenEndpoint
   */
  @NotNull @Valid 
  @Schema(name = "token_endpoint", example = "https://api.openchallenges.io/oauth2/token", description = "Token endpoint URL", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("token_endpoint")
  public URI getTokenEndpoint() {
    return tokenEndpoint;
  }

  public void setTokenEndpoint(URI tokenEndpoint) {
    this.tokenEndpoint = tokenEndpoint;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto userinfoEndpoint(@Nullable URI userinfoEndpoint) {
    this.userinfoEndpoint = userinfoEndpoint;
    return this;
  }

  /**
   * UserInfo endpoint URL
   * @return userinfoEndpoint
   */
  @Valid 
  @Schema(name = "userinfo_endpoint", example = "https://api.openchallenges.io/oauth2/userinfo", description = "UserInfo endpoint URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userinfo_endpoint")
  public @Nullable URI getUserinfoEndpoint() {
    return userinfoEndpoint;
  }

  public void setUserinfoEndpoint(@Nullable URI userinfoEndpoint) {
    this.userinfoEndpoint = userinfoEndpoint;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto jwksUri(@Nullable URI jwksUri) {
    this.jwksUri = jwksUri;
    return this;
  }

  /**
   * JSON Web Key Set document URL
   * @return jwksUri
   */
  @Valid 
  @Schema(name = "jwks_uri", example = "https://api.openchallenges.io/.well-known/jwks.json", description = "JSON Web Key Set document URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("jwks_uri")
  public @Nullable URI getJwksUri() {
    return jwksUri;
  }

  public void setJwksUri(@Nullable URI jwksUri) {
    this.jwksUri = jwksUri;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto scopesSupported(List<String> scopesSupported) {
    this.scopesSupported = scopesSupported;
    return this;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto addScopesSupportedItem(String scopesSupportedItem) {
    if (this.scopesSupported == null) {
      this.scopesSupported = new ArrayList<>();
    }
    this.scopesSupported.add(scopesSupportedItem);
    return this;
  }

  /**
   * Supported OAuth2 scopes
   * @return scopesSupported
   */
  
  @Schema(name = "scopes_supported", example = "[\"openid\",\"profile\",\"email\",\"user:profile\",\"read:org\",\"write:org\"]", description = "Supported OAuth2 scopes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scopes_supported")
  public List<String> getScopesSupported() {
    return scopesSupported;
  }

  public void setScopesSupported(List<String> scopesSupported) {
    this.scopesSupported = scopesSupported;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto responseTypesSupported(List<String> responseTypesSupported) {
    this.responseTypesSupported = responseTypesSupported;
    return this;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto addResponseTypesSupportedItem(String responseTypesSupportedItem) {
    if (this.responseTypesSupported == null) {
      this.responseTypesSupported = new ArrayList<>();
    }
    this.responseTypesSupported.add(responseTypesSupportedItem);
    return this;
  }

  /**
   * Supported OAuth2 response types
   * @return responseTypesSupported
   */
  @NotNull 
  @Schema(name = "response_types_supported", example = "[\"code\",\"token\",\"id_token\"]", description = "Supported OAuth2 response types", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("response_types_supported")
  public List<String> getResponseTypesSupported() {
    return responseTypesSupported;
  }

  public void setResponseTypesSupported(List<String> responseTypesSupported) {
    this.responseTypesSupported = responseTypesSupported;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto grantTypesSupported(List<String> grantTypesSupported) {
    this.grantTypesSupported = grantTypesSupported;
    return this;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto addGrantTypesSupportedItem(String grantTypesSupportedItem) {
    if (this.grantTypesSupported == null) {
      this.grantTypesSupported = new ArrayList<>();
    }
    this.grantTypesSupported.add(grantTypesSupportedItem);
    return this;
  }

  /**
   * Supported OAuth2 grant types
   * @return grantTypesSupported
   */
  
  @Schema(name = "grant_types_supported", example = "[\"authorization_code\",\"refresh_token\",\"client_credentials\"]", description = "Supported OAuth2 grant types", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("grant_types_supported")
  public List<String> getGrantTypesSupported() {
    return grantTypesSupported;
  }

  public void setGrantTypesSupported(List<String> grantTypesSupported) {
    this.grantTypesSupported = grantTypesSupported;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto subjectTypesSupported(List<String> subjectTypesSupported) {
    this.subjectTypesSupported = subjectTypesSupported;
    return this;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto addSubjectTypesSupportedItem(String subjectTypesSupportedItem) {
    if (this.subjectTypesSupported == null) {
      this.subjectTypesSupported = new ArrayList<>();
    }
    this.subjectTypesSupported.add(subjectTypesSupportedItem);
    return this;
  }

  /**
   * Supported subject identifier types
   * @return subjectTypesSupported
   */
  @NotNull 
  @Schema(name = "subject_types_supported", example = "[\"public\"]", description = "Supported subject identifier types", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("subject_types_supported")
  public List<String> getSubjectTypesSupported() {
    return subjectTypesSupported;
  }

  public void setSubjectTypesSupported(List<String> subjectTypesSupported) {
    this.subjectTypesSupported = subjectTypesSupported;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto idTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) {
    this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
    return this;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto addIdTokenSigningAlgValuesSupportedItem(String idTokenSigningAlgValuesSupportedItem) {
    if (this.idTokenSigningAlgValuesSupported == null) {
      this.idTokenSigningAlgValuesSupported = new ArrayList<>();
    }
    this.idTokenSigningAlgValuesSupported.add(idTokenSigningAlgValuesSupportedItem);
    return this;
  }

  /**
   * Supported ID token signing algorithms
   * @return idTokenSigningAlgValuesSupported
   */
  @NotNull 
  @Schema(name = "id_token_signing_alg_values_supported", example = "[\"RS256\",\"HS256\"]", description = "Supported ID token signing algorithms", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id_token_signing_alg_values_supported")
  public List<String> getIdTokenSigningAlgValuesSupported() {
    return idTokenSigningAlgValuesSupported;
  }

  public void setIdTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) {
    this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto revocationEndpoint(@Nullable URI revocationEndpoint) {
    this.revocationEndpoint = revocationEndpoint;
    return this;
  }

  /**
   * Token revocation endpoint URL
   * @return revocationEndpoint
   */
  @Valid 
  @Schema(name = "revocation_endpoint", example = "https://api.openchallenges.io/oauth2/revoke", description = "Token revocation endpoint URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revocation_endpoint")
  public @Nullable URI getRevocationEndpoint() {
    return revocationEndpoint;
  }

  public void setRevocationEndpoint(@Nullable URI revocationEndpoint) {
    this.revocationEndpoint = revocationEndpoint;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto introspectionEndpoint(@Nullable URI introspectionEndpoint) {
    this.introspectionEndpoint = introspectionEndpoint;
    return this;
  }

  /**
   * Token introspection endpoint URL
   * @return introspectionEndpoint
   */
  @Valid 
  @Schema(name = "introspection_endpoint", example = "https://api.openchallenges.io/oauth2/introspect", description = "Token introspection endpoint URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("introspection_endpoint")
  public @Nullable URI getIntrospectionEndpoint() {
    return introspectionEndpoint;
  }

  public void setIntrospectionEndpoint(@Nullable URI introspectionEndpoint) {
    this.introspectionEndpoint = introspectionEndpoint;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto codeChallengeMethodsSupported(List<String> codeChallengeMethodsSupported) {
    this.codeChallengeMethodsSupported = codeChallengeMethodsSupported;
    return this;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto addCodeChallengeMethodsSupportedItem(String codeChallengeMethodsSupportedItem) {
    if (this.codeChallengeMethodsSupported == null) {
      this.codeChallengeMethodsSupported = new ArrayList<>();
    }
    this.codeChallengeMethodsSupported.add(codeChallengeMethodsSupportedItem);
    return this;
  }

  /**
   * Supported PKCE code challenge methods
   * @return codeChallengeMethodsSupported
   */
  
  @Schema(name = "code_challenge_methods_supported", example = "[\"S256\",\"plain\"]", description = "Supported PKCE code challenge methods", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code_challenge_methods_supported")
  public List<String> getCodeChallengeMethodsSupported() {
    return codeChallengeMethodsSupported;
  }

  public void setCodeChallengeMethodsSupported(List<String> codeChallengeMethodsSupported) {
    this.codeChallengeMethodsSupported = codeChallengeMethodsSupported;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto tokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
    this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
    return this;
  }

  public Oauth2WellKnownOpenidConfiguration200ResponseDto addTokenEndpointAuthMethodsSupportedItem(String tokenEndpointAuthMethodsSupportedItem) {
    if (this.tokenEndpointAuthMethodsSupported == null) {
      this.tokenEndpointAuthMethodsSupported = new ArrayList<>();
    }
    this.tokenEndpointAuthMethodsSupported.add(tokenEndpointAuthMethodsSupportedItem);
    return this;
  }

  /**
   * Supported client authentication methods at token endpoint
   * @return tokenEndpointAuthMethodsSupported
   */
  
  @Schema(name = "token_endpoint_auth_methods_supported", example = "[\"client_secret_basic\",\"client_secret_post\"]", description = "Supported client authentication methods at token endpoint", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token_endpoint_auth_methods_supported")
  public List<String> getTokenEndpointAuthMethodsSupported() {
    return tokenEndpointAuthMethodsSupported;
  }

  public void setTokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
    this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Oauth2WellKnownOpenidConfiguration200ResponseDto oauth2WellKnownOpenidConfiguration200Response = (Oauth2WellKnownOpenidConfiguration200ResponseDto) o;
    return Objects.equals(this.issuer, oauth2WellKnownOpenidConfiguration200Response.issuer) &&
        Objects.equals(this.authorizationEndpoint, oauth2WellKnownOpenidConfiguration200Response.authorizationEndpoint) &&
        Objects.equals(this.tokenEndpoint, oauth2WellKnownOpenidConfiguration200Response.tokenEndpoint) &&
        Objects.equals(this.userinfoEndpoint, oauth2WellKnownOpenidConfiguration200Response.userinfoEndpoint) &&
        Objects.equals(this.jwksUri, oauth2WellKnownOpenidConfiguration200Response.jwksUri) &&
        Objects.equals(this.scopesSupported, oauth2WellKnownOpenidConfiguration200Response.scopesSupported) &&
        Objects.equals(this.responseTypesSupported, oauth2WellKnownOpenidConfiguration200Response.responseTypesSupported) &&
        Objects.equals(this.grantTypesSupported, oauth2WellKnownOpenidConfiguration200Response.grantTypesSupported) &&
        Objects.equals(this.subjectTypesSupported, oauth2WellKnownOpenidConfiguration200Response.subjectTypesSupported) &&
        Objects.equals(this.idTokenSigningAlgValuesSupported, oauth2WellKnownOpenidConfiguration200Response.idTokenSigningAlgValuesSupported) &&
        Objects.equals(this.revocationEndpoint, oauth2WellKnownOpenidConfiguration200Response.revocationEndpoint) &&
        Objects.equals(this.introspectionEndpoint, oauth2WellKnownOpenidConfiguration200Response.introspectionEndpoint) &&
        Objects.equals(this.codeChallengeMethodsSupported, oauth2WellKnownOpenidConfiguration200Response.codeChallengeMethodsSupported) &&
        Objects.equals(this.tokenEndpointAuthMethodsSupported, oauth2WellKnownOpenidConfiguration200Response.tokenEndpointAuthMethodsSupported);
  }

  @Override
  public int hashCode() {
    return Objects.hash(issuer, authorizationEndpoint, tokenEndpoint, userinfoEndpoint, jwksUri, scopesSupported, responseTypesSupported, grantTypesSupported, subjectTypesSupported, idTokenSigningAlgValuesSupported, revocationEndpoint, introspectionEndpoint, codeChallengeMethodsSupported, tokenEndpointAuthMethodsSupported);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Oauth2WellKnownOpenidConfiguration200ResponseDto {\n");
    sb.append("    issuer: ").append(toIndentedString(issuer)).append("\n");
    sb.append("    authorizationEndpoint: ").append(toIndentedString(authorizationEndpoint)).append("\n");
    sb.append("    tokenEndpoint: ").append(toIndentedString(tokenEndpoint)).append("\n");
    sb.append("    userinfoEndpoint: ").append(toIndentedString(userinfoEndpoint)).append("\n");
    sb.append("    jwksUri: ").append(toIndentedString(jwksUri)).append("\n");
    sb.append("    scopesSupported: ").append(toIndentedString(scopesSupported)).append("\n");
    sb.append("    responseTypesSupported: ").append(toIndentedString(responseTypesSupported)).append("\n");
    sb.append("    grantTypesSupported: ").append(toIndentedString(grantTypesSupported)).append("\n");
    sb.append("    subjectTypesSupported: ").append(toIndentedString(subjectTypesSupported)).append("\n");
    sb.append("    idTokenSigningAlgValuesSupported: ").append(toIndentedString(idTokenSigningAlgValuesSupported)).append("\n");
    sb.append("    revocationEndpoint: ").append(toIndentedString(revocationEndpoint)).append("\n");
    sb.append("    introspectionEndpoint: ").append(toIndentedString(introspectionEndpoint)).append("\n");
    sb.append("    codeChallengeMethodsSupported: ").append(toIndentedString(codeChallengeMethodsSupported)).append("\n");
    sb.append("    tokenEndpointAuthMethodsSupported: ").append(toIndentedString(tokenEndpointAuthMethodsSupported)).append("\n");
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

    private Oauth2WellKnownOpenidConfiguration200ResponseDto instance;

    public Builder() {
      this(new Oauth2WellKnownOpenidConfiguration200ResponseDto());
    }

    protected Builder(Oauth2WellKnownOpenidConfiguration200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Oauth2WellKnownOpenidConfiguration200ResponseDto value) { 
      this.instance.setIssuer(value.issuer);
      this.instance.setAuthorizationEndpoint(value.authorizationEndpoint);
      this.instance.setTokenEndpoint(value.tokenEndpoint);
      this.instance.setUserinfoEndpoint(value.userinfoEndpoint);
      this.instance.setJwksUri(value.jwksUri);
      this.instance.setScopesSupported(value.scopesSupported);
      this.instance.setResponseTypesSupported(value.responseTypesSupported);
      this.instance.setGrantTypesSupported(value.grantTypesSupported);
      this.instance.setSubjectTypesSupported(value.subjectTypesSupported);
      this.instance.setIdTokenSigningAlgValuesSupported(value.idTokenSigningAlgValuesSupported);
      this.instance.setRevocationEndpoint(value.revocationEndpoint);
      this.instance.setIntrospectionEndpoint(value.introspectionEndpoint);
      this.instance.setCodeChallengeMethodsSupported(value.codeChallengeMethodsSupported);
      this.instance.setTokenEndpointAuthMethodsSupported(value.tokenEndpointAuthMethodsSupported);
      return this;
    }

    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder issuer(URI issuer) {
      this.instance.issuer(issuer);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder authorizationEndpoint(URI authorizationEndpoint) {
      this.instance.authorizationEndpoint(authorizationEndpoint);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder tokenEndpoint(URI tokenEndpoint) {
      this.instance.tokenEndpoint(tokenEndpoint);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder userinfoEndpoint(URI userinfoEndpoint) {
      this.instance.userinfoEndpoint(userinfoEndpoint);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder jwksUri(URI jwksUri) {
      this.instance.jwksUri(jwksUri);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder scopesSupported(List<String> scopesSupported) {
      this.instance.scopesSupported(scopesSupported);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder responseTypesSupported(List<String> responseTypesSupported) {
      this.instance.responseTypesSupported(responseTypesSupported);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder grantTypesSupported(List<String> grantTypesSupported) {
      this.instance.grantTypesSupported(grantTypesSupported);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder subjectTypesSupported(List<String> subjectTypesSupported) {
      this.instance.subjectTypesSupported(subjectTypesSupported);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder idTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) {
      this.instance.idTokenSigningAlgValuesSupported(idTokenSigningAlgValuesSupported);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder revocationEndpoint(URI revocationEndpoint) {
      this.instance.revocationEndpoint(revocationEndpoint);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder introspectionEndpoint(URI introspectionEndpoint) {
      this.instance.introspectionEndpoint(introspectionEndpoint);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder codeChallengeMethodsSupported(List<String> codeChallengeMethodsSupported) {
      this.instance.codeChallengeMethodsSupported(codeChallengeMethodsSupported);
      return this;
    }
    
    public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder tokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
      this.instance.tokenEndpointAuthMethodsSupported(tokenEndpointAuthMethodsSupported);
      return this;
    }
    
    /**
    * returns a built Oauth2WellKnownOpenidConfiguration200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public Oauth2WellKnownOpenidConfiguration200ResponseDto build() {
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
  public static Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder builder() {
    return new Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder toBuilder() {
    Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder builder = new Oauth2WellKnownOpenidConfiguration200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

