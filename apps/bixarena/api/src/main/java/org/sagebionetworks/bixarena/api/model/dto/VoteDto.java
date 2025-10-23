package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.VotePreferenceDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A vote entity representing a user&#39;s preference in a battle between two AI models.
 */

@Schema(name = "Vote", description = "A vote entity representing a user's preference in a battle between two AI models.")
@JsonTypeName("Vote")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class VoteDto {

  private UUID id;

  private UUID battleId;

  private VotePreferenceDto preference;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public VoteDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public VoteDto(UUID id, UUID battleId, VotePreferenceDto preference, OffsetDateTime createdAt) {
    this.id = id;
    this.battleId = battleId;
    this.preference = preference;
    this.createdAt = createdAt;
  }

  public VoteDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the vote
   * @return id
   */
  @NotNull @Valid 
  @Schema(name = "id", description = "The unique identifier of the vote", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public VoteDto battleId(UUID battleId) {
    this.battleId = battleId;
    return this;
  }

  /**
   * The identifier of the battle this vote belongs to
   * @return battleId
   */
  @NotNull @Valid 
  @Schema(name = "battleId", description = "The identifier of the battle this vote belongs to", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("battleId")
  public UUID getBattleId() {
    return battleId;
  }

  public void setBattleId(UUID battleId) {
    this.battleId = battleId;
  }

  public VoteDto preference(VotePreferenceDto preference) {
    this.preference = preference;
    return this;
  }

  /**
   * Get preference
   * @return preference
   */
  @NotNull @Valid 
  @Schema(name = "preference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("preference")
  public VotePreferenceDto getPreference() {
    return preference;
  }

  public void setPreference(VotePreferenceDto preference) {
    this.preference = preference;
  }

  public VoteDto createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Timestamp when the entity was created.
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", example = "2024-01-15T10:30Z", description = "Timestamp when the entity was created.", requiredMode = Schema.RequiredMode.REQUIRED)
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
    VoteDto vote = (VoteDto) o;
    return Objects.equals(this.id, vote.id) &&
        Objects.equals(this.battleId, vote.battleId) &&
        Objects.equals(this.preference, vote.preference) &&
        Objects.equals(this.createdAt, vote.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, battleId, preference, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoteDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    battleId: ").append(toIndentedString(battleId)).append("\n");
    sb.append("    preference: ").append(toIndentedString(preference)).append("\n");
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

    private VoteDto instance;

    public Builder() {
      this(new VoteDto());
    }

    protected Builder(VoteDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(VoteDto value) { 
      this.instance.setId(value.id);
      this.instance.setBattleId(value.battleId);
      this.instance.setPreference(value.preference);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public VoteDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public VoteDto.Builder battleId(UUID battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public VoteDto.Builder preference(VotePreferenceDto preference) {
      this.instance.preference(preference);
      return this;
    }
    
    public VoteDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built VoteDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public VoteDto build() {
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
  public static VoteDto.Builder builder() {
    return new VoteDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public VoteDto.Builder toBuilder() {
    VoteDto.Builder builder = new VoteDto.Builder();
    return builder.copyOf(this);
  }

}

