package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to create a challenge
 */

@Schema(name = "ChallengeCreateRequest", description = "The information used to create a challenge")
@JsonTypeName("ChallengeCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeCreateRequestDto {

  private String slug;

  private String name;

  private @Nullable String headline = null;

  private @Nullable String description;

  private @Nullable String doi = null;

  private ChallengeStatusDto status;

  private @Nullable Long platformId;

  private String websiteUrl = null;

  private @Nullable String avatarUrl = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate startDate = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate endDate = null;

  @Valid
  private List<ChallengeIncentiveDto> incentives = new ArrayList<>();

  @Valid
  private List<ChallengeSubmissionTypeDto> submissionTypes = new ArrayList<>();

  @Valid
  private List<ChallengeCategoryDto> categories = new ArrayList<>();

  @Valid
  private List<Long> inputDataTypes = new ArrayList<>();

  private @Nullable Long operation;

  public ChallengeCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeCreateRequestDto(String slug, String name, ChallengeStatusDto status, String websiteUrl) {
    this.slug = slug;
    this.name = name;
    this.status = status;
    this.websiteUrl = websiteUrl;
  }

  public ChallengeCreateRequestDto slug(String slug) {
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

  public ChallengeCreateRequestDto name(String name) {
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

  public ChallengeCreateRequestDto headline(String headline) {
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

  public ChallengeCreateRequestDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the challenge.
   * @return description
   */
  @Size(min = 0, max = 1000) 
  @Schema(name = "description", example = "This is an example description of the challenge.", description = "The description of the challenge.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ChallengeCreateRequestDto doi(String doi) {
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

  public ChallengeCreateRequestDto status(ChallengeStatusDto status) {
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

  public ChallengeCreateRequestDto platformId(Long platformId) {
    this.platformId = platformId;
    return this;
  }

  /**
   * The unique identifier of a challenge platform.
   * @return platformId
   */
  
  @Schema(name = "platformId", example = "1", description = "The unique identifier of a challenge platform.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("platformId")
  public Long getPlatformId() {
    return platformId;
  }

  public void setPlatformId(Long platformId) {
    this.platformId = platformId;
  }

  public ChallengeCreateRequestDto websiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  /**
   * A URL to the website or image.
   * @return websiteUrl
   */
  @NotNull @Size(max = 500) 
  @Schema(name = "websiteUrl", example = "https://openchallenges.io", description = "A URL to the website or image.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("websiteUrl")
  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public ChallengeCreateRequestDto avatarUrl(String avatarUrl) {
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

  public ChallengeCreateRequestDto startDate(LocalDate startDate) {
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

  public ChallengeCreateRequestDto endDate(LocalDate endDate) {
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

  public ChallengeCreateRequestDto incentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
    return this;
  }

  public ChallengeCreateRequestDto addIncentivesItem(ChallengeIncentiveDto incentivesItem) {
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
  @Valid 
  @Schema(name = "incentives", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("incentives")
  public List<ChallengeIncentiveDto> getIncentives() {
    return incentives;
  }

  public void setIncentives(List<ChallengeIncentiveDto> incentives) {
    this.incentives = incentives;
  }

  public ChallengeCreateRequestDto submissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
    return this;
  }

  public ChallengeCreateRequestDto addSubmissionTypesItem(ChallengeSubmissionTypeDto submissionTypesItem) {
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
  @Valid 
  @Schema(name = "submissionTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("submissionTypes")
  public List<ChallengeSubmissionTypeDto> getSubmissionTypes() {
    return submissionTypes;
  }

  public void setSubmissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
    this.submissionTypes = submissionTypes;
  }

  public ChallengeCreateRequestDto categories(List<ChallengeCategoryDto> categories) {
    this.categories = categories;
    return this;
  }

  public ChallengeCreateRequestDto addCategoriesItem(ChallengeCategoryDto categoriesItem) {
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
  @Valid 
  @Schema(name = "categories", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("categories")
  public List<ChallengeCategoryDto> getCategories() {
    return categories;
  }

  public void setCategories(List<ChallengeCategoryDto> categories) {
    this.categories = categories;
  }

  public ChallengeCreateRequestDto inputDataTypes(List<Long> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
    return this;
  }

  public ChallengeCreateRequestDto addInputDataTypesItem(Long inputDataTypesItem) {
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
  
  @Schema(name = "inputDataTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("inputDataTypes")
  public List<Long> getInputDataTypes() {
    return inputDataTypes;
  }

  public void setInputDataTypes(List<Long> inputDataTypes) {
    this.inputDataTypes = inputDataTypes;
  }

  public ChallengeCreateRequestDto operation(Long operation) {
    this.operation = operation;
    return this;
  }

  /**
   * The unique identifier of the EDAM concept.
   * @return operation
   */
  
  @Schema(name = "operation", example = "1", description = "The unique identifier of the EDAM concept.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operation")
  public Long getOperation() {
    return operation;
  }

  public void setOperation(Long operation) {
    this.operation = operation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeCreateRequestDto challengeCreateRequest = (ChallengeCreateRequestDto) o;
    return Objects.equals(this.slug, challengeCreateRequest.slug) &&
        Objects.equals(this.name, challengeCreateRequest.name) &&
        Objects.equals(this.headline, challengeCreateRequest.headline) &&
        Objects.equals(this.description, challengeCreateRequest.description) &&
        Objects.equals(this.doi, challengeCreateRequest.doi) &&
        Objects.equals(this.status, challengeCreateRequest.status) &&
        Objects.equals(this.platformId, challengeCreateRequest.platformId) &&
        Objects.equals(this.websiteUrl, challengeCreateRequest.websiteUrl) &&
        Objects.equals(this.avatarUrl, challengeCreateRequest.avatarUrl) &&
        Objects.equals(this.startDate, challengeCreateRequest.startDate) &&
        Objects.equals(this.endDate, challengeCreateRequest.endDate) &&
        Objects.equals(this.incentives, challengeCreateRequest.incentives) &&
        Objects.equals(this.submissionTypes, challengeCreateRequest.submissionTypes) &&
        Objects.equals(this.categories, challengeCreateRequest.categories) &&
        Objects.equals(this.inputDataTypes, challengeCreateRequest.inputDataTypes) &&
        Objects.equals(this.operation, challengeCreateRequest.operation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slug, name, headline, description, doi, status, platformId, websiteUrl, avatarUrl, startDate, endDate, incentives, submissionTypes, categories, inputDataTypes, operation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeCreateRequestDto {\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    headline: ").append(toIndentedString(headline)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    doi: ").append(toIndentedString(doi)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    platformId: ").append(toIndentedString(platformId)).append("\n");
    sb.append("    websiteUrl: ").append(toIndentedString(websiteUrl)).append("\n");
    sb.append("    avatarUrl: ").append(toIndentedString(avatarUrl)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    incentives: ").append(toIndentedString(incentives)).append("\n");
    sb.append("    submissionTypes: ").append(toIndentedString(submissionTypes)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    inputDataTypes: ").append(toIndentedString(inputDataTypes)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
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

    private ChallengeCreateRequestDto instance;

    public Builder() {
      this(new ChallengeCreateRequestDto());
    }

    protected Builder(ChallengeCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeCreateRequestDto value) { 
      this.instance.setSlug(value.slug);
      this.instance.setName(value.name);
      this.instance.setHeadline(value.headline);
      this.instance.setDescription(value.description);
      this.instance.setDoi(value.doi);
      this.instance.setStatus(value.status);
      this.instance.setPlatformId(value.platformId);
      this.instance.setWebsiteUrl(value.websiteUrl);
      this.instance.setAvatarUrl(value.avatarUrl);
      this.instance.setStartDate(value.startDate);
      this.instance.setEndDate(value.endDate);
      this.instance.setIncentives(value.incentives);
      this.instance.setSubmissionTypes(value.submissionTypes);
      this.instance.setCategories(value.categories);
      this.instance.setInputDataTypes(value.inputDataTypes);
      this.instance.setOperation(value.operation);
      return this;
    }

    public ChallengeCreateRequestDto.Builder slug(String slug) {
      this.instance.slug(slug);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder headline(String headline) {
      this.instance.headline(headline);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder doi(String doi) {
      this.instance.doi(doi);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder status(ChallengeStatusDto status) {
      this.instance.status(status);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder platformId(Long platformId) {
      this.instance.platformId(platformId);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder websiteUrl(String websiteUrl) {
      this.instance.websiteUrl(websiteUrl);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder avatarUrl(String avatarUrl) {
      this.instance.avatarUrl(avatarUrl);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder startDate(LocalDate startDate) {
      this.instance.startDate(startDate);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder endDate(LocalDate endDate) {
      this.instance.endDate(endDate);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder incentives(List<ChallengeIncentiveDto> incentives) {
      this.instance.incentives(incentives);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder submissionTypes(List<ChallengeSubmissionTypeDto> submissionTypes) {
      this.instance.submissionTypes(submissionTypes);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder categories(List<ChallengeCategoryDto> categories) {
      this.instance.categories(categories);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder inputDataTypes(List<Long> inputDataTypes) {
      this.instance.inputDataTypes(inputDataTypes);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder operation(Long operation) {
      this.instance.operation(operation);
      return this;
    }
    
    /**
    * returns a built ChallengeCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeCreateRequestDto build() {
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
  public static ChallengeCreateRequestDto.Builder builder() {
    return new ChallengeCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeCreateRequestDto.Builder toBuilder() {
    ChallengeCreateRequestDto.Builder builder = new ChallengeCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

