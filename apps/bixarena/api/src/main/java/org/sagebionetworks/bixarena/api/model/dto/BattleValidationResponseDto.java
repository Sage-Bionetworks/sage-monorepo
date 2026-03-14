package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BattleValidationResponseDto
 */

@JsonTypeName("BattleValidationResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleValidationResponseDto {

  private UUID id;

  private UUID battleId;

  private String method;

  private Float confidence;

  private Boolean isBiomedical;

  private @Nullable UUID validatedBy = null;

  private @Nullable String reason = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public BattleValidationResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleValidationResponseDto(UUID id, UUID battleId, String method, Float confidence, Boolean isBiomedical, OffsetDateTime createdAt) {
    this.id = id;
    this.battleId = battleId;
    this.method = method;
    this.confidence = confidence;
    this.isBiomedical = isBiomedical;
    this.createdAt = createdAt;
  }

  public BattleValidationResponseDto id(UUID id) {
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

  public BattleValidationResponseDto battleId(UUID battleId) {
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

  public BattleValidationResponseDto method(String method) {
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

  public BattleValidationResponseDto confidence(Float confidence) {
    this.confidence = confidence;
    return this;
  }

  /**
   * Get confidence
   * minimum: 0
   * maximum: 1
   * @return confidence
   */
  @NotNull @DecimalMin("0") @DecimalMax("1") 
  @Schema(name = "confidence", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("confidence")
  public Float getConfidence() {
    return confidence;
  }

  public void setConfidence(Float confidence) {
    this.confidence = confidence;
  }

  public BattleValidationResponseDto isBiomedical(Boolean isBiomedical) {
    this.isBiomedical = isBiomedical;
    return this;
  }

  /**
   * Get isBiomedical
   * @return isBiomedical
   */
  @NotNull 
  @Schema(name = "isBiomedical", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isBiomedical")
  public Boolean getIsBiomedical() {
    return isBiomedical;
  }

  public void setIsBiomedical(Boolean isBiomedical) {
    this.isBiomedical = isBiomedical;
  }

  public BattleValidationResponseDto validatedBy(@Nullable UUID validatedBy) {
    this.validatedBy = validatedBy;
    return this;
  }

  /**
   * User ID of the validator (null for automated validations)
   * @return validatedBy
   */
  @Valid 
  @Schema(name = "validatedBy", description = "User ID of the validator (null for automated validations)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("validatedBy")
  public @Nullable UUID getValidatedBy() {
    return validatedBy;
  }

  public void setValidatedBy(@Nullable UUID validatedBy) {
    this.validatedBy = validatedBy;
  }

  public BattleValidationResponseDto reason(@Nullable String reason) {
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

  public BattleValidationResponseDto createdAt(OffsetDateTime createdAt) {
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
    BattleValidationResponseDto battleValidationResponse = (BattleValidationResponseDto) o;
    return Objects.equals(this.id, battleValidationResponse.id) &&
        Objects.equals(this.battleId, battleValidationResponse.battleId) &&
        Objects.equals(this.method, battleValidationResponse.method) &&
        Objects.equals(this.confidence, battleValidationResponse.confidence) &&
        Objects.equals(this.isBiomedical, battleValidationResponse.isBiomedical) &&
        Objects.equals(this.validatedBy, battleValidationResponse.validatedBy) &&
        Objects.equals(this.reason, battleValidationResponse.reason) &&
        Objects.equals(this.createdAt, battleValidationResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, battleId, method, confidence, isBiomedical, validatedBy, reason, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleValidationResponseDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    battleId: ").append(toIndentedString(battleId)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    confidence: ").append(toIndentedString(confidence)).append("\n");
    sb.append("    isBiomedical: ").append(toIndentedString(isBiomedical)).append("\n");
    sb.append("    validatedBy: ").append(toIndentedString(validatedBy)).append("\n");
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

    private BattleValidationResponseDto instance;

    public Builder() {
      this(new BattleValidationResponseDto());
    }

    protected Builder(BattleValidationResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleValidationResponseDto value) { 
      this.instance.setId(value.id);
      this.instance.setBattleId(value.battleId);
      this.instance.setMethod(value.method);
      this.instance.setConfidence(value.confidence);
      this.instance.setIsBiomedical(value.isBiomedical);
      this.instance.setValidatedBy(value.validatedBy);
      this.instance.setReason(value.reason);
      this.instance.setCreatedAt(value.createdAt);
      return this;
    }

    public BattleValidationResponseDto.Builder id(UUID id) {
      this.instance.id(id);
      return this;
    }
    
    public BattleValidationResponseDto.Builder battleId(UUID battleId) {
      this.instance.battleId(battleId);
      return this;
    }
    
    public BattleValidationResponseDto.Builder method(String method) {
      this.instance.method(method);
      return this;
    }
    
    public BattleValidationResponseDto.Builder confidence(Float confidence) {
      this.instance.confidence(confidence);
      return this;
    }
    
    public BattleValidationResponseDto.Builder isBiomedical(Boolean isBiomedical) {
      this.instance.isBiomedical(isBiomedical);
      return this;
    }
    
    public BattleValidationResponseDto.Builder validatedBy(UUID validatedBy) {
      this.instance.validatedBy(validatedBy);
      return this;
    }
    
    public BattleValidationResponseDto.Builder reason(String reason) {
      this.instance.reason(reason);
      return this;
    }
    
    public BattleValidationResponseDto.Builder createdAt(OffsetDateTime createdAt) {
      this.instance.createdAt(createdAt);
      return this;
    }
    
    /**
    * returns a built BattleValidationResponseDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleValidationResponseDto build() {
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
  public static BattleValidationResponseDto.Builder builder() {
    return new BattleValidationResponseDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleValidationResponseDto.Builder toBuilder() {
    BattleValidationResponseDto.Builder builder = new BattleValidationResponseDto.Builder();
    return builder.copyOf(this);
  }

}

