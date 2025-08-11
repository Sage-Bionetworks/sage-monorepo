package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamSectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * An EDAM concept search query.
 */

@Schema(name = "EdamConceptSearchQuery", description = "An EDAM concept search query.")
@JsonTypeName("EdamConceptSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class EdamConceptSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private EdamConceptSortDto sort = EdamConceptSortDto.RELEVANCE;

  private @Nullable EdamConceptDirectionDto direction = null;

  @Valid
  private List<@Min(1)Integer> ids = new ArrayList<>();

  private @Nullable String searchTerms;

  @Valid
  private List<EdamSectionDto> sections = new ArrayList<>();

  public EdamConceptSearchQueryDto pageNumber(Integer pageNumber) {
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

  public EdamConceptSearchQueryDto pageSize(Integer pageSize) {
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

  public EdamConceptSearchQueryDto sort(EdamConceptSortDto sort) {
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
  public EdamConceptSortDto getSort() {
    return sort;
  }

  public void setSort(EdamConceptSortDto sort) {
    this.sort = sort;
  }

  public EdamConceptSearchQueryDto direction(EdamConceptDirectionDto direction) {
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
  public EdamConceptDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(EdamConceptDirectionDto direction) {
    this.direction = direction;
  }

  public EdamConceptSearchQueryDto ids(List<@Min(1)Integer> ids) {
    this.ids = ids;
    return this;
  }

  public EdamConceptSearchQueryDto addIdsItem(Integer idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * An array of EDAM concept ids used to filter the results.
   * @return ids
   */
  
  @Schema(name = "ids", description = "An array of EDAM concept ids used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ids")
  public List<@Min(1)Integer> getIds() {
    return ids;
  }

  public void setIds(List<@Min(1)Integer> ids) {
    this.ids = ids;
  }

  public EdamConceptSearchQueryDto searchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
    return this;
  }

  /**
   * A string of search terms used to filter the results.
   * @return searchTerms
   */
  
  @Schema(name = "searchTerms", example = "sequence image", description = "A string of search terms used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchTerms")
  public String getSearchTerms() {
    return searchTerms;
  }

  public void setSearchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
  }

  public EdamConceptSearchQueryDto sections(List<EdamSectionDto> sections) {
    this.sections = sections;
    return this;
  }

  public EdamConceptSearchQueryDto addSectionsItem(EdamSectionDto sectionsItem) {
    if (this.sections == null) {
      this.sections = new ArrayList<>();
    }
    this.sections.add(sectionsItem);
    return this;
  }

  /**
   * An array of EDAM sections (sub-ontologies) used to filter the results.
   * @return sections
   */
  @Valid 
  @Schema(name = "sections", description = "An array of EDAM sections (sub-ontologies) used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sections")
  public List<EdamSectionDto> getSections() {
    return sections;
  }

  public void setSections(List<EdamSectionDto> sections) {
    this.sections = sections;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EdamConceptSearchQueryDto edamConceptSearchQuery = (EdamConceptSearchQueryDto) o;
    return Objects.equals(this.pageNumber, edamConceptSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, edamConceptSearchQuery.pageSize) &&
        Objects.equals(this.sort, edamConceptSearchQuery.sort) &&
        Objects.equals(this.direction, edamConceptSearchQuery.direction) &&
        Objects.equals(this.ids, edamConceptSearchQuery.ids) &&
        Objects.equals(this.searchTerms, edamConceptSearchQuery.searchTerms) &&
        Objects.equals(this.sections, edamConceptSearchQuery.sections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, ids, searchTerms, sections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EdamConceptSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
    sb.append("    searchTerms: ").append(toIndentedString(searchTerms)).append("\n");
    sb.append("    sections: ").append(toIndentedString(sections)).append("\n");
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

    private EdamConceptSearchQueryDto instance;

    public Builder() {
      this(new EdamConceptSearchQueryDto());
    }

    protected Builder(EdamConceptSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EdamConceptSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setIds(value.ids);
      this.instance.setSearchTerms(value.searchTerms);
      this.instance.setSections(value.sections);
      return this;
    }

    public EdamConceptSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public EdamConceptSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public EdamConceptSearchQueryDto.Builder sort(EdamConceptSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public EdamConceptSearchQueryDto.Builder direction(EdamConceptDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public EdamConceptSearchQueryDto.Builder ids(List<Integer> ids) {
      this.instance.ids(ids);
      return this;
    }
    
    public EdamConceptSearchQueryDto.Builder searchTerms(String searchTerms) {
      this.instance.searchTerms(searchTerms);
      return this;
    }
    
    public EdamConceptSearchQueryDto.Builder sections(List<EdamSectionDto> sections) {
      this.instance.sections(sections);
      return this;
    }
    
    /**
    * returns a built EdamConceptSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EdamConceptSearchQueryDto build() {
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
  public static EdamConceptSearchQueryDto.Builder builder() {
    return new EdamConceptSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EdamConceptSearchQueryDto.Builder toBuilder() {
    EdamConceptSearchQueryDto.Builder builder = new EdamConceptSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

