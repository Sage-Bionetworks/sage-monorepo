package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.FoldChangeResultDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexCohortDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Gene Expression
 */

@Schema(name = "GeneExpression", description = "Gene Expression")
@JsonTypeName("GeneExpression")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GeneExpressionDto {

  private String compositeId;

  private String ensemblGeneId;

  private String geneSymbol;

  @Valid
  private List<String> biodomains = new ArrayList<>();

  private String name;

  private String matchedControl;

  private String modelGroup = null;

  private String modelType;

  private String tissue;

  private SexCohortDto sex;

  private @Nullable FoldChangeResultDto _4months;

  private @Nullable FoldChangeResultDto _12months;

  private @Nullable FoldChangeResultDto _18months;

  public GeneExpressionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneExpressionDto(String compositeId, String ensemblGeneId, String geneSymbol, List<String> biodomains, String name, String matchedControl, String modelGroup, String modelType, String tissue, SexCohortDto sex) {
    this.compositeId = compositeId;
    this.ensemblGeneId = ensemblGeneId;
    this.geneSymbol = geneSymbol;
    this.biodomains = biodomains;
    this.name = name;
    this.matchedControl = matchedControl;
    this.modelGroup = modelGroup;
    this.modelType = modelType;
    this.tissue = tissue;
    this.sex = sex;
  }

  public GeneExpressionDto compositeId(String compositeId) {
    this.compositeId = compositeId;
    return this;
  }

  /**
   * Unique identifier for the gene expression object
   * @return compositeId
   */
  @NotNull 
  @Schema(name = "composite_id", example = "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)", description = "Unique identifier for the gene expression object", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("composite_id")
  public String getCompositeId() {
    return compositeId;
  }

  public void setCompositeId(String compositeId) {
    this.compositeId = compositeId;
  }

  public GeneExpressionDto ensemblGeneId(String ensemblGeneId) {
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

  public GeneExpressionDto geneSymbol(String geneSymbol) {
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

  public GeneExpressionDto biodomains(List<String> biodomains) {
    this.biodomains = biodomains;
    return this;
  }

  public GeneExpressionDto addBiodomainsItem(String biodomainsItem) {
    if (this.biodomains == null) {
      this.biodomains = new ArrayList<>();
    }
    this.biodomains.add(biodomainsItem);
    return this;
  }

  /**
   * List of biodomains associated with the gene
   * @return biodomains
   */
  @NotNull 
  @Schema(name = "biodomains", description = "List of biodomains associated with the gene", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("biodomains")
  public List<String> getBiodomains() {
    return biodomains;
  }

  public void setBiodomains(List<String> biodomains) {
    this.biodomains = biodomains;
  }

  public GeneExpressionDto name(String name) {
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

  public GeneExpressionDto matchedControl(String matchedControl) {
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

  public GeneExpressionDto modelGroup(String modelGroup) {
    this.modelGroup = modelGroup;
    return this;
  }

  /**
   * Model group
   * @return modelGroup
   */
  @NotNull 
  @Schema(name = "model_group", description = "Model group", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model_group")
  public String getModelGroup() {
    return modelGroup;
  }

  public void setModelGroup(String modelGroup) {
    this.modelGroup = modelGroup;
  }

  public GeneExpressionDto modelType(String modelType) {
    this.modelType = modelType;
    return this;
  }

  /**
   * Type of model
   * @return modelType
   */
  @NotNull 
  @Schema(name = "model_type", example = "Familial AD", description = "Type of model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model_type")
  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

  public GeneExpressionDto tissue(String tissue) {
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

  public GeneExpressionDto sex(SexCohortDto sex) {
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
  public SexCohortDto getSex() {
    return sex;
  }

  public void setSex(SexCohortDto sex) {
    this.sex = sex;
  }

  public GeneExpressionDto _4months(@Nullable FoldChangeResultDto _4months) {
    this._4months = _4months;
    return this;
  }

  /**
   * Get _4months
   * @return _4months
   */
  @Valid 
  @Schema(name = "4 months", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("4 months")
  public @Nullable FoldChangeResultDto get4months() {
    return _4months;
  }

  public void set4months(@Nullable FoldChangeResultDto _4months) {
    this._4months = _4months;
  }

  public GeneExpressionDto _12months(@Nullable FoldChangeResultDto _12months) {
    this._12months = _12months;
    return this;
  }

  /**
   * Get _12months
   * @return _12months
   */
  @Valid 
  @Schema(name = "12 months", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("12 months")
  public @Nullable FoldChangeResultDto get12months() {
    return _12months;
  }

  public void set12months(@Nullable FoldChangeResultDto _12months) {
    this._12months = _12months;
  }

  public GeneExpressionDto _18months(@Nullable FoldChangeResultDto _18months) {
    this._18months = _18months;
    return this;
  }

  /**
   * Get _18months
   * @return _18months
   */
  @Valid 
  @Schema(name = "18 months", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("18 months")
  public @Nullable FoldChangeResultDto get18months() {
    return _18months;
  }

  public void set18months(@Nullable FoldChangeResultDto _18months) {
    this._18months = _18months;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeneExpressionDto geneExpression = (GeneExpressionDto) o;
    return Objects.equals(this.compositeId, geneExpression.compositeId) &&
        Objects.equals(this.ensemblGeneId, geneExpression.ensemblGeneId) &&
        Objects.equals(this.geneSymbol, geneExpression.geneSymbol) &&
        Objects.equals(this.biodomains, geneExpression.biodomains) &&
        Objects.equals(this.name, geneExpression.name) &&
        Objects.equals(this.matchedControl, geneExpression.matchedControl) &&
        Objects.equals(this.modelGroup, geneExpression.modelGroup) &&
        Objects.equals(this.modelType, geneExpression.modelType) &&
        Objects.equals(this.tissue, geneExpression.tissue) &&
        Objects.equals(this.sex, geneExpression.sex) &&
        Objects.equals(this._4months, geneExpression._4months) &&
        Objects.equals(this._12months, geneExpression._12months) &&
        Objects.equals(this._18months, geneExpression._18months);
  }

  @Override
  public int hashCode() {
    return Objects.hash(compositeId, ensemblGeneId, geneSymbol, biodomains, name, matchedControl, modelGroup, modelType, tissue, sex, _4months, _12months, _18months);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneExpressionDto {\n");
    sb.append("    compositeId: ").append(toIndentedString(compositeId)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    geneSymbol: ").append(toIndentedString(geneSymbol)).append("\n");
    sb.append("    biodomains: ").append(toIndentedString(biodomains)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    matchedControl: ").append(toIndentedString(matchedControl)).append("\n");
    sb.append("    modelGroup: ").append(toIndentedString(modelGroup)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    sex: ").append(toIndentedString(sex)).append("\n");
    sb.append("    _4months: ").append(toIndentedString(_4months)).append("\n");
    sb.append("    _12months: ").append(toIndentedString(_12months)).append("\n");
    sb.append("    _18months: ").append(toIndentedString(_18months)).append("\n");
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

    private GeneExpressionDto instance;

    public Builder() {
      this(new GeneExpressionDto());
    }

    protected Builder(GeneExpressionDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneExpressionDto value) { 
      this.instance.setCompositeId(value.compositeId);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setGeneSymbol(value.geneSymbol);
      this.instance.setBiodomains(value.biodomains);
      this.instance.setName(value.name);
      this.instance.setMatchedControl(value.matchedControl);
      this.instance.setModelGroup(value.modelGroup);
      this.instance.setModelType(value.modelType);
      this.instance.setTissue(value.tissue);
      this.instance.setSex(value.sex);
      this.instance.set4months(value._4months);
      this.instance.set12months(value._12months);
      this.instance.set18months(value._18months);
      return this;
    }

    public GeneExpressionDto.Builder compositeId(String compositeId) {
      this.instance.compositeId(compositeId);
      return this;
    }
    
    public GeneExpressionDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public GeneExpressionDto.Builder geneSymbol(String geneSymbol) {
      this.instance.geneSymbol(geneSymbol);
      return this;
    }
    
    public GeneExpressionDto.Builder biodomains(List<String> biodomains) {
      this.instance.biodomains(biodomains);
      return this;
    }
    
    public GeneExpressionDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public GeneExpressionDto.Builder matchedControl(String matchedControl) {
      this.instance.matchedControl(matchedControl);
      return this;
    }
    
    public GeneExpressionDto.Builder modelGroup(String modelGroup) {
      this.instance.modelGroup(modelGroup);
      return this;
    }
    
    public GeneExpressionDto.Builder modelType(String modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public GeneExpressionDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    public GeneExpressionDto.Builder sex(SexCohortDto sex) {
      this.instance.sex(sex);
      return this;
    }
    
    public GeneExpressionDto.Builder _4months(FoldChangeResultDto _4months) {
      this.instance._4months(_4months);
      return this;
    }
    
    public GeneExpressionDto.Builder _12months(FoldChangeResultDto _12months) {
      this.instance._12months(_12months);
      return this;
    }
    
    public GeneExpressionDto.Builder _18months(FoldChangeResultDto _18months) {
      this.instance._18months(_18months);
      return this;
    }
    
    /**
    * returns a built GeneExpressionDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GeneExpressionDto build() {
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
  public static GeneExpressionDto.Builder builder() {
    return new GeneExpressionDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GeneExpressionDto.Builder toBuilder() {
    GeneExpressionDto.Builder builder = new GeneExpressionDto.Builder();
    return builder.copyOf(this);
  }

}

