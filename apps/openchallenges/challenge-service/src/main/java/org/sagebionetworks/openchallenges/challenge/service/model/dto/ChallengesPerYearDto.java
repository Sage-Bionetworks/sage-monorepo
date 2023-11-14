package org.sagebionetworks.openchallenges.challenge.service.model.dto;

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

/** An object */
@Schema(name = "ChallengesPerYear", description = "An object")
@JsonTypeName("ChallengesPerYear")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@lombok.Builder
public class ChallengesPerYearDto {

  @JsonProperty("years")
  @Valid
  private List<String> years = new ArrayList<>();

  @JsonProperty("challengeCounts")
  @Valid
  private List<Integer> challengeCounts = new ArrayList<>();

  @JsonProperty("undatedChallengeCount")
  private Integer undatedChallengeCount = 0;

  public ChallengesPerYearDto years(List<String> years) {
    this.years = years;
    return this;
  }

  public ChallengesPerYearDto addYearsItem(String yearsItem) {
    if (this.years == null) {
      this.years = new ArrayList<>();
    }
    this.years.add(yearsItem);
    return this;
  }

  /**
   * Get years
   *
   * @return years
   */
  @NotNull
  @Schema(name = "years", required = true)
  public List<String> getYears() {
    return years;
  }

  public void setYears(List<String> years) {
    this.years = years;
  }

  public ChallengesPerYearDto challengeCounts(List<Integer> challengeCounts) {
    this.challengeCounts = challengeCounts;
    return this;
  }

  public ChallengesPerYearDto addChallengeCountsItem(Integer challengeCountsItem) {
    if (this.challengeCounts == null) {
      this.challengeCounts = new ArrayList<>();
    }
    this.challengeCounts.add(challengeCountsItem);
    return this;
  }

  /**
   * Get challengeCounts
   *
   * @return challengeCounts
   */
  @NotNull
  @Schema(name = "challengeCounts", required = true)
  public List<Integer> getChallengeCounts() {
    return challengeCounts;
  }

  public void setChallengeCounts(List<Integer> challengeCounts) {
    this.challengeCounts = challengeCounts;
  }

  public ChallengesPerYearDto undatedChallengeCount(Integer undatedChallengeCount) {
    this.undatedChallengeCount = undatedChallengeCount;
    return this;
  }

  /**
   * Get undatedChallengeCount minimum: 0
   *
   * @return undatedChallengeCount
   */
  @NotNull
  @Min(0)
  @Schema(name = "undatedChallengeCount", example = "0", required = true)
  public Integer getUndatedChallengeCount() {
    return undatedChallengeCount;
  }

  public void setUndatedChallengeCount(Integer undatedChallengeCount) {
    this.undatedChallengeCount = undatedChallengeCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengesPerYearDto challengesPerYear = (ChallengesPerYearDto) o;
    return Objects.equals(this.years, challengesPerYear.years)
        && Objects.equals(this.challengeCounts, challengesPerYear.challengeCounts)
        && Objects.equals(this.undatedChallengeCount, challengesPerYear.undatedChallengeCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(years, challengeCounts, undatedChallengeCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengesPerYearDto {\n");
    sb.append("    years: ").append(toIndentedString(years)).append("\n");
    sb.append("    challengeCounts: ").append(toIndentedString(challengeCounts)).append("\n");
    sb.append("    undatedChallengeCount: ")
        .append(toIndentedString(undatedChallengeCount))
        .append("\n");
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
