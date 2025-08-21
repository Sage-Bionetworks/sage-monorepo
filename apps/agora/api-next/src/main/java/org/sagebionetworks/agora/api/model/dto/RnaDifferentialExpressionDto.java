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
 * RnaDifferentialExpression
 */

@Schema(name = "RnaDifferentialExpression", description = "RnaDifferentialExpression")
@JsonTypeName("RnaDifferentialExpression")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class RnaDifferentialExpressionDto {

  private String id;

  private String ensemblGeneId;

  private String hgncSymbol;

  private BigDecimal logfc;

  private BigDecimal fc;

  private BigDecimal ciL;

  private BigDecimal ciR;

  private BigDecimal adjPVal;

  private String tissue;

  private String study;

  private String model;

  public RnaDifferentialExpressionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RnaDifferentialExpressionDto(String id, String ensemblGeneId, String hgncSymbol, BigDecimal logfc, BigDecimal fc, BigDecimal ciL, BigDecimal ciR, BigDecimal adjPVal, String tissue, String study, String model) {
    this.id = id;
    this.ensemblGeneId = ensemblGeneId;
    this.hgncSymbol = hgncSymbol;
    this.logfc = logfc;
    this.fc = fc;
    this.ciL = ciL;
    this.ciR = ciR;
    this.adjPVal = adjPVal;
    this.tissue = tissue;
    this.study = study;
    this.model = model;
  }

  public RnaDifferentialExpressionDto id(String id) {
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

  public RnaDifferentialExpressionDto ensemblGeneId(String ensemblGeneId) {
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

  public RnaDifferentialExpressionDto hgncSymbol(String hgncSymbol) {
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

  public RnaDifferentialExpressionDto logfc(BigDecimal logfc) {
    this.logfc = logfc;
    return this;
  }

  /**
   * Get logfc
   * @return logfc
   */
  @NotNull @Valid 
  @Schema(name = "logfc", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("logfc")
  public BigDecimal getLogfc() {
    return logfc;
  }

  public void setLogfc(BigDecimal logfc) {
    this.logfc = logfc;
  }

  public RnaDifferentialExpressionDto fc(BigDecimal fc) {
    this.fc = fc;
    return this;
  }

  /**
   * Get fc
   * @return fc
   */
  @NotNull @Valid 
  @Schema(name = "fc", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("fc")
  public BigDecimal getFc() {
    return fc;
  }

  public void setFc(BigDecimal fc) {
    this.fc = fc;
  }

  public RnaDifferentialExpressionDto ciL(BigDecimal ciL) {
    this.ciL = ciL;
    return this;
  }

  /**
   * Get ciL
   * @return ciL
   */
  @NotNull @Valid 
  @Schema(name = "ci_l", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_l")
  public BigDecimal getCiL() {
    return ciL;
  }

  public void setCiL(BigDecimal ciL) {
    this.ciL = ciL;
  }

  public RnaDifferentialExpressionDto ciR(BigDecimal ciR) {
    this.ciR = ciR;
    return this;
  }

  /**
   * Get ciR
   * @return ciR
   */
  @NotNull @Valid 
  @Schema(name = "ci_r", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_r")
  public BigDecimal getCiR() {
    return ciR;
  }

  public void setCiR(BigDecimal ciR) {
    this.ciR = ciR;
  }

  public RnaDifferentialExpressionDto adjPVal(BigDecimal adjPVal) {
    this.adjPVal = adjPVal;
    return this;
  }

  /**
   * Get adjPVal
   * @return adjPVal
   */
  @NotNull @Valid 
  @Schema(name = "adj_p_val", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adj_p_val")
  public BigDecimal getAdjPVal() {
    return adjPVal;
  }

  public void setAdjPVal(BigDecimal adjPVal) {
    this.adjPVal = adjPVal;
  }

  public RnaDifferentialExpressionDto tissue(String tissue) {
    this.tissue = tissue;
    return this;
  }

  /**
   * Get tissue
   * @return tissue
   */
  @NotNull 
  @Schema(name = "tissue", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tissue")
  public String getTissue() {
    return tissue;
  }

  public void setTissue(String tissue) {
    this.tissue = tissue;
  }

  public RnaDifferentialExpressionDto study(String study) {
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

  public RnaDifferentialExpressionDto model(String model) {
    this.model = model;
    return this;
  }

  /**
   * Get model
   * @return model
   */
  @NotNull 
  @Schema(name = "model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RnaDifferentialExpressionDto rnaDifferentialExpression = (RnaDifferentialExpressionDto) o;
    return Objects.equals(this.id, rnaDifferentialExpression.id) &&
        Objects.equals(this.ensemblGeneId, rnaDifferentialExpression.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, rnaDifferentialExpression.hgncSymbol) &&
        Objects.equals(this.logfc, rnaDifferentialExpression.logfc) &&
        Objects.equals(this.fc, rnaDifferentialExpression.fc) &&
        Objects.equals(this.ciL, rnaDifferentialExpression.ciL) &&
        Objects.equals(this.ciR, rnaDifferentialExpression.ciR) &&
        Objects.equals(this.adjPVal, rnaDifferentialExpression.adjPVal) &&
        Objects.equals(this.tissue, rnaDifferentialExpression.tissue) &&
        Objects.equals(this.study, rnaDifferentialExpression.study) &&
        Objects.equals(this.model, rnaDifferentialExpression.model);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ensemblGeneId, hgncSymbol, logfc, fc, ciL, ciR, adjPVal, tissue, study, model);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RnaDifferentialExpressionDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    logfc: ").append(toIndentedString(logfc)).append("\n");
    sb.append("    fc: ").append(toIndentedString(fc)).append("\n");
    sb.append("    ciL: ").append(toIndentedString(ciL)).append("\n");
    sb.append("    ciR: ").append(toIndentedString(ciR)).append("\n");
    sb.append("    adjPVal: ").append(toIndentedString(adjPVal)).append("\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    study: ").append(toIndentedString(study)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
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

    private RnaDifferentialExpressionDto instance;

    public Builder() {
      this(new RnaDifferentialExpressionDto());
    }

    protected Builder(RnaDifferentialExpressionDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(RnaDifferentialExpressionDto value) { 
      this.instance.setId(value.id);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setLogfc(value.logfc);
      this.instance.setFc(value.fc);
      this.instance.setCiL(value.ciL);
      this.instance.setCiR(value.ciR);
      this.instance.setAdjPVal(value.adjPVal);
      this.instance.setTissue(value.tissue);
      this.instance.setStudy(value.study);
      this.instance.setModel(value.model);
      return this;
    }

    public RnaDifferentialExpressionDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder logfc(BigDecimal logfc) {
      this.instance.logfc(logfc);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder fc(BigDecimal fc) {
      this.instance.fc(fc);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder ciL(BigDecimal ciL) {
      this.instance.ciL(ciL);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder ciR(BigDecimal ciR) {
      this.instance.ciR(ciR);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder adjPVal(BigDecimal adjPVal) {
      this.instance.adjPVal(adjPVal);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder study(String study) {
      this.instance.study(study);
      return this;
    }
    
    public RnaDifferentialExpressionDto.Builder model(String model) {
      this.instance.model(model);
      return this;
    }
    
    /**
    * returns a built RnaDifferentialExpressionDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public RnaDifferentialExpressionDto build() {
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
  public static RnaDifferentialExpressionDto.Builder builder() {
    return new RnaDifferentialExpressionDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public RnaDifferentialExpressionDto.Builder toBuilder() {
    RnaDifferentialExpressionDto.Builder builder = new RnaDifferentialExpressionDto.Builder();
    return builder.copyOf(this);
  }

}

