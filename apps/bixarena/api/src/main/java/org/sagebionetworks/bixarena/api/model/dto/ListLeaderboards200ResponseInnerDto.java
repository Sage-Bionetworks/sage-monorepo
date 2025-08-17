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
 * ListLeaderboards200ResponseInnerDto
 */

@JsonTypeName("listLeaderboards_200_response_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ListLeaderboards200ResponseInnerDto {

  private String id;

  private String name;

  private String description;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public ListLeaderboards200ResponseInnerDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ListLeaderboards200ResponseInnerDto(String id, String name, String description, OffsetDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.updatedAt = updatedAt;
  }

  public ListLeaderboards200ResponseInnerDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the leaderboard
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "open-source", description = "Unique identifier for the leaderboard", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ListLeaderboards200ResponseInnerDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Display name for the leaderboard
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Open Source Models", description = "Display name for the leaderboard", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ListLeaderboards200ResponseInnerDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of what this leaderboard measures
   * @return description
   */
  @NotNull 
  @Schema(name = "description", example = "Performance ranking of open-source AI models", description = "Description of what this leaderboard measures", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ListLeaderboards200ResponseInnerDto updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * When this leaderboard was last updated
   * @return updatedAt
   */
  @NotNull @Valid 
  @Schema(name = "updatedAt", example = "2025-08-16T14:30Z", description = "When this leaderboard was last updated", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedAt")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ListLeaderboards200ResponseInnerDto listLeaderboards200ResponseInner = (ListLeaderboards200ResponseInnerDto) o;
    return Objects.equals(this.id, listLeaderboards200ResponseInner.id) &&
        Objects.equals(this.name, listLeaderboards200ResponseInner.name) &&
        Objects.equals(this.description, listLeaderboards200ResponseInner.description) &&
        Objects.equals(this.updatedAt, listLeaderboards200ResponseInner.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListLeaderboards200ResponseInnerDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

    private ListLeaderboards200ResponseInnerDto instance;

    public Builder() {
      this(new ListLeaderboards200ResponseInnerDto());
    }

    protected Builder(ListLeaderboards200ResponseInnerDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ListLeaderboards200ResponseInnerDto value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setDescription(value.description);
      this.instance.setUpdatedAt(value.updatedAt);
      return this;
    }

    public ListLeaderboards200ResponseInnerDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public ListLeaderboards200ResponseInnerDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ListLeaderboards200ResponseInnerDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public ListLeaderboards200ResponseInnerDto.Builder updatedAt(OffsetDateTime updatedAt) {
      this.instance.updatedAt(updatedAt);
      return this;
    }
    
    /**
    * returns a built ListLeaderboards200ResponseInnerDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ListLeaderboards200ResponseInnerDto build() {
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
  public static ListLeaderboards200ResponseInnerDto.Builder builder() {
    return new ListLeaderboards200ResponseInnerDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ListLeaderboards200ResponseInnerDto.Builder toBuilder() {
    ListLeaderboards200ResponseInnerDto.Builder builder = new ListLeaderboards200ResponseInnerDto.Builder();
    return builder.copyOf(this);
  }

}

