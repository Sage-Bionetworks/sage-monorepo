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
 * Mouse Model Overview object for comparison tools
 */

@Schema(name = "MouseModelOverview", description = "Mouse Model Overview object for comparison tools")
@JsonTypeName("MouseModelOverview")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MouseModelOverviewDto {

  private String id;

  private String name;

  private String modelType;

  @Valid
  private List<String> matchedControls = new ArrayList<>();

  private @Nullable LinkDto transcriptomics;

  private @Nullable LinkDto diseaseCorrelation;

  private @Nullable LinkDto biomarkers;

  private @Nullable LinkDto pathology;

  private LinkDto studyData;

  private LinkDto jaxStrain;

  private String center = null;

  @Valid
  private List<String> modifiedGenes = new ArrayList<>();

  /**
   * Gets or Sets availableData
   */
  public enum AvailableDataEnum {
    TRANSCRIPTOMICS("Transcriptomics"),
    
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

  public MouseModelOverviewDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MouseModelOverviewDto(String id, String name, String modelType, List<String> matchedControls, LinkDto studyData, LinkDto jaxStrain, String center, List<String> modifiedGenes, List<AvailableDataEnum> availableData) {
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

  public MouseModelOverviewDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the mouse model overview object
   * @return id
   */
  @NotNull 
  @Schema(name = "_id", description = "Unique identifier for the mouse model overview object", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MouseModelOverviewDto name(String name) {
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

  public MouseModelOverviewDto modelType(String modelType) {
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

  public MouseModelOverviewDto matchedControls(List<String> matchedControls) {
    this.matchedControls = matchedControls;
    return this;
  }

  public MouseModelOverviewDto addMatchedControlsItem(String matchedControlsItem) {
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

  public MouseModelOverviewDto transcriptomics(@Nullable LinkDto transcriptomics) {
    this.transcriptomics = transcriptomics;
    return this;
  }

  /**
   * Get transcriptomics
   * @return transcriptomics
   */
  @Valid 
  @Schema(name = "transcriptomics", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transcriptomics")
  public @Nullable LinkDto getTranscriptomics() {
    return transcriptomics;
  }

  public void setTranscriptomics(@Nullable LinkDto transcriptomics) {
    this.transcriptomics = transcriptomics;
  }

  public MouseModelOverviewDto diseaseCorrelation(@Nullable LinkDto diseaseCorrelation) {
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

  public MouseModelOverviewDto biomarkers(@Nullable LinkDto biomarkers) {
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

  public MouseModelOverviewDto pathology(@Nullable LinkDto pathology) {
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

  public MouseModelOverviewDto studyData(LinkDto studyData) {
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

  public MouseModelOverviewDto jaxStrain(LinkDto jaxStrain) {
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

  public MouseModelOverviewDto center(String center) {
    this.center = center;
    return this;
  }

  /**
   * Get center
   * @return center
   */
  @NotNull 
  @Schema(name = "center", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("center")
  public String getCenter() {
    return center;
  }

  public void setCenter(String center) {
    this.center = center;
  }

  public MouseModelOverviewDto modifiedGenes(List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
    return this;
  }

  public MouseModelOverviewDto addModifiedGenesItem(String modifiedGenesItem) {
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

  public MouseModelOverviewDto availableData(List<AvailableDataEnum> availableData) {
    this.availableData = availableData;
    return this;
  }

  public MouseModelOverviewDto addAvailableDataItem(AvailableDataEnum availableDataItem) {
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
    MouseModelOverviewDto mouseModelOverview = (MouseModelOverviewDto) o;
    return Objects.equals(this.id, mouseModelOverview.id) &&
        Objects.equals(this.name, mouseModelOverview.name) &&
        Objects.equals(this.modelType, mouseModelOverview.modelType) &&
        Objects.equals(this.matchedControls, mouseModelOverview.matchedControls) &&
        Objects.equals(this.transcriptomics, mouseModelOverview.transcriptomics) &&
        Objects.equals(this.diseaseCorrelation, mouseModelOverview.diseaseCorrelation) &&
        Objects.equals(this.biomarkers, mouseModelOverview.biomarkers) &&
        Objects.equals(this.pathology, mouseModelOverview.pathology) &&
        Objects.equals(this.studyData, mouseModelOverview.studyData) &&
        Objects.equals(this.jaxStrain, mouseModelOverview.jaxStrain) &&
        Objects.equals(this.center, mouseModelOverview.center) &&
        Objects.equals(this.modifiedGenes, mouseModelOverview.modifiedGenes) &&
        Objects.equals(this.availableData, mouseModelOverview.availableData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, modelType, matchedControls, transcriptomics, diseaseCorrelation, biomarkers, pathology, studyData, jaxStrain, center, modifiedGenes, availableData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MouseModelOverviewDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    matchedControls: ").append(toIndentedString(matchedControls)).append("\n");
    sb.append("    transcriptomics: ").append(toIndentedString(transcriptomics)).append("\n");
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

    private MouseModelOverviewDto instance;

    public Builder() {
      this(new MouseModelOverviewDto());
    }

    protected Builder(MouseModelOverviewDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MouseModelOverviewDto value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setModelType(value.modelType);
      this.instance.setMatchedControls(value.matchedControls);
      this.instance.setTranscriptomics(value.transcriptomics);
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

    public MouseModelOverviewDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public MouseModelOverviewDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public MouseModelOverviewDto.Builder modelType(String modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public MouseModelOverviewDto.Builder matchedControls(List<String> matchedControls) {
      this.instance.matchedControls(matchedControls);
      return this;
    }
    
    public MouseModelOverviewDto.Builder transcriptomics(LinkDto transcriptomics) {
      this.instance.transcriptomics(transcriptomics);
      return this;
    }
    
    public MouseModelOverviewDto.Builder diseaseCorrelation(LinkDto diseaseCorrelation) {
      this.instance.diseaseCorrelation(diseaseCorrelation);
      return this;
    }
    
    public MouseModelOverviewDto.Builder biomarkers(LinkDto biomarkers) {
      this.instance.biomarkers(biomarkers);
      return this;
    }
    
    public MouseModelOverviewDto.Builder pathology(LinkDto pathology) {
      this.instance.pathology(pathology);
      return this;
    }
    
    public MouseModelOverviewDto.Builder studyData(LinkDto studyData) {
      this.instance.studyData(studyData);
      return this;
    }
    
    public MouseModelOverviewDto.Builder jaxStrain(LinkDto jaxStrain) {
      this.instance.jaxStrain(jaxStrain);
      return this;
    }
    
    public MouseModelOverviewDto.Builder center(String center) {
      this.instance.center(center);
      return this;
    }
    
    public MouseModelOverviewDto.Builder modifiedGenes(List<String> modifiedGenes) {
      this.instance.modifiedGenes(modifiedGenes);
      return this;
    }
    
    public MouseModelOverviewDto.Builder availableData(List<AvailableDataEnum> availableData) {
      this.instance.availableData(availableData);
      return this;
    }
    
    /**
    * returns a built MouseModelOverviewDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MouseModelOverviewDto build() {
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
  public static MouseModelOverviewDto.Builder builder() {
    return new MouseModelOverviewDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MouseModelOverviewDto.Builder toBuilder() {
    MouseModelOverviewDto.Builder builder = new MouseModelOverviewDto.Builder();
    return builder.copyOf(this);
  }

}

