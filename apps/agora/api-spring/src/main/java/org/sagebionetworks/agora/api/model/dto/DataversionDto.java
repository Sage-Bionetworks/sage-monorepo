package org.sagebionetworks.agora.api.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Synapse data version
 */

@Schema(name = "Dataversion", description = "Synapse data version")
@JsonTypeName("Dataversion")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
@lombok.Builder
public class DataversionDto {

  private String dataFile;

  private String dataVersion;

  private String teamImagesId;

  public DataversionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DataversionDto(String dataFile, String dataVersion, String teamImagesId) {
    this.dataFile = dataFile;
    this.dataVersion = dataVersion;
    this.teamImagesId = teamImagesId;
  }

  public DataversionDto dataFile(String dataFile) {
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

  public DataversionDto dataVersion(String dataVersion) {
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

  public DataversionDto teamImagesId(String teamImagesId) {
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
    DataversionDto dataversion = (DataversionDto) o;
    return (
      Objects.equals(this.dataFile, dataversion.dataFile) &&
      Objects.equals(this.dataVersion, dataversion.dataVersion) &&
      Objects.equals(this.teamImagesId, dataversion.teamImagesId)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataFile, dataVersion, teamImagesId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataversionDto {\n");
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
}
