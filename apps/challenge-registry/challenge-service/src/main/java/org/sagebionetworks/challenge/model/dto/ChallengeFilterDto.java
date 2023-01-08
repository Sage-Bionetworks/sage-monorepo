package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** A challenge filter. */
@Schema(name = "ChallengeFilter", description = "A challenge filter.")
@JsonTypeName("ChallengeFilter")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengeFilterDto {

  @JsonProperty("difficulties")
  @Valid
  private List<ChallengeDifficultyDto> difficulties = null;

  @JsonProperty("incentives")
  @Valid
  private List<ChallengeIncentiveDto> incentives = null;

  public ChallengeFilterDto difficulties(List<ChallengeDifficultyDto> difficulties) {
    this.difficulties = difficulties;
    return this;
  }

  public ChallengeFilterDto addDifficultiesItem(ChallengeDifficultyDto difficultiesItem) {
    if (this.difficulties == null) {
      this.difficulties = new ArrayList<>();
    }
    this.difficulties.add(difficultiesItem);
    return this;
  }

  /**
   * An array of challenge difficulty levels used to filter the results.
   *
   * @return difficulties
   */
  @Valid
  @Schema(
      name = "difficulties",
      description = "An array of challenge difficulty levels used to filter the results.",
      required = false)
  public List<ChallengeDifficultyDto> getDifficulties() {
    return difficulties;
  }

  public void setDifficulties(List<ChallengeDifficultyDto> difficulties) {
    this.difficulties = difficulties;
  }

  public ChallengeFilterDto incentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
    return this;
  }

  public ChallengeFilterDto addIncentivesItem(ChallengeIncentiveDto incentivesItem) {
    if (this.incentives == null) {
      this.incentives = new ArrayList<>();
    }
    this.incentives.add(incentivesItem);
    return this;
  }

  /**
   * An array of challenge incentive types used to filter the results.
   *
   * @return incentives
   */
  @Valid
  @Schema(
      name = "incentives",
      description = "An array of challenge incentive types used to filter the results.",
      required = false)
  public List<ChallengeIncentiveDto> getIncentives() {
    return incentives;
  }

  public void setIncentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeFilterDto challengeFilter = (ChallengeFilterDto) o;
    return Objects.equals(this.difficulties, challengeFilter.difficulties)
        && Objects.equals(this.incentives, challengeFilter.incentives);
  }

  @Override
  public int hashCode() {
    return Objects.hash(difficulties, incentives);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeFilterDto {\n");
    sb.append("    difficulties: ").append(toIndentedString(difficulties)).append("\n");
    sb.append("    incentives: ").append(toIndentedString(incentives)).append("\n");
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
