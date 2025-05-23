package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileModelDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.SortDirectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A differential RNA expression profile search query.
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "RnaDifferentialExpressionProfileSearchQuery", description = "A differential RNA expression profile search query.")
@JsonTypeName("RnaDifferentialExpressionProfileSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class RnaDifferentialExpressionProfileSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private RnaDifferentialExpressionProfileSortDto sort = RnaDifferentialExpressionProfileSortDto.HGNC_SYMBOL;

  private @Nullable SortDirectionDto direction = null;

  private RnaDifferentialExpressionProfileModelDto model = RnaDifferentialExpressionProfileModelDto.AD_DIAGNOSIS_AOD_MALES_AND_FEMALES;

  private @Nullable String searchTerm;

  public RnaDifferentialExpressionProfileSearchQueryDto pageNumber(Integer pageNumber) {
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

  public RnaDifferentialExpressionProfileSearchQueryDto pageSize(Integer pageSize) {
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
  @Schema(name = "pageSize", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public RnaDifferentialExpressionProfileSearchQueryDto sort(RnaDifferentialExpressionProfileSortDto sort) {
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
  public RnaDifferentialExpressionProfileSortDto getSort() {
    return sort;
  }

  public void setSort(RnaDifferentialExpressionProfileSortDto sort) {
    this.sort = sort;
  }

  public RnaDifferentialExpressionProfileSearchQueryDto direction(SortDirectionDto direction) {
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

  public RnaDifferentialExpressionProfileSearchQueryDto model(RnaDifferentialExpressionProfileModelDto model) {
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
  public RnaDifferentialExpressionProfileModelDto getModel() {
    return model;
  }

  public void setModel(RnaDifferentialExpressionProfileModelDto model) {
    this.model = model;
  }

  public RnaDifferentialExpressionProfileSearchQueryDto searchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
    return this;
  }

  /**
   * A search term used to filter the results.
   * @return searchTerm
   */
  
  @Schema(name = "searchTerm", example = "A1BG", description = "A search term used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchTerm")
  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RnaDifferentialExpressionProfileSearchQueryDto rnaDifferentialExpressionProfileSearchQuery = (RnaDifferentialExpressionProfileSearchQueryDto) o;
    return Objects.equals(this.pageNumber, rnaDifferentialExpressionProfileSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, rnaDifferentialExpressionProfileSearchQuery.pageSize) &&
        Objects.equals(this.sort, rnaDifferentialExpressionProfileSearchQuery.sort) &&
        Objects.equals(this.direction, rnaDifferentialExpressionProfileSearchQuery.direction) &&
        Objects.equals(this.model, rnaDifferentialExpressionProfileSearchQuery.model) &&
        Objects.equals(this.searchTerm, rnaDifferentialExpressionProfileSearchQuery.searchTerm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, model, searchTerm);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RnaDifferentialExpressionProfileSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    searchTerm: ").append(toIndentedString(searchTerm)).append("\n");
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

    private RnaDifferentialExpressionProfileSearchQueryDto instance;

    public Builder() {
      this(new RnaDifferentialExpressionProfileSearchQueryDto());
    }

    protected Builder(RnaDifferentialExpressionProfileSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(RnaDifferentialExpressionProfileSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setModel(value.model);
      this.instance.setSearchTerm(value.searchTerm);
      return this;
    }

    public RnaDifferentialExpressionProfileSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public RnaDifferentialExpressionProfileSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public RnaDifferentialExpressionProfileSearchQueryDto.Builder sort(RnaDifferentialExpressionProfileSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public RnaDifferentialExpressionProfileSearchQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public RnaDifferentialExpressionProfileSearchQueryDto.Builder model(RnaDifferentialExpressionProfileModelDto model) {
      this.instance.model(model);
      return this;
    }
    
    public RnaDifferentialExpressionProfileSearchQueryDto.Builder searchTerm(String searchTerm) {
      this.instance.searchTerm(searchTerm);
      return this;
    }
    
    /**
    * returns a built RnaDifferentialExpressionProfileSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public RnaDifferentialExpressionProfileSearchQueryDto build() {
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
  public static RnaDifferentialExpressionProfileSearchQueryDto.Builder builder() {
    return new RnaDifferentialExpressionProfileSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public RnaDifferentialExpressionProfileSearchQueryDto.Builder toBuilder() {
    RnaDifferentialExpressionProfileSearchQueryDto.Builder builder = new RnaDifferentialExpressionProfileSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

