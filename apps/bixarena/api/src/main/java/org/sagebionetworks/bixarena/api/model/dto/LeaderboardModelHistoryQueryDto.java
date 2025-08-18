package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardHistorySortDto;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A query for retrieving historical leaderboard data for a model.
 */

@Schema(name = "LeaderboardModelHistoryQuery", description = "A query for retrieving historical leaderboard data for a model.")
@JsonTypeName("LeaderboardModelHistoryQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LeaderboardModelHistoryQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private LeaderboardHistorySortDto sort = LeaderboardHistorySortDto.CREATED_AT;

  private SortDirectionDto direction = SortDirectionDto.ASC;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate fromDate = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate toDate = null;

  public LeaderboardModelHistoryQueryDto pageNumber(Integer pageNumber) {
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

  public LeaderboardModelHistoryQueryDto pageSize(Integer pageSize) {
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
  @Schema(name = "pageSize", example = "50", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public LeaderboardModelHistoryQueryDto sort(LeaderboardHistorySortDto sort) {
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
  public LeaderboardHistorySortDto getSort() {
    return sort;
  }

  public void setSort(LeaderboardHistorySortDto sort) {
    this.sort = sort;
  }

  public LeaderboardModelHistoryQueryDto direction(SortDirectionDto direction) {
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

  public LeaderboardModelHistoryQueryDto fromDate(@Nullable LocalDate fromDate) {
    this.fromDate = fromDate;
    return this;
  }

  /**
   * Include only entries created on or after this date.
   * @return fromDate
   */
  @Valid 
  @Schema(name = "fromDate", example = "2025-08-01", description = "Include only entries created on or after this date.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fromDate")
  public @Nullable LocalDate getFromDate() {
    return fromDate;
  }

  public void setFromDate(@Nullable LocalDate fromDate) {
    this.fromDate = fromDate;
  }

  public LeaderboardModelHistoryQueryDto toDate(@Nullable LocalDate toDate) {
    this.toDate = toDate;
    return this;
  }

  /**
   * Include only entries created on or before this date.
   * @return toDate
   */
  @Valid 
  @Schema(name = "toDate", example = "2025-08-16", description = "Include only entries created on or before this date.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("toDate")
  public @Nullable LocalDate getToDate() {
    return toDate;
  }

  public void setToDate(@Nullable LocalDate toDate) {
    this.toDate = toDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderboardModelHistoryQueryDto leaderboardModelHistoryQuery = (LeaderboardModelHistoryQueryDto) o;
    return Objects.equals(this.pageNumber, leaderboardModelHistoryQuery.pageNumber) &&
        Objects.equals(this.pageSize, leaderboardModelHistoryQuery.pageSize) &&
        Objects.equals(this.sort, leaderboardModelHistoryQuery.sort) &&
        Objects.equals(this.direction, leaderboardModelHistoryQuery.direction) &&
        Objects.equals(this.fromDate, leaderboardModelHistoryQuery.fromDate) &&
        Objects.equals(this.toDate, leaderboardModelHistoryQuery.toDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, fromDate, toDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderboardModelHistoryQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    fromDate: ").append(toIndentedString(fromDate)).append("\n");
    sb.append("    toDate: ").append(toIndentedString(toDate)).append("\n");
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

    private LeaderboardModelHistoryQueryDto instance;

    public Builder() {
      this(new LeaderboardModelHistoryQueryDto());
    }

    protected Builder(LeaderboardModelHistoryQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LeaderboardModelHistoryQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setFromDate(value.fromDate);
      this.instance.setToDate(value.toDate);
      return this;
    }

    public LeaderboardModelHistoryQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public LeaderboardModelHistoryQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public LeaderboardModelHistoryQueryDto.Builder sort(LeaderboardHistorySortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public LeaderboardModelHistoryQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public LeaderboardModelHistoryQueryDto.Builder fromDate(LocalDate fromDate) {
      this.instance.fromDate(fromDate);
      return this;
    }
    
    public LeaderboardModelHistoryQueryDto.Builder toDate(LocalDate toDate) {
      this.instance.toDate(toDate);
      return this;
    }
    
    /**
    * returns a built LeaderboardModelHistoryQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LeaderboardModelHistoryQueryDto build() {
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
  public static LeaderboardModelHistoryQueryDto.Builder builder() {
    return new LeaderboardModelHistoryQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LeaderboardModelHistoryQueryDto.Builder toBuilder() {
    LeaderboardModelHistoryQueryDto.Builder builder = new LeaderboardModelHistoryQueryDto.Builder();
    return builder.copyOf(this);
  }

}

