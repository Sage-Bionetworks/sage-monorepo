package org.sagebionetworks.openchallenges.image.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageAspectRatioDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageHeightDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * An image query that identifies an image either by an object storage key or by a direct URL. Exactly one of &#x60;objectKey&#x60; or &#x60;imageUrl&#x60; must be provided. 
 */

@Schema(name = "ImageQuery", description = "An image query that identifies an image either by an object storage key or by a direct URL. Exactly one of `objectKey` or `imageUrl` must be provided. ")
@JsonTypeName("ImageQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ImageQueryDto {

  private @Nullable String objectKey;

  private @Nullable URI imageUrl;

  private ImageHeightDto height = ImageHeightDto.ORIGINAL;

  private ImageAspectRatioDto aspectRatio = ImageAspectRatioDto.ORIGINAL;

  public ImageQueryDto objectKey(@Nullable String objectKey) {
    this.objectKey = objectKey;
    return this;
  }

  /**
   * The unique identifier of the image.
   * @return objectKey
   */
  @Pattern(regexp = "^[a-zA-Z0-9/_-]+.[a-zA-Z0-9/_-]+") 
  @Schema(name = "objectKey", example = "logo/dream.png", description = "The unique identifier of the image.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("objectKey")
  public @Nullable String getObjectKey() {
    return objectKey;
  }

  public void setObjectKey(@Nullable String objectKey) {
    this.objectKey = objectKey;
  }

  public ImageQueryDto imageUrl(@Nullable URI imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

  /**
   * The HTTPS URL of the image. Use this as an alternative to `objectKey`. 
   * @return imageUrl
   */
  @Valid 
  @Schema(name = "imageUrl", description = "The HTTPS URL of the image. Use this as an alternative to `objectKey`. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("imageUrl")
  public @Nullable URI getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(@Nullable URI imageUrl) {
    this.imageUrl = imageUrl;
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
        Objects.equals(this.imageUrl, imageQuery.imageUrl) &&
        Objects.equals(this.height, imageQuery.height) &&
        Objects.equals(this.aspectRatio, imageQuery.aspectRatio);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectKey, imageUrl, height, aspectRatio);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImageQueryDto {\n");
    sb.append("    objectKey: ").append(toIndentedString(objectKey)).append("\n");
    sb.append("    imageUrl: ").append(toIndentedString(imageUrl)).append("\n");
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
  
  public static class Builder {

    private ImageQueryDto instance;

    public Builder() {
      this(new ImageQueryDto());
    }

    protected Builder(ImageQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ImageQueryDto value) { 
      this.instance.setObjectKey(value.objectKey);
      this.instance.setImageUrl(value.imageUrl);
      this.instance.setHeight(value.height);
      this.instance.setAspectRatio(value.aspectRatio);
      return this;
    }

    public ImageQueryDto.Builder objectKey(String objectKey) {
      this.instance.objectKey(objectKey);
      return this;
    }
    
    public ImageQueryDto.Builder imageUrl(URI imageUrl) {
      this.instance.imageUrl(imageUrl);
      return this;
    }
    
    public ImageQueryDto.Builder height(ImageHeightDto height) {
      this.instance.height(height);
      return this;
    }
    
    public ImageQueryDto.Builder aspectRatio(ImageAspectRatioDto aspectRatio) {
      this.instance.aspectRatio(aspectRatio);
      return this;
    }
    
    /**
    * returns a built ImageQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ImageQueryDto build() {
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
  public static ImageQueryDto.Builder builder() {
    return new ImageQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ImageQueryDto.Builder toBuilder() {
    ImageQueryDto.Builder builder = new ImageQueryDto.Builder();
    return builder.copyOf(this);
  }

}

