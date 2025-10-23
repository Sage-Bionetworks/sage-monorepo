package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.VotePreferenceDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to create a new vote.
 */

@Schema(name = "VoteCreateRequest", description = "The information used to create a new vote.")
@JsonTypeName("VoteCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class VoteCreateRequestDto {

  private UUID battleId;

  private VotePreferenceDto preference;

  public VoteCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public VoteCreateRequestDto(UUID battleId, VotePreferenceDto preference) {
    this.battleId = battleId;
    this.preference = preference;
  }

  public VoteCreateRequestDto battleId(UUID battleId) {
    this.battleId = battleId;
    return this;
  }

  /**
   * Unique identifier (UUID) of the battle.
   * @return battleId
   */
  @NotNull @Valid 
  @Schema(name = "battleId", example = "5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f", description = "Unique identifier (UUID) of the battle.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("battleId")
  public UUID getBattleId() {
    return battleId;
  }

  public void setBattleId(UUID battleId) {
    this.battleId = battleId;
  }

  public VoteCreateRequestDto preference(VotePreferenceDto preference) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoteCreateRequestDto voteCreateRequest = (VoteCreateRequestDto) o;
    return Objects.equals(this.battleId, voteCreateRequest.battleId) &&
        Objects.equals(this.preference, voteCreateRequest.preference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(battleId, preference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoteCreateRequestDto {\n");
    sb.append("    battleId: ").append(toIndentedString(battleId)).append("\n");
    sb.append("    preference: ").append(toIndentedString(preference)).append("\n");
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

    private VoteCreateRequestDto instance;

    public Builder() {
      this(new VoteCreateRequestDto());
    }

    protected Builder(VoteCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(VoteCreateRequestDto value) { 
      this.instance.setBattleId(value.battleId);
      this.instance.setPreference(value.preference);
      return this;
    }

    public VoteCreateRequestDto.Builder battleId(UUID battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public VoteCreateRequestDto.Builder preference(VotePreferenceDto preference) {
      this.instance.preference(preference);
      return this;
    }
    
    /**
    * returns a built VoteCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public VoteCreateRequestDto build() {
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
  public static VoteCreateRequestDto.Builder builder() {
    return new VoteCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public VoteCreateRequestDto.Builder toBuilder() {
    VoteCreateRequestDto.Builder builder = new VoteCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

