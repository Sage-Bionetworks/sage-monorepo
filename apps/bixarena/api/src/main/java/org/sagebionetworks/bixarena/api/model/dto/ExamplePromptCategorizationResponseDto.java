package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.BiomedicalCategoryDto;
import org.sagebionetworks.bixarena.api.model.dto.CategorizationStatusDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The result of a categorization run for an example prompt.
 */

@Schema(name = "ExamplePromptCategorizationResponse", description = "The result of a categorization run for an example prompt.")
@JsonTypeName("ExamplePromptCategorizationResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ExamplePromptCategorizationResponseDto {

  private UUID id;

  private UUID promptId;

  private CategorizationStatusDto status;

  private BiomedicalCategoryDto category;

  private String method;

  private @Nullable UUID categorizedBy = null;

  private @Nullable String reason = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public ExamplePromptCategorizationResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExamplePromptCategorizationResponseDto(UUID id, UUID promptId, CategorizationStatusDto status, BiomedicalCategoryDto category, String method, OffsetDateTime createdAt) {
    this.id = id;
    this.promptId = promptId;
    this.status = status;
    this.category = category;
    this.method = method;
    this.createdAt = createdAt;
  }

  public ExamplePromptCategorizationResponseDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ExamplePromptCategorizationResponseDto promptId(UUID promptId) {
    this.promptId = promptId;
    return this;
  }

  /**
   * Get promptId
   * @return promptId
   */
  @NotNull @Valid 
  @Schema(name = "promptId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promptId")
  public UUID getPromptId() {
    return promptId;
  }

  public void setPromptId(UUID promptId) {
    this.promptId = promptId;
  }

  public ExamplePromptCategorizationResponseDto status(CategorizationStatusDto status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public CategorizationStatusDto getStatus() {
    return status;
  }

  public void setStatus(CategorizationStatusDto status) {
    this.status = status;
  }

  public ExamplePromptCategorizationResponseDto category(BiomedicalCategoryDto category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
   */
  @NotNull @Valid 
  @Schema(name = "category", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("category")
  public BiomedicalCategoryDto getCategory() {
    return category;
  }

  public void setCategory(BiomedicalCategoryDto category) {
    this.category = category;
  }

  public ExamplePromptCategorizationResponseDto method(String method) {
    this.method = method;
    return this;
  }

  /**
   * Get method
   * @return method
   */
  @NotNull @Size(max = 100) 
  @Schema(name = "method", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("method")
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public ExamplePromptCategorizationResponseDto categorizedBy(@Nullable UUID categorizedBy) {
    this.categorizedBy = categorizedBy;
    return this;
  }

  /**
   * User ID of the categorizer. Null for AI runs.
   * @return categorizedBy
   */
  @Valid 
  @Schema(name = "categorizedBy", description = "User ID of the categorizer. Null for AI runs.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("categorizedBy")
  public @Nullable UUID getCategorizedBy() {
    return categorizedBy;
  }

  public void setCategorizedBy(@Nullable UUID categorizedBy) {
    this.categorizedBy = categorizedBy;
  }

  public ExamplePromptCategorizationResponseDto reason(@Nullable String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Human override reason. Always null for AI runs.
   * @return reason
   */
  @Size(max = 1000) 
  @Schema(name = "reason", description = "Human override reason. Always null for AI runs.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public @Nullable String getReason() {
    return reason;
  }

  public void setReason(@Nullable String reason) {
    this.reason = reason;
  }

  public ExamplePromptCategorizationResponseDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ExamplePromptCategorizationResponseDto examplePromptCategorizationResponse = (ExamplePromptCategorizationResponseDto) o;
    return Objects.equals(this.id, examplePromptCategorizationResponse.id) &&
        Objects.equals(this.promptId, examplePromptCategorizationResponse.promptId) &&
        Objects.equals(this.status, examplePromptCategorizationResponse.status) &&
        Objects.equals(this.category, examplePromptCategorizationResponse.category) &&
        Objects.equals(this.method, examplePromptCategorizationResponse.method) &&
        Objects.equals(this.categorizedBy, examplePromptCategorizationResponse.categorizedBy) &&
        Objects.equals(this.reason, examplePromptCategorizationResponse.reason) &&
        Objects.equals(this.createdAt, examplePromptCategorizationResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, promptId, status, category, method, categorizedBy, reason, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExamplePromptCategorizationResponseDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    categorizedBy: ").append(toIndentedString(categorizedBy)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
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

    private ExamplePromptCategorizationResponseDto instance;

    public Builder() {
      this(new ExamplePromptCategorizationResponseDto());
    }

    protected Builder(ExamplePromptCategorizationResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ExamplePromptCategorizationResponseDto value) { 
      this.instance.setId(value.id);
      this.instance.setPromptId(value.promptId);
      this.instance.setStatus(value.status);
      this.instance.setCategory(value.category);
      this.instance.setMethod(value.method);
      this.instance.setCategorizedBy(value.categorizedBy);
      this.instance.setReason(value.reason);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public ExamplePromptCategorizationResponseDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public ExamplePromptCategorizationResponseDto.Builder promptId(UUID promptId) {
      this.instance.promptId(promptId);
      return this;
    }
    
    public ExamplePromptCategorizationResponseDto.Builder status(CategorizationStatusDto status) {
      this.instance.status(status);
      return this;
    }
    
    public ExamplePromptCategorizationResponseDto.Builder category(BiomedicalCategoryDto category) {
      this.instance.category(category);
      return this;
    }
    
    public ExamplePromptCategorizationResponseDto.Builder method(String method) {
      this.instance.method(method);
      return this;
    }
    
    public ExamplePromptCategorizationResponseDto.Builder categorizedBy(UUID categorizedBy) {
      this.instance.categorizedBy(categorizedBy);
      return this;
    }
    
    public ExamplePromptCategorizationResponseDto.Builder reason(String reason) {
      this.instance.reason(reason);
      return this;
    }
    
    public ExamplePromptCategorizationResponseDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built ExamplePromptCategorizationResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ExamplePromptCategorizationResponseDto build() {
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
  public static ExamplePromptCategorizationResponseDto.Builder builder() {
    return new ExamplePromptCategorizationResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ExamplePromptCategorizationResponseDto.Builder toBuilder() {
    ExamplePromptCategorizationResponseDto.Builder builder = new ExamplePromptCategorizationResponseDto.Builder();
    return builder.copyOf(this);
  }

}

