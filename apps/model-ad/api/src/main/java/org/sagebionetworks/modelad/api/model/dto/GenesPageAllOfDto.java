package org.sagebionetworks.modelad.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** GenesPageAllOfDto */
@JsonTypeName("GenesPage_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class GenesPageAllOfDto {

  @JsonProperty("genes")
  @Valid
  private List<GeneDto> genes = new ArrayList<>();

  public GenesPageAllOfDto genes(List<GeneDto> genes) {
    this.genes = genes;
    return this;
  }

  public GenesPageAllOfDto addGenesItem(GeneDto genesItem) {
    if (this.genes == null) {
      this.genes = new ArrayList<>();
    }
    this.genes.add(genesItem);
    return this;
  }

  /**
   * A list of genes.
   *
   * @return genes
   */
  @NotNull
  @Valid
  @Schema(name = "genes", description = "A list of genes.", required = true)
  public List<GeneDto> getGenes() {
    return genes;
  }

  public void setGenes(List<GeneDto> genes) {
    this.genes = genes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenesPageAllOfDto genesPageAllOf = (GenesPageAllOfDto) o;
    return Objects.equals(this.genes, genesPageAllOf.genes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(genes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenesPageAllOfDto {\n");
    sb.append("    genes: ").append(toIndentedString(genes)).append("\n");
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
