package org.sagebionetworks.challenge.model.dto;

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

/** TODO Add schema description */
@Schema(name = "Pageable", description = "TODO Add schema description")
@JsonTypeName("Pageable")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class PageableDto {

  @JsonProperty("page")
  private Integer page = 0;

  @JsonProperty("size")
  private Integer size = 20;

  @JsonProperty("sort")
  @Valid
  private List<String> sort = null;

  public PageableDto page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Zero-based page index (0..N) minimum: 0
   *
   * @return page
   */
  @Min(0)
  @Schema(name = "page", description = "Zero-based page index (0..N)", required = false)
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public PageableDto size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * The size of the page to be returned minimum: 1
   *
   * @return size
   */
  @Min(1)
  @Schema(name = "size", description = "The size of the page to be returned", required = false)
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public PageableDto sort(List<String> sort) {
    this.sort = sort;
    return this;
  }

  public PageableDto addSortItem(String sortItem) {
    if (this.sort == null) {
      this.sort = new ArrayList<>();
    }
    this.sort.add(sortItem);
    return this;
  }

  /**
   * Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple
   * sort criteria are supported.
   *
   * @return sort
   */
  @Schema(
      name = "sort",
      example = "id,asc",
      description =
          "Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.",
      required = false)
  public List<String> getSort() {
    return sort;
  }

  public void setSort(List<String> sort) {
    this.sort = sort;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageableDto pageable = (PageableDto) o;
    return Objects.equals(this.page, pageable.page)
        && Objects.equals(this.size, pageable.size)
        && Objects.equals(this.sort, pageable.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size, sort);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageableDto {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
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
