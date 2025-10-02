package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.LicenseTypeDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelSortDto;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A model search query with pagination and filtering options.
 */

@Schema(name = "ModelSearchQuery", description = "A model search query with pagination and filtering options.")
@JsonTypeName("ModelSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 25;

  private ModelSortDto sort = ModelSortDto.NAME;

  private SortDirectionDto direction = SortDirectionDto.ASC;

  private @Nullable String search = null;

  private @Nullable Boolean active = null;

  private @Nullable LicenseTypeDto license;

  private @Nullable String organization = null;

  public ModelSearchQueryDto pageNumber(Integer pageNumber) {
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

  public ModelSearchQueryDto pageSize(Integer pageSize) {
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
  @Schema(name = "pageSize", example = "25", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public ModelSearchQueryDto sort(ModelSortDto sort) {
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
  public ModelSortDto getSort() {
    return sort;
  }

  public void setSort(ModelSortDto sort) {
    this.sort = sort;
  }

  public ModelSearchQueryDto direction(SortDirectionDto direction) {
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
  public SortDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(SortDirectionDto direction) {
    this.direction = direction;
  }

  public ModelSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by model name or slug (case-insensitive partial match).
   * @return search
   */
  
  @Schema(name = "search", example = "vision", description = "Search by model name or slug (case-insensitive partial match).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  public ModelSearchQueryDto active(@Nullable Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Filter by active status (true returns only active models; false only inactive; omit for all).
   * @return active
   */
  
  @Schema(name = "active", example = "true", description = "Filter by active status (true returns only active models; false only inactive; omit for all).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public @Nullable Boolean getActive() {
    return active;
  }

  public void setActive(@Nullable Boolean active) {
    this.active = active;
  }

  public ModelSearchQueryDto license(@Nullable LicenseTypeDto license) {
    this.license = license;
    return this;
  }

  /**
   * Get license
   * @return license
   */
  @Valid 
  @Schema(name = "license", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("license")
  public @Nullable LicenseTypeDto getLicense() {
    return license;
  }

  public void setLicense(@Nullable LicenseTypeDto license) {
    this.license = license;
  }

  public ModelSearchQueryDto organization(@Nullable String organization) {
    this.organization = organization;
    return this;
  }

  /**
   * Filter by organization name (case-insensitive partial match).
   * @return organization
   */
  
  @Schema(name = "organization", example = "OpenAI", description = "Filter by organization name (case-insensitive partial match).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("organization")
  public @Nullable String getOrganization() {
    return organization;
  }

  public void setOrganization(@Nullable String organization) {
    this.organization = organization;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelSearchQueryDto modelSearchQuery = (ModelSearchQueryDto) o;
    return Objects.equals(this.pageNumber, modelSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, modelSearchQuery.pageSize) &&
        Objects.equals(this.sort, modelSearchQuery.sort) &&
        Objects.equals(this.direction, modelSearchQuery.direction) &&
        Objects.equals(this.search, modelSearchQuery.search) &&
        Objects.equals(this.active, modelSearchQuery.active) &&
        Objects.equals(this.license, modelSearchQuery.license) &&
        Objects.equals(this.organization, modelSearchQuery.organization);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, search, active, license, organization);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    license: ").append(toIndentedString(license)).append("\n");
    sb.append("    organization: ").append(toIndentedString(organization)).append("\n");
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

    private ModelSearchQueryDto instance;

    public Builder() {
      this(new ModelSearchQueryDto());
    }

    protected Builder(ModelSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setSearch(value.search);
      this.instance.setActive(value.active);
      this.instance.setLicense(value.license);
      this.instance.setOrganization(value.organization);
      return this;
    }

    public ModelSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public ModelSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public ModelSearchQueryDto.Builder sort(ModelSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public ModelSearchQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public ModelSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    public ModelSearchQueryDto.Builder active(Boolean active) {
      this.instance.active(active);
      return this;
    }
    
    public ModelSearchQueryDto.Builder license(LicenseTypeDto license) {
      this.instance.license(license);
      return this;
    }
    
    public ModelSearchQueryDto.Builder organization(String organization) {
      this.instance.organization(organization);
      return this;
    }
    
    /**
    * returns a built ModelSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelSearchQueryDto build() {
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
  public static ModelSearchQueryDto.Builder builder() {
    return new ModelSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelSearchQueryDto.Builder toBuilder() {
    ModelSearchQueryDto.Builder builder = new ModelSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

