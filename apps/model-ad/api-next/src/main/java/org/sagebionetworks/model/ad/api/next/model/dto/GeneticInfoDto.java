package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Genetic information about a model
 */

@Schema(name = "GeneticInfo", description = "Genetic information about a model")
@JsonTypeName("GeneticInfo")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GeneticInfoDto {

  private String modifiedGene;

  private String ensemblGeneId;

  private String allele;

  private String alleleType;

  private BigDecimal mgiAlleleId;

  public GeneticInfoDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneticInfoDto(String modifiedGene, String ensemblGeneId, String allele, String alleleType, BigDecimal mgiAlleleId) {
    this.modifiedGene = modifiedGene;
    this.ensemblGeneId = ensemblGeneId;
    this.allele = allele;
    this.alleleType = alleleType;
    this.mgiAlleleId = mgiAlleleId;
  }

  public GeneticInfoDto modifiedGene(String modifiedGene) {
    this.modifiedGene = modifiedGene;
    return this;
  }

  /**
   * Name of the modified gene
   * @return modifiedGene
   */
  @NotNull 
  @Schema(name = "modified_gene", description = "Name of the modified gene", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modified_gene")
  public String getModifiedGene() {
    return modifiedGene;
  }

  public void setModifiedGene(String modifiedGene) {
    this.modifiedGene = modifiedGene;
  }

  public GeneticInfoDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Ensembl gene ID
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", description = "Ensembl gene ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public GeneticInfoDto allele(String allele) {
    this.allele = allele;
    return this;
  }

  /**
   * Allele symbol
   * @return allele
   */
  @NotNull 
  @Schema(name = "allele", description = "Allele symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("allele")
  public String getAllele() {
    return allele;
  }

  public void setAllele(String allele) {
    this.allele = allele;
  }

  public GeneticInfoDto alleleType(String alleleType) {
    this.alleleType = alleleType;
    return this;
  }

  /**
   * Type of allele
   * @return alleleType
   */
  @NotNull 
  @Schema(name = "allele_type", description = "Type of allele", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("allele_type")
  public String getAlleleType() {
    return alleleType;
  }

  public void setAlleleType(String alleleType) {
    this.alleleType = alleleType;
  }

  public GeneticInfoDto mgiAlleleId(BigDecimal mgiAlleleId) {
    this.mgiAlleleId = mgiAlleleId;
    return this;
  }

  /**
   * MGI allele ID
   * @return mgiAlleleId
   */
  @NotNull @Valid 
  @Schema(name = "mgi_allele_id", description = "MGI allele ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mgi_allele_id")
  public BigDecimal getMgiAlleleId() {
    return mgiAlleleId;
  }

  public void setMgiAlleleId(BigDecimal mgiAlleleId) {
    this.mgiAlleleId = mgiAlleleId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeneticInfoDto geneticInfo = (GeneticInfoDto) o;
    return Objects.equals(this.modifiedGene, geneticInfo.modifiedGene) &&
        Objects.equals(this.ensemblGeneId, geneticInfo.ensemblGeneId) &&
        Objects.equals(this.allele, geneticInfo.allele) &&
        Objects.equals(this.alleleType, geneticInfo.alleleType) &&
        Objects.equals(this.mgiAlleleId, geneticInfo.mgiAlleleId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modifiedGene, ensemblGeneId, allele, alleleType, mgiAlleleId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneticInfoDto {\n");
    sb.append("    modifiedGene: ").append(toIndentedString(modifiedGene)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    allele: ").append(toIndentedString(allele)).append("\n");
    sb.append("    alleleType: ").append(toIndentedString(alleleType)).append("\n");
    sb.append("    mgiAlleleId: ").append(toIndentedString(mgiAlleleId)).append("\n");
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

    private GeneticInfoDto instance;

    public Builder() {
      this(new GeneticInfoDto());
    }

    protected Builder(GeneticInfoDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneticInfoDto value) { 
      this.instance.setModifiedGene(value.modifiedGene);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setAllele(value.allele);
      this.instance.setAlleleType(value.alleleType);
      this.instance.setMgiAlleleId(value.mgiAlleleId);
      return this;
    }

    public GeneticInfoDto.Builder modifiedGene(String modifiedGene) {
      this.instance.modifiedGene(modifiedGene);
      return this;
    }
    
    public GeneticInfoDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public GeneticInfoDto.Builder allele(String allele) {
      this.instance.allele(allele);
      return this;
    }
    
    public GeneticInfoDto.Builder alleleType(String alleleType) {
      this.instance.alleleType(alleleType);
      return this;
    }
    
    public GeneticInfoDto.Builder mgiAlleleId(BigDecimal mgiAlleleId) {
      this.instance.mgiAlleleId(mgiAlleleId);
      return this;
    }
    
    /**
    * returns a built GeneticInfoDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GeneticInfoDto build() {
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
  public static GeneticInfoDto.Builder builder() {
    return new GeneticInfoDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GeneticInfoDto.Builder toBuilder() {
    GeneticInfoDto.Builder builder = new GeneticInfoDto.Builder();
    return builder.copyOf(this);
  }

}

