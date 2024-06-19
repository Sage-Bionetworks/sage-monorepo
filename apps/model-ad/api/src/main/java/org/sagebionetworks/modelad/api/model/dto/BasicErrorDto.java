package org.sagebionetworks.modelad.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** Problem details (tools.ietf.org/html/rfc7807) */
@Schema(name = "BasicError", description = "Problem details (tools.ietf.org/html/rfc7807)")
@JsonTypeName("BasicError")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@lombok.AllArgsConstructor
@lombok.Builder
public class BasicErrorDto {

  @JsonProperty("title")
  private String title;

  @JsonProperty("status")
  private Integer status;

  @JsonProperty("detail")
  private String detail;

  @JsonProperty("type")
  private String type;

  public BasicErrorDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A human readable documentation for the problem type
   *
   * @return title
   */
  @NotNull
  @Schema(
      name = "title",
      description = "A human readable documentation for the problem type",
      required = true)
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
   *
   * @return status
   */
  @NotNull
  @Schema(name = "status", description = "The HTTP status code", required = true)
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public BasicErrorDto detail(String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * A human readable explanation specific to this occurrence of the problem
   *
   * @return detail
   */
  @Schema(
      name = "detail",
      description = "A human readable explanation specific to this occurrence of the problem",
      required = false)
  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public BasicErrorDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * An absolute URI that identifies the problem type
   *
   * @return type
   */
  @Schema(
      name = "type",
      description = "An absolute URI that identifies the problem type",
      required = false)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
    return Objects.equals(this.title, basicError.title)
        && Objects.equals(this.status, basicError.status)
        && Objects.equals(this.detail, basicError.detail)
        && Objects.equals(this.type, basicError.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, status, detail, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicErrorDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
