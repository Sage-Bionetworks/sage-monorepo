package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * An object
 */

@Schema(name = "ChallengesPerYear", description = "An object")
@JsonTypeName("ChallengesPerYear")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengesPerYearDto {

  @Valid
  private List<String> years = new ArrayList<>();

  @Valid
  private List<Integer> challengeCounts = new ArrayList<>();

  private Integer undatedChallengeCount = 0;

  public ChallengesPerYearDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengesPerYearDto(List<String> years, List<Integer> challengeCounts, Integer undatedChallengeCount) {
    this.years = years;
    this.challengeCounts = challengeCounts;
    this.undatedChallengeCount = undatedChallengeCount;
  }

  public ChallengesPerYearDto years(List<String> years) {
    this.years = years;
    return this;
  }

  public ChallengesPerYearDto addYearsItem(String yearsItem) {
    if (this.years == null) {
      this.years = new ArrayList<>();
    }
    this.years.add(yearsItem);
    return this;
  }

  /**
   * Get years
   * @return years
   */
  @NotNull 
  @Schema(name = "years", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("years")
  public List<String> getYears() {
    return years;
  }

  public void setYears(List<String> years) {
    this.years = years;
  }

  public ChallengesPerYearDto challengeCounts(List<Integer> challengeCounts) {
    this.challengeCounts = challengeCounts;
    return this;
  }

  public ChallengesPerYearDto addChallengeCountsItem(Integer challengeCountsItem) {
    if (this.challengeCounts == null) {
      this.challengeCounts = new ArrayList<>();
    }
    this.challengeCounts.add(challengeCountsItem);
    return this;
  }

  /**
   * Get challengeCounts
   * @return challengeCounts
   */
  @NotNull 
  @Schema(name = "challengeCounts", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("challengeCounts")
  public List<Integer> getChallengeCounts() {
    return challengeCounts;
  }

  public void setChallengeCounts(List<Integer> challengeCounts) {
    this.challengeCounts = challengeCounts;
  }

  public ChallengesPerYearDto undatedChallengeCount(Integer undatedChallengeCount) {
    this.undatedChallengeCount = undatedChallengeCount;
    return this;
  }

  /**
   * Get undatedChallengeCount
   * minimum: 0
   * @return undatedChallengeCount
   */
  @NotNull @Min(0) 
  @Schema(name = "undatedChallengeCount", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("undatedChallengeCount")
  public Integer getUndatedChallengeCount() {
    return undatedChallengeCount;
  }

  public void setUndatedChallengeCount(Integer undatedChallengeCount) {
    this.undatedChallengeCount = undatedChallengeCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengesPerYearDto challengesPerYear = (ChallengesPerYearDto) o;
    return Objects.equals(this.years, challengesPerYear.years) &&
        Objects.equals(this.challengeCounts, challengesPerYear.challengeCounts) &&
        Objects.equals(this.undatedChallengeCount, challengesPerYear.undatedChallengeCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(years, challengeCounts, undatedChallengeCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengesPerYearDto {\n");
    sb.append("    years: ").append(toIndentedString(years)).append("\n");
    sb.append("    challengeCounts: ").append(toIndentedString(challengeCounts)).append("\n");
    sb.append("    undatedChallengeCount: ").append(toIndentedString(undatedChallengeCount)).append("\n");
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

    private ChallengesPerYearDto instance;

    public Builder() {
      this(new ChallengesPerYearDto());
    }

    protected Builder(ChallengesPerYearDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengesPerYearDto value) { 
      this.instance.setYears(value.years);
      this.instance.setChallengeCounts(value.challengeCounts);
      this.instance.setUndatedChallengeCount(value.undatedChallengeCount);
      return this;
    }

    public ChallengesPerYearDto.Builder years(List<String> years) {
      this.instance.years(years);
      return this;
    }
    
    public ChallengesPerYearDto.Builder challengeCounts(List<Integer> challengeCounts) {
      this.instance.challengeCounts(challengeCounts);
      return this;
    }
    
    public ChallengesPerYearDto.Builder undatedChallengeCount(Integer undatedChallengeCount) {
      this.instance.undatedChallengeCount(undatedChallengeCount);
      return this;
    }
    
    /**
    * returns a built ChallengesPerYearDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengesPerYearDto build() {
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
  public static ChallengesPerYearDto.Builder builder() {
    return new ChallengesPerYearDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengesPerYearDto.Builder toBuilder() {
    ChallengesPerYearDto.Builder builder = new ChallengesPerYearDto.Builder();
    return builder.copyOf(this);
  }

}

