package org.sagebionetworks.agora.api.next.model.dto;

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
 * Problem details (tools.ietf.org/html/rfc7807)
 */

@Schema(name = "BasicError", description = "Problem details (tools.ietf.org/html/rfc7807)")
@JsonTypeName("BasicError")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BasicErrorDto {

  private String title;

  private Integer status;

  private @Nullable String detail;

  private @Nullable String type;

  private @Nullable String instance;

  public BasicErrorDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BasicErrorDto(String title, Integer status) {
    this.title = title;
    this.status = status;
  }

  public BasicErrorDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A human readable documentation for the problem type
   * @return title
   */
  @NotNull 
  @Schema(name = "title", description = "A human readable documentation for the problem type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public BasicErrorDto status(Integer status) {
    this.status = status;
    return this;
  }

  /**
   * The HTTP status code
   * @return status
   */
  @NotNull 
  @Schema(name = "status", description = "The HTTP status code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public BasicErrorDto detail(@Nullable String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * A human readable explanation specific to this occurrence of the problem
   * @return detail
   */
  
  @Schema(name = "detail", description = "A human readable explanation specific to this occurrence of the problem", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("detail")
  public @Nullable String getDetail() {
    return detail;
  }

  public void setDetail(@Nullable String detail) {
    this.detail = detail;
  }

  public BasicErrorDto type(@Nullable String type) {
    this.type = type;
    return this;
  }

  /**
   * An absolute URI that identifies the problem type
   * @return type
   */
  
  @Schema(name = "type", description = "An absolute URI that identifies the problem type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public @Nullable String getType() {
    return type;
  }

  public void setType(@Nullable String type) {
    this.type = type;
  }

  public BasicErrorDto instance(@Nullable String instance) {
    this.instance = instance;
    return this;
  }

  /**
   * An absolute URI that identifies the specific occurrence of the problem
   * @return instance
   */
  
  @Schema(name = "instance", description = "An absolute URI that identifies the specific occurrence of the problem", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("instance")
  public @Nullable String getInstance() {
    return instance;
  }

  public void setInstance(@Nullable String instance) {
    this.instance = instance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicErrorDto basicError = (BasicErrorDto) o;
    return Objects.equals(this.title, basicError.title) &&
        Objects.equals(this.status, basicError.status) &&
        Objects.equals(this.detail, basicError.detail) &&
        Objects.equals(this.type, basicError.type) &&
        Objects.equals(this.instance, basicError.instance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, status, detail, type, instance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicErrorDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    instance: ").append(toIndentedString(instance)).append("\n");
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

    private BasicErrorDto instance;

    public Builder() {
      this(new BasicErrorDto());
    }

    protected Builder(BasicErrorDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BasicErrorDto value) { 
      this.instance.setTitle(value.title);
      this.instance.setStatus(value.status);
      this.instance.setDetail(value.detail);
      this.instance.setType(value.type);
      this.instance.setInstance(value.instance);
      return this;
    }

    public BasicErrorDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public BasicErrorDto.Builder status(Integer status) {
      this.instance.status(status);
      return this;
    }
    
    public BasicErrorDto.Builder detail(String detail) {
      this.instance.detail(detail);
      return this;
    }
    
    public BasicErrorDto.Builder type(String type) {
      this.instance.type(type);
      return this;
    }
    
    public BasicErrorDto.Builder instance(String instance) {
      this.instance.instance(instance);
      return this;
    }
    
    /**
    * returns a built BasicErrorDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BasicErrorDto build() {
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
  public static BasicErrorDto.Builder builder() {
    return new BasicErrorDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BasicErrorDto.Builder toBuilder() {
    BasicErrorDto.Builder builder = new BasicErrorDto.Builder();
    return builder.copyOf(this);
  }

}

