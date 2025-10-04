package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of example prompts.
 */

@Schema(name = "ExamplePromptPage", description = "A page of example prompts.")
@JsonTypeName("ExamplePromptPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ExamplePromptPageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  @Valid
  private List<@Valid ExamplePromptDto> examplePrompts = new ArrayList<>();

  public ExamplePromptPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExamplePromptPageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious, List<@Valid ExamplePromptDto> examplePrompts) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.examplePrompts = examplePrompts;
  }

  public ExamplePromptPageDto number(Integer number) {
    this.number = number;
    return this;
  }

  /**
   * The page number.
   * @return number
   */
  @NotNull 
  @Schema(name = "number", example = "99", description = "The page number.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("number")
  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public ExamplePromptPageDto size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * The number of items in a single page.
   * @return size
   */
  @NotNull 
  @Schema(name = "size", example = "99", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public ExamplePromptPageDto totalElements(Long totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Total number of elements in the result set.
   * @return totalElements
   */
  @NotNull 
  @Schema(name = "totalElements", example = "99", description = "Total number of elements in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalElements")
  public Long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  public ExamplePromptPageDto totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Total number of pages in the result set.
   * @return totalPages
   */
  @NotNull 
  @Schema(name = "totalPages", example = "99", description = "Total number of pages in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalPages")
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public ExamplePromptPageDto hasNext(Boolean hasNext) {
    this.hasNext = hasNext;
    return this;
  }

  /**
   * Returns if there is a next page.
   * @return hasNext
   */
  @NotNull 
  @Schema(name = "hasNext", example = "true", description = "Returns if there is a next page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hasNext")
  public Boolean getHasNext() {
    return hasNext;
  }

  public void setHasNext(Boolean hasNext) {
    this.hasNext = hasNext;
  }

  public ExamplePromptPageDto hasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
    return this;
  }

  /**
   * Returns if there is a previous page.
   * @return hasPrevious
   */
  @NotNull 
  @Schema(name = "hasPrevious", example = "true", description = "Returns if there is a previous page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hasPrevious")
  public Boolean getHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }

  public ExamplePromptPageDto examplePrompts(List<@Valid ExamplePromptDto> examplePrompts) {
    this.examplePrompts = examplePrompts;
    return this;
  }

  public ExamplePromptPageDto addExamplePromptsItem(ExamplePromptDto examplePromptsItem) {
    if (this.examplePrompts == null) {
      this.examplePrompts = new ArrayList<>();
    }
    this.examplePrompts.add(examplePromptsItem);
    return this;
  }

  /**
   * A list of example prompts.
   * @return examplePrompts
   */
  @NotNull @Valid 
  @Schema(name = "examplePrompts", description = "A list of example prompts.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("examplePrompts")
  public List<@Valid ExamplePromptDto> getExamplePrompts() {
    return examplePrompts;
  }

  public void setExamplePrompts(List<@Valid ExamplePromptDto> examplePrompts) {
    this.examplePrompts = examplePrompts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExamplePromptPageDto examplePromptPage = (ExamplePromptPageDto) o;
    return Objects.equals(this.number, examplePromptPage.number) &&
        Objects.equals(this.size, examplePromptPage.size) &&
        Objects.equals(this.totalElements, examplePromptPage.totalElements) &&
        Objects.equals(this.totalPages, examplePromptPage.totalPages) &&
        Objects.equals(this.hasNext, examplePromptPage.hasNext) &&
        Objects.equals(this.hasPrevious, examplePromptPage.hasPrevious) &&
        Objects.equals(this.examplePrompts, examplePromptPage.examplePrompts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, examplePrompts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExamplePromptPageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    examplePrompts: ").append(toIndentedString(examplePrompts)).append("\n");
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

    private ExamplePromptPageDto instance;

    public Builder() {
      this(new ExamplePromptPageDto());
    }

    protected Builder(ExamplePromptPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ExamplePromptPageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setExamplePrompts(value.examplePrompts);
      return this;
    }

    public ExamplePromptPageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public ExamplePromptPageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public ExamplePromptPageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public ExamplePromptPageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public ExamplePromptPageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public ExamplePromptPageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public ExamplePromptPageDto.Builder examplePrompts(List<ExamplePromptDto> examplePrompts) {
      this.instance.examplePrompts(examplePrompts);
      return this;
    }
    
    /**
    * returns a built ExamplePromptPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ExamplePromptPageDto build() {
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
  public static ExamplePromptPageDto.Builder builder() {
    return new ExamplePromptPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ExamplePromptPageDto.Builder toBuilder() {
    ExamplePromptPageDto.Builder builder = new ExamplePromptPageDto.Builder();
    return builder.copyOf(this);
  }

}

