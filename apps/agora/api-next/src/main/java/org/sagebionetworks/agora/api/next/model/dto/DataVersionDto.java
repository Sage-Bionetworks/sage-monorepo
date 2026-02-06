package org.sagebionetworks.agora.api.next.model.dto;

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
 * Synapse data version
 */

@Schema(name = "DataVersion", description = "Synapse data version")
@JsonTypeName("DataVersion")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class DataVersionDto {

  private String dataFile;

  private String dataVersion;

  private String teamImagesId;

  public DataVersionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DataVersionDto(String dataFile, String dataVersion, String teamImagesId) {
    this.dataFile = dataFile;
    this.dataVersion = dataVersion;
    this.teamImagesId = teamImagesId;
  }

  public DataVersionDto dataFile(String dataFile) {
    this.dataFile = dataFile;
    return this;
  }

  /**
   * Get dataFile
   * @return dataFile
   */
  @NotNull 
  @Schema(name = "data_file", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data_file")
  public String getDataFile() {
    return dataFile;
  }

  public void setDataFile(String dataFile) {
    this.dataFile = dataFile;
  }

  public DataVersionDto dataVersion(String dataVersion) {
    this.dataVersion = dataVersion;
    return this;
  }

  /**
   * Get dataVersion
   * @return dataVersion
   */
  @NotNull 
  @Schema(name = "data_version", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data_version")
  public String getDataVersion() {
    return dataVersion;
  }

  public void setDataVersion(String dataVersion) {
    this.dataVersion = dataVersion;
  }

  public DataVersionDto teamImagesId(String teamImagesId) {
    this.teamImagesId = teamImagesId;
    return this;
  }

  /**
   * Get teamImagesId
   * @return teamImagesId
   */
  @NotNull 
  @Schema(name = "team_images_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("team_images_id")
  public String getTeamImagesId() {
    return teamImagesId;
  }

  public void setTeamImagesId(String teamImagesId) {
    this.teamImagesId = teamImagesId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataVersionDto dataVersion = (DataVersionDto) o;
    return Objects.equals(this.dataFile, dataVersion.dataFile) &&
        Objects.equals(this.dataVersion, dataVersion.dataVersion) &&
        Objects.equals(this.teamImagesId, dataVersion.teamImagesId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataFile, dataVersion, teamImagesId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataVersionDto {\n");
    sb.append("    dataFile: ").append(toIndentedString(dataFile)).append("\n");
    sb.append("    dataVersion: ").append(toIndentedString(dataVersion)).append("\n");
    sb.append("    teamImagesId: ").append(toIndentedString(teamImagesId)).append("\n");
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

    private DataVersionDto instance;

    public Builder() {
      this(new DataVersionDto());
    }

    protected Builder(DataVersionDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DataVersionDto value) { 
      this.instance.setDataFile(value.dataFile);
      this.instance.setDataVersion(value.dataVersion);
      this.instance.setTeamImagesId(value.teamImagesId);
      return this;
    }

    public DataVersionDto.Builder dataFile(String dataFile) {
      this.instance.dataFile(dataFile);
      return this;
    }
    
    public DataVersionDto.Builder dataVersion(String dataVersion) {
      this.instance.dataVersion(dataVersion);
      return this;
    }
    
    public DataVersionDto.Builder teamImagesId(String teamImagesId) {
      this.instance.teamImagesId(teamImagesId);
      return this;
    }
    
    /**
    * returns a built DataVersionDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DataVersionDto build() {
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
  public static DataVersionDto.Builder builder() {
    return new DataVersionDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DataVersionDto.Builder toBuilder() {
    DataVersionDto.Builder builder = new DataVersionDto.Builder();
    return builder.copyOf(this);
  }

}

