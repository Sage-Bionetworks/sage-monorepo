package org.sagebionetworks.qtl.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import org.sagebionetworks.qtl.api.model.dto.EffectDirectionDto;
import org.sagebionetworks.qtl.api.model.dto.GenomicLocationDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * eQTL
 */

@Schema(name = "Eqtl", description = "eQTL")
@JsonTypeName("Eqtl")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class EqtlDto {

  private String compositeId;

  private String variant;

  private GenomicLocationDto variantLocation;

  private EffectDirectionDto effectDirection;

  private String geneSymbol;

  private String ensemblGeneId;

  private GenomicLocationDto geneLocation;

  private BigDecimal beta;

  private String adjPVal;

  private BigDecimal adjPValNumeric;

  private BigDecimal maf;

  private String minorAllele;

  private String referenceAllele;

  private String cellTypeId;

  private String cellClass;

  private String cellClassShort;

  private String cellSubclass;

  private String cellSubclassShort;

  public EqtlDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EqtlDto(String compositeId, String variant, GenomicLocationDto variantLocation, EffectDirectionDto effectDirection, String geneSymbol, String ensemblGeneId, GenomicLocationDto geneLocation, BigDecimal beta, String adjPVal, BigDecimal adjPValNumeric, BigDecimal maf, String minorAllele, String referenceAllele, String cellTypeId, String cellClass, String cellClassShort, String cellSubclass, String cellSubclassShort) {
    this.compositeId = compositeId;
    this.variant = variant;
    this.variantLocation = variantLocation;
    this.effectDirection = effectDirection;
    this.geneSymbol = geneSymbol;
    this.ensemblGeneId = ensemblGeneId;
    this.geneLocation = geneLocation;
    this.beta = beta;
    this.adjPVal = adjPVal;
    this.adjPValNumeric = adjPValNumeric;
    this.maf = maf;
    this.minorAllele = minorAllele;
    this.referenceAllele = referenceAllele;
    this.cellTypeId = cellTypeId;
    this.cellClass = cellClass;
    this.cellClassShort = cellClassShort;
    this.cellSubclass = cellSubclass;
    this.cellSubclassShort = cellSubclassShort;
  }

  public EqtlDto compositeId(String compositeId) {
    this.compositeId = compositeId;
    return this;
  }

  /**
   * Unique identifier for the eQTL object
   * @return compositeId
   */
  @NotNull 
  @Schema(name = "composite_id", example = "rs12655812~ENSG00000250891~astro", description = "Unique identifier for the eQTL object", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("composite_id")
  public String getCompositeId() {
    return compositeId;
  }

  public void setCompositeId(String compositeId) {
    this.compositeId = compositeId;
  }

  public EqtlDto variant(String variant) {
    this.variant = variant;
    return this;
  }

  /**
   * Variant ID
   * @return variant
   */
  @NotNull 
  @Schema(name = "variant", example = "rs12655812", description = "Variant ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("variant")
  public String getVariant() {
    return variant;
  }

  public void setVariant(String variant) {
    this.variant = variant;
  }

  public EqtlDto variantLocation(GenomicLocationDto variantLocation) {
    this.variantLocation = variantLocation;
    return this;
  }

  /**
   * Get variantLocation
   * @return variantLocation
   */
  @NotNull @Valid 
  @Schema(name = "variant_location", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("variant_location")
  public GenomicLocationDto getVariantLocation() {
    return variantLocation;
  }

  public void setVariantLocation(GenomicLocationDto variantLocation) {
    this.variantLocation = variantLocation;
  }

  public EqtlDto effectDirection(EffectDirectionDto effectDirection) {
    this.effectDirection = effectDirection;
    return this;
  }

  /**
   * Get effectDirection
   * @return effectDirection
   */
  @NotNull @Valid 
  @Schema(name = "effect_direction", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("effect_direction")
  public EffectDirectionDto getEffectDirection() {
    return effectDirection;
  }

  public void setEffectDirection(EffectDirectionDto effectDirection) {
    this.effectDirection = effectDirection;
  }

  public EqtlDto geneSymbol(String geneSymbol) {
    this.geneSymbol = geneSymbol;
    return this;
  }

  /**
   * Gene symbol
   * @return geneSymbol
   */
  @NotNull 
  @Schema(name = "gene_symbol", example = "LINC02208", description = "Gene symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("gene_symbol")
  public String getGeneSymbol() {
    return geneSymbol;
  }

  public void setGeneSymbol(String geneSymbol) {
    this.geneSymbol = geneSymbol;
  }

  public EqtlDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Ensembl gene ID
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", example = "ENSG00000250891", description = "Ensembl gene ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public EqtlDto geneLocation(GenomicLocationDto geneLocation) {
    this.geneLocation = geneLocation;
    return this;
  }

  /**
   * Get geneLocation
   * @return geneLocation
   */
  @NotNull @Valid 
  @Schema(name = "gene_location", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("gene_location")
  public GenomicLocationDto getGeneLocation() {
    return geneLocation;
  }

  public void setGeneLocation(GenomicLocationDto geneLocation) {
    this.geneLocation = geneLocation;
  }

  public EqtlDto beta(BigDecimal beta) {
    this.beta = beta;
    return this;
  }

  /**
   * Effect size
   * @return beta
   */
  @NotNull @Valid 
  @Schema(name = "beta", example = "0.608491", description = "Effect size", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("beta")
  public BigDecimal getBeta() {
    return beta;
  }

  public void setBeta(BigDecimal beta) {
    this.beta = beta;
  }

  public EqtlDto adjPVal(String adjPVal) {
    this.adjPVal = adjPVal;
    return this;
  }

  /**
   * Adjusted p-value, preformatted for display
   * @return adjPVal
   */
  @NotNull 
  @Schema(name = "adj_p_val", example = "2.61E-162", description = "Adjusted p-value, preformatted for display", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adj_p_val")
  public String getAdjPVal() {
    return adjPVal;
  }

  public void setAdjPVal(String adjPVal) {
    this.adjPVal = adjPVal;
  }

  public EqtlDto adjPValNumeric(BigDecimal adjPValNumeric) {
    this.adjPValNumeric = adjPValNumeric;
    return this;
  }

  /**
   * Base-10 logarithm of the adjusted p-value
   * @return adjPValNumeric
   */
  @NotNull @Valid 
  @Schema(name = "adj_p_val_numeric", example = "-161.583901", description = "Base-10 logarithm of the adjusted p-value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adj_p_val_numeric")
  public BigDecimal getAdjPValNumeric() {
    return adjPValNumeric;
  }

  public void setAdjPValNumeric(BigDecimal adjPValNumeric) {
    this.adjPValNumeric = adjPValNumeric;
  }

  public EqtlDto maf(BigDecimal maf) {
    this.maf = maf;
    return this;
  }

  /**
   * Minor allele frequency
   * @return maf
   */
  @NotNull @Valid 
  @Schema(name = "maf", example = "0.1418", description = "Minor allele frequency", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("maf")
  public BigDecimal getMaf() {
    return maf;
  }

  public void setMaf(BigDecimal maf) {
    this.maf = maf;
  }

  public EqtlDto minorAllele(String minorAllele) {
    this.minorAllele = minorAllele;
    return this;
  }

  /**
   * Minor allele
   * @return minorAllele
   */
  @NotNull 
  @Schema(name = "minor_allele", example = "C", description = "Minor allele", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("minor_allele")
  public String getMinorAllele() {
    return minorAllele;
  }

  public void setMinorAllele(String minorAllele) {
    this.minorAllele = minorAllele;
  }

  public EqtlDto referenceAllele(String referenceAllele) {
    this.referenceAllele = referenceAllele;
    return this;
  }

  /**
   * Reference allele
   * @return referenceAllele
   */
  @NotNull 
  @Schema(name = "reference_allele", example = "T", description = "Reference allele", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference_allele")
  public String getReferenceAllele() {
    return referenceAllele;
  }

  public void setReferenceAllele(String referenceAllele) {
    this.referenceAllele = referenceAllele;
  }

  public EqtlDto cellTypeId(String cellTypeId) {
    this.cellTypeId = cellTypeId;
    return this;
  }

  /**
   * Cell type ID
   * @return cellTypeId
   */
  @NotNull 
  @Schema(name = "cell_type_id", example = "astro", description = "Cell type ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_type_id")
  public String getCellTypeId() {
    return cellTypeId;
  }

  public void setCellTypeId(String cellTypeId) {
    this.cellTypeId = cellTypeId;
  }

  public EqtlDto cellClass(String cellClass) {
    this.cellClass = cellClass;
    return this;
  }

  /**
   * Cell class
   * @return cellClass
   */
  @NotNull 
  @Schema(name = "cell_class", example = "Astrocyte", description = "Cell class", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_class")
  public String getCellClass() {
    return cellClass;
  }

  public void setCellClass(String cellClass) {
    this.cellClass = cellClass;
  }

  public EqtlDto cellClassShort(String cellClassShort) {
    this.cellClassShort = cellClassShort;
    return this;
  }

  /**
   * Abbreviated cell class
   * @return cellClassShort
   */
  @NotNull 
  @Schema(name = "cell_class_short", example = "Astro", description = "Abbreviated cell class", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_class_short")
  public String getCellClassShort() {
    return cellClassShort;
  }

  public void setCellClassShort(String cellClassShort) {
    this.cellClassShort = cellClassShort;
  }

  public EqtlDto cellSubclass(String cellSubclass) {
    this.cellSubclass = cellSubclass;
    return this;
  }

  /**
   * Cell subclass
   * @return cellSubclass
   */
  @NotNull 
  @Schema(name = "cell_subclass", example = "Astrocyte", description = "Cell subclass", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_subclass")
  public String getCellSubclass() {
    return cellSubclass;
  }

  public void setCellSubclass(String cellSubclass) {
    this.cellSubclass = cellSubclass;
  }

  public EqtlDto cellSubclassShort(String cellSubclassShort) {
    this.cellSubclassShort = cellSubclassShort;
    return this;
  }

  /**
   * Abbreviated cell subclass
   * @return cellSubclassShort
   */
  @NotNull 
  @Schema(name = "cell_subclass_short", example = "Astro", description = "Abbreviated cell subclass", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_subclass_short")
  public String getCellSubclassShort() {
    return cellSubclassShort;
  }

  public void setCellSubclassShort(String cellSubclassShort) {
    this.cellSubclassShort = cellSubclassShort;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EqtlDto eqtl = (EqtlDto) o;
    return Objects.equals(this.compositeId, eqtl.compositeId) &&
        Objects.equals(this.variant, eqtl.variant) &&
        Objects.equals(this.variantLocation, eqtl.variantLocation) &&
        Objects.equals(this.effectDirection, eqtl.effectDirection) &&
        Objects.equals(this.geneSymbol, eqtl.geneSymbol) &&
        Objects.equals(this.ensemblGeneId, eqtl.ensemblGeneId) &&
        Objects.equals(this.geneLocation, eqtl.geneLocation) &&
        Objects.equals(this.beta, eqtl.beta) &&
        Objects.equals(this.adjPVal, eqtl.adjPVal) &&
        Objects.equals(this.adjPValNumeric, eqtl.adjPValNumeric) &&
        Objects.equals(this.maf, eqtl.maf) &&
        Objects.equals(this.minorAllele, eqtl.minorAllele) &&
        Objects.equals(this.referenceAllele, eqtl.referenceAllele) &&
        Objects.equals(this.cellTypeId, eqtl.cellTypeId) &&
        Objects.equals(this.cellClass, eqtl.cellClass) &&
        Objects.equals(this.cellClassShort, eqtl.cellClassShort) &&
        Objects.equals(this.cellSubclass, eqtl.cellSubclass) &&
        Objects.equals(this.cellSubclassShort, eqtl.cellSubclassShort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(compositeId, variant, variantLocation, effectDirection, geneSymbol, ensemblGeneId, geneLocation, beta, adjPVal, adjPValNumeric, maf, minorAllele, referenceAllele, cellTypeId, cellClass, cellClassShort, cellSubclass, cellSubclassShort);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EqtlDto {\n");
    sb.append("    compositeId: ").append(toIndentedString(compositeId)).append("\n");
    sb.append("    variant: ").append(toIndentedString(variant)).append("\n");
    sb.append("    variantLocation: ").append(toIndentedString(variantLocation)).append("\n");
    sb.append("    effectDirection: ").append(toIndentedString(effectDirection)).append("\n");
    sb.append("    geneSymbol: ").append(toIndentedString(geneSymbol)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    geneLocation: ").append(toIndentedString(geneLocation)).append("\n");
    sb.append("    beta: ").append(toIndentedString(beta)).append("\n");
    sb.append("    adjPVal: ").append(toIndentedString(adjPVal)).append("\n");
    sb.append("    adjPValNumeric: ").append(toIndentedString(adjPValNumeric)).append("\n");
    sb.append("    maf: ").append(toIndentedString(maf)).append("\n");
    sb.append("    minorAllele: ").append(toIndentedString(minorAllele)).append("\n");
    sb.append("    referenceAllele: ").append(toIndentedString(referenceAllele)).append("\n");
    sb.append("    cellTypeId: ").append(toIndentedString(cellTypeId)).append("\n");
    sb.append("    cellClass: ").append(toIndentedString(cellClass)).append("\n");
    sb.append("    cellClassShort: ").append(toIndentedString(cellClassShort)).append("\n");
    sb.append("    cellSubclass: ").append(toIndentedString(cellSubclass)).append("\n");
    sb.append("    cellSubclassShort: ").append(toIndentedString(cellSubclassShort)).append("\n");
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

    private EqtlDto instance;

    public Builder() {
      this(new EqtlDto());
    }

    protected Builder(EqtlDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EqtlDto value) { 
      this.instance.setCompositeId(value.compositeId);
      this.instance.setVariant(value.variant);
      this.instance.setVariantLocation(value.variantLocation);
      this.instance.setEffectDirection(value.effectDirection);
      this.instance.setGeneSymbol(value.geneSymbol);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setGeneLocation(value.geneLocation);
      this.instance.setBeta(value.beta);
      this.instance.setAdjPVal(value.adjPVal);
      this.instance.setAdjPValNumeric(value.adjPValNumeric);
      this.instance.setMaf(value.maf);
      this.instance.setMinorAllele(value.minorAllele);
      this.instance.setReferenceAllele(value.referenceAllele);
      this.instance.setCellTypeId(value.cellTypeId);
      this.instance.setCellClass(value.cellClass);
      this.instance.setCellClassShort(value.cellClassShort);
      this.instance.setCellSubclass(value.cellSubclass);
      this.instance.setCellSubclassShort(value.cellSubclassShort);
      return this;
    }

    public EqtlDto.Builder compositeId(String compositeId) {
      this.instance.compositeId(compositeId);
      return this;
    }
    
    public EqtlDto.Builder variant(String variant) {
      this.instance.variant(variant);
      return this;
    }
    
    public EqtlDto.Builder variantLocation(GenomicLocationDto variantLocation) {
      this.instance.variantLocation(variantLocation);
      return this;
    }
    
    public EqtlDto.Builder effectDirection(EffectDirectionDto effectDirection) {
      this.instance.effectDirection(effectDirection);
      return this;
    }
    
    public EqtlDto.Builder geneSymbol(String geneSymbol) {
      this.instance.geneSymbol(geneSymbol);
      return this;
    }
    
    public EqtlDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public EqtlDto.Builder geneLocation(GenomicLocationDto geneLocation) {
      this.instance.geneLocation(geneLocation);
      return this;
    }
    
    public EqtlDto.Builder beta(BigDecimal beta) {
      this.instance.beta(beta);
      return this;
    }
    
    public EqtlDto.Builder adjPVal(String adjPVal) {
      this.instance.adjPVal(adjPVal);
      return this;
    }
    
    public EqtlDto.Builder adjPValNumeric(BigDecimal adjPValNumeric) {
      this.instance.adjPValNumeric(adjPValNumeric);
      return this;
    }
    
    public EqtlDto.Builder maf(BigDecimal maf) {
      this.instance.maf(maf);
      return this;
    }
    
    public EqtlDto.Builder minorAllele(String minorAllele) {
      this.instance.minorAllele(minorAllele);
      return this;
    }
    
    public EqtlDto.Builder referenceAllele(String referenceAllele) {
      this.instance.referenceAllele(referenceAllele);
      return this;
    }
    
    public EqtlDto.Builder cellTypeId(String cellTypeId) {
      this.instance.cellTypeId(cellTypeId);
      return this;
    }
    
    public EqtlDto.Builder cellClass(String cellClass) {
      this.instance.cellClass(cellClass);
      return this;
    }
    
    public EqtlDto.Builder cellClassShort(String cellClassShort) {
      this.instance.cellClassShort(cellClassShort);
      return this;
    }
    
    public EqtlDto.Builder cellSubclass(String cellSubclass) {
      this.instance.cellSubclass(cellSubclass);
      return this;
    }
    
    public EqtlDto.Builder cellSubclassShort(String cellSubclassShort) {
      this.instance.cellSubclassShort(cellSubclassShort);
      return this;
    }
    
    /**
    * returns a built EqtlDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EqtlDto build() {
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
  public static EqtlDto.Builder builder() {
    return new EqtlDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EqtlDto.Builder toBuilder() {
    EqtlDto.Builder builder = new EqtlDto.Builder();
    return builder.copyOf(this);
  }

}

