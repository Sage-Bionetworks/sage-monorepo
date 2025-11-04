package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Request to report a model error.
 */

@Schema(name = "ModelErrorCreateRequest", description = "Request to report a model error.")
@JsonTypeName("ModelErrorCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelErrorCreateRequestDto {

  private @Nullable Integer code = null;

  private String message;

  private UUID battleId;

  private UUID roundId;

  public ModelErrorCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelErrorCreateRequestDto(String message, UUID battleId, UUID roundId) {
    this.message = message;
    this.battleId = battleId;
    this.roundId = roundId;
  }

  public ModelErrorCreateRequestDto code(@Nullable Integer code) {
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

  public ModelErrorCreateRequestDto message(String message) {
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

  public ModelErrorCreateRequestDto battleId(UUID battleId) {
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

  public ModelErrorCreateRequestDto roundId(UUID roundId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelErrorCreateRequestDto modelErrorCreateRequest = (ModelErrorCreateRequestDto) o;
    return Objects.equals(this.code, modelErrorCreateRequest.code) &&
        Objects.equals(this.message, modelErrorCreateRequest.message) &&
        Objects.equals(this.battleId, modelErrorCreateRequest.battleId) &&
        Objects.equals(this.roundId, modelErrorCreateRequest.roundId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, battleId, roundId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelErrorCreateRequestDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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
      this.instance.setCode(value.code);
      this.instance.setMessage(value.message);
      this.instance.setBattleId(value.battleId);
      this.instance.setRoundId(value.roundId);
      return this;
    }

    public ModelErrorCreateRequestDto.Builder code(Integer code) {
      this.instance.code(code);
      return this;
    }
    
    public ModelErrorCreateRequestDto.Builder message(String message) {
      this.instance.message(message);
      return this;
    }
    
    public ModelErrorCreateRequestDto.Builder battleId(UUID battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public ModelErrorCreateRequestDto.Builder roundId(UUID roundId) {
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

