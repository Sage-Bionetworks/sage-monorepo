package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of EDAM concepts.
 */

@Schema(name = "EdamConceptsPage", description = "A page of EDAM concepts.")
@JsonTypeName("EdamConceptsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class EdamConceptsPageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  @Valid
  private List<@Valid EdamConceptDto> edamConcepts = new ArrayList<>();

  public EdamConceptsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EdamConceptsPageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious, List<@Valid EdamConceptDto> edamConcepts) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.edamConcepts = edamConcepts;
  }

  public EdamConceptsPageDto number(Integer number) {
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

  public EdamConceptsPageDto size(Integer size) {
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

  public EdamConceptsPageDto totalElements(Long totalElements) {
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

  public EdamConceptsPageDto totalPages(Integer totalPages) {
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

  public EdamConceptsPageDto hasNext(Boolean hasNext) {
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

  public EdamConceptsPageDto hasPrevious(Boolean hasPrevious) {
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

  public EdamConceptsPageDto edamConcepts(List<@Valid EdamConceptDto> edamConcepts) {
    this.edamConcepts = edamConcepts;
    return this;
  }

  public EdamConceptsPageDto addEdamConceptsItem(EdamConceptDto edamConceptsItem) {
    if (this.edamConcepts == null) {
      this.edamConcepts = new ArrayList<>();
    }
    this.edamConcepts.add(edamConceptsItem);
    return this;
  }

  /**
   * A list of EDAM concepts.
   * @return edamConcepts
   */
  @NotNull @Valid 
  @Schema(name = "edamConcepts", description = "A list of EDAM concepts.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("edamConcepts")
  public List<@Valid EdamConceptDto> getEdamConcepts() {
    return edamConcepts;
  }

  public void setEdamConcepts(List<@Valid EdamConceptDto> edamConcepts) {
    this.edamConcepts = edamConcepts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EdamConceptsPageDto edamConceptsPage = (EdamConceptsPageDto) o;
    return Objects.equals(this.number, edamConceptsPage.number) &&
        Objects.equals(this.size, edamConceptsPage.size) &&
        Objects.equals(this.totalElements, edamConceptsPage.totalElements) &&
        Objects.equals(this.totalPages, edamConceptsPage.totalPages) &&
        Objects.equals(this.hasNext, edamConceptsPage.hasNext) &&
        Objects.equals(this.hasPrevious, edamConceptsPage.hasPrevious) &&
        Objects.equals(this.edamConcepts, edamConceptsPage.edamConcepts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, edamConcepts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EdamConceptsPageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    edamConcepts: ").append(toIndentedString(edamConcepts)).append("\n");
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

    private EdamConceptsPageDto instance;

    public Builder() {
      this(new EdamConceptsPageDto());
    }

    protected Builder(EdamConceptsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EdamConceptsPageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setEdamConcepts(value.edamConcepts);
      return this;
    }

    public EdamConceptsPageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public EdamConceptsPageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public EdamConceptsPageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public EdamConceptsPageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public EdamConceptsPageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public EdamConceptsPageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public EdamConceptsPageDto.Builder edamConcepts(List<EdamConceptDto> edamConcepts) {
      this.instance.edamConcepts(edamConcepts);
      return this;
    }
    
    /**
    * returns a built EdamConceptsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EdamConceptsPageDto build() {
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
  public static EdamConceptsPageDto.Builder builder() {
    return new EdamConceptsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EdamConceptsPageDto.Builder toBuilder() {
    EdamConceptsPageDto.Builder builder = new EdamConceptsPageDto.Builder();
    return builder.copyOf(this);
  }

}

