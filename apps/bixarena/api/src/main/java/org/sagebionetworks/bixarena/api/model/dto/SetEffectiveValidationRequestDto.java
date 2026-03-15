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
 * Request to set or clear the effective validation for a battle.
 */

@Schema(name = "SetEffectiveValidationRequest", description = "Request to set or clear the effective validation for a battle.")
@JsonTypeName("SetEffectiveValidationRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class SetEffectiveValidationRequestDto {

  private @Nullable UUID validationId = null;

  public SetEffectiveValidationRequestDto validationId(@Nullable UUID validationId) {
    this.validationId = validationId;
    return this;
  }

  /**
   * ID of the battle validation to set as effective. Set to null to clear the effective validation.
   * @return validationId
   */
  @Valid 
  @Schema(name = "validationId", description = "ID of the battle validation to set as effective. Set to null to clear the effective validation.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("validationId")
  public @Nullable UUID getValidationId() {
    return validationId;
  }

  public void setValidationId(@Nullable UUID validationId) {
    this.validationId = validationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SetEffectiveValidationRequestDto setEffectiveValidationRequest = (SetEffectiveValidationRequestDto) o;
    return Objects.equals(this.validationId, setEffectiveValidationRequest.validationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(validationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SetEffectiveValidationRequestDto {\n");
    sb.append("    validationId: ").append(toIndentedString(validationId)).append("\n");
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

    private SetEffectiveValidationRequestDto instance;

    public Builder() {
      this(new SetEffectiveValidationRequestDto());
    }

    protected Builder(SetEffectiveValidationRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(SetEffectiveValidationRequestDto value) { 
      this.instance.setValidationId(value.validationId);
      return this;
    }

    public SetEffectiveValidationRequestDto.Builder validationId(UUID validationId) {
      this.instance.validationId(validationId);
      return this;
    }
    
    /**
    * returns a built SetEffectiveValidationRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public SetEffectiveValidationRequestDto build() {
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
  public static SetEffectiveValidationRequestDto.Builder builder() {
    return new SetEffectiveValidationRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public SetEffectiveValidationRequestDto.Builder toBuilder() {
    SetEffectiveValidationRequestDto.Builder builder = new SetEffectiveValidationRequestDto.Builder();
    return builder.copyOf(this);
  }

}

