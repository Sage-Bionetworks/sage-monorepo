package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.sagebionetworks.agora.api.model.dto.MedianExpressionDto;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * GCTGeneTissue
 */

@Schema(name = "GCTGeneTissue", description = "GCTGeneTissue")
@JsonTypeName("GCTGeneTissue")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class GCTGeneTissueDto {

  private String name;

  private Float logfc;

  private Float adjPVal;

  private Float ciL;

  private Float ciR;

  private MedianExpressionDto medianexpression;

  public GCTGeneTissueDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GCTGeneTissueDto(String name, Float logfc, Float adjPVal, Float ciL, Float ciR) {
    this.name = name;
    this.logfc = logfc;
    this.adjPVal = adjPVal;
    this.ciL = ciL;
    this.ciR = ciR;
  }

  public GCTGeneTissueDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the gene or tissue.
   * @return name
  */
  @NotNull 
  @Schema(name = "name", description = "Name of the gene or tissue.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GCTGeneTissueDto logfc(Float logfc) {
    this.logfc = logfc;
    return this;
  }

  /**
   * Log fold change value.
   * @return logfc
  */
  @NotNull 
  @Schema(name = "logfc", description = "Log fold change value.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("logfc")
  public Float getLogfc() {
    return logfc;
  }

  public void setLogfc(Float logfc) {
    this.logfc = logfc;
  }

  public GCTGeneTissueDto adjPVal(Float adjPVal) {
    this.adjPVal = adjPVal;
    return this;
  }

  /**
   * Adjusted p-value.
   * @return adjPVal
  */
  @NotNull 
  @Schema(name = "adj_p_val", description = "Adjusted p-value.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adj_p_val")
  public Float getAdjPVal() {
    return adjPVal;
  }

  public void setAdjPVal(Float adjPVal) {
    this.adjPVal = adjPVal;
  }

  public GCTGeneTissueDto ciL(Float ciL) {
    this.ciL = ciL;
    return this;
  }

  /**
   * Lower confidence interval.
   * @return ciL
  */
  @NotNull 
  @Schema(name = "ci_l", description = "Lower confidence interval.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_l")
  public Float getCiL() {
    return ciL;
  }

  public void setCiL(Float ciL) {
    this.ciL = ciL;
  }

  public GCTGeneTissueDto ciR(Float ciR) {
    this.ciR = ciR;
    return this;
  }

  /**
   * Upper confidence interval.
   * @return ciR
  */
  @NotNull 
  @Schema(name = "ci_r", description = "Upper confidence interval.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_r")
  public Float getCiR() {
    return ciR;
  }

  public void setCiR(Float ciR) {
    this.ciR = ciR;
  }

  public GCTGeneTissueDto medianexpression(MedianExpressionDto medianexpression) {
    this.medianexpression = medianexpression;
    return this;
  }

  /**
   * Get medianexpression
   * @return medianexpression
  */
  @Valid 
  @Schema(name = "medianexpression", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("medianexpression")
  public MedianExpressionDto getMedianexpression() {
    return medianexpression;
  }

  public void setMedianexpression(MedianExpressionDto medianexpression) {
    this.medianexpression = medianexpression;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GCTGeneTissueDto gcTGeneTissue = (GCTGeneTissueDto) o;
    return Objects.equals(this.name, gcTGeneTissue.name) &&
        Objects.equals(this.logfc, gcTGeneTissue.logfc) &&
        Objects.equals(this.adjPVal, gcTGeneTissue.adjPVal) &&
        Objects.equals(this.ciL, gcTGeneTissue.ciL) &&
        Objects.equals(this.ciR, gcTGeneTissue.ciR) &&
        Objects.equals(this.medianexpression, gcTGeneTissue.medianexpression);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, logfc, adjPVal, ciL, ciR, medianexpression);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GCTGeneTissueDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    logfc: ").append(toIndentedString(logfc)).append("\n");
    sb.append("    adjPVal: ").append(toIndentedString(adjPVal)).append("\n");
    sb.append("    ciL: ").append(toIndentedString(ciL)).append("\n");
    sb.append("    ciR: ").append(toIndentedString(ciR)).append("\n");
    sb.append("    medianexpression: ").append(toIndentedString(medianexpression)).append("\n");
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

