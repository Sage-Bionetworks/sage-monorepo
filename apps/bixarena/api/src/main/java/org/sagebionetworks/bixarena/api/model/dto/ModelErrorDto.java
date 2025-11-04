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
 * Record of a model error that occurred during interaction.
 */

@Schema(name = "ModelError", description = "Record of a model error that occurred during interaction.")
@JsonTypeName("ModelError")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelErrorDto {

  private UUID id;

  private UUID modelId;

  private @Nullable Integer code = null;

  private String message;

  private UUID battleId;

  private UUID roundId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public ModelErrorDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelErrorDto(UUID id, UUID modelId, String message, UUID battleId, UUID roundId, OffsetDateTime createdAt) {
    this.id = id;
    this.modelId = modelId;
    this.message = message;
    this.battleId = battleId;
    this.roundId = roundId;
    this.createdAt = createdAt;
  }

  public ModelErrorDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of the model error record.
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", example = "98765432-e89b-12d3-a456-426614174099", description = "Unique identifier of the model error record.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ModelErrorDto modelId(UUID modelId) {
    this.modelId = modelId;
    return this;
  }

  /**
   * UUID of an AI model.
   * @return modelId
   */
  @NotNull @Valid 
  @Schema(name = "modelId", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of an AI model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelId")
  public UUID getModelId() {
    return modelId;
  }

  public void setModelId(UUID modelId) {
    this.modelId = modelId;
  }

  public ModelErrorDto code(@Nullable Integer code) {
    this.code = code;
    return this;
  }

  /**
   * HTTP status code from the error response.
   * @return code
   */
  
  @Schema(name = "code", example = "429", description = "HTTP status code from the error response.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public @Nullable Integer getCode() {
    return code;
  }

  public void setCode(@Nullable Integer code) {
    this.code = code;
  }

  public ModelErrorDto message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Error message describing what went wrong.
   * @return message
   */
  @NotNull @Size(min = 1, max = 1000) 
  @Schema(name = "message", example = "Rate limit exceeded", description = "Error message describing what went wrong.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ModelErrorDto battleId(UUID battleId) {
    this.battleId = battleId;
    return this;
  }

  /**
   * Unique identifier (UUID) of the battle.
   * @return battleId
   */
  @NotNull @Valid 
  @Schema(name = "battleId", example = "5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f", description = "Unique identifier (UUID) of the battle.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("battleId")
  public UUID getBattleId() {
    return battleId;
  }

  public void setBattleId(UUID battleId) {
    this.battleId = battleId;
  }

  public ModelErrorDto roundId(UUID roundId) {
    this.roundId = roundId;
    return this;
  }

  /**
   * Unique identifier (UUID) of the battle round.
   * @return roundId
   */
  @NotNull @Valid 
  @Schema(name = "roundId", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", description = "Unique identifier (UUID) of the battle round.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("roundId")
  public UUID getRoundId() {
    return roundId;
  }

  public void setRoundId(UUID roundId) {
    this.roundId = roundId;
  }

  public ModelErrorDto createdAt(OffsetDateTime createdAt) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelErrorDto modelError = (ModelErrorDto) o;
    return Objects.equals(this.id, modelError.id) &&
        Objects.equals(this.modelId, modelError.modelId) &&
        Objects.equals(this.code, modelError.code) &&
        Objects.equals(this.message, modelError.message) &&
        Objects.equals(this.battleId, modelError.battleId) &&
        Objects.equals(this.roundId, modelError.roundId) &&
        Objects.equals(this.createdAt, modelError.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, modelId, code, message, battleId, roundId, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelErrorDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    battleId: ").append(toIndentedString(battleId)).append("\n");
    sb.append("    roundId: ").append(toIndentedString(roundId)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

    private ModelErrorDto instance;

    public Builder() {
      this(new ModelErrorDto());
    }

    protected Builder(ModelErrorDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelErrorDto value) { 
      this.instance.setId(value.id);
      this.instance.setModelId(value.modelId);
      this.instance.setCode(value.code);
      this.instance.setMessage(value.message);
      this.instance.setBattleId(value.battleId);
      this.instance.setRoundId(value.roundId);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public ModelErrorDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public ModelErrorDto.Builder modelId(UUID modelId) {
      this.instance.modelId(modelId);
      return this;
    }
    
    public ModelErrorDto.Builder code(Integer code) {
      this.instance.code(code);
      return this;
    }
    
    public ModelErrorDto.Builder message(String message) {
      this.instance.message(message);
      return this;
    }
    
    public ModelErrorDto.Builder battleId(UUID battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public ModelErrorDto.Builder roundId(UUID roundId) {
      this.instance.roundId(roundId);
      return this;
    }
    
    public ModelErrorDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built ModelErrorDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelErrorDto build() {
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
  public static ModelErrorDto.Builder builder() {
    return new ModelErrorDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelErrorDto.Builder toBuilder() {
    ModelErrorDto.Builder builder = new ModelErrorDto.Builder();
    return builder.copyOf(this);
  }

}

