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
 * Experimental Validation
 */

@Schema(name = "ExperimentalValidation", description = "Experimental Validation")
@JsonTypeName("ExperimentalValidation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ExperimentalValidationDto {

  private String id;

  private String ensemblGeneId;

  private String hgncSymbol;

  private String hypothesisTested;

  private String summaryFindings;

  private String published;

  private String reference;

  private String species;

  private String modelSystem;

  private String outcomeMeasure;

  private String outcomeMeasureDetails;

  private String balancedForSex;

  private String contributors;

  private String team;

  private String referenceDoi;

  private String dateReport;

  public ExperimentalValidationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExperimentalValidationDto(String id, String ensemblGeneId, String hgncSymbol, String hypothesisTested, String summaryFindings, String published, String reference, String species, String modelSystem, String outcomeMeasure, String outcomeMeasureDetails, String balancedForSex, String contributors, String team, String referenceDoi, String dateReport) {
    this.id = id;
    this.ensemblGeneId = ensemblGeneId;
    this.hgncSymbol = hgncSymbol;
    this.hypothesisTested = hypothesisTested;
    this.summaryFindings = summaryFindings;
    this.published = published;
    this.reference = reference;
    this.species = species;
    this.modelSystem = modelSystem;
    this.outcomeMeasure = outcomeMeasure;
    this.outcomeMeasureDetails = outcomeMeasureDetails;
    this.balancedForSex = balancedForSex;
    this.contributors = contributors;
    this.team = team;
    this.referenceDoi = referenceDoi;
    this.dateReport = dateReport;
  }

  public ExperimentalValidationDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ExperimentalValidationDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Get ensemblGeneId
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public ExperimentalValidationDto hgncSymbol(String hgncSymbol) {
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

  public ExperimentalValidationDto hypothesisTested(String hypothesisTested) {
    this.hypothesisTested = hypothesisTested;
    return this;
  }

  /**
   * Get hypothesisTested
   * @return hypothesisTested
   */
  @NotNull 
  @Schema(name = "hypothesis_tested", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hypothesis_tested")
  public String getHypothesisTested() {
    return hypothesisTested;
  }

  public void setHypothesisTested(String hypothesisTested) {
    this.hypothesisTested = hypothesisTested;
  }

  public ExperimentalValidationDto summaryFindings(String summaryFindings) {
    this.summaryFindings = summaryFindings;
    return this;
  }

  /**
   * Get summaryFindings
   * @return summaryFindings
   */
  @NotNull 
  @Schema(name = "summary_findings", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("summary_findings")
  public String getSummaryFindings() {
    return summaryFindings;
  }

  public void setSummaryFindings(String summaryFindings) {
    this.summaryFindings = summaryFindings;
  }

  public ExperimentalValidationDto published(String published) {
    this.published = published;
    return this;
  }

  /**
   * Get published
   * @return published
   */
  @NotNull 
  @Schema(name = "published", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("published")
  public String getPublished() {
    return published;
  }

  public void setPublished(String published) {
    this.published = published;
  }

  public ExperimentalValidationDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  @NotNull 
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public ExperimentalValidationDto species(String species) {
    this.species = species;
    return this;
  }

  /**
   * Get species
   * @return species
   */
  @NotNull 
  @Schema(name = "species", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("species")
  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public ExperimentalValidationDto modelSystem(String modelSystem) {
    this.modelSystem = modelSystem;
    return this;
  }

  /**
   * Get modelSystem
   * @return modelSystem
   */
  @NotNull 
  @Schema(name = "model_system", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model_system")
  public String getModelSystem() {
    return modelSystem;
  }

  public void setModelSystem(String modelSystem) {
    this.modelSystem = modelSystem;
  }

  public ExperimentalValidationDto outcomeMeasure(String outcomeMeasure) {
    this.outcomeMeasure = outcomeMeasure;
    return this;
  }

  /**
   * Get outcomeMeasure
   * @return outcomeMeasure
   */
  @NotNull 
  @Schema(name = "outcome_measure", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("outcome_measure")
  public String getOutcomeMeasure() {
    return outcomeMeasure;
  }

  public void setOutcomeMeasure(String outcomeMeasure) {
    this.outcomeMeasure = outcomeMeasure;
  }

  public ExperimentalValidationDto outcomeMeasureDetails(String outcomeMeasureDetails) {
    this.outcomeMeasureDetails = outcomeMeasureDetails;
    return this;
  }

  /**
   * Get outcomeMeasureDetails
   * @return outcomeMeasureDetails
   */
  @NotNull 
  @Schema(name = "outcome_measure_details", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("outcome_measure_details")
  public String getOutcomeMeasureDetails() {
    return outcomeMeasureDetails;
  }

  public void setOutcomeMeasureDetails(String outcomeMeasureDetails) {
    this.outcomeMeasureDetails = outcomeMeasureDetails;
  }

  public ExperimentalValidationDto balancedForSex(String balancedForSex) {
    this.balancedForSex = balancedForSex;
    return this;
  }

  /**
   * Get balancedForSex
   * @return balancedForSex
   */
  @NotNull 
  @Schema(name = "balanced_for_sex", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("balanced_for_sex")
  public String getBalancedForSex() {
    return balancedForSex;
  }

  public void setBalancedForSex(String balancedForSex) {
    this.balancedForSex = balancedForSex;
  }

  public ExperimentalValidationDto contributors(String contributors) {
    this.contributors = contributors;
    return this;
  }

  /**
   * Get contributors
   * @return contributors
   */
  @NotNull 
  @Schema(name = "contributors", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contributors")
  public String getContributors() {
    return contributors;
  }

  public void setContributors(String contributors) {
    this.contributors = contributors;
  }

  public ExperimentalValidationDto team(String team) {
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

  public ExperimentalValidationDto referenceDoi(String referenceDoi) {
    this.referenceDoi = referenceDoi;
    return this;
  }

  /**
   * Get referenceDoi
   * @return referenceDoi
   */
  @NotNull 
  @Schema(name = "reference_doi", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference_doi")
  public String getReferenceDoi() {
    return referenceDoi;
  }

  public void setReferenceDoi(String referenceDoi) {
    this.referenceDoi = referenceDoi;
  }

  public ExperimentalValidationDto dateReport(String dateReport) {
    this.dateReport = dateReport;
    return this;
  }

  /**
   * Get dateReport
   * @return dateReport
   */
  @NotNull 
  @Schema(name = "date_report", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("date_report")
  public String getDateReport() {
    return dateReport;
  }

  public void setDateReport(String dateReport) {
    this.dateReport = dateReport;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExperimentalValidationDto experimentalValidation = (ExperimentalValidationDto) o;
    return Objects.equals(this.id, experimentalValidation.id) &&
        Objects.equals(this.ensemblGeneId, experimentalValidation.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, experimentalValidation.hgncSymbol) &&
        Objects.equals(this.hypothesisTested, experimentalValidation.hypothesisTested) &&
        Objects.equals(this.summaryFindings, experimentalValidation.summaryFindings) &&
        Objects.equals(this.published, experimentalValidation.published) &&
        Objects.equals(this.reference, experimentalValidation.reference) &&
        Objects.equals(this.species, experimentalValidation.species) &&
        Objects.equals(this.modelSystem, experimentalValidation.modelSystem) &&
        Objects.equals(this.outcomeMeasure, experimentalValidation.outcomeMeasure) &&
        Objects.equals(this.outcomeMeasureDetails, experimentalValidation.outcomeMeasureDetails) &&
        Objects.equals(this.balancedForSex, experimentalValidation.balancedForSex) &&
        Objects.equals(this.contributors, experimentalValidation.contributors) &&
        Objects.equals(this.team, experimentalValidation.team) &&
        Objects.equals(this.referenceDoi, experimentalValidation.referenceDoi) &&
        Objects.equals(this.dateReport, experimentalValidation.dateReport);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ensemblGeneId, hgncSymbol, hypothesisTested, summaryFindings, published, reference, species, modelSystem, outcomeMeasure, outcomeMeasureDetails, balancedForSex, contributors, team, referenceDoi, dateReport);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExperimentalValidationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    hypothesisTested: ").append(toIndentedString(hypothesisTested)).append("\n");
    sb.append("    summaryFindings: ").append(toIndentedString(summaryFindings)).append("\n");
    sb.append("    published: ").append(toIndentedString(published)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    species: ").append(toIndentedString(species)).append("\n");
    sb.append("    modelSystem: ").append(toIndentedString(modelSystem)).append("\n");
    sb.append("    outcomeMeasure: ").append(toIndentedString(outcomeMeasure)).append("\n");
    sb.append("    outcomeMeasureDetails: ").append(toIndentedString(outcomeMeasureDetails)).append("\n");
    sb.append("    balancedForSex: ").append(toIndentedString(balancedForSex)).append("\n");
    sb.append("    contributors: ").append(toIndentedString(contributors)).append("\n");
    sb.append("    team: ").append(toIndentedString(team)).append("\n");
    sb.append("    referenceDoi: ").append(toIndentedString(referenceDoi)).append("\n");
    sb.append("    dateReport: ").append(toIndentedString(dateReport)).append("\n");
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

    private ExperimentalValidationDto instance;

    public Builder() {
      this(new ExperimentalValidationDto());
    }

    protected Builder(ExperimentalValidationDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ExperimentalValidationDto value) { 
      this.instance.setId(value.id);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setHypothesisTested(value.hypothesisTested);
      this.instance.setSummaryFindings(value.summaryFindings);
      this.instance.setPublished(value.published);
      this.instance.setReference(value.reference);
      this.instance.setSpecies(value.species);
      this.instance.setModelSystem(value.modelSystem);
      this.instance.setOutcomeMeasure(value.outcomeMeasure);
      this.instance.setOutcomeMeasureDetails(value.outcomeMeasureDetails);
      this.instance.setBalancedForSex(value.balancedForSex);
      this.instance.setContributors(value.contributors);
      this.instance.setTeam(value.team);
      this.instance.setReferenceDoi(value.referenceDoi);
      this.instance.setDateReport(value.dateReport);
      return this;
    }

    public ExperimentalValidationDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public ExperimentalValidationDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public ExperimentalValidationDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public ExperimentalValidationDto.Builder hypothesisTested(String hypothesisTested) {
      this.instance.hypothesisTested(hypothesisTested);
      return this;
    }
    
    public ExperimentalValidationDto.Builder summaryFindings(String summaryFindings) {
      this.instance.summaryFindings(summaryFindings);
      return this;
    }
    
    public ExperimentalValidationDto.Builder published(String published) {
      this.instance.published(published);
      return this;
    }
    
    public ExperimentalValidationDto.Builder reference(String reference) {
      this.instance.reference(reference);
      return this;
    }
    
    public ExperimentalValidationDto.Builder species(String species) {
      this.instance.species(species);
      return this;
    }
    
    public ExperimentalValidationDto.Builder modelSystem(String modelSystem) {
      this.instance.modelSystem(modelSystem);
      return this;
    }
    
    public ExperimentalValidationDto.Builder outcomeMeasure(String outcomeMeasure) {
      this.instance.outcomeMeasure(outcomeMeasure);
      return this;
    }
    
    public ExperimentalValidationDto.Builder outcomeMeasureDetails(String outcomeMeasureDetails) {
      this.instance.outcomeMeasureDetails(outcomeMeasureDetails);
      return this;
    }
    
    public ExperimentalValidationDto.Builder balancedForSex(String balancedForSex) {
      this.instance.balancedForSex(balancedForSex);
      return this;
    }
    
    public ExperimentalValidationDto.Builder contributors(String contributors) {
      this.instance.contributors(contributors);
      return this;
    }
    
    public ExperimentalValidationDto.Builder team(String team) {
      this.instance.team(team);
      return this;
    }
    
    public ExperimentalValidationDto.Builder referenceDoi(String referenceDoi) {
      this.instance.referenceDoi(referenceDoi);
      return this;
    }
    
    public ExperimentalValidationDto.Builder dateReport(String dateReport) {
      this.instance.dateReport(dateReport);
      return this;
    }
    
    /**
    * returns a built ExperimentalValidationDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ExperimentalValidationDto build() {
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
  public static ExperimentalValidationDto.Builder builder() {
    return new ExperimentalValidationDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ExperimentalValidationDto.Builder toBuilder() {
    ExperimentalValidationDto.Builder builder = new ExperimentalValidationDto.Builder();
    return builder.copyOf(this);
  }

}

