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
 * The information used to create an example prompt. Newly created prompts are inactive until a reviewer publishes them via PATCH. AI auto-categorization runs asynchronously after creation; reviewers can manually override later via the categorization endpoints.
 */

@Schema(name = "ExamplePromptCreateRequest", description = "The information used to create an example prompt. Newly created prompts are inactive until a reviewer publishes them via PATCH. AI auto-categorization runs asynchronously after creation; reviewers can manually override later via the categorization endpoints.")
@JsonTypeName("ExamplePromptCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ExamplePromptCreateRequestDto {

  private String question;

  private @Nullable ExamplePromptSourceDto source;

  public ExamplePromptCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExamplePromptCreateRequestDto(String question) {
    this.question = question;
  }

  public ExamplePromptCreateRequestDto question(String question) {
    this.question = question;
    return this;
  }

  /**
   * The biomedical question text.
   * @return question
   */
  @NotNull @Size(min = 1, max = 160) 
  @Schema(name = "question", example = "What are the main symptoms of Type 2 diabetes?", description = "The biomedical question text.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("question")
  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public ExamplePromptCreateRequestDto source(@Nullable ExamplePromptSourceDto source) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExamplePromptCreateRequestDto examplePromptCreateRequest = (ExamplePromptCreateRequestDto) o;
    return Objects.equals(this.question, examplePromptCreateRequest.question) &&
        Objects.equals(this.source, examplePromptCreateRequest.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(question, source);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExamplePromptCreateRequestDto {\n");
    sb.append("    question: ").append(toIndentedString(question)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
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

    private ExamplePromptCreateRequestDto instance;

    public Builder() {
      this(new ExamplePromptCreateRequestDto());
    }

    protected Builder(ExamplePromptCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ExamplePromptCreateRequestDto value) { 
      this.instance.setQuestion(value.question);
      this.instance.setSource(value.source);
      return this;
    }

    public ExamplePromptCreateRequestDto.Builder question(String question) {
      this.instance.question(question);
      return this;
    }
    
    public ExamplePromptCreateRequestDto.Builder source(ExamplePromptSourceDto source) {
      this.instance.source(source);
      return this;
    }
    
    /**
    * returns a built ExamplePromptCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ExamplePromptCreateRequestDto build() {
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
  public static ExamplePromptCreateRequestDto.Builder builder() {
    return new ExamplePromptCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ExamplePromptCreateRequestDto.Builder toBuilder() {
    ExamplePromptCreateRequestDto.Builder builder = new ExamplePromptCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

