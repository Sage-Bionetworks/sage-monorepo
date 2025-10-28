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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleEvaluationCreateRequestDto battleEvaluationCreateRequest = (BattleEvaluationCreateRequestDto) o;
    return Objects.equals(this.outcome, battleEvaluationCreateRequest.outcome);
  }

  @Override
  public int hashCode() {
    return Objects.hash(outcome);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleEvaluationCreateRequestDto {\n");
    sb.append("    outcome: ").append(toIndentedString(outcome)).append("\n");
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
      return this;
    }

    public BattleEvaluationCreateRequestDto.Builder outcome(BattleEvaluationOutcomeDto outcome) {
      this.instance.outcome(outcome);
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

