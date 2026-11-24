package org.sagebionetworks.qtl.api.model.dto;

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
 * Identifies a single eQTL by its variant, gene and cell type.
 */

@Schema(name = "EqtlPhenotypeQuery", description = "Identifies a single eQTL by its variant, gene and cell type.")
@JsonTypeName("EqtlPhenotypeQuery")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class EqtlPhenotypeQueryDto {

  private String variant;

  private String ensemblGeneId;

  private String cellTypeId;

  public EqtlPhenotypeQueryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EqtlPhenotypeQueryDto(String variant, String ensemblGeneId, String cellTypeId) {
    this.variant = variant;
    this.ensemblGeneId = ensemblGeneId;
    this.cellTypeId = cellTypeId;
  }

  public EqtlPhenotypeQueryDto variant(String variant) {
    this.variant = variant;
    return this;
  }

  /**
   * Variant ID
   * @return variant
   */
  @NotNull 
  @Schema(name = "variant", example = "rs2120461", description = "Variant ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("variant")
  public String getVariant() {
    return variant;
  }

  public void setVariant(String variant) {
    this.variant = variant;
  }

  public EqtlPhenotypeQueryDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Ensembl gene ID
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensemblGeneId", example = "ENSG00000142599", description = "Ensembl gene ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensemblGeneId")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public EqtlPhenotypeQueryDto cellTypeId(String cellTypeId) {
    this.cellTypeId = cellTypeId;
    return this;
  }

  /**
   * Cell type ID of the eQTL's cell subclass
   * @return cellTypeId
   */
  @NotNull 
  @Schema(name = "cellTypeId", example = "astro", description = "Cell type ID of the eQTL's cell subclass", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cellTypeId")
  public String getCellTypeId() {
    return cellTypeId;
  }

  public void setCellTypeId(String cellTypeId) {
    this.cellTypeId = cellTypeId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EqtlPhenotypeQueryDto eqtlPhenotypeQuery = (EqtlPhenotypeQueryDto) o;
    return Objects.equals(this.variant, eqtlPhenotypeQuery.variant) &&
        Objects.equals(this.ensemblGeneId, eqtlPhenotypeQuery.ensemblGeneId) &&
        Objects.equals(this.cellTypeId, eqtlPhenotypeQuery.cellTypeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variant, ensemblGeneId, cellTypeId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EqtlPhenotypeQueryDto {\n");
    sb.append("    variant: ").append(toIndentedString(variant)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    cellTypeId: ").append(toIndentedString(cellTypeId)).append("\n");
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

    private EqtlPhenotypeQueryDto instance;

    public Builder() {
      this(new EqtlPhenotypeQueryDto());
    }

    protected Builder(EqtlPhenotypeQueryDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EqtlPhenotypeQueryDto value) { 
      this.instance.setVariant(value.variant);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setCellTypeId(value.cellTypeId);
      return this;
    }

    public EqtlPhenotypeQueryDto.Builder variant(String variant) {
      this.instance.variant(variant);
      return this;
    }
    
    public EqtlPhenotypeQueryDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public EqtlPhenotypeQueryDto.Builder cellTypeId(String cellTypeId) {
      this.instance.cellTypeId(cellTypeId);
      return this;
    }
    
    /**
    * returns a built EqtlPhenotypeQueryDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EqtlPhenotypeQueryDto build() {
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
  public static EqtlPhenotypeQueryDto.Builder builder() {
    return new EqtlPhenotypeQueryDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EqtlPhenotypeQueryDto.Builder toBuilder() {
    EqtlPhenotypeQueryDto.Builder builder = new EqtlPhenotypeQueryDto.Builder();
    return builder.copyOf(this);
  }

}

