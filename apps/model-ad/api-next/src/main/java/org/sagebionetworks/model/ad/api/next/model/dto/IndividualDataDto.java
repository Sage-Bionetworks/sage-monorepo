package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Data for an individual
 */

@Schema(name = "IndividualData", description = "Data for an individual")
@JsonTypeName("IndividualData")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class IndividualDataDto {

  private String genotype;

  private SexDto sex;

  private String individualId;

  private BigDecimal value;

  public IndividualDataDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public IndividualDataDto(String genotype, SexDto sex, String individualId, BigDecimal value) {
    this.genotype = genotype;
    this.sex = sex;
    this.individualId = individualId;
    this.value = value;
  }

  public IndividualDataDto genotype(String genotype) {
    this.genotype = genotype;
    return this;
  }

  /**
   * Genotype of the individual
   * @return genotype
   */
  @NotNull 
  @Schema(name = "genotype", description = "Genotype of the individual", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("genotype")
  public String getGenotype() {
    return genotype;
  }

  public void setGenotype(String genotype) {
    this.genotype = genotype;
  }

  public IndividualDataDto sex(SexDto sex) {
    this.sex = sex;
    return this;
  }

  /**
   * Get sex
   * @return sex
   */
  @NotNull @Valid 
  @Schema(name = "sex", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sex")
  public SexDto getSex() {
    return sex;
  }

  public void setSex(SexDto sex) {
    this.sex = sex;
  }

  public IndividualDataDto individualId(String individualId) {
    this.individualId = individualId;
    return this;
  }

  /**
   * Unique identifier for the individual
   * @return individualId
   */
  @NotNull 
  @Schema(name = "individual_id", description = "Unique identifier for the individual", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("individual_id")
  public String getIndividualId() {
    return individualId;
  }

  public void setIndividualId(String individualId) {
    this.individualId = individualId;
  }

  public IndividualDataDto value(BigDecimal value) {
    this.value = value;
    return this;
  }

  /**
   * Value for the individual
   * @return value
   */
  @NotNull @Valid 
  @Schema(name = "value", description = "Value for the individual", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("value")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndividualDataDto individualData = (IndividualDataDto) o;
    return Objects.equals(this.genotype, individualData.genotype) &&
        Objects.equals(this.sex, individualData.sex) &&
        Objects.equals(this.individualId, individualData.individualId) &&
        Objects.equals(this.value, individualData.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(genotype, sex, individualId, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndividualDataDto {\n");
    sb.append("    genotype: ").append(toIndentedString(genotype)).append("\n");
    sb.append("    sex: ").append(toIndentedString(sex)).append("\n");
    sb.append("    individualId: ").append(toIndentedString(individualId)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

    private IndividualDataDto instance;

    public Builder() {
      this(new IndividualDataDto());
    }

    protected Builder(IndividualDataDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(IndividualDataDto value) { 
      this.instance.setGenotype(value.genotype);
      this.instance.setSex(value.sex);
      this.instance.setIndividualId(value.individualId);
      this.instance.setValue(value.value);
      return this;
    }

    public IndividualDataDto.Builder genotype(String genotype) {
      this.instance.genotype(genotype);
      return this;
    }
    
    public IndividualDataDto.Builder sex(SexDto sex) {
      this.instance.sex(sex);
      return this;
    }
    
    public IndividualDataDto.Builder individualId(String individualId) {
      this.instance.individualId(individualId);
      return this;
    }
    
    public IndividualDataDto.Builder value(BigDecimal value) {
      this.instance.value(value);
      return this;
    }
    
    /**
    * returns a built IndividualDataDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public IndividualDataDto build() {
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
  public static IndividualDataDto.Builder builder() {
    return new IndividualDataDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public IndividualDataDto.Builder toBuilder() {
    IndividualDataDto.Builder builder = new IndividualDataDto.Builder();
    return builder.copyOf(this);
  }

}

