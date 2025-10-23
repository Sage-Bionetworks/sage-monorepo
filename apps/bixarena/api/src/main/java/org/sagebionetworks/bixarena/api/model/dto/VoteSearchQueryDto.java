package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;
import org.sagebionetworks.bixarena.api.model.dto.VotePreferenceDto;
import org.sagebionetworks.bixarena.api.model.dto.VoteSortDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A vote search query.
 */

@Schema(name = "VoteSearchQuery", description = "A vote search query.")
@JsonTypeName("VoteSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class VoteSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private VoteSortDto sort = VoteSortDto.CREATED_AT;

  private SortDirectionDto direction = SortDirectionDto.ASC;

  private @Nullable VotePreferenceDto preference;

  public VoteSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public VoteSearchQueryDto(Integer pageNumber, Integer pageSize, VoteSortDto sort, SortDirectionDto direction) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.sort = sort;
    this.direction = direction;
  }

  public VoteSearchQueryDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * The page number to return (0-indexed).
   * minimum: 0
   * @return pageNumber
   */
  @NotNull @Min(0) 
  @Schema(name = "pageNumber", example = "0", description = "The page number to return (0-indexed).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pageNumber")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public VoteSearchQueryDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * The number of items to return in a single page.
   * minimum: 1
   * @return pageSize
   */
  @NotNull @Min(1) 
  @Schema(name = "pageSize", example = "10", description = "The number of items to return in a single page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public VoteSearchQueryDto sort(VoteSortDto sort) {
    this.sort = sort;
    return this;
  }

  /**
   * Get sort
   * @return sort
   */
  @NotNull @Valid 
  @Schema(name = "sort", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sort")
  public VoteSortDto getSort() {
    return sort;
  }

  public void setSort(VoteSortDto sort) {
    this.sort = sort;
  }

  public VoteSearchQueryDto direction(SortDirectionDto direction) {
    this.direction = direction;
    return this;
  }

  /**
   * Get direction
   * @return direction
   */
  @NotNull @Valid 
  @Schema(name = "direction", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("direction")
  public SortDirectionDto getDirection() {
    return direction;
  }

  public void setDirection(SortDirectionDto direction) {
    this.direction = direction;
  }

  public VoteSearchQueryDto preference(@Nullable VotePreferenceDto preference) {
    this.preference = preference;
    return this;
  }

  /**
   * Get preference
   * @return preference
   */
  @Valid 
  @Schema(name = "preference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preference")
  public @Nullable VotePreferenceDto getPreference() {
    return preference;
  }

  public void setPreference(@Nullable VotePreferenceDto preference) {
    this.preference = preference;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoteSearchQueryDto voteSearchQuery = (VoteSearchQueryDto) o;
    return Objects.equals(this.pageNumber, voteSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, voteSearchQuery.pageSize) &&
        Objects.equals(this.sort, voteSearchQuery.sort) &&
        Objects.equals(this.direction, voteSearchQuery.direction) &&
        Objects.equals(this.preference, voteSearchQuery.preference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, preference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoteSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    preference: ").append(toIndentedString(preference)).append("\n");
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

    private VoteSearchQueryDto instance;

    public Builder() {
      this(new VoteSearchQueryDto());
    }

    protected Builder(VoteSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(VoteSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setPreference(value.preference);
      return this;
    }

    public VoteSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public VoteSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public VoteSearchQueryDto.Builder sort(VoteSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public VoteSearchQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public VoteSearchQueryDto.Builder preference(VotePreferenceDto preference) {
      this.instance.preference(preference);
      return this;
    }
    
    /**
    * returns a built VoteSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public VoteSearchQueryDto build() {
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
  public static VoteSearchQueryDto.Builder builder() {
    return new VoteSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public VoteSearchQueryDto.Builder toBuilder() {
    VoteSearchQueryDto.Builder builder = new VoteSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

