package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Gene expression individual search query filter options
 */

@Schema(name = "GeneExpressionIndividualSearchQuery", description = "Gene expression individual search query filter options")
@JsonTypeName("GeneExpressionIndividualSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GeneExpressionIndividualSearchQueryDto {

  private String tissue;

  private String name;

  private @Nullable String modelGroup = null;

  private String ensemblGeneId;

  public GeneExpressionIndividualSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneExpressionIndividualSearchQueryDto(String tissue, String name, String ensemblGeneId) {
    this.tissue = tissue;
    this.name = name;
    this.ensemblGeneId = ensemblGeneId;
  }

  public GeneExpressionIndividualSearchQueryDto tissue(String tissue) {
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

  public GeneExpressionIndividualSearchQueryDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Model name to filter by. Used when modelGroup is null.
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "5xFAD (Jax/IU/Pitt)", description = "Model name to filter by. Used when modelGroup is null.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GeneExpressionIndividualSearchQueryDto modelGroup(@Nullable String modelGroup) {
    this.modelGroup = modelGroup;
    return this;
  }

  /**
   * Model group to filter by. Overrides name if both are provided.
   * @return modelGroup
   */
  
  @Schema(name = "modelGroup", example = "5xFAD (IU/Jax/Pitt)", description = "Model group to filter by. Overrides name if both are provided.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modelGroup")
  public @Nullable String getModelGroup() {
    return modelGroup;
  }

  public void setModelGroup(@Nullable String modelGroup) {
    this.modelGroup = modelGroup;
  }

  public GeneExpressionIndividualSearchQueryDto ensemblGeneId(String ensemblGeneId) {
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
    GeneExpressionIndividualSearchQueryDto geneExpressionIndividualSearchQuery = (GeneExpressionIndividualSearchQueryDto) o;
    return Objects.equals(this.tissue, geneExpressionIndividualSearchQuery.tissue) &&
        Objects.equals(this.name, geneExpressionIndividualSearchQuery.name) &&
        Objects.equals(this.modelGroup, geneExpressionIndividualSearchQuery.modelGroup) &&
        Objects.equals(this.ensemblGeneId, geneExpressionIndividualSearchQuery.ensemblGeneId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tissue, name, modelGroup, ensemblGeneId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneExpressionIndividualSearchQueryDto {\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    modelGroup: ").append(toIndentedString(modelGroup)).append("\n");
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

    private GeneExpressionIndividualSearchQueryDto instance;

    public Builder() {
      this(new GeneExpressionIndividualSearchQueryDto());
    }

    protected Builder(GeneExpressionIndividualSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneExpressionIndividualSearchQueryDto value) { 
      this.instance.setTissue(value.tissue);
      this.instance.setName(value.name);
      this.instance.setModelGroup(value.modelGroup);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      return this;
    }

    public GeneExpressionIndividualSearchQueryDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    public GeneExpressionIndividualSearchQueryDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public GeneExpressionIndividualSearchQueryDto.Builder modelGroup(String modelGroup) {
      this.instance.modelGroup(modelGroup);
      return this;
    }
    
    public GeneExpressionIndividualSearchQueryDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    /**
    * returns a built GeneExpressionIndividualSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GeneExpressionIndividualSearchQueryDto build() {
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
  public static GeneExpressionIndividualSearchQueryDto.Builder builder() {
    return new GeneExpressionIndividualSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GeneExpressionIndividualSearchQueryDto.Builder toBuilder() {
    GeneExpressionIndividualSearchQueryDto.Builder builder = new GeneExpressionIndividualSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

