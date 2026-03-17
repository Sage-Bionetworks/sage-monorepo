package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Request body for creating or updating a quest
 */

@Schema(name = "QuestCreateOrUpdate", description = "Request body for creating or updating a quest")
@JsonTypeName("QuestCreateOrUpdate")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class QuestCreateOrUpdateDto {

  private String questId;

  private String title;

  private String description;

  private Integer goal;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private Integer activePostIndex;

  public QuestCreateOrUpdateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QuestCreateOrUpdateDto(String questId, String title, String description, Integer goal, OffsetDateTime startDate, OffsetDateTime endDate, Integer activePostIndex) {
    this.questId = questId;
    this.title = title;
    this.description = description;
    this.goal = goal;
    this.startDate = startDate;
    this.endDate = endDate;
    this.activePostIndex = activePostIndex;
  }

  public QuestCreateOrUpdateDto questId(String questId) {
    this.questId = questId;
    return this;
  }

  /**
   * Unique identifier for the quest
   * @return questId
   */
  @NotNull @Pattern(regexp = "^[a-z0-9-]+$") @Size(min = 1, max = 100) 
  @Schema(name = "questId", example = "build-bioarena-together", description = "Unique identifier for the quest", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("questId")
  public String getQuestId() {
    return questId;
  }

  public void setQuestId(String questId) {
    this.questId = questId;
  }

  public QuestCreateOrUpdateDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Quest display title
   * @return title
   */
  @NotNull @Size(min = 1, max = 200) 
  @Schema(name = "title", example = "Build BioArena Together", description = "Quest display title", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public QuestCreateOrUpdateDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Quest narrative description
   * @return description
   */
  @NotNull @Size(min = 1, max = 5000) 
  @Schema(name = "description", example = "Join forces to build an arena together...", description = "Quest narrative description", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public QuestCreateOrUpdateDto goal(Integer goal) {
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

  public QuestCreateOrUpdateDto startDate(OffsetDateTime startDate) {
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

  public QuestCreateOrUpdateDto endDate(OffsetDateTime endDate) {
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

  public QuestCreateOrUpdateDto activePostIndex(Integer activePostIndex) {
    this.activePostIndex = activePostIndex;
    return this;
  }

  /**
   * Index of the post to expand by default in the UI
   * minimum: 0
   * maximum: 99
   * @return activePostIndex
   */
  @NotNull @Min(0) @Max(99) 
  @Schema(name = "activePostIndex", example = "4", description = "Index of the post to expand by default in the UI", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("activePostIndex")
  public Integer getActivePostIndex() {
    return activePostIndex;
  }

  public void setActivePostIndex(Integer activePostIndex) {
    this.activePostIndex = activePostIndex;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestCreateOrUpdateDto questCreateOrUpdate = (QuestCreateOrUpdateDto) o;
    return Objects.equals(this.questId, questCreateOrUpdate.questId) &&
        Objects.equals(this.title, questCreateOrUpdate.title) &&
        Objects.equals(this.description, questCreateOrUpdate.description) &&
        Objects.equals(this.goal, questCreateOrUpdate.goal) &&
        Objects.equals(this.startDate, questCreateOrUpdate.startDate) &&
        Objects.equals(this.endDate, questCreateOrUpdate.endDate) &&
        Objects.equals(this.activePostIndex, questCreateOrUpdate.activePostIndex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(questId, title, description, goal, startDate, endDate, activePostIndex);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuestCreateOrUpdateDto {\n");
    sb.append("    questId: ").append(toIndentedString(questId)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    goal: ").append(toIndentedString(goal)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    activePostIndex: ").append(toIndentedString(activePostIndex)).append("\n");
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

    private QuestCreateOrUpdateDto instance;

    public Builder() {
      this(new QuestCreateOrUpdateDto());
    }

    protected Builder(QuestCreateOrUpdateDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(QuestCreateOrUpdateDto value) { 
      this.instance.setQuestId(value.questId);
      this.instance.setTitle(value.title);
      this.instance.setDescription(value.description);
      this.instance.setGoal(value.goal);
      this.instance.setStartDate(value.startDate);
      this.instance.setEndDate(value.endDate);
      this.instance.setActivePostIndex(value.activePostIndex);
      return this;
    }

    public QuestCreateOrUpdateDto.Builder questId(String questId) {
      this.instance.questId(questId);
      return this;
    }
    
    public QuestCreateOrUpdateDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public QuestCreateOrUpdateDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public QuestCreateOrUpdateDto.Builder goal(Integer goal) {
      this.instance.goal(goal);
      return this;
    }
    
    public QuestCreateOrUpdateDto.Builder startDate(OffsetDateTime startDate) {
      this.instance.startDate(startDate);
      return this;
    }
    
    public QuestCreateOrUpdateDto.Builder endDate(OffsetDateTime endDate) {
      this.instance.endDate(endDate);
      return this;
    }
    
    public QuestCreateOrUpdateDto.Builder activePostIndex(Integer activePostIndex) {
      this.instance.activePostIndex(activePostIndex);
      return this;
    }
    
    /**
    * returns a built QuestCreateOrUpdateDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public QuestCreateOrUpdateDto build() {
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
  public static QuestCreateOrUpdateDto.Builder builder() {
    return new QuestCreateOrUpdateDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public QuestCreateOrUpdateDto.Builder toBuilder() {
    QuestCreateOrUpdateDto.Builder builder = new QuestCreateOrUpdateDto.Builder();
    return builder.copyOf(this);
  }

}

