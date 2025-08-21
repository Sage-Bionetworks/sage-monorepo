package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.model.dto.TeamMemberDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Team
 */

@Schema(name = "Team", description = "Team")
@JsonTypeName("Team")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class TeamDto {

  private String team;

  private String teamFull;

  private String program;

  private String description;

  @Valid
  private List<@Valid TeamMemberDto> members = new ArrayList<>();

  public TeamDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TeamDto(String team, String teamFull, String program, String description, List<@Valid TeamMemberDto> members) {
    this.team = team;
    this.teamFull = teamFull;
    this.program = program;
    this.description = description;
    this.members = members;
  }

  public TeamDto team(String team) {
    this.team = team;
    return this;
  }

  /**
   * Get team
   * @return team
   */
  @NotNull 
  @Schema(name = "team", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("team")
  public String getTeam() {
    return team;
  }

  public void setTeam(String team) {
    this.team = team;
  }

  public TeamDto teamFull(String teamFull) {
    this.teamFull = teamFull;
    return this;
  }

  /**
   * Get teamFull
   * @return teamFull
   */
  @NotNull 
  @Schema(name = "team_full", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("team_full")
  public String getTeamFull() {
    return teamFull;
  }

  public void setTeamFull(String teamFull) {
    this.teamFull = teamFull;
  }

  public TeamDto program(String program) {
    this.program = program;
    return this;
  }

  /**
   * Get program
   * @return program
   */
  @NotNull 
  @Schema(name = "program", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("program")
  public String getProgram() {
    return program;
  }

  public void setProgram(String program) {
    this.program = program;
  }

  public TeamDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  @NotNull 
  @Schema(name = "description", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TeamDto members(List<@Valid TeamMemberDto> members) {
    this.members = members;
    return this;
  }

  public TeamDto addMembersItem(TeamMemberDto membersItem) {
    if (this.members == null) {
      this.members = new ArrayList<>();
    }
    this.members.add(membersItem);
    return this;
  }

  /**
   * Get members
   * @return members
   */
  @NotNull @Valid 
  @Schema(name = "members", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("members")
  public List<@Valid TeamMemberDto> getMembers() {
    return members;
  }

  public void setMembers(List<@Valid TeamMemberDto> members) {
    this.members = members;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TeamDto team = (TeamDto) o;
    return Objects.equals(this.team, team.team) &&
        Objects.equals(this.teamFull, team.teamFull) &&
        Objects.equals(this.program, team.program) &&
        Objects.equals(this.description, team.description) &&
        Objects.equals(this.members, team.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(team, teamFull, program, description, members);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TeamDto {\n");
    sb.append("    team: ").append(toIndentedString(team)).append("\n");
    sb.append("    teamFull: ").append(toIndentedString(teamFull)).append("\n");
    sb.append("    program: ").append(toIndentedString(program)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    members: ").append(toIndentedString(members)).append("\n");
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

    private TeamDto instance;

    public Builder() {
      this(new TeamDto());
    }

    protected Builder(TeamDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(TeamDto value) { 
      this.instance.setTeam(value.team);
      this.instance.setTeamFull(value.teamFull);
      this.instance.setProgram(value.program);
      this.instance.setDescription(value.description);
      this.instance.setMembers(value.members);
      return this;
    }

    public TeamDto.Builder team(String team) {
      this.instance.team(team);
      return this;
    }
    
    public TeamDto.Builder teamFull(String teamFull) {
      this.instance.teamFull(teamFull);
      return this;
    }
    
    public TeamDto.Builder program(String program) {
      this.instance.program(program);
      return this;
    }
    
    public TeamDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public TeamDto.Builder members(List<TeamMemberDto> members) {
      this.instance.members(members);
      return this;
    }
    
    /**
    * returns a built TeamDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public TeamDto build() {
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
  public static TeamDto.Builder builder() {
    return new TeamDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public TeamDto.Builder toBuilder() {
    TeamDto.Builder builder = new TeamDto.Builder();
    return builder.copyOf(this);
  }

}

