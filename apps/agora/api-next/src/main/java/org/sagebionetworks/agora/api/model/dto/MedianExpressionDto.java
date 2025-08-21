package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MedianExpression
 */

@Schema(name = "MedianExpression", description = "MedianExpression")
@JsonTypeName("MedianExpression")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MedianExpressionDto {

  private @Nullable Float min;

  private @Nullable Float firstQuartile;

  private @Nullable Float median;

  private @Nullable Float mean;

  private @Nullable Float thirdQuartile;

  private @Nullable Float max;

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

  public MedianExpressionDto min(@Nullable Float min) {
    this.min = min;
    return this;
  }

  /**
   * Get min
   * @return min
   */
  
  @Schema(name = "min", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min")
  public @Nullable Float getMin() {
    return min;
  }

  public void setMin(@Nullable Float min) {
    this.min = min;
  }

  public MedianExpressionDto firstQuartile(@Nullable Float firstQuartile) {
    this.firstQuartile = firstQuartile;
    return this;
  }

  /**
   * Get firstQuartile
   * @return firstQuartile
   */
  
  @Schema(name = "first_quartile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("first_quartile")
  public @Nullable Float getFirstQuartile() {
    return firstQuartile;
  }

  public void setFirstQuartile(@Nullable Float firstQuartile) {
    this.firstQuartile = firstQuartile;
  }

  public MedianExpressionDto median(@Nullable Float median) {
    this.median = median;
    return this;
  }

  /**
   * Get median
   * @return median
   */
  
  @Schema(name = "median", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("median")
  public @Nullable Float getMedian() {
    return median;
  }

  public void setMedian(@Nullable Float median) {
    this.median = median;
  }

  public MedianExpressionDto mean(@Nullable Float mean) {
    this.mean = mean;
    return this;
  }

  /**
   * Get mean
   * @return mean
   */
  
  @Schema(name = "mean", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mean")
  public @Nullable Float getMean() {
    return mean;
  }

  public void setMean(@Nullable Float mean) {
    this.mean = mean;
  }

  public MedianExpressionDto thirdQuartile(@Nullable Float thirdQuartile) {
    this.thirdQuartile = thirdQuartile;
    return this;
  }

  /**
   * Get thirdQuartile
   * @return thirdQuartile
   */
  
  @Schema(name = "third_quartile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("third_quartile")
  public @Nullable Float getThirdQuartile() {
    return thirdQuartile;
  }

  public void setThirdQuartile(@Nullable Float thirdQuartile) {
    this.thirdQuartile = thirdQuartile;
  }

  public MedianExpressionDto max(@Nullable Float max) {
    this.max = max;
    return this;
  }

  /**
   * Get max
   * @return max
   */
  
  @Schema(name = "max", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("max")
  public @Nullable Float getMax() {
    return max;
  }

  public void setMax(@Nullable Float max) {
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
  
  public static class Builder {

    private MedianExpressionDto instance;

    public Builder() {
      this(new MedianExpressionDto());
    }

    protected Builder(MedianExpressionDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MedianExpressionDto value) { 
      this.instance.setMin(value.min);
      this.instance.setFirstQuartile(value.firstQuartile);
      this.instance.setMedian(value.median);
      this.instance.setMean(value.mean);
      this.instance.setThirdQuartile(value.thirdQuartile);
      this.instance.setMax(value.max);
      this.instance.setTissue(value.tissue);
      return this;
    }

    public MedianExpressionDto.Builder min(Float min) {
      this.instance.min(min);
      return this;
    }
    
    public MedianExpressionDto.Builder firstQuartile(Float firstQuartile) {
      this.instance.firstQuartile(firstQuartile);
      return this;
    }
    
    public MedianExpressionDto.Builder median(Float median) {
      this.instance.median(median);
      return this;
    }
    
    public MedianExpressionDto.Builder mean(Float mean) {
      this.instance.mean(mean);
      return this;
    }
    
    public MedianExpressionDto.Builder thirdQuartile(Float thirdQuartile) {
      this.instance.thirdQuartile(thirdQuartile);
      return this;
    }
    
    public MedianExpressionDto.Builder max(Float max) {
      this.instance.max(max);
      return this;
    }
    
    public MedianExpressionDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    /**
    * returns a built MedianExpressionDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MedianExpressionDto build() {
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
  public static MedianExpressionDto.Builder builder() {
    return new MedianExpressionDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MedianExpressionDto.Builder toBuilder() {
    MedianExpressionDto.Builder builder = new MedianExpressionDto.Builder();
    return builder.copyOf(this);
  }

}

