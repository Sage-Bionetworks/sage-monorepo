package org.sagebionetworks.agora.api.next.model.dto;

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
 * Nominated Target object for comparison tools
 */

@Schema(name = "NominatedTarget", description = "Nominated Target object for comparison tools")
@JsonTypeName("NominatedTarget")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class NominatedTargetDto {

  private String id;

  private String ensemblGeneId;

  private String hgncSymbol;

  private Integer totalNominations;

  private Integer initialNomination;

  @Valid
  private List<String> nominatingTeams = new ArrayList<>();

  @Valid
  private List<String> cohortStudies = new ArrayList<>();

  @Valid
  private List<String> inputData = new ArrayList<>();

  @Valid
  private List<String> programs = new ArrayList<>();

  private String pharosClass;

  public NominatedTargetDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominatedTargetDto(String id, String ensemblGeneId, String hgncSymbol, Integer totalNominations, Integer initialNomination, List<String> nominatingTeams, List<String> cohortStudies, List<String> inputData, List<String> programs, String pharosClass) {
    this.id = id;
    this.ensemblGeneId = ensemblGeneId;
    this.hgncSymbol = hgncSymbol;
    this.totalNominations = totalNominations;
    this.initialNomination = initialNomination;
    this.nominatingTeams = nominatingTeams;
    this.cohortStudies = cohortStudies;
    this.inputData = inputData;
    this.programs = programs;
    this.pharosClass = pharosClass;
  }

  public NominatedTargetDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the nominated target object
   * @return id
   */
  @NotNull 
  @Schema(name = "_id", description = "Unique identifier for the nominated target object", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NominatedTargetDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Ensembl gene ID for the nominated target
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", example = "ENSG00000141510", description = "Ensembl gene ID for the nominated target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public NominatedTargetDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * HGNC gene symbol for the nominated target
   * @return hgncSymbol
   */
  @NotNull 
  @Schema(name = "hgnc_symbol", example = "TP53", description = "HGNC gene symbol for the nominated target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  public NominatedTargetDto totalNominations(Integer totalNominations) {
    this.totalNominations = totalNominations;
    return this;
  }

  /**
   * Total number of nominations for this target
   * @return totalNominations
   */
  @NotNull 
  @Schema(name = "total_nominations", example = "5", description = "Total number of nominations for this target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total_nominations")
  public Integer getTotalNominations() {
    return totalNominations;
  }

  public void setTotalNominations(Integer totalNominations) {
    this.totalNominations = totalNominations;
  }

  public NominatedTargetDto initialNomination(Integer initialNomination) {
    this.initialNomination = initialNomination;
    return this;
  }

  /**
   * Year of the initial nomination for this target
   * @return initialNomination
   */
  @NotNull 
  @Schema(name = "initial_nomination", example = "2021", description = "Year of the initial nomination for this target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("initial_nomination")
  public Integer getInitialNomination() {
    return initialNomination;
  }

  public void setInitialNomination(Integer initialNomination) {
    this.initialNomination = initialNomination;
  }

  public NominatedTargetDto nominatingTeams(List<String> nominatingTeams) {
    this.nominatingTeams = nominatingTeams;
    return this;
  }

  public NominatedTargetDto addNominatingTeamsItem(String nominatingTeamsItem) {
    if (this.nominatingTeams == null) {
      this.nominatingTeams = new ArrayList<>();
    }
    this.nominatingTeams.add(nominatingTeamsItem);
    return this;
  }

  /**
   * List of teams that nominated this target
   * @return nominatingTeams
   */
  @NotNull 
  @Schema(name = "nominating_teams", example = "[\"Duke\",\"Emory\"]", description = "List of teams that nominated this target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("nominating_teams")
  public List<String> getNominatingTeams() {
    return nominatingTeams;
  }

  public void setNominatingTeams(List<String> nominatingTeams) {
    this.nominatingTeams = nominatingTeams;
  }

  public NominatedTargetDto cohortStudies(List<String> cohortStudies) {
    this.cohortStudies = cohortStudies;
    return this;
  }

  public NominatedTargetDto addCohortStudiesItem(String cohortStudiesItem) {
    if (this.cohortStudies == null) {
      this.cohortStudies = new ArrayList<>();
    }
    this.cohortStudies.add(cohortStudiesItem);
    return this;
  }

  /**
   * List of cohort studies that nominated this target
   * @return cohortStudies
   */
  @NotNull 
  @Schema(name = "cohort_studies", example = "[\"ROSMAP\",\"Mayo\"]", description = "List of cohort studies that nominated this target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cohort_studies")
  public List<String> getCohortStudies() {
    return cohortStudies;
  }

  public void setCohortStudies(List<String> cohortStudies) {
    this.cohortStudies = cohortStudies;
  }

  public NominatedTargetDto inputData(List<String> inputData) {
    this.inputData = inputData;
    return this;
  }

  public NominatedTargetDto addInputDataItem(String inputDataItem) {
    if (this.inputData == null) {
      this.inputData = new ArrayList<>();
    }
    this.inputData.add(inputDataItem);
    return this;
  }

  /**
   * List of input data types that support this nomination
   * @return inputData
   */
  @NotNull 
  @Schema(name = "input_data", example = "[\"RNA\",\"Protein\"]", description = "List of input data types that support this nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("input_data")
  public List<String> getInputData() {
    return inputData;
  }

  public void setInputData(List<String> inputData) {
    this.inputData = inputData;
  }

  public NominatedTargetDto programs(List<String> programs) {
    this.programs = programs;
    return this;
  }

  public NominatedTargetDto addProgramsItem(String programsItem) {
    if (this.programs == null) {
      this.programs = new ArrayList<>();
    }
    this.programs.add(programsItem);
    return this;
  }

  /**
   * List of programs that nominated this target
   * @return programs
   */
  @NotNull 
  @Schema(name = "programs", example = "[\"AMP-AD\",\"Community\"]", description = "List of programs that nominated this target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("programs")
  public List<String> getPrograms() {
    return programs;
  }

  public void setPrograms(List<String> programs) {
    this.programs = programs;
  }

  public NominatedTargetDto pharosClass(String pharosClass) {
    this.pharosClass = pharosClass;
    return this;
  }

  /**
   * Pharos target development level for this target
   * @return pharosClass
   */
  @NotNull 
  @Schema(name = "pharos_class", example = "Tbio", description = "Pharos target development level for this target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pharos_class")
  public String getPharosClass() {
    return pharosClass;
  }

  public void setPharosClass(String pharosClass) {
    this.pharosClass = pharosClass;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NominatedTargetDto nominatedTarget = (NominatedTargetDto) o;
    return Objects.equals(this.id, nominatedTarget.id) &&
        Objects.equals(this.ensemblGeneId, nominatedTarget.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, nominatedTarget.hgncSymbol) &&
        Objects.equals(this.totalNominations, nominatedTarget.totalNominations) &&
        Objects.equals(this.initialNomination, nominatedTarget.initialNomination) &&
        Objects.equals(this.nominatingTeams, nominatedTarget.nominatingTeams) &&
        Objects.equals(this.cohortStudies, nominatedTarget.cohortStudies) &&
        Objects.equals(this.inputData, nominatedTarget.inputData) &&
        Objects.equals(this.programs, nominatedTarget.programs) &&
        Objects.equals(this.pharosClass, nominatedTarget.pharosClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ensemblGeneId, hgncSymbol, totalNominations, initialNomination, nominatingTeams, cohortStudies, inputData, programs, pharosClass);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominatedTargetDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    totalNominations: ").append(toIndentedString(totalNominations)).append("\n");
    sb.append("    initialNomination: ").append(toIndentedString(initialNomination)).append("\n");
    sb.append("    nominatingTeams: ").append(toIndentedString(nominatingTeams)).append("\n");
    sb.append("    cohortStudies: ").append(toIndentedString(cohortStudies)).append("\n");
    sb.append("    inputData: ").append(toIndentedString(inputData)).append("\n");
    sb.append("    programs: ").append(toIndentedString(programs)).append("\n");
    sb.append("    pharosClass: ").append(toIndentedString(pharosClass)).append("\n");
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

    private NominatedTargetDto instance;

    public Builder() {
      this(new NominatedTargetDto());
    }

    protected Builder(NominatedTargetDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NominatedTargetDto value) { 
      this.instance.setId(value.id);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setTotalNominations(value.totalNominations);
      this.instance.setInitialNomination(value.initialNomination);
      this.instance.setNominatingTeams(value.nominatingTeams);
      this.instance.setCohortStudies(value.cohortStudies);
      this.instance.setInputData(value.inputData);
      this.instance.setPrograms(value.programs);
      this.instance.setPharosClass(value.pharosClass);
      return this;
    }

    public NominatedTargetDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public NominatedTargetDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public NominatedTargetDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public NominatedTargetDto.Builder totalNominations(Integer totalNominations) {
      this.instance.totalNominations(totalNominations);
      return this;
    }
    
    public NominatedTargetDto.Builder initialNomination(Integer initialNomination) {
      this.instance.initialNomination(initialNomination);
      return this;
    }
    
    public NominatedTargetDto.Builder nominatingTeams(List<String> nominatingTeams) {
      this.instance.nominatingTeams(nominatingTeams);
      return this;
    }
    
    public NominatedTargetDto.Builder cohortStudies(List<String> cohortStudies) {
      this.instance.cohortStudies(cohortStudies);
      return this;
    }
    
    public NominatedTargetDto.Builder inputData(List<String> inputData) {
      this.instance.inputData(inputData);
      return this;
    }
    
    public NominatedTargetDto.Builder programs(List<String> programs) {
      this.instance.programs(programs);
      return this;
    }
    
    public NominatedTargetDto.Builder pharosClass(String pharosClass) {
      this.instance.pharosClass(pharosClass);
      return this;
    }
    
    /**
    * returns a built NominatedTargetDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NominatedTargetDto build() {
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
  public static NominatedTargetDto.Builder builder() {
    return new NominatedTargetDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NominatedTargetDto.Builder toBuilder() {
    NominatedTargetDto.Builder builder = new NominatedTargetDto.Builder();
    return builder.copyOf(this);
  }

}

