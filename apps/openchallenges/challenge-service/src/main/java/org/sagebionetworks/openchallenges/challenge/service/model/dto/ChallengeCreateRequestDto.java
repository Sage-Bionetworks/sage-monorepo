package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to create a challenge
 */

@Schema(name = "ChallengeCreateRequest", description = "The information used to create a challenge")
@JsonTypeName("ChallengeCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeCreateRequestDto {

  private String name;

  private @Nullable String headline = null;

  private String description;

  private ChallengeStatusDto status;

  public ChallengeCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeCreateRequestDto(String name, String description, ChallengeStatusDto status) {
    this.name = name;
    this.description = description;
    this.status = status;
  }

  public ChallengeCreateRequestDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the challenge.
   * @return name
   */
  @NotNull @Size(min = 3, max = 255) 
  @Schema(name = "name", description = "The name of the challenge.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChallengeCreateRequestDto headline(String headline) {
    this.headline = headline;
    return this;
  }

  /**
   * The headline of the challenge.
   * @return headline
   */
  @Size(min = 0, max = 80) 
  @Schema(name = "headline", example = "Example challenge headline", description = "The headline of the challenge.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("headline")
  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public ChallengeCreateRequestDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the challenge.
   * @return description
   */
  @NotNull @Size(min = 0, max = 1000) 
  @Schema(name = "description", example = "This is an example description of the challenge.", description = "The description of the challenge.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ChallengeCreateRequestDto status(ChallengeStatusDto status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public ChallengeStatusDto getStatus() {
    return status;
  }

  public void setStatus(ChallengeStatusDto status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeCreateRequestDto challengeCreateRequest = (ChallengeCreateRequestDto) o;
    return Objects.equals(this.name, challengeCreateRequest.name) &&
        Objects.equals(this.headline, challengeCreateRequest.headline) &&
        Objects.equals(this.description, challengeCreateRequest.description) &&
        Objects.equals(this.status, challengeCreateRequest.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, headline, description, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeCreateRequestDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    headline: ").append(toIndentedString(headline)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

    private ChallengeCreateRequestDto instance;

    public Builder() {
      this(new ChallengeCreateRequestDto());
    }

    protected Builder(ChallengeCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeCreateRequestDto value) { 
      this.instance.setName(value.name);
      this.instance.setHeadline(value.headline);
      this.instance.setDescription(value.description);
      this.instance.setStatus(value.status);
      return this;
    }

    public ChallengeCreateRequestDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder headline(String headline) {
      this.instance.headline(headline);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder description(String description) {
      this.instance.description(description);
      return this;
    }
    
    public ChallengeCreateRequestDto.Builder status(ChallengeStatusDto status) {
      this.instance.status(status);
      return this;
    }
    
    /**
    * returns a built ChallengeCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeCreateRequestDto build() {
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
  public static ChallengeCreateRequestDto.Builder builder() {
    return new ChallengeCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeCreateRequestDto.Builder toBuilder() {
    ChallengeCreateRequestDto.Builder builder = new ChallengeCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

