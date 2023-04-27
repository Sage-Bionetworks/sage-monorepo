package org.sagebionetworks.openchallenges.organization.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** An image query. */
@Schema(name = "ImageQuery", description = "An image query.")
@JsonTypeName("ImageQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ImageQueryDto {

  @JsonProperty("objectKey")
  private String objectKey;

  public ImageQueryDto objectKey(String objectKey) {
    this.objectKey = objectKey;
    return this;
  }

  /**
   * The object key of the image.
   *
   * @return objectKey
   */
  @NotNull
  @Schema(
      name = "objectKey",
      example = "logo/dream.png",
      description = "The object key of the image.",
      required = true)
  public String getObjectKey() {
    return objectKey;
  }

  public void setObjectKey(String objectKey) {
    this.objectKey = objectKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImageQueryDto imageQuery = (ImageQueryDto) o;
    return Objects.equals(this.objectKey, imageQuery.objectKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImageQueryDto {\n");
    sb.append("    objectKey: ").append(toIndentedString(objectKey)).append("\n");
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
