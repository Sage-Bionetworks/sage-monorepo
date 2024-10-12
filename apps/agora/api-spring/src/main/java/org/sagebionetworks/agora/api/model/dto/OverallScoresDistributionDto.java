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
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Distributions
 */

@Schema(name = "OverallScoresDistribution", description = "Distributions")
@JsonTypeName("OverallScoresDistribution")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class OverallScoresDistributionDto {

  @Valid
  private List<BigDecimal> distribution = new ArrayList<>();

  @Valid
  private List<List<BigDecimal>> bins = new ArrayList<>();

  private String name;

  private String synId;

  private String wikiId;

  public OverallScoresDistributionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OverallScoresDistributionDto(List<BigDecimal> distribution, List<List<BigDecimal>> bins, String name, String synId, String wikiId) {
    this.distribution = distribution;
    this.bins = bins;
    this.name = name;
    this.synId = synId;
    this.wikiId = wikiId;
  }

  public OverallScoresDistributionDto distribution(List<BigDecimal> distribution) {
    this.distribution = distribution;
    return this;
  }

  public OverallScoresDistributionDto addDistributionItem(BigDecimal distributionItem) {
    if (this.distribution == null) {
      this.distribution = new ArrayList<>();
    }
    this.distribution.add(distributionItem);
    return this;
  }

  /**
   * Distribution of overall scores
   * @return distribution
  */
  @NotNull @Valid 
  @Schema(name = "distribution", description = "Distribution of overall scores", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("distribution")
  public List<BigDecimal> getDistribution() {
    return distribution;
  }

  public void setDistribution(List<BigDecimal> distribution) {
    this.distribution = distribution;
  }

  public OverallScoresDistributionDto bins(List<List<BigDecimal>> bins) {
    this.bins = bins;
    return this;
  }

  public OverallScoresDistributionDto addBinsItem(List<BigDecimal> binsItem) {
    if (this.bins == null) {
      this.bins = new ArrayList<>();
    }
    this.bins.add(binsItem);
    return this;
  }

  /**
   * Bins used in the distribution
   * @return bins
  */
  @NotNull @Valid 
  @Schema(name = "bins", description = "Bins used in the distribution", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bins")
  public List<List<BigDecimal>> getBins() {
    return bins;
  }

  public void setBins(List<List<BigDecimal>> bins) {
    this.bins = bins;
  }

  public OverallScoresDistributionDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the score distribution
   * @return name
  */
  @NotNull 
  @Schema(name = "name", description = "Name of the score distribution", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OverallScoresDistributionDto synId(String synId) {
    this.synId = synId;
    return this;
  }

  /**
   * Synapse ID associated with the score
   * @return synId
  */
  @NotNull 
  @Schema(name = "syn_id", description = "Synapse ID associated with the score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("syn_id")
  public String getSynId() {
    return synId;
  }

  public void setSynId(String synId) {
    this.synId = synId;
  }

  public OverallScoresDistributionDto wikiId(String wikiId) {
    this.wikiId = wikiId;
    return this;
  }

  /**
   * Wiki ID associated with the score
   * @return wikiId
  */
  @NotNull 
  @Schema(name = "wiki_id", description = "Wiki ID associated with the score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("wiki_id")
  public String getWikiId() {
    return wikiId;
  }

  public void setWikiId(String wikiId) {
    this.wikiId = wikiId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OverallScoresDistributionDto overallScoresDistribution = (OverallScoresDistributionDto) o;
    return Objects.equals(this.distribution, overallScoresDistribution.distribution) &&
        Objects.equals(this.bins, overallScoresDistribution.bins) &&
        Objects.equals(this.name, overallScoresDistribution.name) &&
        Objects.equals(this.synId, overallScoresDistribution.synId) &&
        Objects.equals(this.wikiId, overallScoresDistribution.wikiId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(distribution, bins, name, synId, wikiId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OverallScoresDistributionDto {\n");
    sb.append("    distribution: ").append(toIndentedString(distribution)).append("\n");
    sb.append("    bins: ").append(toIndentedString(bins)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    synId: ").append(toIndentedString(synId)).append("\n");
    sb.append("    wikiId: ").append(toIndentedString(wikiId)).append("\n");
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

