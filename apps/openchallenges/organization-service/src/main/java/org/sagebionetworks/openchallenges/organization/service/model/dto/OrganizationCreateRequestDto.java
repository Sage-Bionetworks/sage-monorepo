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
 * The information required to create an org account
 */

@Schema(name = "OrganizationCreateRequest", description = "The information required to create an org account")
@JsonTypeName("OrganizationCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class OrganizationCreateRequestDto {

  private String login;

  private String name;

  private @Nullable String description = null;

  private @Nullable String avatarUrl = null;

  private String websiteUrl = null;

  public OrganizationCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OrganizationCreateRequestDto(String login, String name, String websiteUrl) {
    this.login = login;
    this.name = name;
    this.websiteUrl = websiteUrl;
  }

  public OrganizationCreateRequestDto login(String login) {
    this.login = login;
    return this;
  }

  /**
   * The unique login of an organization.
   * @return login
   */
  @NotNull @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") @Size(min = 2, max = 64) 
  @Schema(name = "login", example = "example-org", description = "The unique login of an organization.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("login")
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public OrganizationCreateRequestDto name(String name) {
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

  public OrganizationCreateRequestDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", example = "A short description of the organization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OrganizationCreateRequestDto avatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  /**
   * Get avatarUrl
   * @return avatarUrl
   */
  
  @Schema(name = "avatarUrl", example = "https://via.placeholder.com/300.png", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avatarUrl")
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public OrganizationCreateRequestDto websiteUrl(String websiteUrl) {
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
    OrganizationCreateRequestDto organizationCreateRequest = (OrganizationCreateRequestDto) o;
    return Objects.equals(this.login, organizationCreateRequest.login) &&
        Objects.equals(this.name, organizationCreateRequest.name) &&
        Objects.equals(this.description, organizationCreateRequest.description) &&
        Objects.equals(this.avatarUrl, organizationCreateRequest.avatarUrl) &&
        Objects.equals(this.websiteUrl, organizationCreateRequest.websiteUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(login, name, description, avatarUrl, websiteUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationCreateRequestDto {\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
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

    private OrganizationCreateRequestDto instance;

    public Builder() {
      this(new OrganizationCreateRequestDto());
    }

    protected Builder(OrganizationCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OrganizationCreateRequestDto value) { 
      this.instance.setLogin(value.login);
      this.instance.setName(value.name);
      this.instance.setDescription(value.description);
      this.instance.setAvatarUrl(value.avatarUrl);
      this.instance.setWebsiteUrl(value.websiteUrl);
      return this;
    }

    public OrganizationCreateRequestDto.Builder login(String login) {
      this.instance.login(login);
      return this;
    }
    
    public OrganizationCreateRequestDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public OrganizationCreateRequestDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public OrganizationCreateRequestDto.Builder avatarUrl(String avatarUrl) {
      this.instance.avatarUrl(avatarUrl);
      return this;
    }
    
    public OrganizationCreateRequestDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    /**
    * returns a built OrganizationCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OrganizationCreateRequestDto build() {
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
  public static OrganizationCreateRequestDto.Builder builder() {
    return new OrganizationCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OrganizationCreateRequestDto.Builder toBuilder() {
    OrganizationCreateRequestDto.Builder builder = new OrganizationCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

