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
 * The request payload for a battle round.
 */

@Schema(name = "BattleRoundPayload", description = "The request payload for a battle round.")
@JsonTypeName("BattleRoundPayload")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleRoundPayloadDto {

  private @Nullable MessageCreateDto promptMessage;

  private @Nullable MessageCreateDto model1Message;

  private @Nullable MessageCreateDto model2Message;

  public BattleRoundPayloadDto promptMessage(@Nullable MessageCreateDto promptMessage) {
    this.promptMessage = promptMessage;
    return this;
  }

  /**
   * Get promptMessage
   * @return promptMessage
   */
  @Valid 
  @Schema(name = "promptMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promptMessage")
  public @Nullable MessageCreateDto getPromptMessage() {
    return promptMessage;
  }

  public void setPromptMessage(@Nullable MessageCreateDto promptMessage) {
    this.promptMessage = promptMessage;
  }

  public BattleRoundPayloadDto model1Message(@Nullable MessageCreateDto model1Message) {
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

  public BattleRoundPayloadDto model2Message(@Nullable MessageCreateDto model2Message) {
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
    BattleRoundPayloadDto battleRoundPayload = (BattleRoundPayloadDto) o;
    return Objects.equals(this.promptMessage, battleRoundPayload.promptMessage) &&
        Objects.equals(this.model1Message, battleRoundPayload.model1Message) &&
        Objects.equals(this.model2Message, battleRoundPayload.model2Message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptMessage, model1Message, model2Message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleRoundPayloadDto {\n");
    sb.append("    promptMessage: ").append(toIndentedString(promptMessage)).append("\n");
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

    private BattleRoundPayloadDto instance;

    public Builder() {
      this(new BattleRoundPayloadDto());
    }

    protected Builder(BattleRoundPayloadDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleRoundPayloadDto value) { 
      this.instance.setPromptMessage(value.promptMessage);
      this.instance.setModel1Message(value.model1Message);
      this.instance.setModel2Message(value.model2Message);
      return this;
    }

    public BattleRoundPayloadDto.Builder promptMessage(MessageCreateDto promptMessage) {
      this.instance.promptMessage(promptMessage);
      return this;
    }
    
    public BattleRoundPayloadDto.Builder model1Message(MessageCreateDto model1Message) {
      this.instance.model1Message(model1Message);
      return this;
    }
    
    public BattleRoundPayloadDto.Builder model2Message(MessageCreateDto model2Message) {
      this.instance.model2Message(model2Message);
      return this;
    }
    
    /**
    * returns a built BattleRoundPayloadDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleRoundPayloadDto build() {
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
  public static BattleRoundPayloadDto.Builder builder() {
    return new BattleRoundPayloadDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleRoundPayloadDto.Builder toBuilder() {
    BattleRoundPayloadDto.Builder builder = new BattleRoundPayloadDto.Builder();
    return builder.copyOf(this);
  }

}

