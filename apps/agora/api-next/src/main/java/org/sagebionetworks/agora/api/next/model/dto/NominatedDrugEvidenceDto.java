package org.sagebionetworks.agora.api.next.model.dto;

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
 * Evidence for a drug nomination
 */

@Schema(name = "NominatedDrugEvidence", description = "Evidence for a drug nomination")
@JsonTypeName("NominatedDrugEvidence")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class NominatedDrugEvidenceDto {

  private String grantNumber;

  private String contactPi;

  private String combinedWithCommonName = null;

  private String combinedWithChemblId = null;

  private String evidence;

  private String dataUsed;

  private String adMoa = null;

  private String reference = null;

  private String computationalValidationStatus;

  private String computationalValidationResults = null;

  private String experimentalValidationStatus;

  private String experimentalValidationResults = null;

  private String additionalEvidence = null;

  private String contributors = null;

  private Integer initialNomination;

  private String program;

  public NominatedDrugEvidenceDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominatedDrugEvidenceDto(String grantNumber, String contactPi, String combinedWithCommonName, String combinedWithChemblId, String evidence, String dataUsed, String adMoa, String reference, String computationalValidationStatus, String computationalValidationResults, String experimentalValidationStatus, String experimentalValidationResults, String additionalEvidence, String contributors, Integer initialNomination, String program) {
    this.grantNumber = grantNumber;
    this.contactPi = contactPi;
    this.combinedWithCommonName = combinedWithCommonName;
    this.combinedWithChemblId = combinedWithChemblId;
    this.evidence = evidence;
    this.dataUsed = dataUsed;
    this.adMoa = adMoa;
    this.reference = reference;
    this.computationalValidationStatus = computationalValidationStatus;
    this.computationalValidationResults = computationalValidationResults;
    this.experimentalValidationStatus = experimentalValidationStatus;
    this.experimentalValidationResults = experimentalValidationResults;
    this.additionalEvidence = additionalEvidence;
    this.contributors = contributors;
    this.initialNomination = initialNomination;
    this.program = program;
  }

  public NominatedDrugEvidenceDto grantNumber(String grantNumber) {
    this.grantNumber = grantNumber;
    return this;
  }

  /**
   * The grant number associated with the drug nomination
   * @return grantNumber
   */
  @NotNull 
  @Schema(name = "grant_number", example = "R01AG123456", description = "The grant number associated with the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("grant_number")
  public String getGrantNumber() {
    return grantNumber;
  }

  public void setGrantNumber(String grantNumber) {
    this.grantNumber = grantNumber;
  }

  public NominatedDrugEvidenceDto contactPi(String contactPi) {
    this.contactPi = contactPi;
    return this;
  }

  /**
   * The contact principal investigator for the drug nomination
   * @return contactPi
   */
  @NotNull 
  @Schema(name = "contact_pi", example = "Dr. Jane Doe", description = "The contact principal investigator for the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contact_pi")
  public String getContactPi() {
    return contactPi;
  }

  public void setContactPi(String contactPi) {
    this.contactPi = contactPi;
  }

  public NominatedDrugEvidenceDto combinedWithCommonName(String combinedWithCommonName) {
    this.combinedWithCommonName = combinedWithCommonName;
    return this;
  }

  /**
   * The common name of the drug this is combined with, if applicable
   * @return combinedWithCommonName
   */
  @NotNull 
  @Schema(name = "combined_with_common_name", description = "The common name of the drug this is combined with, if applicable", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("combined_with_common_name")
  public String getCombinedWithCommonName() {
    return combinedWithCommonName;
  }

  public void setCombinedWithCommonName(String combinedWithCommonName) {
    this.combinedWithCommonName = combinedWithCommonName;
  }

  public NominatedDrugEvidenceDto combinedWithChemblId(String combinedWithChemblId) {
    this.combinedWithChemblId = combinedWithChemblId;
    return this;
  }

  /**
   * The ChEMBL ID of the drug this is combined with, if applicable
   * @return combinedWithChemblId
   */
  @NotNull 
  @Schema(name = "combined_with_chembl_id", description = "The ChEMBL ID of the drug this is combined with, if applicable", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("combined_with_chembl_id")
  public String getCombinedWithChemblId() {
    return combinedWithChemblId;
  }

  public void setCombinedWithChemblId(String combinedWithChemblId) {
    this.combinedWithChemblId = combinedWithChemblId;
  }

  public NominatedDrugEvidenceDto evidence(String evidence) {
    this.evidence = evidence;
    return this;
  }

  /**
   * The evidence supporting the drug nomination
   * @return evidence
   */
  @NotNull 
  @Schema(name = "evidence", example = "Preclinical studies showing efficacy in AD mouse models", description = "The evidence supporting the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("evidence")
  public String getEvidence() {
    return evidence;
  }

  public void setEvidence(String evidence) {
    this.evidence = evidence;
  }

  public NominatedDrugEvidenceDto dataUsed(String dataUsed) {
    this.dataUsed = dataUsed;
    return this;
  }

  /**
   * The data used to support the drug nomination
   * @return dataUsed
   */
  @NotNull 
  @Schema(name = "data_used", example = "transcriptomics data, drug target", description = "The data used to support the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data_used")
  public String getDataUsed() {
    return dataUsed;
  }

  public void setDataUsed(String dataUsed) {
    this.dataUsed = dataUsed;
  }

  public NominatedDrugEvidenceDto adMoa(String adMoa) {
    this.adMoa = adMoa;
    return this;
  }

  /**
   * The proposed mechanism of action for Alzheimer's disease
   * @return adMoa
   */
  @NotNull 
  @Schema(name = "ad_moa", example = "DDIT3", description = "The proposed mechanism of action for Alzheimer's disease", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ad_moa")
  public String getAdMoa() {
    return adMoa;
  }

  public void setAdMoa(String adMoa) {
    this.adMoa = adMoa;
  }

  public NominatedDrugEvidenceDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * A reference supporting the drug nomination
   * @return reference
   */
  @NotNull 
  @Schema(name = "reference", example = "https://doi.org/10.1101/2023.12.26.573348", description = "A reference supporting the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public NominatedDrugEvidenceDto computationalValidationStatus(String computationalValidationStatus) {
    this.computationalValidationStatus = computationalValidationStatus;
    return this;
  }

  /**
   * The status of computational validation for the drug nomination
   * @return computationalValidationStatus
   */
  @NotNull 
  @Schema(name = "computational_validation_status", example = "computational validation studies completed", description = "The status of computational validation for the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("computational_validation_status")
  public String getComputationalValidationStatus() {
    return computationalValidationStatus;
  }

  public void setComputationalValidationStatus(String computationalValidationStatus) {
    this.computationalValidationStatus = computationalValidationStatus;
  }

  public NominatedDrugEvidenceDto computationalValidationResults(String computationalValidationResults) {
    this.computationalValidationResults = computationalValidationResults;
    return this;
  }

  /**
   * The results of computational validation for the drug nomination
   * @return computationalValidationResults
   */
  @NotNull 
  @Schema(name = "computational_validation_results", example = "Based on putative targets associated with AD", description = "The results of computational validation for the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("computational_validation_results")
  public String getComputationalValidationResults() {
    return computationalValidationResults;
  }

  public void setComputationalValidationResults(String computationalValidationResults) {
    this.computationalValidationResults = computationalValidationResults;
  }

  public NominatedDrugEvidenceDto experimentalValidationStatus(String experimentalValidationStatus) {
    this.experimentalValidationStatus = experimentalValidationStatus;
    return this;
  }

  /**
   * The status of experimental validation for the drug nomination
   * @return experimentalValidationStatus
   */
  @NotNull 
  @Schema(name = "experimental_validation_status", example = "experimental validation studies completed", description = "The status of experimental validation for the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("experimental_validation_status")
  public String getExperimentalValidationStatus() {
    return experimentalValidationStatus;
  }

  public void setExperimentalValidationStatus(String experimentalValidationStatus) {
    this.experimentalValidationStatus = experimentalValidationStatus;
  }

  public NominatedDrugEvidenceDto experimentalValidationResults(String experimentalValidationResults) {
    this.experimentalValidationResults = experimentalValidationResults;
    return this;
  }

  /**
   * The results of experimental validation for the drug nomination
   * @return experimentalValidationResults
   */
  @NotNull 
  @Schema(name = "experimental_validation_results", example = "improve cognitive, reduce neuroinflamation in female Tg344 rat AD models.", description = "The results of experimental validation for the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("experimental_validation_results")
  public String getExperimentalValidationResults() {
    return experimentalValidationResults;
  }

  public void setExperimentalValidationResults(String experimentalValidationResults) {
    this.experimentalValidationResults = experimentalValidationResults;
  }

  public NominatedDrugEvidenceDto additionalEvidence(String additionalEvidence) {
    this.additionalEvidence = additionalEvidence;
    return this;
  }

  /**
   * Any additional evidence supporting the drug nomination
   * @return additionalEvidence
   */
  @NotNull 
  @Schema(name = "additional_evidence", example = "may penetrate blood-brain barrier", description = "Any additional evidence supporting the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("additional_evidence")
  public String getAdditionalEvidence() {
    return additionalEvidence;
  }

  public void setAdditionalEvidence(String additionalEvidence) {
    this.additionalEvidence = additionalEvidence;
  }

  public NominatedDrugEvidenceDto contributors(String contributors) {
    this.contributors = contributors;
    return this;
  }

  /**
   * The contributors to the drug nomination
   * @return contributors
   */
  @NotNull 
  @Schema(name = "contributors", example = "Kelechi Ndukwe, Peter A Serrano, Patricia Rockwell, Lei Xie, Maria Figueiredo-Pereira", description = "The contributors to the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contributors")
  public String getContributors() {
    return contributors;
  }

  public void setContributors(String contributors) {
    this.contributors = contributors;
  }

  public NominatedDrugEvidenceDto initialNomination(Integer initialNomination) {
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

  public NominatedDrugEvidenceDto program(String program) {
    this.program = program;
    return this;
  }

  /**
   * The program associated with the drug nomination
   * @return program
   */
  @NotNull 
  @Schema(name = "program", example = "ACTDRx AD", description = "The program associated with the drug nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("program")
  public String getProgram() {
    return program;
  }

  public void setProgram(String program) {
    this.program = program;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NominatedDrugEvidenceDto nominatedDrugEvidence = (NominatedDrugEvidenceDto) o;
    return Objects.equals(this.grantNumber, nominatedDrugEvidence.grantNumber) &&
        Objects.equals(this.contactPi, nominatedDrugEvidence.contactPi) &&
        Objects.equals(this.combinedWithCommonName, nominatedDrugEvidence.combinedWithCommonName) &&
        Objects.equals(this.combinedWithChemblId, nominatedDrugEvidence.combinedWithChemblId) &&
        Objects.equals(this.evidence, nominatedDrugEvidence.evidence) &&
        Objects.equals(this.dataUsed, nominatedDrugEvidence.dataUsed) &&
        Objects.equals(this.adMoa, nominatedDrugEvidence.adMoa) &&
        Objects.equals(this.reference, nominatedDrugEvidence.reference) &&
        Objects.equals(this.computationalValidationStatus, nominatedDrugEvidence.computationalValidationStatus) &&
        Objects.equals(this.computationalValidationResults, nominatedDrugEvidence.computationalValidationResults) &&
        Objects.equals(this.experimentalValidationStatus, nominatedDrugEvidence.experimentalValidationStatus) &&
        Objects.equals(this.experimentalValidationResults, nominatedDrugEvidence.experimentalValidationResults) &&
        Objects.equals(this.additionalEvidence, nominatedDrugEvidence.additionalEvidence) &&
        Objects.equals(this.contributors, nominatedDrugEvidence.contributors) &&
        Objects.equals(this.initialNomination, nominatedDrugEvidence.initialNomination) &&
        Objects.equals(this.program, nominatedDrugEvidence.program);
  }

  @Override
  public int hashCode() {
    return Objects.hash(grantNumber, contactPi, combinedWithCommonName, combinedWithChemblId, evidence, dataUsed, adMoa, reference, computationalValidationStatus, computationalValidationResults, experimentalValidationStatus, experimentalValidationResults, additionalEvidence, contributors, initialNomination, program);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominatedDrugEvidenceDto {\n");
    sb.append("    grantNumber: ").append(toIndentedString(grantNumber)).append("\n");
    sb.append("    contactPi: ").append(toIndentedString(contactPi)).append("\n");
    sb.append("    combinedWithCommonName: ").append(toIndentedString(combinedWithCommonName)).append("\n");
    sb.append("    combinedWithChemblId: ").append(toIndentedString(combinedWithChemblId)).append("\n");
    sb.append("    evidence: ").append(toIndentedString(evidence)).append("\n");
    sb.append("    dataUsed: ").append(toIndentedString(dataUsed)).append("\n");
    sb.append("    adMoa: ").append(toIndentedString(adMoa)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    computationalValidationStatus: ").append(toIndentedString(computationalValidationStatus)).append("\n");
    sb.append("    computationalValidationResults: ").append(toIndentedString(computationalValidationResults)).append("\n");
    sb.append("    experimentalValidationStatus: ").append(toIndentedString(experimentalValidationStatus)).append("\n");
    sb.append("    experimentalValidationResults: ").append(toIndentedString(experimentalValidationResults)).append("\n");
    sb.append("    additionalEvidence: ").append(toIndentedString(additionalEvidence)).append("\n");
    sb.append("    contributors: ").append(toIndentedString(contributors)).append("\n");
    sb.append("    initialNomination: ").append(toIndentedString(initialNomination)).append("\n");
    sb.append("    program: ").append(toIndentedString(program)).append("\n");
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

    private NominatedDrugEvidenceDto instance;

    public Builder() {
      this(new NominatedDrugEvidenceDto());
    }

    protected Builder(NominatedDrugEvidenceDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NominatedDrugEvidenceDto value) { 
      this.instance.setGrantNumber(value.grantNumber);
      this.instance.setContactPi(value.contactPi);
      this.instance.setCombinedWithCommonName(value.combinedWithCommonName);
      this.instance.setCombinedWithChemblId(value.combinedWithChemblId);
      this.instance.setEvidence(value.evidence);
      this.instance.setDataUsed(value.dataUsed);
      this.instance.setAdMoa(value.adMoa);
      this.instance.setReference(value.reference);
      this.instance.setComputationalValidationStatus(value.computationalValidationStatus);
      this.instance.setComputationalValidationResults(value.computationalValidationResults);
      this.instance.setExperimentalValidationStatus(value.experimentalValidationStatus);
      this.instance.setExperimentalValidationResults(value.experimentalValidationResults);
      this.instance.setAdditionalEvidence(value.additionalEvidence);
      this.instance.setContributors(value.contributors);
      this.instance.setInitialNomination(value.initialNomination);
      this.instance.setProgram(value.program);
      return this;
    }

    public NominatedDrugEvidenceDto.Builder grantNumber(String grantNumber) {
      this.instance.grantNumber(grantNumber);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder contactPi(String contactPi) {
      this.instance.contactPi(contactPi);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder combinedWithCommonName(String combinedWithCommonName) {
      this.instance.combinedWithCommonName(combinedWithCommonName);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder combinedWithChemblId(String combinedWithChemblId) {
      this.instance.combinedWithChemblId(combinedWithChemblId);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder evidence(String evidence) {
      this.instance.evidence(evidence);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder dataUsed(String dataUsed) {
      this.instance.dataUsed(dataUsed);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder adMoa(String adMoa) {
      this.instance.adMoa(adMoa);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder reference(String reference) {
      this.instance.reference(reference);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder computationalValidationStatus(String computationalValidationStatus) {
      this.instance.computationalValidationStatus(computationalValidationStatus);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder computationalValidationResults(String computationalValidationResults) {
      this.instance.computationalValidationResults(computationalValidationResults);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder experimentalValidationStatus(String experimentalValidationStatus) {
      this.instance.experimentalValidationStatus(experimentalValidationStatus);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder experimentalValidationResults(String experimentalValidationResults) {
      this.instance.experimentalValidationResults(experimentalValidationResults);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder additionalEvidence(String additionalEvidence) {
      this.instance.additionalEvidence(additionalEvidence);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder contributors(String contributors) {
      this.instance.contributors(contributors);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder initialNomination(Integer initialNomination) {
      this.instance.initialNomination(initialNomination);
      return this;
    }
    
    public NominatedDrugEvidenceDto.Builder program(String program) {
      this.instance.program(program);
      return this;
    }
    
    /**
    * returns a built NominatedDrugEvidenceDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NominatedDrugEvidenceDto build() {
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
  public static NominatedDrugEvidenceDto.Builder builder() {
    return new NominatedDrugEvidenceDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NominatedDrugEvidenceDto.Builder toBuilder() {
    NominatedDrugEvidenceDto.Builder builder = new NominatedDrugEvidenceDto.Builder();
    return builder.copyOf(this);
  }

}

