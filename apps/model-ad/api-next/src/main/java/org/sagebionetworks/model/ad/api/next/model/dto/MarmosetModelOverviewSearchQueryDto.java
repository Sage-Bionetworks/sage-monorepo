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
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Marmoset model overview search query with pagination and filtering options.
 */

@Schema(name = "MarmosetModelOverviewSearchQuery", description = "Marmoset model overview search query with pagination and filtering options.")
@JsonTypeName("MarmosetModelOverviewSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MarmosetModelOverviewSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  @Valid
  private @Nullable List<String> items;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  private @Nullable String search = null;

  @Valid
  private @Nullable List<String> availableData;

  @Valid
  private @Nullable List<String> modelTypes;

  @Valid
  private @Nullable List<String> modifiedGenes;

  @Valid
  private List<String> sortFields = new ArrayList<>();

  /**
   * Gets or Sets sortOrders
   */
  public enum SortOrdersEnum {
    NUMBER_1(1),
    
    NUMBER_MINUS_1(-1);

    private final Integer value;

    SortOrdersEnum(Integer value) {
      this.value = value;
    }

    @JsonValue
    public Integer getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SortOrdersEnum fromValue(Integer value) {
      for (SortOrdersEnum b : SortOrdersEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Valid
  private List<SortOrdersEnum> sortOrders = new ArrayList<>();

  public MarmosetModelOverviewSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MarmosetModelOverviewSearchQueryDto(List<String> sortFields, List<SortOrdersEnum> sortOrders) {
    this.sortFields = sortFields;
    this.sortOrders = sortOrders;
  }

  public MarmosetModelOverviewSearchQueryDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * The page number to return (index starts from 0).
   * minimum: 0
   * @return pageNumber
   */
  @Min(0) 
  @Schema(name = "pageNumber", example = "0", description = "The page number to return (index starts from 0).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageNumber")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public MarmosetModelOverviewSearchQueryDto pageSize(Integer pageSize) {
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
  @Schema(name = "pageSize", example = "100", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public MarmosetModelOverviewSearchQueryDto items(@Nullable List<String> items) {
    this.items = items;
    return this;
  }

  public MarmosetModelOverviewSearchQueryDto addItemsItem(String itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of item names to filter by. 
   * @return items
   */
  
  @Schema(name = "items", example = "[\"Presenilin 1\",\"Another Name\"]", description = "List of item names to filter by. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public @Nullable List<String> getItems() {
    return items;
  }

  public void setItems(@Nullable List<String> items) {
    this.items = items;
  }

  public MarmosetModelOverviewSearchQueryDto itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
    this.itemFilterType = itemFilterType;
    return this;
  }

  /**
   * Get itemFilterType
   * @return itemFilterType
   */
  @Valid 
  @Schema(name = "itemFilterType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("itemFilterType")
  public ItemFilterTypeQueryDto getItemFilterType() {
    return itemFilterType;
  }

  public void setItemFilterType(ItemFilterTypeQueryDto itemFilterType) {
    this.itemFilterType = itemFilterType;
  }

  public MarmosetModelOverviewSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by model name (case-insensitive partial match) or by comma separated list of model names (case-insensitive full matches). Examples: 'presenilin 1,another name' (comma-separated list) or 'presenilin' (partial match). Only applied when itemFilterType is 'exclude'. 
   * @return search
   */
  
  @Schema(name = "search", example = "presenilin 1,another name", description = "Search by model name (case-insensitive partial match) or by comma separated list of model names (case-insensitive full matches). Examples: 'presenilin 1,another name' (comma-separated list) or 'presenilin' (partial match). Only applied when itemFilterType is 'exclude'. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  public MarmosetModelOverviewSearchQueryDto availableData(@Nullable List<String> availableData) {
    this.availableData = availableData;
    return this;
  }

  public MarmosetModelOverviewSearchQueryDto addAvailableDataItem(String availableDataItem) {
    if (this.availableData == null) {
      this.availableData = new ArrayList<>();
    }
    this.availableData.add(availableDataItem);
    return this;
  }

  /**
   * Filter by available data types.
   * @return availableData
   */
  
  @Schema(name = "availableData", example = "[\"Plasma Biomarkers\"]", description = "Filter by available data types.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availableData")
  public @Nullable List<String> getAvailableData() {
    return availableData;
  }

  public void setAvailableData(@Nullable List<String> availableData) {
    this.availableData = availableData;
  }

  public MarmosetModelOverviewSearchQueryDto modelTypes(@Nullable List<String> modelTypes) {
    this.modelTypes = modelTypes;
    return this;
  }

  public MarmosetModelOverviewSearchQueryDto addModelTypesItem(String modelTypesItem) {
    if (this.modelTypes == null) {
      this.modelTypes = new ArrayList<>();
    }
    this.modelTypes.add(modelTypesItem);
    return this;
  }

  /**
   * Filter by model type.
   * @return modelTypes
   */
  
  @Schema(name = "modelTypes", example = "[\"Familial AD\",\"Late Onset AD\"]", description = "Filter by model type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modelTypes")
  public @Nullable List<String> getModelTypes() {
    return modelTypes;
  }

  public void setModelTypes(@Nullable List<String> modelTypes) {
    this.modelTypes = modelTypes;
  }

  public MarmosetModelOverviewSearchQueryDto modifiedGenes(@Nullable List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
    return this;
  }

  public MarmosetModelOverviewSearchQueryDto addModifiedGenesItem(String modifiedGenesItem) {
    if (this.modifiedGenes == null) {
      this.modifiedGenes = new ArrayList<>();
    }
    this.modifiedGenes.add(modifiedGenesItem);
    return this;
  }

  /**
   * Filter by modified genes.
   * @return modifiedGenes
   */
  
  @Schema(name = "modifiedGenes", example = "[\"PSEN1\"]", description = "Filter by modified genes.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modifiedGenes")
  public @Nullable List<String> getModifiedGenes() {
    return modifiedGenes;
  }

  public void setModifiedGenes(@Nullable List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
  }

  public MarmosetModelOverviewSearchQueryDto sortFields(List<String> sortFields) {
    this.sortFields = sortFields;
    return this;
  }

  public MarmosetModelOverviewSearchQueryDto addSortFieldsItem(String sortFieldsItem) {
    if (this.sortFields == null) {
      this.sortFields = new ArrayList<>();
    }
    this.sortFields.add(sortFieldsItem);
    return this;
  }

  /**
   * List of field names to sort by (e.g., [\"model_type\", \"name\"]). Each field in sortFields must have a corresponding order in sortOrders. 
   * @return sortFields
   */
  @NotNull @Size(min = 1) 
  @Schema(name = "sortFields", example = "[\"model_type\",\"name\"]", description = "List of field names to sort by (e.g., [\"model_type\", \"name\"]). Each field in sortFields must have a corresponding order in sortOrders. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortFields")
  public List<String> getSortFields() {
    return sortFields;
  }

  public void setSortFields(List<String> sortFields) {
    this.sortFields = sortFields;
  }

  public MarmosetModelOverviewSearchQueryDto sortOrders(List<SortOrdersEnum> sortOrders) {
    this.sortOrders = sortOrders;
    return this;
  }

  public MarmosetModelOverviewSearchQueryDto addSortOrdersItem(SortOrdersEnum sortOrdersItem) {
    if (this.sortOrders == null) {
      this.sortOrders = new ArrayList<>();
    }
    this.sortOrders.add(sortOrdersItem);
    return this;
  }

  /**
   * List of sort directions corresponding to sortFields. Values: 1 (ascending) or -1 (descending). Must have the same length as sortFields. 
   * @return sortOrders
   */
  @NotNull @Size(min = 1) 
  @Schema(name = "sortOrders", example = "[-1,1]", description = "List of sort directions corresponding to sortFields. Values: 1 (ascending) or -1 (descending). Must have the same length as sortFields. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortOrders")
  public List<SortOrdersEnum> getSortOrders() {
    return sortOrders;
  }

  public void setSortOrders(List<SortOrdersEnum> sortOrders) {
    this.sortOrders = sortOrders;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarmosetModelOverviewSearchQueryDto marmosetModelOverviewSearchQuery = (MarmosetModelOverviewSearchQueryDto) o;
    return Objects.equals(this.pageNumber, marmosetModelOverviewSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, marmosetModelOverviewSearchQuery.pageSize) &&
        Objects.equals(this.items, marmosetModelOverviewSearchQuery.items) &&
        Objects.equals(this.itemFilterType, marmosetModelOverviewSearchQuery.itemFilterType) &&
        Objects.equals(this.search, marmosetModelOverviewSearchQuery.search) &&
        Objects.equals(this.availableData, marmosetModelOverviewSearchQuery.availableData) &&
        Objects.equals(this.modelTypes, marmosetModelOverviewSearchQuery.modelTypes) &&
        Objects.equals(this.modifiedGenes, marmosetModelOverviewSearchQuery.modifiedGenes) &&
        Objects.equals(this.sortFields, marmosetModelOverviewSearchQuery.sortFields) &&
        Objects.equals(this.sortOrders, marmosetModelOverviewSearchQuery.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, items, itemFilterType, search, availableData, modelTypes, modifiedGenes, sortFields, sortOrders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarmosetModelOverviewSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemFilterType: ").append(toIndentedString(itemFilterType)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    availableData: ").append(toIndentedString(availableData)).append("\n");
    sb.append("    modelTypes: ").append(toIndentedString(modelTypes)).append("\n");
    sb.append("    modifiedGenes: ").append(toIndentedString(modifiedGenes)).append("\n");
    sb.append("    sortFields: ").append(toIndentedString(sortFields)).append("\n");
    sb.append("    sortOrders: ").append(toIndentedString(sortOrders)).append("\n");
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

    private MarmosetModelOverviewSearchQueryDto instance;

    public Builder() {
      this(new MarmosetModelOverviewSearchQueryDto());
    }

    protected Builder(MarmosetModelOverviewSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MarmosetModelOverviewSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setItems(value.items);
      this.instance.setItemFilterType(value.itemFilterType);
      this.instance.setSearch(value.search);
      this.instance.setAvailableData(value.availableData);
      this.instance.setModelTypes(value.modelTypes);
      this.instance.setModifiedGenes(value.modifiedGenes);
      this.instance.setSortFields(value.sortFields);
      this.instance.setSortOrders(value.sortOrders);
      return this;
    }

    public MarmosetModelOverviewSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder items(List<String> items) {
      this.instance.items(items);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
      this.instance.itemFilterType(itemFilterType);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder availableData(List<String> availableData) {
      this.instance.availableData(availableData);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder modelTypes(List<String> modelTypes) {
      this.instance.modelTypes(modelTypes);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder modifiedGenes(List<String> modifiedGenes) {
      this.instance.modifiedGenes(modifiedGenes);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder sortFields(List<String> sortFields) {
      this.instance.sortFields(sortFields);
      return this;
    }
    
    public MarmosetModelOverviewSearchQueryDto.Builder sortOrders(List<SortOrdersEnum> sortOrders) {
      this.instance.sortOrders(sortOrders);
      return this;
    }
    
    /**
    * returns a built MarmosetModelOverviewSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MarmosetModelOverviewSearchQueryDto build() {
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
  public static MarmosetModelOverviewSearchQueryDto.Builder builder() {
    return new MarmosetModelOverviewSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MarmosetModelOverviewSearchQueryDto.Builder toBuilder() {
    MarmosetModelOverviewSearchQueryDto.Builder builder = new MarmosetModelOverviewSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

