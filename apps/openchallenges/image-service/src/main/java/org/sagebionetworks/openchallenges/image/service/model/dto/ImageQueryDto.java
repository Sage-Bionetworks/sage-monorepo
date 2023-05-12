package org.sagebionetworks.openchallenges.image.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** An image query. */
@Schema(name = "ImageQuery", description = "An image query.")
@JsonTypeName("ImageQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ImageQueryDto {

  @JsonProperty("objectKey")
  private String objectKey;

  @JsonProperty("height")
  private ImageHeightDto height = ImageHeightDto.ORIGINAL;

  @JsonProperty("aspectRatio")
  private ImageAspectRatioDto aspectRatio = ImageAspectRatioDto.ORIGINAL;

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

  public ImageQueryDto height(ImageHeightDto height) {
    this.height = height;
    return this;
  }

  /**
   * Get height
   *
   * @return height
   */
  @Valid
  @Schema(name = "height", required = false)
  public ImageHeightDto getHeight() {
    return height;
  }

  public void setHeight(ImageHeightDto height) {
    this.height = height;
  }

  public ImageQueryDto aspectRatio(ImageAspectRatioDto aspectRatio) {
    this.aspectRatio = aspectRatio;
    return this;
  }

  /**
   * Get aspectRatio
   *
   * @return aspectRatio
   */
  @Valid
  @Schema(name = "aspectRatio", required = false)
  public ImageAspectRatioDto getAspectRatio() {
    return aspectRatio;
  }

  public void setAspectRatio(ImageAspectRatioDto aspectRatio) {
    this.aspectRatio = aspectRatio;
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
    return Objects.equals(this.objectKey, imageQuery.objectKey)
        && Objects.equals(this.height, imageQuery.height)
        && Objects.equals(this.aspectRatio, imageQuery.aspectRatio);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectKey, height, aspectRatio);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImageQueryDto {\n");
    sb.append("    objectKey: ").append(toIndentedString(objectKey)).append("\n");
    sb.append("    height: ").append(toIndentedString(height)).append("\n");
    sb.append("    aspectRatio: ").append(toIndentedString(aspectRatio)).append("\n");
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
