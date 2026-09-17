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
 * Proteomics individual filter query options
 */

@Schema(name = "ProteomicsIndividualFilterQuery", description = "Proteomics individual filter query options")
@JsonTypeName("ProteomicsIndividualFilterQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ProteomicsIndividualFilterQueryDto {

  private String tissue;

  private String modelIdentifier;

  private ModelIdentifierTypeDto modelIdentifierType;

  private String uniqueId;

  public ProteomicsIndividualFilterQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProteomicsIndividualFilterQueryDto(String tissue, String modelIdentifier, ModelIdentifierTypeDto modelIdentifierType, String uniqueId) {
    this.tissue = tissue;
    this.modelIdentifier = modelIdentifier;
    this.modelIdentifierType = modelIdentifierType;
    this.uniqueId = uniqueId;
  }

  public ProteomicsIndividualFilterQueryDto tissue(String tissue) {
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

  public ProteomicsIndividualFilterQueryDto modelIdentifier(String modelIdentifier) {
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

  public ProteomicsIndividualFilterQueryDto modelIdentifierType(ModelIdentifierTypeDto modelIdentifierType) {
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

  public ProteomicsIndividualFilterQueryDto uniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
    return this;
  }

  /**
   * Unique ID to filter by
   * @return uniqueId
   */
  @NotNull 
  @Schema(name = "uniqueId", example = "ENSMUSG00000000001P27144", description = "Unique ID to filter by", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("uniqueId")
  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProteomicsIndividualFilterQueryDto proteomicsIndividualFilterQuery = (ProteomicsIndividualFilterQueryDto) o;
    return Objects.equals(this.tissue, proteomicsIndividualFilterQuery.tissue) &&
        Objects.equals(this.modelIdentifier, proteomicsIndividualFilterQuery.modelIdentifier) &&
        Objects.equals(this.modelIdentifierType, proteomicsIndividualFilterQuery.modelIdentifierType) &&
        Objects.equals(this.uniqueId, proteomicsIndividualFilterQuery.uniqueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tissue, modelIdentifier, modelIdentifierType, uniqueId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProteomicsIndividualFilterQueryDto {\n");
    sb.append("    tissue: ").append(toIndentedString(tissue)).append("\n");
    sb.append("    modelIdentifier: ").append(toIndentedString(modelIdentifier)).append("\n");
    sb.append("    modelIdentifierType: ").append(toIndentedString(modelIdentifierType)).append("\n");
    sb.append("    uniqueId: ").append(toIndentedString(uniqueId)).append("\n");
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

    private ProteomicsIndividualFilterQueryDto instance;

    public Builder() {
      this(new ProteomicsIndividualFilterQueryDto());
    }

    protected Builder(ProteomicsIndividualFilterQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ProteomicsIndividualFilterQueryDto value) { 
      this.instance.setTissue(value.tissue);
      this.instance.setModelIdentifier(value.modelIdentifier);
      this.instance.setModelIdentifierType(value.modelIdentifierType);
      this.instance.setUniqueId(value.uniqueId);
      return this;
    }

    public ProteomicsIndividualFilterQueryDto.Builder tissue(String tissue) {
      this.instance.tissue(tissue);
      return this;
    }
    
    public ProteomicsIndividualFilterQueryDto.Builder modelIdentifier(String modelIdentifier) {
      this.instance.modelIdentifier(modelIdentifier);
      return this;
    }
    
    public ProteomicsIndividualFilterQueryDto.Builder modelIdentifierType(ModelIdentifierTypeDto modelIdentifierType) {
      this.instance.modelIdentifierType(modelIdentifierType);
      return this;
    }
    
    public ProteomicsIndividualFilterQueryDto.Builder uniqueId(String uniqueId) {
      this.instance.uniqueId(uniqueId);
      return this;
    }
    
    /**
    * returns a built ProteomicsIndividualFilterQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ProteomicsIndividualFilterQueryDto build() {
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
  public static ProteomicsIndividualFilterQueryDto.Builder builder() {
    return new ProteomicsIndividualFilterQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ProteomicsIndividualFilterQueryDto.Builder toBuilder() {
    ProteomicsIndividualFilterQueryDto.Builder builder = new ProteomicsIndividualFilterQueryDto.Builder();
    return builder.copyOf(this);
  }

}

