package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneticInfoDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Mouse model details
 */

@Schema(name = "MouseModel", description = "Mouse model details")
@JsonTypeName("MouseModel")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MouseModelDto implements ModelDto {

  private String type;

  private String name;

  @Valid
  private List<String> matchedControls = new ArrayList<>();

  private String modelType;

  private String contributingGroup;

  private String studySynid;

  private String rrid;

  private String jaxId;

  private String alzforumId;

  private String genotype;

  @Valid
  private List<String> aliases = new ArrayList<>();

  private String transcriptomics = null;

  private String diseaseCorrelation = null;

  private String spatialTranscriptomics = null;

  @Valid
  private List<@Valid GeneticInfoDto> geneticInfo = new ArrayList<>();

  @Valid
  private List<@Valid ModelDataDto> biomarkers = new ArrayList<>();

  @Valid
  private List<@Valid ModelDataDto> pathology = new ArrayList<>();

  public MouseModelDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MouseModelDto(String type, String name, List<String> matchedControls, String modelType, String contributingGroup, String studySynid, String rrid, String jaxId, String alzforumId, String genotype, List<String> aliases, String transcriptomics, String diseaseCorrelation, String spatialTranscriptomics, List<@Valid GeneticInfoDto> geneticInfo, List<@Valid ModelDataDto> biomarkers, List<@Valid ModelDataDto> pathology) {
    this.type = type;
    this.name = name;
    this.matchedControls = matchedControls;
    this.modelType = modelType;
    this.contributingGroup = contributingGroup;
    this.studySynid = studySynid;
    this.rrid = rrid;
    this.jaxId = jaxId;
    this.alzforumId = alzforumId;
    this.genotype = genotype;
    this.aliases = aliases;
    this.transcriptomics = transcriptomics;
    this.diseaseCorrelation = diseaseCorrelation;
    this.spatialTranscriptomics = spatialTranscriptomics;
    this.geneticInfo = geneticInfo;
    this.biomarkers = biomarkers;
    this.pathology = pathology;
  }

  public MouseModelDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Organism discriminator (always \"mouse\" for this schema)
   * @return type
   */
  @NotNull 
  @Schema(name = "type", description = "Organism discriminator (always \"mouse\" for this schema)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public MouseModelDto name(String name) {
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

  public MouseModelDto matchedControls(List<String> matchedControls) {
    this.matchedControls = matchedControls;
    return this;
  }

  public MouseModelDto addMatchedControlsItem(String matchedControlsItem) {
    if (this.matchedControls == null) {
      this.matchedControls = new ArrayList<>();
    }
    this.matchedControls.add(matchedControlsItem);
    return this;
  }

  /**
   * List of matched controls
   * @return matchedControls
   */
  @NotNull 
  @Schema(name = "matched_controls", description = "List of matched controls", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("matched_controls")
  public List<String> getMatchedControls() {
    return matchedControls;
  }

  public void setMatchedControls(List<String> matchedControls) {
    this.matchedControls = matchedControls;
  }

  public MouseModelDto modelType(String modelType) {
    this.modelType = modelType;
    return this;
  }

  /**
   * Type of the model
   * @return modelType
   */
  @NotNull 
  @Schema(name = "model_type", description = "Type of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model_type")
  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

  public MouseModelDto contributingGroup(String contributingGroup) {
    this.contributingGroup = contributingGroup;
    return this;
  }

  /**
   * Group contributing the model
   * @return contributingGroup
   */
  @NotNull 
  @Schema(name = "contributing_group", description = "Group contributing the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contributing_group")
  public String getContributingGroup() {
    return contributingGroup;
  }

  public void setContributingGroup(String contributingGroup) {
    this.contributingGroup = contributingGroup;
  }

  public MouseModelDto studySynid(String studySynid) {
    this.studySynid = studySynid;
    return this;
  }

  /**
   * Synapse ID of the study
   * @return studySynid
   */
  @NotNull 
  @Schema(name = "study_synid", description = "Synapse ID of the study", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("study_synid")
  public String getStudySynid() {
    return studySynid;
  }

  public void setStudySynid(String studySynid) {
    this.studySynid = studySynid;
  }

  public MouseModelDto rrid(String rrid) {
    this.rrid = rrid;
    return this;
  }

  /**
   * RRID of the model
   * @return rrid
   */
  @NotNull 
  @Schema(name = "rrid", description = "RRID of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rrid")
  public String getRrid() {
    return rrid;
  }

  public void setRrid(String rrid) {
    this.rrid = rrid;
  }

  public MouseModelDto jaxId(String jaxId) {
    this.jaxId = jaxId;
    return this;
  }

  /**
   * JAX ID of the model
   * @return jaxId
   */
  @NotNull 
  @Schema(name = "jax_id", description = "JAX ID of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("jax_id")
  public String getJaxId() {
    return jaxId;
  }

  public void setJaxId(String jaxId) {
    this.jaxId = jaxId;
  }

  public MouseModelDto alzforumId(String alzforumId) {
    this.alzforumId = alzforumId;
    return this;
  }

  /**
   * AlzForum ID of the model
   * @return alzforumId
   */
  @NotNull 
  @Schema(name = "alzforum_id", description = "AlzForum ID of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("alzforum_id")
  public String getAlzforumId() {
    return alzforumId;
  }

  public void setAlzforumId(String alzforumId) {
    this.alzforumId = alzforumId;
  }

  public MouseModelDto genotype(String genotype) {
    this.genotype = genotype;
    return this;
  }

  /**
   * Genotype of the model
   * @return genotype
   */
  @NotNull 
  @Schema(name = "genotype", description = "Genotype of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("genotype")
  public String getGenotype() {
    return genotype;
  }

  public void setGenotype(String genotype) {
    this.genotype = genotype;
  }

  public MouseModelDto aliases(List<String> aliases) {
    this.aliases = aliases;
    return this;
  }

  public MouseModelDto addAliasesItem(String aliasesItem) {
    if (this.aliases == null) {
      this.aliases = new ArrayList<>();
    }
    this.aliases.add(aliasesItem);
    return this;
  }

  /**
   * List of aliases for the model
   * @return aliases
   */
  @NotNull 
  @Schema(name = "aliases", description = "List of aliases for the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("aliases")
  public List<String> getAliases() {
    return aliases;
  }

  public void setAliases(List<String> aliases) {
    this.aliases = aliases;
  }

  public MouseModelDto transcriptomics(String transcriptomics) {
    this.transcriptomics = transcriptomics;
    return this;
  }

  /**
   * Link to transcriptomics comparison tool data
   * @return transcriptomics
   */
  @NotNull 
  @Schema(name = "transcriptomics", description = "Link to transcriptomics comparison tool data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("transcriptomics")
  public String getTranscriptomics() {
    return transcriptomics;
  }

  public void setTranscriptomics(String transcriptomics) {
    this.transcriptomics = transcriptomics;
  }

  public MouseModelDto diseaseCorrelation(String diseaseCorrelation) {
    this.diseaseCorrelation = diseaseCorrelation;
    return this;
  }

  /**
   * Link to disease correlation comparison tool data
   * @return diseaseCorrelation
   */
  @NotNull 
  @Schema(name = "disease_correlation", description = "Link to disease correlation comparison tool data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("disease_correlation")
  public String getDiseaseCorrelation() {
    return diseaseCorrelation;
  }

  public void setDiseaseCorrelation(String diseaseCorrelation) {
    this.diseaseCorrelation = diseaseCorrelation;
  }

  public MouseModelDto spatialTranscriptomics(String spatialTranscriptomics) {
    this.spatialTranscriptomics = spatialTranscriptomics;
    return this;
  }

  /**
   * Deprecated field for link to spatial transcriptomics data
   * @return spatialTranscriptomics
   */
  @NotNull 
  @Schema(name = "spatial_transcriptomics", description = "Deprecated field for link to spatial transcriptomics data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("spatial_transcriptomics")
  public String getSpatialTranscriptomics() {
    return spatialTranscriptomics;
  }

  public void setSpatialTranscriptomics(String spatialTranscriptomics) {
    this.spatialTranscriptomics = spatialTranscriptomics;
  }

  public MouseModelDto geneticInfo(List<@Valid GeneticInfoDto> geneticInfo) {
    this.geneticInfo = geneticInfo;
    return this;
  }

  public MouseModelDto addGeneticInfoItem(GeneticInfoDto geneticInfoItem) {
    if (this.geneticInfo == null) {
      this.geneticInfo = new ArrayList<>();
    }
    this.geneticInfo.add(geneticInfoItem);
    return this;
  }

  /**
   * Genetic information related to the Model
   * @return geneticInfo
   */
  @NotNull @Valid 
  @Schema(name = "genetic_info", description = "Genetic information related to the Model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("genetic_info")
  public List<@Valid GeneticInfoDto> getGeneticInfo() {
    return geneticInfo;
  }

  public void setGeneticInfo(List<@Valid GeneticInfoDto> geneticInfo) {
    this.geneticInfo = geneticInfo;
  }

  public MouseModelDto biomarkers(List<@Valid ModelDataDto> biomarkers) {
    this.biomarkers = biomarkers;
    return this;
  }

  public MouseModelDto addBiomarkersItem(ModelDataDto biomarkersItem) {
    if (this.biomarkers == null) {
      this.biomarkers = new ArrayList<>();
    }
    this.biomarkers.add(biomarkersItem);
    return this;
  }

  /**
   * List of biomarker data associated with the model
   * @return biomarkers
   */
  @NotNull @Valid 
  @Schema(name = "biomarkers", description = "List of biomarker data associated with the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("biomarkers")
  public List<@Valid ModelDataDto> getBiomarkers() {
    return biomarkers;
  }

  public void setBiomarkers(List<@Valid ModelDataDto> biomarkers) {
    this.biomarkers = biomarkers;
  }

  public MouseModelDto pathology(List<@Valid ModelDataDto> pathology) {
    this.pathology = pathology;
    return this;
  }

  public MouseModelDto addPathologyItem(ModelDataDto pathologyItem) {
    if (this.pathology == null) {
      this.pathology = new ArrayList<>();
    }
    this.pathology.add(pathologyItem);
    return this;
  }

  /**
   * List of pathology data associated with the model
   * @return pathology
   */
  @NotNull @Valid 
  @Schema(name = "pathology", description = "List of pathology data associated with the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pathology")
  public List<@Valid ModelDataDto> getPathology() {
    return pathology;
  }

  public void setPathology(List<@Valid ModelDataDto> pathology) {
    this.pathology = pathology;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MouseModelDto mouseModel = (MouseModelDto) o;
    return Objects.equals(this.type, mouseModel.type) &&
        Objects.equals(this.name, mouseModel.name) &&
        Objects.equals(this.matchedControls, mouseModel.matchedControls) &&
        Objects.equals(this.modelType, mouseModel.modelType) &&
        Objects.equals(this.contributingGroup, mouseModel.contributingGroup) &&
        Objects.equals(this.studySynid, mouseModel.studySynid) &&
        Objects.equals(this.rrid, mouseModel.rrid) &&
        Objects.equals(this.jaxId, mouseModel.jaxId) &&
        Objects.equals(this.alzforumId, mouseModel.alzforumId) &&
        Objects.equals(this.genotype, mouseModel.genotype) &&
        Objects.equals(this.aliases, mouseModel.aliases) &&
        Objects.equals(this.transcriptomics, mouseModel.transcriptomics) &&
        Objects.equals(this.diseaseCorrelation, mouseModel.diseaseCorrelation) &&
        Objects.equals(this.spatialTranscriptomics, mouseModel.spatialTranscriptomics) &&
        Objects.equals(this.geneticInfo, mouseModel.geneticInfo) &&
        Objects.equals(this.biomarkers, mouseModel.biomarkers) &&
        Objects.equals(this.pathology, mouseModel.pathology);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, matchedControls, modelType, contributingGroup, studySynid, rrid, jaxId, alzforumId, genotype, aliases, transcriptomics, diseaseCorrelation, spatialTranscriptomics, geneticInfo, biomarkers, pathology);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MouseModelDto {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    matchedControls: ").append(toIndentedString(matchedControls)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    contributingGroup: ").append(toIndentedString(contributingGroup)).append("\n");
    sb.append("    studySynid: ").append(toIndentedString(studySynid)).append("\n");
    sb.append("    rrid: ").append(toIndentedString(rrid)).append("\n");
    sb.append("    jaxId: ").append(toIndentedString(jaxId)).append("\n");
    sb.append("    alzforumId: ").append(toIndentedString(alzforumId)).append("\n");
    sb.append("    genotype: ").append(toIndentedString(genotype)).append("\n");
    sb.append("    aliases: ").append(toIndentedString(aliases)).append("\n");
    sb.append("    transcriptomics: ").append(toIndentedString(transcriptomics)).append("\n");
    sb.append("    diseaseCorrelation: ").append(toIndentedString(diseaseCorrelation)).append("\n");
    sb.append("    spatialTranscriptomics: ").append(toIndentedString(spatialTranscriptomics)).append("\n");
    sb.append("    geneticInfo: ").append(toIndentedString(geneticInfo)).append("\n");
    sb.append("    biomarkers: ").append(toIndentedString(biomarkers)).append("\n");
    sb.append("    pathology: ").append(toIndentedString(pathology)).append("\n");
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

    private MouseModelDto instance;

    public Builder() {
      this(new MouseModelDto());
    }

    protected Builder(MouseModelDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MouseModelDto value) { 
      this.instance.setType(value.type);
      this.instance.setName(value.name);
      this.instance.setMatchedControls(value.matchedControls);
      this.instance.setModelType(value.modelType);
      this.instance.setContributingGroup(value.contributingGroup);
      this.instance.setStudySynid(value.studySynid);
      this.instance.setRrid(value.rrid);
      this.instance.setJaxId(value.jaxId);
      this.instance.setAlzforumId(value.alzforumId);
      this.instance.setGenotype(value.genotype);
      this.instance.setAliases(value.aliases);
      this.instance.setTranscriptomics(value.transcriptomics);
      this.instance.setDiseaseCorrelation(value.diseaseCorrelation);
      this.instance.setSpatialTranscriptomics(value.spatialTranscriptomics);
      this.instance.setGeneticInfo(value.geneticInfo);
      this.instance.setBiomarkers(value.biomarkers);
      this.instance.setPathology(value.pathology);
      return this;
    }

    public MouseModelDto.Builder type(String type) {
      this.instance.type(type);
      return this;
    }
    
    public MouseModelDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public MouseModelDto.Builder matchedControls(List<String> matchedControls) {
      this.instance.matchedControls(matchedControls);
      return this;
    }
    
    public MouseModelDto.Builder modelType(String modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public MouseModelDto.Builder contributingGroup(String contributingGroup) {
      this.instance.contributingGroup(contributingGroup);
      return this;
    }
    
    public MouseModelDto.Builder studySynid(String studySynid) {
      this.instance.studySynid(studySynid);
      return this;
    }
    
    public MouseModelDto.Builder rrid(String rrid) {
      this.instance.rrid(rrid);
      return this;
    }
    
    public MouseModelDto.Builder jaxId(String jaxId) {
      this.instance.jaxId(jaxId);
      return this;
    }
    
    public MouseModelDto.Builder alzforumId(String alzforumId) {
      this.instance.alzforumId(alzforumId);
      return this;
    }
    
    public MouseModelDto.Builder genotype(String genotype) {
      this.instance.genotype(genotype);
      return this;
    }
    
    public MouseModelDto.Builder aliases(List<String> aliases) {
      this.instance.aliases(aliases);
      return this;
    }
    
    public MouseModelDto.Builder transcriptomics(String transcriptomics) {
      this.instance.transcriptomics(transcriptomics);
      return this;
    }
    
    public MouseModelDto.Builder diseaseCorrelation(String diseaseCorrelation) {
      this.instance.diseaseCorrelation(diseaseCorrelation);
      return this;
    }
    
    public MouseModelDto.Builder spatialTranscriptomics(String spatialTranscriptomics) {
      this.instance.spatialTranscriptomics(spatialTranscriptomics);
      return this;
    }
    
    public MouseModelDto.Builder geneticInfo(List<GeneticInfoDto> geneticInfo) {
      this.instance.geneticInfo(geneticInfo);
      return this;
    }
    
    public MouseModelDto.Builder biomarkers(List<ModelDataDto> biomarkers) {
      this.instance.biomarkers(biomarkers);
      return this;
    }
    
    public MouseModelDto.Builder pathology(List<ModelDataDto> pathology) {
      this.instance.pathology(pathology);
      return this;
    }
    
    /**
    * returns a built MouseModelDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MouseModelDto build() {
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
  public static MouseModelDto.Builder builder() {
    return new MouseModelDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MouseModelDto.Builder toBuilder() {
    MouseModelDto.Builder builder = new MouseModelDto.Builder();
    return builder.copyOf(this);
  }

}

