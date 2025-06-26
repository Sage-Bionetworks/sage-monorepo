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
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSortDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A challenge platform search query.
 */

@Schema(name = "ChallengePlatformSearchQuery", description = "A challenge platform search query.")
@JsonTypeName("ChallengePlatformSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengePlatformSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private ChallengePlatformSortDto sort = ChallengePlatformSortDto.RELEVANCE;

  private @Nullable ChallengePlatformDirectionDto direction = null;

  @Valid
  private List<@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")@Size(min = 3, max = 30)String> slugs = new ArrayList<>();

  private @Nullable String searchTerms;

  public ChallengePlatformSearchQueryDto pageNumber(Integer pageNumber) {
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

  public ChallengePlatformSearchQueryDto pageSize(Integer pageSize) {
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

  public ChallengePlatformSearchQueryDto sort(ChallengePlatformSortDto sort) {
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
  public ChallengePlatformSortDto getSort() {
    return sort;
  }

  public void setSort(ChallengePlatformSortDto sort) {
    this.sort = sort;
  }

  public ChallengePlatformSearchQueryDto direction(ChallengePlatformDirectionDto direction) {
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
  public ChallengePlatformDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(ChallengePlatformDirectionDto direction) {
    this.direction = direction;
  }

  public ChallengePlatformSearchQueryDto slugs(List<@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")@Size(min = 3, max = 30)String> slugs) {
    this.slugs = slugs;
    return this;
  }

  public ChallengePlatformSearchQueryDto addSlugsItem(String slugsItem) {
    if (this.slugs == null) {
      this.slugs = new ArrayList<>();
    }
    this.slugs.add(slugsItem);
    return this;
  }

  /**
   * An array of challenge platform slugs used to filter the results.
   * @return slugs
   */
  
  @Schema(name = "slugs", description = "An array of challenge platform slugs used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("slugs")
  public List<@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")@Size(min = 3, max = 30)String> getSlugs() {
    return slugs;
  }

  public void setSlugs(List<@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")@Size(min = 3, max = 30)String> slugs) {
    this.slugs = slugs;
  }

  public ChallengePlatformSearchQueryDto searchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
    return this;
  }

  /**
   * A string of search terms used to filter the results.
   * @return searchTerms
   */
  
  @Schema(name = "searchTerms", example = "synapse", description = "A string of search terms used to filter the results.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchTerms")
  public String getSearchTerms() {
    return searchTerms;
  }

  public void setSearchTerms(String searchTerms) {
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
    ChallengePlatformSearchQueryDto challengePlatformSearchQuery = (ChallengePlatformSearchQueryDto) o;
    return Objects.equals(this.pageNumber, challengePlatformSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, challengePlatformSearchQuery.pageSize) &&
        Objects.equals(this.sort, challengePlatformSearchQuery.sort) &&
        Objects.equals(this.direction, challengePlatformSearchQuery.direction) &&
        Objects.equals(this.slugs, challengePlatformSearchQuery.slugs) &&
        Objects.equals(this.searchTerms, challengePlatformSearchQuery.searchTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, slugs, searchTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    slugs: ").append(toIndentedString(slugs)).append("\n");
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

    private ChallengePlatformSearchQueryDto instance;

    public Builder() {
      this(new ChallengePlatformSearchQueryDto());
    }

    protected Builder(ChallengePlatformSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengePlatformSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setSlugs(value.slugs);
      this.instance.setSearchTerms(value.searchTerms);
      return this;
    }

    public ChallengePlatformSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public ChallengePlatformSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public ChallengePlatformSearchQueryDto.Builder sort(ChallengePlatformSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public ChallengePlatformSearchQueryDto.Builder direction(ChallengePlatformDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public ChallengePlatformSearchQueryDto.Builder slugs(List<String> slugs) {
      this.instance.slugs(slugs);
      return this;
    }
    
    public ChallengePlatformSearchQueryDto.Builder searchTerms(String searchTerms) {
      this.instance.searchTerms(searchTerms);
      return this;
    }
    
    /**
    * returns a built ChallengePlatformSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengePlatformSearchQueryDto build() {
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
  public static ChallengePlatformSearchQueryDto.Builder builder() {
    return new ChallengePlatformSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengePlatformSearchQueryDto.Builder toBuilder() {
    ChallengePlatformSearchQueryDto.Builder builder = new ChallengePlatformSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

