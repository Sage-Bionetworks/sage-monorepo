package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.model.dto.BioDomainDto;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BioDomains
 */

@Schema(name = "BioDomains", description = "BioDomains")
@JsonTypeName("BioDomains")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class BioDomainsDto {

  private String ensemblGeneId;

  @Valid
  private List<@Valid BioDomainDto> geneBiodomains = new ArrayList<>();

  public BioDomainsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BioDomainsDto(String ensemblGeneId, List<@Valid BioDomainDto> geneBiodomains) {
    this.ensemblGeneId = ensemblGeneId;
    this.geneBiodomains = geneBiodomains;
  }

  public BioDomainsDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * The Ensembl Gene ID.
   * @return ensemblGeneId
  */
  @NotNull 
  @Schema(name = "ensembl_gene_id", description = "The Ensembl Gene ID.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public BioDomainsDto geneBiodomains(List<@Valid BioDomainDto> geneBiodomains) {
    this.geneBiodomains = geneBiodomains;
    return this;
  }

  public BioDomainsDto addGeneBiodomainsItem(BioDomainDto geneBiodomainsItem) {
    if (this.geneBiodomains == null) {
      this.geneBiodomains = new ArrayList<>();
    }
    this.geneBiodomains.add(geneBiodomainsItem);
    return this;
  }

  /**
   * A list of gene biodomains.
   * @return geneBiodomains
  */
  @NotNull @Valid 
  @Schema(name = "gene_biodomains", description = "A list of gene biodomains.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("gene_biodomains")
  public List<@Valid BioDomainDto> getGeneBiodomains() {
    return geneBiodomains;
  }

  public void setGeneBiodomains(List<@Valid BioDomainDto> geneBiodomains) {
    this.geneBiodomains = geneBiodomains;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BioDomainsDto bioDomains = (BioDomainsDto) o;
    return Objects.equals(this.ensemblGeneId, bioDomains.ensemblGeneId) &&
        Objects.equals(this.geneBiodomains, bioDomains.geneBiodomains);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, geneBiodomains);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BioDomainsDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    geneBiodomains: ").append(toIndentedString(geneBiodomains)).append("\n");
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

