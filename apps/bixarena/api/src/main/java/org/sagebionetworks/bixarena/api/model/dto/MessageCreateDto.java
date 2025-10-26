package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.bixarena.api.model.dto.MessageRoleDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Payload used to create a message.
 */

@Schema(name = "MessageCreate", description = "Payload used to create a message.")
@JsonTypeName("MessageCreate")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MessageCreateDto {

  private MessageRoleDto role;

  private String content;

  public MessageCreateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MessageCreateDto(MessageRoleDto role, String content) {
    this.role = role;
    this.content = content;
  }

  public MessageCreateDto role(MessageRoleDto role) {
    this.role = role;
    return this;
  }

  /**
   * Get role
   * @return role
   */
  @NotNull @Valid 
  @Schema(name = "role", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("role")
  public MessageRoleDto getRole() {
    return role;
  }

  public void setRole(MessageRoleDto role) {
    this.role = role;
  }

  public MessageCreateDto content(String content) {
    this.content = content;
    return this;
  }

  /**
   * The content of a message.
   * @return content
   */
  @NotNull 
  @Schema(name = "content", example = "What is the capital of France?", description = "The content of a message.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageCreateDto messageCreate = (MessageCreateDto) o;
    return Objects.equals(this.role, messageCreate.role) &&
        Objects.equals(this.content, messageCreate.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageCreateDto {\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

    private MessageCreateDto instance;

    public Builder() {
      this(new MessageCreateDto());
    }

    protected Builder(MessageCreateDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MessageCreateDto value) { 
      this.instance.setRole(value.role);
      this.instance.setContent(value.content);
      return this;
    }

    public MessageCreateDto.Builder role(MessageRoleDto role) {
      this.instance.role(role);
      return this;
    }
    
    public MessageCreateDto.Builder content(String content) {
      this.instance.content(content);
      return this;
    }
    
    /**
    * returns a built MessageCreateDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MessageCreateDto build() {
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
  public static MessageCreateDto.Builder builder() {
    return new MessageCreateDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MessageCreateDto.Builder toBuilder() {
    MessageCreateDto.Builder builder = new MessageCreateDto.Builder();
    return builder.copyOf(this);
  }

}

