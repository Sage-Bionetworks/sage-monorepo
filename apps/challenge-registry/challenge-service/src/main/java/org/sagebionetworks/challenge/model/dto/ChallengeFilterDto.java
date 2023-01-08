package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

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

  @JsonProperty("minStartDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate minStartDate = null;

  @JsonProperty("maxStartDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate maxStartDate = null;

  @JsonProperty("platforms")
  @Valid
  private List<String> platforms = null;

  @JsonProperty("status")
  @Valid
  private List<ChallengeStatusDto> status = null;

  @JsonProperty("submissionTypes")
  @Valid
  private List<ChallengeSubmissionTypeDto> submissionTypes = null;

  @JsonProperty("searchTerms")
  private String searchTerms;

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

  public ChallengeFilterDto minStartDate(LocalDate minStartDate) {
    this.minStartDate = minStartDate;
    return this;
  }

  /**
   * Keep the challenges that start at this date or later.
   *
   * @return minStartDate
   */
  @Valid
  @Schema(
      name = "minStartDate",
      example = "Fri Jul 21 00:00:00 UTC 2017",
      description = "Keep the challenges that start at this date or later.",
      required = false)
  public LocalDate getMinStartDate() {
    return minStartDate;
  }

  public void setMinStartDate(LocalDate minStartDate) {
    this.minStartDate = minStartDate;
  }

  public ChallengeFilterDto maxStartDate(LocalDate maxStartDate) {
    this.maxStartDate = maxStartDate;
    return this;
  }

  /**
   * Keep the challenges that start at this date or sooner.
   *
   * @return maxStartDate
   */
  @Valid
  @Schema(
      name = "maxStartDate",
      example = "Fri Jul 21 00:00:00 UTC 2017",
      description = "Keep the challenges that start at this date or sooner.",
      required = false)
  public LocalDate getMaxStartDate() {
    return maxStartDate;
  }

  public void setMaxStartDate(LocalDate maxStartDate) {
    this.maxStartDate = maxStartDate;
  }

  public ChallengeFilterDto platforms(List<String> platforms) {
    this.platforms = platforms;
    return this;
  }

  public ChallengeFilterDto addPlatformsItem(String platformsItem) {
    if (this.platforms == null) {
      this.platforms = new ArrayList<>();
    }
    this.platforms.add(platformsItem);
    return this;
  }

  /**
   * An array of challenge platform ids used to filter the results.
   *
   * @return platforms
   */
  @Schema(
      name = "platforms",
      description = "An array of challenge platform ids used to filter the results.",
      required = false)
  public List<String> getPlatforms() {
    return platforms;
  }

  public void setPlatforms(List<String> platforms) {
    this.platforms = platforms;
  }

  public ChallengeFilterDto status(List<ChallengeStatusDto> status) {
    this.status = status;
    return this;
  }

  public ChallengeFilterDto addStatusItem(ChallengeStatusDto statusItem) {
    if (this.status == null) {
      this.status = new ArrayList<>();
    }
    this.status.add(statusItem);
    return this;
  }

  /**
   * An array of challenge status used to filter the results.
   *
   * @return status
   */
  @Valid
  @Schema(
      name = "status",
      description = "An array of challenge status used to filter the results.",
      required = false)
  public List<ChallengeStatusDto> getStatus() {
    return status;
  }

  public void setStatus(List<ChallengeStatusDto> status) {
    this.status = status;
  }

  public ChallengeFilterDto submissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
    return this;
  }

  public ChallengeFilterDto addSubmissionTypesItem(ChallengeSubmissionTypeDto submissionTypesItem) {
    if (this.submissionTypes == null) {
      this.submissionTypes = new ArrayList<>();
    }
    this.submissionTypes.add(submissionTypesItem);
    return this;
  }

  /**
   * An array of challenge submission types used to filter the results.
   *
   * @return submissionTypes
   */
  @Valid
  @Schema(
      name = "submissionTypes",
      description = "An array of challenge submission types used to filter the results.",
      required = false)
  public List<ChallengeSubmissionTypeDto> getSubmissionTypes() {
    return submissionTypes;
  }

  public void setSubmissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
  }

  public ChallengeFilterDto searchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
    return this;
  }

  /**
   * A string of search terms used to filter the results.
   *
   * @return searchTerms
   */
  @Schema(
      name = "searchTerms",
      description = "A string of search terms used to filter the results.",
      required = false)
  public String getSearchTerms() {
    return searchTerms;
  }

  public void setSearchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
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
        && Objects.equals(this.incentives, challengeFilter.incentives)
        && Objects.equals(this.minStartDate, challengeFilter.minStartDate)
        && Objects.equals(this.maxStartDate, challengeFilter.maxStartDate)
        && Objects.equals(this.platforms, challengeFilter.platforms)
        && Objects.equals(this.status, challengeFilter.status)
        && Objects.equals(this.submissionTypes, challengeFilter.submissionTypes)
        && Objects.equals(this.searchTerms, challengeFilter.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        difficulties,
        incentives,
        minStartDate,
        maxStartDate,
        platforms,
        status,
        submissionTypes,
        searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeFilterDto {\n");
    sb.append("    difficulties: ").append(toIndentedString(difficulties)).append("\n");
    sb.append("    incentives: ").append(toIndentedString(incentives)).append("\n");
    sb.append("    minStartDate: ").append(toIndentedString(minStartDate)).append("\n");
    sb.append("    maxStartDate: ").append(toIndentedString(maxStartDate)).append("\n");
    sb.append("    platforms: ").append(toIndentedString(platforms)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    submissionTypes: ").append(toIndentedString(submissionTypes)).append("\n");
    sb.append("    searchTerms: ").append(toIndentedString(searchTerms)).append("\n");
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
