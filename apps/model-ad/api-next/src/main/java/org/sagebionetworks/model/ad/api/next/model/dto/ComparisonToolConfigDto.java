package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigColumnDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigFilterDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolPageDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ComparisonToolConfigDto
 */

@JsonTypeName("ComparisonToolConfig")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ComparisonToolConfigDto {

  private ComparisonToolPageDto page;

  @Valid
  private List<String> dropdowns = new ArrayList<>();

  private String rowCount = null;

  @Valid
  private List<@Valid ComparisonToolConfigColumnDto> columns = new ArrayList<>();

  @Valid
  private List<@Valid ComparisonToolConfigFilterDto> filters = new ArrayList<>();

  public ComparisonToolConfigDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ComparisonToolConfigDto(ComparisonToolPageDto page, List<String> dropdowns, String rowCount, List<@Valid ComparisonToolConfigColumnDto> columns, List<@Valid ComparisonToolConfigFilterDto> filters) {
    this.page = page;
    this.dropdowns = dropdowns;
    this.rowCount = rowCount;
    this.columns = columns;
    this.filters = filters;
  }

  public ComparisonToolConfigDto page(ComparisonToolPageDto page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
   */
  @NotNull @Valid 
  @Schema(name = "page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("page")
  public ComparisonToolPageDto getPage() {
    return page;
  }

  public void setPage(ComparisonToolPageDto page) {
    this.page = page;
  }

  public ComparisonToolConfigDto dropdowns(List<String> dropdowns) {
    this.dropdowns = dropdowns;
    return this;
  }

  public ComparisonToolConfigDto addDropdownsItem(String dropdownsItem) {
    if (this.dropdowns == null) {
      this.dropdowns = new ArrayList<>();
    }
    this.dropdowns.add(dropdownsItem);
    return this;
  }

  /**
   * List of dropdown options
   * @return dropdowns
   */
  @NotNull 
  @Schema(name = "dropdowns", description = "List of dropdown options", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("dropdowns")
  public List<String> getDropdowns() {
    return dropdowns;
  }

  public void setDropdowns(List<String> dropdowns) {
    this.dropdowns = dropdowns;
  }

  public ComparisonToolConfigDto rowCount(String rowCount) {
    this.rowCount = rowCount;
    return this;
  }

  /**
   * Deprecated field that is always null. Previously contained relative description of the total number of rows in the data
   * @return rowCount
   */
  @NotNull 
  @Schema(name = "row_count", description = "Deprecated field that is always null. Previously contained relative description of the total number of rows in the data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("row_count")
  public String getRowCount() {
    return rowCount;
  }

  public void setRowCount(String rowCount) {
    this.rowCount = rowCount;
  }

  public ComparisonToolConfigDto columns(List<@Valid ComparisonToolConfigColumnDto> columns) {
    this.columns = columns;
    return this;
  }

  public ComparisonToolConfigDto addColumnsItem(ComparisonToolConfigColumnDto columnsItem) {
    if (this.columns == null) {
      this.columns = new ArrayList<>();
    }
    this.columns.add(columnsItem);
    return this;
  }

  /**
   * List of column definitions
   * @return columns
   */
  @NotNull @Valid 
  @Schema(name = "columns", description = "List of column definitions", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("columns")
  public List<@Valid ComparisonToolConfigColumnDto> getColumns() {
    return columns;
  }

  public void setColumns(List<@Valid ComparisonToolConfigColumnDto> columns) {
    this.columns = columns;
  }

  public ComparisonToolConfigDto filters(List<@Valid ComparisonToolConfigFilterDto> filters) {
    this.filters = filters;
    return this;
  }

  public ComparisonToolConfigDto addFiltersItem(ComparisonToolConfigFilterDto filtersItem) {
    if (this.filters == null) {
      this.filters = new ArrayList<>();
    }
    this.filters.add(filtersItem);
    return this;
  }

  /**
   * List of filter configurations
   * @return filters
   */
  @NotNull @Valid 
  @Schema(name = "filters", description = "List of filter configurations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("filters")
  public List<@Valid ComparisonToolConfigFilterDto> getFilters() {
    return filters;
  }

  public void setFilters(List<@Valid ComparisonToolConfigFilterDto> filters) {
    this.filters = filters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComparisonToolConfigDto comparisonToolConfig = (ComparisonToolConfigDto) o;
    return Objects.equals(this.page, comparisonToolConfig.page) &&
        Objects.equals(this.dropdowns, comparisonToolConfig.dropdowns) &&
        Objects.equals(this.rowCount, comparisonToolConfig.rowCount) &&
        Objects.equals(this.columns, comparisonToolConfig.columns) &&
        Objects.equals(this.filters, comparisonToolConfig.filters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, dropdowns, rowCount, columns, filters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComparisonToolConfigDto {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    dropdowns: ").append(toIndentedString(dropdowns)).append("\n");
    sb.append("    rowCount: ").append(toIndentedString(rowCount)).append("\n");
    sb.append("    columns: ").append(toIndentedString(columns)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
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

    private ComparisonToolConfigDto instance;

    public Builder() {
      this(new ComparisonToolConfigDto());
    }

    protected Builder(ComparisonToolConfigDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ComparisonToolConfigDto value) { 
      this.instance.setPage(value.page);
      this.instance.setDropdowns(value.dropdowns);
      this.instance.setRowCount(value.rowCount);
      this.instance.setColumns(value.columns);
      this.instance.setFilters(value.filters);
      return this;
    }

    public ComparisonToolConfigDto.Builder page(ComparisonToolPageDto page) {
      this.instance.page(page);
      return this;
    }
    
    public ComparisonToolConfigDto.Builder dropdowns(List<String> dropdowns) {
      this.instance.dropdowns(dropdowns);
      return this;
    }
    
    public ComparisonToolConfigDto.Builder rowCount(String rowCount) {
      this.instance.rowCount(rowCount);
      return this;
    }
    
    public ComparisonToolConfigDto.Builder columns(List<ComparisonToolConfigColumnDto> columns) {
      this.instance.columns(columns);
      return this;
    }
    
    public ComparisonToolConfigDto.Builder filters(List<ComparisonToolConfigFilterDto> filters) {
      this.instance.filters(filters);
      return this;
    }
    
    /**
    * returns a built ComparisonToolConfigDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ComparisonToolConfigDto build() {
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
  public static ComparisonToolConfigDto.Builder builder() {
    return new ComparisonToolConfigDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ComparisonToolConfigDto.Builder toBuilder() {
    ComparisonToolConfigDto.Builder builder = new ComparisonToolConfigDto.Builder();
    return builder.copyOf(this);
  }

}

