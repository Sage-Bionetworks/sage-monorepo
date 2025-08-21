package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SimilarGenesNetworkNode
 */

@Schema(name = "SimilarGenesNetworkNode", description = "SimilarGenesNetworkNode")
@JsonTypeName("SimilarGenesNetworkNode")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class SimilarGenesNetworkNodeDto {

  private String ensemblGeneId;

  private String hgncSymbol;

  @Valid
  private List<String> brainRegions = new ArrayList<>();

  public SimilarGenesNetworkNodeDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SimilarGenesNetworkNodeDto(String ensemblGeneId, String hgncSymbol, List<String> brainRegions) {
    this.ensemblGeneId = ensemblGeneId;
    this.hgncSymbol = hgncSymbol;
    this.brainRegions = brainRegions;
  }

  public SimilarGenesNetworkNodeDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Get ensemblGeneId
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public SimilarGenesNetworkNodeDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * Get hgncSymbol
   * @return hgncSymbol
   */
  @NotNull 
  @Schema(name = "hgnc_symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  public SimilarGenesNetworkNodeDto brainRegions(List<String> brainRegions) {
    this.brainRegions = brainRegions;
    return this;
  }

  public SimilarGenesNetworkNodeDto addBrainRegionsItem(String brainRegionsItem) {
    if (this.brainRegions == null) {
      this.brainRegions = new ArrayList<>();
    }
    this.brainRegions.add(brainRegionsItem);
    return this;
  }

  /**
   * Get brainRegions
   * @return brainRegions
   */
  @NotNull 
  @Schema(name = "brain_regions", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("brain_regions")
  public List<String> getBrainRegions() {
    return brainRegions;
  }

  public void setBrainRegions(List<String> brainRegions) {
    this.brainRegions = brainRegions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimilarGenesNetworkNodeDto similarGenesNetworkNode = (SimilarGenesNetworkNodeDto) o;
    return Objects.equals(this.ensemblGeneId, similarGenesNetworkNode.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, similarGenesNetworkNode.hgncSymbol) &&
        Objects.equals(this.brainRegions, similarGenesNetworkNode.brainRegions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, hgncSymbol, brainRegions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimilarGenesNetworkNodeDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    brainRegions: ").append(toIndentedString(brainRegions)).append("\n");
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

    private SimilarGenesNetworkNodeDto instance;

    public Builder() {
      this(new SimilarGenesNetworkNodeDto());
    }

    protected Builder(SimilarGenesNetworkNodeDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(SimilarGenesNetworkNodeDto value) { 
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setBrainRegions(value.brainRegions);
      return this;
    }

    public SimilarGenesNetworkNodeDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public SimilarGenesNetworkNodeDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public SimilarGenesNetworkNodeDto.Builder brainRegions(List<String> brainRegions) {
      this.instance.brainRegions(brainRegions);
      return this;
    }
    
    /**
    * returns a built SimilarGenesNetworkNodeDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public SimilarGenesNetworkNodeDto build() {
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
  public static SimilarGenesNetworkNodeDto.Builder builder() {
    return new SimilarGenesNetworkNodeDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public SimilarGenesNetworkNodeDto.Builder toBuilder() {
    SimilarGenesNetworkNodeDto.Builder builder = new SimilarGenesNetworkNodeDto.Builder();
    return builder.copyOf(this);
  }

}

