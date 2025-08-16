package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.HistoricalLeaderboardEntryDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of historical leaderboard entries for a specific model.
 */

@Schema(name = "LeaderboardModelHistoryPage", description = "A page of historical leaderboard entries for a specific model.")
@JsonTypeName("LeaderboardModelHistoryPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LeaderboardModelHistoryPageDto {

  private Integer number;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  private Boolean hasNext;

  private Boolean hasPrevious;

  private String modelId;

  private String modelName;

  @Valid
  private List<@Valid HistoricalLeaderboardEntryDto> history = new ArrayList<>();

  public LeaderboardModelHistoryPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LeaderboardModelHistoryPageDto(Integer number, Integer size, Long totalElements, Integer totalPages, Boolean hasNext, Boolean hasPrevious, String modelId, String modelName, List<@Valid HistoricalLeaderboardEntryDto> history) {
    this.number = number;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.modelId = modelId;
    this.modelName = modelName;
    this.history = history;
  }

  public LeaderboardModelHistoryPageDto number(Integer number) {
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

  public LeaderboardModelHistoryPageDto size(Integer size) {
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

  public LeaderboardModelHistoryPageDto totalElements(Long totalElements) {
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

  public LeaderboardModelHistoryPageDto totalPages(Integer totalPages) {
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

  public LeaderboardModelHistoryPageDto hasNext(Boolean hasNext) {
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

  public LeaderboardModelHistoryPageDto hasPrevious(Boolean hasPrevious) {
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

  public LeaderboardModelHistoryPageDto modelId(String modelId) {
    this.modelId = modelId;
    return this;
  }

  /**
   * Identifier for the model
   * @return modelId
   */
  @NotNull 
  @Schema(name = "modelId", example = "model_456", description = "Identifier for the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelId")
  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public LeaderboardModelHistoryPageDto modelName(String modelName) {
    this.modelName = modelName;
    return this;
  }

  /**
   * Display name of the model
   * @return modelName
   */
  @NotNull 
  @Schema(name = "modelName", example = "GPT-4o", description = "Display name of the model", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelName")
  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public LeaderboardModelHistoryPageDto history(List<@Valid HistoricalLeaderboardEntryDto> history) {
    this.history = history;
    return this;
  }

  public LeaderboardModelHistoryPageDto addHistoryItem(HistoricalLeaderboardEntryDto historyItem) {
    if (this.history == null) {
      this.history = new ArrayList<>();
    }
    this.history.add(historyItem);
    return this;
  }

  /**
   * A list of historical leaderboard entries.
   * @return history
   */
  @NotNull @Valid 
  @Schema(name = "history", description = "A list of historical leaderboard entries.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("history")
  public List<@Valid HistoricalLeaderboardEntryDto> getHistory() {
    return history;
  }

  public void setHistory(List<@Valid HistoricalLeaderboardEntryDto> history) {
    this.history = history;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderboardModelHistoryPageDto leaderboardModelHistoryPage = (LeaderboardModelHistoryPageDto) o;
    return Objects.equals(this.number, leaderboardModelHistoryPage.number) &&
        Objects.equals(this.size, leaderboardModelHistoryPage.size) &&
        Objects.equals(this.totalElements, leaderboardModelHistoryPage.totalElements) &&
        Objects.equals(this.totalPages, leaderboardModelHistoryPage.totalPages) &&
        Objects.equals(this.hasNext, leaderboardModelHistoryPage.hasNext) &&
        Objects.equals(this.hasPrevious, leaderboardModelHistoryPage.hasPrevious) &&
        Objects.equals(this.modelId, leaderboardModelHistoryPage.modelId) &&
        Objects.equals(this.modelName, leaderboardModelHistoryPage.modelName) &&
        Objects.equals(this.history, leaderboardModelHistoryPage.history);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size, totalElements, totalPages, hasNext, hasPrevious, modelId, modelName, history);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderboardModelHistoryPageDto {\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    hasNext: ").append(toIndentedString(hasNext)).append("\n");
    sb.append("    hasPrevious: ").append(toIndentedString(hasPrevious)).append("\n");
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    modelName: ").append(toIndentedString(modelName)).append("\n");
    sb.append("    history: ").append(toIndentedString(history)).append("\n");
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

    private LeaderboardModelHistoryPageDto instance;

    public Builder() {
      this(new LeaderboardModelHistoryPageDto());
    }

    protected Builder(LeaderboardModelHistoryPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LeaderboardModelHistoryPageDto value) { 
      this.instance.setNumber(value.number);
      this.instance.setSize(value.size);
      this.instance.setTotalElements(value.totalElements);
      this.instance.setTotalPages(value.totalPages);
      this.instance.setHasNext(value.hasNext);
      this.instance.setHasPrevious(value.hasPrevious);
      this.instance.setModelId(value.modelId);
      this.instance.setModelName(value.modelName);
      this.instance.setHistory(value.history);
      return this;
    }

    public LeaderboardModelHistoryPageDto.Builder number(Integer number) {
      this.instance.number(number);
      return this;
    }
    
    public LeaderboardModelHistoryPageDto.Builder size(Integer size) {
      this.instance.size(size);
      return this;
    }
    
    public LeaderboardModelHistoryPageDto.Builder totalElements(Long totalElements) {
      this.instance.totalElements(totalElements);
      return this;
    }
    
    public LeaderboardModelHistoryPageDto.Builder totalPages(Integer totalPages) {
      this.instance.totalPages(totalPages);
      return this;
    }
    
    public LeaderboardModelHistoryPageDto.Builder hasNext(Boolean hasNext) {
      this.instance.hasNext(hasNext);
      return this;
    }
    
    public LeaderboardModelHistoryPageDto.Builder hasPrevious(Boolean hasPrevious) {
      this.instance.hasPrevious(hasPrevious);
      return this;
    }
    
    public LeaderboardModelHistoryPageDto.Builder modelId(String modelId) {
      this.instance.modelId(modelId);
      return this;
    }
    
    public LeaderboardModelHistoryPageDto.Builder modelName(String modelName) {
      this.instance.modelName(modelName);
      return this;
    }
    
    public LeaderboardModelHistoryPageDto.Builder history(List<HistoricalLeaderboardEntryDto> history) {
      this.instance.history(history);
      return this;
    }
    
    /**
    * returns a built LeaderboardModelHistoryPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LeaderboardModelHistoryPageDto build() {
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
  public static LeaderboardModelHistoryPageDto.Builder builder() {
    return new LeaderboardModelHistoryPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LeaderboardModelHistoryPageDto.Builder toBuilder() {
    LeaderboardModelHistoryPageDto.Builder builder = new LeaderboardModelHistoryPageDto.Builder();
    return builder.copyOf(this);
  }

}

