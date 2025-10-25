package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationOutcomeDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to create a new battle evaluation.
 */

@Schema(name = "BattleEvaluationCreateRequest", description = "The information used to create a new battle evaluation.")
@JsonTypeName("BattleEvaluationCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleEvaluationCreateRequestDto {

  private BattleEvaluationOutcomeDto outcome;

  private Boolean isValid = false;

  private @Nullable String validationError;

  public BattleEvaluationCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleEvaluationCreateRequestDto(BattleEvaluationOutcomeDto outcome) {
    this.outcome = outcome;
  }

  public BattleEvaluationCreateRequestDto outcome(BattleEvaluationOutcomeDto outcome) {
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

  public BattleEvaluationCreateRequestDto isValid(Boolean isValid) {
    this.isValid = isValid;
    return this;
  }

  /**
   * Indicates whether the resource passed server-side validation.
   * @return isValid
   */
  
  @Schema(name = "is_valid", description = "Indicates whether the resource passed server-side validation.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_valid")
  public Boolean getIsValid() {
    return isValid;
  }

  public void setIsValid(Boolean isValid) {
    this.isValid = isValid;
  }

  public BattleEvaluationCreateRequestDto validationError(@Nullable String validationError) {
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
    BattleEvaluationCreateRequestDto battleEvaluationCreateRequest = (BattleEvaluationCreateRequestDto) o;
    return Objects.equals(this.outcome, battleEvaluationCreateRequest.outcome) &&
        Objects.equals(this.isValid, battleEvaluationCreateRequest.isValid) &&
        Objects.equals(this.validationError, battleEvaluationCreateRequest.validationError);
  }

  @Override
  public int hashCode() {
    return Objects.hash(outcome, isValid, validationError);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleEvaluationCreateRequestDto {\n");
    sb.append("    outcome: ").append(toIndentedString(outcome)).append("\n");
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

    private BattleEvaluationCreateRequestDto instance;

    public Builder() {
      this(new BattleEvaluationCreateRequestDto());
    }

    protected Builder(BattleEvaluationCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleEvaluationCreateRequestDto value) { 
      this.instance.setOutcome(value.outcome);
      this.instance.setIsValid(value.isValid);
      this.instance.setValidationError(value.validationError);
      return this;
    }

    public BattleEvaluationCreateRequestDto.Builder outcome(BattleEvaluationOutcomeDto outcome) {
      this.instance.outcome(outcome);
      return this;
    }
    
    public BattleEvaluationCreateRequestDto.Builder isValid(Boolean isValid) {
      this.instance.isValid(isValid);
      return this;
    }
    
    public BattleEvaluationCreateRequestDto.Builder validationError(String validationError) {
      this.instance.validationError(validationError);
      return this;
    }
    
    /**
    * returns a built BattleEvaluationCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleEvaluationCreateRequestDto build() {
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
  public static BattleEvaluationCreateRequestDto.Builder builder() {
    return new BattleEvaluationCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleEvaluationCreateRequestDto.Builder toBuilder() {
    BattleEvaluationCreateRequestDto.Builder builder = new BattleEvaluationCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

