package org.sagebionetworks.model.ad.api.next.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.lang.Nullable;

/**
 * Gene expression search query with pagination and filtering options.
 */

@Schema(
  name = "GeneExpressionSearchQuery",
  description = "Gene expression search query with pagination and filtering options."
)
@JsonTypeName("GeneExpressionSearchQuery")
@Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen",
  comments = "Generator version: 7.14.0"
)
public class GeneExpressionSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  @Valid
  private List<String> categories = new ArrayList<>();

  @Valid
  private @Nullable List<String> items;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  private @Nullable String search = null;

  public GeneExpressionSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneExpressionSearchQueryDto(List<String> categories) {
    this.categories = categories;
  }

  public GeneExpressionSearchQueryDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * The page number.
   * minimum: 0
   * @return pageNumber
   */
  @Min(0)
  @Schema(
    name = "pageNumber",
    example = "0",
    description = "The page number.",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @JsonProperty("pageNumber")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public GeneExpressionSearchQueryDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * The number of items in a single page.
   * minimum: 1
   * maximum: 100
   * @return pageSize
   */
  @Min(1)
  @Max(100)
  @Schema(
    name = "pageSize",
    example = "100",
    description = "The number of items in a single page.",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public GeneExpressionSearchQueryDto categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  public GeneExpressionSearchQueryDto addCategoriesItem(String categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * Array of category values from the dropdown selections. The API will parse these to extract the tissue and sex_cohort information. Expected format: [mainCategory, tissueCategory, sexCohortCategory]
   * @return categories
   */
  @NotNull
  @Size(min = 3, max = 3)
  @Schema(
    name = "categories",
    example = "[\"RNA - DIFFERENTIAL EXPRESSION\",\"Tissue - Hemibrain\",\"Sex - Females & Males\"]",
    description = "Array of category values from the dropdown selections. The API will parse these to extract the tissue and sex_cohort information. Expected format: [mainCategory, tissueCategory, sexCohortCategory] ",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  @JsonProperty("categories")
  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public GeneExpressionSearchQueryDto items(@Nullable List<String> items) {
    this.items = items;
    return this;
  }

  public GeneExpressionSearchQueryDto addItemsItem(String itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of composite identifiers to filter by. Each identifier uses the format \"ensembl_gene_id~name\" where each identifier represents one complete combination of ensembl gene ID and gene name.  Example: \"ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)\" filters for documents matching that exact gene ID and name. Multiple items can be provided to filter for multiple specific combinations.
   * @return items
   */

  @Schema(
    name = "items",
    example = "[\"ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)\",\"ENSMUSG00000000028~APOE4\"]",
    description = "List of composite identifiers to filter by. Each identifier uses the format \"ensembl_gene_id~name\" where each identifier represents one complete combination of ensembl gene ID and gene name.  Example: \"ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)\" filters for documents matching that exact gene ID and name. Multiple items can be provided to filter for multiple specific combinations. ",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @JsonProperty("items")
  public @Nullable List<String> getItems() {
    return items;
  }

  public void setItems(@Nullable List<String> items) {
    this.items = items;
  }

  public GeneExpressionSearchQueryDto itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
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

  public GeneExpressionSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by gene symbol (case-insensitive partial match) or by comma separated list of gene symbols (case-insensitive full matches). Examples: 'gnai,cdc45' (comma-separated list) or 'gna' (partial match). Only applied when  itemFilterType is 'exclude'.
   * @return search
   */

  @Schema(
    name = "search",
    example = "gnai,cdc45",
    description = "Search by gene symbol (case-insensitive partial match) or by comma separated list of gene symbols (case-insensitive full matches). Examples: 'gnai,cdc45' (comma-separated list) or 'gna' (partial match). Only applied when  itemFilterType is 'exclude'. ",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
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
    GeneExpressionSearchQueryDto geneExpressionSearchQuery = (GeneExpressionSearchQueryDto) o;
    return (
      Objects.equals(this.pageNumber, geneExpressionSearchQuery.pageNumber) &&
      Objects.equals(this.pageSize, geneExpressionSearchQuery.pageSize) &&
      Objects.equals(this.categories, geneExpressionSearchQuery.categories) &&
      Objects.equals(this.items, geneExpressionSearchQuery.items) &&
      Objects.equals(this.itemFilterType, geneExpressionSearchQuery.itemFilterType) &&
      Objects.equals(this.search, geneExpressionSearchQuery.search)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, categories, items, itemFilterType, search);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneExpressionSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemFilterType: ").append(toIndentedString(itemFilterType)).append("\n");
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

    private GeneExpressionSearchQueryDto instance;

    public Builder() {
      this(new GeneExpressionSearchQueryDto());
    }

    protected Builder(GeneExpressionSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneExpressionSearchQueryDto value) {
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setCategories(value.categories);
      this.instance.setItems(value.items);
      this.instance.setItemFilterType(value.itemFilterType);
      this.instance.setSearch(value.search);
      return this;
    }

    public GeneExpressionSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }

    public GeneExpressionSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }

    public GeneExpressionSearchQueryDto.Builder categories(List<String> categories) {
      this.instance.categories(categories);
      return this;
    }

    public GeneExpressionSearchQueryDto.Builder items(List<String> items) {
      this.instance.items(items);
      return this;
    }

    public GeneExpressionSearchQueryDto.Builder itemFilterType(
      ItemFilterTypeQueryDto itemFilterType
    ) {
      this.instance.itemFilterType(itemFilterType);
      return this;
    }

    public GeneExpressionSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }

    /**
     * returns a built GeneExpressionSearchQueryDto instance.
     *
     * The builder is not reusable (NullPointerException)
     */
    public GeneExpressionSearchQueryDto build() {
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
  public static GeneExpressionSearchQueryDto.Builder builder() {
    return new GeneExpressionSearchQueryDto.Builder();
  }

  /**
   * Create a builder with a shallow copy of this instance.
   */
  public GeneExpressionSearchQueryDto.Builder toBuilder() {
    GeneExpressionSearchQueryDto.Builder builder = new GeneExpressionSearchQueryDto.Builder();
    return builder.copyOf(this);
  }
}
