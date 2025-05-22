package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaModelDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.SortDirectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A differential expression profile search query (RNA).
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "DifferentialExpressionProfileRnaSearchQuery", description = "A differential expression profile search query (RNA).")
@JsonTypeName("DifferentialExpressionProfileRnaSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class DifferentialExpressionProfileRnaSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private DifferentialExpressionProfileRnaSortDto sort = DifferentialExpressionProfileRnaSortDto.RELEVANCE;

  private @Nullable SortDirectionDto direction = null;

  private DifferentialExpressionProfileRnaModelDto model = DifferentialExpressionProfileRnaModelDto.AD_DIAGNOSIS_AOD_MALES_AND_FEMALES;

  private @Nullable String searchTerms;

  public DifferentialExpressionProfileRnaSearchQueryDto pageNumber(Integer pageNumber) {
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

  public DifferentialExpressionProfileRnaSearchQueryDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * The number of items in a single page.
   * minimum: 1
   * maximum: 100
   * @return pageSize
   */
  @Min(1) @Max(100) 
  @Schema(name = "page_size", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("page_size")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public DifferentialExpressionProfileRnaSearchQueryDto sort(DifferentialExpressionProfileRnaSortDto sort) {
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
  public DifferentialExpressionProfileRnaSortDto getSort() {
    return sort;
  }

  public void setSort(DifferentialExpressionProfileRnaSortDto sort) {
    this.sort = sort;
  }

  public DifferentialExpressionProfileRnaSearchQueryDto direction(SortDirectionDto direction) {
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

  public DifferentialExpressionProfileRnaSearchQueryDto model(DifferentialExpressionProfileRnaModelDto model) {
    this.model = model;
    return this;
  }

  /**
   * Get model
   * @return model
   */
  @Valid 
  @Schema(name = "model", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("model")
  public DifferentialExpressionProfileRnaModelDto getModel() {
    return model;
  }

  public void setModel(DifferentialExpressionProfileRnaModelDto model) {
    this.model = model;
  }

  public DifferentialExpressionProfileRnaSearchQueryDto searchTerms(String searchTerms) {
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
    DifferentialExpressionProfileRnaSearchQueryDto differentialExpressionProfileRnaSearchQuery = (DifferentialExpressionProfileRnaSearchQueryDto) o;
    return Objects.equals(this.pageNumber, differentialExpressionProfileRnaSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, differentialExpressionProfileRnaSearchQuery.pageSize) &&
        Objects.equals(this.sort, differentialExpressionProfileRnaSearchQuery.sort) &&
        Objects.equals(this.direction, differentialExpressionProfileRnaSearchQuery.direction) &&
        Objects.equals(this.model, differentialExpressionProfileRnaSearchQuery.model) &&
        Objects.equals(this.searchTerms, differentialExpressionProfileRnaSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, model, searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DifferentialExpressionProfileRnaSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
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

    private DifferentialExpressionProfileRnaSearchQueryDto instance;

    public Builder() {
      this(new DifferentialExpressionProfileRnaSearchQueryDto());
    }

    protected Builder(DifferentialExpressionProfileRnaSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DifferentialExpressionProfileRnaSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setModel(value.model);
      this.instance.setSearchTerms(value.searchTerms);
      return this;
    }

    public DifferentialExpressionProfileRnaSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public DifferentialExpressionProfileRnaSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public DifferentialExpressionProfileRnaSearchQueryDto.Builder sort(DifferentialExpressionProfileRnaSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public DifferentialExpressionProfileRnaSearchQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public DifferentialExpressionProfileRnaSearchQueryDto.Builder model(DifferentialExpressionProfileRnaModelDto model) {
      this.instance.model(model);
      return this;
    }
    
    public DifferentialExpressionProfileRnaSearchQueryDto.Builder searchTerms(String searchTerms) {
      this.instance.searchTerms(searchTerms);
      return this;
    }
    
    /**
    * returns a built DifferentialExpressionProfileRnaSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DifferentialExpressionProfileRnaSearchQueryDto build() {
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
  public static DifferentialExpressionProfileRnaSearchQueryDto.Builder builder() {
    return new DifferentialExpressionProfileRnaSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DifferentialExpressionProfileRnaSearchQueryDto.Builder toBuilder() {
    DifferentialExpressionProfileRnaSearchQueryDto.Builder builder = new DifferentialExpressionProfileRnaSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

