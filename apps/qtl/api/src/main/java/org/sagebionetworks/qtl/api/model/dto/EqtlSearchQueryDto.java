package org.sagebionetworks.qtl.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.qtl.api.model.dto.ItemFilterTypeQueryDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * eQTL search query with pagination and filtering options.
 */

@Schema(name = "EqtlSearchQuery", description = "eQTL search query with pagination and filtering options.")
@JsonTypeName("EqtlSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class EqtlSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private @Nullable Integer remainingBudget = null;

  @Valid
  private @Nullable List<String> items;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  private @Nullable String search = null;

  @Valid
  private @Nullable List<String> variant;

  @Valid
  private @Nullable List<String> gene;

  @Valid
  private @Nullable List<String> cellClass;

  @Valid
  private @Nullable List<String> cellSubclass;

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

  public EqtlSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EqtlSearchQueryDto(List<String> sortFields, List<SortOrdersEnum> sortOrders) {
    this.sortFields = sortFields;
    this.sortOrders = sortOrders;
  }

  public EqtlSearchQueryDto pageNumber(Integer pageNumber) {
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

  public EqtlSearchQueryDto pageSize(Integer pageSize) {
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

  public EqtlSearchQueryDto remainingBudget(@Nullable Integer remainingBudget) {
    this.remainingBudget = remainingBudget;
    return this;
  }

  /**
   * Maximum number of rows to return, letting a client retrieve matching rows from beyond the current page in a single request. When set, pageNumber and pageSize are ignored. Only applied when itemFilterType is 'exclude'. 
   * minimum: 1
   * maximum: 50
   * @return remainingBudget
   */
  @Min(1) @Max(50) 
  @Schema(name = "remainingBudget", example = "50", description = "Maximum number of rows to return, letting a client retrieve matching rows from beyond the current page in a single request. When set, pageNumber and pageSize are ignored. Only applied when itemFilterType is 'exclude'. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("remainingBudget")
  public @Nullable Integer getRemainingBudget() {
    return remainingBudget;
  }

  public void setRemainingBudget(@Nullable Integer remainingBudget) {
    this.remainingBudget = remainingBudget;
  }

  public EqtlSearchQueryDto items(@Nullable List<String> items) {
    this.items = items;
    return this;
  }

  public EqtlSearchQueryDto addItemsItem(String itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of composite identifiers to filter by. Each identifier uses the format \"variant~ensembl_gene_id~cell_type_id\" where each identifier represents one complete combination of variant, Ensembl gene ID, and cell type ID.  Example: \"rs12655812~ENSG00000250891~astro\" filters for documents matching that exact combination. Multiple items can be provided to filter for multiple specific combinations. 
   * @return items
   */
  
  @Schema(name = "items", example = "[\"rs12655812~ENSG00000250891~astro\",\"rs170094~ENSG00000136267~astro\"]", description = "List of composite identifiers to filter by. Each identifier uses the format \"variant~ensembl_gene_id~cell_type_id\" where each identifier represents one complete combination of variant, Ensembl gene ID, and cell type ID.  Example: \"rs12655812~ENSG00000250891~astro\" filters for documents matching that exact combination. Multiple items can be provided to filter for multiple specific combinations. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public @Nullable List<String> getItems() {
    return items;
  }

  public void setItems(@Nullable List<String> items) {
    this.items = items;
  }

  public EqtlSearchQueryDto itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
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

  public EqtlSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by variant, gene symbol, or Ensembl gene ID. A single term is a case-insensitive partial match on any of the three; a comma separated list full-matches each term, case-insensitively, against any of the three. Examples: 'rs170094,DGKB' (comma-separated list) or 'dgk' (partial match). Only applied when itemFilterType is 'exclude'. 
   * @return search
   */
  
  @Schema(name = "search", example = "rs170094,DGKB", description = "Search by variant, gene symbol, or Ensembl gene ID. A single term is a case-insensitive partial match on any of the three; a comma separated list full-matches each term, case-insensitively, against any of the three. Examples: 'rs170094,DGKB' (comma-separated list) or 'dgk' (partial match). Only applied when itemFilterType is 'exclude'. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  public EqtlSearchQueryDto variant(@Nullable List<String> variant) {
    this.variant = variant;
    return this;
  }

  public EqtlSearchQueryDto addVariantItem(String variantItem) {
    if (this.variant == null) {
      this.variant = new ArrayList<>();
    }
    this.variant.add(variantItem);
    return this;
  }

  /**
   * Filter by variant.
   * @return variant
   */
  
  @Schema(name = "variant", example = "[\"rs12655812\",\"rs170094\"]", description = "Filter by variant.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("variant")
  public @Nullable List<String> getVariant() {
    return variant;
  }

  public void setVariant(@Nullable List<String> variant) {
    this.variant = variant;
  }

  public EqtlSearchQueryDto gene(@Nullable List<String> gene) {
    this.gene = gene;
    return this;
  }

  public EqtlSearchQueryDto addGeneItem(String geneItem) {
    if (this.gene == null) {
      this.gene = new ArrayList<>();
    }
    this.gene.add(geneItem);
    return this;
  }

  /**
   * Filter by gene symbol.
   * @return gene
   */
  
  @Schema(name = "gene", example = "[\"DGKB\",\"LINC02208\"]", description = "Filter by gene symbol.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("gene")
  public @Nullable List<String> getGene() {
    return gene;
  }

  public void setGene(@Nullable List<String> gene) {
    this.gene = gene;
  }

  public EqtlSearchQueryDto cellClass(@Nullable List<String> cellClass) {
    this.cellClass = cellClass;
    return this;
  }

  public EqtlSearchQueryDto addCellClassItem(String cellClassItem) {
    if (this.cellClass == null) {
      this.cellClass = new ArrayList<>();
    }
    this.cellClass.add(cellClassItem);
    return this;
  }

  /**
   * Filter by cell class.
   * @return cellClass
   */
  
  @Schema(name = "cellClass", example = "[\"Astrocyte\"]", description = "Filter by cell class.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellClass")
  public @Nullable List<String> getCellClass() {
    return cellClass;
  }

  public void setCellClass(@Nullable List<String> cellClass) {
    this.cellClass = cellClass;
  }

  public EqtlSearchQueryDto cellSubclass(@Nullable List<String> cellSubclass) {
    this.cellSubclass = cellSubclass;
    return this;
  }

  public EqtlSearchQueryDto addCellSubclassItem(String cellSubclassItem) {
    if (this.cellSubclass == null) {
      this.cellSubclass = new ArrayList<>();
    }
    this.cellSubclass.add(cellSubclassItem);
    return this;
  }

  /**
   * Filter by cell subclass.
   * @return cellSubclass
   */
  
  @Schema(name = "cellSubclass", example = "[\"Astrocyte\",\"Layer 3-5 Intratelencephalic Excitatory Neuron 1\"]", description = "Filter by cell subclass.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellSubclass")
  public @Nullable List<String> getCellSubclass() {
    return cellSubclass;
  }

  public void setCellSubclass(@Nullable List<String> cellSubclass) {
    this.cellSubclass = cellSubclass;
  }

  public EqtlSearchQueryDto sortFields(List<String> sortFields) {
    this.sortFields = sortFields;
    return this;
  }

  public EqtlSearchQueryDto addSortFieldsItem(String sortFieldsItem) {
    if (this.sortFields == null) {
      this.sortFields = new ArrayList<>();
    }
    this.sortFields.add(sortFieldsItem);
    return this;
  }

  /**
   * List of field names to sort by (e.g., [\"adj_p_val\", \"variant\"]). Each field in sortFields must have a corresponding order in sortOrders. 
   * @return sortFields
   */
  @NotNull @Size(min = 1) 
  @Schema(name = "sortFields", example = "[\"adj_p_val\",\"variant\"]", description = "List of field names to sort by (e.g., [\"adj_p_val\", \"variant\"]). Each field in sortFields must have a corresponding order in sortOrders. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortFields")
  public List<String> getSortFields() {
    return sortFields;
  }

  public void setSortFields(List<String> sortFields) {
    this.sortFields = sortFields;
  }

  public EqtlSearchQueryDto sortOrders(List<SortOrdersEnum> sortOrders) {
    this.sortOrders = sortOrders;
    return this;
  }

  public EqtlSearchQueryDto addSortOrdersItem(SortOrdersEnum sortOrdersItem) {
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
  @Schema(name = "sortOrders", example = "[1,1]", description = "List of sort directions corresponding to sortFields. Values: 1 (ascending) or -1 (descending). Must have the same length as sortFields. ", requiredMode = Schema.RequiredMode.REQUIRED)
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
    EqtlSearchQueryDto eqtlSearchQuery = (EqtlSearchQueryDto) o;
    return Objects.equals(this.pageNumber, eqtlSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, eqtlSearchQuery.pageSize) &&
        Objects.equals(this.remainingBudget, eqtlSearchQuery.remainingBudget) &&
        Objects.equals(this.items, eqtlSearchQuery.items) &&
        Objects.equals(this.itemFilterType, eqtlSearchQuery.itemFilterType) &&
        Objects.equals(this.search, eqtlSearchQuery.search) &&
        Objects.equals(this.variant, eqtlSearchQuery.variant) &&
        Objects.equals(this.gene, eqtlSearchQuery.gene) &&
        Objects.equals(this.cellClass, eqtlSearchQuery.cellClass) &&
        Objects.equals(this.cellSubclass, eqtlSearchQuery.cellSubclass) &&
        Objects.equals(this.sortFields, eqtlSearchQuery.sortFields) &&
        Objects.equals(this.sortOrders, eqtlSearchQuery.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, remainingBudget, items, itemFilterType, search, variant, gene, cellClass, cellSubclass, sortFields, sortOrders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EqtlSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    remainingBudget: ").append(toIndentedString(remainingBudget)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemFilterType: ").append(toIndentedString(itemFilterType)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    variant: ").append(toIndentedString(variant)).append("\n");
    sb.append("    gene: ").append(toIndentedString(gene)).append("\n");
    sb.append("    cellClass: ").append(toIndentedString(cellClass)).append("\n");
    sb.append("    cellSubclass: ").append(toIndentedString(cellSubclass)).append("\n");
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

    private EqtlSearchQueryDto instance;

    public Builder() {
      this(new EqtlSearchQueryDto());
    }

    protected Builder(EqtlSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EqtlSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setRemainingBudget(value.remainingBudget);
      this.instance.setItems(value.items);
      this.instance.setItemFilterType(value.itemFilterType);
      this.instance.setSearch(value.search);
      this.instance.setVariant(value.variant);
      this.instance.setGene(value.gene);
      this.instance.setCellClass(value.cellClass);
      this.instance.setCellSubclass(value.cellSubclass);
      this.instance.setSortFields(value.sortFields);
      this.instance.setSortOrders(value.sortOrders);
      return this;
    }

    public EqtlSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder remainingBudget(Integer remainingBudget) {
      this.instance.remainingBudget(remainingBudget);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder items(List<String> items) {
      this.instance.items(items);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
      this.instance.itemFilterType(itemFilterType);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder variant(List<String> variant) {
      this.instance.variant(variant);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder gene(List<String> gene) {
      this.instance.gene(gene);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder cellClass(List<String> cellClass) {
      this.instance.cellClass(cellClass);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder cellSubclass(List<String> cellSubclass) {
      this.instance.cellSubclass(cellSubclass);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder sortFields(List<String> sortFields) {
      this.instance.sortFields(sortFields);
      return this;
    }
    
    public EqtlSearchQueryDto.Builder sortOrders(List<SortOrdersEnum> sortOrders) {
      this.instance.sortOrders(sortOrders);
      return this;
    }
    
    /**
    * returns a built EqtlSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EqtlSearchQueryDto build() {
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
  public static EqtlSearchQueryDto.Builder builder() {
    return new EqtlSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EqtlSearchQueryDto.Builder toBuilder() {
    EqtlSearchQueryDto.Builder builder = new EqtlSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

