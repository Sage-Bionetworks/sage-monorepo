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
 * A battle round linking the prompt and model responses for a battle.
 */

@Schema(name = "BattleRound", description = "A battle round linking the prompt and model responses for a battle.")
@JsonTypeName("BattleRound")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleRoundDto {

  private UUID id;

  private UUID battleId;

  private Integer roundNumber;

  private @Nullable UUID promptMessageId;

  private @Nullable UUID model1MessageId;

  private @Nullable UUID model2MessageId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public BattleRoundDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleRoundDto(UUID id, UUID battleId, Integer roundNumber, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.battleId = battleId;
    this.roundNumber = roundNumber;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public BattleRoundDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier (UUID) of the battle round.
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", description = "Unique identifier (UUID) of the battle round.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public BattleRoundDto battleId(UUID battleId) {
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

  public BattleRoundDto roundNumber(Integer roundNumber) {
    this.roundNumber = roundNumber;
    return this;
  }

  /**
   * Sequential round number within a battle. Starts at 1.
   * minimum: 1
   * @return roundNumber
   */
  @NotNull @Min(1) 
  @Schema(name = "roundNumber", example = "1", description = "Sequential round number within a battle. Starts at 1.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("roundNumber")
  public Integer getRoundNumber() {
    return roundNumber;
  }

  public void setRoundNumber(Integer roundNumber) {
    this.roundNumber = roundNumber;
  }

  public BattleRoundDto promptMessageId(@Nullable UUID promptMessageId) {
    this.promptMessageId = promptMessageId;
    return this;
  }

  /**
   * Unique identifier (UUID) of the message.
   * @return promptMessageId
   */
  @Valid 
  @Schema(name = "promptMessageId", example = "d290f1ee-6c54-4b01-90e6-d701748f0851", description = "Unique identifier (UUID) of the message.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promptMessageId")
  public @Nullable UUID getPromptMessageId() {
    return promptMessageId;
  }

  public void setPromptMessageId(@Nullable UUID promptMessageId) {
    this.promptMessageId = promptMessageId;
  }

  public BattleRoundDto model1MessageId(@Nullable UUID model1MessageId) {
    this.model1MessageId = model1MessageId;
    return this;
  }

  /**
   * Unique identifier (UUID) of the message.
   * @return model1MessageId
   */
  @Valid 
  @Schema(name = "model1MessageId", example = "d290f1ee-6c54-4b01-90e6-d701748f0851", description = "Unique identifier (UUID) of the message.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("model1MessageId")
  public @Nullable UUID getModel1MessageId() {
    return model1MessageId;
  }

  public void setModel1MessageId(@Nullable UUID model1MessageId) {
    this.model1MessageId = model1MessageId;
  }

  public BattleRoundDto model2MessageId(@Nullable UUID model2MessageId) {
    this.model2MessageId = model2MessageId;
    return this;
  }

  /**
   * Unique identifier (UUID) of the message.
   * @return model2MessageId
   */
  @Valid 
  @Schema(name = "model2MessageId", example = "d290f1ee-6c54-4b01-90e6-d701748f0851", description = "Unique identifier (UUID) of the message.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("model2MessageId")
  public @Nullable UUID getModel2MessageId() {
    return model2MessageId;
  }

  public void setModel2MessageId(@Nullable UUID model2MessageId) {
    this.model2MessageId = model2MessageId;
  }

  public BattleRoundDto createdAt(OffsetDateTime createdAt) {
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

  public BattleRoundDto updatedAt(OffsetDateTime updatedAt) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleRoundDto battleRound = (BattleRoundDto) o;
    return Objects.equals(this.id, battleRound.id) &&
        Objects.equals(this.battleId, battleRound.battleId) &&
        Objects.equals(this.roundNumber, battleRound.roundNumber) &&
        Objects.equals(this.promptMessageId, battleRound.promptMessageId) &&
        Objects.equals(this.model1MessageId, battleRound.model1MessageId) &&
        Objects.equals(this.model2MessageId, battleRound.model2MessageId) &&
        Objects.equals(this.createdAt, battleRound.createdAt) &&
        Objects.equals(this.updatedAt, battleRound.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, battleId, roundNumber, promptMessageId, model1MessageId, model2MessageId, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleRoundDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    battleId: ").append(toIndentedString(battleId)).append("\n");
    sb.append("    roundNumber: ").append(toIndentedString(roundNumber)).append("\n");
    sb.append("    promptMessageId: ").append(toIndentedString(promptMessageId)).append("\n");
    sb.append("    model1MessageId: ").append(toIndentedString(model1MessageId)).append("\n");
    sb.append("    model2MessageId: ").append(toIndentedString(model2MessageId)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

    private BattleRoundDto instance;

    public Builder() {
      this(new BattleRoundDto());
    }

    protected Builder(BattleRoundDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleRoundDto value) { 
      this.instance.setId(value.id);
      this.instance.setBattleId(value.battleId);
      this.instance.setRoundNumber(value.roundNumber);
      this.instance.setPromptMessageId(value.promptMessageId);
      this.instance.setModel1MessageId(value.model1MessageId);
      this.instance.setModel2MessageId(value.model2MessageId);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public BattleRoundDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public BattleRoundDto.Builder battleId(UUID battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public BattleRoundDto.Builder roundNumber(Integer roundNumber) {
      this.instance.roundNumber(roundNumber);
      return this;
    }
    
    public BattleRoundDto.Builder promptMessageId(UUID promptMessageId) {
      this.instance.promptMessageId(promptMessageId);
      return this;
    }
    
    public BattleRoundDto.Builder model1MessageId(UUID model1MessageId) {
      this.instance.model1MessageId(model1MessageId);
      return this;
    }
    
    public BattleRoundDto.Builder model2MessageId(UUID model2MessageId) {
      this.instance.model2MessageId(model2MessageId);
      return this;
    }
    
    public BattleRoundDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public BattleRoundDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built BattleRoundDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleRoundDto build() {
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
  public static BattleRoundDto.Builder builder() {
    return new BattleRoundDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleRoundDto.Builder toBuilder() {
    BattleRoundDto.Builder builder = new BattleRoundDto.Builder();
    return builder.copyOf(this);
  }

}

