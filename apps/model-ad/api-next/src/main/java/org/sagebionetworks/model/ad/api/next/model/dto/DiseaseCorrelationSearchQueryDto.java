package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
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

  private String categories;

  private @Nullable String items = null;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  private @Nullable String search = null;

  private String sortFields;

  private String sortOrders;

  public DiseaseCorrelationSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DiseaseCorrelationSearchQueryDto(String categories, String sortFields, String sortOrders) {
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

  public DiseaseCorrelationSearchQueryDto categories(String categories) {
    this.categories = categories;
    return this;
  }

  /**
   * Comma-delimited category values from the dropdown selections. The API will parse these to extract the cluster information. Expected format: \"mainCategory,clusterCategory\" 
   * @return categories
   */
  @NotNull 
  @Schema(name = "categories", example = "CONSENSUS NETWORK MODULES,Consensus Cluster A - ECM Organization", description = "Comma-delimited category values from the dropdown selections. The API will parse these to extract the cluster information. Expected format: \"mainCategory,clusterCategory\" ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("categories")
  public String getCategories() {
    return categories;
  }

  public void setCategories(String categories) {
    this.categories = categories;
  }

  public DiseaseCorrelationSearchQueryDto items(@Nullable String items) {
    this.items = items;
    return this;
  }

  /**
   * Comma-delimited list of composite identifiers to filter by. Each identifier uses the format \"name~age~sex\" where each identifier represents one complete combination of model name, age, and sex.  Example: \"APOE4~4 months~Female,APOE4~8 months~Male,5xFAD (IU/Jax/Pitt)~12 months~Female\" filters for documents matching those exact combinations. 
   * @return items
   */
  
  @Schema(name = "items", example = "APOE4~4 months~Female,APOE4~8 months~Male,5xFAD (IU/Jax/Pitt)~12 months~Female", description = "Comma-delimited list of composite identifiers to filter by. Each identifier uses the format \"name~age~sex\" where each identifier represents one complete combination of model name, age, and sex.  Example: \"APOE4~4 months~Female,APOE4~8 months~Male,5xFAD (IU/Jax/Pitt)~12 months~Female\" filters for documents matching those exact combinations. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public @Nullable String getItems() {
    return items;
  }

  public void setItems(@Nullable String items) {
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

  public DiseaseCorrelationSearchQueryDto sortFields(String sortFields) {
    this.sortFields = sortFields;
    return this;
  }

  /**
   * Comma-delimited field names to sort by (e.g., \"name,age,sex\"). Each field in sortFields must have a corresponding order in sortOrders. 
   * @return sortFields
   */
  @NotNull @Pattern(regexp = "^[a-zA-Z0-9_ ]+(,[a-zA-Z0-9_ ]+)*$") 
  @Schema(name = "sortFields", example = "name,age,sex", description = "Comma-delimited field names to sort by (e.g., \"name,age,sex\"). Each field in sortFields must have a corresponding order in sortOrders. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortFields")
  public String getSortFields() {
    return sortFields;
  }

  public void setSortFields(String sortFields) {
    this.sortFields = sortFields;
  }

  public DiseaseCorrelationSearchQueryDto sortOrders(String sortOrders) {
    this.sortOrders = sortOrders;
    return this;
  }

  /**
   * Comma-delimited sort directions corresponding to sortFields. Values: 1 (ascending) or -1 (descending). Must have the same length as sortFields. 
   * @return sortOrders
   */
  @NotNull @Pattern(regexp = "^-?1(,-?1)*$") 
  @Schema(name = "sortOrders", example = "1,-1,1", description = "Comma-delimited sort directions corresponding to sortFields. Values: 1 (ascending) or -1 (descending). Must have the same length as sortFields. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortOrders")
  public String getSortOrders() {
    return sortOrders;
  }

  public void setSortOrders(String sortOrders) {
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
        Objects.equals(this.sortFields, diseaseCorrelationSearchQuery.sortFields) &&
        Objects.equals(this.sortOrders, diseaseCorrelationSearchQuery.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, categories, items, itemFilterType, search, sortFields, sortOrders);
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
    
    public DiseaseCorrelationSearchQueryDto.Builder categories(String categories) {
      this.instance.categories(categories);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder items(String items) {
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
    
    public DiseaseCorrelationSearchQueryDto.Builder sortFields(String sortFields) {
      this.instance.sortFields(sortFields);
      return this;
    }
    
    public DiseaseCorrelationSearchQueryDto.Builder sortOrders(String sortOrders) {
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

