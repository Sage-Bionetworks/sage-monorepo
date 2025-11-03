package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A model error entity representing a failure that occurred during model interaction. Used for monitoring error rates and potentially auto-disabling unreliable models. 
 */

@Schema(name = "ModelError", description = "A model error entity representing a failure that occurred during model interaction. Used for monitoring error rates and potentially auto-disabling unreliable models. ")
@JsonTypeName("ModelError")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelErrorDto {

  private String id;

  private String modelId;

  private @Nullable Integer errorCode = null;

  private String errorMessage;

  private @Nullable String battleId = null;

  private @Nullable String roundId = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public ModelErrorDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelErrorDto(String id, String modelId, String errorMessage, OffsetDateTime createdAt) {
    this.id = id;
    this.modelId = modelId;
    this.errorMessage = errorMessage;
    this.createdAt = createdAt;
  }

  public ModelErrorDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier (UUID) of the model error record.
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "98765432-e89b-12d3-a456-426614174099", description = "Unique identifier (UUID) of the model error record.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ModelErrorDto modelId(String modelId) {
    this.modelId = modelId;
    return this;
  }

  /**
   * The ID of the model that experienced the error.
   * @return modelId
   */
  @NotNull 
  @Schema(name = "modelId", example = "123e4567-e89b-12d3-a456-426614174002", description = "The ID of the model that experienced the error.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelId")
  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public ModelErrorDto errorCode(@Nullable Integer errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * HTTP status code from the API response.
   * @return errorCode
   */
  
  @Schema(name = "errorCode", example = "429", description = "HTTP status code from the API response.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errorCode")
  public @Nullable Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(@Nullable Integer errorCode) {
    this.errorCode = errorCode;
  }

  public ModelErrorDto errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * The error message from the API or exception with full details.
   * @return errorMessage
   */
  @NotNull 
  @Schema(name = "errorMessage", example = "Rate limit exceeded", description = "The error message from the API or exception with full details.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("errorMessage")
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ModelErrorDto battleId(@Nullable String battleId) {
    this.battleId = battleId;
    return this;
  }

  /**
   * The battle ID (UUID) if the error occurred during a battle.
   * @return battleId
   */
  
  @Schema(name = "battleId", example = "123e4567-e89b-12d3-a456-426614174000", description = "The battle ID (UUID) if the error occurred during a battle.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("battleId")
  public @Nullable String getBattleId() {
    return battleId;
  }

  public void setBattleId(@Nullable String battleId) {
    this.battleId = battleId;
  }

  public ModelErrorDto roundId(@Nullable String roundId) {
    this.roundId = roundId;
    return this;
  }

  /**
   * The round ID (UUID) if the error occurred during a specific round.
   * @return roundId
   */
  
  @Schema(name = "roundId", example = "123e4567-e89b-12d3-a456-426614174001", description = "The round ID (UUID) if the error occurred during a specific round.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roundId")
  public @Nullable String getRoundId() {
    return roundId;
  }

  public void setRoundId(@Nullable String roundId) {
    this.roundId = roundId;
  }

  public ModelErrorDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * When the error was reported by the Gradio app.
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2025-10-29T21:03:53Z", description = "When the error was reported by the Gradio app.", requiredMode = Schema.RequiredMode.REQUIRED)
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
        Objects.equals(this.errorCode, modelError.errorCode) &&
        Objects.equals(this.errorMessage, modelError.errorMessage) &&
        Objects.equals(this.battleId, modelError.battleId) &&
        Objects.equals(this.roundId, modelError.roundId) &&
        Objects.equals(this.createdAt, modelError.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, modelId, errorCode, errorMessage, battleId, roundId, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelErrorDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
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
      this.instance.setErrorCode(value.errorCode);
      this.instance.setErrorMessage(value.errorMessage);
      this.instance.setBattleId(value.battleId);
      this.instance.setRoundId(value.roundId);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public ModelErrorDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public ModelErrorDto.Builder modelId(String modelId) {
      this.instance.modelId(modelId);
      return this;
    }
    
    public ModelErrorDto.Builder errorCode(Integer errorCode) {
      this.instance.errorCode(errorCode);
      return this;
    }
    
    public ModelErrorDto.Builder errorMessage(String errorMessage) {
      this.instance.errorMessage(errorMessage);
      return this;
    }
    
    public ModelErrorDto.Builder battleId(String battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public ModelErrorDto.Builder roundId(String roundId) {
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

