package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import org.sagebionetworks.bixarena.api.model.dto.VisibilityDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A snapshot representing the state of a leaderboard at a specific point in time.
 */

@Schema(name = "LeaderboardSnapshot", description = "A snapshot representing the state of a leaderboard at a specific point in time.")
@JsonTypeName("LeaderboardSnapshot")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LeaderboardSnapshotDto {

  private String id;

  private VisibilityDto visibility = VisibilityDto.PRIVATE;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  private Integer entryCount;

  private @Nullable String description = null;

  public LeaderboardSnapshotDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LeaderboardSnapshotDto(String id, VisibilityDto visibility, OffsetDateTime createdAt, OffsetDateTime updatedAt, Integer entryCount) {
    this.id = id;
    this.visibility = visibility;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.entryCount = entryCount;
  }

  public LeaderboardSnapshotDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for this snapshot
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "snapshot_2025-08-16_14-30", description = "Unique identifier for this snapshot", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LeaderboardSnapshotDto visibility(VisibilityDto visibility) {
    this.visibility = visibility;
    return this;
  }

  /**
   * Get visibility
   * @return visibility
   */
  @NotNull @Valid 
  @Schema(name = "visibility", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("visibility")
  public VisibilityDto getVisibility() {
    return visibility;
  }

  public void setVisibility(VisibilityDto visibility) {
    this.visibility = visibility;
  }

  public LeaderboardSnapshotDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * When this snapshot was created
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2025-08-16T14:30Z", description = "When this snapshot was created", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LeaderboardSnapshotDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Timestamp when the entity was last updated.
   * @return updatedAt
   */
  @NotNull @Valid 
  @Schema(name = "updatedAt", example = "2024-01-15T10:45Z", description = "Timestamp when the entity was last updated.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedAt")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LeaderboardSnapshotDto entryCount(Integer entryCount) {
    this.entryCount = entryCount;
    return this;
  }

  /**
   * Number of models in this snapshot
   * @return entryCount
   */
  @NotNull 
  @Schema(name = "entryCount", example = "50", description = "Number of models in this snapshot", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("entryCount")
  public Integer getEntryCount() {
    return entryCount;
  }

  public void setEntryCount(Integer entryCount) {
    this.entryCount = entryCount;
  }

  public LeaderboardSnapshotDto description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Optional description of this snapshot
   * @return description
   */
  
  @Schema(name = "description", example = "Weekly evaluation run", description = "Optional description of this snapshot", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderboardSnapshotDto leaderboardSnapshot = (LeaderboardSnapshotDto) o;
    return Objects.equals(this.id, leaderboardSnapshot.id) &&
        Objects.equals(this.visibility, leaderboardSnapshot.visibility) &&
        Objects.equals(this.createdAt, leaderboardSnapshot.createdAt) &&
        Objects.equals(this.updatedAt, leaderboardSnapshot.updatedAt) &&
        Objects.equals(this.entryCount, leaderboardSnapshot.entryCount) &&
        Objects.equals(this.description, leaderboardSnapshot.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, visibility, createdAt, updatedAt, entryCount, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderboardSnapshotDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    entryCount: ").append(toIndentedString(entryCount)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

    private LeaderboardSnapshotDto instance;

    public Builder() {
      this(new LeaderboardSnapshotDto());
    }

    protected Builder(LeaderboardSnapshotDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LeaderboardSnapshotDto value) { 
      this.instance.setId(value.id);
      this.instance.setVisibility(value.visibility);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setUpdatedAt(value.updatedAt);
      this.instance.setEntryCount(value.entryCount);
      this.instance.setDescription(value.description);
      return this;
    }

    public LeaderboardSnapshotDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public LeaderboardSnapshotDto.Builder visibility(VisibilityDto visibility) {
      this.instance.visibility(visibility);
      return this;
    }
    
    public LeaderboardSnapshotDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public LeaderboardSnapshotDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    public LeaderboardSnapshotDto.Builder entryCount(Integer entryCount) {
      this.instance.entryCount(entryCount);
      return this;
    }
    
    public LeaderboardSnapshotDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    /**
    * returns a built LeaderboardSnapshotDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LeaderboardSnapshotDto build() {
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
  public static LeaderboardSnapshotDto.Builder builder() {
    return new LeaderboardSnapshotDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LeaderboardSnapshotDto.Builder toBuilder() {
    LeaderboardSnapshotDto.Builder builder = new LeaderboardSnapshotDto.Builder();
    return builder.copyOf(this);
  }

}

