package org.sagebionetworks.agora.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.next.model.dto.ModalityDto;
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

  private String compositeId;

  private String commonName;

  private String chemblId;

  private Integer totalNominations;

  private String combinedWith = null;

  private Integer initialNomination;

  @Valid
  private List<String> principalInvestigators = new ArrayList<>();

  @Valid
  private List<String> programs = new ArrayList<>();

  private ModalityDto modality = null;

  private Integer yearOfFirstApproval = null;

  private String maximumClinicalTrialPhase = null;

  public NominatedDrugDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominatedDrugDto(String compositeId, String commonName, String chemblId, Integer totalNominations, String combinedWith, Integer initialNomination, List<String> principalInvestigators, List<String> programs, ModalityDto modality, Integer yearOfFirstApproval, String maximumClinicalTrialPhase) {
    this.compositeId = compositeId;
    this.commonName = commonName;
    this.chemblId = chemblId;
    this.totalNominations = totalNominations;
    this.combinedWith = combinedWith;
    this.initialNomination = initialNomination;
    this.principalInvestigators = principalInvestigators;
    this.programs = programs;
    this.modality = modality;
    this.yearOfFirstApproval = yearOfFirstApproval;
    this.maximumClinicalTrialPhase = maximumClinicalTrialPhase;
  }

  public NominatedDrugDto compositeId(String compositeId) {
    this.compositeId = compositeId;
    return this;
  }

  /**
   * Unique identifier for the nominated drug entry
   * @return compositeId
   */
  @NotNull 
  @Schema(name = "composite_id", example = "CHEMBL2105758~null", description = "Unique identifier for the nominated drug entry", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("composite_id")
  public String getCompositeId() {
    return compositeId;
  }

  public void setCompositeId(String compositeId) {
    this.compositeId = compositeId;
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

  public NominatedDrugDto chemblId(String chemblId) {
    this.chemblId = chemblId;
    return this;
  }

  /**
   * The ChEMBL ID of the drug
   * @return chemblId
   */
  @NotNull 
  @Schema(name = "chembl_id", example = "CHEMBL2105758", description = "The ChEMBL ID of the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("chembl_id")
  public String getChemblId() {
    return chemblId;
  }

  public void setChemblId(String chemblId) {
    this.chemblId = chemblId;
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

  public NominatedDrugDto combinedWith(String combinedWith) {
    this.combinedWith = combinedWith;
    return this;
  }

  /**
   * The name of the drug this is combined with, if applicable
   * @return combinedWith
   */
  @NotNull 
  @Schema(name = "combined_with", description = "The name of the drug this is combined with, if applicable", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("combined_with")
  public String getCombinedWith() {
    return combinedWith;
  }

  public void setCombinedWith(String combinedWith) {
    this.combinedWith = combinedWith;
  }

  public NominatedDrugDto initialNomination(Integer initialNomination) {
    this.initialNomination = initialNomination;
    return this;
  }

  /**
   * The year the drug was first nominated
   * @return initialNomination
   */
  @NotNull 
  @Schema(name = "initial_nomination", example = "2025", description = "The year the drug was first nominated", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("initial_nomination")
  public Integer getInitialNomination() {
    return initialNomination;
  }

  public void setInitialNomination(Integer initialNomination) {
    this.initialNomination = initialNomination;
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

  public NominatedDrugDto modality(ModalityDto modality) {
    this.modality = modality;
    return this;
  }

  /**
   * Get modality
   * @return modality
   */
  @NotNull @Valid 
  @Schema(name = "modality", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modality")
  public ModalityDto getModality() {
    return modality;
  }

  public void setModality(ModalityDto modality) {
    this.modality = modality;
  }

  public NominatedDrugDto yearOfFirstApproval(Integer yearOfFirstApproval) {
    this.yearOfFirstApproval = yearOfFirstApproval;
    return this;
  }

  /**
   * The year the drug was first approved, if applicable
   * @return yearOfFirstApproval
   */
  @NotNull 
  @Schema(name = "year_of_first_approval", example = "2010", description = "The year the drug was first approved, if applicable", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("year_of_first_approval")
  public Integer getYearOfFirstApproval() {
    return yearOfFirstApproval;
  }

  public void setYearOfFirstApproval(Integer yearOfFirstApproval) {
    this.yearOfFirstApproval = yearOfFirstApproval;
  }

  public NominatedDrugDto maximumClinicalTrialPhase(String maximumClinicalTrialPhase) {
    this.maximumClinicalTrialPhase = maximumClinicalTrialPhase;
    return this;
  }

  /**
   * The maximum clinical trial phase the drug has reached, if applicable
   * @return maximumClinicalTrialPhase
   */
  @NotNull 
  @Schema(name = "maximum_clinical_trial_phase", example = "Phase IV", description = "The maximum clinical trial phase the drug has reached, if applicable", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("maximum_clinical_trial_phase")
  public String getMaximumClinicalTrialPhase() {
    return maximumClinicalTrialPhase;
  }

  public void setMaximumClinicalTrialPhase(String maximumClinicalTrialPhase) {
    this.maximumClinicalTrialPhase = maximumClinicalTrialPhase;
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
    return Objects.equals(this.compositeId, nominatedDrug.compositeId) &&
        Objects.equals(this.commonName, nominatedDrug.commonName) &&
        Objects.equals(this.chemblId, nominatedDrug.chemblId) &&
        Objects.equals(this.totalNominations, nominatedDrug.totalNominations) &&
        Objects.equals(this.combinedWith, nominatedDrug.combinedWith) &&
        Objects.equals(this.initialNomination, nominatedDrug.initialNomination) &&
        Objects.equals(this.principalInvestigators, nominatedDrug.principalInvestigators) &&
        Objects.equals(this.programs, nominatedDrug.programs) &&
        Objects.equals(this.modality, nominatedDrug.modality) &&
        Objects.equals(this.yearOfFirstApproval, nominatedDrug.yearOfFirstApproval) &&
        Objects.equals(this.maximumClinicalTrialPhase, nominatedDrug.maximumClinicalTrialPhase);
  }

  @Override
  public int hashCode() {
    return Objects.hash(compositeId, commonName, chemblId, totalNominations, combinedWith, initialNomination, principalInvestigators, programs, modality, yearOfFirstApproval, maximumClinicalTrialPhase);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominatedDrugDto {\n");
    sb.append("    compositeId: ").append(toIndentedString(compositeId)).append("\n");
    sb.append("    commonName: ").append(toIndentedString(commonName)).append("\n");
    sb.append("    chemblId: ").append(toIndentedString(chemblId)).append("\n");
    sb.append("    totalNominations: ").append(toIndentedString(totalNominations)).append("\n");
    sb.append("    combinedWith: ").append(toIndentedString(combinedWith)).append("\n");
    sb.append("    initialNomination: ").append(toIndentedString(initialNomination)).append("\n");
    sb.append("    principalInvestigators: ").append(toIndentedString(principalInvestigators)).append("\n");
    sb.append("    programs: ").append(toIndentedString(programs)).append("\n");
    sb.append("    modality: ").append(toIndentedString(modality)).append("\n");
    sb.append("    yearOfFirstApproval: ").append(toIndentedString(yearOfFirstApproval)).append("\n");
    sb.append("    maximumClinicalTrialPhase: ").append(toIndentedString(maximumClinicalTrialPhase)).append("\n");
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
      this.instance.setCompositeId(value.compositeId);
      this.instance.setCommonName(value.commonName);
      this.instance.setChemblId(value.chemblId);
      this.instance.setTotalNominations(value.totalNominations);
      this.instance.setCombinedWith(value.combinedWith);
      this.instance.setInitialNomination(value.initialNomination);
      this.instance.setPrincipalInvestigators(value.principalInvestigators);
      this.instance.setPrograms(value.programs);
      this.instance.setModality(value.modality);
      this.instance.setYearOfFirstApproval(value.yearOfFirstApproval);
      this.instance.setMaximumClinicalTrialPhase(value.maximumClinicalTrialPhase);
      return this;
    }

    public NominatedDrugDto.Builder compositeId(String compositeId) {
      this.instance.compositeId(compositeId);
      return this;
    }
    
    public NominatedDrugDto.Builder commonName(String commonName) {
      this.instance.commonName(commonName);
      return this;
    }
    
    public NominatedDrugDto.Builder chemblId(String chemblId) {
      this.instance.chemblId(chemblId);
      return this;
    }
    
    public NominatedDrugDto.Builder totalNominations(Integer totalNominations) {
      this.instance.totalNominations(totalNominations);
      return this;
    }
    
    public NominatedDrugDto.Builder combinedWith(String combinedWith) {
      this.instance.combinedWith(combinedWith);
      return this;
    }
    
    public NominatedDrugDto.Builder initialNomination(Integer initialNomination) {
      this.instance.initialNomination(initialNomination);
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
    
    public NominatedDrugDto.Builder modality(ModalityDto modality) {
      this.instance.modality(modality);
      return this;
    }
    
    public NominatedDrugDto.Builder yearOfFirstApproval(Integer yearOfFirstApproval) {
      this.instance.yearOfFirstApproval(yearOfFirstApproval);
      return this;
    }
    
    public NominatedDrugDto.Builder maximumClinicalTrialPhase(String maximumClinicalTrialPhase) {
      this.instance.maximumClinicalTrialPhase(maximumClinicalTrialPhase);
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

