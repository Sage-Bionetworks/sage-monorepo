package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSortDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSourceDto;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * An example prompt search query with pagination and filtering options.
 */

@Schema(name = "ExamplePromptSearchQuery", description = "An example prompt search query with pagination and filtering options.")
@JsonTypeName("ExamplePromptSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ExamplePromptSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 25;

  private ExamplePromptSortDto sort = ExamplePromptSortDto.CREATED_AT;

  private SortDirectionDto direction = SortDirectionDto.ASC;

  private @Nullable ExamplePromptSourceDto source;

  private @Nullable Boolean active = null;

  private @Nullable String search = null;

  public ExamplePromptSearchQueryDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * The page number.
   * minimum: 0
   * @return pageNumber
   */
  @Min(0) 
  @Schema(name = "pageNumber", example = "0", description = "The page number.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageNumber")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public ExamplePromptSearchQueryDto pageSize(Integer pageSize) {
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
  @Schema(name = "pageSize", example = "25", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public ExamplePromptSearchQueryDto sort(ExamplePromptSortDto sort) {
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
  public ExamplePromptSortDto getSort() {
    return sort;
  }

  public void setSort(ExamplePromptSortDto sort) {
    this.sort = sort;
  }

  public ExamplePromptSearchQueryDto direction(SortDirectionDto direction) {
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

  public ExamplePromptSearchQueryDto source(@Nullable ExamplePromptSourceDto source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   */
  @Valid 
  @Schema(name = "source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("source")
  public @Nullable ExamplePromptSourceDto getSource() {
    return source;
  }

  public void setSource(@Nullable ExamplePromptSourceDto source) {
    this.source = source;
  }

  public ExamplePromptSearchQueryDto active(@Nullable Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Filter by active status (true returns only active prompts; false only inactive; omit for all).
   * @return active
   */
  
  @Schema(name = "active", example = "true", description = "Filter by active status (true returns only active prompts; false only inactive; omit for all).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public @Nullable Boolean getActive() {
    return active;
  }

  public void setActive(@Nullable Boolean active) {
    this.active = active;
  }

  public ExamplePromptSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by question content (case-insensitive partial match).
   * @return search
   */
  
  @Schema(name = "search", example = "diabetes", description = "Search by question content (case-insensitive partial match).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExamplePromptSearchQueryDto examplePromptSearchQuery = (ExamplePromptSearchQueryDto) o;
    return Objects.equals(this.pageNumber, examplePromptSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, examplePromptSearchQuery.pageSize) &&
        Objects.equals(this.sort, examplePromptSearchQuery.sort) &&
        Objects.equals(this.direction, examplePromptSearchQuery.direction) &&
        Objects.equals(this.source, examplePromptSearchQuery.source) &&
        Objects.equals(this.active, examplePromptSearchQuery.active) &&
        Objects.equals(this.search, examplePromptSearchQuery.search);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, source, active, search);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExamplePromptSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
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

    private ExamplePromptSearchQueryDto instance;

    public Builder() {
      this(new ExamplePromptSearchQueryDto());
    }

    protected Builder(ExamplePromptSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ExamplePromptSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setSource(value.source);
      this.instance.setActive(value.active);
      this.instance.setSearch(value.search);
      return this;
    }

    public ExamplePromptSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public ExamplePromptSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public ExamplePromptSearchQueryDto.Builder sort(ExamplePromptSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public ExamplePromptSearchQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public ExamplePromptSearchQueryDto.Builder source(ExamplePromptSourceDto source) {
      this.instance.source(source);
      return this;
    }
    
    public ExamplePromptSearchQueryDto.Builder active(Boolean active) {
      this.instance.active(active);
      return this;
    }
    
    public ExamplePromptSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    /**
    * returns a built ExamplePromptSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ExamplePromptSearchQueryDto build() {
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
  public static ExamplePromptSearchQueryDto.Builder builder() {
    return new ExamplePromptSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ExamplePromptSearchQueryDto.Builder toBuilder() {
    ExamplePromptSearchQueryDto.Builder builder = new ExamplePromptSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

