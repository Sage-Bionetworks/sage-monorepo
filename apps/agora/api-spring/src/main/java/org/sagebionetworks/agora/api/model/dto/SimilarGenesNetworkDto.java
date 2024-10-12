package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.model.dto.SimilarGenesNetworkLinkDto;
import org.sagebionetworks.agora.api.model.dto.SimilarGenesNetworkNodeDto;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SimilarGenesNetwork
 */

@Schema(name = "SimilarGenesNetwork", description = "SimilarGenesNetwork")
@JsonTypeName("SimilarGenesNetwork")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class SimilarGenesNetworkDto {

  @Valid
  private List<@Valid SimilarGenesNetworkNodeDto> nodes;

  @Valid
  private List<@Valid SimilarGenesNetworkLinkDto> links;

  private BigDecimal min;

  private BigDecimal max;

  public SimilarGenesNetworkDto nodes(List<@Valid SimilarGenesNetworkNodeDto> nodes) {
    this.nodes = nodes;
    return this;
  }

  public SimilarGenesNetworkDto addNodesItem(SimilarGenesNetworkNodeDto nodesItem) {
    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }
    this.nodes.add(nodesItem);
    return this;
  }

  /**
   * Get nodes
   * @return nodes
  */
  @Valid 
  @Schema(name = "nodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nodes")
  public List<@Valid SimilarGenesNetworkNodeDto> getNodes() {
    return nodes;
  }

  public void setNodes(List<@Valid SimilarGenesNetworkNodeDto> nodes) {
    this.nodes = nodes;
  }

  public SimilarGenesNetworkDto links(List<@Valid SimilarGenesNetworkLinkDto> links) {
    this.links = links;
    return this;
  }

  public SimilarGenesNetworkDto addLinksItem(SimilarGenesNetworkLinkDto linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Get links
   * @return links
  */
  @Valid 
  @Schema(name = "links", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("links")
  public List<@Valid SimilarGenesNetworkLinkDto> getLinks() {
    return links;
  }

  public void setLinks(List<@Valid SimilarGenesNetworkLinkDto> links) {
    this.links = links;
  }

  public SimilarGenesNetworkDto min(BigDecimal min) {
    this.min = min;
    return this;
  }

  /**
   * Get min
   * @return min
  */
  @Valid 
  @Schema(name = "min", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min")
  public BigDecimal getMin() {
    return min;
  }

  public void setMin(BigDecimal min) {
    this.min = min;
  }

  public SimilarGenesNetworkDto max(BigDecimal max) {
    this.max = max;
    return this;
  }

  /**
   * Get max
   * @return max
  */
  @Valid 
  @Schema(name = "max", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("max")
  public BigDecimal getMax() {
    return max;
  }

  public void setMax(BigDecimal max) {
    this.max = max;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimilarGenesNetworkDto similarGenesNetwork = (SimilarGenesNetworkDto) o;
    return Objects.equals(this.nodes, similarGenesNetwork.nodes) &&
        Objects.equals(this.links, similarGenesNetwork.links) &&
        Objects.equals(this.min, similarGenesNetwork.min) &&
        Objects.equals(this.max, similarGenesNetwork.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodes, links, min, max);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimilarGenesNetworkDto {\n");
    sb.append("    nodes: ").append(toIndentedString(nodes)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    min: ").append(toIndentedString(min)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
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

