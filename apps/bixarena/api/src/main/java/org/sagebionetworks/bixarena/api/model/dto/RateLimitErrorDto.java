package org.sagebionetworks.bixarena.api.model.dto;

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
 * RateLimitErrorDto
 */

@JsonTypeName("RateLimitError")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class RateLimitErrorDto {

  private String title;

  private Integer status;

  private @Nullable String detail;

  private @Nullable String type;

  private @Nullable String instance;

  private Integer limit;

  private String window;

  private Integer retryAfterSeconds;

  public RateLimitErrorDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RateLimitErrorDto(String title, Integer status, Integer limit, String window, Integer retryAfterSeconds) {
    this.title = title;
    this.status = status;
    this.limit = limit;
    this.window = window;
    this.retryAfterSeconds = retryAfterSeconds;
  }

  public RateLimitErrorDto title(String title) {
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

  public RateLimitErrorDto status(Integer status) {
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

  public RateLimitErrorDto detail(@Nullable String detail) {
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

  public RateLimitErrorDto type(@Nullable String type) {
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

  public RateLimitErrorDto instance(@Nullable String instance) {
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

  public RateLimitErrorDto limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Maximum requests allowed per window
   * @return limit
   */
  @NotNull 
  @Schema(name = "limit", example = "100", description = "Maximum requests allowed per window", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("limit")
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public RateLimitErrorDto window(String window) {
    this.window = window;
    return this;
  }

  /**
   * Time window for rate limiting
   * @return window
   */
  @NotNull 
  @Schema(name = "window", example = "1 minute", description = "Time window for rate limiting", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("window")
  public String getWindow() {
    return window;
  }

  public void setWindow(String window) {
    this.window = window;
  }

  public RateLimitErrorDto retryAfterSeconds(Integer retryAfterSeconds) {
    this.retryAfterSeconds = retryAfterSeconds;
    return this;
  }

  /**
   * Seconds to wait before retrying
   * @return retryAfterSeconds
   */
  @NotNull 
  @Schema(name = "retryAfterSeconds", example = "18", description = "Seconds to wait before retrying", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("retryAfterSeconds")
  public Integer getRetryAfterSeconds() {
    return retryAfterSeconds;
  }

  public void setRetryAfterSeconds(Integer retryAfterSeconds) {
    this.retryAfterSeconds = retryAfterSeconds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateLimitErrorDto rateLimitError = (RateLimitErrorDto) o;
    return Objects.equals(this.title, rateLimitError.title) &&
        Objects.equals(this.status, rateLimitError.status) &&
        Objects.equals(this.detail, rateLimitError.detail) &&
        Objects.equals(this.type, rateLimitError.type) &&
        Objects.equals(this.instance, rateLimitError.instance) &&
        Objects.equals(this.limit, rateLimitError.limit) &&
        Objects.equals(this.window, rateLimitError.window) &&
        Objects.equals(this.retryAfterSeconds, rateLimitError.retryAfterSeconds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, status, detail, type, instance, limit, window, retryAfterSeconds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitErrorDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    instance: ").append(toIndentedString(instance)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    window: ").append(toIndentedString(window)).append("\n");
    sb.append("    retryAfterSeconds: ").append(toIndentedString(retryAfterSeconds)).append("\n");
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

    private RateLimitErrorDto instance;

    public Builder() {
      this(new RateLimitErrorDto());
    }

    protected Builder(RateLimitErrorDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(RateLimitErrorDto value) { 
      this.instance.setTitle(value.title);
      this.instance.setStatus(value.status);
      this.instance.setDetail(value.detail);
      this.instance.setType(value.type);
      this.instance.setInstance(value.instance);
      this.instance.setLimit(value.limit);
      this.instance.setWindow(value.window);
      this.instance.setRetryAfterSeconds(value.retryAfterSeconds);
      return this;
    }

    public RateLimitErrorDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public RateLimitErrorDto.Builder status(Integer status) {
      this.instance.status(status);
      return this;
    }
    
    public RateLimitErrorDto.Builder detail(String detail) {
      this.instance.detail(detail);
      return this;
    }
    
    public RateLimitErrorDto.Builder type(String type) {
      this.instance.type(type);
      return this;
    }
    
    public RateLimitErrorDto.Builder instance(String instance) {
      this.instance.instance(instance);
      return this;
    }
    
    public RateLimitErrorDto.Builder limit(Integer limit) {
      this.instance.limit(limit);
      return this;
    }
    
    public RateLimitErrorDto.Builder window(String window) {
      this.instance.window(window);
      return this;
    }
    
    public RateLimitErrorDto.Builder retryAfterSeconds(Integer retryAfterSeconds) {
      this.instance.retryAfterSeconds(retryAfterSeconds);
      return this;
    }
    
    /**
    * returns a built RateLimitErrorDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public RateLimitErrorDto build() {
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
  public static RateLimitErrorDto.Builder builder() {
    return new RateLimitErrorDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public RateLimitErrorDto.Builder toBuilder() {
    RateLimitErrorDto.Builder builder = new RateLimitErrorDto.Builder();
    return builder.copyOf(this);
  }

}

