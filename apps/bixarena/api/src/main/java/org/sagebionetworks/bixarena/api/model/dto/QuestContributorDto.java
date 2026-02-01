package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A user who contributed to the quest
 */

@Schema(name = "QuestContributor", description = "A user who contributed to the quest")
@JsonTypeName("QuestContributor")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class QuestContributorDto {

  private String username;

  private Integer battleCount;

  /**
   * Contributor rank based on average battles per week
   */
  public enum RankEnum {
    CHAMPION("champion"),
    
    KNIGHT("knight"),
    
    APPRENTICE("apprentice");

    private final String value;

    RankEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RankEnum fromValue(String value) {
      for (RankEnum b : RankEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private RankEnum rank;

  private Double battlesPerWeek;

  public QuestContributorDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QuestContributorDto(String username, Integer battleCount, RankEnum rank, Double battlesPerWeek) {
    this.username = username;
    this.battleCount = battleCount;
    this.rank = rank;
    this.battlesPerWeek = battlesPerWeek;
  }

  public QuestContributorDto username(String username) {
    this.username = username;
    return this;
  }

  /**
   * User's display name
   * @return username
   */
  @NotNull 
  @Schema(name = "username", example = "tschaffter", description = "User's display name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public QuestContributorDto battleCount(Integer battleCount) {
    this.battleCount = battleCount;
    return this;
  }

  /**
   * Number of completed battles during quest period
   * @return battleCount
   */
  @NotNull 
  @Schema(name = "battleCount", example = "42", description = "Number of completed battles during quest period", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("battleCount")
  public Integer getBattleCount() {
    return battleCount;
  }

  public void setBattleCount(Integer battleCount) {
    this.battleCount = battleCount;
  }

  public QuestContributorDto rank(RankEnum rank) {
    this.rank = rank;
    return this;
  }

  /**
   * Contributor rank based on average battles per week
   * @return rank
   */
  @NotNull 
  @Schema(name = "rank", example = "champion", description = "Contributor rank based on average battles per week", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rank")
  public RankEnum getRank() {
    return rank;
  }

  public void setRank(RankEnum rank) {
    this.rank = rank;
  }

  public QuestContributorDto battlesPerWeek(Double battlesPerWeek) {
    this.battlesPerWeek = battlesPerWeek;
    return this;
  }

  /**
   * Average battles completed per week during quest
   * @return battlesPerWeek
   */
  @NotNull 
  @Schema(name = "battlesPerWeek", example = "12.5", description = "Average battles completed per week during quest", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("battlesPerWeek")
  public Double getBattlesPerWeek() {
    return battlesPerWeek;
  }

  public void setBattlesPerWeek(Double battlesPerWeek) {
    this.battlesPerWeek = battlesPerWeek;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestContributorDto questContributor = (QuestContributorDto) o;
    return Objects.equals(this.username, questContributor.username) &&
        Objects.equals(this.battleCount, questContributor.battleCount) &&
        Objects.equals(this.rank, questContributor.rank) &&
        Objects.equals(this.battlesPerWeek, questContributor.battlesPerWeek);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, battleCount, rank, battlesPerWeek);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuestContributorDto {\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    battleCount: ").append(toIndentedString(battleCount)).append("\n");
    sb.append("    rank: ").append(toIndentedString(rank)).append("\n");
    sb.append("    battlesPerWeek: ").append(toIndentedString(battlesPerWeek)).append("\n");
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

    private QuestContributorDto instance;

    public Builder() {
      this(new QuestContributorDto());
    }

    protected Builder(QuestContributorDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(QuestContributorDto value) { 
      this.instance.setUsername(value.username);
      this.instance.setBattleCount(value.battleCount);
      this.instance.setRank(value.rank);
      this.instance.setBattlesPerWeek(value.battlesPerWeek);
      return this;
    }

    public QuestContributorDto.Builder username(String username) {
      this.instance.username(username);
      return this;
    }
    
    public QuestContributorDto.Builder battleCount(Integer battleCount) {
      this.instance.battleCount(battleCount);
      return this;
    }
    
    public QuestContributorDto.Builder rank(RankEnum rank) {
      this.instance.rank(rank);
      return this;
    }
    
    public QuestContributorDto.Builder battlesPerWeek(Double battlesPerWeek) {
      this.instance.battlesPerWeek(battlesPerWeek);
      return this;
    }
    
    /**
    * returns a built QuestContributorDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public QuestContributorDto build() {
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
  public static QuestContributorDto.Builder builder() {
    return new QuestContributorDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public QuestContributorDto.Builder toBuilder() {
    QuestContributorDto.Builder builder = new QuestContributorDto.Builder();
    return builder.copyOf(this);
  }

}

