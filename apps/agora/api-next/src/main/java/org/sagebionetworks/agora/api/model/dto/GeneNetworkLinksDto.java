package org.sagebionetworks.agora.api.model.dto;

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
 * Gene Network Links
 */

@Schema(name = "GeneNetworkLinks", description = "Gene Network Links")
@JsonTypeName("GeneNetworkLinks")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GeneNetworkLinksDto {

  private String id;

  private String geneAEnsemblGeneId;

  private String geneBEnsemblGeneId;

  private String geneAExternalGeneName;

  private String geneBExternalGeneName;

  private String brainRegion;

  public GeneNetworkLinksDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneNetworkLinksDto(String id, String geneAEnsemblGeneId, String geneBEnsemblGeneId, String geneAExternalGeneName, String geneBExternalGeneName, String brainRegion) {
    this.id = id;
    this.geneAEnsemblGeneId = geneAEnsemblGeneId;
    this.geneBEnsemblGeneId = geneBEnsemblGeneId;
    this.geneAExternalGeneName = geneAExternalGeneName;
    this.geneBExternalGeneName = geneBExternalGeneName;
    this.brainRegion = brainRegion;
  }

  public GeneNetworkLinksDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GeneNetworkLinksDto geneAEnsemblGeneId(String geneAEnsemblGeneId) {
    this.geneAEnsemblGeneId = geneAEnsemblGeneId;
    return this;
  }

  /**
   * Ensembl gene ID for gene A
   * @return geneAEnsemblGeneId
   */
  @NotNull 
  @Schema(name = "geneA_ensembl_gene_id", description = "Ensembl gene ID for gene A", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("geneA_ensembl_gene_id")
  public String getGeneAEnsemblGeneId() {
    return geneAEnsemblGeneId;
  }

  public void setGeneAEnsemblGeneId(String geneAEnsemblGeneId) {
    this.geneAEnsemblGeneId = geneAEnsemblGeneId;
  }

  public GeneNetworkLinksDto geneBEnsemblGeneId(String geneBEnsemblGeneId) {
    this.geneBEnsemblGeneId = geneBEnsemblGeneId;
    return this;
  }

  /**
   * Ensembl gene ID for gene B
   * @return geneBEnsemblGeneId
   */
  @NotNull 
  @Schema(name = "geneB_ensembl_gene_id", description = "Ensembl gene ID for gene B", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("geneB_ensembl_gene_id")
  public String getGeneBEnsemblGeneId() {
    return geneBEnsemblGeneId;
  }

  public void setGeneBEnsemblGeneId(String geneBEnsemblGeneId) {
    this.geneBEnsemblGeneId = geneBEnsemblGeneId;
  }

  public GeneNetworkLinksDto geneAExternalGeneName(String geneAExternalGeneName) {
    this.geneAExternalGeneName = geneAExternalGeneName;
    return this;
  }

  /**
   * External gene name for gene A
   * @return geneAExternalGeneName
   */
  @NotNull 
  @Schema(name = "geneA_external_gene_name", description = "External gene name for gene A", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("geneA_external_gene_name")
  public String getGeneAExternalGeneName() {
    return geneAExternalGeneName;
  }

  public void setGeneAExternalGeneName(String geneAExternalGeneName) {
    this.geneAExternalGeneName = geneAExternalGeneName;
  }

  public GeneNetworkLinksDto geneBExternalGeneName(String geneBExternalGeneName) {
    this.geneBExternalGeneName = geneBExternalGeneName;
    return this;
  }

  /**
   * External gene name for gene B
   * @return geneBExternalGeneName
   */
  @NotNull 
  @Schema(name = "geneB_external_gene_name", description = "External gene name for gene B", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("geneB_external_gene_name")
  public String getGeneBExternalGeneName() {
    return geneBExternalGeneName;
  }

  public void setGeneBExternalGeneName(String geneBExternalGeneName) {
    this.geneBExternalGeneName = geneBExternalGeneName;
  }

  public GeneNetworkLinksDto brainRegion(String brainRegion) {
    this.brainRegion = brainRegion;
    return this;
  }

  /**
   * Associated brain region
   * @return brainRegion
   */
  @NotNull 
  @Schema(name = "brainRegion", description = "Associated brain region", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("brainRegion")
  public String getBrainRegion() {
    return brainRegion;
  }

  public void setBrainRegion(String brainRegion) {
    this.brainRegion = brainRegion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeneNetworkLinksDto geneNetworkLinks = (GeneNetworkLinksDto) o;
    return Objects.equals(this.id, geneNetworkLinks.id) &&
        Objects.equals(this.geneAEnsemblGeneId, geneNetworkLinks.geneAEnsemblGeneId) &&
        Objects.equals(this.geneBEnsemblGeneId, geneNetworkLinks.geneBEnsemblGeneId) &&
        Objects.equals(this.geneAExternalGeneName, geneNetworkLinks.geneAExternalGeneName) &&
        Objects.equals(this.geneBExternalGeneName, geneNetworkLinks.geneBExternalGeneName) &&
        Objects.equals(this.brainRegion, geneNetworkLinks.brainRegion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, geneAEnsemblGeneId, geneBEnsemblGeneId, geneAExternalGeneName, geneBExternalGeneName, brainRegion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneNetworkLinksDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    geneAEnsemblGeneId: ").append(toIndentedString(geneAEnsemblGeneId)).append("\n");
    sb.append("    geneBEnsemblGeneId: ").append(toIndentedString(geneBEnsemblGeneId)).append("\n");
    sb.append("    geneAExternalGeneName: ").append(toIndentedString(geneAExternalGeneName)).append("\n");
    sb.append("    geneBExternalGeneName: ").append(toIndentedString(geneBExternalGeneName)).append("\n");
    sb.append("    brainRegion: ").append(toIndentedString(brainRegion)).append("\n");
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

    private GeneNetworkLinksDto instance;

    public Builder() {
      this(new GeneNetworkLinksDto());
    }

    protected Builder(GeneNetworkLinksDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneNetworkLinksDto value) { 
      this.instance.setId(value.id);
      this.instance.setGeneAEnsemblGeneId(value.geneAEnsemblGeneId);
      this.instance.setGeneBEnsemblGeneId(value.geneBEnsemblGeneId);
      this.instance.setGeneAExternalGeneName(value.geneAExternalGeneName);
      this.instance.setGeneBExternalGeneName(value.geneBExternalGeneName);
      this.instance.setBrainRegion(value.brainRegion);
      return this;
    }

    public GeneNetworkLinksDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public GeneNetworkLinksDto.Builder geneAEnsemblGeneId(String geneAEnsemblGeneId) {
      this.instance.geneAEnsemblGeneId(geneAEnsemblGeneId);
      return this;
    }
    
    public GeneNetworkLinksDto.Builder geneBEnsemblGeneId(String geneBEnsemblGeneId) {
      this.instance.geneBEnsemblGeneId(geneBEnsemblGeneId);
      return this;
    }
    
    public GeneNetworkLinksDto.Builder geneAExternalGeneName(String geneAExternalGeneName) {
      this.instance.geneAExternalGeneName(geneAExternalGeneName);
      return this;
    }
    
    public GeneNetworkLinksDto.Builder geneBExternalGeneName(String geneBExternalGeneName) {
      this.instance.geneBExternalGeneName(geneBExternalGeneName);
      return this;
    }
    
    public GeneNetworkLinksDto.Builder brainRegion(String brainRegion) {
      this.instance.brainRegion(brainRegion);
      return this;
    }
    
    /**
    * returns a built GeneNetworkLinksDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GeneNetworkLinksDto build() {
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
  public static GeneNetworkLinksDto.Builder builder() {
    return new GeneNetworkLinksDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GeneNetworkLinksDto.Builder toBuilder() {
    GeneNetworkLinksDto.Builder builder = new GeneNetworkLinksDto.Builder();
    return builder.copyOf(this);
  }

}

