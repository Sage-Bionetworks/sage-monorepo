package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TargetNomination
 */

@Schema(name = "TargetNomination", description = "TargetNomination")
@JsonTypeName("TargetNomination")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class TargetNominationDto {

  private String source;

  private String team;

  private String rank;

  private String hgncSymbol;

  private String targetChoiceJustification;

  private String predictedTherapeuticDirection;

  private String dataUsedToSupportTargetSelection;

  private String dataSynapseid;

  private String study = null;

  private String inputData;

  private String validationStudyDetails = null;

  private BigDecimal initialNomination;

  public TargetNominationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TargetNominationDto(String source, String team, String rank, String hgncSymbol, String targetChoiceJustification, String predictedTherapeuticDirection, String dataUsedToSupportTargetSelection, String dataSynapseid, String study, String inputData, String validationStudyDetails, BigDecimal initialNomination) {
    this.source = source;
    this.team = team;
    this.rank = rank;
    this.hgncSymbol = hgncSymbol;
    this.targetChoiceJustification = targetChoiceJustification;
    this.predictedTherapeuticDirection = predictedTherapeuticDirection;
    this.dataUsedToSupportTargetSelection = dataUsedToSupportTargetSelection;
    this.dataSynapseid = dataSynapseid;
    this.study = study;
    this.inputData = inputData;
    this.validationStudyDetails = validationStudyDetails;
    this.initialNomination = initialNomination;
  }

  public TargetNominationDto source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   */
  @NotNull 
  @Schema(name = "source", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("source")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public TargetNominationDto team(String team) {
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

  public TargetNominationDto rank(String rank) {
    this.rank = rank;
    return this;
  }

  /**
   * Get rank
   * @return rank
   */
  @NotNull 
  @Schema(name = "rank", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rank")
  public String getRank() {
    return rank;
  }

  public void setRank(String rank) {
    this.rank = rank;
  }

  public TargetNominationDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * Get hgncSymbol
   * @return hgncSymbol
   */
  @NotNull 
  @Schema(name = "hgnc_symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  public TargetNominationDto targetChoiceJustification(String targetChoiceJustification) {
    this.targetChoiceJustification = targetChoiceJustification;
    return this;
  }

  /**
   * Get targetChoiceJustification
   * @return targetChoiceJustification
   */
  @NotNull 
  @Schema(name = "target_choice_justification", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("target_choice_justification")
  public String getTargetChoiceJustification() {
    return targetChoiceJustification;
  }

  public void setTargetChoiceJustification(String targetChoiceJustification) {
    this.targetChoiceJustification = targetChoiceJustification;
  }

  public TargetNominationDto predictedTherapeuticDirection(String predictedTherapeuticDirection) {
    this.predictedTherapeuticDirection = predictedTherapeuticDirection;
    return this;
  }

  /**
   * Get predictedTherapeuticDirection
   * @return predictedTherapeuticDirection
   */
  @NotNull 
  @Schema(name = "predicted_therapeutic_direction", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("predicted_therapeutic_direction")
  public String getPredictedTherapeuticDirection() {
    return predictedTherapeuticDirection;
  }

  public void setPredictedTherapeuticDirection(String predictedTherapeuticDirection) {
    this.predictedTherapeuticDirection = predictedTherapeuticDirection;
  }

  public TargetNominationDto dataUsedToSupportTargetSelection(String dataUsedToSupportTargetSelection) {
    this.dataUsedToSupportTargetSelection = dataUsedToSupportTargetSelection;
    return this;
  }

  /**
   * Get dataUsedToSupportTargetSelection
   * @return dataUsedToSupportTargetSelection
   */
  @NotNull 
  @Schema(name = "data_used_to_support_target_selection", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data_used_to_support_target_selection")
  public String getDataUsedToSupportTargetSelection() {
    return dataUsedToSupportTargetSelection;
  }

  public void setDataUsedToSupportTargetSelection(String dataUsedToSupportTargetSelection) {
    this.dataUsedToSupportTargetSelection = dataUsedToSupportTargetSelection;
  }

  public TargetNominationDto dataSynapseid(String dataSynapseid) {
    this.dataSynapseid = dataSynapseid;
    return this;
  }

  /**
   * Get dataSynapseid
   * @return dataSynapseid
   */
  @NotNull 
  @Schema(name = "data_synapseid", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data_synapseid")
  public String getDataSynapseid() {
    return dataSynapseid;
  }

  public void setDataSynapseid(String dataSynapseid) {
    this.dataSynapseid = dataSynapseid;
  }

  public TargetNominationDto study(String study) {
    this.study = study;
    return this;
  }

  /**
   * Get study
   * @return study
   */
  @NotNull 
  @Schema(name = "study", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("study")
  public String getStudy() {
    return study;
  }

  public void setStudy(String study) {
    this.study = study;
  }

  public TargetNominationDto inputData(String inputData) {
    this.inputData = inputData;
    return this;
  }

  /**
   * Get inputData
   * @return inputData
   */
  @NotNull 
  @Schema(name = "input_data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("input_data")
  public String getInputData() {
    return inputData;
  }

  public void setInputData(String inputData) {
    this.inputData = inputData;
  }

  public TargetNominationDto validationStudyDetails(String validationStudyDetails) {
    this.validationStudyDetails = validationStudyDetails;
    return this;
  }

  /**
   * Get validationStudyDetails
   * @return validationStudyDetails
   */
  @NotNull 
  @Schema(name = "validation_study_details", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("validation_study_details")
  public String getValidationStudyDetails() {
    return validationStudyDetails;
  }

  public void setValidationStudyDetails(String validationStudyDetails) {
    this.validationStudyDetails = validationStudyDetails;
  }

  public TargetNominationDto initialNomination(BigDecimal initialNomination) {
    this.initialNomination = initialNomination;
    return this;
  }

  /**
   * Get initialNomination
   * @return initialNomination
   */
  @NotNull @Valid 
  @Schema(name = "initial_nomination", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("initial_nomination")
  public BigDecimal getInitialNomination() {
    return initialNomination;
  }

  public void setInitialNomination(BigDecimal initialNomination) {
    this.initialNomination = initialNomination;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TargetNominationDto targetNomination = (TargetNominationDto) o;
    return Objects.equals(this.source, targetNomination.source) &&
        Objects.equals(this.team, targetNomination.team) &&
        Objects.equals(this.rank, targetNomination.rank) &&
        Objects.equals(this.hgncSymbol, targetNomination.hgncSymbol) &&
        Objects.equals(this.targetChoiceJustification, targetNomination.targetChoiceJustification) &&
        Objects.equals(this.predictedTherapeuticDirection, targetNomination.predictedTherapeuticDirection) &&
        Objects.equals(this.dataUsedToSupportTargetSelection, targetNomination.dataUsedToSupportTargetSelection) &&
        Objects.equals(this.dataSynapseid, targetNomination.dataSynapseid) &&
        Objects.equals(this.study, targetNomination.study) &&
        Objects.equals(this.inputData, targetNomination.inputData) &&
        Objects.equals(this.validationStudyDetails, targetNomination.validationStudyDetails) &&
        Objects.equals(this.initialNomination, targetNomination.initialNomination);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, team, rank, hgncSymbol, targetChoiceJustification, predictedTherapeuticDirection, dataUsedToSupportTargetSelection, dataSynapseid, study, inputData, validationStudyDetails, initialNomination);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TargetNominationDto {\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    team: ").append(toIndentedString(team)).append("\n");
    sb.append("    rank: ").append(toIndentedString(rank)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    targetChoiceJustification: ").append(toIndentedString(targetChoiceJustification)).append("\n");
    sb.append("    predictedTherapeuticDirection: ").append(toIndentedString(predictedTherapeuticDirection)).append("\n");
    sb.append("    dataUsedToSupportTargetSelection: ").append(toIndentedString(dataUsedToSupportTargetSelection)).append("\n");
    sb.append("    dataSynapseid: ").append(toIndentedString(dataSynapseid)).append("\n");
    sb.append("    study: ").append(toIndentedString(study)).append("\n");
    sb.append("    inputData: ").append(toIndentedString(inputData)).append("\n");
    sb.append("    validationStudyDetails: ").append(toIndentedString(validationStudyDetails)).append("\n");
    sb.append("    initialNomination: ").append(toIndentedString(initialNomination)).append("\n");
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

    private TargetNominationDto instance;

    public Builder() {
      this(new TargetNominationDto());
    }

    protected Builder(TargetNominationDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(TargetNominationDto value) { 
      this.instance.setSource(value.source);
      this.instance.setTeam(value.team);
      this.instance.setRank(value.rank);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setTargetChoiceJustification(value.targetChoiceJustification);
      this.instance.setPredictedTherapeuticDirection(value.predictedTherapeuticDirection);
      this.instance.setDataUsedToSupportTargetSelection(value.dataUsedToSupportTargetSelection);
      this.instance.setDataSynapseid(value.dataSynapseid);
      this.instance.setStudy(value.study);
      this.instance.setInputData(value.inputData);
      this.instance.setValidationStudyDetails(value.validationStudyDetails);
      this.instance.setInitialNomination(value.initialNomination);
      return this;
    }

    public TargetNominationDto.Builder source(String source) {
      this.instance.source(source);
      return this;
    }
    
    public TargetNominationDto.Builder team(String team) {
      this.instance.team(team);
      return this;
    }
    
    public TargetNominationDto.Builder rank(String rank) {
      this.instance.rank(rank);
      return this;
    }
    
    public TargetNominationDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public TargetNominationDto.Builder targetChoiceJustification(String targetChoiceJustification) {
      this.instance.targetChoiceJustification(targetChoiceJustification);
      return this;
    }
    
    public TargetNominationDto.Builder predictedTherapeuticDirection(String predictedTherapeuticDirection) {
      this.instance.predictedTherapeuticDirection(predictedTherapeuticDirection);
      return this;
    }
    
    public TargetNominationDto.Builder dataUsedToSupportTargetSelection(String dataUsedToSupportTargetSelection) {
      this.instance.dataUsedToSupportTargetSelection(dataUsedToSupportTargetSelection);
      return this;
    }
    
    public TargetNominationDto.Builder dataSynapseid(String dataSynapseid) {
      this.instance.dataSynapseid(dataSynapseid);
      return this;
    }
    
    public TargetNominationDto.Builder study(String study) {
      this.instance.study(study);
      return this;
    }
    
    public TargetNominationDto.Builder inputData(String inputData) {
      this.instance.inputData(inputData);
      return this;
    }
    
    public TargetNominationDto.Builder validationStudyDetails(String validationStudyDetails) {
      this.instance.validationStudyDetails(validationStudyDetails);
      return this;
    }
    
    public TargetNominationDto.Builder initialNomination(BigDecimal initialNomination) {
      this.instance.initialNomination(initialNomination);
      return this;
    }
    
    /**
    * returns a built TargetNominationDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public TargetNominationDto build() {
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
  public static TargetNominationDto.Builder builder() {
    return new TargetNominationDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public TargetNominationDto.Builder toBuilder() {
    TargetNominationDto.Builder builder = new TargetNominationDto.Builder();
    return builder.copyOf(this);
  }

}

