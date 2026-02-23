package org.sagebionetworks.agora.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Nominated Drug search query with pagination and filtering options.
 */

@Schema(name = "NominatedDrugSearchQuery", description = "Nominated Drug search query with pagination and filtering options.")
@JsonTypeName("NominatedDrugSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class NominatedDrugSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  @Valid
  private @Nullable List<String> items;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  private @Nullable String search = null;

  @Valid
  private @Nullable List<String> principalInvestigators;

  @Valid
  private @Nullable List<String> programs;

  @Valid
  private @Nullable List<Integer> totalNominations;

  @Valid
  private @Nullable List<Integer> yearFirstNominated;

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

  public NominatedDrugSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominatedDrugSearchQueryDto(List<String> sortFields, List<SortOrdersEnum> sortOrders) {
    this.sortFields = sortFields;
    this.sortOrders = sortOrders;
  }

  public NominatedDrugSearchQueryDto pageNumber(Integer pageNumber) {
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

  public NominatedDrugSearchQueryDto pageSize(Integer pageSize) {
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

  public NominatedDrugSearchQueryDto items(@Nullable List<String> items) {
    this.items = items;
    return this;
  }

  public NominatedDrugSearchQueryDto addItemsItem(String itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of common_name values to filter by. 
   * @return items
   */
  
  @Schema(name = "items", example = "[\"Agomelatine\",\"Bexarotene\"]", description = "List of common_name values to filter by. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public @Nullable List<String> getItems() {
    return items;
  }

  public void setItems(@Nullable List<String> items) {
    this.items = items;
  }

  public NominatedDrugSearchQueryDto itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
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

  public NominatedDrugSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by common_name value (case-insensitive partial match) or by comma separated list of common_name values (case-insensitive full matches). Examples: 'agomelatine,bexarotene' (comma-separated list) or 'agom' (partial match). Only applied when itemFilterType is 'exclude'. 
   * @return search
   */
  
  @Schema(name = "search", example = "agomelatine,bexarotene", description = "Search by common_name value (case-insensitive partial match) or by comma separated list of common_name values (case-insensitive full matches). Examples: 'agomelatine,bexarotene' (comma-separated list) or 'agom' (partial match). Only applied when itemFilterType is 'exclude'. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  public NominatedDrugSearchQueryDto principalInvestigators(@Nullable List<String> principalInvestigators) {
    this.principalInvestigators = principalInvestigators;
    return this;
  }

  public NominatedDrugSearchQueryDto addPrincipalInvestigatorsItem(String principalInvestigatorsItem) {
    if (this.principalInvestigators == null) {
      this.principalInvestigators = new ArrayList<>();
    }
    this.principalInvestigators.add(principalInvestigatorsItem);
    return this;
  }

  /**
   * Filter by principal investigators.
   * @return principalInvestigators
   */
  
  @Schema(name = "principalInvestigators", example = "[\"Bhatt\",\"Bhattacharya\"]", description = "Filter by principal investigators.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("principalInvestigators")
  public @Nullable List<String> getPrincipalInvestigators() {
    return principalInvestigators;
  }

  public void setPrincipalInvestigators(@Nullable List<String> principalInvestigators) {
    this.principalInvestigators = principalInvestigators;
  }

  public NominatedDrugSearchQueryDto programs(@Nullable List<String> programs) {
    this.programs = programs;
    return this;
  }

  public NominatedDrugSearchQueryDto addProgramsItem(String programsItem) {
    if (this.programs == null) {
      this.programs = new ArrayList<>();
    }
    this.programs.add(programsItem);
    return this;
  }

  /**
   * Filter by programs.
   * @return programs
   */
  
  @Schema(name = "programs", example = "[\"ACTDRx AD\",\"AMP-AD\"]", description = "Filter by programs.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("programs")
  public @Nullable List<String> getPrograms() {
    return programs;
  }

  public void setPrograms(@Nullable List<String> programs) {
    this.programs = programs;
  }

  public NominatedDrugSearchQueryDto totalNominations(@Nullable List<Integer> totalNominations) {
    this.totalNominations = totalNominations;
    return this;
  }

  public NominatedDrugSearchQueryDto addTotalNominationsItem(Integer totalNominationsItem) {
    if (this.totalNominations == null) {
      this.totalNominations = new ArrayList<>();
    }
    this.totalNominations.add(totalNominationsItem);
    return this;
  }

  /**
   * Filter by total nominations.
   * @return totalNominations
   */
  
  @Schema(name = "totalNominations", example = "[1,2]", description = "Filter by total nominations.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalNominations")
  public @Nullable List<Integer> getTotalNominations() {
    return totalNominations;
  }

  public void setTotalNominations(@Nullable List<Integer> totalNominations) {
    this.totalNominations = totalNominations;
  }

  public NominatedDrugSearchQueryDto yearFirstNominated(@Nullable List<Integer> yearFirstNominated) {
    this.yearFirstNominated = yearFirstNominated;
    return this;
  }

  public NominatedDrugSearchQueryDto addYearFirstNominatedItem(Integer yearFirstNominatedItem) {
    if (this.yearFirstNominated == null) {
      this.yearFirstNominated = new ArrayList<>();
    }
    this.yearFirstNominated.add(yearFirstNominatedItem);
    return this;
  }

  /**
   * Filter by year first nominated.
   * @return yearFirstNominated
   */
  
  @Schema(name = "yearFirstNominated", example = "[2024,2025]", description = "Filter by year first nominated.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("yearFirstNominated")
  public @Nullable List<Integer> getYearFirstNominated() {
    return yearFirstNominated;
  }

  public void setYearFirstNominated(@Nullable List<Integer> yearFirstNominated) {
    this.yearFirstNominated = yearFirstNominated;
  }

  public NominatedDrugSearchQueryDto sortFields(List<String> sortFields) {
    this.sortFields = sortFields;
    return this;
  }

  public NominatedDrugSearchQueryDto addSortFieldsItem(String sortFieldsItem) {
    if (this.sortFields == null) {
      this.sortFields = new ArrayList<>();
    }
    this.sortFields.add(sortFieldsItem);
    return this;
  }

  /**
   * List of field names to sort by (e.g., [\"total_nominations\", \"common_name\"]). Each field in sortFields must have a corresponding order in sortOrders. 
   * @return sortFields
   */
  @NotNull @Size(min = 1) 
  @Schema(name = "sortFields", example = "[\"total_nominations\",\"common_name\"]", description = "List of field names to sort by (e.g., [\"total_nominations\", \"common_name\"]). Each field in sortFields must have a corresponding order in sortOrders. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortFields")
  public List<String> getSortFields() {
    return sortFields;
  }

  public void setSortFields(List<String> sortFields) {
    this.sortFields = sortFields;
  }

  public NominatedDrugSearchQueryDto sortOrders(List<SortOrdersEnum> sortOrders) {
    this.sortOrders = sortOrders;
    return this;
  }

  public NominatedDrugSearchQueryDto addSortOrdersItem(SortOrdersEnum sortOrdersItem) {
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
    NominatedDrugSearchQueryDto nominatedDrugSearchQuery = (NominatedDrugSearchQueryDto) o;
    return Objects.equals(this.pageNumber, nominatedDrugSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, nominatedDrugSearchQuery.pageSize) &&
        Objects.equals(this.items, nominatedDrugSearchQuery.items) &&
        Objects.equals(this.itemFilterType, nominatedDrugSearchQuery.itemFilterType) &&
        Objects.equals(this.search, nominatedDrugSearchQuery.search) &&
        Objects.equals(this.principalInvestigators, nominatedDrugSearchQuery.principalInvestigators) &&
        Objects.equals(this.programs, nominatedDrugSearchQuery.programs) &&
        Objects.equals(this.totalNominations, nominatedDrugSearchQuery.totalNominations) &&
        Objects.equals(this.yearFirstNominated, nominatedDrugSearchQuery.yearFirstNominated) &&
        Objects.equals(this.sortFields, nominatedDrugSearchQuery.sortFields) &&
        Objects.equals(this.sortOrders, nominatedDrugSearchQuery.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, items, itemFilterType, search, principalInvestigators, programs, totalNominations, yearFirstNominated, sortFields, sortOrders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominatedDrugSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemFilterType: ").append(toIndentedString(itemFilterType)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    principalInvestigators: ").append(toIndentedString(principalInvestigators)).append("\n");
    sb.append("    programs: ").append(toIndentedString(programs)).append("\n");
    sb.append("    totalNominations: ").append(toIndentedString(totalNominations)).append("\n");
    sb.append("    yearFirstNominated: ").append(toIndentedString(yearFirstNominated)).append("\n");
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

    private NominatedDrugSearchQueryDto instance;

    public Builder() {
      this(new NominatedDrugSearchQueryDto());
    }

    protected Builder(NominatedDrugSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NominatedDrugSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setItems(value.items);
      this.instance.setItemFilterType(value.itemFilterType);
      this.instance.setSearch(value.search);
      this.instance.setPrincipalInvestigators(value.principalInvestigators);
      this.instance.setPrograms(value.programs);
      this.instance.setTotalNominations(value.totalNominations);
      this.instance.setYearFirstNominated(value.yearFirstNominated);
      this.instance.setSortFields(value.sortFields);
      this.instance.setSortOrders(value.sortOrders);
      return this;
    }

    public NominatedDrugSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder items(List<String> items) {
      this.instance.items(items);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
      this.instance.itemFilterType(itemFilterType);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder principalInvestigators(List<String> principalInvestigators) {
      this.instance.principalInvestigators(principalInvestigators);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder programs(List<String> programs) {
      this.instance.programs(programs);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder totalNominations(List<Integer> totalNominations) {
      this.instance.totalNominations(totalNominations);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder yearFirstNominated(List<Integer> yearFirstNominated) {
      this.instance.yearFirstNominated(yearFirstNominated);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder sortFields(List<String> sortFields) {
      this.instance.sortFields(sortFields);
      return this;
    }
    
    public NominatedDrugSearchQueryDto.Builder sortOrders(List<SortOrdersEnum> sortOrders) {
      this.instance.sortOrders(sortOrders);
      return this;
    }
    
    /**
    * returns a built NominatedDrugSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NominatedDrugSearchQueryDto build() {
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
  public static NominatedDrugSearchQueryDto.Builder builder() {
    return new NominatedDrugSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NominatedDrugSearchQueryDto.Builder toBuilder() {
    NominatedDrugSearchQueryDto.Builder builder = new NominatedDrugSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

