package org.sagebionetworks.openchallenges.organization.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * An organization
 */

@Schema(name = "Organization", description = "An organization")
@JsonTypeName("Organization")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OrganizationDto {

  private Long id;

  private String name;

  private String login;

  private @Nullable String description = null;

  private @Nullable String avatarKey = null;

  private String websiteUrl = null;

  private Integer challengeCount = 0;

  private @Nullable String shortName = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public OrganizationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OrganizationDto(Long id, String name, String login, String websiteUrl, Integer challengeCount, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.login = login;
    this.websiteUrl = websiteUrl;
    this.challengeCount = challengeCount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public OrganizationDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of an organization
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "1", description = "The unique identifier of an organization", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OrganizationDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the organization.
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Example organization", description = "The name of the organization.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OrganizationDto login(String login) {
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

  public OrganizationDto description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * A description of the organization.
   * @return description
   */
  
  @Schema(name = "description", example = "A description of the organization.", description = "A description of the organization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public OrganizationDto avatarKey(@Nullable String avatarKey) {
    this.avatarKey = avatarKey;
    return this;
  }

  /**
   * Get avatarKey
   * @return avatarKey
   */
  
  @Schema(name = "avatarKey", example = "logo/dream.png", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avatarKey")
  public @Nullable String getAvatarKey() {
    return avatarKey;
  }

  public void setAvatarKey(@Nullable String avatarKey) {
    this.avatarKey = avatarKey;
  }

  public OrganizationDto websiteUrl(String websiteUrl) {
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

  public OrganizationDto challengeCount(Integer challengeCount) {
    this.challengeCount = challengeCount;
    return this;
  }

  /**
   * The number of challenges involving this organization.
   * minimum: 0
   * @return challengeCount
   */
  @NotNull @Min(0) 
  @Schema(name = "challengeCount", example = "10", description = "The number of challenges involving this organization.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("challengeCount")
  public Integer getChallengeCount() {
    return challengeCount;
  }

  public void setChallengeCount(Integer challengeCount) {
    this.challengeCount = challengeCount;
  }

  public OrganizationDto shortName(@Nullable String shortName) {
    this.shortName = shortName;
    return this;
  }

  /**
   * The abbreviation, which may be an acronym, initialism, or other short form (e.g., \"AI\", \"WashU\", \"etc.\") 
   * @return shortName
   */
  @Size(max = 32) 
  @Schema(name = "shortName", example = "OC", description = "The abbreviation, which may be an acronym, initialism, or other short form (e.g., \"AI\", \"WashU\", \"etc.\") ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shortName")
  public @Nullable String getShortName() {
    return shortName;
  }

  public void setShortName(@Nullable String shortName) {
    this.shortName = shortName;
  }

  public OrganizationDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Datetime when the object was added to the database.
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2022-07-04T22:19:11Z", description = "Datetime when the object was added to the database.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OrganizationDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Datetime when the object was last modified in the database.
   * @return updatedAt
   */
  @NotNull @Valid 
  @Schema(name = "updatedAt", example = "2022-07-04T22:19:11Z", description = "Datetime when the object was last modified in the database.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedAt")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationDto organization = (OrganizationDto) o;
    return Objects.equals(this.id, organization.id) &&
        Objects.equals(this.name, organization.name) &&
        Objects.equals(this.login, organization.login) &&
        Objects.equals(this.description, organization.description) &&
        Objects.equals(this.avatarKey, organization.avatarKey) &&
        Objects.equals(this.websiteUrl, organization.websiteUrl) &&
        Objects.equals(this.challengeCount, organization.challengeCount) &&
        Objects.equals(this.shortName, organization.shortName) &&
        Objects.equals(this.createdAt, organization.createdAt) &&
        Objects.equals(this.updatedAt, organization.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, login, description, avatarKey, websiteUrl, challengeCount, shortName, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    avatarKey: ").append(toIndentedString(avatarKey)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
    sb.append("    challengeCount: ").append(toIndentedString(challengeCount)).append("\n");
    sb.append("    shortName: ").append(toIndentedString(shortName)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

    private OrganizationDto instance;

    public Builder() {
      this(new OrganizationDto());
    }

    protected Builder(OrganizationDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OrganizationDto value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setLogin(value.login);
      this.instance.setDescription(value.description);
      this.instance.setAvatarKey(value.avatarKey);
      this.instance.setWebsiteUrl(value.websiteUrl);
      this.instance.setChallengeCount(value.challengeCount);
      this.instance.setShortName(value.shortName);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public OrganizationDto.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    public OrganizationDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public OrganizationDto.Builder login(String login) {
      this.instance.login(login);
      return this;
    }
    
    public OrganizationDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public OrganizationDto.Builder avatarKey(String avatarKey) {
      this.instance.avatarKey(avatarKey);
      return this;
    }
    
    public OrganizationDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    public OrganizationDto.Builder challengeCount(Integer challengeCount) {
      this.instance.challengeCount(challengeCount);
      return this;
    }
    
    public OrganizationDto.Builder shortName(String shortName) {
      this.instance.shortName(shortName);
      return this;
    }
    
    public OrganizationDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public OrganizationDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built OrganizationDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OrganizationDto build() {
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
  public static OrganizationDto.Builder builder() {
    return new OrganizationDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OrganizationDto.Builder toBuilder() {
    OrganizationDto.Builder builder = new OrganizationDto.Builder();
    return builder.copyOf(this);
  }

}

