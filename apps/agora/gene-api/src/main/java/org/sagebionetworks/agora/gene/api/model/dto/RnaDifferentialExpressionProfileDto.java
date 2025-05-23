package org.sagebionetworks.agora.gene.api.model.dto;

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
 * A RNA differential expression profile.
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "RnaDifferentialExpressionProfile", description = "A RNA differential expression profile.")
@JsonTypeName("RnaDifferentialExpressionProfile")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class RnaDifferentialExpressionProfileDto {

  private String ensemblGeneId;

  private @Nullable String hgncSymbol;

  private @Nullable BigDecimal targetRiskScore = null;

  private @Nullable BigDecimal geneticsScore = null;

  private @Nullable BigDecimal multiOmicsScore = null;

  public RnaDifferentialExpressionProfileDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RnaDifferentialExpressionProfileDto(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public RnaDifferentialExpressionProfileDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * The Ensembl gene ID.
   * @return ensemblGeneId
   */
  @NotNull @Pattern(regexp = "^ENSG\\d{11}$") @Size(min = 15, max = 15) 
  @Schema(name = "ensembl_gene_id", example = "ENSG00000139618", description = "The Ensembl gene ID.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public RnaDifferentialExpressionProfileDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * The HGNC gene symbol.
   * @return hgncSymbol
   */
  @Size(min = 0) 
  @Schema(name = "hgnc_symbol", example = "TP53", description = "The HGNC gene symbol.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  public RnaDifferentialExpressionProfileDto targetRiskScore(BigDecimal targetRiskScore) {
    this.targetRiskScore = targetRiskScore;
    return this;
  }

  /**
   * Target risk score
   * @return targetRiskScore
   */
  @Valid 
  @Schema(name = "target_risk_score", description = "Target risk score", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("target_risk_score")
  public BigDecimal getTargetRiskScore() {
    return targetRiskScore;
  }

  public void setTargetRiskScore(BigDecimal targetRiskScore) {
    this.targetRiskScore = targetRiskScore;
  }

  public RnaDifferentialExpressionProfileDto geneticsScore(BigDecimal geneticsScore) {
    this.geneticsScore = geneticsScore;
    return this;
  }

  /**
   * Genetics score
   * @return geneticsScore
   */
  @Valid 
  @Schema(name = "genetics_score", description = "Genetics score", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("genetics_score")
  public BigDecimal getGeneticsScore() {
    return geneticsScore;
  }

  public void setGeneticsScore(BigDecimal geneticsScore) {
    this.geneticsScore = geneticsScore;
  }

  public RnaDifferentialExpressionProfileDto multiOmicsScore(BigDecimal multiOmicsScore) {
    this.multiOmicsScore = multiOmicsScore;
    return this;
  }

  /**
   * Multi-omics score
   * @return multiOmicsScore
   */
  @Valid 
  @Schema(name = "multi_omics_score", description = "Multi-omics score", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("multi_omics_score")
  public BigDecimal getMultiOmicsScore() {
    return multiOmicsScore;
  }

  public void setMultiOmicsScore(BigDecimal multiOmicsScore) {
    this.multiOmicsScore = multiOmicsScore;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RnaDifferentialExpressionProfileDto rnaDifferentialExpressionProfile = (RnaDifferentialExpressionProfileDto) o;
    return Objects.equals(this.ensemblGeneId, rnaDifferentialExpressionProfile.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, rnaDifferentialExpressionProfile.hgncSymbol) &&
        Objects.equals(this.targetRiskScore, rnaDifferentialExpressionProfile.targetRiskScore) &&
        Objects.equals(this.geneticsScore, rnaDifferentialExpressionProfile.geneticsScore) &&
        Objects.equals(this.multiOmicsScore, rnaDifferentialExpressionProfile.multiOmicsScore);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, hgncSymbol, targetRiskScore, geneticsScore, multiOmicsScore);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RnaDifferentialExpressionProfileDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    targetRiskScore: ").append(toIndentedString(targetRiskScore)).append("\n");
    sb.append("    geneticsScore: ").append(toIndentedString(geneticsScore)).append("\n");
    sb.append("    multiOmicsScore: ").append(toIndentedString(multiOmicsScore)).append("\n");
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

    private RnaDifferentialExpressionProfileDto instance;

    public Builder() {
      this(new RnaDifferentialExpressionProfileDto());
    }

    protected Builder(RnaDifferentialExpressionProfileDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(RnaDifferentialExpressionProfileDto value) { 
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setTargetRiskScore(value.targetRiskScore);
      this.instance.setGeneticsScore(value.geneticsScore);
      this.instance.setMultiOmicsScore(value.multiOmicsScore);
      return this;
    }

    public RnaDifferentialExpressionProfileDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder targetRiskScore(BigDecimal targetRiskScore) {
      this.instance.targetRiskScore(targetRiskScore);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder geneticsScore(BigDecimal geneticsScore) {
      this.instance.geneticsScore(geneticsScore);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder multiOmicsScore(BigDecimal multiOmicsScore) {
      this.instance.multiOmicsScore(multiOmicsScore);
      return this;
    }
    
    /**
    * returns a built RnaDifferentialExpressionProfileDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public RnaDifferentialExpressionProfileDto build() {
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
  public static RnaDifferentialExpressionProfileDto.Builder builder() {
    return new RnaDifferentialExpressionProfileDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public RnaDifferentialExpressionProfileDto.Builder toBuilder() {
    RnaDifferentialExpressionProfileDto.Builder builder = new RnaDifferentialExpressionProfileDto.Builder();
    return builder.copyOf(this);
  }

}

