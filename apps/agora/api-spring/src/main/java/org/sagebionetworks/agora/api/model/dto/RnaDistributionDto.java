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
 * Distributions
 */

@Schema(name = "RnaDistribution", description = "Distributions")
@JsonTypeName("RnaDistribution")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class RnaDistributionDto {

  private String id;

  private String model;

  private String tissue;

  private BigDecimal min;

  private BigDecimal max;

  private BigDecimal firstQuartile;

  private BigDecimal median;

  private BigDecimal thirdQuartile;

  public RnaDistributionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RnaDistributionDto(String id, String model, String tissue, BigDecimal min, BigDecimal max, BigDecimal firstQuartile, BigDecimal median, BigDecimal thirdQuartile) {
    this.id = id;
    this.model = model;
    this.tissue = tissue;
    this.min = min;
    this.max = max;
    this.firstQuartile = firstQuartile;
    this.median = median;
    this.thirdQuartile = thirdQuartile;
  }

  public RnaDistributionDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * ID of the RNA distribution
   * @return id
  */
  @NotNull 
  @Schema(name = "_id", description = "ID of the RNA distribution", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public RnaDistributionDto model(String model) {
    this.model = model;
    return this;
  }

  /**
   * Model of the RNA data
   * @return model
  */
  @NotNull 
  @Schema(name = "model", description = "Model of the RNA data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public RnaDistributionDto tissue(String tissue) {
    this.tissue = tissue;
    return this;
  }

  /**
   * Tissue type
   * @return tissue
  */
  @NotNull 
  @Schema(name = "tissue", description = "Tissue type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tissue")
  public String getTissue() {
    return tissue;
  }

  public void setTissue(String tissue) {
    this.tissue = tissue;
  }

  public RnaDistributionDto min(BigDecimal min) {
    this.min = min;
    return this;
  }

  /**
   * Minimum value in the distribution
   * @return min
  */
  @NotNull @Valid 
  @Schema(name = "min", description = "Minimum value in the distribution", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("min")
  public BigDecimal getMin() {
    return min;
  }

  public void setMin(BigDecimal min) {
    this.min = min;
  }

  public RnaDistributionDto max(BigDecimal max) {
    this.max = max;
    return this;
  }

  /**
   * Maximum value in the distribution
   * @return max
  */
  @NotNull @Valid 
  @Schema(name = "max", description = "Maximum value in the distribution", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("max")
  public BigDecimal getMax() {
    return max;
  }

  public void setMax(BigDecimal max) {
    this.max = max;
  }

  public RnaDistributionDto firstQuartile(BigDecimal firstQuartile) {
    this.firstQuartile = firstQuartile;
    return this;
  }

  /**
   * First quartile value
   * @return firstQuartile
  */
  @NotNull @Valid 
  @Schema(name = "first_quartile", description = "First quartile value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("first_quartile")
  public BigDecimal getFirstQuartile() {
    return firstQuartile;
  }

  public void setFirstQuartile(BigDecimal firstQuartile) {
    this.firstQuartile = firstQuartile;
  }

  public RnaDistributionDto median(BigDecimal median) {
    this.median = median;
    return this;
  }

  /**
   * Median value
   * @return median
  */
  @NotNull @Valid 
  @Schema(name = "median", description = "Median value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("median")
  public BigDecimal getMedian() {
    return median;
  }

  public void setMedian(BigDecimal median) {
    this.median = median;
  }

  public RnaDistributionDto thirdQuartile(BigDecimal thirdQuartile) {
    this.thirdQuartile = thirdQuartile;
    return this;
  }

  /**
   * Third quartile value
   * @return thirdQuartile
  */
  @NotNull @Valid 
  @Schema(name = "third_quartile", description = "Third quartile value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("third_quartile")
  public BigDecimal getThirdQuartile() {
    return thirdQuartile;
  }

  public void setThirdQuartile(BigDecimal thirdQuartile) {
    this.thirdQuartile = thirdQuartile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RnaDistributionDto rnaDistribution = (RnaDistributionDto) o;
    return Objects.equals(this.id, rnaDistribution.id) &&
        Objects.equals(this.model, rnaDistribution.model) &&
        Objects.equals(this.tissue, rnaDistribution.tissue) &&
        Objects.equals(this.min, rnaDistribution.min) &&
        Objects.equals(this.max, rnaDistribution.max) &&
        Objects.equals(this.firstQuartile, rnaDistribution.firstQuartile) &&
        Objects.equals(this.median, rnaDistribution.median) &&
        Objects.equals(this.thirdQuartile, rnaDistribution.thirdQuartile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, model, tissue, min, max, firstQuartile, median, thirdQuartile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RnaDistributionDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    min: ").append(toIndentedString(min)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
    sb.append("    firstQuartile: ").append(toIndentedString(firstQuartile)).append("\n");
    sb.append("    median: ").append(toIndentedString(median)).append("\n");
    sb.append("    thirdQuartile: ").append(toIndentedString(thirdQuartile)).append("\n");
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

