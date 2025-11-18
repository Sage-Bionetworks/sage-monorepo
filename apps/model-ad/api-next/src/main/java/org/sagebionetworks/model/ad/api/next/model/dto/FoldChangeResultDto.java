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
 * Fold Change result
 */

@Schema(name = "FoldChangeResult", description = "Fold Change result")
@JsonTypeName("FoldChangeResult")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class FoldChangeResultDto {

  private BigDecimal log2Fc;

  private BigDecimal adjPVal;

  public FoldChangeResultDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FoldChangeResultDto(BigDecimal log2Fc, BigDecimal adjPVal) {
    this.log2Fc = log2Fc;
    this.adjPVal = adjPVal;
  }

  public FoldChangeResultDto log2Fc(BigDecimal log2Fc) {
    this.log2Fc = log2Fc;
    return this;
  }

  /**
   * Log2 fold change
   * @return log2Fc
   */
  @NotNull @Valid 
  @Schema(name = "log2_fc", description = "Log2 fold change", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("log2_fc")
  public BigDecimal getLog2Fc() {
    return log2Fc;
  }

  public void setLog2Fc(BigDecimal log2Fc) {
    this.log2Fc = log2Fc;
  }

  public FoldChangeResultDto adjPVal(BigDecimal adjPVal) {
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
    FoldChangeResultDto foldChangeResult = (FoldChangeResultDto) o;
    return Objects.equals(this.log2Fc, foldChangeResult.log2Fc) &&
        Objects.equals(this.adjPVal, foldChangeResult.adjPVal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(log2Fc, adjPVal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FoldChangeResultDto {\n");
    sb.append("    log2Fc: ").append(toIndentedString(log2Fc)).append("\n");
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

    private FoldChangeResultDto instance;

    public Builder() {
      this(new FoldChangeResultDto());
    }

    protected Builder(FoldChangeResultDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(FoldChangeResultDto value) { 
      this.instance.setLog2Fc(value.log2Fc);
      this.instance.setAdjPVal(value.adjPVal);
      return this;
    }

    public FoldChangeResultDto.Builder log2Fc(BigDecimal log2Fc) {
      this.instance.log2Fc(log2Fc);
      return this;
    }
    
    public FoldChangeResultDto.Builder adjPVal(BigDecimal adjPVal) {
      this.instance.adjPVal(adjPVal);
      return this;
    }
    
    /**
    * returns a built FoldChangeResultDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public FoldChangeResultDto build() {
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
  public static FoldChangeResultDto.Builder builder() {
    return new FoldChangeResultDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public FoldChangeResultDto.Builder toBuilder() {
    FoldChangeResultDto.Builder builder = new FoldChangeResultDto.Builder();
    return builder.copyOf(this);
  }

}

