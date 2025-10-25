package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationOutcomeDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * An evaluation entity representing a user&#39;s assessment in a battle between two AI models.
 */

@Schema(name = "Evaluation", description = "An evaluation entity representing a user's assessment in a battle between two AI models.")
@JsonTypeName("Evaluation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class EvaluationDto {

  private UUID id;

  private EvaluationOutcomeDto outcome;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public EvaluationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EvaluationDto(UUID id, EvaluationOutcomeDto outcome, OffsetDateTime createdAt) {
    this.id = id;
    this.outcome = outcome;
    this.createdAt = createdAt;
  }

  public EvaluationDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the evaluation
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", description = "The unique identifier of the evaluation", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public EvaluationDto outcome(EvaluationOutcomeDto outcome) {
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

  public EvaluationDto createdAt(OffsetDateTime createdAt) {
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
    EvaluationDto evaluation = (EvaluationDto) o;
    return Objects.equals(this.id, evaluation.id) &&
        Objects.equals(this.outcome, evaluation.outcome) &&
        Objects.equals(this.createdAt, evaluation.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, outcome, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvaluationDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    outcome: ").append(toIndentedString(outcome)).append("\n");
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

    private EvaluationDto instance;

    public Builder() {
      this(new EvaluationDto());
    }

    protected Builder(EvaluationDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EvaluationDto value) { 
      this.instance.setId(value.id);
      this.instance.setOutcome(value.outcome);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public EvaluationDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public EvaluationDto.Builder outcome(EvaluationOutcomeDto outcome) {
      this.instance.outcome(outcome);
      return this;
    }
    
    public EvaluationDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built EvaluationDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EvaluationDto build() {
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
  public static EvaluationDto.Builder builder() {
    return new EvaluationDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EvaluationDto.Builder toBuilder() {
    EvaluationDto.Builder builder = new EvaluationDto.Builder();
    return builder.copyOf(this);
  }

}

