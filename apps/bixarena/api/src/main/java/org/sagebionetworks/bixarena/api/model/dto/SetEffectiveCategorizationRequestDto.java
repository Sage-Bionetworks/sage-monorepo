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
 * Request to set or clear the effective categorization for an example prompt or battle.
 */

@Schema(name = "SetEffectiveCategorizationRequest", description = "Request to set or clear the effective categorization for an example prompt or battle.")
@JsonTypeName("SetEffectiveCategorizationRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class SetEffectiveCategorizationRequestDto {

  private @Nullable UUID categorizationId = null;

  public SetEffectiveCategorizationRequestDto categorizationId(@Nullable UUID categorizationId) {
    this.categorizationId = categorizationId;
    return this;
  }

  /**
   * ID of the categorization row to set as effective. Set to null to clear the effective categorization.
   * @return categorizationId
   */
  @Valid 
  @Schema(name = "categorizationId", description = "ID of the categorization row to set as effective. Set to null to clear the effective categorization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("categorizationId")
  public @Nullable UUID getCategorizationId() {
    return categorizationId;
  }

  public void setCategorizationId(@Nullable UUID categorizationId) {
    this.categorizationId = categorizationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SetEffectiveCategorizationRequestDto setEffectiveCategorizationRequest = (SetEffectiveCategorizationRequestDto) o;
    return Objects.equals(this.categorizationId, setEffectiveCategorizationRequest.categorizationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(categorizationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SetEffectiveCategorizationRequestDto {\n");
    sb.append("    categorizationId: ").append(toIndentedString(categorizationId)).append("\n");
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

    private SetEffectiveCategorizationRequestDto instance;

    public Builder() {
      this(new SetEffectiveCategorizationRequestDto());
    }

    protected Builder(SetEffectiveCategorizationRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(SetEffectiveCategorizationRequestDto value) { 
      this.instance.setCategorizationId(value.categorizationId);
      return this;
    }

    public SetEffectiveCategorizationRequestDto.Builder categorizationId(UUID categorizationId) {
      this.instance.categorizationId(categorizationId);
      return this;
    }
    
    /**
    * returns a built SetEffectiveCategorizationRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public SetEffectiveCategorizationRequestDto build() {
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
  public static SetEffectiveCategorizationRequestDto.Builder builder() {
    return new SetEffectiveCategorizationRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public SetEffectiveCategorizationRequestDto.Builder toBuilder() {
    SetEffectiveCategorizationRequestDto.Builder builder = new SetEffectiveCategorizationRequestDto.Builder();
    return builder.copyOf(this);
  }

}

