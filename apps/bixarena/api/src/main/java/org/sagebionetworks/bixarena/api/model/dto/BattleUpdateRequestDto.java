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
 * The information used to update a battle.
 */

@Schema(name = "BattleUpdateRequest", description = "The information used to update a battle.")
@JsonTypeName("BattleUpdateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleUpdateRequestDto {

  private @Nullable String title = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime endedAt = null;

  public BattleUpdateRequestDto title(@Nullable String title) {
    this.title = title;
    return this;
  }

  /**
   * Title of the battle.
   * @return title
   */
  
  @Schema(name = "title", example = "Gene Expression Analysis Comparison", description = "Title of the battle.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public @Nullable String getTitle() {
    return title;
  }

  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  public BattleUpdateRequestDto endedAt(@Nullable OffsetDateTime endedAt) {
    this.endedAt = endedAt;
    return this;
  }

  /**
   * Timestamp when the entity ended.
   * @return endedAt
   */
  @Valid 
  @Schema(name = "endedAt", example = "2024-01-15T11:45Z", description = "Timestamp when the entity ended.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endedAt")
  public @Nullable OffsetDateTime getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(@Nullable OffsetDateTime endedAt) {
    this.endedAt = endedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleUpdateRequestDto battleUpdateRequest = (BattleUpdateRequestDto) o;
    return Objects.equals(this.title, battleUpdateRequest.title) &&
        Objects.equals(this.endedAt, battleUpdateRequest.endedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, endedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleUpdateRequestDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    endedAt: ").append(toIndentedString(endedAt)).append("\n");
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

    private BattleUpdateRequestDto instance;

    public Builder() {
      this(new BattleUpdateRequestDto());
    }

    protected Builder(BattleUpdateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleUpdateRequestDto value) { 
      this.instance.setTitle(value.title);
      this.instance.setEndedAt(value.endedAt);
      return this;
    }

    public BattleUpdateRequestDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public BattleUpdateRequestDto.Builder endedAt(OffsetDateTime endedAt) {
      this.instance.endedAt(endedAt);
      return this;
    }
    
    /**
    * returns a built BattleUpdateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleUpdateRequestDto build() {
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
  public static BattleUpdateRequestDto.Builder builder() {
    return new BattleUpdateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleUpdateRequestDto.Builder toBuilder() {
    BattleUpdateRequestDto.Builder builder = new BattleUpdateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

