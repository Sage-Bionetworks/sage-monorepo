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
 * Effect size, with its confidence interval, in one cell class.
 */

@Schema(name = "CellClassEffectSize", description = "Effect size, with its confidence interval, in one cell class.")
@JsonTypeName("CellClassEffectSize")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class CellClassEffectSizeDto {

  private String cellTypeId;

  private String cellClass;

  private String cellClassShort;

  private BigDecimal value;

  private BigDecimal ciLeft;

  private BigDecimal ciRight;

  public CellClassEffectSizeDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CellClassEffectSizeDto(String cellTypeId, String cellClass, String cellClassShort, BigDecimal value, BigDecimal ciLeft, BigDecimal ciRight) {
    this.cellTypeId = cellTypeId;
    this.cellClass = cellClass;
    this.cellClassShort = cellClassShort;
    this.value = value;
    this.ciLeft = ciLeft;
    this.ciRight = ciRight;
  }

  public CellClassEffectSizeDto cellTypeId(String cellTypeId) {
    this.cellTypeId = cellTypeId;
    return this;
  }

  /**
   * Cell type ID of the cell class
   * @return cellTypeId
   */
  @NotNull 
  @Schema(name = "cell_type_id", example = "class_astro", description = "Cell type ID of the cell class", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_type_id")
  public String getCellTypeId() {
    return cellTypeId;
  }

  public void setCellTypeId(String cellTypeId) {
    this.cellTypeId = cellTypeId;
  }

  public CellClassEffectSizeDto cellClass(String cellClass) {
    this.cellClass = cellClass;
    return this;
  }

  /**
   * Cell class
   * @return cellClass
   */
  @NotNull 
  @Schema(name = "cell_class", example = "Astrocyte", description = "Cell class", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_class")
  public String getCellClass() {
    return cellClass;
  }

  public void setCellClass(String cellClass) {
    this.cellClass = cellClass;
  }

  public CellClassEffectSizeDto cellClassShort(String cellClassShort) {
    this.cellClassShort = cellClassShort;
    return this;
  }

  /**
   * Abbreviated cell class
   * @return cellClassShort
   */
  @NotNull 
  @Schema(name = "cell_class_short", example = "Astro", description = "Abbreviated cell class", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_class_short")
  public String getCellClassShort() {
    return cellClassShort;
  }

  public void setCellClassShort(String cellClassShort) {
    this.cellClassShort = cellClassShort;
  }

  public CellClassEffectSizeDto value(BigDecimal value) {
    this.value = value;
    return this;
  }

  /**
   * Effect size (beta)
   * @return value
   */
  @NotNull @Valid 
  @Schema(name = "value", example = "0.442711", description = "Effect size (beta)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("value")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public CellClassEffectSizeDto ciLeft(BigDecimal ciLeft) {
    this.ciLeft = ciLeft;
    return this;
  }

  /**
   * Lower bound of the confidence interval
   * @return ciLeft
   */
  @NotNull @Valid 
  @Schema(name = "ci_left", example = "0.137115", description = "Lower bound of the confidence interval", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_left")
  public BigDecimal getCiLeft() {
    return ciLeft;
  }

  public void setCiLeft(BigDecimal ciLeft) {
    this.ciLeft = ciLeft;
  }

  public CellClassEffectSizeDto ciRight(BigDecimal ciRight) {
    this.ciRight = ciRight;
    return this;
  }

  /**
   * Upper bound of the confidence interval
   * @return ciRight
   */
  @NotNull @Valid 
  @Schema(name = "ci_right", example = "0.748307", description = "Upper bound of the confidence interval", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ci_right")
  public BigDecimal getCiRight() {
    return ciRight;
  }

  public void setCiRight(BigDecimal ciRight) {
    this.ciRight = ciRight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CellClassEffectSizeDto cellClassEffectSize = (CellClassEffectSizeDto) o;
    return Objects.equals(this.cellTypeId, cellClassEffectSize.cellTypeId) &&
        Objects.equals(this.cellClass, cellClassEffectSize.cellClass) &&
        Objects.equals(this.cellClassShort, cellClassEffectSize.cellClassShort) &&
        Objects.equals(this.value, cellClassEffectSize.value) &&
        Objects.equals(this.ciLeft, cellClassEffectSize.ciLeft) &&
        Objects.equals(this.ciRight, cellClassEffectSize.ciRight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellTypeId, cellClass, cellClassShort, value, ciLeft, ciRight);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CellClassEffectSizeDto {\n");
    sb.append("    cellTypeId: ").append(toIndentedString(cellTypeId)).append("\n");
    sb.append("    cellClass: ").append(toIndentedString(cellClass)).append("\n");
    sb.append("    cellClassShort: ").append(toIndentedString(cellClassShort)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    ciLeft: ").append(toIndentedString(ciLeft)).append("\n");
    sb.append("    ciRight: ").append(toIndentedString(ciRight)).append("\n");
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

    private CellClassEffectSizeDto instance;

    public Builder() {
      this(new CellClassEffectSizeDto());
    }

    protected Builder(CellClassEffectSizeDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(CellClassEffectSizeDto value) { 
      this.instance.setCellTypeId(value.cellTypeId);
      this.instance.setCellClass(value.cellClass);
      this.instance.setCellClassShort(value.cellClassShort);
      this.instance.setValue(value.value);
      this.instance.setCiLeft(value.ciLeft);
      this.instance.setCiRight(value.ciRight);
      return this;
    }

    public CellClassEffectSizeDto.Builder cellTypeId(String cellTypeId) {
      this.instance.cellTypeId(cellTypeId);
      return this;
    }
    
    public CellClassEffectSizeDto.Builder cellClass(String cellClass) {
      this.instance.cellClass(cellClass);
      return this;
    }
    
    public CellClassEffectSizeDto.Builder cellClassShort(String cellClassShort) {
      this.instance.cellClassShort(cellClassShort);
      return this;
    }
    
    public CellClassEffectSizeDto.Builder value(BigDecimal value) {
      this.instance.value(value);
      return this;
    }
    
    public CellClassEffectSizeDto.Builder ciLeft(BigDecimal ciLeft) {
      this.instance.ciLeft(ciLeft);
      return this;
    }
    
    public CellClassEffectSizeDto.Builder ciRight(BigDecimal ciRight) {
      this.instance.ciRight(ciRight);
      return this;
    }
    
    /**
    * returns a built CellClassEffectSizeDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public CellClassEffectSizeDto build() {
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
  public static CellClassEffectSizeDto.Builder builder() {
    return new CellClassEffectSizeDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public CellClassEffectSizeDto.Builder toBuilder() {
    CellClassEffectSizeDto.Builder builder = new CellClassEffectSizeDto.Builder();
    return builder.copyOf(this);
  }

}

