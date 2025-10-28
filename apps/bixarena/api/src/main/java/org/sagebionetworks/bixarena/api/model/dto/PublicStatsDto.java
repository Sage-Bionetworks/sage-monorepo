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
 * Public statistics about the BixArena platform.
 */

@Schema(name = "PublicStats", description = "Public statistics about the BixArena platform.")
@JsonTypeName("PublicStats")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class PublicStatsDto {

  private Long modelsEvaluated;

  private Long totalBattles;

  private Long totalUsers;

  public PublicStatsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PublicStatsDto(Long modelsEvaluated, Long totalBattles, Long totalUsers) {
    this.modelsEvaluated = modelsEvaluated;
    this.totalBattles = totalBattles;
    this.totalUsers = totalUsers;
  }

  public PublicStatsDto modelsEvaluated(Long modelsEvaluated) {
    this.modelsEvaluated = modelsEvaluated;
    return this;
  }

  /**
   * Total number of unique models that have been evaluated on the platform
   * @return modelsEvaluated
   */
  @NotNull 
  @Schema(name = "modelsEvaluated", example = "42", description = "Total number of unique models that have been evaluated on the platform", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelsEvaluated")
  public Long getModelsEvaluated() {
    return modelsEvaluated;
  }

  public void setModelsEvaluated(Long modelsEvaluated) {
    this.modelsEvaluated = modelsEvaluated;
  }

  public PublicStatsDto totalBattles(Long totalBattles) {
    this.totalBattles = totalBattles;
    return this;
  }

  /**
   * Total number of battles conducted on the platform
   * @return totalBattles
   */
  @NotNull 
  @Schema(name = "totalBattles", example = "1337", description = "Total number of battles conducted on the platform", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalBattles")
  public Long getTotalBattles() {
    return totalBattles;
  }

  public void setTotalBattles(Long totalBattles) {
    this.totalBattles = totalBattles;
  }

  public PublicStatsDto totalUsers(Long totalUsers) {
    this.totalUsers = totalUsers;
    return this;
  }

  /**
   * Total number of registered users on the platform
   * @return totalUsers
   */
  @NotNull 
  @Schema(name = "totalUsers", example = "256", description = "Total number of registered users on the platform", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("totalUsers")
  public Long getTotalUsers() {
    return totalUsers;
  }

  public void setTotalUsers(Long totalUsers) {
    this.totalUsers = totalUsers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PublicStatsDto publicStats = (PublicStatsDto) o;
    return Objects.equals(this.modelsEvaluated, publicStats.modelsEvaluated) &&
        Objects.equals(this.totalBattles, publicStats.totalBattles) &&
        Objects.equals(this.totalUsers, publicStats.totalUsers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modelsEvaluated, totalBattles, totalUsers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PublicStatsDto {\n");
    sb.append("    modelsEvaluated: ").append(toIndentedString(modelsEvaluated)).append("\n");
    sb.append("    totalBattles: ").append(toIndentedString(totalBattles)).append("\n");
    sb.append("    totalUsers: ").append(toIndentedString(totalUsers)).append("\n");
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

    private PublicStatsDto instance;

    public Builder() {
      this(new PublicStatsDto());
    }

    protected Builder(PublicStatsDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(PublicStatsDto value) { 
      this.instance.setModelsEvaluated(value.modelsEvaluated);
      this.instance.setTotalBattles(value.totalBattles);
      this.instance.setTotalUsers(value.totalUsers);
      return this;
    }

    public PublicStatsDto.Builder modelsEvaluated(Long modelsEvaluated) {
      this.instance.modelsEvaluated(modelsEvaluated);
      return this;
    }
    
    public PublicStatsDto.Builder totalBattles(Long totalBattles) {
      this.instance.totalBattles(totalBattles);
      return this;
    }
    
    public PublicStatsDto.Builder totalUsers(Long totalUsers) {
      this.instance.totalUsers(totalUsers);
      return this;
    }
    
    /**
    * returns a built PublicStatsDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public PublicStatsDto build() {
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
  public static PublicStatsDto.Builder builder() {
    return new PublicStatsDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public PublicStatsDto.Builder toBuilder() {
    PublicStatsDto.Builder builder = new PublicStatsDto.Builder();
    return builder.copyOf(this);
  }

}

