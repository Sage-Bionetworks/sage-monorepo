package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.IndividualDataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Data associated with a model
 */

@Schema(name = "ModelData", description = "Data associated with a model")
@JsonTypeName("ModelData")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelDataDto {

  private String name;

  private String evidenceType;

  private String tissue;

  private String age;

  private String units;

  private BigDecimal yAxisMax;

  @Valid
  private List<@Valid IndividualDataDto> data = new ArrayList<>();

  public ModelDataDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelDataDto(String name, String evidenceType, String tissue, String age, String units, BigDecimal yAxisMax, List<@Valid IndividualDataDto> data) {
    this.name = name;
    this.evidenceType = evidenceType;
    this.tissue = tissue;
    this.age = age;
    this.units = units;
    this.yAxisMax = yAxisMax;
    this.data = data;
  }

  public ModelDataDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the model
   * @return name
   */
  @NotNull 
  @Schema(name = "name", description = "Name of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ModelDataDto evidenceType(String evidenceType) {
    this.evidenceType = evidenceType;
    return this;
  }

  /**
   * Type of evidence
   * @return evidenceType
   */
  @NotNull 
  @Schema(name = "evidence_type", description = "Type of evidence", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("evidence_type")
  public String getEvidenceType() {
    return evidenceType;
  }

  public void setEvidenceType(String evidenceType) {
    this.evidenceType = evidenceType;
  }

  public ModelDataDto tissue(String tissue) {
    this.tissue = tissue;
    return this;
  }

  /**
   * Tissue collected
   * @return tissue
   */
  @NotNull 
  @Schema(name = "tissue", description = "Tissue collected", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tissue")
  public String getTissue() {
    return tissue;
  }

  public void setTissue(String tissue) {
    this.tissue = tissue;
  }

  public ModelDataDto age(String age) {
    this.age = age;
    return this;
  }

  /**
   * Age at collection
   * @return age
   */
  @NotNull 
  @Schema(name = "age", description = "Age at collection", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("age")
  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public ModelDataDto units(String units) {
    this.units = units;
    return this;
  }

  /**
   * Units of measurement
   * @return units
   */
  @NotNull 
  @Schema(name = "units", description = "Units of measurement", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("units")
  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public ModelDataDto yAxisMax(BigDecimal yAxisMax) {
    this.yAxisMax = yAxisMax;
    return this;
  }

  /**
   * Maximum value for y-axis in visualizations
   * @return yAxisMax
   */
  @NotNull @Valid 
  @Schema(name = "y_axis_max", description = "Maximum value for y-axis in visualizations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("y_axis_max")
  public BigDecimal getyAxisMax() {
    return yAxisMax;
  }

  public void setyAxisMax(BigDecimal yAxisMax) {
    this.yAxisMax = yAxisMax;
  }

  public ModelDataDto data(List<@Valid IndividualDataDto> data) {
    this.data = data;
    return this;
  }

  public ModelDataDto addDataItem(IndividualDataDto dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<>();
    }
    this.data.add(dataItem);
    return this;
  }

  /**
   * Values for each individual
   * @return data
   */
  @NotNull @Valid 
  @Schema(name = "data", description = "Values for each individual", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data")
  public List<@Valid IndividualDataDto> getData() {
    return data;
  }

  public void setData(List<@Valid IndividualDataDto> data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelDataDto modelData = (ModelDataDto) o;
    return Objects.equals(this.name, modelData.name) &&
        Objects.equals(this.evidenceType, modelData.evidenceType) &&
        Objects.equals(this.tissue, modelData.tissue) &&
        Objects.equals(this.age, modelData.age) &&
        Objects.equals(this.units, modelData.units) &&
        Objects.equals(this.yAxisMax, modelData.yAxisMax) &&
        Objects.equals(this.data, modelData.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, evidenceType, tissue, age, units, yAxisMax, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelDataDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    evidenceType: ").append(toIndentedString(evidenceType)).append("\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
    sb.append("    units: ").append(toIndentedString(units)).append("\n");
    sb.append("    yAxisMax: ").append(toIndentedString(yAxisMax)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

    private ModelDataDto instance;

    public Builder() {
      this(new ModelDataDto());
    }

    protected Builder(ModelDataDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelDataDto value) { 
      this.instance.setName(value.name);
      this.instance.setEvidenceType(value.evidenceType);
      this.instance.setTissue(value.tissue);
      this.instance.setAge(value.age);
      this.instance.setUnits(value.units);
      this.instance.setyAxisMax(value.yAxisMax);
      this.instance.setData(value.data);
      return this;
    }

    public ModelDataDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ModelDataDto.Builder evidenceType(String evidenceType) {
      this.instance.evidenceType(evidenceType);
      return this;
    }
    
    public ModelDataDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    public ModelDataDto.Builder age(String age) {
      this.instance.age(age);
      return this;
    }
    
    public ModelDataDto.Builder units(String units) {
      this.instance.units(units);
      return this;
    }
    
    public ModelDataDto.Builder yAxisMax(BigDecimal yAxisMax) {
      this.instance.yAxisMax(yAxisMax);
      return this;
    }
    
    public ModelDataDto.Builder data(List<IndividualDataDto> data) {
      this.instance.data(data);
      return this;
    }
    
    /**
    * returns a built ModelDataDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelDataDto build() {
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
  public static ModelDataDto.Builder builder() {
    return new ModelDataDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelDataDto.Builder toBuilder() {
    ModelDataDto.Builder builder = new ModelDataDto.Builder();
    return builder.copyOf(this);
  }

}

