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

  @JsonProperty("challengeContributorRoles")
  @Valid
  private List<ChallengeContributorRoleDto> challengeContributorRoles = null;

  @JsonProperty("sort")
  private OrganizationSortDto sort = OrganizationSortDto.RELEVANCE;

  @JsonProperty("direction")
  private OrganizationDirectionDto direction = null;

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

  public OrganizationSearchQueryDto challengeContributorRoles(
      List<ChallengeContributorRoleDto> challengeContributorRoles) {
    this.challengeContributorRoles = challengeContributorRoles;
    return this;
  }

  public OrganizationSearchQueryDto addChallengeContributorRolesItem(
      ChallengeContributorRoleDto challengeContributorRolesItem) {
    if (this.challengeContributorRoles == null) {
      this.challengeContributorRoles = new ArrayList<>();
    }
    this.challengeContributorRoles.add(challengeContributorRolesItem);
    return this;
  }

  /**
   * An array of challenge contributor roles used to filter the results.
   *
   * @return challengeContributorRoles
   */
  @Valid
  @Schema(
      name = "challengeContributorRoles",
      description = "An array of challenge contributor roles used to filter the results.",
      required = false)
  public List<ChallengeContributorRoleDto> getChallengeContributorRoles() {
    return challengeContributorRoles;
  }

  public void setChallengeContributorRoles(
      List<ChallengeContributorRoleDto> challengeContributorRoles) {
    this.challengeContributorRoles = challengeContributorRoles;
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
        && Objects.equals(
            this.challengeContributorRoles, organizationSearchQuery.challengeContributorRoles)
        && Objects.equals(this.sort, organizationSearchQuery.sort)
        && Objects.equals(this.direction, organizationSearchQuery.direction)
        && Objects.equals(this.searchTerms, organizationSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        pageNumber, pageSize, challengeContributorRoles, sort, direction, searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    challengeContributorRoles: ")
        .append(toIndentedString(challengeContributorRoles))
        .append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
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
