package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A community quest with its metadata and posts
 */

@Schema(name = "Quest", description = "A community quest with its metadata and posts")
@JsonTypeName("Quest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class QuestDto {

  private String questId;

  private String title;

  private String description;

  private Integer goal;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private Integer activePostIndex;

  private Integer totalBlocks;

  @Valid
  private List<@Valid QuestPostDto> posts = new ArrayList<>();

  public QuestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QuestDto(String questId, String title, String description, Integer goal, OffsetDateTime startDate, OffsetDateTime endDate, Integer activePostIndex, Integer totalBlocks, List<@Valid QuestPostDto> posts) {
    this.questId = questId;
    this.title = title;
    this.description = description;
    this.goal = goal;
    this.startDate = startDate;
    this.endDate = endDate;
    this.activePostIndex = activePostIndex;
    this.totalBlocks = totalBlocks;
    this.posts = posts;
  }

  public QuestDto questId(String questId) {
    this.questId = questId;
    return this;
  }

  /**
   * Unique identifier for the quest
   * @return questId
   */
  @NotNull 
  @Schema(name = "questId", example = "build-bioarena-together", description = "Unique identifier for the quest", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("questId")
  public String getQuestId() {
    return questId;
  }

  public void setQuestId(String questId) {
    this.questId = questId;
  }

  public QuestDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Quest display title
   * @return title
   */
  @NotNull @Size(max = 200) 
  @Schema(name = "title", example = "Build BioArena Together", description = "Quest display title", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public QuestDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Quest narrative description
   * @return description
   */
  @NotNull @Size(max = 5000) 
  @Schema(name = "description", example = "Join forces to build an arena together...", description = "Quest narrative description", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public QuestDto goal(Integer goal) {
    this.goal = goal;
    return this;
  }

  /**
   * Target total battle count for the quest
   * minimum: 0
   * @return goal
   */
  @NotNull @Min(0) 
  @Schema(name = "goal", example = "2850", description = "Target total battle count for the quest", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("goal")
  public Integer getGoal() {
    return goal;
  }

  public void setGoal(Integer goal) {
    this.goal = goal;
  }

  public QuestDto startDate(OffsetDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Quest start date
   * @return startDate
   */
  @NotNull @Valid 
  @Schema(name = "startDate", example = "2026-02-01T00:00Z", description = "Quest start date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("startDate")
  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(OffsetDateTime startDate) {
    this.startDate = startDate;
  }

  public QuestDto endDate(OffsetDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Quest end date
   * @return endDate
   */
  @NotNull @Valid 
  @Schema(name = "endDate", example = "2026-04-30T23:59:59Z", description = "Quest end date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("endDate")
  public OffsetDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(OffsetDateTime endDate) {
    this.endDate = endDate;
  }

  public QuestDto activePostIndex(Integer activePostIndex) {
    this.activePostIndex = activePostIndex;
    return this;
  }

  /**
   * Index of the post to expand by default in the UI
   * @return activePostIndex
   */
  @NotNull 
  @Schema(name = "activePostIndex", example = "4", description = "Index of the post to expand by default in the UI", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("activePostIndex")
  public Integer getActivePostIndex() {
    return activePostIndex;
  }

  public void setActivePostIndex(Integer activePostIndex) {
    this.activePostIndex = activePostIndex;
  }

  public QuestDto totalBlocks(Integer totalBlocks) {
    this.totalBlocks = totalBlocks;
    return this;
  }

  /**
   * Current total number of completed battles during the quest period
   * minimum: 0
   * @return totalBlocks
   */
  @NotNull @Min(0) 
  @Schema(name = "totalBlocks", example = "150", description = "Current total number of completed battles during the quest period", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalBlocks")
  public Integer getTotalBlocks() {
    return totalBlocks;
  }

  public void setTotalBlocks(Integer totalBlocks) {
    this.totalBlocks = totalBlocks;
  }

  public QuestDto posts(List<@Valid QuestPostDto> posts) {
    this.posts = posts;
    return this;
  }

  public QuestDto addPostsItem(QuestPostDto postsItem) {
    if (this.posts == null) {
      this.posts = new ArrayList<>();
    }
    this.posts.add(postsItem);
    return this;
  }

  /**
   * Quest posts ordered by post index
   * @return posts
   */
  @NotNull @Valid 
  @Schema(name = "posts", description = "Quest posts ordered by post index", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("posts")
  public List<@Valid QuestPostDto> getPosts() {
    return posts;
  }

  public void setPosts(List<@Valid QuestPostDto> posts) {
    this.posts = posts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestDto quest = (QuestDto) o;
    return Objects.equals(this.questId, quest.questId) &&
        Objects.equals(this.title, quest.title) &&
        Objects.equals(this.description, quest.description) &&
        Objects.equals(this.goal, quest.goal) &&
        Objects.equals(this.startDate, quest.startDate) &&
        Objects.equals(this.endDate, quest.endDate) &&
        Objects.equals(this.activePostIndex, quest.activePostIndex) &&
        Objects.equals(this.totalBlocks, quest.totalBlocks) &&
        Objects.equals(this.posts, quest.posts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(questId, title, description, goal, startDate, endDate, activePostIndex, totalBlocks, posts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuestDto {\n");
    sb.append("    questId: ").append(toIndentedString(questId)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    goal: ").append(toIndentedString(goal)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    activePostIndex: ").append(toIndentedString(activePostIndex)).append("\n");
    sb.append("    totalBlocks: ").append(toIndentedString(totalBlocks)).append("\n");
    sb.append("    posts: ").append(toIndentedString(posts)).append("\n");
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

    private QuestDto instance;

    public Builder() {
      this(new QuestDto());
    }

    protected Builder(QuestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(QuestDto value) { 
      this.instance.setQuestId(value.questId);
      this.instance.setTitle(value.title);
      this.instance.setDescription(value.description);
      this.instance.setGoal(value.goal);
      this.instance.setStartDate(value.startDate);
      this.instance.setEndDate(value.endDate);
      this.instance.setActivePostIndex(value.activePostIndex);
      this.instance.setTotalBlocks(value.totalBlocks);
      this.instance.setPosts(value.posts);
      return this;
    }

    public QuestDto.Builder questId(String questId) {
      this.instance.questId(questId);
      return this;
    }
    
    public QuestDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public QuestDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public QuestDto.Builder goal(Integer goal) {
      this.instance.goal(goal);
      return this;
    }
    
    public QuestDto.Builder startDate(OffsetDateTime startDate) {
      this.instance.startDate(startDate);
      return this;
    }
    
    public QuestDto.Builder endDate(OffsetDateTime endDate) {
      this.instance.endDate(endDate);
      return this;
    }
    
    public QuestDto.Builder activePostIndex(Integer activePostIndex) {
      this.instance.activePostIndex(activePostIndex);
      return this;
    }
    
    public QuestDto.Builder totalBlocks(Integer totalBlocks) {
      this.instance.totalBlocks(totalBlocks);
      return this;
    }
    
    public QuestDto.Builder posts(List<QuestPostDto> posts) {
      this.instance.posts(posts);
      return this;
    }
    
    /**
    * returns a built QuestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public QuestDto build() {
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
  public static QuestDto.Builder builder() {
    return new QuestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public QuestDto.Builder toBuilder() {
    QuestDto.Builder builder = new QuestDto.Builder();
    return builder.copyOf(this);
  }

}

