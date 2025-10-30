package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to create a new battle.
 */

@Schema(name = "BattleCreateRequest", description = "The information used to create a new battle.")
@JsonTypeName("BattleCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleCreateRequestDto {

  private @Nullable String title;

  public BattleCreateRequestDto title(@Nullable String title) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleCreateRequestDto battleCreateRequest = (BattleCreateRequestDto) o;
    return Objects.equals(this.title, battleCreateRequest.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleCreateRequestDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

    private BattleCreateRequestDto instance;

    public Builder() {
      this(new BattleCreateRequestDto());
    }

    protected Builder(BattleCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleCreateRequestDto value) { 
      this.instance.setTitle(value.title);
      return this;
    }

    public BattleCreateRequestDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    /**
    * returns a built BattleCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleCreateRequestDto build() {
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
  public static BattleCreateRequestDto.Builder builder() {
    return new BattleCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleCreateRequestDto.Builder toBuilder() {
    BattleCreateRequestDto.Builder builder = new BattleCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

