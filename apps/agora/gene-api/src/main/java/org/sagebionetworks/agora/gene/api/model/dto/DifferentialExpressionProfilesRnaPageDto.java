package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of differential expression profiles (RNA).
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "DifferentialExpressionProfilesRnaPage", description = "A page of differential expression profiles (RNA).")
@JsonTypeName("DifferentialExpressionProfilesRnaPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class DifferentialExpressionProfilesRnaPageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  @Valid
  private List<@Valid DifferentialExpressionProfileRnaDto> differentialExpressionProfilesRna = new ArrayList<>();

  public DifferentialExpressionProfilesRnaPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DifferentialExpressionProfilesRnaPageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious, List<@Valid DifferentialExpressionProfileRnaDto> differentialExpressionProfilesRna) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.differentialExpressionProfilesRna = differentialExpressionProfilesRna;
  }

  public DifferentialExpressionProfilesRnaPageDto number(Integer number) {
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

  public DifferentialExpressionProfilesRnaPageDto size(Integer size) {
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

  public DifferentialExpressionProfilesRnaPageDto totalElements(Long totalElements) {
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

  public DifferentialExpressionProfilesRnaPageDto totalPages(Integer totalPages) {
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

  public DifferentialExpressionProfilesRnaPageDto hasNext(Boolean hasNext) {
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

  public DifferentialExpressionProfilesRnaPageDto hasPrevious(Boolean hasPrevious) {
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

  public DifferentialExpressionProfilesRnaPageDto differentialExpressionProfilesRna(List<@Valid DifferentialExpressionProfileRnaDto> differentialExpressionProfilesRna) {
    this.differentialExpressionProfilesRna = differentialExpressionProfilesRna;
    return this;
  }

  public DifferentialExpressionProfilesRnaPageDto addDifferentialExpressionProfilesRnaItem(DifferentialExpressionProfileRnaDto differentialExpressionProfilesRnaItem) {
    if (this.differentialExpressionProfilesRna == null) {
      this.differentialExpressionProfilesRna = new ArrayList<>();
    }
    this.differentialExpressionProfilesRna.add(differentialExpressionProfilesRnaItem);
    return this;
  }

  /**
   * A list of differential epxression profiles (RNA).
   * @return differentialExpressionProfilesRna
   */
  @NotNull @Valid 
  @Schema(name = "differentialExpressionProfilesRna", description = "A list of differential epxression profiles (RNA).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("differentialExpressionProfilesRna")
  public List<@Valid DifferentialExpressionProfileRnaDto> getDifferentialExpressionProfilesRna() {
    return differentialExpressionProfilesRna;
  }

  public void setDifferentialExpressionProfilesRna(List<@Valid DifferentialExpressionProfileRnaDto> differentialExpressionProfilesRna) {
    this.differentialExpressionProfilesRna = differentialExpressionProfilesRna;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DifferentialExpressionProfilesRnaPageDto differentialExpressionProfilesRnaPage = (DifferentialExpressionProfilesRnaPageDto) o;
    return Objects.equals(this.number, differentialExpressionProfilesRnaPage.number) &&
        Objects.equals(this.size, differentialExpressionProfilesRnaPage.size) &&
        Objects.equals(this.totalElements, differentialExpressionProfilesRnaPage.totalElements) &&
        Objects.equals(this.totalPages, differentialExpressionProfilesRnaPage.totalPages) &&
        Objects.equals(this.hasNext, differentialExpressionProfilesRnaPage.hasNext) &&
        Objects.equals(this.hasPrevious, differentialExpressionProfilesRnaPage.hasPrevious) &&
        Objects.equals(this.differentialExpressionProfilesRna, differentialExpressionProfilesRnaPage.differentialExpressionProfilesRna);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, differentialExpressionProfilesRna);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DifferentialExpressionProfilesRnaPageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    differentialExpressionProfilesRna: ").append(toIndentedString(differentialExpressionProfilesRna)).append("\n");
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

    private DifferentialExpressionProfilesRnaPageDto instance;

    public Builder() {
      this(new DifferentialExpressionProfilesRnaPageDto());
    }

    protected Builder(DifferentialExpressionProfilesRnaPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DifferentialExpressionProfilesRnaPageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setDifferentialExpressionProfilesRna(value.differentialExpressionProfilesRna);
      return this;
    }

    public DifferentialExpressionProfilesRnaPageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public DifferentialExpressionProfilesRnaPageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public DifferentialExpressionProfilesRnaPageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public DifferentialExpressionProfilesRnaPageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public DifferentialExpressionProfilesRnaPageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public DifferentialExpressionProfilesRnaPageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public DifferentialExpressionProfilesRnaPageDto.Builder differentialExpressionProfilesRna(List<DifferentialExpressionProfileRnaDto> differentialExpressionProfilesRna) {
      this.instance.differentialExpressionProfilesRna(differentialExpressionProfilesRna);
      return this;
    }
    
    /**
    * returns a built DifferentialExpressionProfilesRnaPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DifferentialExpressionProfilesRnaPageDto build() {
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
  public static DifferentialExpressionProfilesRnaPageDto.Builder builder() {
    return new DifferentialExpressionProfilesRnaPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DifferentialExpressionProfilesRnaPageDto.Builder toBuilder() {
    DifferentialExpressionProfilesRnaPageDto.Builder builder = new DifferentialExpressionProfilesRnaPageDto.Builder();
    return builder.copyOf(this);
  }

}

