package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardEntryDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of leaderboard entries.
 */

@Schema(name = "LeaderboardEntryPage", description = "A page of leaderboard entries.")
@JsonTypeName("LeaderboardEntryPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LeaderboardEntryPageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  private String snapshotId;

  @Valid
  private List<@Valid LeaderboardEntryDto> entries = new ArrayList<>();

  public LeaderboardEntryPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LeaderboardEntryPageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious, OffsetDateTime updatedAt, String snapshotId, List<@Valid LeaderboardEntryDto> entries) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.updatedAt = updatedAt;
    this.snapshotId = snapshotId;
    this.entries = entries;
  }

  public LeaderboardEntryPageDto number(Integer number) {
    this.number = number;
    return this;
  }

  /**
   * The page number.
   * @return number
   */
  @NotNull 
  @Schema(name = "number", example = "99", description = "The page number.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("number")
  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public LeaderboardEntryPageDto size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * The number of items in a single page.
   * @return size
   */
  @NotNull 
  @Schema(name = "size", example = "99", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public LeaderboardEntryPageDto totalElements(Long totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Total number of elements in the result set.
   * @return totalElements
   */
  @NotNull 
  @Schema(name = "totalElements", example = "99", description = "Total number of elements in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalElements")
  public Long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  public LeaderboardEntryPageDto totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Total number of pages in the result set.
   * @return totalPages
   */
  @NotNull 
  @Schema(name = "totalPages", example = "99", description = "Total number of pages in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalPages")
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public LeaderboardEntryPageDto hasNext(Boolean hasNext) {
    this.hasNext = hasNext;
    return this;
  }

  /**
   * Returns if there is a next page.
   * @return hasNext
   */
  @NotNull 
  @Schema(name = "hasNext", example = "true", description = "Returns if there is a next page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hasNext")
  public Boolean getHasNext() {
    return hasNext;
  }

  public void setHasNext(Boolean hasNext) {
    this.hasNext = hasNext;
  }

  public LeaderboardEntryPageDto hasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
    return this;
  }

  /**
   * Returns if there is a previous page.
   * @return hasPrevious
   */
  @NotNull 
  @Schema(name = "hasPrevious", example = "true", description = "Returns if there is a previous page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hasPrevious")
  public Boolean getHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }

  public LeaderboardEntryPageDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * When this leaderboard was last updated
   * @return updatedAt
   */
  @NotNull @Valid 
  @Schema(name = "updatedAt", example = "2025-08-16T14:30Z", description = "When this leaderboard was last updated", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedAt")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LeaderboardEntryPageDto snapshotId(String snapshotId) {
    this.snapshotId = snapshotId;
    return this;
  }

  /**
   * Identifier for this snapshot/timepoint
   * @return snapshotId
   */
  @NotNull 
  @Schema(name = "snapshotId", example = "snapshot_2025-08-16_14-30", description = "Identifier for this snapshot/timepoint", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("snapshotId")
  public String getSnapshotId() {
    return snapshotId;
  }

  public void setSnapshotId(String snapshotId) {
    this.snapshotId = snapshotId;
  }

  public LeaderboardEntryPageDto entries(List<@Valid LeaderboardEntryDto> entries) {
    this.entries = entries;
    return this;
  }

  public LeaderboardEntryPageDto addEntriesItem(LeaderboardEntryDto entriesItem) {
    if (this.entries == null) {
      this.entries = new ArrayList<>();
    }
    this.entries.add(entriesItem);
    return this;
  }

  /**
   * A list of leaderboard entries.
   * @return entries
   */
  @NotNull @Valid 
  @Schema(name = "entries", description = "A list of leaderboard entries.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("entries")
  public List<@Valid LeaderboardEntryDto> getEntries() {
    return entries;
  }

  public void setEntries(List<@Valid LeaderboardEntryDto> entries) {
    this.entries = entries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderboardEntryPageDto leaderboardEntryPage = (LeaderboardEntryPageDto) o;
    return Objects.equals(this.number, leaderboardEntryPage.number) &&
        Objects.equals(this.size, leaderboardEntryPage.size) &&
        Objects.equals(this.totalElements, leaderboardEntryPage.totalElements) &&
        Objects.equals(this.totalPages, leaderboardEntryPage.totalPages) &&
        Objects.equals(this.hasNext, leaderboardEntryPage.hasNext) &&
        Objects.equals(this.hasPrevious, leaderboardEntryPage.hasPrevious) &&
        Objects.equals(this.updatedAt, leaderboardEntryPage.updatedAt) &&
        Objects.equals(this.snapshotId, leaderboardEntryPage.snapshotId) &&
        Objects.equals(this.entries, leaderboardEntryPage.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, updatedAt, snapshotId, entries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderboardEntryPageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    snapshotId: ").append(toIndentedString(snapshotId)).append("\n");
    sb.append("    entries: ").append(toIndentedString(entries)).append("\n");
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

    private LeaderboardEntryPageDto instance;

    public Builder() {
      this(new LeaderboardEntryPageDto());
    }

    protected Builder(LeaderboardEntryPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LeaderboardEntryPageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setUpdatedAt(value.updatedAt);
      this.instance.setSnapshotId(value.snapshotId);
      this.instance.setEntries(value.entries);
      return this;
    }

    public LeaderboardEntryPageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public LeaderboardEntryPageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public LeaderboardEntryPageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public LeaderboardEntryPageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public LeaderboardEntryPageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public LeaderboardEntryPageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public LeaderboardEntryPageDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    public LeaderboardEntryPageDto.Builder snapshotId(String snapshotId) {
      this.instance.snapshotId(snapshotId);
      return this;
    }
    
    public LeaderboardEntryPageDto.Builder entries(List<LeaderboardEntryDto> entries) {
      this.instance.entries(entries);
      return this;
    }
    
    /**
    * returns a built LeaderboardEntryPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LeaderboardEntryPageDto build() {
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
  public static LeaderboardEntryPageDto.Builder builder() {
    return new LeaderboardEntryPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LeaderboardEntryPageDto.Builder toBuilder() {
    LeaderboardEntryPageDto.Builder builder = new LeaderboardEntryPageDto.Builder();
    return builder.copyOf(this);
  }

}

