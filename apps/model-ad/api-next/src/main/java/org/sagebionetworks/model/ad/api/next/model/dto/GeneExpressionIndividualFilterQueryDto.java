package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelIdentifierTypeDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Gene expression individual filter query options
 */

@Schema(name = "GeneExpressionIndividualFilterQuery", description = "Gene expression individual filter query options")
@JsonTypeName("GeneExpressionIndividualFilterQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GeneExpressionIndividualFilterQueryDto {

  private String tissue;

  private String modelIdentifier;

  private ModelIdentifierTypeDto modelIdentifierType;

  private String ensemblGeneId;

  public GeneExpressionIndividualFilterQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneExpressionIndividualFilterQueryDto(String tissue, String modelIdentifier, ModelIdentifierTypeDto modelIdentifierType, String ensemblGeneId) {
    this.tissue = tissue;
    this.modelIdentifier = modelIdentifier;
    this.modelIdentifierType = modelIdentifierType;
    this.ensemblGeneId = ensemblGeneId;
  }

  public GeneExpressionIndividualFilterQueryDto tissue(String tissue) {
    this.tissue = tissue;
    return this;
  }

  /**
   * Tissue type to filter by
   * @return tissue
   */
  @NotNull 
  @Schema(name = "tissue", example = "Hippocampus", description = "Tissue type to filter by", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tissue")
  public String getTissue() {
    return tissue;
  }

  public void setTissue(String tissue) {
    this.tissue = tissue;
  }

  public GeneExpressionIndividualFilterQueryDto modelIdentifier(String modelIdentifier) {
    this.modelIdentifier = modelIdentifier;
    return this;
  }

  /**
   * The model name or model group to filter by
   * @return modelIdentifier
   */
  @NotNull 
  @Schema(name = "modelIdentifier", example = "5xFAD (Jax/IU/Pitt)", description = "The model name or model group to filter by", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelIdentifier")
  public String getModelIdentifier() {
    return modelIdentifier;
  }

  public void setModelIdentifier(String modelIdentifier) {
    this.modelIdentifier = modelIdentifier;
  }

  public GeneExpressionIndividualFilterQueryDto modelIdentifierType(ModelIdentifierTypeDto modelIdentifierType) {
    this.modelIdentifierType = modelIdentifierType;
    return this;
  }

  /**
   * Get modelIdentifierType
   * @return modelIdentifierType
   */
  @NotNull @Valid 
  @Schema(name = "modelIdentifierType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelIdentifierType")
  public ModelIdentifierTypeDto getModelIdentifierType() {
    return modelIdentifierType;
  }

  public void setModelIdentifierType(ModelIdentifierTypeDto modelIdentifierType) {
    this.modelIdentifierType = modelIdentifierType;
  }

  public GeneExpressionIndividualFilterQueryDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Ensembl Gene ID to filter by
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensemblGeneId", example = "ENSMUSG00000000001", description = "Ensembl Gene ID to filter by", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensemblGeneId")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeneExpressionIndividualFilterQueryDto geneExpressionIndividualFilterQuery = (GeneExpressionIndividualFilterQueryDto) o;
    return Objects.equals(this.tissue, geneExpressionIndividualFilterQuery.tissue) &&
        Objects.equals(this.modelIdentifier, geneExpressionIndividualFilterQuery.modelIdentifier) &&
        Objects.equals(this.modelIdentifierType, geneExpressionIndividualFilterQuery.modelIdentifierType) &&
        Objects.equals(this.ensemblGeneId, geneExpressionIndividualFilterQuery.ensemblGeneId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tissue, modelIdentifier, modelIdentifierType, ensemblGeneId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneExpressionIndividualFilterQueryDto {\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    modelIdentifier: ").append(toIndentedString(modelIdentifier)).append("\n");
    sb.append("    modelIdentifierType: ").append(toIndentedString(modelIdentifierType)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
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

    private GeneExpressionIndividualFilterQueryDto instance;

    public Builder() {
      this(new GeneExpressionIndividualFilterQueryDto());
    }

    protected Builder(GeneExpressionIndividualFilterQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneExpressionIndividualFilterQueryDto value) { 
      this.instance.setTissue(value.tissue);
      this.instance.setModelIdentifier(value.modelIdentifier);
      this.instance.setModelIdentifierType(value.modelIdentifierType);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      return this;
    }

    public GeneExpressionIndividualFilterQueryDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    public GeneExpressionIndividualFilterQueryDto.Builder modelIdentifier(String modelIdentifier) {
      this.instance.modelIdentifier(modelIdentifier);
      return this;
    }
    
    public GeneExpressionIndividualFilterQueryDto.Builder modelIdentifierType(ModelIdentifierTypeDto modelIdentifierType) {
      this.instance.modelIdentifierType(modelIdentifierType);
      return this;
    }
    
    public GeneExpressionIndividualFilterQueryDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    /**
    * returns a built GeneExpressionIndividualFilterQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GeneExpressionIndividualFilterQueryDto build() {
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
  public static GeneExpressionIndividualFilterQueryDto.Builder builder() {
    return new GeneExpressionIndividualFilterQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GeneExpressionIndividualFilterQueryDto.Builder toBuilder() {
    GeneExpressionIndividualFilterQueryDto.Builder builder = new GeneExpressionIndividualFilterQueryDto.Builder();
    return builder.copyOf(this);
  }

}

