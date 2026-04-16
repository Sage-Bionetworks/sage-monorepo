package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.BiomedicalCategoryDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Request to manually categorize an example prompt.
 */

@Schema(name = "ExamplePromptCategorizationCreateRequest", description = "Request to manually categorize an example prompt.")
@JsonTypeName("ExamplePromptCategorizationCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ExamplePromptCategorizationCreateRequestDto {

  @Valid
  private List<BiomedicalCategoryDto> categories = new ArrayList<>();

  private @Nullable String reason = null;

  public ExamplePromptCategorizationCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExamplePromptCategorizationCreateRequestDto(List<BiomedicalCategoryDto> categories) {
    this.categories = categories;
  }

  public ExamplePromptCategorizationCreateRequestDto categories(List<BiomedicalCategoryDto> categories) {
    this.categories = categories;
    return this;
  }

  public ExamplePromptCategorizationCreateRequestDto addCategoriesItem(BiomedicalCategoryDto categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * Get categories
   * @return categories
   */
  @NotNull @Valid @Size(min = 1, max = 3) 
  @Schema(name = "categories", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("categories")
  public List<BiomedicalCategoryDto> getCategories() {
    return categories;
  }

  public void setCategories(List<BiomedicalCategoryDto> categories) {
    this.categories = categories;
  }

  public ExamplePromptCategorizationCreateRequestDto reason(@Nullable String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Reason for the categorization decision
   * @return reason
   */
  @Size(max = 1000) 
  @Schema(name = "reason", description = "Reason for the categorization decision", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public @Nullable String getReason() {
    return reason;
  }

  public void setReason(@Nullable String reason) {
    this.reason = reason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExamplePromptCategorizationCreateRequestDto examplePromptCategorizationCreateRequest = (ExamplePromptCategorizationCreateRequestDto) o;
    return Objects.equals(this.categories, examplePromptCategorizationCreateRequest.categories) &&
        Objects.equals(this.reason, examplePromptCategorizationCreateRequest.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(categories, reason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExamplePromptCategorizationCreateRequestDto {\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
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

    private ExamplePromptCategorizationCreateRequestDto instance;

    public Builder() {
      this(new ExamplePromptCategorizationCreateRequestDto());
    }

    protected Builder(ExamplePromptCategorizationCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ExamplePromptCategorizationCreateRequestDto value) { 
      this.instance.setCategories(value.categories);
      this.instance.setReason(value.reason);
      return this;
    }

    public ExamplePromptCategorizationCreateRequestDto.Builder categories(List<BiomedicalCategoryDto> categories) {
      this.instance.categories(categories);
      return this;
    }
    
    public ExamplePromptCategorizationCreateRequestDto.Builder reason(String reason) {
      this.instance.reason(reason);
      return this;
    }
    
    /**
    * returns a built ExamplePromptCategorizationCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ExamplePromptCategorizationCreateRequestDto build() {
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
  public static ExamplePromptCategorizationCreateRequestDto.Builder builder() {
    return new ExamplePromptCategorizationCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ExamplePromptCategorizationCreateRequestDto.Builder toBuilder() {
    ExamplePromptCategorizationCreateRequestDto.Builder builder = new ExamplePromptCategorizationCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

