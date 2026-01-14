package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Gene expression details filter query options
 */

@Schema(name = "GeneExpressionDetailFilterQuery", description = "Gene expression details filter query options")
@JsonTypeName("GeneExpressionDetailFilterQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GeneExpressionDetailFilterQueryDto {

  private String tissue;

  private String modelIdentifier;

  /**
   * Specifies whether modelIdentifier is a name or modelGroup
   */
  public enum ModelIdentifierTypeEnum {
    NAME("name"),
    
    MODEL_GROUP("modelGroup");

    private final String value;

    ModelIdentifierTypeEnum(String value) {
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
    public static ModelIdentifierTypeEnum fromValue(String value) {
      for (ModelIdentifierTypeEnum b : ModelIdentifierTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ModelIdentifierTypeEnum modelIdentifierType;

  private String ensemblGeneId;

  public GeneExpressionDetailFilterQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneExpressionDetailFilterQueryDto(String tissue, String modelIdentifier, ModelIdentifierTypeEnum modelIdentifierType, String ensemblGeneId) {
    this.tissue = tissue;
    this.modelIdentifier = modelIdentifier;
    this.modelIdentifierType = modelIdentifierType;
    this.ensemblGeneId = ensemblGeneId;
  }

  public GeneExpressionDetailFilterQueryDto tissue(String tissue) {
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

  public GeneExpressionDetailFilterQueryDto modelIdentifier(String modelIdentifier) {
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

  public GeneExpressionDetailFilterQueryDto modelIdentifierType(ModelIdentifierTypeEnum modelIdentifierType) {
    this.modelIdentifierType = modelIdentifierType;
    return this;
  }

  /**
   * Specifies whether modelIdentifier is a name or modelGroup
   * @return modelIdentifierType
   */
  @NotNull 
  @Schema(name = "modelIdentifierType", example = "name", description = "Specifies whether modelIdentifier is a name or modelGroup", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelIdentifierType")
  public ModelIdentifierTypeEnum getModelIdentifierType() {
    return modelIdentifierType;
  }

  public void setModelIdentifierType(ModelIdentifierTypeEnum modelIdentifierType) {
    this.modelIdentifierType = modelIdentifierType;
  }

  public GeneExpressionDetailFilterQueryDto ensemblGeneId(String ensemblGeneId) {
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
    GeneExpressionDetailFilterQueryDto geneExpressionDetailFilterQuery = (GeneExpressionDetailFilterQueryDto) o;
    return Objects.equals(this.tissue, geneExpressionDetailFilterQuery.tissue) &&
        Objects.equals(this.modelIdentifier, geneExpressionDetailFilterQuery.modelIdentifier) &&
        Objects.equals(this.modelIdentifierType, geneExpressionDetailFilterQuery.modelIdentifierType) &&
        Objects.equals(this.ensemblGeneId, geneExpressionDetailFilterQuery.ensemblGeneId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tissue, modelIdentifier, modelIdentifierType, ensemblGeneId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneExpressionDetailFilterQueryDto {\n");
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

    private GeneExpressionDetailFilterQueryDto instance;

    public Builder() {
      this(new GeneExpressionDetailFilterQueryDto());
    }

    protected Builder(GeneExpressionDetailFilterQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneExpressionDetailFilterQueryDto value) { 
      this.instance.setTissue(value.tissue);
      this.instance.setModelIdentifier(value.modelIdentifier);
      this.instance.setModelIdentifierType(value.modelIdentifierType);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      return this;
    }

    public GeneExpressionDetailFilterQueryDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    public GeneExpressionDetailFilterQueryDto.Builder modelIdentifier(String modelIdentifier) {
      this.instance.modelIdentifier(modelIdentifier);
      return this;
    }
    
    public GeneExpressionDetailFilterQueryDto.Builder modelIdentifierType(ModelIdentifierTypeEnum modelIdentifierType) {
      this.instance.modelIdentifierType(modelIdentifierType);
      return this;
    }
    
    public GeneExpressionDetailFilterQueryDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    /**
    * returns a built GeneExpressionDetailFilterQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GeneExpressionDetailFilterQueryDto build() {
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
  public static GeneExpressionDetailFilterQueryDto.Builder builder() {
    return new GeneExpressionDetailFilterQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GeneExpressionDetailFilterQueryDto.Builder toBuilder() {
    GeneExpressionDetailFilterQueryDto.Builder builder = new GeneExpressionDetailFilterQueryDto.Builder();
    return builder.copyOf(this);
  }

}

