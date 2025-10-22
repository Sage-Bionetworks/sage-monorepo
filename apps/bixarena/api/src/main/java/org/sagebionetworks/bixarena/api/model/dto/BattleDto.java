package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A battle entity representing a comparison between two AI models.
 */

@Schema(name = "Battle", description = "A battle entity representing a comparison between two AI models.")
@JsonTypeName("Battle")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleDto {

  private UUID id;

  private @Nullable String title;

  private UUID userId;

  private UUID leftModelId;

  private UUID rightModelId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime endedAt;

  public BattleDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleDto(UUID id, UUID userId, UUID leftModelId, UUID rightModelId, OffsetDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.leftModelId = leftModelId;
    this.rightModelId = rightModelId;
    this.createdAt = createdAt;
  }

  public BattleDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier (UUID) of the battle.
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", example = "5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f", description = "Unique identifier (UUID) of the battle.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public BattleDto title(@Nullable String title) {
    this.title = title;
    return this;
  }

  /**
   * Title of the battle.
   * @return title
   */
  
  @Schema(name = "title", example = "Gene Expression Analysis Comparison", description = "Title of the battle.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public @Nullable String getTitle() {
    return title;
  }

  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  public BattleDto userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * UUID of a user.
   * @return userId
   */
  @NotNull @Valid 
  @Schema(name = "userId", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of a user.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("userId")
  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public BattleDto leftModelId(UUID leftModelId) {
    this.leftModelId = leftModelId;
    return this;
  }

  /**
   * UUID of an AI model.
   * @return leftModelId
   */
  @NotNull @Valid 
  @Schema(name = "leftModelId", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of an AI model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("leftModelId")
  public UUID getLeftModelId() {
    return leftModelId;
  }

  public void setLeftModelId(UUID leftModelId) {
    this.leftModelId = leftModelId;
  }

  public BattleDto rightModelId(UUID rightModelId) {
    this.rightModelId = rightModelId;
    return this;
  }

  /**
   * UUID of an AI model.
   * @return rightModelId
   */
  @NotNull @Valid 
  @Schema(name = "rightModelId", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of an AI model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rightModelId")
  public UUID getRightModelId() {
    return rightModelId;
  }

  public void setRightModelId(UUID rightModelId) {
    this.rightModelId = rightModelId;
  }

  public BattleDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Timestamp when the entity was created.
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2024-01-15T10:30Z", description = "Timestamp when the entity was created.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public BattleDto endedAt(@Nullable OffsetDateTime endedAt) {
    this.endedAt = endedAt;
    return this;
  }

  /**
   * Timestamp when the entity ended.
   * @return endedAt
   */
  @Valid 
  @Schema(name = "endedAt", example = "2024-01-15T11:45Z", description = "Timestamp when the entity ended.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endedAt")
  public @Nullable OffsetDateTime getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(@Nullable OffsetDateTime endedAt) {
    this.endedAt = endedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleDto battle = (BattleDto) o;
    return Objects.equals(this.id, battle.id) &&
        Objects.equals(this.title, battle.title) &&
        Objects.equals(this.userId, battle.userId) &&
        Objects.equals(this.leftModelId, battle.leftModelId) &&
        Objects.equals(this.rightModelId, battle.rightModelId) &&
        Objects.equals(this.createdAt, battle.createdAt) &&
        Objects.equals(this.endedAt, battle.endedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, userId, leftModelId, rightModelId, createdAt, endedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    leftModelId: ").append(toIndentedString(leftModelId)).append("\n");
    sb.append("    rightModelId: ").append(toIndentedString(rightModelId)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    endedAt: ").append(toIndentedString(endedAt)).append("\n");
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

    private BattleDto instance;

    public Builder() {
      this(new BattleDto());
    }

    protected Builder(BattleDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleDto value) { 
      this.instance.setId(value.id);
      this.instance.setTitle(value.title);
      this.instance.setUserId(value.userId);
      this.instance.setLeftModelId(value.leftModelId);
      this.instance.setRightModelId(value.rightModelId);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setEndedAt(value.endedAt);
      return this;
    }

    public BattleDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public BattleDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public BattleDto.Builder userId(UUID userId) {
      this.instance.userId(userId);
      return this;
    }
    
    public BattleDto.Builder leftModelId(UUID leftModelId) {
      this.instance.leftModelId(leftModelId);
      return this;
    }
    
    public BattleDto.Builder rightModelId(UUID rightModelId) {
      this.instance.rightModelId(rightModelId);
      return this;
    }
    
    public BattleDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public BattleDto.Builder endedAt(OffsetDateTime endedAt) {
      this.instance.endedAt(endedAt);
      return this;
    }
    
    /**
    * returns a built BattleDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleDto build() {
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
  public static BattleDto.Builder builder() {
    return new BattleDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleDto.Builder toBuilder() {
    BattleDto.Builder builder = new BattleDto.Builder();
    return builder.copyOf(this);
  }

}

