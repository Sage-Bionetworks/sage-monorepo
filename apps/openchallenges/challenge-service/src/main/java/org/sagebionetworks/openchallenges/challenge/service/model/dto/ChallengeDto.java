package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.SimpleChallengePlatformDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A challenge
 */

@Schema(name = "Challenge", description = "A challenge")
@JsonTypeName("Challenge")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeDto {

  private Long id;

  private String slug;

  private String name;

  private @Nullable String headline = null;

  private String description;

  private @Nullable String doi = null;

  private ChallengeStatusDto status;

  private @Nullable SimpleChallengePlatformDto platform = null;

  private @Nullable String websiteUrl = null;

  private @Nullable String avatarUrl = null;

  @Valid
  private List<ChallengeIncentiveDto> incentives = new ArrayList<>();

  @Valid
  private List<ChallengeSubmissionTypeDto> submissionTypes = new ArrayList<>();

  @Valid
  private List<@Valid EdamConceptDto> inputDataTypes = new ArrayList<>();

  @Valid
  private List<ChallengeCategoryDto> categories = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate startDate = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate endDate = null;

  private Integer starredCount = 0;

  private @Nullable EdamConceptDto operation = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public ChallengeDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeDto(Long id, String slug, String name, String description, ChallengeStatusDto status, List<ChallengeIncentiveDto> incentives, List<ChallengeSubmissionTypeDto> submissionTypes, List<ChallengeCategoryDto> categories, Integer starredCount, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.description = description;
    this.status = status;
    this.incentives = incentives;
    this.submissionTypes = submissionTypes;
    this.categories = categories;
    this.starredCount = starredCount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public ChallengeDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the challenge.
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "1", description = "The unique identifier of the challenge.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ChallengeDto slug(String slug) {
    this.slug = slug;
    return this;
  }

  /**
   * The unique slug of the challenge.
   * @return slug
   */
  @NotNull @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") @Size(min = 3, max = 255) 
  @Schema(name = "slug", example = "awesome-challenge", description = "The unique slug of the challenge.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("slug")
  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public ChallengeDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the challenge.
   * @return name
   */
  @NotNull @Size(min = 3, max = 255) 
  @Schema(name = "name", example = "Awesome Challenge", description = "The name of the challenge.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChallengeDto headline(String headline) {
    this.headline = headline;
    return this;
  }

  /**
   * The headline of the challenge.
   * @return headline
   */
  @Size(min = 0, max = 80) 
  @Schema(name = "headline", example = "Example challenge headline", description = "The headline of the challenge.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("headline")
  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public ChallengeDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the challenge.
   * @return description
   */
  @NotNull @Size(min = 0, max = 1000) 
  @Schema(name = "description", example = "This is an example description of the challenge.", description = "The description of the challenge.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ChallengeDto doi(String doi) {
    this.doi = doi;
    return this;
  }

  /**
   * The DOI of the challenge.
   * @return doi
   */
  @Size(max = 120) 
  @Schema(name = "doi", example = "https://doi.org/123/abc", description = "The DOI of the challenge.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doi")
  public String getDoi() {
    return doi;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public ChallengeDto status(ChallengeStatusDto status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public ChallengeStatusDto getStatus() {
    return status;
  }

  public void setStatus(ChallengeStatusDto status) {
    this.status = status;
  }

  public ChallengeDto platform(SimpleChallengePlatformDto platform) {
    this.platform = platform;
    return this;
  }

  /**
   * Get platform
   * @return platform
   */
  @Valid 
  @Schema(name = "platform", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("platform")
  public SimpleChallengePlatformDto getPlatform() {
    return platform;
  }

  public void setPlatform(SimpleChallengePlatformDto platform) {
    this.platform = platform;
  }

  public ChallengeDto websiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  /**
   * A URL to the website or image.
   * @return websiteUrl
   */
  @Size(max = 500) 
  @Schema(name = "websiteUrl", example = "https://openchallenges.io", description = "A URL to the website or image.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("websiteUrl")
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public ChallengeDto avatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  /**
   * A URL to the website or image.
   * @return avatarUrl
   */
  @Size(max = 500) 
  @Schema(name = "avatarUrl", example = "https://openchallenges.io", description = "A URL to the website or image.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avatarUrl")
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public ChallengeDto incentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
    return this;
  }

  public ChallengeDto addIncentivesItem(ChallengeIncentiveDto incentivesItem) {
    if (this.incentives == null) {
      this.incentives = new ArrayList<>();
    }
    this.incentives.add(incentivesItem);
    return this;
  }

  /**
   * Get incentives
   * @return incentives
   */
  @NotNull @Valid 
  @Schema(name = "incentives", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("incentives")
  public List<ChallengeIncentiveDto> getIncentives() {
    return incentives;
  }

  public void setIncentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
  }

  public ChallengeDto submissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
    return this;
  }

  public ChallengeDto addSubmissionTypesItem(ChallengeSubmissionTypeDto submissionTypesItem) {
    if (this.submissionTypes == null) {
      this.submissionTypes = new ArrayList<>();
    }
    this.submissionTypes.add(submissionTypesItem);
    return this;
  }

  /**
   * Get submissionTypes
   * @return submissionTypes
   */
  @NotNull @Valid 
  @Schema(name = "submissionTypes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("submissionTypes")
  public List<ChallengeSubmissionTypeDto> getSubmissionTypes() {
    return submissionTypes;
  }

  public void setSubmissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
  }

  public ChallengeDto inputDataTypes(List<@Valid EdamConceptDto> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
    return this;
  }

  public ChallengeDto addInputDataTypesItem(EdamConceptDto inputDataTypesItem) {
    if (this.inputDataTypes == null) {
      this.inputDataTypes = new ArrayList<>();
    }
    this.inputDataTypes.add(inputDataTypesItem);
    return this;
  }

  /**
   * Get inputDataTypes
   * @return inputDataTypes
   */
  @Valid 
  @Schema(name = "inputDataTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("inputDataTypes")
  public List<@Valid EdamConceptDto> getInputDataTypes() {
    return inputDataTypes;
  }

  public void setInputDataTypes(List<@Valid EdamConceptDto> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
  }

  public ChallengeDto categories(List<ChallengeCategoryDto> categories) {
    this.categories = categories;
    return this;
  }

  public ChallengeDto addCategoriesItem(ChallengeCategoryDto categoriesItem) {
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
  @NotNull @Valid 
  @Schema(name = "categories", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("categories")
  public List<ChallengeCategoryDto> getCategories() {
    return categories;
  }

  public void setCategories(List<ChallengeCategoryDto> categories) {
    this.categories = categories;
  }

  public ChallengeDto startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * The start date of the challenge.
   * @return startDate
   */
  @Valid 
  @Schema(name = "startDate", example = "2017-07-21", description = "The start date of the challenge.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDate")
  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public ChallengeDto endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * The end date of the challenge.
   * @return endDate
   */
  @Valid 
  @Schema(name = "endDate", example = "2017-07-21", description = "The end date of the challenge.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endDate")
  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public ChallengeDto starredCount(Integer starredCount) {
    this.starredCount = starredCount;
    return this;
  }

  /**
   * The number of times the challenge has been starred by users.
   * minimum: 0
   * @return starredCount
   */
  @NotNull @Min(0) 
  @Schema(name = "starredCount", example = "100", description = "The number of times the challenge has been starred by users.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("starredCount")
  public Integer getStarredCount() {
    return starredCount;
  }

  public void setStarredCount(Integer starredCount) {
    this.starredCount = starredCount;
  }

  public ChallengeDto operation(EdamConceptDto operation) {
    this.operation = operation;
    return this;
  }

  /**
   * Get operation
   * @return operation
   */
  @Valid 
  @Schema(name = "operation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operation")
  public EdamConceptDto getOperation() {
    return operation;
  }

  public void setOperation(EdamConceptDto operation) {
    this.operation = operation;
  }

  public ChallengeDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Datetime when the object was added to the database.
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2022-07-04T22:19:11Z", description = "Datetime when the object was added to the database.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ChallengeDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Datetime when the object was last modified in the database.
   * @return updatedAt
   */
  @NotNull @Valid 
  @Schema(name = "updatedAt", example = "2022-07-04T22:19:11Z", description = "Datetime when the object was last modified in the database.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedAt")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeDto challenge = (ChallengeDto) o;
    return Objects.equals(this.id, challenge.id) &&
        Objects.equals(this.slug, challenge.slug) &&
        Objects.equals(this.name, challenge.name) &&
        Objects.equals(this.headline, challenge.headline) &&
        Objects.equals(this.description, challenge.description) &&
        Objects.equals(this.doi, challenge.doi) &&
        Objects.equals(this.status, challenge.status) &&
        Objects.equals(this.platform, challenge.platform) &&
        Objects.equals(this.websiteUrl, challenge.websiteUrl) &&
        Objects.equals(this.avatarUrl, challenge.avatarUrl) &&
        Objects.equals(this.incentives, challenge.incentives) &&
        Objects.equals(this.submissionTypes, challenge.submissionTypes) &&
        Objects.equals(this.inputDataTypes, challenge.inputDataTypes) &&
        Objects.equals(this.categories, challenge.categories) &&
        Objects.equals(this.startDate, challenge.startDate) &&
        Objects.equals(this.endDate, challenge.endDate) &&
        Objects.equals(this.starredCount, challenge.starredCount) &&
        Objects.equals(this.operation, challenge.operation) &&
        Objects.equals(this.createdAt, challenge.createdAt) &&
        Objects.equals(this.updatedAt, challenge.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, slug, name, headline, description, doi, status, platform, websiteUrl, avatarUrl, incentives, submissionTypes, inputDataTypes, categories, startDate, endDate, starredCount, operation, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    headline: ").append(toIndentedString(headline)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    doi: ").append(toIndentedString(doi)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    platform: ").append(toIndentedString(platform)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    incentives: ").append(toIndentedString(incentives)).append("\n");
    sb.append("    submissionTypes: ").append(toIndentedString(submissionTypes)).append("\n");
    sb.append("    inputDataTypes: ").append(toIndentedString(inputDataTypes)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    starredCount: ").append(toIndentedString(starredCount)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

    private ChallengeDto instance;

    public Builder() {
      this(new ChallengeDto());
    }

    protected Builder(ChallengeDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeDto value) { 
      this.instance.setId(value.id);
      this.instance.setSlug(value.slug);
      this.instance.setName(value.name);
      this.instance.setHeadline(value.headline);
      this.instance.setDescription(value.description);
      this.instance.setDoi(value.doi);
      this.instance.setStatus(value.status);
      this.instance.setPlatform(value.platform);
      this.instance.setWebsiteUrl(value.websiteUrl);
      this.instance.setAvatarUrl(value.avatarUrl);
      this.instance.setIncentives(value.incentives);
      this.instance.setSubmissionTypes(value.submissionTypes);
      this.instance.setInputDataTypes(value.inputDataTypes);
      this.instance.setCategories(value.categories);
      this.instance.setStartDate(value.startDate);
      this.instance.setEndDate(value.endDate);
      this.instance.setStarredCount(value.starredCount);
      this.instance.setOperation(value.operation);
      this.instance.setCreatedAt(value.createdAt);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public ChallengeDto.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    public ChallengeDto.Builder slug(String slug) {
      this.instance.slug(slug);
      return this;
    }
    
    public ChallengeDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ChallengeDto.Builder headline(String headline) {
      this.instance.headline(headline);
      return this;
    }
    
    public ChallengeDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public ChallengeDto.Builder doi(String doi) {
      this.instance.doi(doi);
      return this;
    }
    
    public ChallengeDto.Builder status(ChallengeStatusDto status) {
      this.instance.status(status);
      return this;
    }
    
    public ChallengeDto.Builder platform(SimpleChallengePlatformDto platform) {
      this.instance.platform(platform);
      return this;
    }
    
    public ChallengeDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    public ChallengeDto.Builder avatarUrl(String avatarUrl) {
      this.instance.avatarUrl(avatarUrl);
      return this;
    }
    
    public ChallengeDto.Builder incentives(List<ChallengeIncentiveDto> incentives) {
      this.instance.incentives(incentives);
      return this;
    }
    
    public ChallengeDto.Builder submissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
      this.instance.submissionTypes(submissionTypes);
      return this;
    }
    
    public ChallengeDto.Builder inputDataTypes(List<EdamConceptDto> inputDataTypes) {
      this.instance.inputDataTypes(inputDataTypes);
      return this;
    }
    
    public ChallengeDto.Builder categories(List<ChallengeCategoryDto> categories) {
      this.instance.categories(categories);
      return this;
    }
    
    public ChallengeDto.Builder startDate(LocalDate startDate) {
      this.instance.startDate(startDate);
      return this;
    }
    
    public ChallengeDto.Builder endDate(LocalDate endDate) {
      this.instance.endDate(endDate);
      return this;
    }
    
    public ChallengeDto.Builder starredCount(Integer starredCount) {
      this.instance.starredCount(starredCount);
      return this;
    }
    
    public ChallengeDto.Builder operation(EdamConceptDto operation) {
      this.instance.operation(operation);
      return this;
    }
    
    public ChallengeDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    public ChallengeDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built ChallengeDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeDto build() {
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
  public static ChallengeDto.Builder builder() {
    return new ChallengeDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeDto.Builder toBuilder() {
    ChallengeDto.Builder builder = new ChallengeDto.Builder();
    return builder.copyOf(this);
  }

}

