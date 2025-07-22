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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OrganizationUpdateRequestDto {

  private String name;

  private String description = null;

  private String avatarKey = null;

  private String websiteUrl = null;

  private String acronym = null;

  public OrganizationUpdateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OrganizationUpdateRequestDto(String name, String description, String avatarKey, String websiteUrl, String acronym) {
    this.name = name;
    this.description = description;
    this.avatarKey = avatarKey;
    this.websiteUrl = websiteUrl;
    this.acronym = acronym;
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

  public OrganizationUpdateRequestDto avatarKey(String avatarKey) {
    this.avatarKey = avatarKey;
    return this;
  }

  /**
   * Get avatarKey
   * @return avatarKey
   */
  @NotNull 
  @Schema(name = "avatarKey", example = "https://via.placeholder.com/300.png", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("avatarKey")
  public String getAvatarKey() {
    return avatarKey;
  }

  public void setAvatarKey(String avatarKey) {
    this.avatarKey = avatarKey;
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

  public OrganizationUpdateRequestDto acronym(String acronym) {
    this.acronym = acronym;
    return this;
  }

  /**
   * An acronym of the organization.
   * @return acronym
   */
  @NotNull @Size(max = 10) 
  @Schema(name = "acronym", example = "OC", description = "An acronym of the organization.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("acronym")
  public String getAcronym() {
    return acronym;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
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
        Objects.equals(this.avatarKey, organizationUpdateRequest.avatarKey) &&
        Objects.equals(this.websiteUrl, organizationUpdateRequest.websiteUrl) &&
        Objects.equals(this.acronym, organizationUpdateRequest.acronym);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, avatarKey, websiteUrl, acronym);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationUpdateRequestDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    avatarKey: ").append(toIndentedString(avatarKey)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
    sb.append("    acronym: ").append(toIndentedString(acronym)).append("\n");
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
      this.instance.setAvatarKey(value.avatarKey);
      this.instance.setWebsiteUrl(value.websiteUrl);
      this.instance.setAcronym(value.acronym);
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
    
    public OrganizationUpdateRequestDto.Builder avatarKey(String avatarKey) {
      this.instance.avatarKey(avatarKey);
      return this;
    }
    
    public OrganizationUpdateRequestDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    public OrganizationUpdateRequestDto.Builder acronym(String acronym) {
      this.instance.acronym(acronym);
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

