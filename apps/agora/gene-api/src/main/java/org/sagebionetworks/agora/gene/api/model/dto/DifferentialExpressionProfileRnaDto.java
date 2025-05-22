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
 * A differential expression profile (RNA).
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "DifferentialExpressionProfileRna", description = "A differential expression profile (RNA).")
@JsonTypeName("DifferentialExpressionProfileRna")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class DifferentialExpressionProfileRnaDto {

  private String ensemblGeneId;

  private @Nullable String hgncSymbol;

  private @Nullable BigDecimal targetRiskScore = null;

  public DifferentialExpressionProfileRnaDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DifferentialExpressionProfileRnaDto(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public DifferentialExpressionProfileRnaDto ensemblGeneId(String ensemblGeneId) {
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

  public DifferentialExpressionProfileRnaDto hgncSymbol(String hgncSymbol) {
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

  public DifferentialExpressionProfileRnaDto targetRiskScore(BigDecimal targetRiskScore) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DifferentialExpressionProfileRnaDto differentialExpressionProfileRna = (DifferentialExpressionProfileRnaDto) o;
    return Objects.equals(this.ensemblGeneId, differentialExpressionProfileRna.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, differentialExpressionProfileRna.hgncSymbol) &&
        Objects.equals(this.targetRiskScore, differentialExpressionProfileRna.targetRiskScore);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, hgncSymbol, targetRiskScore);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DifferentialExpressionProfileRnaDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    targetRiskScore: ").append(toIndentedString(targetRiskScore)).append("\n");
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

    private DifferentialExpressionProfileRnaDto instance;

    public Builder() {
      this(new DifferentialExpressionProfileRnaDto());
    }

    protected Builder(DifferentialExpressionProfileRnaDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DifferentialExpressionProfileRnaDto value) { 
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setTargetRiskScore(value.targetRiskScore);
      return this;
    }

    public DifferentialExpressionProfileRnaDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public DifferentialExpressionProfileRnaDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public DifferentialExpressionProfileRnaDto.Builder targetRiskScore(BigDecimal targetRiskScore) {
      this.instance.targetRiskScore(targetRiskScore);
      return this;
    }
    
    /**
    * returns a built DifferentialExpressionProfileRnaDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DifferentialExpressionProfileRnaDto build() {
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
  public static DifferentialExpressionProfileRnaDto.Builder builder() {
    return new DifferentialExpressionProfileRnaDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DifferentialExpressionProfileRnaDto.Builder toBuilder() {
    DifferentialExpressionProfileRnaDto.Builder builder = new DifferentialExpressionProfileRnaDto.Builder();
    return builder.copyOf(this);
  }

}

