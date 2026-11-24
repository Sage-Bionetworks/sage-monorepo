package org.sagebionetworks.qtl.api.model.dto;

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
 * Five-number summary of gene expression for samples with one genotype.
 */

@Schema(name = "GenotypeExpressionSummary", description = "Five-number summary of gene expression for samples with one genotype.")
@JsonTypeName("GenotypeExpressionSummary")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GenotypeExpressionSummaryDto {

  private String genotype;

  private BigDecimal min;

  private BigDecimal firstQuartile;

  private BigDecimal median;

  private BigDecimal thirdQuartile;

  private BigDecimal max;

  public GenotypeExpressionSummaryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GenotypeExpressionSummaryDto(String genotype, BigDecimal min, BigDecimal firstQuartile, BigDecimal median, BigDecimal thirdQuartile, BigDecimal max) {
    this.genotype = genotype;
    this.min = min;
    this.firstQuartile = firstQuartile;
    this.median = median;
    this.thirdQuartile = thirdQuartile;
    this.max = max;
  }

  public GenotypeExpressionSummaryDto genotype(String genotype) {
    this.genotype = genotype;
    return this;
  }

  /**
   * Genotype
   * @return genotype
   */
  @NotNull 
  @Schema(name = "genotype", example = "CT", description = "Genotype", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("genotype")
  public String getGenotype() {
    return genotype;
  }

  public void setGenotype(String genotype) {
    this.genotype = genotype;
  }

  public GenotypeExpressionSummaryDto min(BigDecimal min) {
    this.min = min;
    return this;
  }

  /**
   * Minimum expression
   * @return min
   */
  @NotNull @Valid 
  @Schema(name = "min", example = "-2.2889", description = "Minimum expression", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("min")
  public BigDecimal getMin() {
    return min;
  }

  public void setMin(BigDecimal min) {
    this.min = min;
  }

  public GenotypeExpressionSummaryDto firstQuartile(BigDecimal firstQuartile) {
    this.firstQuartile = firstQuartile;
    return this;
  }

  /**
   * First quartile of expression
   * @return firstQuartile
   */
  @NotNull @Valid 
  @Schema(name = "first_quartile", example = "-0.5532", description = "First quartile of expression", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("first_quartile")
  public BigDecimal getFirstQuartile() {
    return firstQuartile;
  }

  public void setFirstQuartile(BigDecimal firstQuartile) {
    this.firstQuartile = firstQuartile;
  }

  public GenotypeExpressionSummaryDto median(BigDecimal median) {
    this.median = median;
    return this;
  }

  /**
   * Median expression
   * @return median
   */
  @NotNull @Valid 
  @Schema(name = "median", example = "-0.004", description = "Median expression", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("median")
  public BigDecimal getMedian() {
    return median;
  }

  public void setMedian(BigDecimal median) {
    this.median = median;
  }

  public GenotypeExpressionSummaryDto thirdQuartile(BigDecimal thirdQuartile) {
    this.thirdQuartile = thirdQuartile;
    return this;
  }

  /**
   * Third quartile of expression
   * @return thirdQuartile
   */
  @NotNull @Valid 
  @Schema(name = "third_quartile", example = "0.6039", description = "Third quartile of expression", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("third_quartile")
  public BigDecimal getThirdQuartile() {
    return thirdQuartile;
  }

  public void setThirdQuartile(BigDecimal thirdQuartile) {
    this.thirdQuartile = thirdQuartile;
  }

  public GenotypeExpressionSummaryDto max(BigDecimal max) {
    this.max = max;
    return this;
  }

  /**
   * Maximum expression
   * @return max
   */
  @NotNull @Valid 
  @Schema(name = "max", example = "2.3396", description = "Maximum expression", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("max")
  public BigDecimal getMax() {
    return max;
  }

  public void setMax(BigDecimal max) {
    this.max = max;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenotypeExpressionSummaryDto genotypeExpressionSummary = (GenotypeExpressionSummaryDto) o;
    return Objects.equals(this.genotype, genotypeExpressionSummary.genotype) &&
        Objects.equals(this.min, genotypeExpressionSummary.min) &&
        Objects.equals(this.firstQuartile, genotypeExpressionSummary.firstQuartile) &&
        Objects.equals(this.median, genotypeExpressionSummary.median) &&
        Objects.equals(this.thirdQuartile, genotypeExpressionSummary.thirdQuartile) &&
        Objects.equals(this.max, genotypeExpressionSummary.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(genotype, min, firstQuartile, median, thirdQuartile, max);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenotypeExpressionSummaryDto {\n");
    sb.append("    genotype: ").append(toIndentedString(genotype)).append("\n");
    sb.append("    min: ").append(toIndentedString(min)).append("\n");
    sb.append("    firstQuartile: ").append(toIndentedString(firstQuartile)).append("\n");
    sb.append("    median: ").append(toIndentedString(median)).append("\n");
    sb.append("    thirdQuartile: ").append(toIndentedString(thirdQuartile)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
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

    private GenotypeExpressionSummaryDto instance;

    public Builder() {
      this(new GenotypeExpressionSummaryDto());
    }

    protected Builder(GenotypeExpressionSummaryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GenotypeExpressionSummaryDto value) { 
      this.instance.setGenotype(value.genotype);
      this.instance.setMin(value.min);
      this.instance.setFirstQuartile(value.firstQuartile);
      this.instance.setMedian(value.median);
      this.instance.setThirdQuartile(value.thirdQuartile);
      this.instance.setMax(value.max);
      return this;
    }

    public GenotypeExpressionSummaryDto.Builder genotype(String genotype) {
      this.instance.genotype(genotype);
      return this;
    }
    
    public GenotypeExpressionSummaryDto.Builder min(BigDecimal min) {
      this.instance.min(min);
      return this;
    }
    
    public GenotypeExpressionSummaryDto.Builder firstQuartile(BigDecimal firstQuartile) {
      this.instance.firstQuartile(firstQuartile);
      return this;
    }
    
    public GenotypeExpressionSummaryDto.Builder median(BigDecimal median) {
      this.instance.median(median);
      return this;
    }
    
    public GenotypeExpressionSummaryDto.Builder thirdQuartile(BigDecimal thirdQuartile) {
      this.instance.thirdQuartile(thirdQuartile);
      return this;
    }
    
    public GenotypeExpressionSummaryDto.Builder max(BigDecimal max) {
      this.instance.max(max);
      return this;
    }
    
    /**
    * returns a built GenotypeExpressionSummaryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GenotypeExpressionSummaryDto build() {
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
  public static GenotypeExpressionSummaryDto.Builder builder() {
    return new GenotypeExpressionSummaryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GenotypeExpressionSummaryDto.Builder toBuilder() {
    GenotypeExpressionSummaryDto.Builder builder = new GenotypeExpressionSummaryDto.Builder();
    return builder.copyOf(this);
  }

}

