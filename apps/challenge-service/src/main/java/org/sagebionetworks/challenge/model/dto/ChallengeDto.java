package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

/** A challenge */
@Schema(name = "Challenge", description = "A challenge")
@JsonTypeName("Challenge")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengeDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("status")
  private ChallengeStatusDto status;

  @JsonProperty("difficulty")
  private ChallengeDifficultyDto difficulty;

  @JsonProperty("platform")
  private SimpleChallengePlatformDto platform;

  @JsonProperty("incentives")
  @Valid
  private List<ChallengeIncentiveDto> incentives = null;

  @JsonProperty("createdAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @JsonProperty("updatedAt")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public ChallengeDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of a challenge.
   *
   * @return id
   */
  @NotNull
  @Schema(
      name = "id",
      example = "1",
      description = "The unique identifier of a challenge.",
      required = true)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ChallengeDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the a challenge
   *
   * @return name
   */
  @NotNull
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  @Size(min = 3, max = 60)
  @Schema(
      name = "name",
      example = "awesome-challenge",
      description = "The name of the a challenge",
      required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChallengeDto status(ChallengeStatusDto status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   *
   * @return status
   */
  @NotNull
  @Valid
  @Schema(name = "status", required = true)
  public ChallengeStatusDto getStatus() {
    return status;
  }

  public void setStatus(ChallengeStatusDto status) {
    this.status = status;
  }

  public ChallengeDto difficulty(ChallengeDifficultyDto difficulty) {
    this.difficulty = difficulty;
    return this;
  }

  /**
   * Get difficulty
   *
   * @return difficulty
   */
  @NotNull
  @Valid
  @Schema(name = "difficulty", required = true)
  public ChallengeDifficultyDto getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(ChallengeDifficultyDto difficulty) {
    this.difficulty = difficulty;
  }

  public ChallengeDto platform(SimpleChallengePlatformDto platform) {
    this.platform = platform;
    return this;
  }

  /**
   * Get platform
   *
   * @return platform
   */
  @NotNull
  @Valid
  @Schema(name = "platform", required = true)
  public SimpleChallengePlatformDto getPlatform() {
    return platform;
  }

  public void setPlatform(SimpleChallengePlatformDto platform) {
    this.platform = platform;
  }

  public ChallengeDto incentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
    return this;
  }

  public ChallengeDto addIncentivesItem(ChallengeIncentiveDto incentivesItem) {
    if (this.incentives == null) {
      this.incentives = new ArrayList<>();
    }
    this.incentives.add(incentivesItem);
    return this;
  }

  /**
   * Get incentives
   *
   * @return incentives
   */
  @Valid
  @Schema(name = "incentives", required = false)
  public List<ChallengeIncentiveDto> getIncentives() {
    return incentives;
  }

  public void setIncentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
  }

  public ChallengeDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   *
   * @return createdAt
   */
  @NotNull
  @Valid
  @Schema(name = "createdAt", example = "2022-07-04T22:19:11Z", required = true)
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ChallengeDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   *
   * @return updatedAt
   */
  @NotNull
  @Valid
  @Schema(name = "updatedAt", example = "2022-07-04T22:19:11Z", required = true)
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeDto challenge = (ChallengeDto) o;
    return Objects.equals(this.id, challenge.id)
        && Objects.equals(this.name, challenge.name)
        && Objects.equals(this.status, challenge.status)
        && Objects.equals(this.difficulty, challenge.difficulty)
        && Objects.equals(this.platform, challenge.platform)
        && Objects.equals(this.incentives, challenge.incentives)
        && Objects.equals(this.createdAt, challenge.createdAt)
        && Objects.equals(this.updatedAt, challenge.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, status, difficulty, platform, incentives, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    difficulty: ").append(toIndentedString(difficulty)).append("\n");
    sb.append("    platform: ").append(toIndentedString(platform)).append("\n");
    sb.append("    incentives: ").append(toIndentedString(incentives)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
