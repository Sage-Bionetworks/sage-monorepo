package org.sagebionetworks.agora.api.model.dto;

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
 * Team Member
 */

@Schema(name = "TeamMember", description = "Team Member")
@JsonTypeName("TeamMember")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class TeamMemberDto {

  private String name;

  private Boolean isprimaryinvestigator;

  private @Nullable String url;

  public TeamMemberDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TeamMemberDto(String name, Boolean isprimaryinvestigator) {
    this.name = name;
    this.isprimaryinvestigator = isprimaryinvestigator;
  }

  public TeamMemberDto name(String name) {
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

  public TeamMemberDto isprimaryinvestigator(Boolean isprimaryinvestigator) {
    this.isprimaryinvestigator = isprimaryinvestigator;
    return this;
  }

  /**
   * Get isprimaryinvestigator
   * @return isprimaryinvestigator
   */
  @NotNull 
  @Schema(name = "isprimaryinvestigator", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isprimaryinvestigator")
  public Boolean getIsprimaryinvestigator() {
    return isprimaryinvestigator;
  }

  public void setIsprimaryinvestigator(Boolean isprimaryinvestigator) {
    this.isprimaryinvestigator = isprimaryinvestigator;
  }

  public TeamMemberDto url(@Nullable String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
   */
  
  @Schema(name = "url", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("url")
  public @Nullable String getUrl() {
    return url;
  }

  public void setUrl(@Nullable String url) {
    this.url = url;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TeamMemberDto teamMember = (TeamMemberDto) o;
    return Objects.equals(this.name, teamMember.name) &&
        Objects.equals(this.isprimaryinvestigator, teamMember.isprimaryinvestigator) &&
        Objects.equals(this.url, teamMember.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, isprimaryinvestigator, url);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TeamMemberDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    isprimaryinvestigator: ").append(toIndentedString(isprimaryinvestigator)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
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

    private TeamMemberDto instance;

    public Builder() {
      this(new TeamMemberDto());
    }

    protected Builder(TeamMemberDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(TeamMemberDto value) { 
      this.instance.setName(value.name);
      this.instance.setIsprimaryinvestigator(value.isprimaryinvestigator);
      this.instance.setUrl(value.url);
      return this;
    }

    public TeamMemberDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public TeamMemberDto.Builder isprimaryinvestigator(Boolean isprimaryinvestigator) {
      this.instance.isprimaryinvestigator(isprimaryinvestigator);
      return this;
    }
    
    public TeamMemberDto.Builder url(String url) {
      this.instance.url(url);
      return this;
    }
    
    /**
    * returns a built TeamMemberDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public TeamMemberDto build() {
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
  public static TeamMemberDto.Builder builder() {
    return new TeamMemberDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public TeamMemberDto.Builder toBuilder() {
    TeamMemberDto.Builder builder = new TeamMemberDto.Builder();
    return builder.copyOf(this);
  }

}

