package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Request body for reporting a model error from the Gradio app. This enables tracking model failures, monitoring error rates, and potentially auto-disabling unreliable models. 
 */

@Schema(name = "ModelErrorCreateRequest", description = "Request body for reporting a model error from the Gradio app. This enables tracking model failures, monitoring error rates, and potentially auto-disabling unreliable models. ")
@JsonTypeName("ModelErrorCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelErrorCreateRequestDto {

  private @Nullable Integer errorCode = null;

  private String errorMessage;

  private @Nullable String battleId = null;

  private @Nullable String roundId = null;

  public ModelErrorCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelErrorCreateRequestDto(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ModelErrorCreateRequestDto errorCode(@Nullable Integer errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * HTTP status code from the API response (400, 401, 402, 403, 408, 429, 502, 503). May be null for network errors or client-side exceptions.
   * @return errorCode
   */
  
  @Schema(name = "errorCode", example = "429", description = "HTTP status code from the API response (400, 401, 402, 403, 408, 429, 502, 503). May be null for network errors or client-side exceptions.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errorCode")
  public @Nullable Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(@Nullable Integer errorCode) {
    this.errorCode = errorCode;
  }

  public ModelErrorCreateRequestDto errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * The error message from the API or exception with full details
   * @return errorMessage
   */
  @NotNull @Size(min = 1, max = 1000) 
  @Schema(name = "errorMessage", example = "Rate limit exceeded", description = "The error message from the API or exception with full details", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("errorMessage")
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ModelErrorCreateRequestDto battleId(@Nullable String battleId) {
    this.battleId = battleId;
    return this;
  }

  /**
   * The battle ID (UUID) if the error occurred during a battle (for tracking impact)
   * @return battleId
   */
  
  @Schema(name = "battleId", example = "123e4567-e89b-12d3-a456-426614174000", description = "The battle ID (UUID) if the error occurred during a battle (for tracking impact)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("battleId")
  public @Nullable String getBattleId() {
    return battleId;
  }

  public void setBattleId(@Nullable String battleId) {
    this.battleId = battleId;
  }

  public ModelErrorCreateRequestDto roundId(@Nullable String roundId) {
    this.roundId = roundId;
    return this;
  }

  /**
   * The round ID (UUID) if the error occurred during a specific round
   * @return roundId
   */
  
  @Schema(name = "roundId", example = "123e4567-e89b-12d3-a456-426614174001", description = "The round ID (UUID) if the error occurred during a specific round", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roundId")
  public @Nullable String getRoundId() {
    return roundId;
  }

  public void setRoundId(@Nullable String roundId) {
    this.roundId = roundId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelErrorCreateRequestDto modelErrorCreateRequest = (ModelErrorCreateRequestDto) o;
    return Objects.equals(this.errorCode, modelErrorCreateRequest.errorCode) &&
        Objects.equals(this.errorMessage, modelErrorCreateRequest.errorMessage) &&
        Objects.equals(this.battleId, modelErrorCreateRequest.battleId) &&
        Objects.equals(this.roundId, modelErrorCreateRequest.roundId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorCode, errorMessage, battleId, roundId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelErrorCreateRequestDto {\n");
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
    sb.append("    battleId: ").append(toIndentedString(battleId)).append("\n");
    sb.append("    roundId: ").append(toIndentedString(roundId)).append("\n");
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

    private ModelErrorCreateRequestDto instance;

    public Builder() {
      this(new ModelErrorCreateRequestDto());
    }

    protected Builder(ModelErrorCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelErrorCreateRequestDto value) { 
      this.instance.setErrorCode(value.errorCode);
      this.instance.setErrorMessage(value.errorMessage);
      this.instance.setBattleId(value.battleId);
      this.instance.setRoundId(value.roundId);
      return this;
    }

    public ModelErrorCreateRequestDto.Builder errorCode(Integer errorCode) {
      this.instance.errorCode(errorCode);
      return this;
    }
    
    public ModelErrorCreateRequestDto.Builder errorMessage(String errorMessage) {
      this.instance.errorMessage(errorMessage);
      return this;
    }
    
    public ModelErrorCreateRequestDto.Builder battleId(String battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public ModelErrorCreateRequestDto.Builder roundId(String roundId) {
      this.instance.roundId(roundId);
      return this;
    }
    
    /**
    * returns a built ModelErrorCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelErrorCreateRequestDto build() {
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
  public static ModelErrorCreateRequestDto.Builder builder() {
    return new ModelErrorCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelErrorCreateRequestDto.Builder toBuilder() {
    ModelErrorCreateRequestDto.Builder builder = new ModelErrorCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

