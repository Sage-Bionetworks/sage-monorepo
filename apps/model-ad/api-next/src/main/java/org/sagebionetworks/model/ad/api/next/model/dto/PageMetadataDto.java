package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The metadata of a page.
 */

@Schema(name = "PageMetadata", description = "The metadata of a page.")
@JsonTypeName("PageMetadata")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class PageMetadataDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  public PageMetadataDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PageMetadataDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
  }

  public PageMetadataDto number(Integer number) {
    this.number = number;
    return this;
  }

  /**
   * The page number.
   * @return number
   */
  @NotNull 
  @Schema(name = "number", example = "0", description = "The page number.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("number")
  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public PageMetadataDto size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * The number of items in a single page.
   * @return size
   */
  @NotNull 
  @Schema(name = "size", example = "100", description = "The number of items in a single page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public PageMetadataDto totalElements(Long totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Total number of elements in the result set.
   * @return totalElements
   */
  @NotNull 
  @Schema(name = "totalElements", example = "250", description = "Total number of elements in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalElements")
  public Long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  public PageMetadataDto totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Total number of pages in the result set.
   * @return totalPages
   */
  @NotNull 
  @Schema(name = "totalPages", example = "3", description = "Total number of pages in the result set.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalPages")
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public PageMetadataDto hasNext(Boolean hasNext) {
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

  public PageMetadataDto hasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
    return this;
  }

  /**
   * Returns if there is a previous page.
   * @return hasPrevious
   */
  @NotNull 
  @Schema(name = "hasPrevious", example = "false", description = "Returns if there is a previous page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hasPrevious")
  public Boolean getHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageMetadataDto pageMetadata = (PageMetadataDto) o;
    return Objects.equals(this.number, pageMetadata.number) &&
        Objects.equals(this.size, pageMetadata.size) &&
        Objects.equals(this.totalElements, pageMetadata.totalElements) &&
        Objects.equals(this.totalPages, pageMetadata.totalPages) &&
        Objects.equals(this.hasNext, pageMetadata.hasNext) &&
        Objects.equals(this.hasPrevious, pageMetadata.hasPrevious);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageMetadataDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
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

    private PageMetadataDto instance;

    public Builder() {
      this(new PageMetadataDto());
    }

    protected Builder(PageMetadataDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(PageMetadataDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      return this;
    }

    public PageMetadataDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public PageMetadataDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public PageMetadataDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public PageMetadataDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public PageMetadataDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public PageMetadataDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    /**
    * returns a built PageMetadataDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public PageMetadataDto build() {
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
  public static PageMetadataDto.Builder builder() {
    return new PageMetadataDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public PageMetadataDto.Builder toBuilder() {
    PageMetadataDto.Builder builder = new PageMetadataDto.Builder();
    return builder.copyOf(this);
  }

}

