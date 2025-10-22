package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.BattleSortDto;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A battle search query.
 */

@Schema(name = "BattleSearchQuery", description = "A battle search query.")
@JsonTypeName("BattleSearchQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleSearchQueryDto {

  private Integer pageNumber = 0;

  private Integer pageSize = 100;

  private BattleSortDto sort = BattleSortDto.CREATED_AT;

  private SortDirectionDto direction = SortDirectionDto.ASC;

  private @Nullable UUID userId = null;

  public BattleSearchQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleSearchQueryDto(Integer pageNumber, Integer pageSize, BattleSortDto sort, SortDirectionDto direction) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.sort = sort;
    this.direction = direction;
  }

  public BattleSearchQueryDto pageNumber(Integer pageNumber) {
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

  public BattleSearchQueryDto pageSize(Integer pageSize) {
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

  public BattleSearchQueryDto sort(BattleSortDto sort) {
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
  public BattleSortDto getSort() {
    return sort;
  }

  public void setSort(BattleSortDto sort) {
    this.sort = sort;
  }

  public BattleSearchQueryDto direction(SortDirectionDto direction) {
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

  public BattleSearchQueryDto userId(@Nullable UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Filter by user ID.
   * @return userId
   */
  @Valid 
  @Schema(name = "userId", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d", description = "Filter by user ID.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public @Nullable UUID getUserId() {
    return userId;
  }

  public void setUserId(@Nullable UUID userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleSearchQueryDto battleSearchQuery = (BattleSearchQueryDto) o;
    return Objects.equals(this.pageNumber, battleSearchQuery.pageNumber) &&
        Objects.equals(this.pageSize, battleSearchQuery.pageSize) &&
        Objects.equals(this.sort, battleSearchQuery.sort) &&
        Objects.equals(this.direction, battleSearchQuery.direction) &&
        Objects.equals(this.userId, battleSearchQuery.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort, direction, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleSearchQueryDto {\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

    private BattleSearchQueryDto instance;

    public Builder() {
      this(new BattleSearchQueryDto());
    }

    protected Builder(BattleSearchQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleSearchQueryDto value) { 
      this.instance.setPageNumber(value.pageNumber);
      this.instance.setPageSize(value.pageSize);
      this.instance.setSort(value.sort);
      this.instance.setDirection(value.direction);
      this.instance.setUserId(value.userId);
      return this;
    }

    public BattleSearchQueryDto.Builder pageNumber(Integer pageNumber) {
      this.instance.pageNumber(pageNumber);
      return this;
    }
    
    public BattleSearchQueryDto.Builder pageSize(Integer pageSize) {
      this.instance.pageSize(pageSize);
      return this;
    }
    
    public BattleSearchQueryDto.Builder sort(BattleSortDto sort) {
      this.instance.sort(sort);
      return this;
    }
    
    public BattleSearchQueryDto.Builder direction(SortDirectionDto direction) {
      this.instance.direction(direction);
      return this;
    }
    
    public BattleSearchQueryDto.Builder userId(UUID userId) {
      this.instance.userId(userId);
      return this;
    }
    
    /**
    * returns a built BattleSearchQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleSearchQueryDto build() {
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
  public static BattleSearchQueryDto.Builder builder() {
    return new BattleSearchQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleSearchQueryDto.Builder toBuilder() {
    BattleSearchQueryDto.Builder builder = new BattleSearchQueryDto.Builder();
    return builder.copyOf(this);
  }

}

