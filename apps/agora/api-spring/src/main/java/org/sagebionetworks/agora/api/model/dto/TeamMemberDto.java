package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Team Member
 */

@Schema(name = "TeamMember", description = "Team Member")
@JsonTypeName("TeamMember")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class TeamMemberDto {

  private String name;

  private Boolean isPrimaryInvestigator;

  private String url;

  public TeamMemberDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TeamMemberDto(String name, Boolean isPrimaryInvestigator) {
    this.name = name;
    this.isPrimaryInvestigator = isPrimaryInvestigator;
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

  public TeamMemberDto isPrimaryInvestigator(Boolean isPrimaryInvestigator) {
    this.isPrimaryInvestigator = isPrimaryInvestigator;
    return this;
  }

  /**
   * Get isPrimaryInvestigator
   * @return isPrimaryInvestigator
  */
  @NotNull 
  @Schema(name = "isPrimaryInvestigator", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isPrimaryInvestigator")
  public Boolean getIsPrimaryInvestigator() {
    return isPrimaryInvestigator;
  }

  public void setIsPrimaryInvestigator(Boolean isPrimaryInvestigator) {
    this.isPrimaryInvestigator = isPrimaryInvestigator;
  }

  public TeamMemberDto url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
  */
  
  @Schema(name = "url", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
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
        Objects.equals(this.isPrimaryInvestigator, teamMember.isPrimaryInvestigator) &&
        Objects.equals(this.url, teamMember.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, isPrimaryInvestigator, url);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TeamMemberDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    isPrimaryInvestigator: ").append(toIndentedString(isPrimaryInvestigator)).append("\n");
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
}

