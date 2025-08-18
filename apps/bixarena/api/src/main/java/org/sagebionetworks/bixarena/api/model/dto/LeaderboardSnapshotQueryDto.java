package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotSortDto;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A query for retrieving leaderboard snapshots.
 */

@Schema(name = "LeaderboardSnapshotQuery", description = "A query for retrieving leaderboard snapshots.")
@JsonTypeName("LeaderboardSnapshotQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LeaderboardSnapshotQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private LeaderboardSnapshotSortDto sort = LeaderboardSnapshotSortDto.CREATED_AT;

  private SortDirectionDto direction = SortDirectionDto.ASC;

  public LeaderboardSnapshotQueryDto pageNumber(Integer pageNumber) {
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

  public LeaderboardSnapshotQueryDto pageSize(Integer pageSize) {
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
  @Schema(name = "pageSize", example = "20", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public LeaderboardSnapshotQueryDto sort(LeaderboardSnapshotSortDto sort) {
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
  public LeaderboardSnapshotSortDto getSort() {
    return sort;
  }

  public void setSort(LeaderboardSnapshotSortDto sort) {
    this.sort = sort;
  }

  public LeaderboardSnapshotQueryDto direction(SortDirectionDto direction) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderboardSnapshotQueryDto leaderboardSnapshotQuery = (LeaderboardSnapshotQueryDto) o;
    return Objects.equals(this.pageNumber, leaderboardSnapshotQuery.pageNumber) &&
        Objects.equals(this.pageSize, leaderboardSnapshotQuery.pageSize) &&
        Objects.equals(this.sort, leaderboardSnapshotQuery.sort) &&
        Objects.equals(this.direction, leaderboardSnapshotQuery.direction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderboardSnapshotQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
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

    private LeaderboardSnapshotQueryDto instance;

    public Builder() {
      this(new LeaderboardSnapshotQueryDto());
    }

    protected Builder(LeaderboardSnapshotQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LeaderboardSnapshotQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      return this;
    }

    public LeaderboardSnapshotQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public LeaderboardSnapshotQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public LeaderboardSnapshotQueryDto.Builder sort(LeaderboardSnapshotSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public LeaderboardSnapshotQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    /**
    * returns a built LeaderboardSnapshotQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LeaderboardSnapshotQueryDto build() {
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
  public static LeaderboardSnapshotQueryDto.Builder builder() {
    return new LeaderboardSnapshotQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LeaderboardSnapshotQueryDto.Builder toBuilder() {
    LeaderboardSnapshotQueryDto.Builder builder = new LeaderboardSnapshotQueryDto.Builder();
    return builder.copyOf(this);
  }

}

