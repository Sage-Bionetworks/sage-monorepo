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

/** An EDAM concept search query. */
@Schema(name = "EdamConceptSearchQuery", description = "An EDAM concept search query.")
@JsonTypeName("EdamConceptSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class EdamConceptSearchQueryDto {

  @JsonProperty("pageNumber")
  private Integer pageNumber = 0;

  @JsonProperty("pageSize")
  private Integer pageSize = 100;

  @JsonProperty("sort")
  private EdamConceptSortDto sort = EdamConceptSortDto.RELEVANCE;

  @JsonProperty("direction")
  private EdamConceptDirectionDto direction = null;

  @JsonProperty("ids")
  @Valid
  private List<Long> ids = null;

  @JsonProperty("searchTerms")
  private String searchTerms;

  @JsonProperty("sections")
  @Valid
  private List<EdamSectionDto> sections = null;

  public EdamConceptSearchQueryDto pageNumber(Integer pageNumber) {
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

  public EdamConceptSearchQueryDto pageSize(Integer pageSize) {
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

  public EdamConceptSearchQueryDto sort(EdamConceptSortDto sort) {
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
  public EdamConceptSortDto getSort() {
    return sort;
  }

  public void setSort(EdamConceptSortDto sort) {
    this.sort = sort;
  }

  public EdamConceptSearchQueryDto direction(EdamConceptDirectionDto direction) {
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
  public EdamConceptDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(EdamConceptDirectionDto direction) {
    this.direction = direction;
  }

  public EdamConceptSearchQueryDto ids(List<Long> ids) {
    this.ids = ids;
    return this;
  }

  public EdamConceptSearchQueryDto addIdsItem(Long idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * An array of EDAM concept ids used to filter the results.
   *
   * @return ids
   */
  @Schema(
      name = "ids",
      description = "An array of EDAM concept ids used to filter the results.",
      required = false)
  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

  public EdamConceptSearchQueryDto searchTerms(String searchTerms) {
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
      example = "sequence image",
      description = "A string of search terms used to filter the results.",
      required = false)
  public String getSearchTerms() {
    return searchTerms;
  }

  public void setSearchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
  }

  public EdamConceptSearchQueryDto sections(List<EdamSectionDto> sections) {
    this.sections = sections;
    return this;
  }

  public EdamConceptSearchQueryDto addSectionsItem(EdamSectionDto sectionsItem) {
    if (this.sections == null) {
      this.sections = new ArrayList<>();
    }
    this.sections.add(sectionsItem);
    return this;
  }

  /**
   * An array of EDAM sections (sub-ontologies) used to filter the results.
   *
   * @return sections
   */
  @Valid
  @Schema(
      name = "sections",
      description = "An array of EDAM sections (sub-ontologies) used to filter the results.",
      required = false)
  public List<EdamSectionDto> getSections() {
    return sections;
  }

  public void setSections(List<EdamSectionDto> sections) {
    this.sections = sections;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EdamConceptSearchQueryDto edamConceptSearchQuery = (EdamConceptSearchQueryDto) o;
    return Objects.equals(this.pageNumber, edamConceptSearchQuery.pageNumber)
        && Objects.equals(this.pageSize, edamConceptSearchQuery.pageSize)
        && Objects.equals(this.sort, edamConceptSearchQuery.sort)
        && Objects.equals(this.direction, edamConceptSearchQuery.direction)
        && Objects.equals(this.ids, edamConceptSearchQuery.ids)
        && Objects.equals(this.searchTerms, edamConceptSearchQuery.searchTerms)
        && Objects.equals(this.sections, edamConceptSearchQuery.sections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, ids, searchTerms, sections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EdamConceptSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
    sb.append("    searchTerms: ").append(toIndentedString(searchTerms)).append("\n");
    sb.append("    sections: ").append(toIndentedString(sections)).append("\n");
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
