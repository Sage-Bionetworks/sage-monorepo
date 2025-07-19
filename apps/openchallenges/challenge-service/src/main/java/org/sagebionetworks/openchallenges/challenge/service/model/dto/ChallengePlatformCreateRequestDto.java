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
 * The information used to create a challenge platform
 */

@Schema(name = "ChallengePlatformCreateRequest", description = "The information used to create a challenge platform")
@JsonTypeName("ChallengePlatformCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengePlatformCreateRequestDto {

  private String displayName;

  public ChallengePlatformCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengePlatformCreateRequestDto(String displayName) {
    this.displayName = displayName;
  }

  public ChallengePlatformCreateRequestDto displayName(String displayName) {
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
    ChallengePlatformCreateRequestDto challengePlatformCreateRequest = (ChallengePlatformCreateRequestDto) o;
    return Objects.equals(this.displayName, challengePlatformCreateRequest.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengePlatformCreateRequestDto {\n");
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

    private ChallengePlatformCreateRequestDto instance;

    public Builder() {
      this(new ChallengePlatformCreateRequestDto());
    }

    protected Builder(ChallengePlatformCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengePlatformCreateRequestDto value) { 
      this.instance.setDisplayName(value.displayName);
      return this;
    }

    public ChallengePlatformCreateRequestDto.Builder displayName(String displayName) {
      this.instance.displayName(displayName);
      return this;
    }
    
    /**
    * returns a built ChallengePlatformCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengePlatformCreateRequestDto build() {
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
  public static ChallengePlatformCreateRequestDto.Builder builder() {
    return new ChallengePlatformCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengePlatformCreateRequestDto.Builder toBuilder() {
    ChallengePlatformCreateRequestDto.Builder builder = new ChallengePlatformCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

