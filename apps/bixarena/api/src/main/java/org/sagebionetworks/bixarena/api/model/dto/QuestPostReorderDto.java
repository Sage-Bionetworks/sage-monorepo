package org.sagebionetworks.bixarena.api.model.dto;

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
 * Request body for reordering all posts in a quest
 */

@Schema(name = "QuestPostReorder", description = "Request body for reordering all posts in a quest")
@JsonTypeName("QuestPostReorder")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class QuestPostReorderDto {

  @Valid
  private List<Integer> postIndexes = new ArrayList<>();

  public QuestPostReorderDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QuestPostReorderDto(List<Integer> postIndexes) {
    this.postIndexes = postIndexes;
  }

  public QuestPostReorderDto postIndexes(List<Integer> postIndexes) {
    this.postIndexes = postIndexes;
    return this;
  }

  public QuestPostReorderDto addPostIndexesItem(Integer postIndexesItem) {
    if (this.postIndexes == null) {
      this.postIndexes = new ArrayList<>();
    }
    this.postIndexes.add(postIndexesItem);
    return this;
  }

  /**
   * Complete ordered list of existing post indexes. The backend reassigns post_index values 0, 1, 2, ... based on array order.
   * @return postIndexes
   */
  @NotNull 
  @Schema(name = "postIndexes", example = "[3,0,1,2,4]", description = "Complete ordered list of existing post indexes. The backend reassigns post_index values 0, 1, 2, ... based on array order.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("postIndexes")
  public List<Integer> getPostIndexes() {
    return postIndexes;
  }

  public void setPostIndexes(List<Integer> postIndexes) {
    this.postIndexes = postIndexes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestPostReorderDto questPostReorder = (QuestPostReorderDto) o;
    return Objects.equals(this.postIndexes, questPostReorder.postIndexes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postIndexes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuestPostReorderDto {\n");
    sb.append("    postIndexes: ").append(toIndentedString(postIndexes)).append("\n");
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

    private QuestPostReorderDto instance;

    public Builder() {
      this(new QuestPostReorderDto());
    }

    protected Builder(QuestPostReorderDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(QuestPostReorderDto value) { 
      this.instance.setPostIndexes(value.postIndexes);
      return this;
    }

    public QuestPostReorderDto.Builder postIndexes(List<Integer> postIndexes) {
      this.instance.postIndexes(postIndexes);
      return this;
    }
    
    /**
    * returns a built QuestPostReorderDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public QuestPostReorderDto build() {
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
  public static QuestPostReorderDto.Builder builder() {
    return new QuestPostReorderDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public QuestPostReorderDto.Builder toBuilder() {
    QuestPostReorderDto.Builder builder = new QuestPostReorderDto.Builder();
    return builder.copyOf(this);
  }

}

