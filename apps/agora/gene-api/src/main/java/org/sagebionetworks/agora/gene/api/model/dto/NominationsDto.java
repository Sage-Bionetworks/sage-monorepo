package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * A gene nomination.
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "Nominations", description = "A gene nomination.")
@JsonTypeName("Nominations")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class NominationsDto {

  private Integer count;

  private Integer year;

  @Valid
  private List<String> teams = new ArrayList<>();

  @Valid
  private List<String> studies = new ArrayList<>();

  @Valid
  private List<String> inputs = new ArrayList<>();

  @Valid
  private List<String> programs = new ArrayList<>();

  @Valid
  private List<String> validations = new ArrayList<>();

  public NominationsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominationsDto(Integer count, Integer year, List<String> teams, List<String> studies, List<String> inputs, List<String> programs, List<String> validations) {
    this.count = count;
    this.year = year;
    this.teams = teams;
    this.studies = studies;
    this.inputs = inputs;
    this.programs = programs;
    this.validations = validations;
  }

  public NominationsDto count(Integer count) {
    this.count = count;
    return this;
  }

  /**
   * The total number of gene nominations.
   * @return count
   */
  @NotNull 
  @Schema(name = "count", description = "The total number of gene nominations.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("count")
  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public NominationsDto year(Integer year) {
    this.year = year;
    return this;
  }

  /**
   * The year of the nominations.
   * @return year
   */
  @NotNull 
  @Schema(name = "year", description = "The year of the nominations.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("year")
  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public NominationsDto teams(List<String> teams) {
    this.teams = teams;
    return this;
  }

  public NominationsDto addTeamsItem(String teamsItem) {
    if (this.teams == null) {
      this.teams = new ArrayList<>();
    }
    this.teams.add(teamsItem);
    return this;
  }

  /**
   * The list of teams involved in the nominations.
   * @return teams
   */
  @NotNull 
  @Schema(name = "teams", description = "The list of teams involved in the nominations.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("teams")
  public List<String> getTeams() {
    return teams;
  }

  public void setTeams(List<String> teams) {
    this.teams = teams;
  }

  public NominationsDto studies(List<String> studies) {
    this.studies = studies;
    return this;
  }

  public NominationsDto addStudiesItem(String studiesItem) {
    if (this.studies == null) {
      this.studies = new ArrayList<>();
    }
    this.studies.add(studiesItem);
    return this;
  }

  /**
   * The list of studies related to the nominations.
   * @return studies
   */
  @NotNull 
  @Schema(name = "studies", description = "The list of studies related to the nominations.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("studies")
  public List<String> getStudies() {
    return studies;
  }

  public void setStudies(List<String> studies) {
    this.studies = studies;
  }

  public NominationsDto inputs(List<String> inputs) {
    this.inputs = inputs;
    return this;
  }

  public NominationsDto addInputsItem(String inputsItem) {
    if (this.inputs == null) {
      this.inputs = new ArrayList<>();
    }
    this.inputs.add(inputsItem);
    return this;
  }

  /**
   * The input data used for the nominations.
   * @return inputs
   */
  @NotNull 
  @Schema(name = "inputs", description = "The input data used for the nominations.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("inputs")
  public List<String> getInputs() {
    return inputs;
  }

  public void setInputs(List<String> inputs) {
    this.inputs = inputs;
  }

  public NominationsDto programs(List<String> programs) {
    this.programs = programs;
    return this;
  }

  public NominationsDto addProgramsItem(String programsItem) {
    if (this.programs == null) {
      this.programs = new ArrayList<>();
    }
    this.programs.add(programsItem);
    return this;
  }

  /**
   * The list of programs associated with the nominations.
   * @return programs
   */
  @NotNull 
  @Schema(name = "programs", description = "The list of programs associated with the nominations.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("programs")
  public List<String> getPrograms() {
    return programs;
  }

  public void setPrograms(List<String> programs) {
    this.programs = programs;
  }

  public NominationsDto validations(List<String> validations) {
    this.validations = validations;
    return this;
  }

  public NominationsDto addValidationsItem(String validationsItem) {
    if (this.validations == null) {
      this.validations = new ArrayList<>();
    }
    this.validations.add(validationsItem);
    return this;
  }

  /**
   * The list of validations for the nominations.
   * @return validations
   */
  @NotNull 
  @Schema(name = "validations", description = "The list of validations for the nominations.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("validations")
  public List<String> getValidations() {
    return validations;
  }

  public void setValidations(List<String> validations) {
    this.validations = validations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NominationsDto nominations = (NominationsDto) o;
    return Objects.equals(this.count, nominations.count) &&
        Objects.equals(this.year, nominations.year) &&
        Objects.equals(this.teams, nominations.teams) &&
        Objects.equals(this.studies, nominations.studies) &&
        Objects.equals(this.inputs, nominations.inputs) &&
        Objects.equals(this.programs, nominations.programs) &&
        Objects.equals(this.validations, nominations.validations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(count, year, teams, studies, inputs, programs, validations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominationsDto {\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    year: ").append(toIndentedString(year)).append("\n");
    sb.append("    teams: ").append(toIndentedString(teams)).append("\n");
    sb.append("    studies: ").append(toIndentedString(studies)).append("\n");
    sb.append("    inputs: ").append(toIndentedString(inputs)).append("\n");
    sb.append("    programs: ").append(toIndentedString(programs)).append("\n");
    sb.append("    validations: ").append(toIndentedString(validations)).append("\n");
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

    private NominationsDto instance;

    public Builder() {
      this(new NominationsDto());
    }

    protected Builder(NominationsDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NominationsDto value) { 
      this.instance.setCount(value.count);
      this.instance.setYear(value.year);
      this.instance.setTeams(value.teams);
      this.instance.setStudies(value.studies);
      this.instance.setInputs(value.inputs);
      this.instance.setPrograms(value.programs);
      this.instance.setValidations(value.validations);
      return this;
    }

    public NominationsDto.Builder count(Integer count) {
      this.instance.count(count);
      return this;
    }
    
    public NominationsDto.Builder year(Integer year) {
      this.instance.year(year);
      return this;
    }
    
    public NominationsDto.Builder teams(List<String> teams) {
      this.instance.teams(teams);
      return this;
    }
    
    public NominationsDto.Builder studies(List<String> studies) {
      this.instance.studies(studies);
      return this;
    }
    
    public NominationsDto.Builder inputs(List<String> inputs) {
      this.instance.inputs(inputs);
      return this;
    }
    
    public NominationsDto.Builder programs(List<String> programs) {
      this.instance.programs(programs);
      return this;
    }
    
    public NominationsDto.Builder validations(List<String> validations) {
      this.instance.validations(validations);
      return this;
    }
    
    /**
    * returns a built NominationsDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NominationsDto build() {
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
  public static NominationsDto.Builder builder() {
    return new NominationsDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NominationsDto.Builder toBuilder() {
    NominationsDto.Builder builder = new NominationsDto.Builder();
    return builder.copyOf(this);
  }

}

