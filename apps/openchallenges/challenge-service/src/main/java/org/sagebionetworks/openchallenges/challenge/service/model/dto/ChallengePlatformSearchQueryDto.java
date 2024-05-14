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

/** A challenge platform search query. */
@Schema(name = "ChallengePlatformSearchQuery", description = "A challenge platform search query.")
@JsonTypeName("ChallengePlatformSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengePlatformSearchQueryDto {

  @JsonProperty("pageNumber")
  private Integer pageNumber = 0;

  @JsonProperty("pageSize")
  private Integer pageSize = 100;

  @JsonProperty("sort")
  private ChallengePlatformSortDto sort = ChallengePlatformSortDto.RELEVANCE;

  @JsonProperty("direction")
  private ChallengePlatformDirectionDto direction = null;

  @JsonProperty("ids")
  @Valid
  private List<Long> ids = null;

  @JsonProperty("searchTerms")
  private String searchTerms;

  public ChallengePlatformSearchQueryDto pageNumber(Integer pageNumber) {
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

  public ChallengePlatformSearchQueryDto pageSize(Integer pageSize) {
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

  public ChallengePlatformSearchQueryDto sort(ChallengePlatformSortDto sort) {
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
  public ChallengePlatformSortDto getSort() {
    return sort;
  }

  public void setSort(ChallengePlatformSortDto sort) {
    this.sort = sort;
  }

  public ChallengePlatformSearchQueryDto direction(ChallengePlatformDirectionDto direction) {
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
  public ChallengePlatformDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(ChallengePlatformDirectionDto direction) {
    this.direction = direction;
  }

  public ChallengePlatformSearchQueryDto ids(List<Long> ids) {
    this.ids = ids;
    return this;
  }

  public ChallengePlatformSearchQueryDto addIdsItem(Long idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * An array of challenge platform ids used to filter the results.
   *
   * @return ids
   */
  @Schema(
      name = "ids",
      description = "An array of challenge platform ids used to filter the results.",
      required = false)
  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

  public ChallengePlatformSearchQueryDto searchTerms(String searchTerms) {
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
      example = "synapse",
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
    ChallengePlatformSearchQueryDto challengePlatformSearchQuery =
        (ChallengePlatformSearchQueryDto) o;
    return Objects.equals(this.pageNumber, challengePlatformSearchQuery.pageNumber)
        && Objects.equals(this.pageSize, challengePlatformSearchQuery.pageSize)
        && Objects.equals(this.sort, challengePlatformSearchQuery.sort)
        && Objects.equals(this.direction, challengePlatformSearchQuery.direction)
        && Objects.equals(this.ids, challengePlatformSearchQuery.ids)
        && Objects.equals(this.searchTerms, challengePlatformSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, ids, searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
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
