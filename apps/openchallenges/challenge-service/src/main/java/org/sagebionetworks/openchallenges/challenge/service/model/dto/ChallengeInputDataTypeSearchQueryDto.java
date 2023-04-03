package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** A challenge input data type search query. */
@Schema(
    name = "ChallengeInputDataTypeSearchQuery",
    description = "A challenge input data type search query.")
@JsonTypeName("ChallengeInputDataTypeSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengeInputDataTypeSearchQueryDto {

  @JsonProperty("pageNumber")
  private Integer pageNumber = 0;

  @JsonProperty("pageSize")
  private Integer pageSize = 100;

  @JsonProperty("sort")
  private ChallengeInputDataTypeSortDto sort = ChallengeInputDataTypeSortDto.RELEVANCE;

  @JsonProperty("direction")
  private ChallengeInputDataTypeDirectionDto direction = null;

  @JsonProperty("searchTerms")
  private String searchTerms;

  public ChallengeInputDataTypeSearchQueryDto pageNumber(Integer pageNumber) {
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

  public ChallengeInputDataTypeSearchQueryDto pageSize(Integer pageSize) {
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

  public ChallengeInputDataTypeSearchQueryDto sort(ChallengeInputDataTypeSortDto sort) {
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
  public ChallengeInputDataTypeSortDto getSort() {
    return sort;
  }

  public void setSort(ChallengeInputDataTypeSortDto sort) {
    this.sort = sort;
  }

  public ChallengeInputDataTypeSearchQueryDto direction(
      ChallengeInputDataTypeDirectionDto direction) {
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
  public ChallengeInputDataTypeDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(ChallengeInputDataTypeDirectionDto direction) {
    this.direction = direction;
  }

  public ChallengeInputDataTypeSearchQueryDto searchTerms(String searchTerms) {
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
      example = "genomic",
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
    ChallengeInputDataTypeSearchQueryDto challengeInputDataTypeSearchQuery =
        (ChallengeInputDataTypeSearchQueryDto) o;
    return Objects.equals(this.pageNumber, challengeInputDataTypeSearchQuery.pageNumber)
        && Objects.equals(this.pageSize, challengeInputDataTypeSearchQuery.pageSize)
        && Objects.equals(this.sort, challengeInputDataTypeSearchQuery.sort)
        && Objects.equals(this.direction, challengeInputDataTypeSearchQuery.direction)
        && Objects.equals(this.searchTerms, challengeInputDataTypeSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeInputDataTypeSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
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
