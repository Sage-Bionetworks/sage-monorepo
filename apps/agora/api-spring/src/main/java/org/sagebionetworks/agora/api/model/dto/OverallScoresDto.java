package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OverallScores
 */

@Schema(name = "OverallScores", description = "OverallScores")
@JsonTypeName("OverallScores")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class OverallScoresDto {

  private String ensemblGeneId;

  private BigDecimal targetRiskScore;

  private BigDecimal geneticsScore;

  private BigDecimal multiOmicsScore;

  private BigDecimal literatureScore;

  public OverallScoresDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OverallScoresDto(String ensemblGeneId, BigDecimal targetRiskScore, BigDecimal geneticsScore, BigDecimal multiOmicsScore, BigDecimal literatureScore) {
    this.ensemblGeneId = ensemblGeneId;
    this.targetRiskScore = targetRiskScore;
    this.geneticsScore = geneticsScore;
    this.multiOmicsScore = multiOmicsScore;
    this.literatureScore = literatureScore;
  }

  public OverallScoresDto ensemblGeneId(String ensemblGeneId) {
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

  public OverallScoresDto targetRiskScore(BigDecimal targetRiskScore) {
    this.targetRiskScore = targetRiskScore;
    return this;
  }

  /**
   * Get targetRiskScore
   * @return targetRiskScore
  */
  @NotNull @Valid 
  @Schema(name = "target_risk_score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("target_risk_score")
  public BigDecimal getTargetRiskScore() {
    return targetRiskScore;
  }

  public void setTargetRiskScore(BigDecimal targetRiskScore) {
    this.targetRiskScore = targetRiskScore;
  }

  public OverallScoresDto geneticsScore(BigDecimal geneticsScore) {
    this.geneticsScore = geneticsScore;
    return this;
  }

  /**
   * Get geneticsScore
   * @return geneticsScore
  */
  @NotNull @Valid 
  @Schema(name = "genetics_score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("genetics_score")
  public BigDecimal getGeneticsScore() {
    return geneticsScore;
  }

  public void setGeneticsScore(BigDecimal geneticsScore) {
    this.geneticsScore = geneticsScore;
  }

  public OverallScoresDto multiOmicsScore(BigDecimal multiOmicsScore) {
    this.multiOmicsScore = multiOmicsScore;
    return this;
  }

  /**
   * Get multiOmicsScore
   * @return multiOmicsScore
  */
  @NotNull @Valid 
  @Schema(name = "multi_omics_score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("multi_omics_score")
  public BigDecimal getMultiOmicsScore() {
    return multiOmicsScore;
  }

  public void setMultiOmicsScore(BigDecimal multiOmicsScore) {
    this.multiOmicsScore = multiOmicsScore;
  }

  public OverallScoresDto literatureScore(BigDecimal literatureScore) {
    this.literatureScore = literatureScore;
    return this;
  }

  /**
   * Get literatureScore
   * @return literatureScore
  */
  @NotNull @Valid 
  @Schema(name = "literature_score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("literature_score")
  public BigDecimal getLiteratureScore() {
    return literatureScore;
  }

  public void setLiteratureScore(BigDecimal literatureScore) {
    this.literatureScore = literatureScore;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OverallScoresDto overallScores = (OverallScoresDto) o;
    return Objects.equals(this.ensemblGeneId, overallScores.ensemblGeneId) &&
        Objects.equals(this.targetRiskScore, overallScores.targetRiskScore) &&
        Objects.equals(this.geneticsScore, overallScores.geneticsScore) &&
        Objects.equals(this.multiOmicsScore, overallScores.multiOmicsScore) &&
        Objects.equals(this.literatureScore, overallScores.literatureScore);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, targetRiskScore, geneticsScore, multiOmicsScore, literatureScore);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OverallScoresDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    targetRiskScore: ").append(toIndentedString(targetRiskScore)).append("\n");
    sb.append("    geneticsScore: ").append(toIndentedString(geneticsScore)).append("\n");
    sb.append("    multiOmicsScore: ").append(toIndentedString(multiOmicsScore)).append("\n");
    sb.append("    literatureScore: ").append(toIndentedString(literatureScore)).append("\n");
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

