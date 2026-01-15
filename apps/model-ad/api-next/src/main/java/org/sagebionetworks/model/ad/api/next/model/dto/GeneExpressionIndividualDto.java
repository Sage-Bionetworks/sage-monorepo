package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * Gene Expression Individual
 */

@Schema(name = "GeneExpressionIndividual", description = "Gene Expression Individual")
@JsonTypeName("GeneExpressionIndividual")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GeneExpressionIndividualDto {

  private String ensemblGeneId;

  private String geneSymbol;

  private String tissue;

  private String name;

  private @Nullable String modelGroup = null;

  private String matchedControl;

  private String units;

  private String age;

  private Integer ageNumeric;

  @Valid
  private List<String> resultOrder = new ArrayList<>();

  @Valid
  private List<@Valid IndividualDataDto> data = new ArrayList<>();

  public GeneExpressionIndividualDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneExpressionIndividualDto(String ensemblGeneId, String geneSymbol, String tissue, String name, String matchedControl, String units, String age, Integer ageNumeric, List<String> resultOrder, List<@Valid IndividualDataDto> data) {
    this.ensemblGeneId = ensemblGeneId;
    this.geneSymbol = geneSymbol;
    this.tissue = tissue;
    this.name = name;
    this.matchedControl = matchedControl;
    this.units = units;
    this.age = age;
    this.ageNumeric = ageNumeric;
    this.resultOrder = resultOrder;
    this.data = data;
  }

  public GeneExpressionIndividualDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Ensembl Gene ID
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", description = "Ensembl Gene ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public GeneExpressionIndividualDto geneSymbol(String geneSymbol) {
    this.geneSymbol = geneSymbol;
    return this;
  }

  /**
   * Gene Symbol
   * @return geneSymbol
   */
  @NotNull 
  @Schema(name = "gene_symbol", example = "Gnai3", description = "Gene Symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("gene_symbol")
  public String getGeneSymbol() {
    return geneSymbol;
  }

  public void setGeneSymbol(String geneSymbol) {
    this.geneSymbol = geneSymbol;
  }

  public GeneExpressionIndividualDto tissue(String tissue) {
    this.tissue = tissue;
    return this;
  }

  /**
   * Tissue type
   * @return tissue
   */
  @NotNull 
  @Schema(name = "tissue", example = "Hemibrain", description = "Tissue type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tissue")
  public String getTissue() {
    return tissue;
  }

  public void setTissue(String tissue) {
    this.tissue = tissue;
  }

  public GeneExpressionIndividualDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Model name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", description = "Model name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GeneExpressionIndividualDto modelGroup(@Nullable String modelGroup) {
    this.modelGroup = modelGroup;
    return this;
  }

  /**
   * Model group
   * @return modelGroup
   */
  
  @Schema(name = "model_group", description = "Model group", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("model_group")
  public @Nullable String getModelGroup() {
    return modelGroup;
  }

  public void setModelGroup(@Nullable String modelGroup) {
    this.modelGroup = modelGroup;
  }

  public GeneExpressionIndividualDto matchedControl(String matchedControl) {
    this.matchedControl = matchedControl;
    return this;
  }

  /**
   * Matched control for the model
   * @return matchedControl
   */
  @NotNull 
  @Schema(name = "matched_control", example = "C57BL/6J", description = "Matched control for the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("matched_control")
  public String getMatchedControl() {
    return matchedControl;
  }

  public void setMatchedControl(String matchedControl) {
    this.matchedControl = matchedControl;
  }

  public GeneExpressionIndividualDto units(String units) {
    this.units = units;
    return this;
  }

  /**
   * Units of measurement for gene expression
   * @return units
   */
  @NotNull 
  @Schema(name = "units", example = "Log2 Counts per Million", description = "Units of measurement for gene expression", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("units")
  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public GeneExpressionIndividualDto age(String age) {
    this.age = age;
    return this;
  }

  /**
   * Age of the model
   * @return age
   */
  @NotNull 
  @Schema(name = "age", example = "4 months", description = "Age of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("age")
  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public GeneExpressionIndividualDto ageNumeric(Integer ageNumeric) {
    this.ageNumeric = ageNumeric;
    return this;
  }

  /**
   * Numeric representation of the age
   * @return ageNumeric
   */
  @NotNull 
  @Schema(name = "age_numeric", example = "4", description = "Numeric representation of the age", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("age_numeric")
  public Integer getAgeNumeric() {
    return ageNumeric;
  }

  public void setAgeNumeric(Integer ageNumeric) {
    this.ageNumeric = ageNumeric;
  }

  public GeneExpressionIndividualDto resultOrder(List<String> resultOrder) {
    this.resultOrder = resultOrder;
    return this;
  }

  public GeneExpressionIndividualDto addResultOrderItem(String resultOrderItem) {
    if (this.resultOrder == null) {
      this.resultOrder = new ArrayList<>();
    }
    this.resultOrder.add(resultOrderItem);
    return this;
  }

  /**
   * List of genotypes in the order to display results
   * @return resultOrder
   */
  @NotNull 
  @Schema(name = "result_order", example = "[\"C57BL6J\",\"Trem2\"]", description = "List of genotypes in the order to display results", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("result_order")
  public List<String> getResultOrder() {
    return resultOrder;
  }

  public void setResultOrder(List<String> resultOrder) {
    this.resultOrder = resultOrder;
  }

  public GeneExpressionIndividualDto data(List<@Valid IndividualDataDto> data) {
    this.data = data;
    return this;
  }

  public GeneExpressionIndividualDto addDataItem(IndividualDataDto dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<>();
    }
    this.data.add(dataItem);
    return this;
  }

  /**
   * List of individual gene expression data points
   * @return data
   */
  @NotNull @Valid 
  @Schema(name = "data", description = "List of individual gene expression data points", requiredMode = Schema.RequiredMode.REQUIRED)
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
    GeneExpressionIndividualDto geneExpressionIndividual = (GeneExpressionIndividualDto) o;
    return Objects.equals(this.ensemblGeneId, geneExpressionIndividual.ensemblGeneId) &&
        Objects.equals(this.geneSymbol, geneExpressionIndividual.geneSymbol) &&
        Objects.equals(this.tissue, geneExpressionIndividual.tissue) &&
        Objects.equals(this.name, geneExpressionIndividual.name) &&
        Objects.equals(this.modelGroup, geneExpressionIndividual.modelGroup) &&
        Objects.equals(this.matchedControl, geneExpressionIndividual.matchedControl) &&
        Objects.equals(this.units, geneExpressionIndividual.units) &&
        Objects.equals(this.age, geneExpressionIndividual.age) &&
        Objects.equals(this.ageNumeric, geneExpressionIndividual.ageNumeric) &&
        Objects.equals(this.resultOrder, geneExpressionIndividual.resultOrder) &&
        Objects.equals(this.data, geneExpressionIndividual.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, geneSymbol, tissue, name, modelGroup, matchedControl, units, age, ageNumeric, resultOrder, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneExpressionIndividualDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    geneSymbol: ").append(toIndentedString(geneSymbol)).append("\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    modelGroup: ").append(toIndentedString(modelGroup)).append("\n");
    sb.append("    matchedControl: ").append(toIndentedString(matchedControl)).append("\n");
    sb.append("    units: ").append(toIndentedString(units)).append("\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
    sb.append("    ageNumeric: ").append(toIndentedString(ageNumeric)).append("\n");
    sb.append("    resultOrder: ").append(toIndentedString(resultOrder)).append("\n");
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

    private GeneExpressionIndividualDto instance;

    public Builder() {
      this(new GeneExpressionIndividualDto());
    }

    protected Builder(GeneExpressionIndividualDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneExpressionIndividualDto value) { 
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setGeneSymbol(value.geneSymbol);
      this.instance.setTissue(value.tissue);
      this.instance.setName(value.name);
      this.instance.setModelGroup(value.modelGroup);
      this.instance.setMatchedControl(value.matchedControl);
      this.instance.setUnits(value.units);
      this.instance.setAge(value.age);
      this.instance.setAgeNumeric(value.ageNumeric);
      this.instance.setResultOrder(value.resultOrder);
      this.instance.setData(value.data);
      return this;
    }

    public GeneExpressionIndividualDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder geneSymbol(String geneSymbol) {
      this.instance.geneSymbol(geneSymbol);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder modelGroup(String modelGroup) {
      this.instance.modelGroup(modelGroup);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder matchedControl(String matchedControl) {
      this.instance.matchedControl(matchedControl);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder units(String units) {
      this.instance.units(units);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder age(String age) {
      this.instance.age(age);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder ageNumeric(Integer ageNumeric) {
      this.instance.ageNumeric(ageNumeric);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder resultOrder(List<String> resultOrder) {
      this.instance.resultOrder(resultOrder);
      return this;
    }
    
    public GeneExpressionIndividualDto.Builder data(List<IndividualDataDto> data) {
      this.instance.data(data);
      return this;
    }
    
    /**
    * returns a built GeneExpressionIndividualDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GeneExpressionIndividualDto build() {
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
  public static GeneExpressionIndividualDto.Builder builder() {
    return new GeneExpressionIndividualDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GeneExpressionIndividualDto.Builder toBuilder() {
    GeneExpressionIndividualDto.Builder builder = new GeneExpressionIndividualDto.Builder();
    return builder.copyOf(this);
  }

}

