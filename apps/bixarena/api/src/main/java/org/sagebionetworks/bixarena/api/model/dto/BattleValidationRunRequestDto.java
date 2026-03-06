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
 * Request to run an automated validation method against a battle.
 */

@Schema(name = "BattleValidationRunRequest", description = "Request to run an automated validation method against a battle.")
@JsonTypeName("BattleValidationRunRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleValidationRunRequestDto {

  private @Nullable String method;

  public BattleValidationRunRequestDto method(@Nullable String method) {
    this.method = method;
    return this;
  }

  /**
   * Validation method to run (e.g. 'openrouter-haiku-v1'). If not specified, the default configured method is used.
   * @return method
   */
  @Size(max = 100) 
  @Schema(name = "method", description = "Validation method to run (e.g. 'openrouter-haiku-v1'). If not specified, the default configured method is used.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("method")
  public @Nullable String getMethod() {
    return method;
  }

  public void setMethod(@Nullable String method) {
    this.method = method;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleValidationRunRequestDto battleValidationRunRequest = (BattleValidationRunRequestDto) o;
    return Objects.equals(this.method, battleValidationRunRequest.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleValidationRunRequestDto {\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
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

    private BattleValidationRunRequestDto instance;

    public Builder() {
      this(new BattleValidationRunRequestDto());
    }

    protected Builder(BattleValidationRunRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleValidationRunRequestDto value) { 
      this.instance.setMethod(value.method);
      return this;
    }

    public BattleValidationRunRequestDto.Builder method(String method) {
      this.instance.method(method);
      return this;
    }
    
    /**
    * returns a built BattleValidationRunRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleValidationRunRequestDto build() {
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
  public static BattleValidationRunRequestDto.Builder builder() {
    return new BattleValidationRunRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleValidationRunRequestDto.Builder toBuilder() {
    BattleValidationRunRequestDto.Builder builder = new BattleValidationRunRequestDto.Builder();
    return builder.copyOf(this);
  }

}

