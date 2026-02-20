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
 * Nominated Target search query with pagination and filtering options.
 */

@Schema(name = "NominatedTargetSearchQuery", description = "Nominated Target search query with pagination and filtering options.")
@JsonTypeName("NominatedTargetSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class NominatedTargetSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  @Valid
  private @Nullable List<String> items;

  private ItemFilterTypeQueryDto itemFilterType = ItemFilterTypeQueryDto.INCLUDE;

  private @Nullable String search = null;

  @Valid
  private @Nullable List<String> cohortStudies;

  @Valid
  private @Nullable List<String> inputData;

  @Valid
  private @Nullable List<Integer> initialNomination;

  @Valid
  private @Nullable List<String> nominatingTeams;

  @Valid
  private @Nullable List<String> pharosClass;

  @Valid
  private @Nullable List<String> programs;

  @Valid
  private @Nullable List<Integer> totalNominations;

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

  public NominatedTargetSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominatedTargetSearchQueryDto(List<String> sortFields, List<SortOrdersEnum> sortOrders) {
    this.sortFields = sortFields;
    this.sortOrders = sortOrders;
  }

  public NominatedTargetSearchQueryDto pageNumber(Integer pageNumber) {
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

  public NominatedTargetSearchQueryDto pageSize(Integer pageSize) {
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

  public NominatedTargetSearchQueryDto items(@Nullable List<String> items) {
    this.items = items;
    return this;
  }

  public NominatedTargetSearchQueryDto addItemsItem(String itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of hgnc_symbol values to filter by. 
   * @return items
   */
  
  @Schema(name = "items", example = "[\"CFH\",\"SEMA3F\"]", description = "List of hgnc_symbol values to filter by. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public @Nullable List<String> getItems() {
    return items;
  }

  public void setItems(@Nullable List<String> items) {
    this.items = items;
  }

  public NominatedTargetSearchQueryDto itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
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

  public NominatedTargetSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by hgnc_symbol value (case-insensitive partial match) or by comma separated list of hgnc_symbol values (case-insensitive full matches). Examples: 'pten,plec' (comma-separated list) or 'ple' (partial match). Only applied when itemFilterType is 'exclude'. 
   * @return search
   */
  
  @Schema(name = "search", example = "pten,plec", description = "Search by hgnc_symbol value (case-insensitive partial match) or by comma separated list of hgnc_symbol values (case-insensitive full matches). Examples: 'pten,plec' (comma-separated list) or 'ple' (partial match). Only applied when itemFilterType is 'exclude'. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  public NominatedTargetSearchQueryDto cohortStudies(@Nullable List<String> cohortStudies) {
    this.cohortStudies = cohortStudies;
    return this;
  }

  public NominatedTargetSearchQueryDto addCohortStudiesItem(String cohortStudiesItem) {
    if (this.cohortStudies == null) {
      this.cohortStudies = new ArrayList<>();
    }
    this.cohortStudies.add(cohortStudiesItem);
    return this;
  }

  /**
   * Filter by cohort studies.
   * @return cohortStudies
   */
  
  @Schema(name = "cohortStudies", example = "[\"ABA\",\"ACT\"]", description = "Filter by cohort studies.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cohortStudies")
  public @Nullable List<String> getCohortStudies() {
    return cohortStudies;
  }

  public void setCohortStudies(@Nullable List<String> cohortStudies) {
    this.cohortStudies = cohortStudies;
  }

  public NominatedTargetSearchQueryDto inputData(@Nullable List<String> inputData) {
    this.inputData = inputData;
    return this;
  }

  public NominatedTargetSearchQueryDto addInputDataItem(String inputDataItem) {
    if (this.inputData == null) {
      this.inputData = new ArrayList<>();
    }
    this.inputData.add(inputDataItem);
    return this;
  }

  /**
   * Filter by input data types.
   * @return inputData
   */
  
  @Schema(name = "inputData", example = "[\"Behavior\",\"Clinical\"]", description = "Filter by input data types.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("inputData")
  public @Nullable List<String> getInputData() {
    return inputData;
  }

  public void setInputData(@Nullable List<String> inputData) {
    this.inputData = inputData;
  }

  public NominatedTargetSearchQueryDto initialNomination(@Nullable List<Integer> initialNomination) {
    this.initialNomination = initialNomination;
    return this;
  }

  public NominatedTargetSearchQueryDto addInitialNominationItem(Integer initialNominationItem) {
    if (this.initialNomination == null) {
      this.initialNomination = new ArrayList<>();
    }
    this.initialNomination.add(initialNominationItem);
    return this;
  }

  /**
   * Filter by initial nomination year.
   * @return initialNomination
   */
  
  @Schema(name = "initialNomination", example = "[2021,2022]", description = "Filter by initial nomination year.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("initialNomination")
  public @Nullable List<Integer> getInitialNomination() {
    return initialNomination;
  }

  public void setInitialNomination(@Nullable List<Integer> initialNomination) {
    this.initialNomination = initialNomination;
  }

  public NominatedTargetSearchQueryDto nominatingTeams(@Nullable List<String> nominatingTeams) {
    this.nominatingTeams = nominatingTeams;
    return this;
  }

  public NominatedTargetSearchQueryDto addNominatingTeamsItem(String nominatingTeamsItem) {
    if (this.nominatingTeams == null) {
      this.nominatingTeams = new ArrayList<>();
    }
    this.nominatingTeams.add(nominatingTeamsItem);
    return this;
  }

  /**
   * Filter by nominating teams.
   * @return nominatingTeams
   */
  
  @Schema(name = "nominatingTeams", example = "[\"ASU\",\"Chang Lab\"]", description = "Filter by nominating teams.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nominatingTeams")
  public @Nullable List<String> getNominatingTeams() {
    return nominatingTeams;
  }

  public void setNominatingTeams(@Nullable List<String> nominatingTeams) {
    this.nominatingTeams = nominatingTeams;
  }

  public NominatedTargetSearchQueryDto pharosClass(@Nullable List<String> pharosClass) {
    this.pharosClass = pharosClass;
    return this;
  }

  public NominatedTargetSearchQueryDto addPharosClassItem(String pharosClassItem) {
    if (this.pharosClass == null) {
      this.pharosClass = new ArrayList<>();
    }
    this.pharosClass.add(pharosClassItem);
    return this;
  }

  /**
   * Filter by Pharos class.
   * @return pharosClass
   */
  
  @Schema(name = "pharosClass", example = "[\"Tclin\",\"Tchem\"]", description = "Filter by Pharos class.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pharosClass")
  public @Nullable List<String> getPharosClass() {
    return pharosClass;
  }

  public void setPharosClass(@Nullable List<String> pharosClass) {
    this.pharosClass = pharosClass;
  }

  public NominatedTargetSearchQueryDto programs(@Nullable List<String> programs) {
    this.programs = programs;
    return this;
  }

  public NominatedTargetSearchQueryDto addProgramsItem(String programsItem) {
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
  
  @Schema(name = "programs", example = "[\"AMP-AD\",\"Community\"]", description = "Filter by programs.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("programs")
  public @Nullable List<String> getPrograms() {
    return programs;
  }

  public void setPrograms(@Nullable List<String> programs) {
    this.programs = programs;
  }

  public NominatedTargetSearchQueryDto totalNominations(@Nullable List<Integer> totalNominations) {
    this.totalNominations = totalNominations;
    return this;
  }

  public NominatedTargetSearchQueryDto addTotalNominationsItem(Integer totalNominationsItem) {
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

  public NominatedTargetSearchQueryDto sortFields(List<String> sortFields) {
    this.sortFields = sortFields;
    return this;
  }

  public NominatedTargetSearchQueryDto addSortFieldsItem(String sortFieldsItem) {
    if (this.sortFields == null) {
      this.sortFields = new ArrayList<>();
    }
    this.sortFields.add(sortFieldsItem);
    return this;
  }

  /**
   * List of field names to sort by (e.g., [\"total_nominations\", \"hgnc_symbol\"]). Each field in sortFields must have a corresponding order in sortOrders. 
   * @return sortFields
   */
  @NotNull @Size(min = 1) 
  @Schema(name = "sortFields", example = "[\"total_nominations\",\"hgnc_symbol\"]", description = "List of field names to sort by (e.g., [\"total_nominations\", \"hgnc_symbol\"]). Each field in sortFields must have a corresponding order in sortOrders. ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortFields")
  public List<String> getSortFields() {
    return sortFields;
  }

  public void setSortFields(List<String> sortFields) {
    this.sortFields = sortFields;
  }

  public NominatedTargetSearchQueryDto sortOrders(List<SortOrdersEnum> sortOrders) {
    this.sortOrders = sortOrders;
    return this;
  }

  public NominatedTargetSearchQueryDto addSortOrdersItem(SortOrdersEnum sortOrdersItem) {
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
    NominatedTargetSearchQueryDto nominatedTargetSearchQuery = (NominatedTargetSearchQueryDto) o;
    return Objects.equals(this.pageNumber, nominatedTargetSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, nominatedTargetSearchQuery.pageSize) &&
        Objects.equals(this.items, nominatedTargetSearchQuery.items) &&
        Objects.equals(this.itemFilterType, nominatedTargetSearchQuery.itemFilterType) &&
        Objects.equals(this.search, nominatedTargetSearchQuery.search) &&
        Objects.equals(this.cohortStudies, nominatedTargetSearchQuery.cohortStudies) &&
        Objects.equals(this.inputData, nominatedTargetSearchQuery.inputData) &&
        Objects.equals(this.initialNomination, nominatedTargetSearchQuery.initialNomination) &&
        Objects.equals(this.nominatingTeams, nominatedTargetSearchQuery.nominatingTeams) &&
        Objects.equals(this.pharosClass, nominatedTargetSearchQuery.pharosClass) &&
        Objects.equals(this.programs, nominatedTargetSearchQuery.programs) &&
        Objects.equals(this.totalNominations, nominatedTargetSearchQuery.totalNominations) &&
        Objects.equals(this.sortFields, nominatedTargetSearchQuery.sortFields) &&
        Objects.equals(this.sortOrders, nominatedTargetSearchQuery.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, items, itemFilterType, search, cohortStudies, inputData, initialNomination, nominatingTeams, pharosClass, programs, totalNominations, sortFields, sortOrders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominatedTargetSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemFilterType: ").append(toIndentedString(itemFilterType)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    cohortStudies: ").append(toIndentedString(cohortStudies)).append("\n");
    sb.append("    inputData: ").append(toIndentedString(inputData)).append("\n");
    sb.append("    initialNomination: ").append(toIndentedString(initialNomination)).append("\n");
    sb.append("    nominatingTeams: ").append(toIndentedString(nominatingTeams)).append("\n");
    sb.append("    pharosClass: ").append(toIndentedString(pharosClass)).append("\n");
    sb.append("    programs: ").append(toIndentedString(programs)).append("\n");
    sb.append("    totalNominations: ").append(toIndentedString(totalNominations)).append("\n");
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

    private NominatedTargetSearchQueryDto instance;

    public Builder() {
      this(new NominatedTargetSearchQueryDto());
    }

    protected Builder(NominatedTargetSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NominatedTargetSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setItems(value.items);
      this.instance.setItemFilterType(value.itemFilterType);
      this.instance.setSearch(value.search);
      this.instance.setCohortStudies(value.cohortStudies);
      this.instance.setInputData(value.inputData);
      this.instance.setInitialNomination(value.initialNomination);
      this.instance.setNominatingTeams(value.nominatingTeams);
      this.instance.setPharosClass(value.pharosClass);
      this.instance.setPrograms(value.programs);
      this.instance.setTotalNominations(value.totalNominations);
      this.instance.setSortFields(value.sortFields);
      this.instance.setSortOrders(value.sortOrders);
      return this;
    }

    public NominatedTargetSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder items(List<String> items) {
      this.instance.items(items);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder itemFilterType(ItemFilterTypeQueryDto itemFilterType) {
      this.instance.itemFilterType(itemFilterType);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder cohortStudies(List<String> cohortStudies) {
      this.instance.cohortStudies(cohortStudies);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder inputData(List<String> inputData) {
      this.instance.inputData(inputData);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder initialNomination(List<Integer> initialNomination) {
      this.instance.initialNomination(initialNomination);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder nominatingTeams(List<String> nominatingTeams) {
      this.instance.nominatingTeams(nominatingTeams);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder pharosClass(List<String> pharosClass) {
      this.instance.pharosClass(pharosClass);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder programs(List<String> programs) {
      this.instance.programs(programs);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder totalNominations(List<Integer> totalNominations) {
      this.instance.totalNominations(totalNominations);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder sortFields(List<String> sortFields) {
      this.instance.sortFields(sortFields);
      return this;
    }
    
    public NominatedTargetSearchQueryDto.Builder sortOrders(List<SortOrdersEnum> sortOrders) {
      this.instance.sortOrders(sortOrders);
      return this;
    }
    
    /**
    * returns a built NominatedTargetSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NominatedTargetSearchQueryDto build() {
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
  public static NominatedTargetSearchQueryDto.Builder builder() {
    return new NominatedTargetSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NominatedTargetSearchQueryDto.Builder toBuilder() {
    NominatedTargetSearchQueryDto.Builder builder = new NominatedTargetSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

