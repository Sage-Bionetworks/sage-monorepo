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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OrganizationCreateRequestDto {

  private String login;

  private String name;

  private @Nullable String description = null;

  private @Nullable String avatarKey = null;

  private String websiteUrl = null;

  private @Nullable String acronym = null;

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

  public OrganizationCreateRequestDto description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", example = "A short description of the organization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public OrganizationCreateRequestDto avatarKey(@Nullable String avatarKey) {
    this.avatarKey = avatarKey;
    return this;
  }

  /**
   * Get avatarKey
   * @return avatarKey
   */
  
  @Schema(name = "avatarKey", example = "logo/300.png", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avatarKey")
  public @Nullable String getAvatarKey() {
    return avatarKey;
  }

  public void setAvatarKey(@Nullable String avatarKey) {
    this.avatarKey = avatarKey;
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

  public OrganizationCreateRequestDto acronym(@Nullable String acronym) {
    this.acronym = acronym;
    return this;
  }

  /**
   * An acronym of the organization.
   * @return acronym
   */
  @Size(max = 10) 
  @Schema(name = "acronym", example = "OC", description = "An acronym of the organization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("acronym")
  public @Nullable String getAcronym() {
    return acronym;
  }

  public void setAcronym(@Nullable String acronym) {
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
    OrganizationCreateRequestDto organizationCreateRequest = (OrganizationCreateRequestDto) o;
    return Objects.equals(this.login, organizationCreateRequest.login) &&
        Objects.equals(this.name, organizationCreateRequest.name) &&
        Objects.equals(this.description, organizationCreateRequest.description) &&
        Objects.equals(this.avatarKey, organizationCreateRequest.avatarKey) &&
        Objects.equals(this.websiteUrl, organizationCreateRequest.websiteUrl) &&
        Objects.equals(this.acronym, organizationCreateRequest.acronym);
  }

  @Override
  public int hashCode() {
    return Objects.hash(login, name, description, avatarKey, websiteUrl, acronym);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationCreateRequestDto {\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
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
      this.instance.setAvatarKey(value.avatarKey);
      this.instance.setWebsiteUrl(value.websiteUrl);
      this.instance.setAcronym(value.acronym);
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
    
    public OrganizationCreateRequestDto.Builder avatarKey(String avatarKey) {
      this.instance.avatarKey(avatarKey);
      return this;
    }
    
    public OrganizationCreateRequestDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    public OrganizationCreateRequestDto.Builder acronym(String acronym) {
      this.instance.acronym(acronym);
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

