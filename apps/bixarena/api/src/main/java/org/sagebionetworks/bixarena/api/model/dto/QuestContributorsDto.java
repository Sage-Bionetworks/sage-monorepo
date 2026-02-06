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
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * List of contributors to a quest with their battle counts
 */

@Schema(name = "QuestContributors", description = "List of contributors to a quest with their battle counts")
@JsonTypeName("QuestContributors")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class QuestContributorsDto {

  private String questId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private Integer totalContributors;

  @Valid
  private List<@Valid QuestContributorDto> contributors = new ArrayList<>();

  public QuestContributorsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QuestContributorsDto(String questId, OffsetDateTime startDate, OffsetDateTime endDate, Integer totalContributors, List<@Valid QuestContributorDto> contributors) {
    this.questId = questId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.totalContributors = totalContributors;
    this.contributors = contributors;
  }

  public QuestContributorsDto questId(String questId) {
    this.questId = questId;
    return this;
  }

  /**
   * The quest identifier
   * @return questId
   */
  @NotNull 
  @Schema(name = "questId", example = "build-bioarena-together", description = "The quest identifier", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("questId")
  public String getQuestId() {
    return questId;
  }

  public void setQuestId(String questId) {
    this.questId = questId;
  }

  public QuestContributorsDto startDate(OffsetDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Quest start date
   * @return startDate
   */
  @NotNull @Valid 
  @Schema(name = "startDate", example = "2026-01-20T00:00Z", description = "Quest start date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("startDate")
  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(OffsetDateTime startDate) {
    this.startDate = startDate;
  }

  public QuestContributorsDto endDate(OffsetDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Quest end date
   * @return endDate
   */
  @NotNull @Valid 
  @Schema(name = "endDate", example = "2026-04-20T23:59:59Z", description = "Quest end date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("endDate")
  public OffsetDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(OffsetDateTime endDate) {
    this.endDate = endDate;
  }

  public QuestContributorsDto totalContributors(Integer totalContributors) {
    this.totalContributors = totalContributors;
    return this;
  }

  /**
   * Total number of unique contributors
   * @return totalContributors
   */
  @NotNull 
  @Schema(name = "totalContributors", example = "42", description = "Total number of unique contributors", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalContributors")
  public Integer getTotalContributors() {
    return totalContributors;
  }

  public void setTotalContributors(Integer totalContributors) {
    this.totalContributors = totalContributors;
  }

  public QuestContributorsDto contributors(List<@Valid QuestContributorDto> contributors) {
    this.contributors = contributors;
    return this;
  }

  public QuestContributorsDto addContributorsItem(QuestContributorDto contributorsItem) {
    if (this.contributors == null) {
      this.contributors = new ArrayList<>();
    }
    this.contributors.add(contributorsItem);
    return this;
  }

  /**
   * Contributors ordered by battle count (descending)
   * @return contributors
   */
  @NotNull @Valid 
  @Schema(name = "contributors", description = "Contributors ordered by battle count (descending)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contributors")
  public List<@Valid QuestContributorDto> getContributors() {
    return contributors;
  }

  public void setContributors(List<@Valid QuestContributorDto> contributors) {
    this.contributors = contributors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestContributorsDto questContributors = (QuestContributorsDto) o;
    return Objects.equals(this.questId, questContributors.questId) &&
        Objects.equals(this.startDate, questContributors.startDate) &&
        Objects.equals(this.endDate, questContributors.endDate) &&
        Objects.equals(this.totalContributors, questContributors.totalContributors) &&
        Objects.equals(this.contributors, questContributors.contributors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(questId, startDate, endDate, totalContributors, contributors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuestContributorsDto {\n");
    sb.append("    questId: ").append(toIndentedString(questId)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    totalContributors: ").append(toIndentedString(totalContributors)).append("\n");
    sb.append("    contributors: ").append(toIndentedString(contributors)).append("\n");
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

    private QuestContributorsDto instance;

    public Builder() {
      this(new QuestContributorsDto());
    }

    protected Builder(QuestContributorsDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(QuestContributorsDto value) { 
      this.instance.setQuestId(value.questId);
      this.instance.setStartDate(value.startDate);
      this.instance.setEndDate(value.endDate);
      this.instance.setTotalContributors(value.totalContributors);
      this.instance.setContributors(value.contributors);
      return this;
    }

    public QuestContributorsDto.Builder questId(String questId) {
      this.instance.questId(questId);
      return this;
    }
    
    public QuestContributorsDto.Builder startDate(OffsetDateTime startDate) {
      this.instance.startDate(startDate);
      return this;
    }
    
    public QuestContributorsDto.Builder endDate(OffsetDateTime endDate) {
      this.instance.endDate(endDate);
      return this;
    }
    
    public QuestContributorsDto.Builder totalContributors(Integer totalContributors) {
      this.instance.totalContributors(totalContributors);
      return this;
    }
    
    public QuestContributorsDto.Builder contributors(List<QuestContributorDto> contributors) {
      this.instance.contributors(contributors);
      return this;
    }
    
    /**
    * returns a built QuestContributorsDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public QuestContributorsDto build() {
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
  public static QuestContributorsDto.Builder builder() {
    return new QuestContributorsDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public QuestContributorsDto.Builder toBuilder() {
    QuestContributorsDto.Builder builder = new QuestContributorsDto.Builder();
    return builder.copyOf(this);
  }

}

