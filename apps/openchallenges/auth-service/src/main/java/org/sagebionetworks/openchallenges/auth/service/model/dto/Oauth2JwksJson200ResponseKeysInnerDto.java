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
 * Oauth2JwksJson200ResponseKeysInnerDto
 */

@JsonTypeName("oauth2JwksJson_200_response_keys_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class Oauth2JwksJson200ResponseKeysInnerDto {

  private String kty;

  private String use;

  private String kid;

  private String alg;

  private @Nullable String n;

  private @Nullable String e;

  public Oauth2JwksJson200ResponseKeysInnerDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Oauth2JwksJson200ResponseKeysInnerDto(String kty, String use, String kid, String alg) {
    this.kty = kty;
    this.use = use;
    this.kid = kid;
    this.alg = alg;
  }

  public Oauth2JwksJson200ResponseKeysInnerDto kty(String kty) {
    this.kty = kty;
    return this;
  }

  /**
   * Key type
   * @return kty
   */
  @NotNull 
  @Schema(name = "kty", example = "RSA", description = "Key type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("kty")
  public String getKty() {
    return kty;
  }

  public void setKty(String kty) {
    this.kty = kty;
  }

  public Oauth2JwksJson200ResponseKeysInnerDto use(String use) {
    this.use = use;
    return this;
  }

  /**
   * Public key use
   * @return use
   */
  @NotNull 
  @Schema(name = "use", example = "sig", description = "Public key use", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("use")
  public String getUse() {
    return use;
  }

  public void setUse(String use) {
    this.use = use;
  }

  public Oauth2JwksJson200ResponseKeysInnerDto kid(String kid) {
    this.kid = kid;
    return this;
  }

  /**
   * Key ID
   * @return kid
   */
  @NotNull 
  @Schema(name = "kid", example = "2011-04-29", description = "Key ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("kid")
  public String getKid() {
    return kid;
  }

  public void setKid(String kid) {
    this.kid = kid;
  }

  public Oauth2JwksJson200ResponseKeysInnerDto alg(String alg) {
    this.alg = alg;
    return this;
  }

  /**
   * Algorithm
   * @return alg
   */
  @NotNull 
  @Schema(name = "alg", example = "RS256", description = "Algorithm", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("alg")
  public String getAlg() {
    return alg;
  }

  public void setAlg(String alg) {
    this.alg = alg;
  }

  public Oauth2JwksJson200ResponseKeysInnerDto n(@Nullable String n) {
    this.n = n;
    return this;
  }

  /**
   * RSA modulus
   * @return n
   */
  
  @Schema(name = "n", example = "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbIS", description = "RSA modulus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("n")
  public @Nullable String getN() {
    return n;
  }

  public void setN(@Nullable String n) {
    this.n = n;
  }

  public Oauth2JwksJson200ResponseKeysInnerDto e(@Nullable String e) {
    this.e = e;
    return this;
  }

  /**
   * RSA exponent
   * @return e
   */
  
  @Schema(name = "e", example = "AQAB", description = "RSA exponent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("e")
  public @Nullable String getE() {
    return e;
  }

  public void setE(@Nullable String e) {
    this.e = e;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Oauth2JwksJson200ResponseKeysInnerDto oauth2JwksJson200ResponseKeysInner = (Oauth2JwksJson200ResponseKeysInnerDto) o;
    return Objects.equals(this.kty, oauth2JwksJson200ResponseKeysInner.kty) &&
        Objects.equals(this.use, oauth2JwksJson200ResponseKeysInner.use) &&
        Objects.equals(this.kid, oauth2JwksJson200ResponseKeysInner.kid) &&
        Objects.equals(this.alg, oauth2JwksJson200ResponseKeysInner.alg) &&
        Objects.equals(this.n, oauth2JwksJson200ResponseKeysInner.n) &&
        Objects.equals(this.e, oauth2JwksJson200ResponseKeysInner.e);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kty, use, kid, alg, n, e);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Oauth2JwksJson200ResponseKeysInnerDto {\n");
    sb.append("    kty: ").append(toIndentedString(kty)).append("\n");
    sb.append("    use: ").append(toIndentedString(use)).append("\n");
    sb.append("    kid: ").append(toIndentedString(kid)).append("\n");
    sb.append("    alg: ").append(toIndentedString(alg)).append("\n");
    sb.append("    n: ").append(toIndentedString(n)).append("\n");
    sb.append("    e: ").append(toIndentedString(e)).append("\n");
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

    private Oauth2JwksJson200ResponseKeysInnerDto instance;

    public Builder() {
      this(new Oauth2JwksJson200ResponseKeysInnerDto());
    }

    protected Builder(Oauth2JwksJson200ResponseKeysInnerDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Oauth2JwksJson200ResponseKeysInnerDto value) { 
      this.instance.setKty(value.kty);
      this.instance.setUse(value.use);
      this.instance.setKid(value.kid);
      this.instance.setAlg(value.alg);
      this.instance.setN(value.n);
      this.instance.setE(value.e);
      return this;
    }

    public Oauth2JwksJson200ResponseKeysInnerDto.Builder kty(String kty) {
      this.instance.kty(kty);
      return this;
    }
    
    public Oauth2JwksJson200ResponseKeysInnerDto.Builder use(String use) {
      this.instance.use(use);
      return this;
    }
    
    public Oauth2JwksJson200ResponseKeysInnerDto.Builder kid(String kid) {
      this.instance.kid(kid);
      return this;
    }
    
    public Oauth2JwksJson200ResponseKeysInnerDto.Builder alg(String alg) {
      this.instance.alg(alg);
      return this;
    }
    
    public Oauth2JwksJson200ResponseKeysInnerDto.Builder n(String n) {
      this.instance.n(n);
      return this;
    }
    
    public Oauth2JwksJson200ResponseKeysInnerDto.Builder e(String e) {
      this.instance.e(e);
      return this;
    }
    
    /**
    * returns a built Oauth2JwksJson200ResponseKeysInnerDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public Oauth2JwksJson200ResponseKeysInnerDto build() {
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
  public static Oauth2JwksJson200ResponseKeysInnerDto.Builder builder() {
    return new Oauth2JwksJson200ResponseKeysInnerDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public Oauth2JwksJson200ResponseKeysInnerDto.Builder toBuilder() {
    Oauth2JwksJson200ResponseKeysInnerDto.Builder builder = new Oauth2JwksJson200ResponseKeysInnerDto.Builder();
    return builder.copyOf(this);
  }

}

