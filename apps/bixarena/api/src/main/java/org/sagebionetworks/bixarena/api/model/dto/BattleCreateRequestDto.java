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

  private @Nullable String title = null;

  private String userId;

  private String modelAId;

  private String modelBId;

  public BattleCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleCreateRequestDto(String userId, String modelAId, String modelBId) {
    this.userId = userId;
    this.modelAId = modelAId;
    this.modelBId = modelBId;
  }

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

  public BattleCreateRequestDto userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * UUID of the user who is initiating this battle.
   * @return userId
   */
  @NotNull 
  @Schema(name = "userId", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of the user who is initiating this battle.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public BattleCreateRequestDto modelAId(String modelAId) {
    this.modelAId = modelAId;
    return this;
  }

  /**
   * UUID of model A to compare.
   * @return modelAId
   */
  @NotNull 
  @Schema(name = "modelAId", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of model A to compare.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelAId")
  public String getModelAId() {
    return modelAId;
  }

  public void setModelAId(String modelAId) {
    this.modelAId = modelAId;
  }

  public BattleCreateRequestDto modelBId(String modelBId) {
    this.modelBId = modelBId;
    return this;
  }

  /**
   * UUID of model B to compare.
   * @return modelBId
   */
  @NotNull 
  @Schema(name = "modelBId", example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c", description = "UUID of model B to compare.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelBId")
  public String getModelBId() {
    return modelBId;
  }

  public void setModelBId(String modelBId) {
    this.modelBId = modelBId;
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
    return Objects.equals(this.title, battleCreateRequest.title) &&
        Objects.equals(this.userId, battleCreateRequest.userId) &&
        Objects.equals(this.modelAId, battleCreateRequest.modelAId) &&
        Objects.equals(this.modelBId, battleCreateRequest.modelBId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, userId, modelAId, modelBId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleCreateRequestDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    modelAId: ").append(toIndentedString(modelAId)).append("\n");
    sb.append("    modelBId: ").append(toIndentedString(modelBId)).append("\n");
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
      this.instance.setUserId(value.userId);
      this.instance.setModelAId(value.modelAId);
      this.instance.setModelBId(value.modelBId);
      return this;
    }

    public BattleCreateRequestDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public BattleCreateRequestDto.Builder userId(String userId) {
      this.instance.userId(userId);
      return this;
    }
    
    public BattleCreateRequestDto.Builder modelAId(String modelAId) {
      this.instance.modelAId(modelAId);
      return this;
    }
    
    public BattleCreateRequestDto.Builder modelBId(String modelBId) {
      this.instance.modelBId(modelBId);
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

