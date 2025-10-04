package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSourceDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A self-contained example prompt for biomedical question answering.
 */

@Schema(name = "ExamplePrompt", description = "A self-contained example prompt for biomedical question answering.")
@JsonTypeName("ExamplePrompt")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ExamplePromptDto {

  private String id;

  private String question;

  private ExamplePromptSourceDto source;

  private Boolean active;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public ExamplePromptDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExamplePromptDto(String id, String question, ExamplePromptSourceDto source, Boolean active, OffsetDateTime createdAt) {
    this.id = id;
    this.question = question;
    this.source = source;
    this.active = active;
    this.createdAt = createdAt;
  }

  public ExamplePromptDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the example prompt.
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "123e4567-e89b-12d3-a456-426614174000", description = "The unique identifier of the example prompt.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ExamplePromptDto question(String question) {
    this.question = question;
    return this;
  }

  /**
   * The biomedical question text.
   * @return question
   */
  @NotNull @Size(min = 1, max = 1000) 
  @Schema(name = "question", example = "What are the main symptoms of Type 2 diabetes?", description = "The biomedical question text.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("question")
  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public ExamplePromptDto source(ExamplePromptSourceDto source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   */
  @NotNull @Valid 
  @Schema(name = "source", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("source")
  public ExamplePromptSourceDto getSource() {
    return source;
  }

  public void setSource(ExamplePromptSourceDto source) {
    this.source = source;
  }

  public ExamplePromptDto active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Whether this example prompt is currently active/visible for use.
   * @return active
   */
  @NotNull 
  @Schema(name = "active", example = "true", description = "Whether this example prompt is currently active/visible for use.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public ExamplePromptDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * When the example prompt was created.
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2025-08-01T09:00Z", description = "When the example prompt was created.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExamplePromptDto examplePrompt = (ExamplePromptDto) o;
    return Objects.equals(this.id, examplePrompt.id) &&
        Objects.equals(this.question, examplePrompt.question) &&
        Objects.equals(this.source, examplePrompt.source) &&
        Objects.equals(this.active, examplePrompt.active) &&
        Objects.equals(this.createdAt, examplePrompt.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, question, source, active, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExamplePromptDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    question: ").append(toIndentedString(question)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

    private ExamplePromptDto instance;

    public Builder() {
      this(new ExamplePromptDto());
    }

    protected Builder(ExamplePromptDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ExamplePromptDto value) { 
      this.instance.setId(value.id);
      this.instance.setQuestion(value.question);
      this.instance.setSource(value.source);
      this.instance.setActive(value.active);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public ExamplePromptDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public ExamplePromptDto.Builder question(String question) {
      this.instance.question(question);
      return this;
    }
    
    public ExamplePromptDto.Builder source(ExamplePromptSourceDto source) {
      this.instance.source(source);
      return this;
    }
    
    public ExamplePromptDto.Builder active(Boolean active) {
      this.instance.active(active);
      return this;
    }
    
    public ExamplePromptDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built ExamplePromptDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ExamplePromptDto build() {
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
  public static ExamplePromptDto.Builder builder() {
    return new ExamplePromptDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ExamplePromptDto.Builder toBuilder() {
    ExamplePromptDto.Builder builder = new ExamplePromptDto.Builder();
    return builder.copyOf(this);
  }

}

