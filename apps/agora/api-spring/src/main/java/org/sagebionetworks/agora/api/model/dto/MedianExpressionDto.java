package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * MedianExpression
 */

@Schema(name = "MedianExpression", description = "MedianExpression")
@JsonTypeName("MedianExpression")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class MedianExpressionDto {

  private Float min;

  private Float firstQuartile;

  private Float median;

  private Float mean;

  private Float thirdQuartile;

  private Float max;

  private String tissue;

  public MedianExpressionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MedianExpressionDto(String tissue) {
    this.tissue = tissue;
  }

  public MedianExpressionDto min(Float min) {
    this.min = min;
    return this;
  }

  /**
   * Get min
   * @return min
  */
  
  @Schema(name = "min", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min")
  public Float getMin() {
    return min;
  }

  public void setMin(Float min) {
    this.min = min;
  }

  public MedianExpressionDto firstQuartile(Float firstQuartile) {
    this.firstQuartile = firstQuartile;
    return this;
  }

  /**
   * Get firstQuartile
   * @return firstQuartile
  */
  
  @Schema(name = "first_quartile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("first_quartile")
  public Float getFirstQuartile() {
    return firstQuartile;
  }

  public void setFirstQuartile(Float firstQuartile) {
    this.firstQuartile = firstQuartile;
  }

  public MedianExpressionDto median(Float median) {
    this.median = median;
    return this;
  }

  /**
   * Get median
   * @return median
  */
  
  @Schema(name = "median", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("median")
  public Float getMedian() {
    return median;
  }

  public void setMedian(Float median) {
    this.median = median;
  }

  public MedianExpressionDto mean(Float mean) {
    this.mean = mean;
    return this;
  }

  /**
   * Get mean
   * @return mean
  */
  
  @Schema(name = "mean", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mean")
  public Float getMean() {
    return mean;
  }

  public void setMean(Float mean) {
    this.mean = mean;
  }

  public MedianExpressionDto thirdQuartile(Float thirdQuartile) {
    this.thirdQuartile = thirdQuartile;
    return this;
  }

  /**
   * Get thirdQuartile
   * @return thirdQuartile
  */
  
  @Schema(name = "third_quartile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("third_quartile")
  public Float getThirdQuartile() {
    return thirdQuartile;
  }

  public void setThirdQuartile(Float thirdQuartile) {
    this.thirdQuartile = thirdQuartile;
  }

  public MedianExpressionDto max(Float max) {
    this.max = max;
    return this;
  }

  /**
   * Get max
   * @return max
  */
  
  @Schema(name = "max", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("max")
  public Float getMax() {
    return max;
  }

  public void setMax(Float max) {
    this.max = max;
  }

  public MedianExpressionDto tissue(String tissue) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MedianExpressionDto medianExpression = (MedianExpressionDto) o;
    return Objects.equals(this.min, medianExpression.min) &&
        Objects.equals(this.firstQuartile, medianExpression.firstQuartile) &&
        Objects.equals(this.median, medianExpression.median) &&
        Objects.equals(this.mean, medianExpression.mean) &&
        Objects.equals(this.thirdQuartile, medianExpression.thirdQuartile) &&
        Objects.equals(this.max, medianExpression.max) &&
        Objects.equals(this.tissue, medianExpression.tissue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, firstQuartile, median, mean, thirdQuartile, max, tissue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MedianExpressionDto {\n");
    sb.append("    min: ").append(toIndentedString(min)).append("\n");
    sb.append("    firstQuartile: ").append(toIndentedString(firstQuartile)).append("\n");
    sb.append("    median: ").append(toIndentedString(median)).append("\n");
    sb.append("    mean: ").append(toIndentedString(mean)).append("\n");
    sb.append("    thirdQuartile: ").append(toIndentedString(thirdQuartile)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
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

