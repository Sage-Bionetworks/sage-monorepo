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

  private UUID model1Id;

  private UUID model2Id;

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
  public BattleDto(UUID id, UUID userId, UUID model1Id, UUID model2Id, OffsetDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.model1Id = model1Id;
    this.model2Id = model2Id;
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

  public BattleDto model1Id(UUID model1Id) {
    this.model1Id = model1Id;
    return this;
  }

  /**
   * UUID of an AI model.
   * @return model1Id
   */
  @NotNull @Valid 
  @Schema(name = "model1Id", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of an AI model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model1Id")
  public UUID getModel1Id() {
    return model1Id;
  }

  public void setModel1Id(UUID model1Id) {
    this.model1Id = model1Id;
  }

  public BattleDto model2Id(UUID model2Id) {
    this.model2Id = model2Id;
    return this;
  }

  /**
   * UUID of an AI model.
   * @return model2Id
   */
  @NotNull @Valid 
  @Schema(name = "model2Id", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of an AI model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model2Id")
  public UUID getModel2Id() {
    return model2Id;
  }

  public void setModel2Id(UUID model2Id) {
    this.model2Id = model2Id;
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
        Objects.equals(this.model1Id, battle.model1Id) &&
        Objects.equals(this.model2Id, battle.model2Id) &&
        Objects.equals(this.createdAt, battle.createdAt) &&
        Objects.equals(this.endedAt, battle.endedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, userId, model1Id, model2Id, createdAt, endedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    model1Id: ").append(toIndentedString(model1Id)).append("\n");
    sb.append("    model2Id: ").append(toIndentedString(model2Id)).append("\n");
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
      this.instance.setModel1Id(value.model1Id);
      this.instance.setModel2Id(value.model2Id);
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
    
    public BattleDto.Builder model1Id(UUID model1Id) {
      this.instance.model1Id(model1Id);
      return this;
    }
    
    public BattleDto.Builder model2Id(UUID model2Id) {
      this.instance.model2Id(model2Id);
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

