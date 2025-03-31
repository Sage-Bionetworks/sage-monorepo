package org.sagebionetworks.amp.als.dataset.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDto;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * A page of datasets.
 */

@Schema(name = "DatasetsPage", description = "A page of datasets.")
@JsonTypeName("DatasetsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@lombok.Builder
public class DatasetsPageDto {

  @JsonProperty("number")
  private Integer number;

  @JsonProperty("size")
  private Integer size;

  @JsonProperty("totalElements")
  private Long totalElements;

  @JsonProperty("totalPages")
  private Integer totalPages;

  @JsonProperty("hasNext")
  private Boolean hasNext;

  @JsonProperty("hasPrevious")
  private Boolean hasPrevious;

  @JsonProperty("datasets")
  @Valid
  private List<DatasetDto> datasets = new ArrayList<>();

  public DatasetsPageDto number(Integer number) {
    this.number = number;
    return this;
  }

  /**
   * The page number.
   * @return number
  */
  @NotNull 
  @Schema(name = "number", example = "99", description = "The page number.", required = true)
  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public DatasetsPageDto size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * The number of items in a single page.
   * @return size
  */
  @NotNull 
  @Schema(name = "size", example = "99", description = "The number of items in a single page.", required = true)
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public DatasetsPageDto totalElements(Long totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Total number of elements in the result set.
   * @return totalElements
  */
  @NotNull 
  @Schema(name = "totalElements", example = "99", description = "Total number of elements in the result set.", required = true)
  public Long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  public DatasetsPageDto totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Total number of pages in the result set.
   * @return totalPages
  */
  @NotNull 
  @Schema(name = "totalPages", example = "99", description = "Total number of pages in the result set.", required = true)
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public DatasetsPageDto hasNext(Boolean hasNext) {
    this.hasNext = hasNext;
    return this;
  }

  /**
   * Returns if there is a next page.
   * @return hasNext
  */
  @NotNull 
  @Schema(name = "hasNext", example = "true", description = "Returns if there is a next page.", required = true)
  public Boolean getHasNext() {
    return hasNext;
  }

  public void setHasNext(Boolean hasNext) {
    this.hasNext = hasNext;
  }

  public DatasetsPageDto hasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
    return this;
  }

  /**
   * Returns if there is a previous page.
   * @return hasPrevious
  */
  @NotNull 
  @Schema(name = "hasPrevious", example = "true", description = "Returns if there is a previous page.", required = true)
  public Boolean getHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }

  public DatasetsPageDto datasets(List<DatasetDto> datasets) {
    this.datasets = datasets;
    return this;
  }

  public DatasetsPageDto addDatasetsItem(DatasetDto datasetsItem) {
    if (this.datasets == null) {
      this.datasets = new ArrayList<>();
    }
    this.datasets.add(datasetsItem);
    return this;
  }

  /**
   * A list of datasets.
   * @return datasets
  */
  @NotNull @Valid 
  @Schema(name = "datasets", description = "A list of datasets.", required = true)
  public List<DatasetDto> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<DatasetDto> datasets) {
    this.datasets = datasets;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DatasetsPageDto datasetsPage = (DatasetsPageDto) o;
    return Objects.equals(this.number, datasetsPage.number) &&
        Objects.equals(this.size, datasetsPage.size) &&
        Objects.equals(this.totalElements, datasetsPage.totalElements) &&
        Objects.equals(this.totalPages, datasetsPage.totalPages) &&
        Objects.equals(this.hasNext, datasetsPage.hasNext) &&
        Objects.equals(this.hasPrevious, datasetsPage.hasPrevious) &&
        Objects.equals(this.datasets, datasetsPage.datasets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, datasets);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DatasetsPageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    datasets: ").append(toIndentedString(datasets)).append("\n");
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
}

