package org.sagebionetworks.amp.als.dataset.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDirectionDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetSortDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A dataset search query.
 */

@Schema(name = "DatasetSearchQuery", description = "A dataset search query.")
@JsonTypeName("DatasetSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class DatasetSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private DatasetSortDto sort = DatasetSortDto.RELEVANCE;

  private @Nullable Integer sortSeed = null;

  private @Nullable DatasetDirectionDto direction = null;

  private @Nullable String searchTerms;

  public DatasetSearchQueryDto pageNumber(Integer pageNumber) {
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

  public DatasetSearchQueryDto pageSize(Integer pageSize) {
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

  public DatasetSearchQueryDto sort(DatasetSortDto sort) {
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
  public DatasetSortDto getSort() {
    return sort;
  }

  public void setSort(DatasetSortDto sort) {
    this.sort = sort;
  }

  public DatasetSearchQueryDto sortSeed(Integer sortSeed) {
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

  public DatasetSearchQueryDto direction(DatasetDirectionDto direction) {
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
  public DatasetDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(DatasetDirectionDto direction) {
    this.direction = direction;
  }

  public DatasetSearchQueryDto searchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
    return this;
  }

  /**
   * A string of search terms used to filter the results.
   * @return searchTerms
   */
  
  @Schema(name = "searchTerms", example = "clinical", description = "A string of search terms used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    DatasetSearchQueryDto datasetSearchQuery = (DatasetSearchQueryDto) o;
    return Objects.equals(this.pageNumber, datasetSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, datasetSearchQuery.pageSize) &&
        Objects.equals(this.sort, datasetSearchQuery.sort) &&
        Objects.equals(this.sortSeed, datasetSearchQuery.sortSeed) &&
        Objects.equals(this.direction, datasetSearchQuery.direction) &&
        Objects.equals(this.searchTerms, datasetSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, sortSeed, direction, searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DatasetSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    sortSeed: ").append(toIndentedString(sortSeed)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
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

    private DatasetSearchQueryDto instance;

    public Builder() {
      this(new DatasetSearchQueryDto());
    }

    protected Builder(DatasetSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DatasetSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setSortSeed(value.sortSeed);
      this.instance.setDirection(value.direction);
      this.instance.setSearchTerms(value.searchTerms);
      return this;
    }

    public DatasetSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public DatasetSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public DatasetSearchQueryDto.Builder sort(DatasetSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public DatasetSearchQueryDto.Builder sortSeed(Integer sortSeed) {
      this.instance.sortSeed(sortSeed);
      return this;
    }
    
    public DatasetSearchQueryDto.Builder direction(DatasetDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public DatasetSearchQueryDto.Builder searchTerms(String searchTerms) {
      this.instance.searchTerms(searchTerms);
      return this;
    }
    
    /**
    * returns a built DatasetSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DatasetSearchQueryDto build() {
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
  public static DatasetSearchQueryDto.Builder builder() {
    return new DatasetSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DatasetSearchQueryDto.Builder toBuilder() {
    DatasetSearchQueryDto.Builder builder = new DatasetSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

