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
import org.sagebionetworks.model.ad.api.next.model.dto.CorrelationResultDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Disease Correlation
 */

@Schema(name = "DiseaseCorrelation", description = "Disease Correlation")
@JsonTypeName("DiseaseCorrelation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class DiseaseCorrelationDto {

  private String id;

  private String name;

  private String matchedControl;

  private String modelType;

  @Valid
  private List<String> modifiedGenes = new ArrayList<>();

  private String cluster;

  private String age;

  private SexDto sex;

  private @Nullable CorrelationResultDto IFG;

  private @Nullable CorrelationResultDto PHG;

  private @Nullable CorrelationResultDto TCX;

  private @Nullable CorrelationResultDto CBE;

  private @Nullable CorrelationResultDto DLPFC;

  private @Nullable CorrelationResultDto FP;

  private @Nullable CorrelationResultDto STG;

  public DiseaseCorrelationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DiseaseCorrelationDto(String id, String name, String matchedControl, String modelType, List<String> modifiedGenes, String cluster, String age, SexDto sex) {
    this.id = id;
    this.name = name;
    this.matchedControl = matchedControl;
    this.modelType = modelType;
    this.modifiedGenes = modifiedGenes;
    this.cluster = cluster;
    this.age = age;
    this.sex = sex;
  }

  public DiseaseCorrelationDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the disease correlation object
   * @return id
   */
  @NotNull 
  @Schema(name = "_id", description = "Unique identifier for the disease correlation object", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DiseaseCorrelationDto name(String name) {
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

  public DiseaseCorrelationDto matchedControl(String matchedControl) {
    this.matchedControl = matchedControl;
    return this;
  }

  /**
   * Matched control for the model
   * @return matchedControl
   */
  @NotNull 
  @Schema(name = "matched_control", description = "Matched control for the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("matched_control")
  public String getMatchedControl() {
    return matchedControl;
  }

  public void setMatchedControl(String matchedControl) {
    this.matchedControl = matchedControl;
  }

  public DiseaseCorrelationDto modelType(String modelType) {
    this.modelType = modelType;
    return this;
  }

  /**
   * Type of model
   * @return modelType
   */
  @NotNull 
  @Schema(name = "model_type", description = "Type of model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model_type")
  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

  public DiseaseCorrelationDto modifiedGenes(List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
    return this;
  }

  public DiseaseCorrelationDto addModifiedGenesItem(String modifiedGenesItem) {
    if (this.modifiedGenes == null) {
      this.modifiedGenes = new ArrayList<>();
    }
    this.modifiedGenes.add(modifiedGenesItem);
    return this;
  }

  /**
   * List of modified genes
   * @return modifiedGenes
   */
  @NotNull 
  @Schema(name = "modified_genes", description = "List of modified genes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modified_genes")
  public List<String> getModifiedGenes() {
    return modifiedGenes;
  }

  public void setModifiedGenes(List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
  }

  public DiseaseCorrelationDto cluster(String cluster) {
    this.cluster = cluster;
    return this;
  }

  /**
   * Cluster information
   * @return cluster
   */
  @NotNull 
  @Schema(name = "cluster", description = "Cluster information", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cluster")
  public String getCluster() {
    return cluster;
  }

  public void setCluster(String cluster) {
    this.cluster = cluster;
  }

  public DiseaseCorrelationDto age(String age) {
    this.age = age;
    return this;
  }

  /**
   * Age of the model
   * @return age
   */
  @NotNull 
  @Schema(name = "age", description = "Age of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("age")
  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public DiseaseCorrelationDto sex(SexDto sex) {
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

  public DiseaseCorrelationDto IFG(@Nullable CorrelationResultDto IFG) {
    this.IFG = IFG;
    return this;
  }

  /**
   * Get IFG
   * @return IFG
   */
  @Valid 
  @Schema(name = "IFG", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("IFG")
  public @Nullable CorrelationResultDto getIFG() {
    return IFG;
  }

  public void setIFG(@Nullable CorrelationResultDto IFG) {
    this.IFG = IFG;
  }

  public DiseaseCorrelationDto PHG(@Nullable CorrelationResultDto PHG) {
    this.PHG = PHG;
    return this;
  }

  /**
   * Get PHG
   * @return PHG
   */
  @Valid 
  @Schema(name = "PHG", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("PHG")
  public @Nullable CorrelationResultDto getPHG() {
    return PHG;
  }

  public void setPHG(@Nullable CorrelationResultDto PHG) {
    this.PHG = PHG;
  }

  public DiseaseCorrelationDto TCX(@Nullable CorrelationResultDto TCX) {
    this.TCX = TCX;
    return this;
  }

  /**
   * Get TCX
   * @return TCX
   */
  @Valid 
  @Schema(name = "TCX", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("TCX")
  public @Nullable CorrelationResultDto getTCX() {
    return TCX;
  }

  public void setTCX(@Nullable CorrelationResultDto TCX) {
    this.TCX = TCX;
  }

  public DiseaseCorrelationDto CBE(@Nullable CorrelationResultDto CBE) {
    this.CBE = CBE;
    return this;
  }

  /**
   * Get CBE
   * @return CBE
   */
  @Valid 
  @Schema(name = "CBE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("CBE")
  public @Nullable CorrelationResultDto getCBE() {
    return CBE;
  }

  public void setCBE(@Nullable CorrelationResultDto CBE) {
    this.CBE = CBE;
  }

  public DiseaseCorrelationDto DLPFC(@Nullable CorrelationResultDto DLPFC) {
    this.DLPFC = DLPFC;
    return this;
  }

  /**
   * Get DLPFC
   * @return DLPFC
   */
  @Valid 
  @Schema(name = "DLPFC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("DLPFC")
  public @Nullable CorrelationResultDto getDLPFC() {
    return DLPFC;
  }

  public void setDLPFC(@Nullable CorrelationResultDto DLPFC) {
    this.DLPFC = DLPFC;
  }

  public DiseaseCorrelationDto FP(@Nullable CorrelationResultDto FP) {
    this.FP = FP;
    return this;
  }

  /**
   * Get FP
   * @return FP
   */
  @Valid 
  @Schema(name = "FP", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("FP")
  public @Nullable CorrelationResultDto getFP() {
    return FP;
  }

  public void setFP(@Nullable CorrelationResultDto FP) {
    this.FP = FP;
  }

  public DiseaseCorrelationDto STG(@Nullable CorrelationResultDto STG) {
    this.STG = STG;
    return this;
  }

  /**
   * Get STG
   * @return STG
   */
  @Valid 
  @Schema(name = "STG", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("STG")
  public @Nullable CorrelationResultDto getSTG() {
    return STG;
  }

  public void setSTG(@Nullable CorrelationResultDto STG) {
    this.STG = STG;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DiseaseCorrelationDto diseaseCorrelation = (DiseaseCorrelationDto) o;
    return Objects.equals(this.id, diseaseCorrelation.id) &&
        Objects.equals(this.name, diseaseCorrelation.name) &&
        Objects.equals(this.matchedControl, diseaseCorrelation.matchedControl) &&
        Objects.equals(this.modelType, diseaseCorrelation.modelType) &&
        Objects.equals(this.modifiedGenes, diseaseCorrelation.modifiedGenes) &&
        Objects.equals(this.cluster, diseaseCorrelation.cluster) &&
        Objects.equals(this.age, diseaseCorrelation.age) &&
        Objects.equals(this.sex, diseaseCorrelation.sex) &&
        Objects.equals(this.IFG, diseaseCorrelation.IFG) &&
        Objects.equals(this.PHG, diseaseCorrelation.PHG) &&
        Objects.equals(this.TCX, diseaseCorrelation.TCX) &&
        Objects.equals(this.CBE, diseaseCorrelation.CBE) &&
        Objects.equals(this.DLPFC, diseaseCorrelation.DLPFC) &&
        Objects.equals(this.FP, diseaseCorrelation.FP) &&
        Objects.equals(this.STG, diseaseCorrelation.STG);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, matchedControl, modelType, modifiedGenes, cluster, age, sex, IFG, PHG, TCX, CBE, DLPFC, FP, STG);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DiseaseCorrelationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    matchedControl: ").append(toIndentedString(matchedControl)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    modifiedGenes: ").append(toIndentedString(modifiedGenes)).append("\n");
    sb.append("    cluster: ").append(toIndentedString(cluster)).append("\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
    sb.append("    sex: ").append(toIndentedString(sex)).append("\n");
    sb.append("    IFG: ").append(toIndentedString(IFG)).append("\n");
    sb.append("    PHG: ").append(toIndentedString(PHG)).append("\n");
    sb.append("    TCX: ").append(toIndentedString(TCX)).append("\n");
    sb.append("    CBE: ").append(toIndentedString(CBE)).append("\n");
    sb.append("    DLPFC: ").append(toIndentedString(DLPFC)).append("\n");
    sb.append("    FP: ").append(toIndentedString(FP)).append("\n");
    sb.append("    STG: ").append(toIndentedString(STG)).append("\n");
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

    private DiseaseCorrelationDto instance;

    public Builder() {
      this(new DiseaseCorrelationDto());
    }

    protected Builder(DiseaseCorrelationDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DiseaseCorrelationDto value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setMatchedControl(value.matchedControl);
      this.instance.setModelType(value.modelType);
      this.instance.setModifiedGenes(value.modifiedGenes);
      this.instance.setCluster(value.cluster);
      this.instance.setAge(value.age);
      this.instance.setSex(value.sex);
      this.instance.setIFG(value.IFG);
      this.instance.setPHG(value.PHG);
      this.instance.setTCX(value.TCX);
      this.instance.setCBE(value.CBE);
      this.instance.setDLPFC(value.DLPFC);
      this.instance.setFP(value.FP);
      this.instance.setSTG(value.STG);
      return this;
    }

    public DiseaseCorrelationDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder matchedControl(String matchedControl) {
      this.instance.matchedControl(matchedControl);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder modelType(String modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder modifiedGenes(List<String> modifiedGenes) {
      this.instance.modifiedGenes(modifiedGenes);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder cluster(String cluster) {
      this.instance.cluster(cluster);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder age(String age) {
      this.instance.age(age);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder sex(SexDto sex) {
      this.instance.sex(sex);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder IFG(CorrelationResultDto IFG) {
      this.instance.IFG(IFG);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder PHG(CorrelationResultDto PHG) {
      this.instance.PHG(PHG);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder TCX(CorrelationResultDto TCX) {
      this.instance.TCX(TCX);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder CBE(CorrelationResultDto CBE) {
      this.instance.CBE(CBE);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder DLPFC(CorrelationResultDto DLPFC) {
      this.instance.DLPFC(DLPFC);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder FP(CorrelationResultDto FP) {
      this.instance.FP(FP);
      return this;
    }
    
    public DiseaseCorrelationDto.Builder STG(CorrelationResultDto STG) {
      this.instance.STG(STG);
      return this;
    }
    
    /**
    * returns a built DiseaseCorrelationDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DiseaseCorrelationDto build() {
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
  public static DiseaseCorrelationDto.Builder builder() {
    return new DiseaseCorrelationDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DiseaseCorrelationDto.Builder toBuilder() {
    DiseaseCorrelationDto.Builder builder = new DiseaseCorrelationDto.Builder();
    return builder.copyOf(this);
  }

}

