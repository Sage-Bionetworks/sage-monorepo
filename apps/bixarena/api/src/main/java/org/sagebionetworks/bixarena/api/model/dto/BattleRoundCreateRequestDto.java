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
 * Payload to create a new battle round with the user prompt.
 */

@Schema(name = "BattleRoundCreateRequest", description = "Payload to create a new battle round with the user prompt.")
@JsonTypeName("BattleRoundCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleRoundCreateRequestDto {

  private MessageCreateDto promptMessage;

  public BattleRoundCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleRoundCreateRequestDto(MessageCreateDto promptMessage) {
    this.promptMessage = promptMessage;
  }

  public BattleRoundCreateRequestDto promptMessage(MessageCreateDto promptMessage) {
    this.promptMessage = promptMessage;
    return this;
  }

  /**
   * Get promptMessage
   * @return promptMessage
   */
  @NotNull @Valid 
  @Schema(name = "promptMessage", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promptMessage")
  public MessageCreateDto getPromptMessage() {
    return promptMessage;
  }

  public void setPromptMessage(MessageCreateDto promptMessage) {
    this.promptMessage = promptMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleRoundCreateRequestDto battleRoundCreateRequest = (BattleRoundCreateRequestDto) o;
    return Objects.equals(this.promptMessage, battleRoundCreateRequest.promptMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promptMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleRoundCreateRequestDto {\n");
    sb.append("    promptMessage: ").append(toIndentedString(promptMessage)).append("\n");
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

    private BattleRoundCreateRequestDto instance;

    public Builder() {
      this(new BattleRoundCreateRequestDto());
    }

    protected Builder(BattleRoundCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleRoundCreateRequestDto value) { 
      this.instance.setPromptMessage(value.promptMessage);
      return this;
    }

    public BattleRoundCreateRequestDto.Builder promptMessage(MessageCreateDto promptMessage) {
      this.instance.promptMessage(promptMessage);
      return this;
    }
    
    /**
    * returns a built BattleRoundCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleRoundCreateRequestDto build() {
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
  public static BattleRoundCreateRequestDto.Builder builder() {
    return new BattleRoundCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleRoundCreateRequestDto.Builder toBuilder() {
    BattleRoundCreateRequestDto.Builder builder = new BattleRoundCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

