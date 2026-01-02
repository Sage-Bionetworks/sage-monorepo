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
 * Model overview search query with pagination and filtering options.
 */

@Schema(name = "ModelOverviewSearchQuery", description = "Model overview search query with pagination and filtering options.")
@JsonTypeName("ModelOverviewSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelOverviewSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  @Valid
  private @Nullable List<String> items;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  private @Nullable String search = null;

  @Valid
  private @Nullable List<String> availableData;

  @Valid
  private @Nullable List<String> center;

  @Valid
  private @Nullable List<String> modelType;

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

  public ModelOverviewSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelOverviewSearchQueryDto(List<String> sortFields, List<SortOrdersEnum> sortOrders) {
    this.sortFields = sortFields;
    this.sortOrders = sortOrders;
  }

  public ModelOverviewSearchQueryDto pageNumber(Integer pageNumber) {
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

  public ModelOverviewSearchQueryDto pageSize(Integer pageSize) {
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

  public ModelOverviewSearchQueryDto items(@Nullable List<String> items) {
    this.items = items;
    return this;
  }

  public ModelOverviewSearchQueryDto addItemsItem(String itemsItem) {
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
  
  @Schema(name = "items", example = "[\"3xTg-AD\",\"5xFAD (UCI)\"]", description = "List of item names to filter by. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public @Nullable List<String> getItems() {
    return items;
  }

  public void setItems(@Nullable List<String> items) {
    this.items = items;
  }

  public ModelOverviewSearchQueryDto itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
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

  public ModelOverviewSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by model name (case-insensitive partial match) or by comma separated list of model names (case-insensitive full matches). Examples: '3xtg-ad,5xfad' (comma-separated list) or 'fad' (partial match). Only applied when itemFilterType is 'exclude'. 
   * @return search
   */
  
  @Schema(name = "search", example = "3xtg-ad,5xfad", description = "Search by model name (case-insensitive partial match) or by comma separated list of model names (case-insensitive full matches). Examples: '3xtg-ad,5xfad' (comma-separated list) or 'fad' (partial match). Only applied when itemFilterType is 'exclude'. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  public ModelOverviewSearchQueryDto availableData(@Nullable List<String> availableData) {
    this.availableData = availableData;
    return this;
  }

  public ModelOverviewSearchQueryDto addAvailableDataItem(String availableDataItem) {
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
  
  @Schema(name = "available_data", example = "[\"Biomarkers\",\"Disease Correlation\"]", description = "Filter by available data types.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("available_data")
  public @Nullable List<String> getAvailableData() {
    return availableData;
  }

  public void setAvailableData(@Nullable List<String> availableData) {
    this.availableData = availableData;
  }

  public ModelOverviewSearchQueryDto center(@Nullable List<String> center) {
    this.center = center;
    return this;
  }

  public ModelOverviewSearchQueryDto addCenterItem(String centerItem) {
    if (this.center == null) {
      this.center = new ArrayList<>();
    }
    this.center.add(centerItem);
    return this;
  }

  /**
   * Filter by contributing center.
   * @return center
   */
  
  @Schema(name = "center", example = "[\"IU/Jax/Pitt\",\"UCI\"]", description = "Filter by contributing center.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("center")
  public @Nullable List<String> getCenter() {
    return center;
  }

  public void setCenter(@Nullable List<String> center) {
    this.center = center;
  }

  public ModelOverviewSearchQueryDto modelType(@Nullable List<String> modelType) {
    this.modelType = modelType;
    return this;
  }

  public ModelOverviewSearchQueryDto addModelTypeItem(String modelTypeItem) {
    if (this.modelType == null) {
      this.modelType = new ArrayList<>();
    }
    this.modelType.add(modelTypeItem);
    return this;
  }

  /**
   * Filter by model type.
   * @return modelType
   */
  
  @Schema(name = "model_type", example = "[\"Familial AD\",\"Late Onset AD\"]", description = "Filter by model type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("model_type")
  public @Nullable List<String> getModelType() {
    return modelType;
  }

  public void setModelType(@Nullable List<String> modelType) {
    this.modelType = modelType;
  }

  public ModelOverviewSearchQueryDto modifiedGenes(@Nullable List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
    return this;
  }

  public ModelOverviewSearchQueryDto addModifiedGenesItem(String modifiedGenesItem) {
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
  
  @Schema(name = "modified_genes", example = "[\"5xFAD (IU/Jax/Pitt)\",\"Abca7*V1599M\"]", description = "Filter by modified genes.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modified_genes")
  public @Nullable List<String> getModifiedGenes() {
    return modifiedGenes;
  }

  public void setModifiedGenes(@Nullable List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
  }

  public ModelOverviewSearchQueryDto sortFields(List<String> sortFields) {
    this.sortFields = sortFields;
    return this;
  }

  public ModelOverviewSearchQueryDto addSortFieldsItem(String sortFieldsItem) {
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

  public ModelOverviewSearchQueryDto sortOrders(List<SortOrdersEnum> sortOrders) {
    this.sortOrders = sortOrders;
    return this;
  }

  public ModelOverviewSearchQueryDto addSortOrdersItem(SortOrdersEnum sortOrdersItem) {
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
    ModelOverviewSearchQueryDto modelOverviewSearchQuery = (ModelOverviewSearchQueryDto) o;
    return Objects.equals(this.pageNumber, modelOverviewSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, modelOverviewSearchQuery.pageSize) &&
        Objects.equals(this.items, modelOverviewSearchQuery.items) &&
        Objects.equals(this.itemFilterType, modelOverviewSearchQuery.itemFilterType) &&
        Objects.equals(this.search, modelOverviewSearchQuery.search) &&
        Objects.equals(this.availableData, modelOverviewSearchQuery.availableData) &&
        Objects.equals(this.center, modelOverviewSearchQuery.center) &&
        Objects.equals(this.modelType, modelOverviewSearchQuery.modelType) &&
        Objects.equals(this.modifiedGenes, modelOverviewSearchQuery.modifiedGenes) &&
        Objects.equals(this.sortFields, modelOverviewSearchQuery.sortFields) &&
        Objects.equals(this.sortOrders, modelOverviewSearchQuery.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, items, itemFilterType, search, availableData, center, modelType, modifiedGenes, sortFields, sortOrders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelOverviewSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemFilterType: ").append(toIndentedString(itemFilterType)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    availableData: ").append(toIndentedString(availableData)).append("\n");
    sb.append("    center: ").append(toIndentedString(center)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
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

    private ModelOverviewSearchQueryDto instance;

    public Builder() {
      this(new ModelOverviewSearchQueryDto());
    }

    protected Builder(ModelOverviewSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelOverviewSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setItems(value.items);
      this.instance.setItemFilterType(value.itemFilterType);
      this.instance.setSearch(value.search);
      this.instance.setAvailableData(value.availableData);
      this.instance.setCenter(value.center);
      this.instance.setModelType(value.modelType);
      this.instance.setModifiedGenes(value.modifiedGenes);
      this.instance.setSortFields(value.sortFields);
      this.instance.setSortOrders(value.sortOrders);
      return this;
    }

    public ModelOverviewSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder items(List<String> items) {
      this.instance.items(items);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
      this.instance.itemFilterType(itemFilterType);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder availableData(List<String> availableData) {
      this.instance.availableData(availableData);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder center(List<String> center) {
      this.instance.center(center);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder modelType(List<String> modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder modifiedGenes(List<String> modifiedGenes) {
      this.instance.modifiedGenes(modifiedGenes);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder sortFields(List<String> sortFields) {
      this.instance.sortFields(sortFields);
      return this;
    }
    
    public ModelOverviewSearchQueryDto.Builder sortOrders(List<SortOrdersEnum> sortOrders) {
      this.instance.sortOrders(sortOrders);
      return this;
    }
    
    /**
    * returns a built ModelOverviewSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelOverviewSearchQueryDto build() {
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
  public static ModelOverviewSearchQueryDto.Builder builder() {
    return new ModelOverviewSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelOverviewSearchQueryDto.Builder toBuilder() {
    ModelOverviewSearchQueryDto.Builder builder = new ModelOverviewSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

