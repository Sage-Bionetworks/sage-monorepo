package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.gene.api.model.dto.ProteinDifferentialExpressionProfileDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of protein differential expression profiles.
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "ProteinDifferentialExpressionProfilePage", description = "A page of protein differential expression profiles.")
@JsonTypeName("ProteinDifferentialExpressionProfilePage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ProteinDifferentialExpressionProfilePageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  @Valid
  private List<@Valid ProteinDifferentialExpressionProfileDto> proteinDifferentialExpressionProfiles = new ArrayList<>();

  public ProteinDifferentialExpressionProfilePageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProteinDifferentialExpressionProfilePageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious, List<@Valid ProteinDifferentialExpressionProfileDto> proteinDifferentialExpressionProfiles) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.proteinDifferentialExpressionProfiles = proteinDifferentialExpressionProfiles;
  }

  public ProteinDifferentialExpressionProfilePageDto number(Integer number) {
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

  public ProteinDifferentialExpressionProfilePageDto size(Integer size) {
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

  public ProteinDifferentialExpressionProfilePageDto totalElements(Long totalElements) {
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

  public ProteinDifferentialExpressionProfilePageDto totalPages(Integer totalPages) {
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

  public ProteinDifferentialExpressionProfilePageDto hasNext(Boolean hasNext) {
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

  public ProteinDifferentialExpressionProfilePageDto hasPrevious(Boolean hasPrevious) {
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

  public ProteinDifferentialExpressionProfilePageDto proteinDifferentialExpressionProfiles(List<@Valid ProteinDifferentialExpressionProfileDto> proteinDifferentialExpressionProfiles) {
    this.proteinDifferentialExpressionProfiles = proteinDifferentialExpressionProfiles;
    return this;
  }

  public ProteinDifferentialExpressionProfilePageDto addProteinDifferentialExpressionProfilesItem(ProteinDifferentialExpressionProfileDto proteinDifferentialExpressionProfilesItem) {
    if (this.proteinDifferentialExpressionProfiles == null) {
      this.proteinDifferentialExpressionProfiles = new ArrayList<>();
    }
    this.proteinDifferentialExpressionProfiles.add(proteinDifferentialExpressionProfilesItem);
    return this;
  }

  /**
   * A list of protein differential epxression profiles.
   * @return proteinDifferentialExpressionProfiles
   */
  @NotNull @Valid 
  @Schema(name = "proteinDifferentialExpressionProfiles", description = "A list of protein differential epxression profiles.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("proteinDifferentialExpressionProfiles")
  public List<@Valid ProteinDifferentialExpressionProfileDto> getProteinDifferentialExpressionProfiles() {
    return proteinDifferentialExpressionProfiles;
  }

  public void setProteinDifferentialExpressionProfiles(List<@Valid ProteinDifferentialExpressionProfileDto> proteinDifferentialExpressionProfiles) {
    this.proteinDifferentialExpressionProfiles = proteinDifferentialExpressionProfiles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProteinDifferentialExpressionProfilePageDto proteinDifferentialExpressionProfilePage = (ProteinDifferentialExpressionProfilePageDto) o;
    return Objects.equals(this.number, proteinDifferentialExpressionProfilePage.number) &&
        Objects.equals(this.size, proteinDifferentialExpressionProfilePage.size) &&
        Objects.equals(this.totalElements, proteinDifferentialExpressionProfilePage.totalElements) &&
        Objects.equals(this.totalPages, proteinDifferentialExpressionProfilePage.totalPages) &&
        Objects.equals(this.hasNext, proteinDifferentialExpressionProfilePage.hasNext) &&
        Objects.equals(this.hasPrevious, proteinDifferentialExpressionProfilePage.hasPrevious) &&
        Objects.equals(this.proteinDifferentialExpressionProfiles, proteinDifferentialExpressionProfilePage.proteinDifferentialExpressionProfiles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, proteinDifferentialExpressionProfiles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProteinDifferentialExpressionProfilePageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    proteinDifferentialExpressionProfiles: ").append(toIndentedString(proteinDifferentialExpressionProfiles)).append("\n");
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

    private ProteinDifferentialExpressionProfilePageDto instance;

    public Builder() {
      this(new ProteinDifferentialExpressionProfilePageDto());
    }

    protected Builder(ProteinDifferentialExpressionProfilePageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ProteinDifferentialExpressionProfilePageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setProteinDifferentialExpressionProfiles(value.proteinDifferentialExpressionProfiles);
      return this;
    }

    public ProteinDifferentialExpressionProfilePageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public ProteinDifferentialExpressionProfilePageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public ProteinDifferentialExpressionProfilePageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public ProteinDifferentialExpressionProfilePageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public ProteinDifferentialExpressionProfilePageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public ProteinDifferentialExpressionProfilePageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public ProteinDifferentialExpressionProfilePageDto.Builder proteinDifferentialExpressionProfiles(List<ProteinDifferentialExpressionProfileDto> proteinDifferentialExpressionProfiles) {
      this.instance.proteinDifferentialExpressionProfiles(proteinDifferentialExpressionProfiles);
      return this;
    }
    
    /**
    * returns a built ProteinDifferentialExpressionProfilePageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ProteinDifferentialExpressionProfilePageDto build() {
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
  public static ProteinDifferentialExpressionProfilePageDto.Builder builder() {
    return new ProteinDifferentialExpressionProfilePageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ProteinDifferentialExpressionProfilePageDto.Builder toBuilder() {
    ProteinDifferentialExpressionProfilePageDto.Builder builder = new ProteinDifferentialExpressionProfilePageDto.Builder();
    return builder.copyOf(this);
  }

}

