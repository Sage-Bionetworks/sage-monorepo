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
 * A battle evaluation entity representing a user&#39;s assessment in a battle between two AI models.
 */

@Schema(name = "BattleEvaluation", description = "A battle evaluation entity representing a user's assessment in a battle between two AI models.")
@JsonTypeName("BattleEvaluation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleEvaluationDto {

  private UUID id;

  private BattleEvaluationOutcomeDto outcome;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  private Boolean isValid = false;

  private @Nullable String validationError;

  public BattleEvaluationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleEvaluationDto(UUID id, BattleEvaluationOutcomeDto outcome, OffsetDateTime createdAt, Boolean isValid) {
    this.id = id;
    this.outcome = outcome;
    this.createdAt = createdAt;
    this.isValid = isValid;
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

  public BattleEvaluationDto isValid(Boolean isValid) {
    this.isValid = isValid;
    return this;
  }

  /**
   * Indicates whether the resource passed server-side validation.
   * @return isValid
   */
  @NotNull 
  @Schema(name = "is_valid", description = "Indicates whether the resource passed server-side validation.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("is_valid")
  public Boolean getIsValid() {
    return isValid;
  }

  public void setIsValid(Boolean isValid) {
    this.isValid = isValid;
  }

  public BattleEvaluationDto validationError(@Nullable String validationError) {
    this.validationError = validationError;
    return this;
  }

  /**
   * Short validation error message or reason
   * @return validationError
   */
  @Size(max = 1000) 
  @Schema(name = "validation_error", description = "Short validation error message or reason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("validation_error")
  public @Nullable String getValidationError() {
    return validationError;
  }

  public void setValidationError(@Nullable String validationError) {
    this.validationError = validationError;
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
        Objects.equals(this.outcome, battleEvaluation.outcome) &&
        Objects.equals(this.createdAt, battleEvaluation.createdAt) &&
        Objects.equals(this.isValid, battleEvaluation.isValid) &&
        Objects.equals(this.validationError, battleEvaluation.validationError);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, outcome, createdAt, isValid, validationError);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleEvaluationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    outcome: ").append(toIndentedString(outcome)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    isValid: ").append(toIndentedString(isValid)).append("\n");
    sb.append("    validationError: ").append(toIndentedString(validationError)).append("\n");
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
      this.instance.setOutcome(value.outcome);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setIsValid(value.isValid);
      this.instance.setValidationError(value.validationError);
      return this;
    }

    public BattleEvaluationDto.Builder id(UUID id) {
      this.instance.id(id);
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
    
    public BattleEvaluationDto.Builder isValid(Boolean isValid) {
      this.instance.isValid(isValid);
      return this;
    }
    
    public BattleEvaluationDto.Builder validationError(String validationError) {
      this.instance.validationError(validationError);
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

