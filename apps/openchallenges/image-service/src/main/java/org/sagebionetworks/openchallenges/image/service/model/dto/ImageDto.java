package org.sagebionetworks.openchallenges.image.service.model.dto;

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
 * An image
 */

@Schema(name = "Image", description = "An image")
@JsonTypeName("Image")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ImageDto {

  private String url;

  public ImageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ImageDto(String url) {
    this.url = url;
  }

  public ImageDto url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
   */
  @NotNull 
  @Schema(name = "url", example = "http://example.com/an-image.png", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImageDto image = (ImageDto) o;
    return Objects.equals(this.url, image.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImageDto {\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
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

    private ImageDto instance;

    public Builder() {
      this(new ImageDto());
    }

    protected Builder(ImageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ImageDto value) { 
      this.instance.setUrl(value.url);
      return this;
    }

    public ImageDto.Builder url(String url) {
      this.instance.url(url);
      return this;
    }
    
    /**
    * returns a built ImageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ImageDto build() {
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
  public static ImageDto.Builder builder() {
    return new ImageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ImageDto.Builder toBuilder() {
    ImageDto.Builder builder = new ImageDto.Builder();
    return builder.copyOf(this);
  }

}

