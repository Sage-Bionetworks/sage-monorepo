package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** A page of challenge platforms. */
@Schema(name = "ChallengePlatformsPage", description = "A page of challenge platforms.")
@JsonTypeName("ChallengePlatformsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@lombok.Builder
public class ChallengePlatformsPageDto {

  @JsonProperty("number")
  private Integer number;

  @JsonProperty("size")
  private Integer size;

  @JsonProperty("totalElements")
  private Long totalElements;

  @JsonProperty("totalPages")
  private Integer totalPages;

  @JsonProperty("hasNext")
  private Boolean hasNext;

  @JsonProperty("hasPrevious")
  private Boolean hasPrevious;

  @JsonProperty("challengePlatforms")
  @Valid
  private List<ChallengePlatformDto> challengePlatforms = new ArrayList<>();

  public ChallengePlatformsPageDto number(Integer number) {
    this.number = number;
    return this;
  }

  /**
   * The page number.
   *
   * @return number
   */
  @NotNull
  @Schema(name = "number", example = "99", description = "The page number.", required = true)
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
   *
   * @return size
   */
  @NotNull
  @Schema(
    name = "size",
    example = "99",
    description = "The number of items in a single page.",
    required = true
  )
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
   *
   * @return totalElements
   */
  @NotNull
  @Schema(
    name = "totalElements",
    example = "99",
    description = "Total number of elements in the result set.",
    required = true
  )
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
   *
   * @return totalPages
   */
  @NotNull
  @Schema(
    name = "totalPages",
    example = "99",
    description = "Total number of pages in the result set.",
    required = true
  )
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
   *
   * @return hasNext
   */
  @NotNull
  @Schema(
    name = "hasNext",
    example = "true",
    description = "Returns if there is a next page.",
    required = true
  )
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
   *
   * @return hasPrevious
   */
  @NotNull
  @Schema(
    name = "hasPrevious",
    example = "true",
    description = "Returns if there is a previous page.",
    required = true
  )
  public Boolean getHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(Boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }

  public ChallengePlatformsPageDto challengePlatforms(
    List<ChallengePlatformDto> challengePlatforms
  ) {
    this.challengePlatforms = challengePlatforms;
    return this;
  }

  public ChallengePlatformsPageDto addChallengePlatformsItem(
    ChallengePlatformDto challengePlatformsItem
  ) {
    if (this.challengePlatforms == null) {
      this.challengePlatforms = new ArrayList<>();
    }
    this.challengePlatforms.add(challengePlatformsItem);
    return this;
  }

  /**
   * A list of challenge platforms.
   *
   * @return challengePlatforms
   */
  @NotNull
  @Valid
  @Schema(
    name = "challengePlatforms",
    description = "A list of challenge platforms.",
    required = true
  )
  public List<ChallengePlatformDto> getChallengePlatforms() {
    return challengePlatforms;
  }

  public void setChallengePlatforms(List<ChallengePlatformDto> challengePlatforms) {
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
    return (
      Objects.equals(this.number, challengePlatformsPage.number) &&
      Objects.equals(this.size, challengePlatformsPage.size) &&
      Objects.equals(this.totalElements, challengePlatformsPage.totalElements) &&
      Objects.equals(this.totalPages, challengePlatformsPage.totalPages) &&
      Objects.equals(this.hasNext, challengePlatformsPage.hasNext) &&
      Objects.equals(this.hasPrevious, challengePlatformsPage.hasPrevious) &&
      Objects.equals(this.challengePlatforms, challengePlatformsPage.challengePlatforms)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      number,
      size,
      totalElements,
      totalPages,
      hasNext,
      hasPrevious,
      challengePlatforms
    );
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
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
