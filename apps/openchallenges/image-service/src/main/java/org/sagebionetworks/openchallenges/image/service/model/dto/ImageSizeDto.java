package org.sagebionetworks.openchallenges.image.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** The size of the image. */
@Schema(name = "ImageSize", description = "The size of the image.")
@JsonTypeName("ImageSize")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ImageSizeDto {

  @JsonProperty("width")
  private ImageSizeOptionDto width = null;

  @JsonProperty("height")
  private ImageSizeOptionDto height = null;

  public ImageSizeDto width(ImageSizeOptionDto width) {
    this.width = width;
    return this;
  }

  /**
   * Get width
   *
   * @return width
   */
  @Valid
  @Schema(name = "width", required = false)
  public ImageSizeOptionDto getWidth() {
    return width;
  }

  public void setWidth(ImageSizeOptionDto width) {
    this.width = width;
  }

  public ImageSizeDto height(ImageSizeOptionDto height) {
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
  public ImageSizeOptionDto getHeight() {
    return height;
  }

  public void setHeight(ImageSizeOptionDto height) {
    this.height = height;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImageSizeDto imageSize = (ImageSizeDto) o;
    return Objects.equals(this.width, imageSize.width)
        && Objects.equals(this.height, imageSize.height);
  }

  @Override
  public int hashCode() {
    return Objects.hash(width, height);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImageSizeDto {\n");
    sb.append("    width: ").append(toIndentedString(width)).append("\n");
    sb.append("    height: ").append(toIndentedString(height)).append("\n");
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
