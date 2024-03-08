package org.sagebionetworks.openchallenges.challenge.service.model.dto;

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

/** A challenge search query. */
@Schema(name = "ChallengeSearchQuery", description = "A challenge search query.")
@JsonTypeName("ChallengeSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengeSearchQueryDto {

  @JsonProperty("pageNumber")
  private Integer pageNumber = 0;

  @JsonProperty("pageSize")
  private Integer pageSize = 100;

  @JsonProperty("sort")
  private ChallengeSortDto sort = ChallengeSortDto.RELEVANCE;

  @JsonProperty("sortSeed")
  private Integer sortSeed = null;

  @JsonProperty("direction")
  private ChallengeDirectionDto direction = null;

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

  @JsonProperty("organizations")
  @Valid
  private List<Long> organizations = null;

  @JsonProperty("status")
  @Valid
  private List<ChallengeStatusDto> status = null;

  @JsonProperty("submissionTypes")
  @Valid
  private List<ChallengeSubmissionTypeDto> submissionTypes = null;

  @JsonProperty("categories")
  @Valid
  private List<ChallengeCategoryDto> categories = null;

  @JsonProperty("searchTerms")
  private String searchTerms;

  public ChallengeSearchQueryDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * The page number. minimum: 0
   *
   * @return pageNumber
   */
  @Min(0)
  @Schema(name = "pageNumber", description = "The page number.", required = false)
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public ChallengeSearchQueryDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * The number of items in a single page. minimum: 1
   *
   * @return pageSize
   */
  @Min(1)
  @Schema(
      name = "pageSize",
      description = "The number of items in a single page.",
      required = false)
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public ChallengeSearchQueryDto sort(ChallengeSortDto sort) {
    this.sort = sort;
    return this;
  }

  /**
   * Get sort
   *
   * @return sort
   */
  @Valid
  @Schema(name = "sort", required = false)
  public ChallengeSortDto getSort() {
    return sort;
  }

  public void setSort(ChallengeSortDto sort) {
    this.sort = sort;
  }

  public ChallengeSearchQueryDto sortSeed(Integer sortSeed) {
    this.sortSeed = sortSeed;
    return this;
  }

  /**
   * The seed that initializes the random sorter. minimum: 0 maximum: 2147483647
   *
   * @return sortSeed
   */
  @Min(0)
  @Max(2147483647)
  @Schema(
      name = "sortSeed",
      description = "The seed that initializes the random sorter.",
      required = false)
  public Integer getSortSeed() {
    return sortSeed;
  }

  public void setSortSeed(Integer sortSeed) {
    this.sortSeed = sortSeed;
  }

  public ChallengeSearchQueryDto direction(ChallengeDirectionDto direction) {
    this.direction = direction;
    return this;
  }

  /**
   * Get direction
   *
   * @return direction
   */
  @Valid
  @Schema(name = "direction", required = false)
  public ChallengeDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(ChallengeDirectionDto direction) {
    this.direction = direction;
  }

  public ChallengeSearchQueryDto incentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
    return this;
  }

  public ChallengeSearchQueryDto addIncentivesItem(ChallengeIncentiveDto incentivesItem) {
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

  public ChallengeSearchQueryDto minStartDate(LocalDate minStartDate) {
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

  public ChallengeSearchQueryDto maxStartDate(LocalDate maxStartDate) {
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

  public ChallengeSearchQueryDto platforms(List<String> platforms) {
    this.platforms = platforms;
    return this;
  }

  public ChallengeSearchQueryDto addPlatformsItem(String platformsItem) {
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

  public ChallengeSearchQueryDto organizations(List<Long> organizations) {
    this.organizations = organizations;
    return this;
  }

  public ChallengeSearchQueryDto addOrganizationsItem(Long organizationsItem) {
    if (this.organizations == null) {
      this.organizations = new ArrayList<>();
    }
    this.organizations.add(organizationsItem);
    return this;
  }

  /**
   * An array of organization ids used to filter the results.
   *
   * @return organizations
   */
  @Schema(
      name = "organizations",
      description = "An array of organization ids used to filter the results.",
      required = false)
  public List<Long> getOrganizations() {
    return organizations;
  }

  public void setOrganizations(List<Long> organizations) {
    this.organizations = organizations;
  }

  public ChallengeSearchQueryDto status(List<ChallengeStatusDto> status) {
    this.status = status;
    return this;
  }

  public ChallengeSearchQueryDto addStatusItem(ChallengeStatusDto statusItem) {
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

  public ChallengeSearchQueryDto submissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
    return this;
  }

  public ChallengeSearchQueryDto addSubmissionTypesItem(
      ChallengeSubmissionTypeDto submissionTypesItem) {
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

  public ChallengeSearchQueryDto categories(List<ChallengeCategoryDto> categories) {
    this.categories = categories;
    return this;
  }

  public ChallengeSearchQueryDto addCategoriesItem(ChallengeCategoryDto categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * The array of challenge categories used to filter the results.
   *
   * @return categories
   */
  @Valid
  @Schema(
      name = "categories",
      description = "The array of challenge categories used to filter the results.",
      required = false)
  public List<ChallengeCategoryDto> getCategories() {
    return categories;
  }

  public void setCategories(List<ChallengeCategoryDto> categories) {
    this.categories = categories;
  }

  public ChallengeSearchQueryDto searchTerms(String searchTerms) {
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
      example = "dream challenge",
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
    ChallengeSearchQueryDto challengeSearchQuery = (ChallengeSearchQueryDto) o;
    return Objects.equals(this.pageNumber, challengeSearchQuery.pageNumber)
        && Objects.equals(this.pageSize, challengeSearchQuery.pageSize)
        && Objects.equals(this.sort, challengeSearchQuery.sort)
        && Objects.equals(this.sortSeed, challengeSearchQuery.sortSeed)
        && Objects.equals(this.direction, challengeSearchQuery.direction)
        && Objects.equals(this.incentives, challengeSearchQuery.incentives)
        && Objects.equals(this.minStartDate, challengeSearchQuery.minStartDate)
        && Objects.equals(this.maxStartDate, challengeSearchQuery.maxStartDate)
        && Objects.equals(this.platforms, challengeSearchQuery.platforms)
        && Objects.equals(this.organizations, challengeSearchQuery.organizations)
        && Objects.equals(this.status, challengeSearchQuery.status)
        && Objects.equals(this.submissionTypes, challengeSearchQuery.submissionTypes)
        && Objects.equals(this.categories, challengeSearchQuery.categories)
        && Objects.equals(this.searchTerms, challengeSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        pageNumber,
        pageSize,
        sort,
        sortSeed,
        direction,
        incentives,
        minStartDate,
        maxStartDate,
        platforms,
        organizations,
        status,
        submissionTypes,
        categories,
        searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    sortSeed: ").append(toIndentedString(sortSeed)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    incentives: ").append(toIndentedString(incentives)).append("\n");
    sb.append("    minStartDate: ").append(toIndentedString(minStartDate)).append("\n");
    sb.append("    maxStartDate: ").append(toIndentedString(maxStartDate)).append("\n");
    sb.append("    platforms: ").append(toIndentedString(platforms)).append("\n");
    sb.append("    organizations: ").append(toIndentedString(organizations)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    submissionTypes: ").append(toIndentedString(submissionTypes)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
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
