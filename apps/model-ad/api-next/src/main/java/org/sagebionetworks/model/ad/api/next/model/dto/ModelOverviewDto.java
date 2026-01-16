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
import org.sagebionetworks.model.ad.api.next.model.dto.LinkDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Model Overview object for comparison tools
 */

@Schema(name = "ModelOverview", description = "Model Overview object for comparison tools")
@JsonTypeName("ModelOverview")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelOverviewDto {

  private String id;

  private String name;

  private String modelType;

  @Valid
  private List<String> matchedControls = new ArrayList<>();

  private @Nullable LinkDto geneExpression;

  private @Nullable LinkDto diseaseCorrelation;

  private @Nullable LinkDto biomarkers;

  private @Nullable LinkDto pathology;

  private LinkDto studyData;

  private LinkDto jaxStrain;

  private LinkDto center;

  @Valid
  private List<String> modifiedGenes = new ArrayList<>();

  /**
   * Gets or Sets availableData
   */
  public enum AvailableDataEnum {
    GENE_EXPRESSION("Gene Expression"),
    
    PATHOLOGY("Pathology"),
    
    BIOMARKERS("Biomarkers"),
    
    DISEASE_CORRELATION("Disease Correlation");

    private final String value;

    AvailableDataEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AvailableDataEnum fromValue(String value) {
      for (AvailableDataEnum b : AvailableDataEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Valid
  private List<AvailableDataEnum> availableData = new ArrayList<>();

  public ModelOverviewDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelOverviewDto(String id, String name, String modelType, List<String> matchedControls, LinkDto studyData, LinkDto jaxStrain, LinkDto center, List<String> modifiedGenes, List<AvailableDataEnum> availableData) {
    this.id = id;
    this.name = name;
    this.modelType = modelType;
    this.matchedControls = matchedControls;
    this.studyData = studyData;
    this.jaxStrain = jaxStrain;
    this.center = center;
    this.modifiedGenes = modifiedGenes;
    this.availableData = availableData;
  }

  public ModelOverviewDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the model overview object
   * @return id
   */
  @NotNull 
  @Schema(name = "_id", description = "Unique identifier for the model overview object", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ModelOverviewDto name(String name) {
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

  public ModelOverviewDto modelType(String modelType) {
    this.modelType = modelType;
    return this;
  }

  /**
   * Model type (e.g., Late Onset AD, Familial AD)
   * @return modelType
   */
  @NotNull 
  @Schema(name = "model_type", description = "Model type (e.g., Late Onset AD, Familial AD)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model_type")
  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

  public ModelOverviewDto matchedControls(List<String> matchedControls) {
    this.matchedControls = matchedControls;
    return this;
  }

  public ModelOverviewDto addMatchedControlsItem(String matchedControlsItem) {
    if (this.matchedControls == null) {
      this.matchedControls = new ArrayList<>();
    }
    this.matchedControls.add(matchedControlsItem);
    return this;
  }

  /**
   * List of matched control models
   * @return matchedControls
   */
  @NotNull 
  @Schema(name = "matched_controls", description = "List of matched control models", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("matched_controls")
  public List<String> getMatchedControls() {
    return matchedControls;
  }

  public void setMatchedControls(List<String> matchedControls) {
    this.matchedControls = matchedControls;
  }

  public ModelOverviewDto geneExpression(@Nullable LinkDto geneExpression) {
    this.geneExpression = geneExpression;
    return this;
  }

  /**
   * Get geneExpression
   * @return geneExpression
   */
  @Valid 
  @Schema(name = "gene_expression", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("gene_expression")
  public @Nullable LinkDto getGeneExpression() {
    return geneExpression;
  }

  public void setGeneExpression(@Nullable LinkDto geneExpression) {
    this.geneExpression = geneExpression;
  }

  public ModelOverviewDto diseaseCorrelation(@Nullable LinkDto diseaseCorrelation) {
    this.diseaseCorrelation = diseaseCorrelation;
    return this;
  }

  /**
   * Get diseaseCorrelation
   * @return diseaseCorrelation
   */
  @Valid 
  @Schema(name = "disease_correlation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("disease_correlation")
  public @Nullable LinkDto getDiseaseCorrelation() {
    return diseaseCorrelation;
  }

  public void setDiseaseCorrelation(@Nullable LinkDto diseaseCorrelation) {
    this.diseaseCorrelation = diseaseCorrelation;
  }

  public ModelOverviewDto biomarkers(@Nullable LinkDto biomarkers) {
    this.biomarkers = biomarkers;
    return this;
  }

  /**
   * Get biomarkers
   * @return biomarkers
   */
  @Valid 
  @Schema(name = "biomarkers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("biomarkers")
  public @Nullable LinkDto getBiomarkers() {
    return biomarkers;
  }

  public void setBiomarkers(@Nullable LinkDto biomarkers) {
    this.biomarkers = biomarkers;
  }

  public ModelOverviewDto pathology(@Nullable LinkDto pathology) {
    this.pathology = pathology;
    return this;
  }

  /**
   * Get pathology
   * @return pathology
   */
  @Valid 
  @Schema(name = "pathology", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pathology")
  public @Nullable LinkDto getPathology() {
    return pathology;
  }

  public void setPathology(@Nullable LinkDto pathology) {
    this.pathology = pathology;
  }

  public ModelOverviewDto studyData(LinkDto studyData) {
    this.studyData = studyData;
    return this;
  }

  /**
   * Get studyData
   * @return studyData
   */
  @NotNull @Valid 
  @Schema(name = "study_data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("study_data")
  public LinkDto getStudyData() {
    return studyData;
  }

  public void setStudyData(LinkDto studyData) {
    this.studyData = studyData;
  }

  public ModelOverviewDto jaxStrain(LinkDto jaxStrain) {
    this.jaxStrain = jaxStrain;
    return this;
  }

  /**
   * Get jaxStrain
   * @return jaxStrain
   */
  @NotNull @Valid 
  @Schema(name = "jax_strain", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("jax_strain")
  public LinkDto getJaxStrain() {
    return jaxStrain;
  }

  public void setJaxStrain(LinkDto jaxStrain) {
    this.jaxStrain = jaxStrain;
  }

  public ModelOverviewDto center(LinkDto center) {
    this.center = center;
    return this;
  }

  /**
   * Get center
   * @return center
   */
  @NotNull @Valid 
  @Schema(name = "center", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("center")
  public LinkDto getCenter() {
    return center;
  }

  public void setCenter(LinkDto center) {
    this.center = center;
  }

  public ModelOverviewDto modifiedGenes(List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
    return this;
  }

  public ModelOverviewDto addModifiedGenesItem(String modifiedGenesItem) {
    if (this.modifiedGenes == null) {
      this.modifiedGenes = new ArrayList<>();
    }
    this.modifiedGenes.add(modifiedGenesItem);
    return this;
  }

  /**
   * List of modified genes in the model
   * @return modifiedGenes
   */
  @NotNull 
  @Schema(name = "modified_genes", description = "List of modified genes in the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modified_genes")
  public List<String> getModifiedGenes() {
    return modifiedGenes;
  }

  public void setModifiedGenes(List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
  }

  public ModelOverviewDto availableData(List<AvailableDataEnum> availableData) {
    this.availableData = availableData;
    return this;
  }

  public ModelOverviewDto addAvailableDataItem(AvailableDataEnum availableDataItem) {
    if (this.availableData == null) {
      this.availableData = new ArrayList<>();
    }
    this.availableData.add(availableDataItem);
    return this;
  }

  /**
   * Get availableData
   * @return availableData
   */
  @NotNull 
  @Schema(name = "available_data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("available_data")
  public List<AvailableDataEnum> getAvailableData() {
    return availableData;
  }

  public void setAvailableData(List<AvailableDataEnum> availableData) {
    this.availableData = availableData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelOverviewDto modelOverview = (ModelOverviewDto) o;
    return Objects.equals(this.id, modelOverview.id) &&
        Objects.equals(this.name, modelOverview.name) &&
        Objects.equals(this.modelType, modelOverview.modelType) &&
        Objects.equals(this.matchedControls, modelOverview.matchedControls) &&
        Objects.equals(this.geneExpression, modelOverview.geneExpression) &&
        Objects.equals(this.diseaseCorrelation, modelOverview.diseaseCorrelation) &&
        Objects.equals(this.biomarkers, modelOverview.biomarkers) &&
        Objects.equals(this.pathology, modelOverview.pathology) &&
        Objects.equals(this.studyData, modelOverview.studyData) &&
        Objects.equals(this.jaxStrain, modelOverview.jaxStrain) &&
        Objects.equals(this.center, modelOverview.center) &&
        Objects.equals(this.modifiedGenes, modelOverview.modifiedGenes) &&
        Objects.equals(this.availableData, modelOverview.availableData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, modelType, matchedControls, geneExpression, diseaseCorrelation, biomarkers, pathology, studyData, jaxStrain, center, modifiedGenes, availableData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelOverviewDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    matchedControls: ").append(toIndentedString(matchedControls)).append("\n");
    sb.append("    geneExpression: ").append(toIndentedString(geneExpression)).append("\n");
    sb.append("    diseaseCorrelation: ").append(toIndentedString(diseaseCorrelation)).append("\n");
    sb.append("    biomarkers: ").append(toIndentedString(biomarkers)).append("\n");
    sb.append("    pathology: ").append(toIndentedString(pathology)).append("\n");
    sb.append("    studyData: ").append(toIndentedString(studyData)).append("\n");
    sb.append("    jaxStrain: ").append(toIndentedString(jaxStrain)).append("\n");
    sb.append("    center: ").append(toIndentedString(center)).append("\n");
    sb.append("    modifiedGenes: ").append(toIndentedString(modifiedGenes)).append("\n");
    sb.append("    availableData: ").append(toIndentedString(availableData)).append("\n");
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

    private ModelOverviewDto instance;

    public Builder() {
      this(new ModelOverviewDto());
    }

    protected Builder(ModelOverviewDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelOverviewDto value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setModelType(value.modelType);
      this.instance.setMatchedControls(value.matchedControls);
      this.instance.setGeneExpression(value.geneExpression);
      this.instance.setDiseaseCorrelation(value.diseaseCorrelation);
      this.instance.setBiomarkers(value.biomarkers);
      this.instance.setPathology(value.pathology);
      this.instance.setStudyData(value.studyData);
      this.instance.setJaxStrain(value.jaxStrain);
      this.instance.setCenter(value.center);
      this.instance.setModifiedGenes(value.modifiedGenes);
      this.instance.setAvailableData(value.availableData);
      return this;
    }

    public ModelOverviewDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public ModelOverviewDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ModelOverviewDto.Builder modelType(String modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public ModelOverviewDto.Builder matchedControls(List<String> matchedControls) {
      this.instance.matchedControls(matchedControls);
      return this;
    }
    
    public ModelOverviewDto.Builder geneExpression(LinkDto geneExpression) {
      this.instance.geneExpression(geneExpression);
      return this;
    }
    
    public ModelOverviewDto.Builder diseaseCorrelation(LinkDto diseaseCorrelation) {
      this.instance.diseaseCorrelation(diseaseCorrelation);
      return this;
    }
    
    public ModelOverviewDto.Builder biomarkers(LinkDto biomarkers) {
      this.instance.biomarkers(biomarkers);
      return this;
    }
    
    public ModelOverviewDto.Builder pathology(LinkDto pathology) {
      this.instance.pathology(pathology);
      return this;
    }
    
    public ModelOverviewDto.Builder studyData(LinkDto studyData) {
      this.instance.studyData(studyData);
      return this;
    }
    
    public ModelOverviewDto.Builder jaxStrain(LinkDto jaxStrain) {
      this.instance.jaxStrain(jaxStrain);
      return this;
    }
    
    public ModelOverviewDto.Builder center(LinkDto center) {
      this.instance.center(center);
      return this;
    }
    
    public ModelOverviewDto.Builder modifiedGenes(List<String> modifiedGenes) {
      this.instance.modifiedGenes(modifiedGenes);
      return this;
    }
    
    public ModelOverviewDto.Builder availableData(List<AvailableDataEnum> availableData) {
      this.instance.availableData(availableData);
      return this;
    }
    
    /**
    * returns a built ModelOverviewDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelOverviewDto build() {
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
  public static ModelOverviewDto.Builder builder() {
    return new ModelOverviewDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelOverviewDto.Builder toBuilder() {
    ModelOverviewDto.Builder builder = new ModelOverviewDto.Builder();
    return builder.copyOf(this);
  }

}

