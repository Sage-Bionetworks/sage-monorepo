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
 * A historical entry representing a model&#39;s performance at a specific point in time.
 */

@Schema(name = "HistoricalLeaderboardEntry", description = "A historical entry representing a model's performance at a specific point in time.")
@JsonTypeName("HistoricalLeaderboardEntry")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class HistoricalLeaderboardEntryDto {

  private String snapshotId;

  private Double btScore;

  private Integer voteCount;

  private Integer rank;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public HistoricalLeaderboardEntryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public HistoricalLeaderboardEntryDto(String snapshotId, Double btScore, Integer voteCount, Integer rank, OffsetDateTime createdAt) {
    this.snapshotId = snapshotId;
    this.btScore = btScore;
    this.voteCount = voteCount;
    this.rank = rank;
    this.createdAt = createdAt;
  }

  public HistoricalLeaderboardEntryDto snapshotId(String snapshotId) {
    this.snapshotId = snapshotId;
    return this;
  }

  /**
   * Identifier for the snapshot/timepoint
   * @return snapshotId
   */
  @NotNull 
  @Schema(name = "snapshotId", example = "snapshot_2025-08-15_10-00", description = "Identifier for the snapshot/timepoint", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("snapshotId")
  public String getSnapshotId() {
    return snapshotId;
  }

  public void setSnapshotId(String snapshotId) {
    this.snapshotId = snapshotId;
  }

  public HistoricalLeaderboardEntryDto btScore(Double btScore) {
    this.btScore = btScore;
    return this;
  }

  /**
   * Primary scoring metric at this point in time
   * @return btScore
   */
  @NotNull 
  @Schema(name = "btScore", example = "0.915", description = "Primary scoring metric at this point in time", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("btScore")
  public Double getBtScore() {
    return btScore;
  }

  public void setBtScore(Double btScore) {
    this.btScore = btScore;
  }

  public HistoricalLeaderboardEntryDto voteCount(Integer voteCount) {
    this.voteCount = voteCount;
    return this;
  }

  /**
   * Number of votes/evaluations at this point in time
   * @return voteCount
   */
  @NotNull 
  @Schema(name = "voteCount", example = "1180", description = "Number of votes/evaluations at this point in time", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("voteCount")
  public Integer getVoteCount() {
    return voteCount;
  }

  public void setVoteCount(Integer voteCount) {
    this.voteCount = voteCount;
  }

  public HistoricalLeaderboardEntryDto rank(Integer rank) {
    this.rank = rank;
    return this;
  }

  /**
   * Rank position at this point in time (1-based)
   * @return rank
   */
  @NotNull 
  @Schema(name = "rank", example = "2", description = "Rank position at this point in time (1-based)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rank")
  public Integer getRank() {
    return rank;
  }

  public void setRank(Integer rank) {
    this.rank = rank;
  }

  public HistoricalLeaderboardEntryDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * When this snapshot was created
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2025-08-15T10:00Z", description = "When this snapshot was created", requiredMode = Schema.RequiredMode.REQUIRED)
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
    HistoricalLeaderboardEntryDto historicalLeaderboardEntry = (HistoricalLeaderboardEntryDto) o;
    return Objects.equals(this.snapshotId, historicalLeaderboardEntry.snapshotId) &&
        Objects.equals(this.btScore, historicalLeaderboardEntry.btScore) &&
        Objects.equals(this.voteCount, historicalLeaderboardEntry.voteCount) &&
        Objects.equals(this.rank, historicalLeaderboardEntry.rank) &&
        Objects.equals(this.createdAt, historicalLeaderboardEntry.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(snapshotId, btScore, voteCount, rank, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HistoricalLeaderboardEntryDto {\n");
    sb.append("    snapshotId: ").append(toIndentedString(snapshotId)).append("\n");
    sb.append("    btScore: ").append(toIndentedString(btScore)).append("\n");
    sb.append("    voteCount: ").append(toIndentedString(voteCount)).append("\n");
    sb.append("    rank: ").append(toIndentedString(rank)).append("\n");
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

    private HistoricalLeaderboardEntryDto instance;

    public Builder() {
      this(new HistoricalLeaderboardEntryDto());
    }

    protected Builder(HistoricalLeaderboardEntryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(HistoricalLeaderboardEntryDto value) { 
      this.instance.setSnapshotId(value.snapshotId);
      this.instance.setBtScore(value.btScore);
      this.instance.setVoteCount(value.voteCount);
      this.instance.setRank(value.rank);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public HistoricalLeaderboardEntryDto.Builder snapshotId(String snapshotId) {
      this.instance.snapshotId(snapshotId);
      return this;
    }
    
    public HistoricalLeaderboardEntryDto.Builder btScore(Double btScore) {
      this.instance.btScore(btScore);
      return this;
    }
    
    public HistoricalLeaderboardEntryDto.Builder voteCount(Integer voteCount) {
      this.instance.voteCount(voteCount);
      return this;
    }
    
    public HistoricalLeaderboardEntryDto.Builder rank(Integer rank) {
      this.instance.rank(rank);
      return this;
    }
    
    public HistoricalLeaderboardEntryDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built HistoricalLeaderboardEntryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public HistoricalLeaderboardEntryDto build() {
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
  public static HistoricalLeaderboardEntryDto.Builder builder() {
    return new HistoricalLeaderboardEntryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public HistoricalLeaderboardEntryDto.Builder toBuilder() {
    HistoricalLeaderboardEntryDto.Builder builder = new HistoricalLeaderboardEntryDto.Builder();
    return builder.copyOf(this);
  }

}

