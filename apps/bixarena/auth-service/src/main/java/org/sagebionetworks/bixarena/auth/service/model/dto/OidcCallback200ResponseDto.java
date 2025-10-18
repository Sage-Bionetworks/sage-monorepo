package org.sagebionetworks.bixarena.auth.service.model.dto;

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
 * OidcCallback200ResponseDto
 */

@JsonTypeName("oidcCallback_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OidcCallback200ResponseDto {

  private @Nullable String status;

  public OidcCallback200ResponseDto status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", example = "ok", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OidcCallback200ResponseDto oidcCallback200Response = (OidcCallback200ResponseDto) o;
    return Objects.equals(this.status, oidcCallback200Response.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OidcCallback200ResponseDto {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

    private OidcCallback200ResponseDto instance;

    public Builder() {
      this(new OidcCallback200ResponseDto());
    }

    protected Builder(OidcCallback200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OidcCallback200ResponseDto value) { 
      this.instance.setStatus(value.status);
      return this;
    }

    public OidcCallback200ResponseDto.Builder status(String status) {
      this.instance.status(status);
      return this;
    }
    
    /**
    * returns a built OidcCallback200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OidcCallback200ResponseDto build() {
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
  public static OidcCallback200ResponseDto.Builder builder() {
    return new OidcCallback200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OidcCallback200ResponseDto.Builder toBuilder() {
    OidcCallback200ResponseDto.Builder builder = new OidcCallback200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

