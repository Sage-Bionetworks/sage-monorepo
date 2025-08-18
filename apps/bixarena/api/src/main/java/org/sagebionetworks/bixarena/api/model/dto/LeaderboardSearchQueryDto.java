package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSortDto;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A leaderboard search query with pagination and filtering options.
 */

@Schema(name = "LeaderboardSearchQuery", description = "A leaderboard search query with pagination and filtering options.")
@JsonTypeName("LeaderboardSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LeaderboardSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private LeaderboardSortDto sort = LeaderboardSortDto.RANK;

  private SortDirectionDto direction = SortDirectionDto.ASC;

  private @Nullable String search = null;

  private @Nullable String snapshotId = null;

  public LeaderboardSearchQueryDto pageNumber(Integer pageNumber) {
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

  public LeaderboardSearchQueryDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * The number of items in a single page.
   * minimum: 1
   * maximum: 1000
   * @return pageSize
   */
  @Min(1) @Max(1000) 
  @Schema(name = "pageSize", example = "25", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public LeaderboardSearchQueryDto sort(LeaderboardSortDto sort) {
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
  public LeaderboardSortDto getSort() {
    return sort;
  }

  public void setSort(LeaderboardSortDto sort) {
    this.sort = sort;
  }

  public LeaderboardSearchQueryDto direction(SortDirectionDto direction) {
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

  public LeaderboardSearchQueryDto search(@Nullable String search) {
    this.search = search;
    return this;
  }

  /**
   * Search by model name (case-insensitive partial match).
   * @return search
   */
  
  @Schema(name = "search", example = "gpt", description = "Search by model name (case-insensitive partial match).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search")
  public @Nullable String getSearch() {
    return search;
  }

  public void setSearch(@Nullable String search) {
    this.search = search;
  }

  public LeaderboardSearchQueryDto snapshotId(@Nullable String snapshotId) {
    this.snapshotId = snapshotId;
    return this;
  }

  /**
   * Get a specific historical snapshot instead of latest.
   * @return snapshotId
   */
  
  @Schema(name = "snapshotId", example = "snapshot_2025-08-15_10-00", description = "Get a specific historical snapshot instead of latest.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("snapshotId")
  public @Nullable String getSnapshotId() {
    return snapshotId;
  }

  public void setSnapshotId(@Nullable String snapshotId) {
    this.snapshotId = snapshotId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderboardSearchQueryDto leaderboardSearchQuery = (LeaderboardSearchQueryDto) o;
    return Objects.equals(this.pageNumber, leaderboardSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, leaderboardSearchQuery.pageSize) &&
        Objects.equals(this.sort, leaderboardSearchQuery.sort) &&
        Objects.equals(this.direction, leaderboardSearchQuery.direction) &&
        Objects.equals(this.search, leaderboardSearchQuery.search) &&
        Objects.equals(this.snapshotId, leaderboardSearchQuery.snapshotId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, search, snapshotId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderboardSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    snapshotId: ").append(toIndentedString(snapshotId)).append("\n");
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

    private LeaderboardSearchQueryDto instance;

    public Builder() {
      this(new LeaderboardSearchQueryDto());
    }

    protected Builder(LeaderboardSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LeaderboardSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setSearch(value.search);
      this.instance.setSnapshotId(value.snapshotId);
      return this;
    }

    public LeaderboardSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public LeaderboardSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public LeaderboardSearchQueryDto.Builder sort(LeaderboardSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public LeaderboardSearchQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public LeaderboardSearchQueryDto.Builder search(String search) {
      this.instance.search(search);
      return this;
    }
    
    public LeaderboardSearchQueryDto.Builder snapshotId(String snapshotId) {
      this.instance.snapshotId(snapshotId);
      return this;
    }
    
    /**
    * returns a built LeaderboardSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LeaderboardSearchQueryDto build() {
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
  public static LeaderboardSearchQueryDto.Builder builder() {
    return new LeaderboardSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LeaderboardSearchQueryDto.Builder toBuilder() {
    LeaderboardSearchQueryDto.Builder builder = new LeaderboardSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

