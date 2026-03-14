package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Request body for creating or updating a quest post
 */

@Schema(name = "QuestPostCreateOrUpdate", description = "Request body for creating or updating a quest post")
@JsonTypeName("QuestPostCreateOrUpdate")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class QuestPostCreateOrUpdateDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate date = null;

  private String title;

  private String description;

  @Valid
  private List<URI> images = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime publishDate = null;

  private @Nullable Integer requiredProgress = null;

  /**
   * Minimum contributor tier required to unlock content. Null means public access.
   */
  public enum RequiredTierEnum {
    KNIGHT("knight"),
    
    CHAMPION("champion");

    private final String value;

    RequiredTierEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RequiredTierEnum fromValue(String value) {
      for (RequiredTierEnum b : RequiredTierEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      return null;
    }
  }

  private @Nullable RequiredTierEnum requiredTier = null;

  public QuestPostCreateOrUpdateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QuestPostCreateOrUpdateDto(String title, String description, List<URI> images) {
    this.title = title;
    this.description = description;
    this.images = images;
  }

  public QuestPostCreateOrUpdateDto date(@Nullable LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Optional display date for the post
   * @return date
   */
  @Valid 
  @Schema(name = "date", example = "2026-02-03", description = "Optional display date for the post", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("date")
  public @Nullable LocalDate getDate() {
    return date;
  }

  public void setDate(@Nullable LocalDate date) {
    this.date = date;
  }

  public QuestPostCreateOrUpdateDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Post heading
   * @return title
   */
  @NotNull @Size(min = 1, max = 200) 
  @Schema(name = "title", example = "Chapter 1: Laying the First Stones", description = "Post heading", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public QuestPostCreateOrUpdateDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Post content text
   * @return description
   */
  @NotNull @Size(min = 1, max = 10000) 
  @Schema(name = "description", example = "The arena walls began to rise...", description = "Post content text", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public QuestPostCreateOrUpdateDto images(List<URI> images) {
    this.images = images;
    return this;
  }

  public QuestPostCreateOrUpdateDto addImagesItem(URI imagesItem) {
    if (this.images == null) {
      this.images = new ArrayList<>();
    }
    this.images.add(imagesItem);
    return this;
  }

  /**
   * List of image URLs for the post
   * @return images
   */
  @NotNull @Valid @Size(max = 50) 
  @Schema(name = "images", description = "List of image URLs for the post", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("images")
  public List<URI> getImages() {
    return images;
  }

  public void setImages(List<URI> images) {
    this.images = images;
  }

  public QuestPostCreateOrUpdateDto publishDate(@Nullable OffsetDateTime publishDate) {
    this.publishDate = publishDate;
    return this;
  }

  /**
   * Post is hidden entirely before this timestamp. Null means immediately visible.
   * @return publishDate
   */
  @Valid 
  @Schema(name = "publishDate", example = "2026-03-16T09:00Z", description = "Post is hidden entirely before this timestamp. Null means immediately visible.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("publishDate")
  public @Nullable OffsetDateTime getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(@Nullable OffsetDateTime publishDate) {
    this.publishDate = publishDate;
  }

  public QuestPostCreateOrUpdateDto requiredProgress(@Nullable Integer requiredProgress) {
    this.requiredProgress = requiredProgress;
    return this;
  }

  /**
   * Minimum quest-wide battle count required to unlock content. Null means no progress gate.
   * minimum: 0
   * @return requiredProgress
   */
  @Min(0) 
  @Schema(name = "requiredProgress", example = "500", description = "Minimum quest-wide battle count required to unlock content. Null means no progress gate.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requiredProgress")
  public @Nullable Integer getRequiredProgress() {
    return requiredProgress;
  }

  public void setRequiredProgress(@Nullable Integer requiredProgress) {
    this.requiredProgress = requiredProgress;
  }

  public QuestPostCreateOrUpdateDto requiredTier(@Nullable RequiredTierEnum requiredTier) {
    this.requiredTier = requiredTier;
    return this;
  }

  /**
   * Minimum contributor tier required to unlock content. Null means public access.
   * @return requiredTier
   */
  
  @Schema(name = "requiredTier", example = "knight", description = "Minimum contributor tier required to unlock content. Null means public access.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requiredTier")
  public @Nullable RequiredTierEnum getRequiredTier() {
    return requiredTier;
  }

  public void setRequiredTier(@Nullable RequiredTierEnum requiredTier) {
    this.requiredTier = requiredTier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestPostCreateOrUpdateDto questPostCreateOrUpdate = (QuestPostCreateOrUpdateDto) o;
    return Objects.equals(this.date, questPostCreateOrUpdate.date) &&
        Objects.equals(this.title, questPostCreateOrUpdate.title) &&
        Objects.equals(this.description, questPostCreateOrUpdate.description) &&
        Objects.equals(this.images, questPostCreateOrUpdate.images) &&
        Objects.equals(this.publishDate, questPostCreateOrUpdate.publishDate) &&
        Objects.equals(this.requiredProgress, questPostCreateOrUpdate.requiredProgress) &&
        Objects.equals(this.requiredTier, questPostCreateOrUpdate.requiredTier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, title, description, images, publishDate, requiredProgress, requiredTier);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuestPostCreateOrUpdateDto {\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    images: ").append(toIndentedString(images)).append("\n");
    sb.append("    publishDate: ").append(toIndentedString(publishDate)).append("\n");
    sb.append("    requiredProgress: ").append(toIndentedString(requiredProgress)).append("\n");
    sb.append("    requiredTier: ").append(toIndentedString(requiredTier)).append("\n");
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

    private QuestPostCreateOrUpdateDto instance;

    public Builder() {
      this(new QuestPostCreateOrUpdateDto());
    }

    protected Builder(QuestPostCreateOrUpdateDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(QuestPostCreateOrUpdateDto value) { 
      this.instance.setDate(value.date);
      this.instance.setTitle(value.title);
      this.instance.setDescription(value.description);
      this.instance.setImages(value.images);
      this.instance.setPublishDate(value.publishDate);
      this.instance.setRequiredProgress(value.requiredProgress);
      this.instance.setRequiredTier(value.requiredTier);
      return this;
    }

    public QuestPostCreateOrUpdateDto.Builder date(LocalDate date) {
      this.instance.date(date);
      return this;
    }
    
    public QuestPostCreateOrUpdateDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public QuestPostCreateOrUpdateDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public QuestPostCreateOrUpdateDto.Builder images(List<URI> images) {
      this.instance.images(images);
      return this;
    }
    
    public QuestPostCreateOrUpdateDto.Builder publishDate(OffsetDateTime publishDate) {
      this.instance.publishDate(publishDate);
      return this;
    }
    
    public QuestPostCreateOrUpdateDto.Builder requiredProgress(Integer requiredProgress) {
      this.instance.requiredProgress(requiredProgress);
      return this;
    }
    
    public QuestPostCreateOrUpdateDto.Builder requiredTier(RequiredTierEnum requiredTier) {
      this.instance.requiredTier(requiredTier);
      return this;
    }
    
    /**
    * returns a built QuestPostCreateOrUpdateDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public QuestPostCreateOrUpdateDto build() {
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
  public static QuestPostCreateOrUpdateDto.Builder builder() {
    return new QuestPostCreateOrUpdateDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public QuestPostCreateOrUpdateDto.Builder toBuilder() {
    QuestPostCreateOrUpdateDto.Builder builder = new QuestPostCreateOrUpdateDto.Builder();
    return builder.copyOf(this);
  }

}

