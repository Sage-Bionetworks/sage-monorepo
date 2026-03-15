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
 * Request to manually validate or invalidate a battle.
 */

@Schema(name = "BattleValidationCreateRequest", description = "Request to manually validate or invalidate a battle.")
@JsonTypeName("BattleValidationCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleValidationCreateRequestDto {

  private Boolean isBiomedical;

  private @Nullable String reason = null;

  public BattleValidationCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleValidationCreateRequestDto(Boolean isBiomedical) {
    this.isBiomedical = isBiomedical;
  }

  public BattleValidationCreateRequestDto isBiomedical(Boolean isBiomedical) {
    this.isBiomedical = isBiomedical;
    return this;
  }

  /**
   * Whether the admin considers this battle biomedically related
   * @return isBiomedical
   */
  @NotNull 
  @Schema(name = "isBiomedical", description = "Whether the admin considers this battle biomedically related", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isBiomedical")
  public Boolean getIsBiomedical() {
    return isBiomedical;
  }

  public void setIsBiomedical(Boolean isBiomedical) {
    this.isBiomedical = isBiomedical;
  }

  public BattleValidationCreateRequestDto reason(@Nullable String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Optional reason for the validation decision
   * @return reason
   */
  @Size(max = 1000) 
  @Schema(name = "reason", description = "Optional reason for the validation decision", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public @Nullable String getReason() {
    return reason;
  }

  public void setReason(@Nullable String reason) {
    this.reason = reason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleValidationCreateRequestDto battleValidationCreateRequest = (BattleValidationCreateRequestDto) o;
    return Objects.equals(this.isBiomedical, battleValidationCreateRequest.isBiomedical) &&
        Objects.equals(this.reason, battleValidationCreateRequest.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isBiomedical, reason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleValidationCreateRequestDto {\n");
    sb.append("    isBiomedical: ").append(toIndentedString(isBiomedical)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
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

    private BattleValidationCreateRequestDto instance;

    public Builder() {
      this(new BattleValidationCreateRequestDto());
    }

    protected Builder(BattleValidationCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleValidationCreateRequestDto value) { 
      this.instance.setIsBiomedical(value.isBiomedical);
      this.instance.setReason(value.reason);
      return this;
    }

    public BattleValidationCreateRequestDto.Builder isBiomedical(Boolean isBiomedical) {
      this.instance.isBiomedical(isBiomedical);
      return this;
    }
    
    public BattleValidationCreateRequestDto.Builder reason(String reason) {
      this.instance.reason(reason);
      return this;
    }
    
    /**
    * returns a built BattleValidationCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleValidationCreateRequestDto build() {
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
  public static BattleValidationCreateRequestDto.Builder builder() {
    return new BattleValidationCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleValidationCreateRequestDto.Builder toBuilder() {
    BattleValidationCreateRequestDto.Builder builder = new BattleValidationCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

