package org.sagebionetworks.openchallenges.image.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageAspectRatioDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageHeightDto;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * An image query.
 */

@Schema(name = "ImageQuery", description = "An image query.")
@JsonTypeName("ImageQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ImageQueryDto {

  private String objectKey;

  private ImageHeightDto height = ImageHeightDto.ORIGINAL;

  private ImageAspectRatioDto aspectRatio = ImageAspectRatioDto.ORIGINAL;

  public ImageQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ImageQueryDto(String objectKey) {
    this.objectKey = objectKey;
  }

  public ImageQueryDto objectKey(String objectKey) {
    this.objectKey = objectKey;
    return this;
  }

  /**
   * The unique identifier of the image.
   * @return objectKey
  */
  @NotNull @Pattern(regexp = "^[a-zA-Z0-9/_-]+.[a-zA-Z0-9/_-]+") 
  @Schema(name = "objectKey", example = "logo/dream.png", description = "The unique identifier of the image.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("objectKey")
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
   * @return height
  */
  @Valid 
  @Schema(name = "height", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("height")
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
   * @return aspectRatio
  */
  @Valid 
  @Schema(name = "aspectRatio", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("aspectRatio")
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
    return Objects.equals(this.objectKey, imageQuery.objectKey) &&
        Objects.equals(this.height, imageQuery.height) &&
        Objects.equals(this.aspectRatio, imageQuery.aspectRatio);
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
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

