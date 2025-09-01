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
 * GetCurrentUser200ResponseDto
 */

@JsonTypeName("getCurrentUser_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GetCurrentUser200ResponseDto {

  private String sub;

  private @Nullable String name;

  private @Nullable String givenName;

  private @Nullable String familyName;

  private @Nullable String preferredUsername;

  private @Nullable String email;

  private @Nullable Boolean emailVerified;

  private @Nullable URI picture;

  private @Nullable URI website;

  private @Nullable String locale;

  private @Nullable Integer updatedAt;

  public GetCurrentUser200ResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GetCurrentUser200ResponseDto(String sub) {
    this.sub = sub;
  }

  public GetCurrentUser200ResponseDto sub(String sub) {
    this.sub = sub;
    return this;
  }

  /**
   * Subject identifier (user ID)
   * @return sub
   */
  @NotNull 
  @Schema(name = "sub", example = "248289761001", description = "Subject identifier (user ID)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sub")
  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public GetCurrentUser200ResponseDto name(@Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Full name of the user
   * @return name
   */
  
  @Schema(name = "name", example = "Jane Doe", description = "Full name of the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public @Nullable String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public GetCurrentUser200ResponseDto givenName(@Nullable String givenName) {
    this.givenName = givenName;
    return this;
  }

  /**
   * Given name of the user
   * @return givenName
   */
  
  @Schema(name = "given_name", example = "Jane", description = "Given name of the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("given_name")
  public @Nullable String getGivenName() {
    return givenName;
  }

  public void setGivenName(@Nullable String givenName) {
    this.givenName = givenName;
  }

  public GetCurrentUser200ResponseDto familyName(@Nullable String familyName) {
    this.familyName = familyName;
    return this;
  }

  /**
   * Family name of the user
   * @return familyName
   */
  
  @Schema(name = "family_name", example = "Doe", description = "Family name of the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("family_name")
  public @Nullable String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(@Nullable String familyName) {
    this.familyName = familyName;
  }

  public GetCurrentUser200ResponseDto preferredUsername(@Nullable String preferredUsername) {
    this.preferredUsername = preferredUsername;
    return this;
  }

  /**
   * Preferred username
   * @return preferredUsername
   */
  
  @Schema(name = "preferred_username", example = "j.doe", description = "Preferred username", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferred_username")
  public @Nullable String getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(@Nullable String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }

  public GetCurrentUser200ResponseDto email(@Nullable String email) {
    this.email = email;
    return this;
  }

  /**
   * Email address
   * @return email
   */
  @jakarta.validation.constraints.Email 
  @Schema(name = "email", example = "janedoe@example.com", description = "Email address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public @Nullable String getEmail() {
    return email;
  }

  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  public GetCurrentUser200ResponseDto emailVerified(@Nullable Boolean emailVerified) {
    this.emailVerified = emailVerified;
    return this;
  }

  /**
   * Whether the email address has been verified
   * @return emailVerified
   */
  
  @Schema(name = "email_verified", example = "true", description = "Whether the email address has been verified", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email_verified")
  public @Nullable Boolean getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(@Nullable Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public GetCurrentUser200ResponseDto picture(@Nullable URI picture) {
    this.picture = picture;
    return this;
  }

  /**
   * Profile picture URL
   * @return picture
   */
  @Valid 
  @Schema(name = "picture", example = "https://example.com/profile.jpg", description = "Profile picture URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("picture")
  public @Nullable URI getPicture() {
    return picture;
  }

  public void setPicture(@Nullable URI picture) {
    this.picture = picture;
  }

  public GetCurrentUser200ResponseDto website(@Nullable URI website) {
    this.website = website;
    return this;
  }

  /**
   * User's website
   * @return website
   */
  @Valid 
  @Schema(name = "website", example = "https://janedoe.example.com", description = "User's website", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("website")
  public @Nullable URI getWebsite() {
    return website;
  }

  public void setWebsite(@Nullable URI website) {
    this.website = website;
  }

  public GetCurrentUser200ResponseDto locale(@Nullable String locale) {
    this.locale = locale;
    return this;
  }

  /**
   * User's locale
   * @return locale
   */
  
  @Schema(name = "locale", example = "en-US", description = "User's locale", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locale")
  public @Nullable String getLocale() {
    return locale;
  }

  public void setLocale(@Nullable String locale) {
    this.locale = locale;
  }

  public GetCurrentUser200ResponseDto updatedAt(@Nullable Integer updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Time the user's information was last updated
   * @return updatedAt
   */
  
  @Schema(name = "updated_at", example = "1311280970", description = "Time the user's information was last updated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updated_at")
  public @Nullable Integer getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(@Nullable Integer updatedAt) {
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
    GetCurrentUser200ResponseDto getCurrentUser200Response = (GetCurrentUser200ResponseDto) o;
    return Objects.equals(this.sub, getCurrentUser200Response.sub) &&
        Objects.equals(this.name, getCurrentUser200Response.name) &&
        Objects.equals(this.givenName, getCurrentUser200Response.givenName) &&
        Objects.equals(this.familyName, getCurrentUser200Response.familyName) &&
        Objects.equals(this.preferredUsername, getCurrentUser200Response.preferredUsername) &&
        Objects.equals(this.email, getCurrentUser200Response.email) &&
        Objects.equals(this.emailVerified, getCurrentUser200Response.emailVerified) &&
        Objects.equals(this.picture, getCurrentUser200Response.picture) &&
        Objects.equals(this.website, getCurrentUser200Response.website) &&
        Objects.equals(this.locale, getCurrentUser200Response.locale) &&
        Objects.equals(this.updatedAt, getCurrentUser200Response.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sub, name, givenName, familyName, preferredUsername, email, emailVerified, picture, website, locale, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetCurrentUser200ResponseDto {\n");
    sb.append("    sub: ").append(toIndentedString(sub)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
    sb.append("    preferredUsername: ").append(toIndentedString(preferredUsername)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    emailVerified: ").append(toIndentedString(emailVerified)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    website: ").append(toIndentedString(website)).append("\n");
    sb.append("    locale: ").append(toIndentedString(locale)).append("\n");
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

    private GetCurrentUser200ResponseDto instance;

    public Builder() {
      this(new GetCurrentUser200ResponseDto());
    }

    protected Builder(GetCurrentUser200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GetCurrentUser200ResponseDto value) { 
      this.instance.setSub(value.sub);
      this.instance.setName(value.name);
      this.instance.setGivenName(value.givenName);
      this.instance.setFamilyName(value.familyName);
      this.instance.setPreferredUsername(value.preferredUsername);
      this.instance.setEmail(value.email);
      this.instance.setEmailVerified(value.emailVerified);
      this.instance.setPicture(value.picture);
      this.instance.setWebsite(value.website);
      this.instance.setLocale(value.locale);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public GetCurrentUser200ResponseDto.Builder sub(String sub) {
      this.instance.sub(sub);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder givenName(String givenName) {
      this.instance.givenName(givenName);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder familyName(String familyName) {
      this.instance.familyName(familyName);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder preferredUsername(String preferredUsername) {
      this.instance.preferredUsername(preferredUsername);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder email(String email) {
      this.instance.email(email);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder emailVerified(Boolean emailVerified) {
      this.instance.emailVerified(emailVerified);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder picture(URI picture) {
      this.instance.picture(picture);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder website(URI website) {
      this.instance.website(website);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder locale(String locale) {
      this.instance.locale(locale);
      return this;
    }
    
    public GetCurrentUser200ResponseDto.Builder updatedAt(Integer updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built GetCurrentUser200ResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GetCurrentUser200ResponseDto build() {
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
  public static GetCurrentUser200ResponseDto.Builder builder() {
    return new GetCurrentUser200ResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GetCurrentUser200ResponseDto.Builder toBuilder() {
    GetCurrentUser200ResponseDto.Builder builder = new GetCurrentUser200ResponseDto.Builder();
    return builder.copyOf(this);
  }

}

