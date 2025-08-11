package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A challenge search query.
 */

@Schema(name = "ChallengeSearchQuery", description = "A challenge search query.")
@JsonTypeName("ChallengeSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private ChallengeSortDto sort = ChallengeSortDto.RELEVANCE;

  private @Nullable Integer sortSeed = null;

  private @Nullable ChallengeDirectionDto direction = null;

  @Valid
  private List<ChallengeIncentiveDto> incentives = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate minStartDate = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate maxStartDate = null;

  @Valid
  private List<@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")@Size(min = 3, max = 30)String> platforms = new ArrayList<>();

  @Valid
  private List<Long> organizations = new ArrayList<>();

  @Valid
  private List<ChallengeStatusDto> status = new ArrayList<>();

  @Valid
  private List<ChallengeSubmissionTypeDto> submissionTypes = new ArrayList<>();

  @Valid
  private List<@Min(1)Integer> inputDataTypes = new ArrayList<>();

  @Valid
  private List<@Min(1)Integer> operations = new ArrayList<>();

  @Valid
  private List<ChallengeCategoryDto> categories = new ArrayList<>();

  private @Nullable String searchTerms;

  public ChallengeSearchQueryDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * The page number.
   * minimum: 0
   * @return pageNumber
   */
  @Min(0) 
  @Schema(name = "pageNumber", description = "The page number.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageNumber")
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
   * The number of items in a single page.
   * minimum: 1
   * @return pageSize
   */
  @Min(1) 
  @Schema(name = "pageSize", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
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
   * @return sort
   */
  @Valid 
  @Schema(name = "sort", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sort")
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
   * The seed that initializes the random sorter.
   * minimum: 0
   * maximum: 2147483647
   * @return sortSeed
   */
  @Min(0) @Max(2147483647) 
  @Schema(name = "sortSeed", description = "The seed that initializes the random sorter.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sortSeed")
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
   * @return direction
   */
  @Valid 
  @Schema(name = "direction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("direction")
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
   * @return incentives
   */
  @Valid 
  @Schema(name = "incentives", description = "An array of challenge incentive types used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("incentives")
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
   * @return minStartDate
   */
  @Valid 
  @Schema(name = "minStartDate", example = "2017-07-21", description = "Keep the challenges that start at this date or later.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minStartDate")
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
   * @return maxStartDate
   */
  @Valid 
  @Schema(name = "maxStartDate", example = "2017-07-21", description = "Keep the challenges that start at this date or sooner.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxStartDate")
  public LocalDate getMaxStartDate() {
    return maxStartDate;
  }

  public void setMaxStartDate(LocalDate maxStartDate) {
    this.maxStartDate = maxStartDate;
  }

  public ChallengeSearchQueryDto platforms(List<@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")@Size(min = 3, max = 30)String> platforms) {
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
   * @return platforms
   */
  
  @Schema(name = "platforms", description = "An array of challenge platform ids used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("platforms")
  public List<@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")@Size(min = 3, max = 30)String> getPlatforms() {
    return platforms;
  }

  public void setPlatforms(List<@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")@Size(min = 3, max = 30)String> platforms) {
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
   * @return organizations
   */
  
  @Schema(name = "organizations", description = "An array of organization ids used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("organizations")
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
   * @return status
   */
  @Valid 
  @Schema(name = "status", description = "An array of challenge status used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
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

  public ChallengeSearchQueryDto addSubmissionTypesItem(ChallengeSubmissionTypeDto submissionTypesItem) {
    if (this.submissionTypes == null) {
      this.submissionTypes = new ArrayList<>();
    }
    this.submissionTypes.add(submissionTypesItem);
    return this;
  }

  /**
   * An array of challenge submission types used to filter the results.
   * @return submissionTypes
   */
  @Valid 
  @Schema(name = "submissionTypes", description = "An array of challenge submission types used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("submissionTypes")
  public List<ChallengeSubmissionTypeDto> getSubmissionTypes() {
    return submissionTypes;
  }

  public void setSubmissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
  }

  public ChallengeSearchQueryDto inputDataTypes(List<@Min(1)Integer> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
    return this;
  }

  public ChallengeSearchQueryDto addInputDataTypesItem(Integer inputDataTypesItem) {
    if (this.inputDataTypes == null) {
      this.inputDataTypes = new ArrayList<>();
    }
    this.inputDataTypes.add(inputDataTypesItem);
    return this;
  }

  /**
   * An array of EDAM concept ID used to filter the results.
   * @return inputDataTypes
   */
  
  @Schema(name = "inputDataTypes", description = "An array of EDAM concept ID used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("inputDataTypes")
  public List<@Min(1)Integer> getInputDataTypes() {
    return inputDataTypes;
  }

  public void setInputDataTypes(List<@Min(1)Integer> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
  }

  public ChallengeSearchQueryDto operations(List<@Min(1)Integer> operations) {
    this.operations = operations;
    return this;
  }

  public ChallengeSearchQueryDto addOperationsItem(Integer operationsItem) {
    if (this.operations == null) {
      this.operations = new ArrayList<>();
    }
    this.operations.add(operationsItem);
    return this;
  }

  /**
   * An array of EDAM concept ID used to filter the results.
   * @return operations
   */
  
  @Schema(name = "operations", description = "An array of EDAM concept ID used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operations")
  public List<@Min(1)Integer> getOperations() {
    return operations;
  }

  public void setOperations(List<@Min(1)Integer> operations) {
    this.operations = operations;
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
   * @return categories
   */
  @Valid 
  @Schema(name = "categories", description = "The array of challenge categories used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("categories")
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
   * @return searchTerms
   */
  
  @Schema(name = "searchTerms", example = "dream challenge", description = "A string of search terms used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchTerms")
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
    return Objects.equals(this.pageNumber, challengeSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, challengeSearchQuery.pageSize) &&
        Objects.equals(this.sort, challengeSearchQuery.sort) &&
        Objects.equals(this.sortSeed, challengeSearchQuery.sortSeed) &&
        Objects.equals(this.direction, challengeSearchQuery.direction) &&
        Objects.equals(this.incentives, challengeSearchQuery.incentives) &&
        Objects.equals(this.minStartDate, challengeSearchQuery.minStartDate) &&
        Objects.equals(this.maxStartDate, challengeSearchQuery.maxStartDate) &&
        Objects.equals(this.platforms, challengeSearchQuery.platforms) &&
        Objects.equals(this.organizations, challengeSearchQuery.organizations) &&
        Objects.equals(this.status, challengeSearchQuery.status) &&
        Objects.equals(this.submissionTypes, challengeSearchQuery.submissionTypes) &&
        Objects.equals(this.inputDataTypes, challengeSearchQuery.inputDataTypes) &&
        Objects.equals(this.operations, challengeSearchQuery.operations) &&
        Objects.equals(this.categories, challengeSearchQuery.categories) &&
        Objects.equals(this.searchTerms, challengeSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, sortSeed, direction, incentives, minStartDate, maxStartDate, platforms, organizations, status, submissionTypes, inputDataTypes, operations, categories, searchTerms);
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
    sb.append("    inputDataTypes: ").append(toIndentedString(inputDataTypes)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    searchTerms: ").append(toIndentedString(searchTerms)).append("\n");
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

    private ChallengeSearchQueryDto instance;

    public Builder() {
      this(new ChallengeSearchQueryDto());
    }

    protected Builder(ChallengeSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setSortSeed(value.sortSeed);
      this.instance.setDirection(value.direction);
      this.instance.setIncentives(value.incentives);
      this.instance.setMinStartDate(value.minStartDate);
      this.instance.setMaxStartDate(value.maxStartDate);
      this.instance.setPlatforms(value.platforms);
      this.instance.setOrganizations(value.organizations);
      this.instance.setStatus(value.status);
      this.instance.setSubmissionTypes(value.submissionTypes);
      this.instance.setInputDataTypes(value.inputDataTypes);
      this.instance.setOperations(value.operations);
      this.instance.setCategories(value.categories);
      this.instance.setSearchTerms(value.searchTerms);
      return this;
    }

    public ChallengeSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder sort(ChallengeSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder sortSeed(Integer sortSeed) {
      this.instance.sortSeed(sortSeed);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder direction(ChallengeDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder incentives(List<ChallengeIncentiveDto> incentives) {
      this.instance.incentives(incentives);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder minStartDate(LocalDate minStartDate) {
      this.instance.minStartDate(minStartDate);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder maxStartDate(LocalDate maxStartDate) {
      this.instance.maxStartDate(maxStartDate);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder platforms(List<String> platforms) {
      this.instance.platforms(platforms);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder organizations(List<Long> organizations) {
      this.instance.organizations(organizations);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder status(List<ChallengeStatusDto> status) {
      this.instance.status(status);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder submissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
      this.instance.submissionTypes(submissionTypes);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder inputDataTypes(List<Integer> inputDataTypes) {
      this.instance.inputDataTypes(inputDataTypes);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder operations(List<Integer> operations) {
      this.instance.operations(operations);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder categories(List<ChallengeCategoryDto> categories) {
      this.instance.categories(categories);
      return this;
    }
    
    public ChallengeSearchQueryDto.Builder searchTerms(String searchTerms) {
      this.instance.searchTerms(searchTerms);
      return this;
    }
    
    /**
    * returns a built ChallengeSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeSearchQueryDto build() {
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
  public static ChallengeSearchQueryDto.Builder builder() {
    return new ChallengeSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeSearchQueryDto.Builder toBuilder() {
    ChallengeSearchQueryDto.Builder builder = new ChallengeSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

