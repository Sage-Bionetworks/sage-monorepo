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
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolNounDto;
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

  private @Nullable String rowIdDataKey = null;

  private @Nullable String parentIdDataKey = null;

  private @Nullable ComparisonToolNounDto parentNoun = null;

  private @Nullable ComparisonToolNounDto viewNoun = null;

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

  public ComparisonToolConfigDto rowIdDataKey(@Nullable String rowIdDataKey) {
    this.rowIdDataKey = rowIdDataKey;
    return this;
  }

  /**
   * The data key holding this view's row UID, overriding the comparison tool's default row id key for this view. Null when the view declares no hierarchy. 
   * @return rowIdDataKey
   */
  
  @Schema(name = "row_id_data_key", example = "composite_id", description = "The data key holding this view's row UID, overriding the comparison tool's default row id key for this view. Null when the view declares no hierarchy. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("row_id_data_key")
  public @Nullable String getRowIdDataKey() {
    return rowIdDataKey;
  }

  public void setRowIdDataKey(@Nullable String rowIdDataKey) {
    this.rowIdDataKey = rowIdDataKey;
  }

  public ComparisonToolConfigDto parentIdDataKey(@Nullable String parentIdDataKey) {
    this.parentIdDataKey = parentIdDataKey;
    return this;
  }

  /**
   * The data key holding the id of the row's parent. It must map to the same values in every view of the comparison tool, though not necessarily through the same field, since different views can read different collections. Equal to row_id_data_key means the view's rows are their own parents; a different key means the view's rows are children. Null when the view declares no hierarchy. 
   * @return parentIdDataKey
   */
  
  @Schema(name = "parent_id_data_key", example = "rna_composite_id", description = "The data key holding the id of the row's parent. It must map to the same values in every view of the comparison tool, though not necessarily through the same field, since different views can read different collections. Equal to row_id_data_key means the view's rows are their own parents; a different key means the view's rows are children. Null when the view declares no hierarchy. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("parent_id_data_key")
  public @Nullable String getParentIdDataKey() {
    return parentIdDataKey;
  }

  public void setParentIdDataKey(@Nullable String parentIdDataKey) {
    this.parentIdDataKey = parentIdDataKey;
  }

  public ComparisonToolConfigDto parentNoun(@Nullable ComparisonToolNounDto parentNoun) {
    this.parentNoun = parentNoun;
    return this;
  }

  /**
   * The noun for this view's parent records, used to label parent counts. Set by a child view, which displays both counts. Null otherwise. 
   * @return parentNoun
   */
  @Valid 
  @Schema(name = "parent_noun", description = "The noun for this view's parent records, used to label parent counts. Set by a child view, which displays both counts. Null otherwise. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("parent_noun")
  public @Nullable ComparisonToolNounDto getParentNoun() {
    return parentNoun;
  }

  public void setParentNoun(@Nullable ComparisonToolNounDto parentNoun) {
    this.parentNoun = parentNoun;
  }

  public ComparisonToolConfigDto viewNoun(@Nullable ComparisonToolNounDto viewNoun) {
    this.viewNoun = viewNoun;
    return this;
  }

  /**
   * The noun for this view's own records, used to label row counts. Any view can set it to override the generic nouns in the comparison table. Null otherwise. 
   * @return viewNoun
   */
  @Valid 
  @Schema(name = "view_noun", description = "The noun for this view's own records, used to label row counts. Any view can set it to override the generic nouns in the comparison table. Null otherwise. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("view_noun")
  public @Nullable ComparisonToolNounDto getViewNoun() {
    return viewNoun;
  }

  public void setViewNoun(@Nullable ComparisonToolNounDto viewNoun) {
    this.viewNoun = viewNoun;
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
        Objects.equals(this.filters, comparisonToolConfig.filters) &&
        Objects.equals(this.rowIdDataKey, comparisonToolConfig.rowIdDataKey) &&
        Objects.equals(this.parentIdDataKey, comparisonToolConfig.parentIdDataKey) &&
        Objects.equals(this.parentNoun, comparisonToolConfig.parentNoun) &&
        Objects.equals(this.viewNoun, comparisonToolConfig.viewNoun);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, dropdowns, rowCount, columns, filters, rowIdDataKey, parentIdDataKey, parentNoun, viewNoun);
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
    sb.append("    rowIdDataKey: ").append(toIndentedString(rowIdDataKey)).append("\n");
    sb.append("    parentIdDataKey: ").append(toIndentedString(parentIdDataKey)).append("\n");
    sb.append("    parentNoun: ").append(toIndentedString(parentNoun)).append("\n");
    sb.append("    viewNoun: ").append(toIndentedString(viewNoun)).append("\n");
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
      this.instance.setRowIdDataKey(value.rowIdDataKey);
      this.instance.setParentIdDataKey(value.parentIdDataKey);
      this.instance.setParentNoun(value.parentNoun);
      this.instance.setViewNoun(value.viewNoun);
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
    
    public ComparisonToolConfigDto.Builder rowIdDataKey(String rowIdDataKey) {
      this.instance.rowIdDataKey(rowIdDataKey);
      return this;
    }
    
    public ComparisonToolConfigDto.Builder parentIdDataKey(String parentIdDataKey) {
      this.instance.parentIdDataKey(parentIdDataKey);
      return this;
    }
    
    public ComparisonToolConfigDto.Builder parentNoun(ComparisonToolNounDto parentNoun) {
      this.instance.parentNoun(parentNoun);
      return this;
    }
    
    public ComparisonToolConfigDto.Builder viewNoun(ComparisonToolNounDto viewNoun) {
      this.instance.viewNoun(viewNoun);
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

