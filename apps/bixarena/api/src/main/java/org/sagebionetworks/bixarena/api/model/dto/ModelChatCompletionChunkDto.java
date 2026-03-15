package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.ModelChatUsageDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A streaming event chunk from the model chat completion
 */

@Schema(name = "ModelChatCompletionChunk", description = "A streaming event chunk from the model chat completion")
@JsonTypeName("ModelChatCompletionChunk")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelChatCompletionChunkDto {

  private @Nullable String content;

  /**
   * Current state of the streaming response
   */
  public enum StatusEnum {
    STREAMING("streaming"),
    
    COMPLETE("complete"),
    
    ERROR("error");

    private final String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  /**
   * Reason the model stopped generating
   */
  public enum FinishReasonEnum {
    STOP("stop"),
    
    LENGTH("length"),
    
    CONTENT_FILTER("content_filter"),
    
    ERROR("error");

    private final String value;

    FinishReasonEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static FinishReasonEnum fromValue(String value) {
      for (FinishReasonEnum b : FinishReasonEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  private @Nullable FinishReasonEnum finishReason = null;

  private @Nullable String errorMessage;

  private @Nullable ModelChatUsageDto usage;

  public ModelChatCompletionChunkDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelChatCompletionChunkDto(StatusEnum status) {
    this.status = status;
  }

  public ModelChatCompletionChunkDto content(@Nullable String content) {
    this.content = content;
    return this;
  }

  /**
   * The content of a message.
   * @return content
   */
  @Size(max = 10000) 
  @Schema(name = "content", example = "What is the capital of France?", description = "The content of a message.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public @Nullable String getContent() {
    return content;
  }

  public void setContent(@Nullable String content) {
    this.content = content;
  }

  public ModelChatCompletionChunkDto status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Current state of the streaming response
   * @return status
   */
  @NotNull 
  @Schema(name = "status", description = "Current state of the streaming response", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public ModelChatCompletionChunkDto finishReason(@Nullable FinishReasonEnum finishReason) {
    this.finishReason = finishReason;
    return this;
  }

  /**
   * Reason the model stopped generating
   * @return finishReason
   */
  
  @Schema(name = "finishReason", description = "Reason the model stopped generating", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("finishReason")
  public @Nullable FinishReasonEnum getFinishReason() {
    return finishReason;
  }

  public void setFinishReason(@Nullable FinishReasonEnum finishReason) {
    this.finishReason = finishReason;
  }

  public ModelChatCompletionChunkDto errorMessage(@Nullable String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * Error message describing what went wrong.
   * @return errorMessage
   */
  @Size(min = 1, max = 1000) 
  @Schema(name = "errorMessage", example = "Rate limit exceeded", description = "Error message describing what went wrong.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errorMessage")
  public @Nullable String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(@Nullable String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ModelChatCompletionChunkDto usage(@Nullable ModelChatUsageDto usage) {
    this.usage = usage;
    return this;
  }

  /**
   * Get usage
   * @return usage
   */
  @Valid 
  @Schema(name = "usage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("usage")
  public @Nullable ModelChatUsageDto getUsage() {
    return usage;
  }

  public void setUsage(@Nullable ModelChatUsageDto usage) {
    this.usage = usage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelChatCompletionChunkDto modelChatCompletionChunk = (ModelChatCompletionChunkDto) o;
    return Objects.equals(this.content, modelChatCompletionChunk.content) &&
        Objects.equals(this.status, modelChatCompletionChunk.status) &&
        Objects.equals(this.finishReason, modelChatCompletionChunk.finishReason) &&
        Objects.equals(this.errorMessage, modelChatCompletionChunk.errorMessage) &&
        Objects.equals(this.usage, modelChatCompletionChunk.usage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, status, finishReason, errorMessage, usage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelChatCompletionChunkDto {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    finishReason: ").append(toIndentedString(finishReason)).append("\n");
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
    sb.append("    usage: ").append(toIndentedString(usage)).append("\n");
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

    private ModelChatCompletionChunkDto instance;

    public Builder() {
      this(new ModelChatCompletionChunkDto());
    }

    protected Builder(ModelChatCompletionChunkDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelChatCompletionChunkDto value) { 
      this.instance.setContent(value.content);
      this.instance.setStatus(value.status);
      this.instance.setFinishReason(value.finishReason);
      this.instance.setErrorMessage(value.errorMessage);
      this.instance.setUsage(value.usage);
      return this;
    }

    public ModelChatCompletionChunkDto.Builder content(String content) {
      this.instance.content(content);
      return this;
    }
    
    public ModelChatCompletionChunkDto.Builder status(StatusEnum status) {
      this.instance.status(status);
      return this;
    }
    
    public ModelChatCompletionChunkDto.Builder finishReason(FinishReasonEnum finishReason) {
      this.instance.finishReason(finishReason);
      return this;
    }
    
    public ModelChatCompletionChunkDto.Builder errorMessage(String errorMessage) {
      this.instance.errorMessage(errorMessage);
      return this;
    }
    
    public ModelChatCompletionChunkDto.Builder usage(ModelChatUsageDto usage) {
      this.instance.usage(usage);
      return this;
    }
    
    /**
    * returns a built ModelChatCompletionChunkDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelChatCompletionChunkDto build() {
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
  public static ModelChatCompletionChunkDto.Builder builder() {
    return new ModelChatCompletionChunkDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelChatCompletionChunkDto.Builder toBuilder() {
    ModelChatCompletionChunkDto.Builder builder = new ModelChatCompletionChunkDto.Builder();
    return builder.copyOf(this);
  }

}

