package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of challenge platforms.
 */

@Schema(name = "ChallengePlatformsPage", description = "A page of challenge platforms.")
@JsonTypeName("ChallengePlatformsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengePlatformsPageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  @Valid
  private List<@Valid ChallengePlatformDto> challengePlatforms = new ArrayList<>();

  public ChallengePlatformsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengePlatformsPageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
  }

  public ChallengePlatformsPageDto number(Integer number) {
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

  public ChallengePlatformsPageDto size(Integer size) {
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

  public ChallengePlatformsPageDto totalElements(Long totalElements) {
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

  public ChallengePlatformsPageDto totalPages(Integer totalPages) {
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

  public ChallengePlatformsPageDto hasNext(Boolean hasNext) {
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

  public ChallengePlatformsPageDto hasPrevious(Boolean hasPrevious) {
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

  public ChallengePlatformsPageDto challengePlatforms(List<@Valid ChallengePlatformDto> challengePlatforms) {
    this.challengePlatforms = challengePlatforms;
    return this;
  }

  public ChallengePlatformsPageDto addChallengePlatformsItem(ChallengePlatformDto challengePlatformsItem) {
    if (this.challengePlatforms == null) {
      this.challengePlatforms = new ArrayList<>();
    }
    this.challengePlatforms.add(challengePlatformsItem);
    return this;
  }

  /**
   * A list of challenge platforms.
   * @return challengePlatforms
   */
  @Valid 
  @Schema(name = "challengePlatforms", description = "A list of challenge platforms.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("challengePlatforms")
  public List<@Valid ChallengePlatformDto> getChallengePlatforms() {
    return challengePlatforms;
  }

  public void setChallengePlatforms(List<@Valid ChallengePlatformDto> challengePlatforms) {
    this.challengePlatforms = challengePlatforms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengePlatformsPageDto challengePlatformsPage = (ChallengePlatformsPageDto) o;
    return Objects.equals(this.number, challengePlatformsPage.number) &&
        Objects.equals(this.size, challengePlatformsPage.size) &&
        Objects.equals(this.totalElements, challengePlatformsPage.totalElements) &&
        Objects.equals(this.totalPages, challengePlatformsPage.totalPages) &&
        Objects.equals(this.hasNext, challengePlatformsPage.hasNext) &&
        Objects.equals(this.hasPrevious, challengePlatformsPage.hasPrevious) &&
        Objects.equals(this.challengePlatforms, challengePlatformsPage.challengePlatforms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, challengePlatforms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformsPageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    challengePlatforms: ").append(toIndentedString(challengePlatforms)).append("\n");
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

    private ChallengePlatformsPageDto instance;

    public Builder() {
      this(new ChallengePlatformsPageDto());
    }

    protected Builder(ChallengePlatformsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengePlatformsPageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setChallengePlatforms(value.challengePlatforms);
      return this;
    }

    public ChallengePlatformsPageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public ChallengePlatformsPageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public ChallengePlatformsPageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public ChallengePlatformsPageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public ChallengePlatformsPageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public ChallengePlatformsPageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public ChallengePlatformsPageDto.Builder challengePlatforms(List<ChallengePlatformDto> challengePlatforms) {
      this.instance.challengePlatforms(challengePlatforms);
      return this;
    }
    
    /**
    * returns a built ChallengePlatformsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengePlatformsPageDto build() {
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
  public static ChallengePlatformsPageDto.Builder builder() {
    return new ChallengePlatformsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengePlatformsPageDto.Builder toBuilder() {
    ChallengePlatformsPageDto.Builder builder = new ChallengePlatformsPageDto.Builder();
    return builder.copyOf(this);
  }

}

