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
 * Statistics about a user&#39;s participation in battles.
 */

@Schema(name = "UserStats", description = "Statistics about a user's participation in battles.")
@JsonTypeName("UserStats")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class UserStatsDto {

  private Long totalBattles;

  private Long completedBattles;

  private Long activeBattles;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime firstBattleAt = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime latestBattleAt = null;

  private Long rank;

  public UserStatsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserStatsDto(Long totalBattles, Long completedBattles, Long activeBattles, Long rank) {
    this.totalBattles = totalBattles;
    this.completedBattles = completedBattles;
    this.activeBattles = activeBattles;
    this.rank = rank;
  }

  public UserStatsDto totalBattles(Long totalBattles) {
    this.totalBattles = totalBattles;
    return this;
  }

  /**
   * Total number of battles the user has participated in (as arbiter)
   * @return totalBattles
   */
  @NotNull 
  @Schema(name = "totalBattles", example = "42", description = "Total number of battles the user has participated in (as arbiter)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalBattles")
  public Long getTotalBattles() {
    return totalBattles;
  }

  public void setTotalBattles(Long totalBattles) {
    this.totalBattles = totalBattles;
  }

  public UserStatsDto completedBattles(Long completedBattles) {
    this.completedBattles = completedBattles;
    return this;
  }

  /**
   * Number of battles that have been completed (endedAt is set)
   * @return completedBattles
   */
  @NotNull 
  @Schema(name = "completedBattles", example = "38", description = "Number of battles that have been completed (endedAt is set)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("completedBattles")
  public Long getCompletedBattles() {
    return completedBattles;
  }

  public void setCompletedBattles(Long completedBattles) {
    this.completedBattles = completedBattles;
  }

  public UserStatsDto activeBattles(Long activeBattles) {
    this.activeBattles = activeBattles;
    return this;
  }

  /**
   * Number of battles currently in progress (endedAt is null)
   * @return activeBattles
   */
  @NotNull 
  @Schema(name = "activeBattles", example = "4", description = "Number of battles currently in progress (endedAt is null)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("activeBattles")
  public Long getActiveBattles() {
    return activeBattles;
  }

  public void setActiveBattles(Long activeBattles) {
    this.activeBattles = activeBattles;
  }

  public UserStatsDto firstBattleAt(@Nullable OffsetDateTime firstBattleAt) {
    this.firstBattleAt = firstBattleAt;
    return this;
  }

  /**
   * Timestamp of the user's first battle
   * @return firstBattleAt
   */
  @Valid 
  @Schema(name = "firstBattleAt", example = "2024-01-15T10:30Z", description = "Timestamp of the user's first battle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstBattleAt")
  public @Nullable OffsetDateTime getFirstBattleAt() {
    return firstBattleAt;
  }

  public void setFirstBattleAt(@Nullable OffsetDateTime firstBattleAt) {
    this.firstBattleAt = firstBattleAt;
  }

  public UserStatsDto latestBattleAt(@Nullable OffsetDateTime latestBattleAt) {
    this.latestBattleAt = latestBattleAt;
    return this;
  }

  /**
   * Timestamp of the user's most recent battle
   * @return latestBattleAt
   */
  @Valid 
  @Schema(name = "latestBattleAt", example = "2024-10-26T14:23Z", description = "Timestamp of the user's most recent battle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("latestBattleAt")
  public @Nullable OffsetDateTime getLatestBattleAt() {
    return latestBattleAt;
  }

  public void setLatestBattleAt(@Nullable OffsetDateTime latestBattleAt) {
    this.latestBattleAt = latestBattleAt;
  }

  public UserStatsDto rank(Long rank) {
    this.rank = rank;
    return this;
  }

  /**
   * User's rank based on completed battles using standard competition ranking. Users with the same number of completed battles share the same rank. All users have a rank, including those with 0 completed battles. 
   * @return rank
   */
  @NotNull 
  @Schema(name = "rank", example = "42", description = "User's rank based on completed battles using standard competition ranking. Users with the same number of completed battles share the same rank. All users have a rank, including those with 0 completed battles. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rank")
  public Long getRank() {
    return rank;
  }

  public void setRank(Long rank) {
    this.rank = rank;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserStatsDto userStats = (UserStatsDto) o;
    return Objects.equals(this.totalBattles, userStats.totalBattles) &&
        Objects.equals(this.completedBattles, userStats.completedBattles) &&
        Objects.equals(this.activeBattles, userStats.activeBattles) &&
        Objects.equals(this.firstBattleAt, userStats.firstBattleAt) &&
        Objects.equals(this.latestBattleAt, userStats.latestBattleAt) &&
        Objects.equals(this.rank, userStats.rank);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalBattles, completedBattles, activeBattles, firstBattleAt, latestBattleAt, rank);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserStatsDto {\n");
    sb.append("    totalBattles: ").append(toIndentedString(totalBattles)).append("\n");
    sb.append("    completedBattles: ").append(toIndentedString(completedBattles)).append("\n");
    sb.append("    activeBattles: ").append(toIndentedString(activeBattles)).append("\n");
    sb.append("    firstBattleAt: ").append(toIndentedString(firstBattleAt)).append("\n");
    sb.append("    latestBattleAt: ").append(toIndentedString(latestBattleAt)).append("\n");
    sb.append("    rank: ").append(toIndentedString(rank)).append("\n");
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

    private UserStatsDto instance;

    public Builder() {
      this(new UserStatsDto());
    }

    protected Builder(UserStatsDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(UserStatsDto value) { 
      this.instance.setTotalBattles(value.totalBattles);
      this.instance.setCompletedBattles(value.completedBattles);
      this.instance.setActiveBattles(value.activeBattles);
      this.instance.setFirstBattleAt(value.firstBattleAt);
      this.instance.setLatestBattleAt(value.latestBattleAt);
      this.instance.setRank(value.rank);
      return this;
    }

    public UserStatsDto.Builder totalBattles(Long totalBattles) {
      this.instance.totalBattles(totalBattles);
      return this;
    }
    
    public UserStatsDto.Builder completedBattles(Long completedBattles) {
      this.instance.completedBattles(completedBattles);
      return this;
    }
    
    public UserStatsDto.Builder activeBattles(Long activeBattles) {
      this.instance.activeBattles(activeBattles);
      return this;
    }
    
    public UserStatsDto.Builder firstBattleAt(OffsetDateTime firstBattleAt) {
      this.instance.firstBattleAt(firstBattleAt);
      return this;
    }
    
    public UserStatsDto.Builder latestBattleAt(OffsetDateTime latestBattleAt) {
      this.instance.latestBattleAt(latestBattleAt);
      return this;
    }
    
    public UserStatsDto.Builder rank(Long rank) {
      this.instance.rank(rank);
      return this;
    }
    
    /**
    * returns a built UserStatsDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public UserStatsDto build() {
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
  public static UserStatsDto.Builder builder() {
    return new UserStatsDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public UserStatsDto.Builder toBuilder() {
    UserStatsDto.Builder builder = new UserStatsDto.Builder();
    return builder.copyOf(this);
  }

}

