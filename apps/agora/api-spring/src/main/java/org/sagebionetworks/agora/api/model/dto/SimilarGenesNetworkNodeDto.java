package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SimilarGenesNetworkNode
 */

@Schema(name = "SimilarGenesNetworkNode", description = "SimilarGenesNetworkNode")
@JsonTypeName("SimilarGenesNetworkNode")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class SimilarGenesNetworkNodeDto {

  private String ensemblGeneId;

  private String hgncSymbol;

  @Valid
  private List<String> brainRegions;

  public SimilarGenesNetworkNodeDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Get ensemblGeneId
   * @return ensemblGeneId
  */
  
  @Schema(name = "ensembl_gene_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
  
  @Schema(name = "hgnc_symbol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
  
  @Schema(name = "brain_regions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
}

