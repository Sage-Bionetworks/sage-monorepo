package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * EnsemblInfo
 */

@Schema(name = "EnsemblInfo", description = "EnsemblInfo")
@JsonTypeName("EnsemblInfo")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class EnsemblInfoDto {

  private Integer ensemblRelease;

  @Valid
  private List<String> ensemblPossibleReplacements = new ArrayList<>();

  private String ensemblPermalink;

  public EnsemblInfoDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EnsemblInfoDto(Integer ensemblRelease, List<String> ensemblPossibleReplacements, String ensemblPermalink) {
    this.ensemblRelease = ensemblRelease;
    this.ensemblPossibleReplacements = ensemblPossibleReplacements;
    this.ensemblPermalink = ensemblPermalink;
  }

  public EnsemblInfoDto ensemblRelease(Integer ensemblRelease) {
    this.ensemblRelease = ensemblRelease;
    return this;
  }

  /**
   * Get ensemblRelease
   * @return ensemblRelease
  */
  @NotNull 
  @Schema(name = "ensembl_release", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_release")
  public Integer getEnsemblRelease() {
    return ensemblRelease;
  }

  public void setEnsemblRelease(Integer ensemblRelease) {
    this.ensemblRelease = ensemblRelease;
  }

  public EnsemblInfoDto ensemblPossibleReplacements(List<String> ensemblPossibleReplacements) {
    this.ensemblPossibleReplacements = ensemblPossibleReplacements;
    return this;
  }

  public EnsemblInfoDto addEnsemblPossibleReplacementsItem(String ensemblPossibleReplacementsItem) {
    if (this.ensemblPossibleReplacements == null) {
      this.ensemblPossibleReplacements = new ArrayList<>();
    }
    this.ensemblPossibleReplacements.add(ensemblPossibleReplacementsItem);
    return this;
  }

  /**
   * Get ensemblPossibleReplacements
   * @return ensemblPossibleReplacements
  */
  @NotNull 
  @Schema(name = "ensembl_possible_replacements", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_possible_replacements")
  public List<String> getEnsemblPossibleReplacements() {
    return ensemblPossibleReplacements;
  }

  public void setEnsemblPossibleReplacements(List<String> ensemblPossibleReplacements) {
    this.ensemblPossibleReplacements = ensemblPossibleReplacements;
  }

  public EnsemblInfoDto ensemblPermalink(String ensemblPermalink) {
    this.ensemblPermalink = ensemblPermalink;
    return this;
  }

  /**
   * Get ensemblPermalink
   * @return ensemblPermalink
  */
  @NotNull 
  @Schema(name = "ensembl_permalink", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_permalink")
  public String getEnsemblPermalink() {
    return ensemblPermalink;
  }

  public void setEnsemblPermalink(String ensemblPermalink) {
    this.ensemblPermalink = ensemblPermalink;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnsemblInfoDto ensemblInfo = (EnsemblInfoDto) o;
    return Objects.equals(this.ensemblRelease, ensemblInfo.ensemblRelease) &&
        Objects.equals(this.ensemblPossibleReplacements, ensemblInfo.ensemblPossibleReplacements) &&
        Objects.equals(this.ensemblPermalink, ensemblInfo.ensemblPermalink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblRelease, ensemblPossibleReplacements, ensemblPermalink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EnsemblInfoDto {\n");
    sb.append("    ensemblRelease: ").append(toIndentedString(ensemblRelease)).append("\n");
    sb.append("    ensemblPossibleReplacements: ").append(toIndentedString(ensemblPossibleReplacements)).append("\n");
    sb.append("    ensemblPermalink: ").append(toIndentedString(ensemblPermalink)).append("\n");
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

