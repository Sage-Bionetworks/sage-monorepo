package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.sagebionetworks.bixarena.api.model.dto.MessageCreateDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Payload to update model responses for an existing battle round.
 */

@Schema(name = "BattleRoundUpdateRequest", description = "Payload to update model responses for an existing battle round.")
@JsonTypeName("BattleRoundUpdateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleRoundUpdateRequestDto {

  private @Nullable MessageCreateDto model1Message;

  private @Nullable MessageCreateDto model2Message;

  public BattleRoundUpdateRequestDto model1Message(@Nullable MessageCreateDto model1Message) {
    this.model1Message = model1Message;
    return this;
  }

  /**
   * Get model1Message
   * @return model1Message
   */
  @Valid 
  @Schema(name = "model1Message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("model1Message")
  public @Nullable MessageCreateDto getModel1Message() {
    return model1Message;
  }

  public void setModel1Message(@Nullable MessageCreateDto model1Message) {
    this.model1Message = model1Message;
  }

  public BattleRoundUpdateRequestDto model2Message(@Nullable MessageCreateDto model2Message) {
    this.model2Message = model2Message;
    return this;
  }

  /**
   * Get model2Message
   * @return model2Message
   */
  @Valid 
  @Schema(name = "model2Message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("model2Message")
  public @Nullable MessageCreateDto getModel2Message() {
    return model2Message;
  }

  public void setModel2Message(@Nullable MessageCreateDto model2Message) {
    this.model2Message = model2Message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleRoundUpdateRequestDto battleRoundUpdateRequest = (BattleRoundUpdateRequestDto) o;
    return Objects.equals(this.model1Message, battleRoundUpdateRequest.model1Message) &&
        Objects.equals(this.model2Message, battleRoundUpdateRequest.model2Message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model1Message, model2Message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleRoundUpdateRequestDto {\n");
    sb.append("    model1Message: ").append(toIndentedString(model1Message)).append("\n");
    sb.append("    model2Message: ").append(toIndentedString(model2Message)).append("\n");
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

    private BattleRoundUpdateRequestDto instance;

    public Builder() {
      this(new BattleRoundUpdateRequestDto());
    }

    protected Builder(BattleRoundUpdateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleRoundUpdateRequestDto value) { 
      this.instance.setModel1Message(value.model1Message);
      this.instance.setModel2Message(value.model2Message);
      return this;
    }

    public BattleRoundUpdateRequestDto.Builder model1Message(MessageCreateDto model1Message) {
      this.instance.model1Message(model1Message);
      return this;
    }
    
    public BattleRoundUpdateRequestDto.Builder model2Message(MessageCreateDto model2Message) {
      this.instance.model2Message(model2Message);
      return this;
    }
    
    /**
    * returns a built BattleRoundUpdateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleRoundUpdateRequestDto build() {
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
  public static BattleRoundUpdateRequestDto.Builder builder() {
    return new BattleRoundUpdateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleRoundUpdateRequestDto.Builder toBuilder() {
    BattleRoundUpdateRequestDto.Builder builder = new BattleRoundUpdateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

