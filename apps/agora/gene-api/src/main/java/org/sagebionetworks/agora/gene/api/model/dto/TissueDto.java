package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.sagebionetworks.agora.gene.api.model.dto.MedianExpressionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A tissue.
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "Tissue", description = "A tissue.")
@JsonTypeName("Tissue")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class TissueDto {

  private String name;

  private Float logfc;

  private Float adjPVal;

  private Float ciL;

  private Float ciR;

  private @Nullable MedianExpressionDto medianexpression;

  public TissueDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TissueDto(String name, Float logfc, Float adjPVal, Float ciL, Float ciR) {
    this.name = name;
    this.logfc = logfc;
    this.adjPVal = adjPVal;
    this.ciL = ciL;
    this.ciR = ciR;
  }

  public TissueDto name(String name) {
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

  public TissueDto logfc(Float logfc) {
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

  public TissueDto adjPVal(Float adjPVal) {
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

  public TissueDto ciL(Float ciL) {
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

  public TissueDto ciR(Float ciR) {
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

  public TissueDto medianexpression(MedianExpressionDto medianexpression) {
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
    TissueDto tissue = (TissueDto) o;
    return Objects.equals(this.name, tissue.name) &&
        Objects.equals(this.logfc, tissue.logfc) &&
        Objects.equals(this.adjPVal, tissue.adjPVal) &&
        Objects.equals(this.ciL, tissue.ciL) &&
        Objects.equals(this.ciR, tissue.ciR) &&
        Objects.equals(this.medianexpression, tissue.medianexpression);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, logfc, adjPVal, ciL, ciR, medianexpression);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TissueDto {\n");
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
  
  public static class Builder {

    private TissueDto instance;

    public Builder() {
      this(new TissueDto());
    }

    protected Builder(TissueDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(TissueDto value) { 
      this.instance.setName(value.name);
      this.instance.setLogfc(value.logfc);
      this.instance.setAdjPVal(value.adjPVal);
      this.instance.setCiL(value.ciL);
      this.instance.setCiR(value.ciR);
      this.instance.setMedianexpression(value.medianexpression);
      return this;
    }

    public TissueDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public TissueDto.Builder logfc(Float logfc) {
      this.instance.logfc(logfc);
      return this;
    }
    
    public TissueDto.Builder adjPVal(Float adjPVal) {
      this.instance.adjPVal(adjPVal);
      return this;
    }
    
    public TissueDto.Builder ciL(Float ciL) {
      this.instance.ciL(ciL);
      return this;
    }
    
    public TissueDto.Builder ciR(Float ciR) {
      this.instance.ciR(ciR);
      return this;
    }
    
    public TissueDto.Builder medianexpression(MedianExpressionDto medianexpression) {
      this.instance.medianexpression(medianexpression);
      return this;
    }
    
    /**
    * returns a built TissueDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public TissueDto build() {
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
  public static TissueDto.Builder builder() {
    return new TissueDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public TissueDto.Builder toBuilder() {
    TissueDto.Builder builder = new TissueDto.Builder();
    return builder.copyOf(this);
  }

}

