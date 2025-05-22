package org.sagebionetworks.agora.gene.api.model.dto;

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
 * A differential expression profile (protein).
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "DifferentialExpressionProfileProtein", description = "A differential expression profile (protein).")
@JsonTypeName("DifferentialExpressionProfileProtein")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class DifferentialExpressionProfileProteinDto {

  private String ensemblGeneId;

  private @Nullable String hgncSymbol;

  public DifferentialExpressionProfileProteinDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DifferentialExpressionProfileProteinDto(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public DifferentialExpressionProfileProteinDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * The Ensembl gene ID.
   * @return ensemblGeneId
   */
  @NotNull @Pattern(regexp = "^ENSG\\d{11}$") @Size(min = 15, max = 15) 
  @Schema(name = "ensembl_gene_id", example = "ENSG00000139618", description = "The Ensembl gene ID.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public DifferentialExpressionProfileProteinDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * The HGNC gene symbol.
   * @return hgncSymbol
   */
  @Size(min = 0) 
  @Schema(name = "hgnc_symbol", example = "TP53", description = "The HGNC gene symbol.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DifferentialExpressionProfileProteinDto differentialExpressionProfileProtein = (DifferentialExpressionProfileProteinDto) o;
    return Objects.equals(this.ensemblGeneId, differentialExpressionProfileProtein.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, differentialExpressionProfileProtein.hgncSymbol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, hgncSymbol);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DifferentialExpressionProfileProteinDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
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

    private DifferentialExpressionProfileProteinDto instance;

    public Builder() {
      this(new DifferentialExpressionProfileProteinDto());
    }

    protected Builder(DifferentialExpressionProfileProteinDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DifferentialExpressionProfileProteinDto value) { 
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      return this;
    }

    public DifferentialExpressionProfileProteinDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public DifferentialExpressionProfileProteinDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    /**
    * returns a built DifferentialExpressionProfileProteinDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DifferentialExpressionProfileProteinDto build() {
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
  public static DifferentialExpressionProfileProteinDto.Builder builder() {
    return new DifferentialExpressionProfileProteinDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DifferentialExpressionProfileProteinDto.Builder toBuilder() {
    DifferentialExpressionProfileProteinDto.Builder builder = new DifferentialExpressionProfileProteinDto.Builder();
    return builder.copyOf(this);
  }

}

