package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2JwksJson200ResponseKeysInnerDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Oauth2JwksJson200ResponseDto
 */

@JsonTypeName("oauth2JwksJson_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class Oauth2JwksJson200ResponseDto {

  @Valid
  private List<@Valid Oauth2JwksJson200ResponseKeysInnerDto> keys = new ArrayList<>();

  public Oauth2JwksJson200ResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Oauth2JwksJson200ResponseDto(List<@Valid Oauth2JwksJson200ResponseKeysInnerDto> keys) {
    this.keys = keys;
  }

  public Oauth2JwksJson200ResponseDto keys(List<@Valid Oauth2JwksJson200ResponseKeysInnerDto> keys) {
    this.keys = keys;
    return this;
  }

  public Oauth2JwksJson200ResponseDto addKeysItem(Oauth2JwksJson200ResponseKeysInnerDto keysItem) {
    if (this.keys == null) {
      this.keys = new ArrayList<>();
    }
    this.keys.add(keysItem);
    return this;
  }

  /**
   * Array of JSON Web Keys
   * @return keys
   */
  @NotNull @Valid 
  @Schema(name = "keys", description = "Array of JSON Web Keys", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("keys")
  public List<@Valid Oauth2JwksJson200ResponseKeysInnerDto> getKeys() {
    return keys;
  }

  public void setKeys(List<@Valid Oauth2JwksJson200ResponseKeysInnerDto> keys) {
    this.keys = keys;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Oauth2JwksJson200ResponseDto oauth2JwksJson200Response = (Oauth2JwksJson200ResponseDto) o;
    return Objects.equals(this.keys, oauth2JwksJson200Response.keys);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keys);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Oauth2JwksJson200ResponseDto {\n");
    sb.append("    keys: ").append(toIndentedString(keys)).append("\n");
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

    private Oauth2JwksJson200ResponseDto instance;

    public Builder() {
      this(new Oauth2JwksJson200ResponseDto());
    }

    protected Builder(Oauth2JwksJson200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Oauth2JwksJson200ResponseDto value) { 
      this.instance.setKeys(value.keys);
      return this;
    }

    public Oauth2JwksJson200ResponseDto.Builder keys(List<Oauth2JwksJson200ResponseKeysInnerDto> keys) {
      this.instance.keys(keys);
      return this;
    }
    
    /**
    * returns a built Oauth2JwksJson200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public Oauth2JwksJson200ResponseDto build() {
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
  public static Oauth2JwksJson200ResponseDto.Builder builder() {
    return new Oauth2JwksJson200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public Oauth2JwksJson200ResponseDto.Builder toBuilder() {
    Oauth2JwksJson200ResponseDto.Builder builder = new Oauth2JwksJson200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

