package org.sagebionetworks.openchallenges.organization.service.model.dto;

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
 * The information required to update an org account
 */

@Schema(name = "OrganizationUpdateRequest", description = "The information required to update an org account")
@JsonTypeName("OrganizationUpdateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class OrganizationUpdateRequestDto {

  private String name;

  private String description = null;

  private String avatarUrl = null;

  private String websiteUrl = null;

  public OrganizationUpdateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OrganizationUpdateRequestDto(String name, String description, String avatarUrl, String websiteUrl) {
    this.name = name;
    this.description = description;
    this.avatarUrl = avatarUrl;
    this.websiteUrl = websiteUrl;
  }

  public OrganizationUpdateRequestDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Example organization", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OrganizationUpdateRequestDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  @NotNull 
  @Schema(name = "description", example = "A short description of the organization.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OrganizationUpdateRequestDto avatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  /**
   * Get avatarUrl
   * @return avatarUrl
   */
  @NotNull 
  @Schema(name = "avatarUrl", example = "https://via.placeholder.com/300.png", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("avatarUrl")
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public OrganizationUpdateRequestDto websiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  /**
   * A URL to the website or image.
   * @return websiteUrl
   */
  @NotNull @Size(max = 500) 
  @Schema(name = "websiteUrl", example = "https://openchallenges.io", description = "A URL to the website or image.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("websiteUrl")
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationUpdateRequestDto organizationUpdateRequest = (OrganizationUpdateRequestDto) o;
    return Objects.equals(this.name, organizationUpdateRequest.name) &&
        Objects.equals(this.description, organizationUpdateRequest.description) &&
        Objects.equals(this.avatarUrl, organizationUpdateRequest.avatarUrl) &&
        Objects.equals(this.websiteUrl, organizationUpdateRequest.websiteUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, avatarUrl, websiteUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationUpdateRequestDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
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

    private OrganizationUpdateRequestDto instance;

    public Builder() {
      this(new OrganizationUpdateRequestDto());
    }

    protected Builder(OrganizationUpdateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OrganizationUpdateRequestDto value) { 
      this.instance.setName(value.name);
      this.instance.setDescription(value.description);
      this.instance.setAvatarUrl(value.avatarUrl);
      this.instance.setWebsiteUrl(value.websiteUrl);
      return this;
    }

    public OrganizationUpdateRequestDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public OrganizationUpdateRequestDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public OrganizationUpdateRequestDto.Builder avatarUrl(String avatarUrl) {
      this.instance.avatarUrl(avatarUrl);
      return this;
    }
    
    public OrganizationUpdateRequestDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    /**
    * returns a built OrganizationUpdateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OrganizationUpdateRequestDto build() {
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
  public static OrganizationUpdateRequestDto.Builder builder() {
    return new OrganizationUpdateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OrganizationUpdateRequestDto.Builder toBuilder() {
    OrganizationUpdateRequestDto.Builder builder = new OrganizationUpdateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

