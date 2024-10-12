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
 * ProteinDifferentialExpression
 */

@Schema(name = "ProteinDifferentialExpression", description = "ProteinDifferentialExpression")
@JsonTypeName("ProteinDifferentialExpression")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ProteinDifferentialExpressionDto {

  private String id;

  private String uniqid;

  private String hgncSymbol;

  private String uniprotid;

  private String ensemblGeneId;

  private String tissue;

  private BigDecimal log2Fc;

  private BigDecimal ciUpr;

  private BigDecimal ciLwr;

  private BigDecimal pval;

  private BigDecimal corPval;

  public ProteinDifferentialExpressionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProteinDifferentialExpressionDto(String id, String uniqid, String hgncSymbol, String uniprotid, String ensemblGeneId, String tissue, BigDecimal log2Fc, BigDecimal ciUpr, BigDecimal ciLwr, BigDecimal pval, BigDecimal corPval) {
    this.id = id;
    this.uniqid = uniqid;
    this.hgncSymbol = hgncSymbol;
    this.uniprotid = uniprotid;
    this.ensemblGeneId = ensemblGeneId;
    this.tissue = tissue;
    this.log2Fc = log2Fc;
    this.ciUpr = ciUpr;
    this.ciLwr = ciLwr;
    this.pval = pval;
    this.corPval = corPval;
  }

  public ProteinDifferentialExpressionDto id(String id) {
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

  public ProteinDifferentialExpressionDto uniqid(String uniqid) {
    this.uniqid = uniqid;
    return this;
  }

  /**
   * Get uniqid
   * @return uniqid
  */
  @NotNull 
  @Schema(name = "uniqid", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("uniqid")
  public String getUniqid() {
    return uniqid;
  }

  public void setUniqid(String uniqid) {
    this.uniqid = uniqid;
  }

  public ProteinDifferentialExpressionDto hgncSymbol(String hgncSymbol) {
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

  public ProteinDifferentialExpressionDto uniprotid(String uniprotid) {
    this.uniprotid = uniprotid;
    return this;
  }

  /**
   * Get uniprotid
   * @return uniprotid
  */
  @NotNull 
  @Schema(name = "uniprotid", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("uniprotid")
  public String getUniprotid() {
    return uniprotid;
  }

  public void setUniprotid(String uniprotid) {
    this.uniprotid = uniprotid;
  }

  public ProteinDifferentialExpressionDto ensemblGeneId(String ensemblGeneId) {
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

  public ProteinDifferentialExpressionDto tissue(String tissue) {
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

  public ProteinDifferentialExpressionDto log2Fc(BigDecimal log2Fc) {
    this.log2Fc = log2Fc;
    return this;
  }

  /**
   * Get log2Fc
   * @return log2Fc
  */
  @NotNull @Valid 
  @Schema(name = "log2_fc", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("log2_fc")
  public BigDecimal getLog2Fc() {
    return log2Fc;
  }

  public void setLog2Fc(BigDecimal log2Fc) {
    this.log2Fc = log2Fc;
  }

  public ProteinDifferentialExpressionDto ciUpr(BigDecimal ciUpr) {
    this.ciUpr = ciUpr;
    return this;
  }

  /**
   * Get ciUpr
   * @return ciUpr
  */
  @NotNull @Valid 
  @Schema(name = "ci_upr", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_upr")
  public BigDecimal getCiUpr() {
    return ciUpr;
  }

  public void setCiUpr(BigDecimal ciUpr) {
    this.ciUpr = ciUpr;
  }

  public ProteinDifferentialExpressionDto ciLwr(BigDecimal ciLwr) {
    this.ciLwr = ciLwr;
    return this;
  }

  /**
   * Get ciLwr
   * @return ciLwr
  */
  @NotNull @Valid 
  @Schema(name = "ci_lwr", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_lwr")
  public BigDecimal getCiLwr() {
    return ciLwr;
  }

  public void setCiLwr(BigDecimal ciLwr) {
    this.ciLwr = ciLwr;
  }

  public ProteinDifferentialExpressionDto pval(BigDecimal pval) {
    this.pval = pval;
    return this;
  }

  /**
   * Get pval
   * @return pval
  */
  @NotNull @Valid 
  @Schema(name = "pval", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pval")
  public BigDecimal getPval() {
    return pval;
  }

  public void setPval(BigDecimal pval) {
    this.pval = pval;
  }

  public ProteinDifferentialExpressionDto corPval(BigDecimal corPval) {
    this.corPval = corPval;
    return this;
  }

  /**
   * Get corPval
   * @return corPval
  */
  @NotNull @Valid 
  @Schema(name = "cor_pval", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cor_pval")
  public BigDecimal getCorPval() {
    return corPval;
  }

  public void setCorPval(BigDecimal corPval) {
    this.corPval = corPval;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProteinDifferentialExpressionDto proteinDifferentialExpression = (ProteinDifferentialExpressionDto) o;
    return Objects.equals(this.id, proteinDifferentialExpression.id) &&
        Objects.equals(this.uniqid, proteinDifferentialExpression.uniqid) &&
        Objects.equals(this.hgncSymbol, proteinDifferentialExpression.hgncSymbol) &&
        Objects.equals(this.uniprotid, proteinDifferentialExpression.uniprotid) &&
        Objects.equals(this.ensemblGeneId, proteinDifferentialExpression.ensemblGeneId) &&
        Objects.equals(this.tissue, proteinDifferentialExpression.tissue) &&
        Objects.equals(this.log2Fc, proteinDifferentialExpression.log2Fc) &&
        Objects.equals(this.ciUpr, proteinDifferentialExpression.ciUpr) &&
        Objects.equals(this.ciLwr, proteinDifferentialExpression.ciLwr) &&
        Objects.equals(this.pval, proteinDifferentialExpression.pval) &&
        Objects.equals(this.corPval, proteinDifferentialExpression.corPval);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uniqid, hgncSymbol, uniprotid, ensemblGeneId, tissue, log2Fc, ciUpr, ciLwr, pval, corPval);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProteinDifferentialExpressionDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uniqid: ").append(toIndentedString(uniqid)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    uniprotid: ").append(toIndentedString(uniprotid)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    log2Fc: ").append(toIndentedString(log2Fc)).append("\n");
    sb.append("    ciUpr: ").append(toIndentedString(ciUpr)).append("\n");
    sb.append("    ciLwr: ").append(toIndentedString(ciLwr)).append("\n");
    sb.append("    pval: ").append(toIndentedString(pval)).append("\n");
    sb.append("    corPval: ").append(toIndentedString(corPval)).append("\n");
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

