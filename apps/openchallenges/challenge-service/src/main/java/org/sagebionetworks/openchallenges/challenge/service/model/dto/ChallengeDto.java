package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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

  @JsonProperty("slug")
  private String slug;

  @JsonProperty("name")
  private String name;

  @JsonProperty("headline")
  private String headline;

  @JsonProperty("description")
  private String description;

  @JsonProperty("doi")
  private String doi;

  @JsonProperty("status")
  private ChallengeStatusDto status;

  @JsonProperty("platform")
  private SimpleChallengePlatformDto platform;

  @JsonProperty("websiteUrl")
  private String websiteUrl;

  @JsonProperty("avatarUrl")
  private String avatarUrl;

  @JsonProperty("incentives")
  @Valid
  private List<ChallengeIncentiveDto> incentives = new ArrayList<>();

  @JsonProperty("submissionTypes")
  @Valid
  private List<ChallengeSubmissionTypeDto> submissionTypes = new ArrayList<>();

  @JsonProperty("inputDataTypes")
  @Valid
  private List<SimpleChallengeInputDataTypeDto> inputDataTypes = null;

  @JsonProperty("startDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate startDate = null;

  @JsonProperty("endDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate endDate = null;

  @JsonProperty("starredCount")
  private Integer starredCount = 0;

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
   * The unique identifier of the challenge.
   *
   * @return id
   */
  @NotNull
  @Schema(
      name = "id",
      example = "1",
      description = "The unique identifier of the challenge.",
      required = true)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ChallengeDto slug(String slug) {
    this.slug = slug;
    return this;
  }

  /**
   * The slug of the challenge.
   *
   * @return slug
   */
  @NotNull
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  @Size(min = 3, max = 255)
  @Schema(
      name = "slug",
      example = "awesome-challenge",
      description = "The slug of the challenge.",
      required = true)
  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public ChallengeDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the challenge.
   *
   * @return name
   */
  @NotNull
  @Size(min = 3, max = 255)
  @Schema(name = "name", description = "The name of the challenge.", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChallengeDto headline(String headline) {
    this.headline = headline;
    return this;
  }

  /**
   * The headline of the challenge.
   *
   * @return headline
   */
  @Size(min = 0, max = 80)
  @Schema(
      name = "headline",
      example = "Example challenge headline",
      description = "The headline of the challenge.",
      required = false)
  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public ChallengeDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the challenge.
   *
   * @return description
   */
  @NotNull
  @Size(min = 0, max = 1000)
  @Schema(
      name = "description",
      example = "This is an example description of the challenge.",
      description = "The description of the challenge.",
      required = true)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ChallengeDto doi(String doi) {
    this.doi = doi;
    return this;
  }

  /**
   * Get doi
   *
   * @return doi
   */
  @Schema(name = "doi", required = false)
  public String getDoi() {
    return doi;
  }

  public void setDoi(String doi) {
    this.doi = doi;
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

  public ChallengeDto websiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  /**
   * Get websiteUrl
   *
   * @return websiteUrl
   */
  @Schema(name = "websiteUrl", required = false)
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public ChallengeDto avatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  /**
   * Get avatarUrl
   *
   * @return avatarUrl
   */
  @Schema(name = "avatarUrl", required = false)
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
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
  @NotNull
  @Valid
  @Schema(name = "incentives", required = true)
  public List<ChallengeIncentiveDto> getIncentives() {
    return incentives;
  }

  public void setIncentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
  }

  public ChallengeDto submissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
    return this;
  }

  public ChallengeDto addSubmissionTypesItem(ChallengeSubmissionTypeDto submissionTypesItem) {
    if (this.submissionTypes == null) {
      this.submissionTypes = new ArrayList<>();
    }
    this.submissionTypes.add(submissionTypesItem);
    return this;
  }

  /**
   * Get submissionTypes
   *
   * @return submissionTypes
   */
  @NotNull
  @Valid
  @Schema(name = "submissionTypes", required = true)
  public List<ChallengeSubmissionTypeDto> getSubmissionTypes() {
    return submissionTypes;
  }

  public void setSubmissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
  }

  public ChallengeDto inputDataTypes(List<SimpleChallengeInputDataTypeDto> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
    return this;
  }

  public ChallengeDto addInputDataTypesItem(SimpleChallengeInputDataTypeDto inputDataTypesItem) {
    if (this.inputDataTypes == null) {
      this.inputDataTypes = new ArrayList<>();
    }
    this.inputDataTypes.add(inputDataTypesItem);
    return this;
  }

  /**
   * Get inputDataTypes
   *
   * @return inputDataTypes
   */
  @Valid
  @Schema(name = "inputDataTypes", required = false)
  public List<SimpleChallengeInputDataTypeDto> getInputDataTypes() {
    return inputDataTypes;
  }

  public void setInputDataTypes(List<SimpleChallengeInputDataTypeDto> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
  }

  public ChallengeDto startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * The start date of the challenge.
   *
   * @return startDate
   */
  @Valid
  @Schema(
      name = "startDate",
      example = "Fri Jul 21 00:00:00 UTC 2017",
      description = "The start date of the challenge.",
      required = false)
  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public ChallengeDto endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * The end date of the challenge.
   *
   * @return endDate
   */
  @Valid
  @Schema(
      name = "endDate",
      example = "Fri Jul 21 00:00:00 UTC 2017",
      description = "The end date of the challenge.",
      required = false)
  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public ChallengeDto starredCount(Integer starredCount) {
    this.starredCount = starredCount;
    return this;
  }

  /**
   * The number of times the challenge has been starred by users.
   *
   * @return starredCount
   */
  @NotNull
  @Schema(
      name = "starredCount",
      description = "The number of times the challenge has been starred by users.",
      required = true)
  public Integer getStarredCount() {
    return starredCount;
  }

  public void setStarredCount(Integer starredCount) {
    this.starredCount = starredCount;
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
        && Objects.equals(this.slug, challenge.slug)
        && Objects.equals(this.name, challenge.name)
        && Objects.equals(this.headline, challenge.headline)
        && Objects.equals(this.description, challenge.description)
        && Objects.equals(this.doi, challenge.doi)
        && Objects.equals(this.status, challenge.status)
        && Objects.equals(this.platform, challenge.platform)
        && Objects.equals(this.websiteUrl, challenge.websiteUrl)
        && Objects.equals(this.avatarUrl, challenge.avatarUrl)
        && Objects.equals(this.incentives, challenge.incentives)
        && Objects.equals(this.submissionTypes, challenge.submissionTypes)
        && Objects.equals(this.inputDataTypes, challenge.inputDataTypes)
        && Objects.equals(this.startDate, challenge.startDate)
        && Objects.equals(this.endDate, challenge.endDate)
        && Objects.equals(this.starredCount, challenge.starredCount)
        && Objects.equals(this.createdAt, challenge.createdAt)
        && Objects.equals(this.updatedAt, challenge.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        slug,
        name,
        headline,
        description,
        doi,
        status,
        platform,
        websiteUrl,
        avatarUrl,
        incentives,
        submissionTypes,
        inputDataTypes,
        startDate,
        endDate,
        starredCount,
        createdAt,
        updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    headline: ").append(toIndentedString(headline)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    doi: ").append(toIndentedString(doi)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    platform: ").append(toIndentedString(platform)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    incentives: ").append(toIndentedString(incentives)).append("\n");
    sb.append("    submissionTypes: ").append(toIndentedString(submissionTypes)).append("\n");
    sb.append("    inputDataTypes: ").append(toIndentedString(inputDataTypes)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    starredCount: ").append(toIndentedString(starredCount)).append("\n");
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
