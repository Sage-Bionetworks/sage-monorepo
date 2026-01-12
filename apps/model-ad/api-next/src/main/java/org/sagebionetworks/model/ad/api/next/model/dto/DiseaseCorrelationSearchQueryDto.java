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
 * Disease correlation search query with pagination and filtering options.
 */

@Schema(name = "DiseaseCorrelationSearchQuery", description = "Disease correlation search query with pagination and filtering options.")
@JsonTypeName("DiseaseCorrelationSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class DiseaseCorrelationSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  @Valid
  private List<String> categories = new ArrayList<>();

  @Valid
  private @Nullable List<String> items;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  private @Nullable String search = null;

  @Valid
  private @Nullable List<String> age;

  @Valid
  private @Nullable List<String> modelType;

  @Valid
  private @Nullable List<String> modifiedGenes;

  @Valid
  private @Nullable List<String> name;

  @Valid
  private @Nullable List<String> sex;

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

  public DiseaseCorrelationSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DiseaseCorrelationSearchQueryDto(List<String> categories, List<String> sortFields, List<SortOrdersEnum> sortOrders) {
    this.categories = categories;
    this.sortFields = sortFields;
    this.sortOrders = sortOrders;
  }

  public DiseaseCorrelationSearchQueryDto pageNumber(Integer pageNumber) {
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

  public DiseaseCorrelationSearchQueryDto pageSize(Integer pageSize) {
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

  public DiseaseCorrelationSearchQueryDto categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addCategoriesItem(String categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * Array of category values from the dropdown selections. The API will parse these to extract the cluster information. Expected format: [mainCategory, clusterCategory] 
   * @return categories
   */
  @NotNull @Size(min = 2, max = 2) 
  @Schema(name = "categories", example = "[\"CONSENSUS NETWORK MODULES\",\"Consensus Cluster A - ECM Organization\"]", description = "Array of category values from the dropdown selections. The API will parse these to extract the cluster information. Expected format: [mainCategory, clusterCategory] ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("categories")
  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public DiseaseCorrelationSearchQueryDto items(@Nullable List<String> items) {
    this.items = items;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addItemsItem(String itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of composite identifiers to filter by. Each identifier uses the format \"name~age~sex\" where each identifier represents one complete combination of model name, age, and sex.  Example: \"APOE4~4 months~Female\" filters for documents matching that exact combination. Multiple items can be provided to filter for multiple specific combinations. 
   * @return items
   */
  
  @Schema(name = "items", example = "[\"APOE4~4 months~Female\",\"APOE4~8 months~Male\",\"5xFAD (IU/Jax/Pitt)~12 months~Female\"]", description = "List of composite identifiers to filter by. Each identifier uses the format \"name~age~sex\" where each identifier represents one complete combination of model name, age, and sex.  Example: \"APOE4~4 months~Female\" filters for documents matching that exact combination. Multiple items can be provided to filter for multiple specific combinations. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public @Nullable List<String> getItems() {
    return items;
  }

  public void setItems(@Nullable List<String> items) {
    this.items = items;
  }

  public DiseaseCorrelationSearchQueryDto itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
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

  public DiseaseCorrelationSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by model name (case-insensitive partial match) or by comma separated list of model names (case-insensitive full matches). Examples: '3xtg-ad,5xfad (uci)' (comma-separated list) or 'fad' (partial match). Only applied when itemFilterType is 'exclude'. 
   * @return search
   */
  
  @Schema(name = "search", example = "3xtg-ad,5xfad (uci)", description = "Search by model name (case-insensitive partial match) or by comma separated list of model names (case-insensitive full matches). Examples: '3xtg-ad,5xfad (uci)' (comma-separated list) or 'fad' (partial match). Only applied when itemFilterType is 'exclude'. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  public DiseaseCorrelationSearchQueryDto age(@Nullable List<String> age) {
    this.age = age;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addAgeItem(String ageItem) {
    if (this.age == null) {
      this.age = new ArrayList<>();
    }
    this.age.add(ageItem);
    return this;
  }

  /**
   * Filter by age.
   * @return age
   */
  
  @Schema(name = "age", example = "[\"4 months\",\"12 months\"]", description = "Filter by age.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("age")
  public @Nullable List<String> getAge() {
    return age;
  }

  public void setAge(@Nullable List<String> age) {
    this.age = age;
  }

  public DiseaseCorrelationSearchQueryDto modelType(@Nullable List<String> modelType) {
    this.modelType = modelType;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addModelTypeItem(String modelTypeItem) {
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
  
  @Schema(name = "modelType", example = "[\"Familial AD\",\"Late Onset AD\"]", description = "Filter by model type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modelType")
  public @Nullable List<String> getModelType() {
    return modelType;
  }

  public void setModelType(@Nullable List<String> modelType) {
    this.modelType = modelType;
  }

  public DiseaseCorrelationSearchQueryDto modifiedGenes(@Nullable List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addModifiedGenesItem(String modifiedGenesItem) {
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
  
  @Schema(name = "modifiedGenes", example = "[\"APOE\",\"Trem2\"]", description = "Filter by modified genes.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modifiedGenes")
  public @Nullable List<String> getModifiedGenes() {
    return modifiedGenes;
  }

  public void setModifiedGenes(@Nullable List<String> modifiedGenes) {
    this.modifiedGenes = modifiedGenes;
  }

  public DiseaseCorrelationSearchQueryDto name(@Nullable List<String> name) {
    this.name = name;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addNameItem(String nameItem) {
    if (this.name == null) {
      this.name = new ArrayList<>();
    }
    this.name.add(nameItem);
    return this;
  }

  /**
   * Filter by mouse model name.
   * @return name
   */
  
  @Schema(name = "name", example = "[\"3xTg-AD\",\"5xFAD (IU/Jax/Pitt)\"]", description = "Filter by mouse model name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public @Nullable List<String> getName() {
    return name;
  }

  public void setName(@Nullable List<String> name) {
    this.name = name;
  }

  public DiseaseCorrelationSearchQueryDto sex(@Nullable List<String> sex) {
    this.sex = sex;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addSexItem(String sexItem) {
    if (this.sex == null) {
      this.sex = new ArrayList<>();
    }
    this.sex.add(sexItem);
    return this;
  }

  /**
   * Filter by sex.
   * @return sex
   */
  
  @Schema(name = "sex", example = "[\"Female\",\"Male\"]", description = "Filter by sex.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sex")
  public @Nullable List<String> getSex() {
    return sex;
  }

  public void setSex(@Nullable List<String> sex) {
    this.sex = sex;
  }

  public DiseaseCorrelationSearchQueryDto sortFields(List<String> sortFields) {
    this.sortFields = sortFields;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addSortFieldsItem(String sortFieldsItem) {
    if (this.sortFields == null) {
      this.sortFields = new ArrayList<>();
    }
    this.sortFields.add(sortFieldsItem);
    return this;
  }

  /**
   * List of field names to sort by (e.g., [\"name\", \"age\", \"sex\"]). Each field in sortFields must have a corresponding order in sortOrders. 
   * @return sortFields
   */
  @NotNull @Size(min = 1) 
  @Schema(name = "sortFields", example = "[\"name\",\"age\",\"sex\"]", description = "List of field names to sort by (e.g., [\"name\", \"age\", \"sex\"]). Each field in sortFields must have a corresponding order in sortOrders. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortFields")
  public List<String> getSortFields() {
    return sortFields;
  }

  public void setSortFields(List<String> sortFields) {
    this.sortFields = sortFields;
  }

  public DiseaseCorrelationSearchQueryDto sortOrders(List<SortOrdersEnum> sortOrders) {
    this.sortOrders = sortOrders;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addSortOrdersItem(SortOrdersEnum sortOrdersItem) {
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
  @Schema(name = "sortOrders", example = "[1,-1,1]", description = "List of sort directions corresponding to sortFields. Values: 1 (ascending) or -1 (descending). Must have the same length as sortFields. ", requiredMode = Schema.RequiredMode.REQUIRED)
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
    DiseaseCorrelationSearchQueryDto diseaseCorrelationSearchQuery = (DiseaseCorrelationSearchQueryDto) o;
    return Objects.equals(this.pageNumber, diseaseCorrelationSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, diseaseCorrelationSearchQuery.pageSize) &&
        Objects.equals(this.categories, diseaseCorrelationSearchQuery.categories) &&
        Objects.equals(this.items, diseaseCorrelationSearchQuery.items) &&
        Objects.equals(this.itemFilterType, diseaseCorrelationSearchQuery.itemFilterType) &&
        Objects.equals(this.search, diseaseCorrelationSearchQuery.search) &&
        Objects.equals(this.age, diseaseCorrelationSearchQuery.age) &&
        Objects.equals(this.modelType, diseaseCorrelationSearchQuery.modelType) &&
        Objects.equals(this.modifiedGenes, diseaseCorrelationSearchQuery.modifiedGenes) &&
        Objects.equals(this.name, diseaseCorrelationSearchQuery.name) &&
        Objects.equals(this.sex, diseaseCorrelationSearchQuery.sex) &&
        Objects.equals(this.sortFields, diseaseCorrelationSearchQuery.sortFields) &&
        Objects.equals(this.sortOrders, diseaseCorrelationSearchQuery.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, categories, items, itemFilterType, search, age, modelType, modifiedGenes, name, sex, sortFields, sortOrders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DiseaseCorrelationSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemFilterType: ").append(toIndentedString(itemFilterType)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
    sb.append("    modifiedGenes: ").append(toIndentedString(modifiedGenes)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    sex: ").append(toIndentedString(sex)).append("\n");
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

    private DiseaseCorrelationSearchQueryDto instance;

    public Builder() {
      this(new DiseaseCorrelationSearchQueryDto());
    }

    protected Builder(DiseaseCorrelationSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DiseaseCorrelationSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setCategories(value.categories);
      this.instance.setItems(value.items);
      this.instance.setItemFilterType(value.itemFilterType);
      this.instance.setSearch(value.search);
      this.instance.setAge(value.age);
      this.instance.setModelType(value.modelType);
      this.instance.setModifiedGenes(value.modifiedGenes);
      this.instance.setName(value.name);
      this.instance.setSex(value.sex);
      this.instance.setSortFields(value.sortFields);
      this.instance.setSortOrders(value.sortOrders);
      return this;
    }

    public DiseaseCorrelationSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder categories(List<String> categories) {
      this.instance.categories(categories);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder items(List<String> items) {
      this.instance.items(items);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
      this.instance.itemFilterType(itemFilterType);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder age(List<String> age) {
      this.instance.age(age);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder modelType(List<String> modelType) {
      this.instance.modelType(modelType);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder modifiedGenes(List<String> modifiedGenes) {
      this.instance.modifiedGenes(modifiedGenes);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder name(List<String> name) {
      this.instance.name(name);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder sex(List<String> sex) {
      this.instance.sex(sex);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder sortFields(List<String> sortFields) {
      this.instance.sortFields(sortFields);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder sortOrders(List<SortOrdersEnum> sortOrders) {
      this.instance.sortOrders(sortOrders);
      return this;
    }
    
    /**
    * returns a built DiseaseCorrelationSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DiseaseCorrelationSearchQueryDto build() {
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
  public static DiseaseCorrelationSearchQueryDto.Builder builder() {
    return new DiseaseCorrelationSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DiseaseCorrelationSearchQueryDto.Builder toBuilder() {
    DiseaseCorrelationSearchQueryDto.Builder builder = new DiseaseCorrelationSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

