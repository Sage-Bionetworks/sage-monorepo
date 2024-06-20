package org.sagebionetworks.modelad.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** A gene */
@Schema(name = "Gene", description = "A gene")
@JsonTypeName("Gene")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// @lombok.NoArgsConstructor
@lombok.Builder
public class GeneDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  public GeneDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the gene.
   *
   * @return id
   */
  @NotNull
  @Schema(
      name = "id",
      example = "1",
      description = "The unique identifier of the gene.",
      required = true)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GeneDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the gene.
   *
   * @return name
   */
  @NotNull
  @Size(min = 3, max = 255)
  @Schema(name = "name", description = "The name of the gene.", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GeneDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the gene.
   *
   * @return description
   */
  @NotNull
  @Size(min = 0, max = 1000)
  @Schema(
      name = "description",
      example = "This is an example description of the gene.",
      description = "The description of the gene.",
      required = true)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeneDto gene = (GeneDto) o;
    return Objects.equals(this.id, gene.id)
        && Objects.equals(this.name, gene.name)
        && Objects.equals(this.description, gene.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
