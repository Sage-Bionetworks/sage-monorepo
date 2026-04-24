package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSourceDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to update an example prompt. Only fields present in the request are modified.
 */

@Schema(name = "ExamplePromptUpdateRequest", description = "The information used to update an example prompt. Only fields present in the request are modified.")
@JsonTypeName("ExamplePromptUpdateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ExamplePromptUpdateRequestDto {

  private @Nullable String question = null;

  private @Nullable ExamplePromptSourceDto source;

  private @Nullable Boolean active = null;

  public ExamplePromptUpdateRequestDto question(@Nullable String question) {
    this.question = question;
    return this;
  }

  /**
   * The biomedical question text.
   * @return question
   */
  @Size(min = 1, max = 160) 
  @Schema(name = "question", example = "What are the main symptoms of Type 2 diabetes?", description = "The biomedical question text.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("question")
  public @Nullable String getQuestion() {
    return question;
  }

  public void setQuestion(@Nullable String question) {
    this.question = question;
  }

  public ExamplePromptUpdateRequestDto source(@Nullable ExamplePromptSourceDto source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   */
  @Valid 
  @Schema(name = "source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("source")
  public @Nullable ExamplePromptSourceDto getSource() {
    return source;
  }

  public void setSource(@Nullable ExamplePromptSourceDto source) {
    this.source = source;
  }

  public ExamplePromptUpdateRequestDto active(@Nullable Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Whether this example prompt is currently active/visible for use.
   * @return active
   */
  
  @Schema(name = "active", example = "true", description = "Whether this example prompt is currently active/visible for use.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public @Nullable Boolean getActive() {
    return active;
  }

  public void setActive(@Nullable Boolean active) {
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExamplePromptUpdateRequestDto examplePromptUpdateRequest = (ExamplePromptUpdateRequestDto) o;
    return Objects.equals(this.question, examplePromptUpdateRequest.question) &&
        Objects.equals(this.source, examplePromptUpdateRequest.source) &&
        Objects.equals(this.active, examplePromptUpdateRequest.active);
  }

  @Override
  public int hashCode() {
    return Objects.hash(question, source, active);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExamplePromptUpdateRequestDto {\n");
    sb.append("    question: ").append(toIndentedString(question)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
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

    private ExamplePromptUpdateRequestDto instance;

    public Builder() {
      this(new ExamplePromptUpdateRequestDto());
    }

    protected Builder(ExamplePromptUpdateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ExamplePromptUpdateRequestDto value) { 
      this.instance.setQuestion(value.question);
      this.instance.setSource(value.source);
      this.instance.setActive(value.active);
      return this;
    }

    public ExamplePromptUpdateRequestDto.Builder question(String question) {
      this.instance.question(question);
      return this;
    }
    
    public ExamplePromptUpdateRequestDto.Builder source(ExamplePromptSourceDto source) {
      this.instance.source(source);
      return this;
    }
    
    public ExamplePromptUpdateRequestDto.Builder active(Boolean active) {
      this.instance.active(active);
      return this;
    }
    
    /**
    * returns a built ExamplePromptUpdateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ExamplePromptUpdateRequestDto build() {
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
  public static ExamplePromptUpdateRequestDto.Builder builder() {
    return new ExamplePromptUpdateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ExamplePromptUpdateRequestDto.Builder toBuilder() {
    ExamplePromptUpdateRequestDto.Builder builder = new ExamplePromptUpdateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

