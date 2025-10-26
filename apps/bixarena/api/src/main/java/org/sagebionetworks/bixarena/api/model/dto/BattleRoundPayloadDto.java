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

  private @Nullable MessageCreateDto prompt;

  private @Nullable MessageCreateDto response1;

  private @Nullable MessageCreateDto response2;

  public BattleRoundPayloadDto prompt(@Nullable MessageCreateDto prompt) {
    this.prompt = prompt;
    return this;
  }

  /**
   * Get prompt
   * @return prompt
   */
  @Valid 
  @Schema(name = "prompt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("prompt")
  public @Nullable MessageCreateDto getPrompt() {
    return prompt;
  }

  public void setPrompt(@Nullable MessageCreateDto prompt) {
    this.prompt = prompt;
  }

  public BattleRoundPayloadDto response1(@Nullable MessageCreateDto response1) {
    this.response1 = response1;
    return this;
  }

  /**
   * Get response1
   * @return response1
   */
  @Valid 
  @Schema(name = "response1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("response1")
  public @Nullable MessageCreateDto getResponse1() {
    return response1;
  }

  public void setResponse1(@Nullable MessageCreateDto response1) {
    this.response1 = response1;
  }

  public BattleRoundPayloadDto response2(@Nullable MessageCreateDto response2) {
    this.response2 = response2;
    return this;
  }

  /**
   * Get response2
   * @return response2
   */
  @Valid 
  @Schema(name = "response2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("response2")
  public @Nullable MessageCreateDto getResponse2() {
    return response2;
  }

  public void setResponse2(@Nullable MessageCreateDto response2) {
    this.response2 = response2;
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
    return Objects.equals(this.prompt, battleRoundPayload.prompt) &&
        Objects.equals(this.response1, battleRoundPayload.response1) &&
        Objects.equals(this.response2, battleRoundPayload.response2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prompt, response1, response2);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleRoundPayloadDto {\n");
    sb.append("    prompt: ").append(toIndentedString(prompt)).append("\n");
    sb.append("    response1: ").append(toIndentedString(response1)).append("\n");
    sb.append("    response2: ").append(toIndentedString(response2)).append("\n");
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
      this.instance.setPrompt(value.prompt);
      this.instance.setResponse1(value.response1);
      this.instance.setResponse2(value.response2);
      return this;
    }

    public BattleRoundPayloadDto.Builder prompt(MessageCreateDto prompt) {
      this.instance.prompt(prompt);
      return this;
    }
    
    public BattleRoundPayloadDto.Builder response1(MessageCreateDto response1) {
      this.instance.response1(response1);
      return this;
    }
    
    public BattleRoundPayloadDto.Builder response2(MessageCreateDto response2) {
      this.instance.response2(response2);
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

