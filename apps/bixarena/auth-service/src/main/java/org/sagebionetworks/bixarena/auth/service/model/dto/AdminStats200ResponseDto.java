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
 * AdminStats200ResponseDto
 */

@JsonTypeName("adminStats_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class AdminStats200ResponseDto {

  private @Nullable Boolean ok;

  public AdminStats200ResponseDto ok(@Nullable Boolean ok) {
    this.ok = ok;
    return this;
  }

  /**
   * Get ok
   * @return ok
   */
  
  @Schema(name = "ok", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ok")
  public @Nullable Boolean getOk() {
    return ok;
  }

  public void setOk(@Nullable Boolean ok) {
    this.ok = ok;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdminStats200ResponseDto adminStats200Response = (AdminStats200ResponseDto) o;
    return Objects.equals(this.ok, adminStats200Response.ok);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ok);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdminStats200ResponseDto {\n");
    sb.append("    ok: ").append(toIndentedString(ok)).append("\n");
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

    private AdminStats200ResponseDto instance;

    public Builder() {
      this(new AdminStats200ResponseDto());
    }

    protected Builder(AdminStats200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(AdminStats200ResponseDto value) { 
      this.instance.setOk(value.ok);
      return this;
    }

    public AdminStats200ResponseDto.Builder ok(Boolean ok) {
      this.instance.ok(ok);
      return this;
    }
    
    /**
    * returns a built AdminStats200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public AdminStats200ResponseDto build() {
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
  public static AdminStats200ResponseDto.Builder builder() {
    return new AdminStats200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public AdminStats200ResponseDto.Builder toBuilder() {
    AdminStats200ResponseDto.Builder builder = new AdminStats200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

