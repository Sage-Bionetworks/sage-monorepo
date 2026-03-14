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
 * A single quest post. Content fields (description, images) are null/empty when the caller does not meet unlock gates.
 */

@Schema(name = "QuestPost", description = "A single quest post. Content fields (description, images) are null/empty when the caller does not meet unlock gates.")
@JsonTypeName("QuestPost")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class QuestPostDto {

  private Integer postIndex;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate date = null;

  private String title;

  private @Nullable String description = null;

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

  private Boolean locked;

  public QuestPostDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QuestPostDto(Integer postIndex, String title, List<URI> images, Boolean locked) {
    this.postIndex = postIndex;
    this.title = title;
    this.images = images;
    this.locked = locked;
  }

  public QuestPostDto postIndex(Integer postIndex) {
    this.postIndex = postIndex;
    return this;
  }

  /**
   * Display ordering index (0-based)
   * @return postIndex
   */
  @NotNull 
  @Schema(name = "postIndex", example = "0", description = "Display ordering index (0-based)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("postIndex")
  public Integer getPostIndex() {
    return postIndex;
  }

  public void setPostIndex(Integer postIndex) {
    this.postIndex = postIndex;
  }

  public QuestPostDto date(@Nullable LocalDate date) {
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

  public QuestPostDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Post heading (always visible for published posts)
   * @return title
   */
  @NotNull 
  @Schema(name = "title", example = "Chapter 1: Laying the First Stones", description = "Post heading (always visible for published posts)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public QuestPostDto description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Post content text. Null when the caller does not meet unlock gates.
   * @return description
   */
  
  @Schema(name = "description", example = "The arena walls began to rise...", description = "Post content text. Null when the caller does not meet unlock gates.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public QuestPostDto images(List<URI> images) {
    this.images = images;
    return this;
  }

  public QuestPostDto addImagesItem(URI imagesItem) {
    if (this.images == null) {
      this.images = new ArrayList<>();
    }
    this.images.add(imagesItem);
    return this;
  }

  /**
   * Image URLs for the post. Empty when the caller does not meet unlock gates.
   * @return images
   */
  @NotNull @Valid 
  @Schema(name = "images", description = "Image URLs for the post. Empty when the caller does not meet unlock gates.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("images")
  public List<URI> getImages() {
    return images;
  }

  public void setImages(List<URI> images) {
    this.images = images;
  }

  public QuestPostDto publishDate(@Nullable OffsetDateTime publishDate) {
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

  public QuestPostDto requiredProgress(@Nullable Integer requiredProgress) {
    this.requiredProgress = requiredProgress;
    return this;
  }

  /**
   * Minimum quest-wide battle count required to unlock content. Null means no progress gate.
   * @return requiredProgress
   */
  
  @Schema(name = "requiredProgress", example = "500", description = "Minimum quest-wide battle count required to unlock content. Null means no progress gate.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requiredProgress")
  public @Nullable Integer getRequiredProgress() {
    return requiredProgress;
  }

  public void setRequiredProgress(@Nullable Integer requiredProgress) {
    this.requiredProgress = requiredProgress;
  }

  public QuestPostDto requiredTier(@Nullable RequiredTierEnum requiredTier) {
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

  public QuestPostDto locked(Boolean locked) {
    this.locked = locked;
    return this;
  }

  /**
   * Whether the post content is locked for the current caller. True when the caller does not meet the required progress or tier gates. Always false for posts with no gates.
   * @return locked
   */
  @NotNull 
  @Schema(name = "locked", example = "false", description = "Whether the post content is locked for the current caller. True when the caller does not meet the required progress or tier gates. Always false for posts with no gates.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("locked")
  public Boolean getLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestPostDto questPost = (QuestPostDto) o;
    return Objects.equals(this.postIndex, questPost.postIndex) &&
        Objects.equals(this.date, questPost.date) &&
        Objects.equals(this.title, questPost.title) &&
        Objects.equals(this.description, questPost.description) &&
        Objects.equals(this.images, questPost.images) &&
        Objects.equals(this.publishDate, questPost.publishDate) &&
        Objects.equals(this.requiredProgress, questPost.requiredProgress) &&
        Objects.equals(this.requiredTier, questPost.requiredTier) &&
        Objects.equals(this.locked, questPost.locked);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postIndex, date, title, description, images, publishDate, requiredProgress, requiredTier, locked);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuestPostDto {\n");
    sb.append("    postIndex: ").append(toIndentedString(postIndex)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    images: ").append(toIndentedString(images)).append("\n");
    sb.append("    publishDate: ").append(toIndentedString(publishDate)).append("\n");
    sb.append("    requiredProgress: ").append(toIndentedString(requiredProgress)).append("\n");
    sb.append("    requiredTier: ").append(toIndentedString(requiredTier)).append("\n");
    sb.append("    locked: ").append(toIndentedString(locked)).append("\n");
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

    private QuestPostDto instance;

    public Builder() {
      this(new QuestPostDto());
    }

    protected Builder(QuestPostDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(QuestPostDto value) { 
      this.instance.setPostIndex(value.postIndex);
      this.instance.setDate(value.date);
      this.instance.setTitle(value.title);
      this.instance.setDescription(value.description);
      this.instance.setImages(value.images);
      this.instance.setPublishDate(value.publishDate);
      this.instance.setRequiredProgress(value.requiredProgress);
      this.instance.setRequiredTier(value.requiredTier);
      this.instance.setLocked(value.locked);
      return this;
    }

    public QuestPostDto.Builder postIndex(Integer postIndex) {
      this.instance.postIndex(postIndex);
      return this;
    }
    
    public QuestPostDto.Builder date(LocalDate date) {
      this.instance.date(date);
      return this;
    }
    
    public QuestPostDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public QuestPostDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public QuestPostDto.Builder images(List<URI> images) {
      this.instance.images(images);
      return this;
    }
    
    public QuestPostDto.Builder publishDate(OffsetDateTime publishDate) {
      this.instance.publishDate(publishDate);
      return this;
    }
    
    public QuestPostDto.Builder requiredProgress(Integer requiredProgress) {
      this.instance.requiredProgress(requiredProgress);
      return this;
    }
    
    public QuestPostDto.Builder requiredTier(RequiredTierEnum requiredTier) {
      this.instance.requiredTier(requiredTier);
      return this;
    }
    
    public QuestPostDto.Builder locked(Boolean locked) {
      this.instance.locked(locked);
      return this;
    }
    
    /**
    * returns a built QuestPostDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public QuestPostDto build() {
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
  public static QuestPostDto.Builder builder() {
    return new QuestPostDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public QuestPostDto.Builder toBuilder() {
    QuestPostDto.Builder builder = new QuestPostDto.Builder();
    return builder.copyOf(this);
  }

}

