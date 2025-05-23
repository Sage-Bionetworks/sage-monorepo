package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.agora.gene.api.model.dto.ProteinDifferentialExpressionProfileProfilingMethodDto;
import org.sagebionetworks.agora.gene.api.model.dto.ProteinDifferentialExpressionProfileSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.SortDirectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A differential protein expression profile search query.
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "ProteinDifferentialExpressionProfileSearchQuery", description = "A differential protein expression profile search query.")
@JsonTypeName("ProteinDifferentialExpressionProfileSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ProteinDifferentialExpressionProfileSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private ProteinDifferentialExpressionProfileSortDto sort = ProteinDifferentialExpressionProfileSortDto.HGNC_SYMBOL;

  private @Nullable SortDirectionDto direction = null;

  private ProteinDifferentialExpressionProfileProfilingMethodDto profilingMethod = ProteinDifferentialExpressionProfileProfilingMethodDto.LFQ;

  private @Nullable String searchTerms;

  public ProteinDifferentialExpressionProfileSearchQueryDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * The page number.
   * minimum: 0
   * @return pageNumber
   */
  @Min(0) 
  @Schema(name = "page_number", description = "The page number.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("page_number")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public ProteinDifferentialExpressionProfileSearchQueryDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * The number of items in a single page.
   * minimum: 1
   * @return pageSize
   */
  @Min(1) 
  @Schema(name = "page_size", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("page_size")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public ProteinDifferentialExpressionProfileSearchQueryDto sort(ProteinDifferentialExpressionProfileSortDto sort) {
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
  public ProteinDifferentialExpressionProfileSortDto getSort() {
    return sort;
  }

  public void setSort(ProteinDifferentialExpressionProfileSortDto sort) {
    this.sort = sort;
  }

  public ProteinDifferentialExpressionProfileSearchQueryDto direction(SortDirectionDto direction) {
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
  public SortDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(SortDirectionDto direction) {
    this.direction = direction;
  }

  public ProteinDifferentialExpressionProfileSearchQueryDto profilingMethod(ProteinDifferentialExpressionProfileProfilingMethodDto profilingMethod) {
    this.profilingMethod = profilingMethod;
    return this;
  }

  /**
   * Get profilingMethod
   * @return profilingMethod
   */
  @Valid 
  @Schema(name = "profiling_method", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profiling_method")
  public ProteinDifferentialExpressionProfileProfilingMethodDto getProfilingMethod() {
    return profilingMethod;
  }

  public void setProfilingMethod(ProteinDifferentialExpressionProfileProfilingMethodDto profilingMethod) {
    this.profilingMethod = profilingMethod;
  }

  public ProteinDifferentialExpressionProfileSearchQueryDto searchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
    return this;
  }

  /**
   * A string of search terms used to filter the results.
   * @return searchTerms
   */
  
  @Schema(name = "search_terms", example = "A1BG", description = "A string of search terms used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search_terms")
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
    ProteinDifferentialExpressionProfileSearchQueryDto proteinDifferentialExpressionProfileSearchQuery = (ProteinDifferentialExpressionProfileSearchQueryDto) o;
    return Objects.equals(this.pageNumber, proteinDifferentialExpressionProfileSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, proteinDifferentialExpressionProfileSearchQuery.pageSize) &&
        Objects.equals(this.sort, proteinDifferentialExpressionProfileSearchQuery.sort) &&
        Objects.equals(this.direction, proteinDifferentialExpressionProfileSearchQuery.direction) &&
        Objects.equals(this.profilingMethod, proteinDifferentialExpressionProfileSearchQuery.profilingMethod) &&
        Objects.equals(this.searchTerms, proteinDifferentialExpressionProfileSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, profilingMethod, searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProteinDifferentialExpressionProfileSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    profilingMethod: ").append(toIndentedString(profilingMethod)).append("\n");
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

    private ProteinDifferentialExpressionProfileSearchQueryDto instance;

    public Builder() {
      this(new ProteinDifferentialExpressionProfileSearchQueryDto());
    }

    protected Builder(ProteinDifferentialExpressionProfileSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ProteinDifferentialExpressionProfileSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setProfilingMethod(value.profilingMethod);
      this.instance.setSearchTerms(value.searchTerms);
      return this;
    }

    public ProteinDifferentialExpressionProfileSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public ProteinDifferentialExpressionProfileSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public ProteinDifferentialExpressionProfileSearchQueryDto.Builder sort(ProteinDifferentialExpressionProfileSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public ProteinDifferentialExpressionProfileSearchQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public ProteinDifferentialExpressionProfileSearchQueryDto.Builder profilingMethod(ProteinDifferentialExpressionProfileProfilingMethodDto profilingMethod) {
      this.instance.profilingMethod(profilingMethod);
      return this;
    }
    
    public ProteinDifferentialExpressionProfileSearchQueryDto.Builder searchTerms(String searchTerms) {
      this.instance.searchTerms(searchTerms);
      return this;
    }
    
    /**
    * returns a built ProteinDifferentialExpressionProfileSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ProteinDifferentialExpressionProfileSearchQueryDto build() {
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
  public static ProteinDifferentialExpressionProfileSearchQueryDto.Builder builder() {
    return new ProteinDifferentialExpressionProfileSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ProteinDifferentialExpressionProfileSearchQueryDto.Builder toBuilder() {
    ProteinDifferentialExpressionProfileSearchQueryDto.Builder builder = new ProteinDifferentialExpressionProfileSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

