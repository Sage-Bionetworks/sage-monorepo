package org.sagebionetworks.agora.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Represents the health of a service
 */

@Schema(name = "HealthCheck", description = "Represents the health of a service")
@JsonTypeName("HealthCheck")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class HealthCheckDto {

  /**
   * Indicates whether the service status is acceptable or not
   */
  public enum StatusEnum {
    PASS("pass"),
    
    FAIL("fail"),
    
    WARN("warn");

    private final String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  public HealthCheckDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public HealthCheckDto(StatusEnum status) {
    this.status = status;
  }

  public HealthCheckDto status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Indicates whether the service status is acceptable or not
   * @return status
   */
  @NotNull 
  @Schema(name = "status", description = "Indicates whether the service status is acceptable or not", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
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
    HealthCheckDto healthCheck = (HealthCheckDto) o;
    return Objects.equals(this.status, healthCheck.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HealthCheckDto {\n");
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

    private HealthCheckDto instance;

    public Builder() {
      this(new HealthCheckDto());
    }

    protected Builder(HealthCheckDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(HealthCheckDto value) { 
      this.instance.setStatus(value.status);
      return this;
    }

    public HealthCheckDto.Builder status(StatusEnum status) {
      this.instance.status(status);
      return this;
    }
    
    /**
    * returns a built HealthCheckDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public HealthCheckDto build() {
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
  public static HealthCheckDto.Builder builder() {
    return new HealthCheckDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public HealthCheckDto.Builder toBuilder() {
    HealthCheckDto.Builder builder = new HealthCheckDto.Builder();
    return builder.copyOf(this);
  }

}

