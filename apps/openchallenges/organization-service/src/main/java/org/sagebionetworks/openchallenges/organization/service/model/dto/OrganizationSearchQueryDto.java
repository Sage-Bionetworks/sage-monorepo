package org.sagebionetworks.openchallenges.organization.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationCategoryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDirectionDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSortDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * An organization search query.
 */

@Schema(name = "OrganizationSearchQuery", description = "An organization search query.")
@JsonTypeName("OrganizationSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OrganizationSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  @Valid
  private List<OrganizationCategoryDto> categories = new ArrayList<>();

  @Valid
  private List<ChallengeContributionRoleDto> challengeContributionRoles = new ArrayList<>();

  private OrganizationSortDto sort = OrganizationSortDto.RELEVANCE;

  private @Nullable OrganizationDirectionDto direction = null;

  @Valid
  private List<Long> ids = new ArrayList<>();

  private @Nullable String searchTerms;

  public OrganizationSearchQueryDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * The page number.
   * minimum: 0
   * @return pageNumber
   */
  @Min(0) 
  @Schema(name = "pageNumber", description = "The page number.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageNumber")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public OrganizationSearchQueryDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * The number of items in a single page.
   * minimum: 1
   * @return pageSize
   */
  @Min(1) 
  @Schema(name = "pageSize", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public OrganizationSearchQueryDto categories(List<OrganizationCategoryDto> categories) {
    this.categories = categories;
    return this;
  }

  public OrganizationSearchQueryDto addCategoriesItem(OrganizationCategoryDto categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * The array of organization categories used to filter the results.
   * @return categories
   */
  @Valid 
  @Schema(name = "categories", description = "The array of organization categories used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("categories")
  public List<OrganizationCategoryDto> getCategories() {
    return categories;
  }

  public void setCategories(List<OrganizationCategoryDto> categories) {
    this.categories = categories;
  }

  public OrganizationSearchQueryDto challengeContributionRoles(List<ChallengeContributionRoleDto> challengeContributionRoles) {
    this.challengeContributionRoles = challengeContributionRoles;
    return this;
  }

  public OrganizationSearchQueryDto addChallengeContributionRolesItem(ChallengeContributionRoleDto challengeContributionRolesItem) {
    if (this.challengeContributionRoles == null) {
      this.challengeContributionRoles = new ArrayList<>();
    }
    this.challengeContributionRoles.add(challengeContributionRolesItem);
    return this;
  }

  /**
   * An array of challenge contribution roles used to filter the results.
   * @return challengeContributionRoles
   */
  @Valid 
  @Schema(name = "challengeContributionRoles", description = "An array of challenge contribution roles used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("challengeContributionRoles")
  public List<ChallengeContributionRoleDto> getChallengeContributionRoles() {
    return challengeContributionRoles;
  }

  public void setChallengeContributionRoles(List<ChallengeContributionRoleDto> challengeContributionRoles) {
    this.challengeContributionRoles = challengeContributionRoles;
  }

  public OrganizationSearchQueryDto sort(OrganizationSortDto sort) {
    this.sort = sort;
    return this;
  }

  /**
   * Get sort
   * @return sort
   */
  @Valid 
  @Schema(name = "sort", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sort")
  public OrganizationSortDto getSort() {
    return sort;
  }

  public void setSort(OrganizationSortDto sort) {
    this.sort = sort;
  }

  public OrganizationSearchQueryDto direction(@Nullable OrganizationDirectionDto direction) {
    this.direction = direction;
    return this;
  }

  /**
   * Get direction
   * @return direction
   */
  @Valid 
  @Schema(name = "direction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("direction")
  public @Nullable OrganizationDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(@Nullable OrganizationDirectionDto direction) {
    this.direction = direction;
  }

  public OrganizationSearchQueryDto ids(List<Long> ids) {
    this.ids = ids;
    return this;
  }

  public OrganizationSearchQueryDto addIdsItem(Long idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * An array of organization ids used to filter the results.
   * @return ids
   */
  
  @Schema(name = "ids", description = "An array of organization ids used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ids")
  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

  public OrganizationSearchQueryDto searchTerms(@Nullable String searchTerms) {
    this.searchTerms = searchTerms;
    return this;
  }

  /**
   * A string of search terms used to filter the results.
   * @return searchTerms
   */
  
  @Schema(name = "searchTerms", example = "data provider", description = "A string of search terms used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchTerms")
  public @Nullable String getSearchTerms() {
    return searchTerms;
  }

  public void setSearchTerms(@Nullable String searchTerms) {
    this.searchTerms = searchTerms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationSearchQueryDto organizationSearchQuery = (OrganizationSearchQueryDto) o;
    return Objects.equals(this.pageNumber, organizationSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, organizationSearchQuery.pageSize) &&
        Objects.equals(this.categories, organizationSearchQuery.categories) &&
        Objects.equals(this.challengeContributionRoles, organizationSearchQuery.challengeContributionRoles) &&
        Objects.equals(this.sort, organizationSearchQuery.sort) &&
        Objects.equals(this.direction, organizationSearchQuery.direction) &&
        Objects.equals(this.ids, organizationSearchQuery.ids) &&
        Objects.equals(this.searchTerms, organizationSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, categories, challengeContributionRoles, sort, direction, ids, searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    challengeContributionRoles: ").append(toIndentedString(challengeContributionRoles)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
    sb.append("    searchTerms: ").append(toIndentedString(searchTerms)).append("\n");
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

    private OrganizationSearchQueryDto instance;

    public Builder() {
      this(new OrganizationSearchQueryDto());
    }

    protected Builder(OrganizationSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(OrganizationSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setCategories(value.categories);
      this.instance.setChallengeContributionRoles(value.challengeContributionRoles);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setIds(value.ids);
      this.instance.setSearchTerms(value.searchTerms);
      return this;
    }

    public OrganizationSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public OrganizationSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public OrganizationSearchQueryDto.Builder categories(List<OrganizationCategoryDto> categories) {
      this.instance.categories(categories);
      return this;
    }
    
    public OrganizationSearchQueryDto.Builder challengeContributionRoles(List<ChallengeContributionRoleDto> challengeContributionRoles) {
      this.instance.challengeContributionRoles(challengeContributionRoles);
      return this;
    }
    
    public OrganizationSearchQueryDto.Builder sort(OrganizationSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public OrganizationSearchQueryDto.Builder direction(OrganizationDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public OrganizationSearchQueryDto.Builder ids(List<Long> ids) {
      this.instance.ids(ids);
      return this;
    }
    
    public OrganizationSearchQueryDto.Builder searchTerms(String searchTerms) {
      this.instance.searchTerms(searchTerms);
      return this;
    }
    
    /**
    * returns a built OrganizationSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public OrganizationSearchQueryDto build() {
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
  public static OrganizationSearchQueryDto.Builder builder() {
    return new OrganizationSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public OrganizationSearchQueryDto.Builder toBuilder() {
    OrganizationSearchQueryDto.Builder builder = new OrganizationSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

