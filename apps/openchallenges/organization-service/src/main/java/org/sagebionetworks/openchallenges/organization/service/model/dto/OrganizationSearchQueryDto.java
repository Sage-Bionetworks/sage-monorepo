package org.sagebionetworks.openchallenges.organization.service.model.dto;

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

/** An organization search query. */
@Schema(name = "OrganizationSearchQuery", description = "An organization search query.")
@JsonTypeName("OrganizationSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class OrganizationSearchQueryDto {

  @JsonProperty("pageNumber")
  private Integer pageNumber = 0;

  @JsonProperty("pageSize")
  private Integer pageSize = 100;

  @JsonProperty("categories")
  @Valid
  private List<OrganizationCategoryDto> categories = null;

  @JsonProperty("challengeContributionRoles")
  @Valid
  private List<ChallengeContributionRoleDto> challengeContributionRoles = null;

  @JsonProperty("sort")
  private OrganizationSortDto sort = OrganizationSortDto.RELEVANCE;

  @JsonProperty("direction")
  private OrganizationDirectionDto direction = null;

  @JsonProperty("ids")
  @Valid
  private List<Long> ids = null;

  @JsonProperty("searchTerms")
  private String searchTerms;

  public OrganizationSearchQueryDto pageNumber(Integer pageNumber) {
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

  public OrganizationSearchQueryDto pageSize(Integer pageSize) {
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

  public OrganizationSearchQueryDto categories(List<OrganizationCategoryDto> categories) {
    this.categories = categories;
    return this;
  }

  public OrganizationSearchQueryDto addCategoriesItem(OrganizationCategoryDto categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * The array of organization categories used to filter the results.
   *
   * @return categories
   */
  @Valid
  @Schema(
      name = "categories",
      description = "The array of organization categories used to filter the results.",
      required = false)
  public List<OrganizationCategoryDto> getCategories() {
    return categories;
  }

  public void setCategories(List<OrganizationCategoryDto> categories) {
    this.categories = categories;
  }

  public OrganizationSearchQueryDto challengeContributionRoles(
      List<ChallengeContributionRoleDto> challengeContributionRoles) {
    this.challengeContributionRoles = challengeContributionRoles;
    return this;
  }

  public OrganizationSearchQueryDto addChallengeContributionRolesItem(
      ChallengeContributionRoleDto challengeContributionRolesItem) {
    if (this.challengeContributionRoles == null) {
      this.challengeContributionRoles = new ArrayList<>();
    }
    this.challengeContributionRoles.add(challengeContributionRolesItem);
    return this;
  }

  /**
   * An array of challenge contribution roles used to filter the results.
   *
   * @return challengeContributionRoles
   */
  @Valid
  @Schema(
      name = "challengeContributionRoles",
      description = "An array of challenge contribution roles used to filter the results.",
      required = false)
  public List<ChallengeContributionRoleDto> getChallengeContributionRoles() {
    return challengeContributionRoles;
  }

  public void setChallengeContributionRoles(
      List<ChallengeContributionRoleDto> challengeContributionRoles) {
    this.challengeContributionRoles = challengeContributionRoles;
  }

  public OrganizationSearchQueryDto sort(OrganizationSortDto sort) {
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
  public OrganizationSortDto getSort() {
    return sort;
  }

  public void setSort(OrganizationSortDto sort) {
    this.sort = sort;
  }

  public OrganizationSearchQueryDto direction(OrganizationDirectionDto direction) {
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
  public OrganizationDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(OrganizationDirectionDto direction) {
    this.direction = direction;
  }

  public OrganizationSearchQueryDto ids(List<Long> ids) {
    this.ids = ids;
    return this;
  }

  public OrganizationSearchQueryDto addIdsItem(Long idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * An array of organization ids used to filter the results.
   *
   * @return ids
   */
  @Schema(
      name = "ids",
      description = "An array of organization ids used to filter the results.",
      required = false)
  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

  public OrganizationSearchQueryDto searchTerms(String searchTerms) {
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
      example = "data provider",
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
    OrganizationSearchQueryDto organizationSearchQuery = (OrganizationSearchQueryDto) o;
    return Objects.equals(this.pageNumber, organizationSearchQuery.pageNumber)
        && Objects.equals(this.pageSize, organizationSearchQuery.pageSize)
        && Objects.equals(this.categories, organizationSearchQuery.categories)
        && Objects.equals(
            this.challengeContributionRoles, organizationSearchQuery.challengeContributionRoles)
        && Objects.equals(this.sort, organizationSearchQuery.sort)
        && Objects.equals(this.direction, organizationSearchQuery.direction)
        && Objects.equals(this.ids, organizationSearchQuery.ids)
        && Objects.equals(this.searchTerms, organizationSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        pageNumber,
        pageSize,
        categories,
        challengeContributionRoles,
        sort,
        direction,
        ids,
        searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    challengeContributionRoles: ")
        .append(toIndentedString(challengeContributionRoles))
        .append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
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
