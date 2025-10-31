package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.ModelDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A battle between two AI models.
 */

@Schema(name = "BattleCreateResponse", description = "A battle between two AI models.")
@JsonTypeName("BattleCreateResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleCreateResponseDto {

  private UUID id;

  private @Nullable String title;

  private UUID userId;

  private ModelDto model1;

  private ModelDto model2;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime endedAt;

  public BattleCreateResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleCreateResponseDto(UUID id, UUID userId, ModelDto model1, ModelDto model2, OffsetDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.model1 = model1;
    this.model2 = model2;
    this.createdAt = createdAt;
  }

  public BattleCreateResponseDto id(UUID id) {
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

  public BattleCreateResponseDto title(@Nullable String title) {
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

  public BattleCreateResponseDto userId(UUID userId) {
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

  public BattleCreateResponseDto model1(ModelDto model1) {
    this.model1 = model1;
    return this;
  }

  /**
   * Get model1
   * @return model1
   */
  @NotNull @Valid 
  @Schema(name = "model1", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model1")
  public ModelDto getModel1() {
    return model1;
  }

  public void setModel1(ModelDto model1) {
    this.model1 = model1;
  }

  public BattleCreateResponseDto model2(ModelDto model2) {
    this.model2 = model2;
    return this;
  }

  /**
   * Get model2
   * @return model2
   */
  @NotNull @Valid 
  @Schema(name = "model2", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model2")
  public ModelDto getModel2() {
    return model2;
  }

  public void setModel2(ModelDto model2) {
    this.model2 = model2;
  }

  public BattleCreateResponseDto createdAt(OffsetDateTime createdAt) {
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

  public BattleCreateResponseDto endedAt(@Nullable OffsetDateTime endedAt) {
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
    BattleCreateResponseDto battleCreateResponse = (BattleCreateResponseDto) o;
    return Objects.equals(this.id, battleCreateResponse.id) &&
        Objects.equals(this.title, battleCreateResponse.title) &&
        Objects.equals(this.userId, battleCreateResponse.userId) &&
        Objects.equals(this.model1, battleCreateResponse.model1) &&
        Objects.equals(this.model2, battleCreateResponse.model2) &&
        Objects.equals(this.createdAt, battleCreateResponse.createdAt) &&
        Objects.equals(this.endedAt, battleCreateResponse.endedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, userId, model1, model2, createdAt, endedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleCreateResponseDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    model1: ").append(toIndentedString(model1)).append("\n");
    sb.append("    model2: ").append(toIndentedString(model2)).append("\n");
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

    private BattleCreateResponseDto instance;

    public Builder() {
      this(new BattleCreateResponseDto());
    }

    protected Builder(BattleCreateResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleCreateResponseDto value) { 
      this.instance.setId(value.id);
      this.instance.setTitle(value.title);
      this.instance.setUserId(value.userId);
      this.instance.setModel1(value.model1);
      this.instance.setModel2(value.model2);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setEndedAt(value.endedAt);
      return this;
    }

    public BattleCreateResponseDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public BattleCreateResponseDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public BattleCreateResponseDto.Builder userId(UUID userId) {
      this.instance.userId(userId);
      return this;
    }
    
    public BattleCreateResponseDto.Builder model1(ModelDto model1) {
      this.instance.model1(model1);
      return this;
    }
    
    public BattleCreateResponseDto.Builder model2(ModelDto model2) {
      this.instance.model2(model2);
      return this;
    }
    
    public BattleCreateResponseDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public BattleCreateResponseDto.Builder endedAt(OffsetDateTime endedAt) {
      this.instance.endedAt(endedAt);
      return this;
    }
    
    /**
    * returns a built BattleCreateResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleCreateResponseDto build() {
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
  public static BattleCreateResponseDto.Builder builder() {
    return new BattleCreateResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleCreateResponseDto.Builder toBuilder() {
    BattleCreateResponseDto.Builder builder = new BattleCreateResponseDto.Builder();
    return builder.copyOf(this);
  }

}

