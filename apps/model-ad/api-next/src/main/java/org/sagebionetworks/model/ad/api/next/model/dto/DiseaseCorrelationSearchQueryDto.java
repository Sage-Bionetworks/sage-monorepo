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
  private List<String> category = new ArrayList<>();

  @Valid
  private @Nullable List<String> items;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  public DiseaseCorrelationSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DiseaseCorrelationSearchQueryDto(List<String> category) {
    this.category = category;
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

  public DiseaseCorrelationSearchQueryDto category(List<String> category) {
    this.category = category;
    return this;
  }

  public DiseaseCorrelationSearchQueryDto addCategoryItem(String categoryItem) {
    if (this.category == null) {
      this.category = new ArrayList<>();
    }
    this.category.add(categoryItem);
    return this;
  }

  /**
   * The category selections
   * @return category
   */
  @NotNull @Size(min = 2, max = 2) 
  @Schema(name = "category", example = "[\"CONSENSUS NETWORK MODULES\",\"Consensus Cluster A - ECM Organization\"]", description = "The category selections", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("category")
  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
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
   * List of item IDs to filter by.
   * @return items
   */
  
  @Schema(name = "items", example = "[\"507f1f77bcf86cd799439011\",\"507f191e810c19729de860ea\"]", description = "List of item IDs to filter by.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
        Objects.equals(this.category, diseaseCorrelationSearchQuery.category) &&
        Objects.equals(this.items, diseaseCorrelationSearchQuery.items) &&
        Objects.equals(this.itemFilterType, diseaseCorrelationSearchQuery.itemFilterType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, category, items, itemFilterType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DiseaseCorrelationSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemFilterType: ").append(toIndentedString(itemFilterType)).append("\n");
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
      this.instance.setCategory(value.category);
      this.instance.setItems(value.items);
      this.instance.setItemFilterType(value.itemFilterType);
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
    
    public DiseaseCorrelationSearchQueryDto.Builder category(List<String> category) {
      this.instance.category(category);
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

