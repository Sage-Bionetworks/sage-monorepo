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
 * SimilarGenesNetworkLink
 */

@Schema(name = "SimilarGenesNetworkLink", description = "SimilarGenesNetworkLink")
@JsonTypeName("SimilarGenesNetworkLink")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class SimilarGenesNetworkLinkDto {

  private String source;

  private String target;

  private String sourceHgncSymbol;

  private String targetHgncSymbol;

  @Valid
  private List<String> brainRegions = new ArrayList<>();

  public SimilarGenesNetworkLinkDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SimilarGenesNetworkLinkDto(String source, String target, String sourceHgncSymbol, String targetHgncSymbol, List<String> brainRegions) {
    this.source = source;
    this.target = target;
    this.sourceHgncSymbol = sourceHgncSymbol;
    this.targetHgncSymbol = targetHgncSymbol;
    this.brainRegions = brainRegions;
  }

  public SimilarGenesNetworkLinkDto source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   */
  @NotNull 
  @Schema(name = "source", requiredMode = Schema.RequiredMode.REQUIRED)
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
  @NotNull 
  @Schema(name = "target", requiredMode = Schema.RequiredMode.REQUIRED)
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
  @NotNull 
  @Schema(name = "source_hgnc_symbol", requiredMode = Schema.RequiredMode.REQUIRED)
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
  @NotNull 
  @Schema(name = "target_hgnc_symbol", requiredMode = Schema.RequiredMode.REQUIRED)
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
  
  public static class Builder {

    private SimilarGenesNetworkLinkDto instance;

    public Builder() {
      this(new SimilarGenesNetworkLinkDto());
    }

    protected Builder(SimilarGenesNetworkLinkDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(SimilarGenesNetworkLinkDto value) { 
      this.instance.setSource(value.source);
      this.instance.setTarget(value.target);
      this.instance.setSourceHgncSymbol(value.sourceHgncSymbol);
      this.instance.setTargetHgncSymbol(value.targetHgncSymbol);
      this.instance.setBrainRegions(value.brainRegions);
      return this;
    }

    public SimilarGenesNetworkLinkDto.Builder source(String source) {
      this.instance.source(source);
      return this;
    }
    
    public SimilarGenesNetworkLinkDto.Builder target(String target) {
      this.instance.target(target);
      return this;
    }
    
    public SimilarGenesNetworkLinkDto.Builder sourceHgncSymbol(String sourceHgncSymbol) {
      this.instance.sourceHgncSymbol(sourceHgncSymbol);
      return this;
    }
    
    public SimilarGenesNetworkLinkDto.Builder targetHgncSymbol(String targetHgncSymbol) {
      this.instance.targetHgncSymbol(targetHgncSymbol);
      return this;
    }
    
    public SimilarGenesNetworkLinkDto.Builder brainRegions(List<String> brainRegions) {
      this.instance.brainRegions(brainRegions);
      return this;
    }
    
    /**
    * returns a built SimilarGenesNetworkLinkDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public SimilarGenesNetworkLinkDto build() {
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
  public static SimilarGenesNetworkLinkDto.Builder builder() {
    return new SimilarGenesNetworkLinkDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public SimilarGenesNetworkLinkDto.Builder toBuilder() {
    SimilarGenesNetworkLinkDto.Builder builder = new SimilarGenesNetworkLinkDto.Builder();
    return builder.copyOf(this);
  }

}

