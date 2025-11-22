package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A single entry in a leaderboard representing a model&#39;s performance.
 */

@Schema(name = "LeaderboardEntry", description = "A single entry in a leaderboard representing a model's performance.")
@JsonTypeName("LeaderboardEntry")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LeaderboardEntryDto {

  private String id;

  private String modelId;

  private String modelName;

  private @Nullable String modelOrganization = null;

  private String modelUrl;

  private String license;

  private Double btScore;

  private Integer voteCount;

  private Integer rank;

  private Double bootstrapQ025;

  private Double bootstrapQ975;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public LeaderboardEntryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LeaderboardEntryDto(String id, String modelId, String modelName, String modelUrl, String license, Double btScore, Integer voteCount, Integer rank, Double bootstrapQ025, Double bootstrapQ975, OffsetDateTime createdAt) {
    this.id = id;
    this.modelId = modelId;
    this.modelName = modelName;
    this.modelUrl = modelUrl;
    this.license = license;
    this.btScore = btScore;
    this.voteCount = voteCount;
    this.rank = rank;
    this.bootstrapQ025 = bootstrapQ025;
    this.bootstrapQ975 = bootstrapQ975;
    this.createdAt = createdAt;
  }

  public LeaderboardEntryDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for this leaderboard entry
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "entry_123", description = "Unique identifier for this leaderboard entry", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LeaderboardEntryDto modelId(String modelId) {
    this.modelId = modelId;
    return this;
  }

  /**
   * Identifier for the model
   * @return modelId
   */
  @NotNull 
  @Schema(name = "modelId", example = "model_456", description = "Identifier for the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelId")
  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public LeaderboardEntryDto modelName(String modelName) {
    this.modelName = modelName;
    return this;
  }

  /**
   * Display name of the model
   * @return modelName
   */
  @NotNull 
  @Schema(name = "modelName", example = "GPT-4o", description = "Display name of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelName")
  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public LeaderboardEntryDto modelOrganization(@Nullable String modelOrganization) {
    this.modelOrganization = modelOrganization;
    return this;
  }

  /**
   * Organization that created the model
   * @return modelOrganization
   */
  @Size(max = 200) 
  @Schema(name = "modelOrganization", example = "OpenAI", description = "Organization that created the model", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modelOrganization")
  public @Nullable String getModelOrganization() {
    return modelOrganization;
  }

  public void setModelOrganization(@Nullable String modelOrganization) {
    this.modelOrganization = modelOrganization;
  }

  public LeaderboardEntryDto modelUrl(String modelUrl) {
    this.modelUrl = modelUrl;
    return this;
  }

  /**
   * External link to model information
   * @return modelUrl
   */
  @NotNull @Size(max = 300) 
  @Schema(name = "modelUrl", example = "https://openai.com/gpt-4", description = "External link to model information", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelUrl")
  public String getModelUrl() {
    return modelUrl;
  }

  public void setModelUrl(String modelUrl) {
    this.modelUrl = modelUrl;
  }

  public LeaderboardEntryDto license(String license) {
    this.license = license;
    return this;
  }

  /**
   * License type of the model
   * @return license
   */
  @NotNull 
  @Schema(name = "license", example = "MIT", description = "License type of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("license")
  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public LeaderboardEntryDto btScore(Double btScore) {
    this.btScore = btScore;
    return this;
  }

  /**
   * Bradley-Terry score - primary ranking metric
   * @return btScore
   */
  @NotNull 
  @Schema(name = "btScore", example = "0.925", description = "Bradley-Terry score - primary ranking metric", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("btScore")
  public Double getBtScore() {
    return btScore;
  }

  public void setBtScore(Double btScore) {
    this.btScore = btScore;
  }

  public LeaderboardEntryDto voteCount(Integer voteCount) {
    this.voteCount = voteCount;
    return this;
  }

  /**
   * Number of votes/evaluations
   * @return voteCount
   */
  @NotNull 
  @Schema(name = "voteCount", example = "1250", description = "Number of votes/evaluations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("voteCount")
  public Integer getVoteCount() {
    return voteCount;
  }

  public void setVoteCount(Integer voteCount) {
    this.voteCount = voteCount;
  }

  public LeaderboardEntryDto rank(Integer rank) {
    this.rank = rank;
    return this;
  }

  /**
   * Current rank position (1-based)
   * @return rank
   */
  @NotNull 
  @Schema(name = "rank", example = "1", description = "Current rank position (1-based)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rank")
  public Integer getRank() {
    return rank;
  }

  public void setRank(Integer rank) {
    this.rank = rank;
  }

  public LeaderboardEntryDto bootstrapQ025(Double bootstrapQ025) {
    this.bootstrapQ025 = bootstrapQ025;
    return this;
  }

  /**
   * Bootstrap confidence interval lower bound (2.5th percentile)
   * @return bootstrapQ025
   */
  @NotNull 
  @Schema(name = "bootstrapQ025", example = "887", description = "Bootstrap confidence interval lower bound (2.5th percentile)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bootstrapQ025")
  public Double getBootstrapQ025() {
    return bootstrapQ025;
  }

  public void setBootstrapQ025(Double bootstrapQ025) {
    this.bootstrapQ025 = bootstrapQ025;
  }

  public LeaderboardEntryDto bootstrapQ975(Double bootstrapQ975) {
    this.bootstrapQ975 = bootstrapQ975;
    return this;
  }

  /**
   * Bootstrap confidence interval upper bound (97.5th percentile)
   * @return bootstrapQ975
   */
  @NotNull 
  @Schema(name = "bootstrapQ975", example = "1063", description = "Bootstrap confidence interval upper bound (97.5th percentile)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bootstrapQ975")
  public Double getBootstrapQ975() {
    return bootstrapQ975;
  }

  public void setBootstrapQ975(Double bootstrapQ975) {
    this.bootstrapQ975 = bootstrapQ975;
  }

  public LeaderboardEntryDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * When this entry was created
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2025-08-16T10:30Z", description = "When this entry was created", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderboardEntryDto leaderboardEntry = (LeaderboardEntryDto) o;
    return Objects.equals(this.id, leaderboardEntry.id) &&
        Objects.equals(this.modelId, leaderboardEntry.modelId) &&
        Objects.equals(this.modelName, leaderboardEntry.modelName) &&
        Objects.equals(this.modelOrganization, leaderboardEntry.modelOrganization) &&
        Objects.equals(this.modelUrl, leaderboardEntry.modelUrl) &&
        Objects.equals(this.license, leaderboardEntry.license) &&
        Objects.equals(this.btScore, leaderboardEntry.btScore) &&
        Objects.equals(this.voteCount, leaderboardEntry.voteCount) &&
        Objects.equals(this.rank, leaderboardEntry.rank) &&
        Objects.equals(this.bootstrapQ025, leaderboardEntry.bootstrapQ025) &&
        Objects.equals(this.bootstrapQ975, leaderboardEntry.bootstrapQ975) &&
        Objects.equals(this.createdAt, leaderboardEntry.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, modelId, modelName, modelOrganization, modelUrl, license, btScore, voteCount, rank, bootstrapQ025, bootstrapQ975, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderboardEntryDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    modelName: ").append(toIndentedString(modelName)).append("\n");
    sb.append("    modelOrganization: ").append(toIndentedString(modelOrganization)).append("\n");
    sb.append("    modelUrl: ").append(toIndentedString(modelUrl)).append("\n");
    sb.append("    license: ").append(toIndentedString(license)).append("\n");
    sb.append("    btScore: ").append(toIndentedString(btScore)).append("\n");
    sb.append("    voteCount: ").append(toIndentedString(voteCount)).append("\n");
    sb.append("    rank: ").append(toIndentedString(rank)).append("\n");
    sb.append("    bootstrapQ025: ").append(toIndentedString(bootstrapQ025)).append("\n");
    sb.append("    bootstrapQ975: ").append(toIndentedString(bootstrapQ975)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

    private LeaderboardEntryDto instance;

    public Builder() {
      this(new LeaderboardEntryDto());
    }

    protected Builder(LeaderboardEntryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LeaderboardEntryDto value) { 
      this.instance.setId(value.id);
      this.instance.setModelId(value.modelId);
      this.instance.setModelName(value.modelName);
      this.instance.setModelOrganization(value.modelOrganization);
      this.instance.setModelUrl(value.modelUrl);
      this.instance.setLicense(value.license);
      this.instance.setBtScore(value.btScore);
      this.instance.setVoteCount(value.voteCount);
      this.instance.setRank(value.rank);
      this.instance.setBootstrapQ025(value.bootstrapQ025);
      this.instance.setBootstrapQ975(value.bootstrapQ975);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public LeaderboardEntryDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public LeaderboardEntryDto.Builder modelId(String modelId) {
      this.instance.modelId(modelId);
      return this;
    }
    
    public LeaderboardEntryDto.Builder modelName(String modelName) {
      this.instance.modelName(modelName);
      return this;
    }
    
    public LeaderboardEntryDto.Builder modelOrganization(String modelOrganization) {
      this.instance.modelOrganization(modelOrganization);
      return this;
    }
    
    public LeaderboardEntryDto.Builder modelUrl(String modelUrl) {
      this.instance.modelUrl(modelUrl);
      return this;
    }
    
    public LeaderboardEntryDto.Builder license(String license) {
      this.instance.license(license);
      return this;
    }
    
    public LeaderboardEntryDto.Builder btScore(Double btScore) {
      this.instance.btScore(btScore);
      return this;
    }
    
    public LeaderboardEntryDto.Builder voteCount(Integer voteCount) {
      this.instance.voteCount(voteCount);
      return this;
    }
    
    public LeaderboardEntryDto.Builder rank(Integer rank) {
      this.instance.rank(rank);
      return this;
    }
    
    public LeaderboardEntryDto.Builder bootstrapQ025(Double bootstrapQ025) {
      this.instance.bootstrapQ025(bootstrapQ025);
      return this;
    }
    
    public LeaderboardEntryDto.Builder bootstrapQ975(Double bootstrapQ975) {
      this.instance.bootstrapQ975(bootstrapQ975);
      return this;
    }
    
    public LeaderboardEntryDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built LeaderboardEntryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LeaderboardEntryDto build() {
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
  public static LeaderboardEntryDto.Builder builder() {
    return new LeaderboardEntryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LeaderboardEntryDto.Builder toBuilder() {
    LeaderboardEntryDto.Builder builder = new LeaderboardEntryDto.Builder();
    return builder.copyOf(this);
  }

}

