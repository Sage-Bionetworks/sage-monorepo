package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileProteinDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of differential expression profiles (protein).
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "DifferentialExpressionProfilesProteinPage", description = "A page of differential expression profiles (protein).")
@JsonTypeName("DifferentialExpressionProfilesProteinPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class DifferentialExpressionProfilesProteinPageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  @Valid
  private List<@Valid DifferentialExpressionProfileProteinDto> differentialExpressionProfilesProtein = new ArrayList<>();

  public DifferentialExpressionProfilesProteinPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DifferentialExpressionProfilesProteinPageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious, List<@Valid DifferentialExpressionProfileProteinDto> differentialExpressionProfilesProtein) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.differentialExpressionProfilesProtein = differentialExpressionProfilesProtein;
  }

  public DifferentialExpressionProfilesProteinPageDto number(Integer number) {
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

  public DifferentialExpressionProfilesProteinPageDto size(Integer size) {
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

  public DifferentialExpressionProfilesProteinPageDto totalElements(Long totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Total number of elements in the result set.
   * @return totalElements
   */
  @NotNull 
  @Schema(name = "total_elements", example = "99", description = "Total number of elements in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total_elements")
  public Long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  public DifferentialExpressionProfilesProteinPageDto totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Total number of pages in the result set.
   * @return totalPages
   */
  @NotNull 
  @Schema(name = "total_pages", example = "99", description = "Total number of pages in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total_pages")
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public DifferentialExpressionProfilesProteinPageDto hasNext(Boolean hasNext) {
    this.hasNext = hasNext;
    return this;
  }

  /**
   * Returns if there is a next page.
   * @return hasNext
   */
  @NotNull 
  @Schema(name = "has_next", example = "true", description = "Returns if there is a next page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("has_next")
  public Boolean getHasNext() {
    return hasNext;
  }

  public void setHasNext(Boolean hasNext) {
    this.hasNext = hasNext;
  }

  public DifferentialExpressionProfilesProteinPageDto hasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
    return this;
  }

  /**
   * Returns if there is a previous page.
   * @return hasPrevious
   */
  @NotNull 
  @Schema(name = "has_previous", example = "true", description = "Returns if there is a previous page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("has_previous")
  public Boolean getHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }

  public DifferentialExpressionProfilesProteinPageDto differentialExpressionProfilesProtein(List<@Valid DifferentialExpressionProfileProteinDto> differentialExpressionProfilesProtein) {
    this.differentialExpressionProfilesProtein = differentialExpressionProfilesProtein;
    return this;
  }

  public DifferentialExpressionProfilesProteinPageDto addDifferentialExpressionProfilesProteinItem(DifferentialExpressionProfileProteinDto differentialExpressionProfilesProteinItem) {
    if (this.differentialExpressionProfilesProtein == null) {
      this.differentialExpressionProfilesProtein = new ArrayList<>();
    }
    this.differentialExpressionProfilesProtein.add(differentialExpressionProfilesProteinItem);
    return this;
  }

  /**
   * A list of differential epxression profiles (protein).
   * @return differentialExpressionProfilesProtein
   */
  @NotNull @Valid 
  @Schema(name = "differentialExpressionProfilesProtein", description = "A list of differential epxression profiles (protein).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("differentialExpressionProfilesProtein")
  public List<@Valid DifferentialExpressionProfileProteinDto> getDifferentialExpressionProfilesProtein() {
    return differentialExpressionProfilesProtein;
  }

  public void setDifferentialExpressionProfilesProtein(List<@Valid DifferentialExpressionProfileProteinDto> differentialExpressionProfilesProtein) {
    this.differentialExpressionProfilesProtein = differentialExpressionProfilesProtein;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DifferentialExpressionProfilesProteinPageDto differentialExpressionProfilesProteinPage = (DifferentialExpressionProfilesProteinPageDto) o;
    return Objects.equals(this.number, differentialExpressionProfilesProteinPage.number) &&
        Objects.equals(this.size, differentialExpressionProfilesProteinPage.size) &&
        Objects.equals(this.totalElements, differentialExpressionProfilesProteinPage.totalElements) &&
        Objects.equals(this.totalPages, differentialExpressionProfilesProteinPage.totalPages) &&
        Objects.equals(this.hasNext, differentialExpressionProfilesProteinPage.hasNext) &&
        Objects.equals(this.hasPrevious, differentialExpressionProfilesProteinPage.hasPrevious) &&
        Objects.equals(this.differentialExpressionProfilesProtein, differentialExpressionProfilesProteinPage.differentialExpressionProfilesProtein);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, differentialExpressionProfilesProtein);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DifferentialExpressionProfilesProteinPageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    differentialExpressionProfilesProtein: ").append(toIndentedString(differentialExpressionProfilesProtein)).append("\n");
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

    private DifferentialExpressionProfilesProteinPageDto instance;

    public Builder() {
      this(new DifferentialExpressionProfilesProteinPageDto());
    }

    protected Builder(DifferentialExpressionProfilesProteinPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DifferentialExpressionProfilesProteinPageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setDifferentialExpressionProfilesProtein(value.differentialExpressionProfilesProtein);
      return this;
    }

    public DifferentialExpressionProfilesProteinPageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public DifferentialExpressionProfilesProteinPageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public DifferentialExpressionProfilesProteinPageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public DifferentialExpressionProfilesProteinPageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public DifferentialExpressionProfilesProteinPageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public DifferentialExpressionProfilesProteinPageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public DifferentialExpressionProfilesProteinPageDto.Builder differentialExpressionProfilesProtein(List<DifferentialExpressionProfileProteinDto> differentialExpressionProfilesProtein) {
      this.instance.differentialExpressionProfilesProtein(differentialExpressionProfilesProtein);
      return this;
    }
    
    /**
    * returns a built DifferentialExpressionProfilesProteinPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DifferentialExpressionProfilesProteinPageDto build() {
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
  public static DifferentialExpressionProfilesProteinPageDto.Builder builder() {
    return new DifferentialExpressionProfilesProteinPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DifferentialExpressionProfilesProteinPageDto.Builder toBuilder() {
    DifferentialExpressionProfilesProteinPageDto.Builder builder = new DifferentialExpressionProfilesProteinPageDto.Builder();
    return builder.copyOf(this);
  }

}

