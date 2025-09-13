package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.auth.service.model.dto.AuthScopeDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.UserRoleDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserProfileDto
 */

@JsonTypeName("UserProfile")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class UserProfileDto {

  private String id;

  private String username;

  private String email;

  private @Nullable String firstName;

  private @Nullable String lastName;

  private UserRoleDto role;

  @Valid
  private List<AuthScopeDto> scopes = new ArrayList<>();

  private @Nullable URI avatarUrl;

  private @Nullable String bio;

  private @Nullable URI website;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime updatedAt;

  public UserProfileDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserProfileDto(String id, String username, String email, UserRoleDto role, OffsetDateTime createdAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.role = role;
    this.createdAt = createdAt;
  }

  public UserProfileDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique user identifier
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "user_123456789", description = "Unique user identifier", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserProfileDto username(String username) {
    this.username = username;
    return this;
  }

  /**
   * User's username
   * @return username
   */
  @NotNull 
  @Schema(name = "username", example = "johndoe", description = "User's username", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserProfileDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * User's email address
   * @return email
   */
  @NotNull @jakarta.validation.constraints.Email 
  @Schema(name = "email", example = "john.doe@example.com", description = "User's email address", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserProfileDto firstName(@Nullable String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * User's first name
   * @return firstName
   */
  
  @Schema(name = "firstName", example = "John", description = "User's first name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstName")
  public @Nullable String getFirstName() {
    return firstName;
  }

  public void setFirstName(@Nullable String firstName) {
    this.firstName = firstName;
  }

  public UserProfileDto lastName(@Nullable String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * User's last name
   * @return lastName
   */
  
  @Schema(name = "lastName", example = "Doe", description = "User's last name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastName")
  public @Nullable String getLastName() {
    return lastName;
  }

  public void setLastName(@Nullable String lastName) {
    this.lastName = lastName;
  }

  public UserProfileDto role(UserRoleDto role) {
    this.role = role;
    return this;
  }

  /**
   * Get role
   * @return role
   */
  @NotNull @Valid 
  @Schema(name = "role", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("role")
  public UserRoleDto getRole() {
    return role;
  }

  public void setRole(UserRoleDto role) {
    this.role = role;
  }

  public UserProfileDto scopes(List<AuthScopeDto> scopes) {
    this.scopes = scopes;
    return this;
  }

  public UserProfileDto addScopesItem(AuthScopeDto scopesItem) {
    if (this.scopes == null) {
      this.scopes = new ArrayList<>();
    }
    this.scopes.add(scopesItem);
    return this;
  }

  /**
   * User's authorized scopes/permissions
   * @return scopes
   */
  @Valid 
  @Schema(name = "scopes", description = "User's authorized scopes/permissions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scopes")
  public List<AuthScopeDto> getScopes() {
    return scopes;
  }

  public void setScopes(List<AuthScopeDto> scopes) {
    this.scopes = scopes;
  }

  public UserProfileDto avatarUrl(@Nullable URI avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  /**
   * URL to user's avatar image
   * @return avatarUrl
   */
  @Valid 
  @Schema(name = "avatarUrl", example = "https://example.com/avatars/johndoe.jpg", description = "URL to user's avatar image", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avatarUrl")
  public @Nullable URI getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(@Nullable URI avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public UserProfileDto bio(@Nullable String bio) {
    this.bio = bio;
    return this;
  }

  /**
   * User's biography or description
   * @return bio
   */
  @Size(max = 500) 
  @Schema(name = "bio", example = "Researcher in computational biology", description = "User's biography or description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bio")
  public @Nullable String getBio() {
    return bio;
  }

  public void setBio(@Nullable String bio) {
    this.bio = bio;
  }

  public UserProfileDto website(@Nullable URI website) {
    this.website = website;
    return this;
  }

  /**
   * User's website URL
   * @return website
   */
  @Valid 
  @Schema(name = "website", example = "https://johndoe.com", description = "User's website URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("website")
  public @Nullable URI getWebsite() {
    return website;
  }

  public void setWebsite(@Nullable URI website) {
    this.website = website;
  }

  public UserProfileDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Timestamp when the user account was created
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2024-01-15T10:30Z", description = "Timestamp when the user account was created", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public UserProfileDto updatedAt(@Nullable OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Timestamp when the user profile was last updated
   * @return updatedAt
   */
  @Valid 
  @Schema(name = "updatedAt", example = "2024-02-01T14:20Z", description = "Timestamp when the user profile was last updated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updatedAt")
  public @Nullable OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(@Nullable OffsetDateTime updatedAt) {
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
    UserProfileDto userProfile = (UserProfileDto) o;
    return Objects.equals(this.id, userProfile.id) &&
        Objects.equals(this.username, userProfile.username) &&
        Objects.equals(this.email, userProfile.email) &&
        Objects.equals(this.firstName, userProfile.firstName) &&
        Objects.equals(this.lastName, userProfile.lastName) &&
        Objects.equals(this.role, userProfile.role) &&
        Objects.equals(this.scopes, userProfile.scopes) &&
        Objects.equals(this.avatarUrl, userProfile.avatarUrl) &&
        Objects.equals(this.bio, userProfile.bio) &&
        Objects.equals(this.website, userProfile.website) &&
        Objects.equals(this.createdAt, userProfile.createdAt) &&
        Objects.equals(this.updatedAt, userProfile.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, email, firstName, lastName, role, scopes, avatarUrl, bio, website, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserProfileDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    scopes: ").append(toIndentedString(scopes)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
    sb.append("    website: ").append(toIndentedString(website)).append("\n");
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

    private UserProfileDto instance;

    public Builder() {
      this(new UserProfileDto());
    }

    protected Builder(UserProfileDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(UserProfileDto value) { 
      this.instance.setId(value.id);
      this.instance.setUsername(value.username);
      this.instance.setEmail(value.email);
      this.instance.setFirstName(value.firstName);
      this.instance.setLastName(value.lastName);
      this.instance.setRole(value.role);
      this.instance.setScopes(value.scopes);
      this.instance.setAvatarUrl(value.avatarUrl);
      this.instance.setBio(value.bio);
      this.instance.setWebsite(value.website);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public UserProfileDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public UserProfileDto.Builder username(String username) {
      this.instance.username(username);
      return this;
    }
    
    public UserProfileDto.Builder email(String email) {
      this.instance.email(email);
      return this;
    }
    
    public UserProfileDto.Builder firstName(String firstName) {
      this.instance.firstName(firstName);
      return this;
    }
    
    public UserProfileDto.Builder lastName(String lastName) {
      this.instance.lastName(lastName);
      return this;
    }
    
    public UserProfileDto.Builder role(UserRoleDto role) {
      this.instance.role(role);
      return this;
    }
    
    public UserProfileDto.Builder scopes(List<AuthScopeDto> scopes) {
      this.instance.scopes(scopes);
      return this;
    }
    
    public UserProfileDto.Builder avatarUrl(URI avatarUrl) {
      this.instance.avatarUrl(avatarUrl);
      return this;
    }
    
    public UserProfileDto.Builder bio(String bio) {
      this.instance.bio(bio);
      return this;
    }
    
    public UserProfileDto.Builder website(URI website) {
      this.instance.website(website);
      return this;
    }
    
    public UserProfileDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public UserProfileDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built UserProfileDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public UserProfileDto build() {
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
  public static UserProfileDto.Builder builder() {
    return new UserProfileDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public UserProfileDto.Builder toBuilder() {
    UserProfileDto.Builder builder = new UserProfileDto.Builder();
    return builder.copyOf(this);
  }

}

