package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationOutcomeDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to create a new evaluation.
 */

@Schema(name = "EvaluationCreateRequest", description = "The information used to create a new evaluation.")
@JsonTypeName("EvaluationCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class EvaluationCreateRequestDto {

  private EvaluationOutcomeDto outcome;

  public EvaluationCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EvaluationCreateRequestDto(EvaluationOutcomeDto outcome) {
    this.outcome = outcome;
  }

  public EvaluationCreateRequestDto outcome(EvaluationOutcomeDto outcome) {
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
  public EvaluationOutcomeDto getOutcome() {
    return outcome;
  }

  public void setOutcome(EvaluationOutcomeDto outcome) {
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
    EvaluationCreateRequestDto evaluationCreateRequest = (EvaluationCreateRequestDto) o;
    return Objects.equals(this.outcome, evaluationCreateRequest.outcome);
  }

  @Override
  public int hashCode() {
    return Objects.hash(outcome);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvaluationCreateRequestDto {\n");
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

    private EvaluationCreateRequestDto instance;

    public Builder() {
      this(new EvaluationCreateRequestDto());
    }

    protected Builder(EvaluationCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EvaluationCreateRequestDto value) { 
      this.instance.setOutcome(value.outcome);
      return this;
    }

    public EvaluationCreateRequestDto.Builder outcome(EvaluationOutcomeDto outcome) {
      this.instance.outcome(outcome);
      return this;
    }
    
    /**
    * returns a built EvaluationCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EvaluationCreateRequestDto build() {
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
  public static EvaluationCreateRequestDto.Builder builder() {
    return new EvaluationCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EvaluationCreateRequestDto.Builder toBuilder() {
    EvaluationCreateRequestDto.Builder builder = new EvaluationCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

