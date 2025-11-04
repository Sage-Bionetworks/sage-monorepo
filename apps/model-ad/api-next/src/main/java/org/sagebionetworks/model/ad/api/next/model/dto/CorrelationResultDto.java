package org.sagebionetworks.model.ad.api.next.model.dto;

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
 * Correlation result
 */

@Schema(name = "CorrelationResult", description = "Correlation result")
@JsonTypeName("CorrelationResult")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class CorrelationResultDto {

  private BigDecimal correlation;

  private BigDecimal adjPVal;

  public CorrelationResultDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CorrelationResultDto(BigDecimal correlation, BigDecimal adjPVal) {
    this.correlation = correlation;
    this.adjPVal = adjPVal;
  }

  public CorrelationResultDto correlation(BigDecimal correlation) {
    this.correlation = correlation;
    return this;
  }

  /**
   * Correlation value
   * @return correlation
   */
  @NotNull @Valid 
  @Schema(name = "correlation", description = "Correlation value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("correlation")
  public BigDecimal getCorrelation() {
    return correlation;
  }

  public void setCorrelation(BigDecimal correlation) {
    this.correlation = correlation;
  }

  public CorrelationResultDto adjPVal(BigDecimal adjPVal) {
    this.adjPVal = adjPVal;
    return this;
  }

  /**
   * Adjusted p-value
   * @return adjPVal
   */
  @NotNull @Valid 
  @Schema(name = "adj_p_val", description = "Adjusted p-value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adj_p_val")
  public BigDecimal getAdjPVal() {
    return adjPVal;
  }

  public void setAdjPVal(BigDecimal adjPVal) {
    this.adjPVal = adjPVal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CorrelationResultDto correlationResult = (CorrelationResultDto) o;
    return Objects.equals(this.correlation, correlationResult.correlation) &&
        Objects.equals(this.adjPVal, correlationResult.adjPVal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(correlation, adjPVal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CorrelationResultDto {\n");
    sb.append("    correlation: ").append(toIndentedString(correlation)).append("\n");
    sb.append("    adjPVal: ").append(toIndentedString(adjPVal)).append("\n");
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

    private CorrelationResultDto instance;

    public Builder() {
      this(new CorrelationResultDto());
    }

    protected Builder(CorrelationResultDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(CorrelationResultDto value) { 
      this.instance.setCorrelation(value.correlation);
      this.instance.setAdjPVal(value.adjPVal);
      return this;
    }

    public CorrelationResultDto.Builder correlation(BigDecimal correlation) {
      this.instance.correlation(correlation);
      return this;
    }
    
    public CorrelationResultDto.Builder adjPVal(BigDecimal adjPVal) {
      this.instance.adjPVal(adjPVal);
      return this;
    }
    
    /**
    * returns a built CorrelationResultDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public CorrelationResultDto build() {
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
  public static CorrelationResultDto.Builder builder() {
    return new CorrelationResultDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public CorrelationResultDto.Builder toBuilder() {
    CorrelationResultDto.Builder builder = new CorrelationResultDto.Builder();
    return builder.copyOf(this);
  }

}

