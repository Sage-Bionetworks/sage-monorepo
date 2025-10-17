package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * GetJwks200ResponseDto
 */

@JsonTypeName("getJwks_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GetJwks200ResponseDto {

  @Valid
  private List<Object> keys = new ArrayList<>();

  public GetJwks200ResponseDto keys(List<Object> keys) {
    this.keys = keys;
    return this;
  }

  public GetJwks200ResponseDto addKeysItem(Object keysItem) {
    if (this.keys == null) {
      this.keys = new ArrayList<>();
    }
    this.keys.add(keysItem);
    return this;
  }

  /**
   * Get keys
   * @return keys
   */
  
  @Schema(name = "keys", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("keys")
  public List<Object> getKeys() {
    return keys;
  }

  public void setKeys(List<Object> keys) {
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
    GetJwks200ResponseDto getJwks200Response = (GetJwks200ResponseDto) o;
    return Objects.equals(this.keys, getJwks200Response.keys);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keys);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetJwks200ResponseDto {\n");
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

    private GetJwks200ResponseDto instance;

    public Builder() {
      this(new GetJwks200ResponseDto());
    }

    protected Builder(GetJwks200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GetJwks200ResponseDto value) { 
      this.instance.setKeys(value.keys);
      return this;
    }

    public GetJwks200ResponseDto.Builder keys(List<Object> keys) {
      this.instance.keys(keys);
      return this;
    }
    
    /**
    * returns a built GetJwks200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GetJwks200ResponseDto build() {
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
  public static GetJwks200ResponseDto.Builder builder() {
    return new GetJwks200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GetJwks200ResponseDto.Builder toBuilder() {
    GetJwks200ResponseDto.Builder builder = new GetJwks200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

