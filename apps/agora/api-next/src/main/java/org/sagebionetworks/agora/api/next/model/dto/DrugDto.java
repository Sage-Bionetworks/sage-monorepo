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
import org.sagebionetworks.agora.api.next.model.dto.LinkedTargetDto;
import org.sagebionetworks.agora.api.next.model.dto.ModalityDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugEvidenceDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A drug entry
 */

@Schema(name = "Drug", description = "A drug entry")
@JsonTypeName("Drug")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class DrugDto {

  private String commonName;

  private String description = null;

  private String iupacId = null;

  private String chemblId;

  private String drugBankId;

  @Valid
  private List<String> aliases;

  private ModalityDto modality;

  private Integer yearOfFirstApproval = null;

  private String maximumClinicalTrialPhase = null;

  @Valid
  private List<@Valid LinkedTargetDto> linkedTargets;

  @Valid
  private List<String> mechanismsOfAction;

  @Valid
  private List<@Valid NominatedDrugEvidenceDto> drugNominations = new ArrayList<>();

  public DrugDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DrugDto(String commonName, String description, String iupacId, String chemblId, String drugBankId, List<String> aliases, ModalityDto modality, Integer yearOfFirstApproval, String maximumClinicalTrialPhase, List<@Valid LinkedTargetDto> linkedTargets, List<String> mechanismsOfAction, List<@Valid NominatedDrugEvidenceDto> drugNominations) {
    this.commonName = commonName;
    this.description = description;
    this.iupacId = iupacId;
    this.chemblId = chemblId;
    this.drugBankId = drugBankId;
    this.aliases = aliases;
    this.modality = modality;
    this.yearOfFirstApproval = yearOfFirstApproval;
    this.maximumClinicalTrialPhase = maximumClinicalTrialPhase;
    this.linkedTargets = linkedTargets;
    this.mechanismsOfAction = mechanismsOfAction;
    this.drugNominations = drugNominations;
  }

  public DrugDto commonName(String commonName) {
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

  public DrugDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A brief description of the drug
   * @return description
   */
  @NotNull 
  @Schema(name = "description", description = "A brief description of the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DrugDto iupacId(String iupacId) {
    this.iupacId = iupacId;
    return this;
  }

  /**
   * The IUPAC ID of the drug
   * @return iupacId
   */
  @NotNull 
  @Schema(name = "iupac_id", description = "The IUPAC ID of the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("iupac_id")
  public String getIupacId() {
    return iupacId;
  }

  public void setIupacId(String iupacId) {
    this.iupacId = iupacId;
  }

  public DrugDto chemblId(String chemblId) {
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

  public DrugDto drugBankId(String drugBankId) {
    this.drugBankId = drugBankId;
    return this;
  }

  /**
   * The DrugBank ID of the drug
   * @return drugBankId
   */
  @NotNull 
  @Schema(name = "drug_bank_id", example = "DB04819", description = "The DrugBank ID of the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("drug_bank_id")
  public String getDrugBankId() {
    return drugBankId;
  }

  public void setDrugBankId(String drugBankId) {
    this.drugBankId = drugBankId;
  }

  public DrugDto aliases(List<String> aliases) {
    this.aliases = aliases;
    return this;
  }

  public DrugDto addAliasesItem(String aliasesItem) {
    if (this.aliases == null) {
      this.aliases = new ArrayList<>();
    }
    this.aliases.add(aliasesItem);
    return this;
  }

  /**
   * Alternative names for the drug
   * @return aliases
   */
  @NotNull 
  @Schema(name = "aliases", example = "[\"Agomelatina\",\"S-20098\",\"Valdoxan\"]", description = "Alternative names for the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("aliases")
  public List<String> getAliases() {
    return aliases;
  }

  public void setAliases(List<String> aliases) {
    this.aliases = aliases;
  }

  public DrugDto modality(ModalityDto modality) {
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

  public DrugDto yearOfFirstApproval(Integer yearOfFirstApproval) {
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

  public DrugDto maximumClinicalTrialPhase(String maximumClinicalTrialPhase) {
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

  public DrugDto linkedTargets(List<@Valid LinkedTargetDto> linkedTargets) {
    this.linkedTargets = linkedTargets;
    return this;
  }

  public DrugDto addLinkedTargetsItem(LinkedTargetDto linkedTargetsItem) {
    if (this.linkedTargets == null) {
      this.linkedTargets = new ArrayList<>();
    }
    this.linkedTargets.add(linkedTargetsItem);
    return this;
  }

  /**
   * The linked targets associated with the drug
   * @return linkedTargets
   */
  @NotNull @Valid 
  @Schema(name = "linked_targets", description = "The linked targets associated with the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("linked_targets")
  public List<@Valid LinkedTargetDto> getLinkedTargets() {
    return linkedTargets;
  }

  public void setLinkedTargets(List<@Valid LinkedTargetDto> linkedTargets) {
    this.linkedTargets = linkedTargets;
  }

  public DrugDto mechanismsOfAction(List<String> mechanismsOfAction) {
    this.mechanismsOfAction = mechanismsOfAction;
    return this;
  }

  public DrugDto addMechanismsOfActionItem(String mechanismsOfActionItem) {
    if (this.mechanismsOfAction == null) {
      this.mechanismsOfAction = new ArrayList<>();
    }
    this.mechanismsOfAction.add(mechanismsOfActionItem);
    return this;
  }

  /**
   * The mechanisms of action associated with the drug
   * @return mechanismsOfAction
   */
  @NotNull 
  @Schema(name = "mechanisms_of_action", example = "[\"Melatonin receptor agonist\",\"Serotonin 2c (5-HT2c) receptor antagonist\"]", description = "The mechanisms of action associated with the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mechanisms_of_action")
  public List<String> getMechanismsOfAction() {
    return mechanismsOfAction;
  }

  public void setMechanismsOfAction(List<String> mechanismsOfAction) {
    this.mechanismsOfAction = mechanismsOfAction;
  }

  public DrugDto drugNominations(List<@Valid NominatedDrugEvidenceDto> drugNominations) {
    this.drugNominations = drugNominations;
    return this;
  }

  public DrugDto addDrugNominationsItem(NominatedDrugEvidenceDto drugNominationsItem) {
    if (this.drugNominations == null) {
      this.drugNominations = new ArrayList<>();
    }
    this.drugNominations.add(drugNominationsItem);
    return this;
  }

  /**
   * The drug nomination evidence associated with the drug
   * @return drugNominations
   */
  @NotNull @Valid 
  @Schema(name = "drug_nominations", description = "The drug nomination evidence associated with the drug", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("drug_nominations")
  public List<@Valid NominatedDrugEvidenceDto> getDrugNominations() {
    return drugNominations;
  }

  public void setDrugNominations(List<@Valid NominatedDrugEvidenceDto> drugNominations) {
    this.drugNominations = drugNominations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DrugDto drug = (DrugDto) o;
    return Objects.equals(this.commonName, drug.commonName) &&
        Objects.equals(this.description, drug.description) &&
        Objects.equals(this.iupacId, drug.iupacId) &&
        Objects.equals(this.chemblId, drug.chemblId) &&
        Objects.equals(this.drugBankId, drug.drugBankId) &&
        Objects.equals(this.aliases, drug.aliases) &&
        Objects.equals(this.modality, drug.modality) &&
        Objects.equals(this.yearOfFirstApproval, drug.yearOfFirstApproval) &&
        Objects.equals(this.maximumClinicalTrialPhase, drug.maximumClinicalTrialPhase) &&
        Objects.equals(this.linkedTargets, drug.linkedTargets) &&
        Objects.equals(this.mechanismsOfAction, drug.mechanismsOfAction) &&
        Objects.equals(this.drugNominations, drug.drugNominations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commonName, description, iupacId, chemblId, drugBankId, aliases, modality, yearOfFirstApproval, maximumClinicalTrialPhase, linkedTargets, mechanismsOfAction, drugNominations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DrugDto {\n");
    sb.append("    commonName: ").append(toIndentedString(commonName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    iupacId: ").append(toIndentedString(iupacId)).append("\n");
    sb.append("    chemblId: ").append(toIndentedString(chemblId)).append("\n");
    sb.append("    drugBankId: ").append(toIndentedString(drugBankId)).append("\n");
    sb.append("    aliases: ").append(toIndentedString(aliases)).append("\n");
    sb.append("    modality: ").append(toIndentedString(modality)).append("\n");
    sb.append("    yearOfFirstApproval: ").append(toIndentedString(yearOfFirstApproval)).append("\n");
    sb.append("    maximumClinicalTrialPhase: ").append(toIndentedString(maximumClinicalTrialPhase)).append("\n");
    sb.append("    linkedTargets: ").append(toIndentedString(linkedTargets)).append("\n");
    sb.append("    mechanismsOfAction: ").append(toIndentedString(mechanismsOfAction)).append("\n");
    sb.append("    drugNominations: ").append(toIndentedString(drugNominations)).append("\n");
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

    private DrugDto instance;

    public Builder() {
      this(new DrugDto());
    }

    protected Builder(DrugDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DrugDto value) { 
      this.instance.setCommonName(value.commonName);
      this.instance.setDescription(value.description);
      this.instance.setIupacId(value.iupacId);
      this.instance.setChemblId(value.chemblId);
      this.instance.setDrugBankId(value.drugBankId);
      this.instance.setAliases(value.aliases);
      this.instance.setModality(value.modality);
      this.instance.setYearOfFirstApproval(value.yearOfFirstApproval);
      this.instance.setMaximumClinicalTrialPhase(value.maximumClinicalTrialPhase);
      this.instance.setLinkedTargets(value.linkedTargets);
      this.instance.setMechanismsOfAction(value.mechanismsOfAction);
      this.instance.setDrugNominations(value.drugNominations);
      return this;
    }

    public DrugDto.Builder commonName(String commonName) {
      this.instance.commonName(commonName);
      return this;
    }
    
    public DrugDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public DrugDto.Builder iupacId(String iupacId) {
      this.instance.iupacId(iupacId);
      return this;
    }
    
    public DrugDto.Builder chemblId(String chemblId) {
      this.instance.chemblId(chemblId);
      return this;
    }
    
    public DrugDto.Builder drugBankId(String drugBankId) {
      this.instance.drugBankId(drugBankId);
      return this;
    }
    
    public DrugDto.Builder aliases(List<String> aliases) {
      this.instance.aliases(aliases);
      return this;
    }
    
    public DrugDto.Builder modality(ModalityDto modality) {
      this.instance.modality(modality);
      return this;
    }
    
    public DrugDto.Builder yearOfFirstApproval(Integer yearOfFirstApproval) {
      this.instance.yearOfFirstApproval(yearOfFirstApproval);
      return this;
    }
    
    public DrugDto.Builder maximumClinicalTrialPhase(String maximumClinicalTrialPhase) {
      this.instance.maximumClinicalTrialPhase(maximumClinicalTrialPhase);
      return this;
    }
    
    public DrugDto.Builder linkedTargets(List<LinkedTargetDto> linkedTargets) {
      this.instance.linkedTargets(linkedTargets);
      return this;
    }
    
    public DrugDto.Builder mechanismsOfAction(List<String> mechanismsOfAction) {
      this.instance.mechanismsOfAction(mechanismsOfAction);
      return this;
    }
    
    public DrugDto.Builder drugNominations(List<NominatedDrugEvidenceDto> drugNominations) {
      this.instance.drugNominations(drugNominations);
      return this;
    }
    
    /**
    * returns a built DrugDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DrugDto build() {
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
  public static DrugDto.Builder builder() {
    return new DrugDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DrugDto.Builder toBuilder() {
    DrugDto.Builder builder = new DrugDto.Builder();
    return builder.copyOf(this);
  }

}

