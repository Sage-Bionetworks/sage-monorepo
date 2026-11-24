package org.sagebionetworks.qtl.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.qtl.api.model.dto.CellClassEffectSizeDto;
import org.sagebionetworks.qtl.api.model.dto.GenotypeExpressionSummaryDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Expression summaries and cell class effect sizes for a single eQTL.
 */

@Schema(name = "EqtlPhenotype", description = "Expression summaries and cell class effect sizes for a single eQTL.")
@JsonTypeName("EqtlPhenotype")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class EqtlPhenotypeDto {

  private String variant;

  private String ensemblGeneId;

  private String cellTypeId;

  @Valid
  private List<@Valid GenotypeExpressionSummaryDto> expressionSummaries = new ArrayList<>();

  @Valid
  private List<@Valid CellClassEffectSizeDto> effectSizeCellClassData = new ArrayList<>();

  public EqtlPhenotypeDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EqtlPhenotypeDto(String variant, String ensemblGeneId, String cellTypeId, List<@Valid GenotypeExpressionSummaryDto> expressionSummaries, List<@Valid CellClassEffectSizeDto> effectSizeCellClassData) {
    this.variant = variant;
    this.ensemblGeneId = ensemblGeneId;
    this.cellTypeId = cellTypeId;
    this.expressionSummaries = expressionSummaries;
    this.effectSizeCellClassData = effectSizeCellClassData;
  }

  public EqtlPhenotypeDto variant(String variant) {
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

  public EqtlPhenotypeDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Ensembl gene ID
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", example = "ENSG00000142599", description = "Ensembl gene ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public EqtlPhenotypeDto cellTypeId(String cellTypeId) {
    this.cellTypeId = cellTypeId;
    return this;
  }

  /**
   * Cell type ID of the eQTL's cell subclass
   * @return cellTypeId
   */
  @NotNull 
  @Schema(name = "cell_type_id", example = "astro", description = "Cell type ID of the eQTL's cell subclass", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cell_type_id")
  public String getCellTypeId() {
    return cellTypeId;
  }

  public void setCellTypeId(String cellTypeId) {
    this.cellTypeId = cellTypeId;
  }

  public EqtlPhenotypeDto expressionSummaries(List<@Valid GenotypeExpressionSummaryDto> expressionSummaries) {
    this.expressionSummaries = expressionSummaries;
    return this;
  }

  public EqtlPhenotypeDto addExpressionSummariesItem(GenotypeExpressionSummaryDto expressionSummariesItem) {
    if (this.expressionSummaries == null) {
      this.expressionSummaries = new ArrayList<>();
    }
    this.expressionSummaries.add(expressionSummariesItem);
    return this;
  }

  /**
   * Gene expression summaries for each genotype, in the order stored in the data
   * @return expressionSummaries
   */
  @NotNull @Valid 
  @Schema(name = "expression_summaries", description = "Gene expression summaries for each genotype, in the order stored in the data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("expression_summaries")
  public List<@Valid GenotypeExpressionSummaryDto> getExpressionSummaries() {
    return expressionSummaries;
  }

  public void setExpressionSummaries(List<@Valid GenotypeExpressionSummaryDto> expressionSummaries) {
    this.expressionSummaries = expressionSummaries;
  }

  public EqtlPhenotypeDto effectSizeCellClassData(List<@Valid CellClassEffectSizeDto> effectSizeCellClassData) {
    this.effectSizeCellClassData = effectSizeCellClassData;
    return this;
  }

  public EqtlPhenotypeDto addEffectSizeCellClassDataItem(CellClassEffectSizeDto effectSizeCellClassDataItem) {
    if (this.effectSizeCellClassData == null) {
      this.effectSizeCellClassData = new ArrayList<>();
    }
    this.effectSizeCellClassData.add(effectSizeCellClassDataItem);
    return this;
  }

  /**
   * The variant's effect size on the gene in each cell class
   * @return effectSizeCellClassData
   */
  @NotNull @Valid 
  @Schema(name = "effect_size_cell_class_data", description = "The variant's effect size on the gene in each cell class", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("effect_size_cell_class_data")
  public List<@Valid CellClassEffectSizeDto> getEffectSizeCellClassData() {
    return effectSizeCellClassData;
  }

  public void setEffectSizeCellClassData(List<@Valid CellClassEffectSizeDto> effectSizeCellClassData) {
    this.effectSizeCellClassData = effectSizeCellClassData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EqtlPhenotypeDto eqtlPhenotype = (EqtlPhenotypeDto) o;
    return Objects.equals(this.variant, eqtlPhenotype.variant) &&
        Objects.equals(this.ensemblGeneId, eqtlPhenotype.ensemblGeneId) &&
        Objects.equals(this.cellTypeId, eqtlPhenotype.cellTypeId) &&
        Objects.equals(this.expressionSummaries, eqtlPhenotype.expressionSummaries) &&
        Objects.equals(this.effectSizeCellClassData, eqtlPhenotype.effectSizeCellClassData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variant, ensemblGeneId, cellTypeId, expressionSummaries, effectSizeCellClassData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EqtlPhenotypeDto {\n");
    sb.append("    variant: ").append(toIndentedString(variant)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    cellTypeId: ").append(toIndentedString(cellTypeId)).append("\n");
    sb.append("    expressionSummaries: ").append(toIndentedString(expressionSummaries)).append("\n");
    sb.append("    effectSizeCellClassData: ").append(toIndentedString(effectSizeCellClassData)).append("\n");
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

    private EqtlPhenotypeDto instance;

    public Builder() {
      this(new EqtlPhenotypeDto());
    }

    protected Builder(EqtlPhenotypeDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EqtlPhenotypeDto value) { 
      this.instance.setVariant(value.variant);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setCellTypeId(value.cellTypeId);
      this.instance.setExpressionSummaries(value.expressionSummaries);
      this.instance.setEffectSizeCellClassData(value.effectSizeCellClassData);
      return this;
    }

    public EqtlPhenotypeDto.Builder variant(String variant) {
      this.instance.variant(variant);
      return this;
    }
    
    public EqtlPhenotypeDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public EqtlPhenotypeDto.Builder cellTypeId(String cellTypeId) {
      this.instance.cellTypeId(cellTypeId);
      return this;
    }
    
    public EqtlPhenotypeDto.Builder expressionSummaries(List<GenotypeExpressionSummaryDto> expressionSummaries) {
      this.instance.expressionSummaries(expressionSummaries);
      return this;
    }
    
    public EqtlPhenotypeDto.Builder effectSizeCellClassData(List<CellClassEffectSizeDto> effectSizeCellClassData) {
      this.instance.effectSizeCellClassData(effectSizeCellClassData);
      return this;
    }
    
    /**
    * returns a built EqtlPhenotypeDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EqtlPhenotypeDto build() {
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
  public static EqtlPhenotypeDto.Builder builder() {
    return new EqtlPhenotypeDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EqtlPhenotypeDto.Builder toBuilder() {
    EqtlPhenotypeDto.Builder builder = new EqtlPhenotypeDto.Builder();
    return builder.copyOf(this);
  }

}

