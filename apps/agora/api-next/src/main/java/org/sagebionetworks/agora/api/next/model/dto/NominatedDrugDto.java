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
 * A nominated drug entry
 */

@Schema(name = "NominatedDrug", description = "A nominated drug entry")
@JsonTypeName("NominatedDrug")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class NominatedDrugDto {

  private String commonName;

  private Integer totalNominations;

  private Integer yearFirstNominated;

  @Valid
  private List<String> principalInvestigators = new ArrayList<>();

  @Valid
  private List<String> programs = new ArrayList<>();

  public NominatedDrugDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominatedDrugDto(String commonName, Integer totalNominations, Integer yearFirstNominated, List<String> principalInvestigators, List<String> programs) {
    this.commonName = commonName;
    this.totalNominations = totalNominations;
    this.yearFirstNominated = yearFirstNominated;
    this.principalInvestigators = principalInvestigators;
    this.programs = programs;
  }

  public NominatedDrugDto commonName(String commonName) {
    this.commonName = commonName;
    return this;
  }

  /**
   * The common name of the drug
   * @return commonName
   */
  @NotNull 
  @Schema(name = "common_name", example = "Agomelatine", description = "The common name of the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("common_name")
  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public NominatedDrugDto totalNominations(Integer totalNominations) {
    this.totalNominations = totalNominations;
    return this;
  }

  /**
   * The total number of nominations for the drug
   * @return totalNominations
   */
  @NotNull 
  @Schema(name = "total_nominations", example = "1", description = "The total number of nominations for the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total_nominations")
  public Integer getTotalNominations() {
    return totalNominations;
  }

  public void setTotalNominations(Integer totalNominations) {
    this.totalNominations = totalNominations;
  }

  public NominatedDrugDto yearFirstNominated(Integer yearFirstNominated) {
    this.yearFirstNominated = yearFirstNominated;
    return this;
  }

  /**
   * The year the drug was first nominated
   * @return yearFirstNominated
   */
  @NotNull 
  @Schema(name = "year_first_nominated", example = "2025", description = "The year the drug was first nominated", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("year_first_nominated")
  public Integer getYearFirstNominated() {
    return yearFirstNominated;
  }

  public void setYearFirstNominated(Integer yearFirstNominated) {
    this.yearFirstNominated = yearFirstNominated;
  }

  public NominatedDrugDto principalInvestigators(List<String> principalInvestigators) {
    this.principalInvestigators = principalInvestigators;
    return this;
  }

  public NominatedDrugDto addPrincipalInvestigatorsItem(String principalInvestigatorsItem) {
    if (this.principalInvestigators == null) {
      this.principalInvestigators = new ArrayList<>();
    }
    this.principalInvestigators.add(principalInvestigatorsItem);
    return this;
  }

  /**
   * The principal investigators who nominated the drug
   * @return principalInvestigators
   */
  @NotNull 
  @Schema(name = "principal_investigators", example = "[\"Xie\"]", description = "The principal investigators who nominated the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("principal_investigators")
  public List<String> getPrincipalInvestigators() {
    return principalInvestigators;
  }

  public void setPrincipalInvestigators(List<String> principalInvestigators) {
    this.principalInvestigators = principalInvestigators;
  }

  public NominatedDrugDto programs(List<String> programs) {
    this.programs = programs;
    return this;
  }

  public NominatedDrugDto addProgramsItem(String programsItem) {
    if (this.programs == null) {
      this.programs = new ArrayList<>();
    }
    this.programs.add(programsItem);
    return this;
  }

  /**
   * The programs associated with the drug nomination
   * @return programs
   */
  @NotNull 
  @Schema(name = "programs", example = "[\"ACTDRx AD\"]", description = "The programs associated with the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("programs")
  public List<String> getPrograms() {
    return programs;
  }

  public void setPrograms(List<String> programs) {
    this.programs = programs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NominatedDrugDto nominatedDrug = (NominatedDrugDto) o;
    return Objects.equals(this.commonName, nominatedDrug.commonName) &&
        Objects.equals(this.totalNominations, nominatedDrug.totalNominations) &&
        Objects.equals(this.yearFirstNominated, nominatedDrug.yearFirstNominated) &&
        Objects.equals(this.principalInvestigators, nominatedDrug.principalInvestigators) &&
        Objects.equals(this.programs, nominatedDrug.programs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commonName, totalNominations, yearFirstNominated, principalInvestigators, programs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominatedDrugDto {\n");
    sb.append("    commonName: ").append(toIndentedString(commonName)).append("\n");
    sb.append("    totalNominations: ").append(toIndentedString(totalNominations)).append("\n");
    sb.append("    yearFirstNominated: ").append(toIndentedString(yearFirstNominated)).append("\n");
    sb.append("    principalInvestigators: ").append(toIndentedString(principalInvestigators)).append("\n");
    sb.append("    programs: ").append(toIndentedString(programs)).append("\n");
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

    private NominatedDrugDto instance;

    public Builder() {
      this(new NominatedDrugDto());
    }

    protected Builder(NominatedDrugDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NominatedDrugDto value) { 
      this.instance.setCommonName(value.commonName);
      this.instance.setTotalNominations(value.totalNominations);
      this.instance.setYearFirstNominated(value.yearFirstNominated);
      this.instance.setPrincipalInvestigators(value.principalInvestigators);
      this.instance.setPrograms(value.programs);
      return this;
    }

    public NominatedDrugDto.Builder commonName(String commonName) {
      this.instance.commonName(commonName);
      return this;
    }
    
    public NominatedDrugDto.Builder totalNominations(Integer totalNominations) {
      this.instance.totalNominations(totalNominations);
      return this;
    }
    
    public NominatedDrugDto.Builder yearFirstNominated(Integer yearFirstNominated) {
      this.instance.yearFirstNominated(yearFirstNominated);
      return this;
    }
    
    public NominatedDrugDto.Builder principalInvestigators(List<String> principalInvestigators) {
      this.instance.principalInvestigators(principalInvestigators);
      return this;
    }
    
    public NominatedDrugDto.Builder programs(List<String> programs) {
      this.instance.programs(programs);
      return this;
    }
    
    /**
    * returns a built NominatedDrugDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NominatedDrugDto build() {
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
  public static NominatedDrugDto.Builder builder() {
    return new NominatedDrugDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NominatedDrugDto.Builder toBuilder() {
    NominatedDrugDto.Builder builder = new NominatedDrugDto.Builder();
    return builder.copyOf(this);
  }

}

