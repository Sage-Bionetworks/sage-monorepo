package org.sagebionetworks.openchallenges.challenge.service.model.dto;

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
 * A challenge platform update request.
 */

@Schema(name = "ChallengePlatformUpdateRequest", description = "A challenge platform update request.")
@JsonTypeName("ChallengePlatformUpdateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengePlatformUpdateRequestDto {

  private String displayName;

  public ChallengePlatformUpdateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengePlatformUpdateRequestDto(String displayName) {
    this.displayName = displayName;
  }

  public ChallengePlatformUpdateRequestDto displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * The display name of the challenge platform.
   * @return displayName
   */
  @NotNull @Size(min = 3, max = 50) 
  @Schema(name = "displayName", example = "Example Challenge Platform", description = "The display name of the challenge platform.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengePlatformUpdateRequestDto challengePlatformUpdateRequest = (ChallengePlatformUpdateRequestDto) o;
    return Objects.equals(this.displayName, challengePlatformUpdateRequest.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformUpdateRequestDto {\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
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

    private ChallengePlatformUpdateRequestDto instance;

    public Builder() {
      this(new ChallengePlatformUpdateRequestDto());
    }

    protected Builder(ChallengePlatformUpdateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengePlatformUpdateRequestDto value) { 
      this.instance.setDisplayName(value.displayName);
      return this;
    }

    public ChallengePlatformUpdateRequestDto.Builder displayName(String displayName) {
      this.instance.displayName(displayName);
      return this;
    }
    
    /**
    * returns a built ChallengePlatformUpdateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengePlatformUpdateRequestDto build() {
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
  public static ChallengePlatformUpdateRequestDto.Builder builder() {
    return new ChallengePlatformUpdateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengePlatformUpdateRequestDto.Builder toBuilder() {
    ChallengePlatformUpdateRequestDto.Builder builder = new ChallengePlatformUpdateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

