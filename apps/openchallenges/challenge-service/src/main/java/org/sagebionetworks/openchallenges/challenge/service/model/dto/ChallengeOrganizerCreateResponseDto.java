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
 * The unique identifier of the challenge organizer created
 */

@Schema(name = "ChallengeOrganizerCreateResponse", description = "The unique identifier of the challenge organizer created")
@JsonTypeName("ChallengeOrganizerCreateResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class ChallengeOrganizerCreateResponseDto {

  private String id;

  public ChallengeOrganizerCreateResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChallengeOrganizerCreateResponseDto(String id) {
    this.id = id;
  }

  public ChallengeOrganizerCreateResponseDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of a challenge organizer
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "507f1f77bcf86cd799439011", description = "The unique identifier of a challenge organizer", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
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
    ChallengeOrganizerCreateResponseDto challengeOrganizerCreateResponse = (ChallengeOrganizerCreateResponseDto) o;
    return Objects.equals(this.id, challengeOrganizerCreateResponse.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeOrganizerCreateResponseDto {\n");
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

    private ChallengeOrganizerCreateResponseDto instance;

    public Builder() {
      this(new ChallengeOrganizerCreateResponseDto());
    }

    protected Builder(ChallengeOrganizerCreateResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ChallengeOrganizerCreateResponseDto value) { 
      this.instance.setId(value.id);
      return this;
    }

    public ChallengeOrganizerCreateResponseDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    /**
    * returns a built ChallengeOrganizerCreateResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ChallengeOrganizerCreateResponseDto build() {
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
  public static ChallengeOrganizerCreateResponseDto.Builder builder() {
    return new ChallengeOrganizerCreateResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ChallengeOrganizerCreateResponseDto.Builder toBuilder() {
    ChallengeOrganizerCreateResponseDto.Builder builder = new ChallengeOrganizerCreateResponseDto.Builder();
    return builder.copyOf(this);
  }

}

