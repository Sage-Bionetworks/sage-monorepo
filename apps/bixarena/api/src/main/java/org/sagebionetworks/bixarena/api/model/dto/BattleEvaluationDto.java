package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationOutcomeDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A battle evaluation describing the outcome of a matchup.
 */

@Schema(name = "BattleEvaluation", description = "A battle evaluation describing the outcome of a matchup.")
@JsonTypeName("BattleEvaluation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleEvaluationDto {

  private UUID id;

  private UUID battleId;

  private BattleEvaluationOutcomeDto outcome;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  private Boolean valid = false;

  public BattleEvaluationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleEvaluationDto(UUID id, UUID battleId, BattleEvaluationOutcomeDto outcome, OffsetDateTime createdAt, Boolean valid) {
    this.id = id;
    this.battleId = battleId;
    this.outcome = outcome;
    this.createdAt = createdAt;
    this.valid = valid;
  }

  public BattleEvaluationDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the battle evaluation
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", description = "The unique identifier of the battle evaluation", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public BattleEvaluationDto battleId(UUID battleId) {
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

  public BattleEvaluationDto outcome(BattleEvaluationOutcomeDto outcome) {
    this.outcome = outcome;
    return this;
  }

  /**
   * Get outcome
   * @return outcome
   */
  @NotNull @Valid 
  @Schema(name = "outcome", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("outcome")
  public BattleEvaluationOutcomeDto getOutcome() {
    return outcome;
  }

  public void setOutcome(BattleEvaluationOutcomeDto outcome) {
    this.outcome = outcome;
  }

  public BattleEvaluationDto createdAt(OffsetDateTime createdAt) {
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

  public BattleEvaluationDto valid(Boolean valid) {
    this.valid = valid;
    return this;
  }

  /**
   * Indicates whether the battle evaluation passed the configured validation checks.
   * @return valid
   */
  @NotNull 
  @Schema(name = "valid", description = "Indicates whether the battle evaluation passed the configured validation checks.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("valid")
  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleEvaluationDto battleEvaluation = (BattleEvaluationDto) o;
    return Objects.equals(this.id, battleEvaluation.id) &&
        Objects.equals(this.battleId, battleEvaluation.battleId) &&
        Objects.equals(this.outcome, battleEvaluation.outcome) &&
        Objects.equals(this.createdAt, battleEvaluation.createdAt) &&
        Objects.equals(this.valid, battleEvaluation.valid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, battleId, outcome, createdAt, valid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleEvaluationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    battleId: ").append(toIndentedString(battleId)).append("\n");
    sb.append("    outcome: ").append(toIndentedString(outcome)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    valid: ").append(toIndentedString(valid)).append("\n");
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

    private BattleEvaluationDto instance;

    public Builder() {
      this(new BattleEvaluationDto());
    }

    protected Builder(BattleEvaluationDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleEvaluationDto value) { 
      this.instance.setId(value.id);
      this.instance.setBattleId(value.battleId);
      this.instance.setOutcome(value.outcome);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setValid(value.valid);
      return this;
    }

    public BattleEvaluationDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public BattleEvaluationDto.Builder battleId(UUID battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public BattleEvaluationDto.Builder outcome(BattleEvaluationOutcomeDto outcome) {
      this.instance.outcome(outcome);
      return this;
    }
    
    public BattleEvaluationDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public BattleEvaluationDto.Builder valid(Boolean valid) {
      this.instance.valid(valid);
      return this;
    }
    
    /**
    * returns a built BattleEvaluationDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleEvaluationDto build() {
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
  public static BattleEvaluationDto.Builder builder() {
    return new BattleEvaluationDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleEvaluationDto.Builder toBuilder() {
    BattleEvaluationDto.Builder builder = new BattleEvaluationDto.Builder();
    return builder.copyOf(this);
  }

}

