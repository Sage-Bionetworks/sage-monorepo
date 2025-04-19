package org.sagebionetworks.amp.als.dataset.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of datasets.
 */

@Schema(name = "DatasetsPage", description = "A page of datasets.")
@JsonTypeName("DatasetsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class DatasetsPageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  @Valid
  private List<@Valid DatasetDto> datasets = new ArrayList<>();

  public DatasetsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DatasetsPageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious, List<@Valid DatasetDto> datasets) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.datasets = datasets;
  }

  public DatasetsPageDto number(Integer number) {
    this.number = number;
    return this;
  }

  /**
   * The page number.
   * @return number
   */
  @NotNull 
  @Schema(name = "number", example = "99", description = "The page number.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("number")
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
  @Schema(name = "size", example = "99", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("size")
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
  @Schema(name = "totalElements", example = "99", description = "Total number of elements in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalElements")
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
  @Schema(name = "totalPages", example = "99", description = "Total number of pages in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalPages")
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
  @Schema(name = "hasNext", example = "true", description = "Returns if there is a next page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hasNext")
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
  @Schema(name = "hasPrevious", example = "true", description = "Returns if there is a previous page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hasPrevious")
  public Boolean getHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }

  public DatasetsPageDto datasets(List<@Valid DatasetDto> datasets) {
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
  @Schema(name = "datasets", description = "A list of datasets.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("datasets")
  public List<@Valid DatasetDto> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<@Valid DatasetDto> datasets) {
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
  
  public static class Builder {

    private DatasetsPageDto instance;

    public Builder() {
      this(new DatasetsPageDto());
    }

    protected Builder(DatasetsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DatasetsPageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setDatasets(value.datasets);
      return this;
    }

    public DatasetsPageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public DatasetsPageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public DatasetsPageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public DatasetsPageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public DatasetsPageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public DatasetsPageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public DatasetsPageDto.Builder datasets(List<DatasetDto> datasets) {
      this.instance.datasets(datasets);
      return this;
    }
    
    /**
    * returns a built DatasetsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DatasetsPageDto build() {
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
  public static DatasetsPageDto.Builder builder() {
    return new DatasetsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DatasetsPageDto.Builder toBuilder() {
    DatasetsPageDto.Builder builder = new DatasetsPageDto.Builder();
    return builder.copyOf(this);
  }

}

