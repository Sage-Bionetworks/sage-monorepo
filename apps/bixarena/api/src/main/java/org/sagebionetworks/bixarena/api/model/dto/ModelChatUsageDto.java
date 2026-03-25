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
 * Token usage and generation parameters for the chat completion
 */

@Schema(name = "ModelChatUsage", description = "Token usage and generation parameters for the chat completion")
@JsonTypeName("ModelChatUsage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelChatUsageDto {

  private @Nullable String model;

  private @Nullable Float temperature;

  private @Nullable Float topP;

  private @Nullable Integer maxTokens;

  private Integer promptTokens;

  private Integer completionTokens;

  private Integer totalTokens;

  public ModelChatUsageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelChatUsageDto(Integer promptTokens, Integer completionTokens, Integer totalTokens) {
    this.promptTokens = promptTokens;
    this.completionTokens = completionTokens;
    this.totalTokens = totalTokens;
  }

  public ModelChatUsageDto model(@Nullable String model) {
    this.model = model;
    return this;
  }

  /**
   * The model identifier that was used
   * @return model
   */
  
  @Schema(name = "model", description = "The model identifier that was used", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("model")
  public @Nullable String getModel() {
    return model;
  }

  public void setModel(@Nullable String model) {
    this.model = model;
  }

  public ModelChatUsageDto temperature(@Nullable Float temperature) {
    this.temperature = temperature;
    return this;
  }

  /**
   * The sampling temperature that was used
   * @return temperature
   */
  
  @Schema(name = "temperature", description = "The sampling temperature that was used", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("temperature")
  public @Nullable Float getTemperature() {
    return temperature;
  }

  public void setTemperature(@Nullable Float temperature) {
    this.temperature = temperature;
  }

  public ModelChatUsageDto topP(@Nullable Float topP) {
    this.topP = topP;
    return this;
  }

  /**
   * The nucleus sampling parameter that was used
   * @return topP
   */
  
  @Schema(name = "topP", description = "The nucleus sampling parameter that was used", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("topP")
  public @Nullable Float getTopP() {
    return topP;
  }

  public void setTopP(@Nullable Float topP) {
    this.topP = topP;
  }

  public ModelChatUsageDto maxTokens(@Nullable Integer maxTokens) {
    this.maxTokens = maxTokens;
    return this;
  }

  /**
   * The maximum token limit that was set
   * @return maxTokens
   */
  
  @Schema(name = "maxTokens", description = "The maximum token limit that was set", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxTokens")
  public @Nullable Integer getMaxTokens() {
    return maxTokens;
  }

  public void setMaxTokens(@Nullable Integer maxTokens) {
    this.maxTokens = maxTokens;
  }

  public ModelChatUsageDto promptTokens(Integer promptTokens) {
    this.promptTokens = promptTokens;
    return this;
  }

  /**
   * Number of tokens in the prompt
   * @return promptTokens
   */
  @NotNull 
  @Schema(name = "promptTokens", description = "Number of tokens in the prompt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promptTokens")
  public Integer getPromptTokens() {
    return promptTokens;
  }

  public void setPromptTokens(Integer promptTokens) {
    this.promptTokens = promptTokens;
  }

  public ModelChatUsageDto completionTokens(Integer completionTokens) {
    this.completionTokens = completionTokens;
    return this;
  }

  /**
   * Number of tokens in the completion
   * @return completionTokens
   */
  @NotNull 
  @Schema(name = "completionTokens", description = "Number of tokens in the completion", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("completionTokens")
  public Integer getCompletionTokens() {
    return completionTokens;
  }

  public void setCompletionTokens(Integer completionTokens) {
    this.completionTokens = completionTokens;
  }

  public ModelChatUsageDto totalTokens(Integer totalTokens) {
    this.totalTokens = totalTokens;
    return this;
  }

  /**
   * Total number of tokens used
   * @return totalTokens
   */
  @NotNull 
  @Schema(name = "totalTokens", description = "Total number of tokens used", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalTokens")
  public Integer getTotalTokens() {
    return totalTokens;
  }

  public void setTotalTokens(Integer totalTokens) {
    this.totalTokens = totalTokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelChatUsageDto modelChatUsage = (ModelChatUsageDto) o;
    return Objects.equals(this.model, modelChatUsage.model) &&
        Objects.equals(this.temperature, modelChatUsage.temperature) &&
        Objects.equals(this.topP, modelChatUsage.topP) &&
        Objects.equals(this.maxTokens, modelChatUsage.maxTokens) &&
        Objects.equals(this.promptTokens, modelChatUsage.promptTokens) &&
        Objects.equals(this.completionTokens, modelChatUsage.completionTokens) &&
        Objects.equals(this.totalTokens, modelChatUsage.totalTokens);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, temperature, topP, maxTokens, promptTokens, completionTokens, totalTokens);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelChatUsageDto {\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    temperature: ").append(toIndentedString(temperature)).append("\n");
    sb.append("    topP: ").append(toIndentedString(topP)).append("\n");
    sb.append("    maxTokens: ").append(toIndentedString(maxTokens)).append("\n");
    sb.append("    promptTokens: ").append(toIndentedString(promptTokens)).append("\n");
    sb.append("    completionTokens: ").append(toIndentedString(completionTokens)).append("\n");
    sb.append("    totalTokens: ").append(toIndentedString(totalTokens)).append("\n");
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

    private ModelChatUsageDto instance;

    public Builder() {
      this(new ModelChatUsageDto());
    }

    protected Builder(ModelChatUsageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelChatUsageDto value) { 
      this.instance.setModel(value.model);
      this.instance.setTemperature(value.temperature);
      this.instance.setTopP(value.topP);
      this.instance.setMaxTokens(value.maxTokens);
      this.instance.setPromptTokens(value.promptTokens);
      this.instance.setCompletionTokens(value.completionTokens);
      this.instance.setTotalTokens(value.totalTokens);
      return this;
    }

    public ModelChatUsageDto.Builder model(String model) {
      this.instance.model(model);
      return this;
    }
    
    public ModelChatUsageDto.Builder temperature(Float temperature) {
      this.instance.temperature(temperature);
      return this;
    }
    
    public ModelChatUsageDto.Builder topP(Float topP) {
      this.instance.topP(topP);
      return this;
    }
    
    public ModelChatUsageDto.Builder maxTokens(Integer maxTokens) {
      this.instance.maxTokens(maxTokens);
      return this;
    }
    
    public ModelChatUsageDto.Builder promptTokens(Integer promptTokens) {
      this.instance.promptTokens(promptTokens);
      return this;
    }
    
    public ModelChatUsageDto.Builder completionTokens(Integer completionTokens) {
      this.instance.completionTokens(completionTokens);
      return this;
    }
    
    public ModelChatUsageDto.Builder totalTokens(Integer totalTokens) {
      this.instance.totalTokens(totalTokens);
      return this;
    }
    
    /**
    * returns a built ModelChatUsageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelChatUsageDto build() {
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
  public static ModelChatUsageDto.Builder builder() {
    return new ModelChatUsageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelChatUsageDto.Builder toBuilder() {
    ModelChatUsageDto.Builder builder = new ModelChatUsageDto.Builder();
    return builder.copyOf(this);
  }

}

