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
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.BiomedicalCategoryDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The result of a categorization run for a battle.
 */

@Schema(name = "BattleCategorizationResponse", description = "The result of a categorization run for a battle.")
@JsonTypeName("BattleCategorizationResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleCategorizationResponseDto {

  private UUID id;

  private UUID battleId;

  @Valid
  private List<BiomedicalCategoryDto> categories = new ArrayList<>();

  private String method;

  private @Nullable UUID categorizedBy = null;

  private @Nullable String reason = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public BattleCategorizationResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleCategorizationResponseDto(UUID id, UUID battleId, List<BiomedicalCategoryDto> categories, String method, OffsetDateTime createdAt) {
    this.id = id;
    this.battleId = battleId;
    this.categories = categories;
    this.method = method;
    this.createdAt = createdAt;
  }

  public BattleCategorizationResponseDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public BattleCategorizationResponseDto battleId(UUID battleId) {
    this.battleId = battleId;
    return this;
  }

  /**
   * Get battleId
   * @return battleId
   */
  @NotNull @Valid 
  @Schema(name = "battleId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("battleId")
  public UUID getBattleId() {
    return battleId;
  }

  public void setBattleId(UUID battleId) {
    this.battleId = battleId;
  }

  public BattleCategorizationResponseDto categories(List<BiomedicalCategoryDto> categories) {
    this.categories = categories;
    return this;
  }

  public BattleCategorizationResponseDto addCategoriesItem(BiomedicalCategoryDto categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * Categories assigned by this run. Empty when the classifier ran successfully but declared no category fits (legitimate \"no fit\" result). Always non-empty for human-review rows (the create request requires at least one).
   * @return categories
   */
  @NotNull @Valid @Size(max = 3) 
  @Schema(name = "categories", description = "Categories assigned by this run. Empty when the classifier ran successfully but declared no category fits (legitimate \"no fit\" result). Always non-empty for human-review rows (the create request requires at least one).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("categories")
  public List<BiomedicalCategoryDto> getCategories() {
    return categories;
  }

  public void setCategories(List<BiomedicalCategoryDto> categories) {
    this.categories = categories;
  }

  public BattleCategorizationResponseDto method(String method) {
    this.method = method;
    return this;
  }

  /**
   * Get method
   * @return method
   */
  @NotNull @Size(max = 100) 
  @Schema(name = "method", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("method")
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public BattleCategorizationResponseDto categorizedBy(@Nullable UUID categorizedBy) {
    this.categorizedBy = categorizedBy;
    return this;
  }

  /**
   * User ID of the categorizer. Null for AI runs.
   * @return categorizedBy
   */
  @Valid 
  @Schema(name = "categorizedBy", description = "User ID of the categorizer. Null for AI runs.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("categorizedBy")
  public @Nullable UUID getCategorizedBy() {
    return categorizedBy;
  }

  public void setCategorizedBy(@Nullable UUID categorizedBy) {
    this.categorizedBy = categorizedBy;
  }

  public BattleCategorizationResponseDto reason(@Nullable String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Human override reason. Always null for AI runs.
   * @return reason
   */
  @Size(max = 1000) 
  @Schema(name = "reason", description = "Human override reason. Always null for AI runs.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public @Nullable String getReason() {
    return reason;
  }

  public void setReason(@Nullable String reason) {
    this.reason = reason;
  }

  public BattleCategorizationResponseDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleCategorizationResponseDto battleCategorizationResponse = (BattleCategorizationResponseDto) o;
    return Objects.equals(this.id, battleCategorizationResponse.id) &&
        Objects.equals(this.battleId, battleCategorizationResponse.battleId) &&
        Objects.equals(this.categories, battleCategorizationResponse.categories) &&
        Objects.equals(this.method, battleCategorizationResponse.method) &&
        Objects.equals(this.categorizedBy, battleCategorizationResponse.categorizedBy) &&
        Objects.equals(this.reason, battleCategorizationResponse.reason) &&
        Objects.equals(this.createdAt, battleCategorizationResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, battleId, categories, method, categorizedBy, reason, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleCategorizationResponseDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    battleId: ").append(toIndentedString(battleId)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    categorizedBy: ").append(toIndentedString(categorizedBy)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

    private BattleCategorizationResponseDto instance;

    public Builder() {
      this(new BattleCategorizationResponseDto());
    }

    protected Builder(BattleCategorizationResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleCategorizationResponseDto value) { 
      this.instance.setId(value.id);
      this.instance.setBattleId(value.battleId);
      this.instance.setCategories(value.categories);
      this.instance.setMethod(value.method);
      this.instance.setCategorizedBy(value.categorizedBy);
      this.instance.setReason(value.reason);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public BattleCategorizationResponseDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public BattleCategorizationResponseDto.Builder battleId(UUID battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public BattleCategorizationResponseDto.Builder categories(List<BiomedicalCategoryDto> categories) {
      this.instance.categories(categories);
      return this;
    }
    
    public BattleCategorizationResponseDto.Builder method(String method) {
      this.instance.method(method);
      return this;
    }
    
    public BattleCategorizationResponseDto.Builder categorizedBy(UUID categorizedBy) {
      this.instance.categorizedBy(categorizedBy);
      return this;
    }
    
    public BattleCategorizationResponseDto.Builder reason(String reason) {
      this.instance.reason(reason);
      return this;
    }
    
    public BattleCategorizationResponseDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built BattleCategorizationResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleCategorizationResponseDto build() {
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
  public static BattleCategorizationResponseDto.Builder builder() {
    return new BattleCategorizationResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleCategorizationResponseDto.Builder toBuilder() {
    BattleCategorizationResponseDto.Builder builder = new BattleCategorizationResponseDto.Builder();
    return builder.copyOf(this);
  }

}

