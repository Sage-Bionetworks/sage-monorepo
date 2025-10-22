package org.sagebionetworks.bixarena.auth.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OIDC-compliant user information response
 */

@Schema(name = "UserInfo", description = "OIDC-compliant user information response")
@JsonTypeName("UserInfo")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class UserInfoDto {

  private String sub;

  private @Nullable String preferredUsername;

  private @Nullable String email;

  private @Nullable Boolean emailVerified;

  /**
   * Gets or Sets roles
   */
  public enum RolesEnum {
    USER("user"),
    
    ADMIN("admin");

    private final String value;

    RolesEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RolesEnum fromValue(String value) {
      for (RolesEnum b : RolesEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Valid
  private List<RolesEnum> roles = new ArrayList<>();

  public UserInfoDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserInfoDto(String sub) {
    this.sub = sub;
  }

  public UserInfoDto sub(String sub) {
    this.sub = sub;
    return this;
  }

  /**
   * Subject identifier - the Synapse user ID
   * @return sub
   */
  @NotNull 
  @Schema(name = "sub", example = "3350396", description = "Subject identifier - the Synapse user ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sub")
  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public UserInfoDto preferredUsername(@Nullable String preferredUsername) {
    this.preferredUsername = preferredUsername;
    return this;
  }

  /**
   * Preferred username for display
   * @return preferredUsername
   */
  
  @Schema(name = "preferred_username", example = "john.doe", description = "Preferred username for display", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferred_username")
  public @Nullable String getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(@Nullable String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }

  public UserInfoDto email(@Nullable String email) {
    this.email = email;
    return this;
  }

  /**
   * User's email address
   * @return email
   */
  @jakarta.validation.constraints.Email 
  @Schema(name = "email", example = "john.doe@example.com", description = "User's email address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public @Nullable String getEmail() {
    return email;
  }

  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  public UserInfoDto emailVerified(@Nullable Boolean emailVerified) {
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

  public UserInfoDto roles(List<RolesEnum> roles) {
    this.roles = roles;
    return this;
  }

  public UserInfoDto addRolesItem(RolesEnum rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * User roles assigned within BixArena
   * @return roles
   */
  
  @Schema(name = "roles", example = "[\"user\"]", description = "User roles assigned within BixArena", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roles")
  public List<RolesEnum> getRoles() {
    return roles;
  }

  public void setRoles(List<RolesEnum> roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserInfoDto userInfo = (UserInfoDto) o;
    return Objects.equals(this.sub, userInfo.sub) &&
        Objects.equals(this.preferredUsername, userInfo.preferredUsername) &&
        Objects.equals(this.email, userInfo.email) &&
        Objects.equals(this.emailVerified, userInfo.emailVerified) &&
        Objects.equals(this.roles, userInfo.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sub, preferredUsername, email, emailVerified, roles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfoDto {\n");
    sb.append("    sub: ").append(toIndentedString(sub)).append("\n");
    sb.append("    preferredUsername: ").append(toIndentedString(preferredUsername)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    emailVerified: ").append(toIndentedString(emailVerified)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
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

    private UserInfoDto instance;

    public Builder() {
      this(new UserInfoDto());
    }

    protected Builder(UserInfoDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(UserInfoDto value) { 
      this.instance.setSub(value.sub);
      this.instance.setPreferredUsername(value.preferredUsername);
      this.instance.setEmail(value.email);
      this.instance.setEmailVerified(value.emailVerified);
      this.instance.setRoles(value.roles);
      return this;
    }

    public UserInfoDto.Builder sub(String sub) {
      this.instance.sub(sub);
      return this;
    }
    
    public UserInfoDto.Builder preferredUsername(String preferredUsername) {
      this.instance.preferredUsername(preferredUsername);
      return this;
    }
    
    public UserInfoDto.Builder email(String email) {
      this.instance.email(email);
      return this;
    }
    
    public UserInfoDto.Builder emailVerified(Boolean emailVerified) {
      this.instance.emailVerified(emailVerified);
      return this;
    }
    
    public UserInfoDto.Builder roles(List<RolesEnum> roles) {
      this.instance.roles(roles);
      return this;
    }
    
    /**
    * returns a built UserInfoDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public UserInfoDto build() {
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
  public static UserInfoDto.Builder builder() {
    return new UserInfoDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public UserInfoDto.Builder toBuilder() {
    UserInfoDto.Builder builder = new UserInfoDto.Builder();
    return builder.copyOf(this);
  }

}

