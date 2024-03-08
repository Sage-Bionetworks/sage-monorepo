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
  private String headline = null;

  @JsonProperty("description")
  private String description;

  @JsonProperty("doi")
  private String doi = null;

  @JsonProperty("status")
  private ChallengeStatusDto status;

  @JsonProperty("platform")
  private SimpleChallengePlatformDto platform = null;

  @JsonProperty("websiteUrl")
  private String websiteUrl = null;

  @JsonProperty("avatarUrl")
  private String avatarUrl = null;

  @JsonProperty("incentives")
  @Valid
  private List<ChallengeIncentiveDto> incentives = new ArrayList<>();

  @JsonProperty("submissionTypes")
  @Valid
  private List<ChallengeSubmissionTypeDto> submissionTypes = new ArrayList<>();

  @JsonProperty("inputDataTypes")
  @Valid
  private List<EdamDataDto> inputDataTypes = null;

  @JsonProperty("categories")
  @Valid
  private List<ChallengeCategoryDto> categories = new ArrayList<>();

  @JsonProperty("startDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate startDate = null;

  @JsonProperty("endDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate endDate = null;

  @JsonProperty("starredCount")
  private Integer starredCount = 0;

  @JsonProperty("operation")
  private EdamOperationDto operation = null;

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
   * The unique slug of the challenge.
   *
   * @return slug
   */
  @NotNull
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  @Size(min = 3, max = 255)
  @Schema(
      name = "slug",
      example = "awesome-challenge",
      description = "The unique slug of the challenge.",
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
   * The DOI of the challenge.
   *
   * @return doi
   */
  @Size(max = 120)
  @Schema(
      name = "doi",
      example = "https://doi.org/123/abc",
      description = "The DOI of the challenge.",
      required = false)
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
  @Valid
  @Schema(name = "platform", required = false)
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
   * A URL to the website or image.
   *
   * @return websiteUrl
   */
  @Size(max = 500)
  @Schema(
      name = "websiteUrl",
      example = "https://openchallenges.io",
      description = "A URL to the website or image.",
      required = false)
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
   * A URL to the website or image.
   *
   * @return avatarUrl
   */
  @Size(max = 500)
  @Schema(
      name = "avatarUrl",
      example = "https://openchallenges.io",
      description = "A URL to the website or image.",
      required = false)
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

  public ChallengeDto inputDataTypes(List<EdamDataDto> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
    return this;
  }

  public ChallengeDto addInputDataTypesItem(EdamDataDto inputDataTypesItem) {
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
  public List<EdamDataDto> getInputDataTypes() {
    return inputDataTypes;
  }

  public void setInputDataTypes(List<EdamDataDto> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
  }

  public ChallengeDto categories(List<ChallengeCategoryDto> categories) {
    this.categories = categories;
    return this;
  }

  public ChallengeDto addCategoriesItem(ChallengeCategoryDto categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * Get categories
   *
   * @return categories
   */
  @NotNull
  @Valid
  @Schema(name = "categories", required = true)
  public List<ChallengeCategoryDto> getCategories() {
    return categories;
  }

  public void setCategories(List<ChallengeCategoryDto> categories) {
    this.categories = categories;
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
   * The number of times the challenge has been starred by users. minimum: 0
   *
   * @return starredCount
   */
  @NotNull
  @Min(0)
  @Schema(
      name = "starredCount",
      example = "100",
      description = "The number of times the challenge has been starred by users.",
      required = true)
  public Integer getStarredCount() {
    return starredCount;
  }

  public void setStarredCount(Integer starredCount) {
    this.starredCount = starredCount;
  }

  public ChallengeDto operation(EdamOperationDto operation) {
    this.operation = operation;
    return this;
  }

  /**
   * Get operation
   *
   * @return operation
   */
  @Valid
  @Schema(name = "operation", required = false)
  public EdamOperationDto getOperation() {
    return operation;
  }

  public void setOperation(EdamOperationDto operation) {
    this.operation = operation;
  }

  public ChallengeDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Datetime when the object was added to the database.
   *
   * @return createdAt
   */
  @NotNull
  @Valid
  @Schema(
      name = "createdAt",
      example = "2022-07-04T22:19:11Z",
      description = "Datetime when the object was added to the database.",
      required = true)
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
   * Datetime when the object was last modified in the database.
   *
   * @return updatedAt
   */
  @NotNull
  @Valid
  @Schema(
      name = "updatedAt",
      example = "2022-07-04T22:19:11Z",
      description = "Datetime when the object was last modified in the database.",
      required = true)
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
        && Objects.equals(this.categories, challenge.categories)
        && Objects.equals(this.startDate, challenge.startDate)
        && Objects.equals(this.endDate, challenge.endDate)
        && Objects.equals(this.starredCount, challenge.starredCount)
        && Objects.equals(this.operation, challenge.operation)
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
        categories,
        startDate,
        endDate,
        starredCount,
        operation,
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
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    starredCount: ").append(toIndentedString(starredCount)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
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
