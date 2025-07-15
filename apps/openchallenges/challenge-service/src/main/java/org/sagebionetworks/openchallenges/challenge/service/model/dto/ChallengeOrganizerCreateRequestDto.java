package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeOrganizerRoleDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to create a challenge organizer
 */

@Schema(name = "ChallengeOrganizerCreateRequest", description = "The information used to create a challenge organizer")
@JsonTypeName("ChallengeOrganizerCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeOrganizerCreateRequestDto {

  private String name;

  private @Nullable String login;

  @Valid
  private List<ChallengeOrganizerRoleDto> roles = new ArrayList<>();

  public ChallengeOrganizerCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeOrganizerCreateRequestDto(String name) {
    this.name = name;
  }

  public ChallengeOrganizerCreateRequestDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChallengeOrganizerCreateRequestDto login(String login) {
    this.login = login;
    return this;
  }

  /**
   * The user or organization account name
   * @return login
   */
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") @Size(min = 3, max = 25) 
  @Schema(name = "login", example = "awesome-user", description = "The user or organization account name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("login")
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public ChallengeOrganizerCreateRequestDto roles(List<ChallengeOrganizerRoleDto> roles) {
    this.roles = roles;
    return this;
  }

  public ChallengeOrganizerCreateRequestDto addRolesItem(ChallengeOrganizerRoleDto rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * Get roles
   * @return roles
   */
  @Valid 
  @Schema(name = "roles", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roles")
  public List<ChallengeOrganizerRoleDto> getRoles() {
    return roles;
  }

  public void setRoles(List<ChallengeOrganizerRoleDto> roles) {
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
    ChallengeOrganizerCreateRequestDto challengeOrganizerCreateRequest = (ChallengeOrganizerCreateRequestDto) o;
    return Objects.equals(this.name, challengeOrganizerCreateRequest.name) &&
        Objects.equals(this.login, challengeOrganizerCreateRequest.login) &&
        Objects.equals(this.roles, challengeOrganizerCreateRequest.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, login, roles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeOrganizerCreateRequestDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
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

    private ChallengeOrganizerCreateRequestDto instance;

    public Builder() {
      this(new ChallengeOrganizerCreateRequestDto());
    }

    protected Builder(ChallengeOrganizerCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeOrganizerCreateRequestDto value) { 
      this.instance.setName(value.name);
      this.instance.setLogin(value.login);
      this.instance.setRoles(value.roles);
      return this;
    }

    public ChallengeOrganizerCreateRequestDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ChallengeOrganizerCreateRequestDto.Builder login(String login) {
      this.instance.login(login);
      return this;
    }
    
    public ChallengeOrganizerCreateRequestDto.Builder roles(List<ChallengeOrganizerRoleDto> roles) {
      this.instance.roles(roles);
      return this;
    }
    
    /**
    * returns a built ChallengeOrganizerCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeOrganizerCreateRequestDto build() {
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
  public static ChallengeOrganizerCreateRequestDto.Builder builder() {
    return new ChallengeOrganizerCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeOrganizerCreateRequestDto.Builder toBuilder() {
    ChallengeOrganizerCreateRequestDto.Builder builder = new ChallengeOrganizerCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

