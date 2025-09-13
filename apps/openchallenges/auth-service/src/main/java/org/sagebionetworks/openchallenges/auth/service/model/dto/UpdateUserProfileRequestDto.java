package org.sagebionetworks.openchallenges.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateUserProfileRequestDto
 */

@JsonTypeName("UpdateUserProfileRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class UpdateUserProfileRequestDto {

  private @Nullable String firstName;

  private @Nullable String lastName;

  private @Nullable String bio;

  private @Nullable URI website;

  private @Nullable URI avatarUrl;

  public UpdateUserProfileRequestDto firstName(@Nullable String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * User's first name
   * @return firstName
   */
  @Size(max = 50) 
  @Schema(name = "firstName", example = "John", description = "User's first name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstName")
  public @Nullable String getFirstName() {
    return firstName;
  }

  public void setFirstName(@Nullable String firstName) {
    this.firstName = firstName;
  }

  public UpdateUserProfileRequestDto lastName(@Nullable String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * User's last name
   * @return lastName
   */
  @Size(max = 50) 
  @Schema(name = "lastName", example = "Doe", description = "User's last name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastName")
  public @Nullable String getLastName() {
    return lastName;
  }

  public void setLastName(@Nullable String lastName) {
    this.lastName = lastName;
  }

  public UpdateUserProfileRequestDto bio(@Nullable String bio) {
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

  public UpdateUserProfileRequestDto website(@Nullable URI website) {
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

  public UpdateUserProfileRequestDto avatarUrl(@Nullable URI avatarUrl) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateUserProfileRequestDto updateUserProfileRequest = (UpdateUserProfileRequestDto) o;
    return Objects.equals(this.firstName, updateUserProfileRequest.firstName) &&
        Objects.equals(this.lastName, updateUserProfileRequest.lastName) &&
        Objects.equals(this.bio, updateUserProfileRequest.bio) &&
        Objects.equals(this.website, updateUserProfileRequest.website) &&
        Objects.equals(this.avatarUrl, updateUserProfileRequest.avatarUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, bio, website, avatarUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateUserProfileRequestDto {\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
    sb.append("    website: ").append(toIndentedString(website)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
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

    private UpdateUserProfileRequestDto instance;

    public Builder() {
      this(new UpdateUserProfileRequestDto());
    }

    protected Builder(UpdateUserProfileRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(UpdateUserProfileRequestDto value) { 
      this.instance.setFirstName(value.firstName);
      this.instance.setLastName(value.lastName);
      this.instance.setBio(value.bio);
      this.instance.setWebsite(value.website);
      this.instance.setAvatarUrl(value.avatarUrl);
      return this;
    }

    public UpdateUserProfileRequestDto.Builder firstName(String firstName) {
      this.instance.firstName(firstName);
      return this;
    }
    
    public UpdateUserProfileRequestDto.Builder lastName(String lastName) {
      this.instance.lastName(lastName);
      return this;
    }
    
    public UpdateUserProfileRequestDto.Builder bio(String bio) {
      this.instance.bio(bio);
      return this;
    }
    
    public UpdateUserProfileRequestDto.Builder website(URI website) {
      this.instance.website(website);
      return this;
    }
    
    public UpdateUserProfileRequestDto.Builder avatarUrl(URI avatarUrl) {
      this.instance.avatarUrl(avatarUrl);
      return this;
    }
    
    /**
    * returns a built UpdateUserProfileRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public UpdateUserProfileRequestDto build() {
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
  public static UpdateUserProfileRequestDto.Builder builder() {
    return new UpdateUserProfileRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public UpdateUserProfileRequestDto.Builder toBuilder() {
    UpdateUserProfileRequestDto.Builder builder = new UpdateUserProfileRequestDto.Builder();
    return builder.copyOf(this);
  }

}

