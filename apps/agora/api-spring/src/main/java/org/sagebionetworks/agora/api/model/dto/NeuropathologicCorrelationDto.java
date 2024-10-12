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
 * NeuropathologicCorrelation
 */

@Schema(name = "NeuropathologicCorrelation", description = "NeuropathologicCorrelation")
@JsonTypeName("NeuropathologicCorrelation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class NeuropathologicCorrelationDto {

  private String id;

  private String ensg;

  private String gname;

  private BigDecimal oddsratio;

  private BigDecimal ciLower;

  private BigDecimal ciUpper;

  private BigDecimal pval;

  private BigDecimal pvalAdj;

  private String neuropathType;

  public NeuropathologicCorrelationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NeuropathologicCorrelationDto(String id, String ensg, String gname, BigDecimal oddsratio, BigDecimal ciLower, BigDecimal ciUpper, BigDecimal pval, BigDecimal pvalAdj, String neuropathType) {
    this.id = id;
    this.ensg = ensg;
    this.gname = gname;
    this.oddsratio = oddsratio;
    this.ciLower = ciLower;
    this.ciUpper = ciUpper;
    this.pval = pval;
    this.pvalAdj = pvalAdj;
    this.neuropathType = neuropathType;
  }

  public NeuropathologicCorrelationDto id(String id) {
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

  public NeuropathologicCorrelationDto ensg(String ensg) {
    this.ensg = ensg;
    return this;
  }

  /**
   * Get ensg
   * @return ensg
  */
  @NotNull 
  @Schema(name = "ensg", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensg")
  public String getEnsg() {
    return ensg;
  }

  public void setEnsg(String ensg) {
    this.ensg = ensg;
  }

  public NeuropathologicCorrelationDto gname(String gname) {
    this.gname = gname;
    return this;
  }

  /**
   * Get gname
   * @return gname
  */
  @NotNull 
  @Schema(name = "gname", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("gname")
  public String getGname() {
    return gname;
  }

  public void setGname(String gname) {
    this.gname = gname;
  }

  public NeuropathologicCorrelationDto oddsratio(BigDecimal oddsratio) {
    this.oddsratio = oddsratio;
    return this;
  }

  /**
   * Get oddsratio
   * @return oddsratio
  */
  @NotNull @Valid 
  @Schema(name = "oddsratio", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("oddsratio")
  public BigDecimal getOddsratio() {
    return oddsratio;
  }

  public void setOddsratio(BigDecimal oddsratio) {
    this.oddsratio = oddsratio;
  }

  public NeuropathologicCorrelationDto ciLower(BigDecimal ciLower) {
    this.ciLower = ciLower;
    return this;
  }

  /**
   * Get ciLower
   * @return ciLower
  */
  @NotNull @Valid 
  @Schema(name = "ci_lower", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_lower")
  public BigDecimal getCiLower() {
    return ciLower;
  }

  public void setCiLower(BigDecimal ciLower) {
    this.ciLower = ciLower;
  }

  public NeuropathologicCorrelationDto ciUpper(BigDecimal ciUpper) {
    this.ciUpper = ciUpper;
    return this;
  }

  /**
   * Get ciUpper
   * @return ciUpper
  */
  @NotNull @Valid 
  @Schema(name = "ci_upper", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_upper")
  public BigDecimal getCiUpper() {
    return ciUpper;
  }

  public void setCiUpper(BigDecimal ciUpper) {
    this.ciUpper = ciUpper;
  }

  public NeuropathologicCorrelationDto pval(BigDecimal pval) {
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

  public NeuropathologicCorrelationDto pvalAdj(BigDecimal pvalAdj) {
    this.pvalAdj = pvalAdj;
    return this;
  }

  /**
   * Get pvalAdj
   * @return pvalAdj
  */
  @NotNull @Valid 
  @Schema(name = "pval_adj", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pval_adj")
  public BigDecimal getPvalAdj() {
    return pvalAdj;
  }

  public void setPvalAdj(BigDecimal pvalAdj) {
    this.pvalAdj = pvalAdj;
  }

  public NeuropathologicCorrelationDto neuropathType(String neuropathType) {
    this.neuropathType = neuropathType;
    return this;
  }

  /**
   * Get neuropathType
   * @return neuropathType
  */
  @NotNull 
  @Schema(name = "neuropath_type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("neuropath_type")
  public String getNeuropathType() {
    return neuropathType;
  }

  public void setNeuropathType(String neuropathType) {
    this.neuropathType = neuropathType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NeuropathologicCorrelationDto neuropathologicCorrelation = (NeuropathologicCorrelationDto) o;
    return Objects.equals(this.id, neuropathologicCorrelation.id) &&
        Objects.equals(this.ensg, neuropathologicCorrelation.ensg) &&
        Objects.equals(this.gname, neuropathologicCorrelation.gname) &&
        Objects.equals(this.oddsratio, neuropathologicCorrelation.oddsratio) &&
        Objects.equals(this.ciLower, neuropathologicCorrelation.ciLower) &&
        Objects.equals(this.ciUpper, neuropathologicCorrelation.ciUpper) &&
        Objects.equals(this.pval, neuropathologicCorrelation.pval) &&
        Objects.equals(this.pvalAdj, neuropathologicCorrelation.pvalAdj) &&
        Objects.equals(this.neuropathType, neuropathologicCorrelation.neuropathType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ensg, gname, oddsratio, ciLower, ciUpper, pval, pvalAdj, neuropathType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NeuropathologicCorrelationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    ensg: ").append(toIndentedString(ensg)).append("\n");
    sb.append("    gname: ").append(toIndentedString(gname)).append("\n");
    sb.append("    oddsratio: ").append(toIndentedString(oddsratio)).append("\n");
    sb.append("    ciLower: ").append(toIndentedString(ciLower)).append("\n");
    sb.append("    ciUpper: ").append(toIndentedString(ciUpper)).append("\n");
    sb.append("    pval: ").append(toIndentedString(pval)).append("\n");
    sb.append("    pvalAdj: ").append(toIndentedString(pvalAdj)).append("\n");
    sb.append("    neuropathType: ").append(toIndentedString(neuropathType)).append("\n");
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

