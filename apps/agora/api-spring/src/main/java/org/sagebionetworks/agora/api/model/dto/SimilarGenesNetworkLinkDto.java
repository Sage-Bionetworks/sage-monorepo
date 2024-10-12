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
 * SimilarGenesNetworkLink
 */

@Schema(name = "SimilarGenesNetworkLink", description = "SimilarGenesNetworkLink")
@JsonTypeName("SimilarGenesNetworkLink")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class SimilarGenesNetworkLinkDto {

  private String source;

  private String target;

  private String sourceHgncSymbol;

  private String targetHgncSymbol;

  @Valid
  private List<String> brainRegions;

  public SimilarGenesNetworkLinkDto source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
  */
  
  @Schema(name = "source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("source")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public SimilarGenesNetworkLinkDto target(String target) {
    this.target = target;
    return this;
  }

  /**
   * Get target
   * @return target
  */
  
  @Schema(name = "target", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("target")
  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public SimilarGenesNetworkLinkDto sourceHgncSymbol(String sourceHgncSymbol) {
    this.sourceHgncSymbol = sourceHgncSymbol;
    return this;
  }

  /**
   * Get sourceHgncSymbol
   * @return sourceHgncSymbol
  */
  
  @Schema(name = "source_hgnc_symbol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("source_hgnc_symbol")
  public String getSourceHgncSymbol() {
    return sourceHgncSymbol;
  }

  public void setSourceHgncSymbol(String sourceHgncSymbol) {
    this.sourceHgncSymbol = sourceHgncSymbol;
  }

  public SimilarGenesNetworkLinkDto targetHgncSymbol(String targetHgncSymbol) {
    this.targetHgncSymbol = targetHgncSymbol;
    return this;
  }

  /**
   * Get targetHgncSymbol
   * @return targetHgncSymbol
  */
  
  @Schema(name = "target_hgnc_symbol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("target_hgnc_symbol")
  public String getTargetHgncSymbol() {
    return targetHgncSymbol;
  }

  public void setTargetHgncSymbol(String targetHgncSymbol) {
    this.targetHgncSymbol = targetHgncSymbol;
  }

  public SimilarGenesNetworkLinkDto brainRegions(List<String> brainRegions) {
    this.brainRegions = brainRegions;
    return this;
  }

  public SimilarGenesNetworkLinkDto addBrainRegionsItem(String brainRegionsItem) {
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
    SimilarGenesNetworkLinkDto similarGenesNetworkLink = (SimilarGenesNetworkLinkDto) o;
    return Objects.equals(this.source, similarGenesNetworkLink.source) &&
        Objects.equals(this.target, similarGenesNetworkLink.target) &&
        Objects.equals(this.sourceHgncSymbol, similarGenesNetworkLink.sourceHgncSymbol) &&
        Objects.equals(this.targetHgncSymbol, similarGenesNetworkLink.targetHgncSymbol) &&
        Objects.equals(this.brainRegions, similarGenesNetworkLink.brainRegions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, target, sourceHgncSymbol, targetHgncSymbol, brainRegions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimilarGenesNetworkLinkDto {\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    target: ").append(toIndentedString(target)).append("\n");
    sb.append("    sourceHgncSymbol: ").append(toIndentedString(sourceHgncSymbol)).append("\n");
    sb.append("    targetHgncSymbol: ").append(toIndentedString(targetHgncSymbol)).append("\n");
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

