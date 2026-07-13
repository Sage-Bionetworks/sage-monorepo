package org.sagebionetworks.model.ad.api.next.model.dto;

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
 * Link with required display text
 */

@Schema(name = "NamedLink", description = "Link with required display text")
@JsonTypeName("NamedLink")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class NamedLinkDto {

  private String linkText;

  private @Nullable String linkUrl;

  public NamedLinkDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NamedLinkDto(String linkText) {
    this.linkText = linkText;
  }

  public NamedLinkDto linkText(String linkText) {
    this.linkText = linkText;
    return this;
  }

  /**
   * Link text
   * @return linkText
   */
  @NotNull 
  @Schema(name = "link_text", description = "Link text", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("link_text")
  public String getLinkText() {
    return linkText;
  }

  public void setLinkText(String linkText) {
    this.linkText = linkText;
  }

  public NamedLinkDto linkUrl(@Nullable String linkUrl) {
    this.linkUrl = linkUrl;
    return this;
  }

  /**
   * URL for the related resource
   * @return linkUrl
   */
  
  @Schema(name = "link_url", description = "URL for the related resource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("link_url")
  public @Nullable String getLinkUrl() {
    return linkUrl;
  }

  public void setLinkUrl(@Nullable String linkUrl) {
    this.linkUrl = linkUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NamedLinkDto namedLink = (NamedLinkDto) o;
    return Objects.equals(this.linkText, namedLink.linkText) &&
        Objects.equals(this.linkUrl, namedLink.linkUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(linkText, linkUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NamedLinkDto {\n");
    sb.append("    linkText: ").append(toIndentedString(linkText)).append("\n");
    sb.append("    linkUrl: ").append(toIndentedString(linkUrl)).append("\n");
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

    private NamedLinkDto instance;

    public Builder() {
      this(new NamedLinkDto());
    }

    protected Builder(NamedLinkDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NamedLinkDto value) { 
      this.instance.setLinkText(value.linkText);
      this.instance.setLinkUrl(value.linkUrl);
      return this;
    }

    public NamedLinkDto.Builder linkText(String linkText) {
      this.instance.linkText(linkText);
      return this;
    }
    
    public NamedLinkDto.Builder linkUrl(String linkUrl) {
      this.instance.linkUrl(linkUrl);
      return this;
    }
    
    /**
    * returns a built NamedLinkDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NamedLinkDto build() {
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
  public static NamedLinkDto.Builder builder() {
    return new NamedLinkDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NamedLinkDto.Builder toBuilder() {
    NamedLinkDto.Builder builder = new NamedLinkDto.Builder();
    return builder.copyOf(this);
  }

}

