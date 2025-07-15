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
 * The unique identifier of the challenge contribution
 */

@Schema(name = "ChallengeContributionCreateResponse", description = "The unique identifier of the challenge contribution")
@JsonTypeName("ChallengeContributionCreateResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeContributionCreateResponseDto {

  private Long id;

  public ChallengeContributionCreateResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeContributionCreateResponseDto(Long id) {
    this.id = id;
  }

  public ChallengeContributionCreateResponseDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of a challenge contribution
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "1", description = "The unique identifier of a challenge contribution", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeContributionCreateResponseDto challengeContributionCreateResponse = (ChallengeContributionCreateResponseDto) o;
    return Objects.equals(this.id, challengeContributionCreateResponse.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeContributionCreateResponseDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

    private ChallengeContributionCreateResponseDto instance;

    public Builder() {
      this(new ChallengeContributionCreateResponseDto());
    }

    protected Builder(ChallengeContributionCreateResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeContributionCreateResponseDto value) { 
      this.instance.setId(value.id);
      return this;
    }

    public ChallengeContributionCreateResponseDto.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    /**
    * returns a built ChallengeContributionCreateResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeContributionCreateResponseDto build() {
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
  public static ChallengeContributionCreateResponseDto.Builder builder() {
    return new ChallengeContributionCreateResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeContributionCreateResponseDto.Builder toBuilder() {
    ChallengeContributionCreateResponseDto.Builder builder = new ChallengeContributionCreateResponseDto.Builder();
    return builder.copyOf(this);
  }

}

