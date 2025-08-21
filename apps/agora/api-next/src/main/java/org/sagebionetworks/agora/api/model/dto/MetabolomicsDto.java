package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Metabolomics
 */

@Schema(name = "Metabolomics", description = "Metabolomics")
@JsonTypeName("Metabolomics")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MetabolomicsDto {

  private String id;

  private String associatedGeneName;

  private String ensemblGeneId;

  private String metaboliteId;

  private String metaboliteFullName;

  private BigDecimal associationP;

  private BigDecimal geneWidePThreshold1kgp;

  @Valid
  private List<BigDecimal> nPerGroup = new ArrayList<>();

  @Valid
  private List<String> boxplotGroupNames = new ArrayList<>();

  @Valid
  private List<BigDecimal> adDiagnosisPValue = new ArrayList<>();

  @Valid
  private List<List<BigDecimal>> transposedBoxplotStats = new ArrayList<>();

  public MetabolomicsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MetabolomicsDto(String id, String associatedGeneName, String ensemblGeneId, String metaboliteId, String metaboliteFullName, BigDecimal associationP, BigDecimal geneWidePThreshold1kgp, List<BigDecimal> nPerGroup, List<String> boxplotGroupNames, List<BigDecimal> adDiagnosisPValue, List<List<BigDecimal>> transposedBoxplotStats) {
    this.id = id;
    this.associatedGeneName = associatedGeneName;
    this.ensemblGeneId = ensemblGeneId;
    this.metaboliteId = metaboliteId;
    this.metaboliteFullName = metaboliteFullName;
    this.associationP = associationP;
    this.geneWidePThreshold1kgp = geneWidePThreshold1kgp;
    this.nPerGroup = nPerGroup;
    this.boxplotGroupNames = boxplotGroupNames;
    this.adDiagnosisPValue = adDiagnosisPValue;
    this.transposedBoxplotStats = transposedBoxplotStats;
  }

  public MetabolomicsDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MetabolomicsDto associatedGeneName(String associatedGeneName) {
    this.associatedGeneName = associatedGeneName;
    return this;
  }

  /**
   * Get associatedGeneName
   * @return associatedGeneName
   */
  @NotNull 
  @Schema(name = "associated_gene_name", example = "VGF", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("associated_gene_name")
  public String getAssociatedGeneName() {
    return associatedGeneName;
  }

  public void setAssociatedGeneName(String associatedGeneName) {
    this.associatedGeneName = associatedGeneName;
  }

  public MetabolomicsDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Get ensemblGeneId
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public MetabolomicsDto metaboliteId(String metaboliteId) {
    this.metaboliteId = metaboliteId;
    return this;
  }

  /**
   * Get metaboliteId
   * @return metaboliteId
   */
  @NotNull 
  @Schema(name = "metabolite_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("metabolite_id")
  public String getMetaboliteId() {
    return metaboliteId;
  }

  public void setMetaboliteId(String metaboliteId) {
    this.metaboliteId = metaboliteId;
  }

  public MetabolomicsDto metaboliteFullName(String metaboliteFullName) {
    this.metaboliteFullName = metaboliteFullName;
    return this;
  }

  /**
   * Get metaboliteFullName
   * @return metaboliteFullName
   */
  @NotNull 
  @Schema(name = "metabolite_full_name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("metabolite_full_name")
  public String getMetaboliteFullName() {
    return metaboliteFullName;
  }

  public void setMetaboliteFullName(String metaboliteFullName) {
    this.metaboliteFullName = metaboliteFullName;
  }

  public MetabolomicsDto associationP(BigDecimal associationP) {
    this.associationP = associationP;
    return this;
  }

  /**
   * Get associationP
   * @return associationP
   */
  @NotNull @Valid 
  @Schema(name = "association_p", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("association_p")
  public BigDecimal getAssociationP() {
    return associationP;
  }

  public void setAssociationP(BigDecimal associationP) {
    this.associationP = associationP;
  }

  public MetabolomicsDto geneWidePThreshold1kgp(BigDecimal geneWidePThreshold1kgp) {
    this.geneWidePThreshold1kgp = geneWidePThreshold1kgp;
    return this;
  }

  /**
   * Get geneWidePThreshold1kgp
   * @return geneWidePThreshold1kgp
   */
  @NotNull @Valid 
  @Schema(name = "gene_wide_p_threshold_1kgp", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("gene_wide_p_threshold_1kgp")
  public BigDecimal getGeneWidePThreshold1kgp() {
    return geneWidePThreshold1kgp;
  }

  public void setGeneWidePThreshold1kgp(BigDecimal geneWidePThreshold1kgp) {
    this.geneWidePThreshold1kgp = geneWidePThreshold1kgp;
  }

  public MetabolomicsDto nPerGroup(List<BigDecimal> nPerGroup) {
    this.nPerGroup = nPerGroup;
    return this;
  }

  public MetabolomicsDto addNPerGroupItem(BigDecimal nPerGroupItem) {
    if (this.nPerGroup == null) {
      this.nPerGroup = new ArrayList<>();
    }
    this.nPerGroup.add(nPerGroupItem);
    return this;
  }

  /**
   * Get nPerGroup
   * @return nPerGroup
   */
  @NotNull @Valid 
  @Schema(name = "n_per_group", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("n_per_group")
  public List<BigDecimal> getnPerGroup() {
    return nPerGroup;
  }

  public void setnPerGroup(List<BigDecimal> nPerGroup) {
    this.nPerGroup = nPerGroup;
  }

  public MetabolomicsDto boxplotGroupNames(List<String> boxplotGroupNames) {
    this.boxplotGroupNames = boxplotGroupNames;
    return this;
  }

  public MetabolomicsDto addBoxplotGroupNamesItem(String boxplotGroupNamesItem) {
    if (this.boxplotGroupNames == null) {
      this.boxplotGroupNames = new ArrayList<>();
    }
    this.boxplotGroupNames.add(boxplotGroupNamesItem);
    return this;
  }

  /**
   * Get boxplotGroupNames
   * @return boxplotGroupNames
   */
  @NotNull 
  @Schema(name = "boxplot_group_names", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("boxplot_group_names")
  public List<String> getBoxplotGroupNames() {
    return boxplotGroupNames;
  }

  public void setBoxplotGroupNames(List<String> boxplotGroupNames) {
    this.boxplotGroupNames = boxplotGroupNames;
  }

  public MetabolomicsDto adDiagnosisPValue(List<BigDecimal> adDiagnosisPValue) {
    this.adDiagnosisPValue = adDiagnosisPValue;
    return this;
  }

  public MetabolomicsDto addAdDiagnosisPValueItem(BigDecimal adDiagnosisPValueItem) {
    if (this.adDiagnosisPValue == null) {
      this.adDiagnosisPValue = new ArrayList<>();
    }
    this.adDiagnosisPValue.add(adDiagnosisPValueItem);
    return this;
  }

  /**
   * Get adDiagnosisPValue
   * @return adDiagnosisPValue
   */
  @NotNull @Valid 
  @Schema(name = "ad_diagnosis_p_value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ad_diagnosis_p_value")
  public List<BigDecimal> getAdDiagnosisPValue() {
    return adDiagnosisPValue;
  }

  public void setAdDiagnosisPValue(List<BigDecimal> adDiagnosisPValue) {
    this.adDiagnosisPValue = adDiagnosisPValue;
  }

  public MetabolomicsDto transposedBoxplotStats(List<List<BigDecimal>> transposedBoxplotStats) {
    this.transposedBoxplotStats = transposedBoxplotStats;
    return this;
  }

  public MetabolomicsDto addTransposedBoxplotStatsItem(List<BigDecimal> transposedBoxplotStatsItem) {
    if (this.transposedBoxplotStats == null) {
      this.transposedBoxplotStats = new ArrayList<>();
    }
    this.transposedBoxplotStats.add(transposedBoxplotStatsItem);
    return this;
  }

  /**
   * min, first quartile, median, third quartile, max
   * @return transposedBoxplotStats
   */
  @NotNull @Valid 
  @Schema(name = "transposed_boxplot_stats", description = "min, first quartile, median, third quartile, max", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("transposed_boxplot_stats")
  public List<List<BigDecimal>> getTransposedBoxplotStats() {
    return transposedBoxplotStats;
  }

  public void setTransposedBoxplotStats(List<List<BigDecimal>> transposedBoxplotStats) {
    this.transposedBoxplotStats = transposedBoxplotStats;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetabolomicsDto metabolomics = (MetabolomicsDto) o;
    return Objects.equals(this.id, metabolomics.id) &&
        Objects.equals(this.associatedGeneName, metabolomics.associatedGeneName) &&
        Objects.equals(this.ensemblGeneId, metabolomics.ensemblGeneId) &&
        Objects.equals(this.metaboliteId, metabolomics.metaboliteId) &&
        Objects.equals(this.metaboliteFullName, metabolomics.metaboliteFullName) &&
        Objects.equals(this.associationP, metabolomics.associationP) &&
        Objects.equals(this.geneWidePThreshold1kgp, metabolomics.geneWidePThreshold1kgp) &&
        Objects.equals(this.nPerGroup, metabolomics.nPerGroup) &&
        Objects.equals(this.boxplotGroupNames, metabolomics.boxplotGroupNames) &&
        Objects.equals(this.adDiagnosisPValue, metabolomics.adDiagnosisPValue) &&
        Objects.equals(this.transposedBoxplotStats, metabolomics.transposedBoxplotStats);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, associatedGeneName, ensemblGeneId, metaboliteId, metaboliteFullName, associationP, geneWidePThreshold1kgp, nPerGroup, boxplotGroupNames, adDiagnosisPValue, transposedBoxplotStats);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetabolomicsDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    associatedGeneName: ").append(toIndentedString(associatedGeneName)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    metaboliteId: ").append(toIndentedString(metaboliteId)).append("\n");
    sb.append("    metaboliteFullName: ").append(toIndentedString(metaboliteFullName)).append("\n");
    sb.append("    associationP: ").append(toIndentedString(associationP)).append("\n");
    sb.append("    geneWidePThreshold1kgp: ").append(toIndentedString(geneWidePThreshold1kgp)).append("\n");
    sb.append("    nPerGroup: ").append(toIndentedString(nPerGroup)).append("\n");
    sb.append("    boxplotGroupNames: ").append(toIndentedString(boxplotGroupNames)).append("\n");
    sb.append("    adDiagnosisPValue: ").append(toIndentedString(adDiagnosisPValue)).append("\n");
    sb.append("    transposedBoxplotStats: ").append(toIndentedString(transposedBoxplotStats)).append("\n");
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

    private MetabolomicsDto instance;

    public Builder() {
      this(new MetabolomicsDto());
    }

    protected Builder(MetabolomicsDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MetabolomicsDto value) { 
      this.instance.setId(value.id);
      this.instance.setAssociatedGeneName(value.associatedGeneName);
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setMetaboliteId(value.metaboliteId);
      this.instance.setMetaboliteFullName(value.metaboliteFullName);
      this.instance.setAssociationP(value.associationP);
      this.instance.setGeneWidePThreshold1kgp(value.geneWidePThreshold1kgp);
      this.instance.setnPerGroup(value.nPerGroup);
      this.instance.setBoxplotGroupNames(value.boxplotGroupNames);
      this.instance.setAdDiagnosisPValue(value.adDiagnosisPValue);
      this.instance.setTransposedBoxplotStats(value.transposedBoxplotStats);
      return this;
    }

    public MetabolomicsDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public MetabolomicsDto.Builder associatedGeneName(String associatedGeneName) {
      this.instance.associatedGeneName(associatedGeneName);
      return this;
    }
    
    public MetabolomicsDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public MetabolomicsDto.Builder metaboliteId(String metaboliteId) {
      this.instance.metaboliteId(metaboliteId);
      return this;
    }
    
    public MetabolomicsDto.Builder metaboliteFullName(String metaboliteFullName) {
      this.instance.metaboliteFullName(metaboliteFullName);
      return this;
    }
    
    public MetabolomicsDto.Builder associationP(BigDecimal associationP) {
      this.instance.associationP(associationP);
      return this;
    }
    
    public MetabolomicsDto.Builder geneWidePThreshold1kgp(BigDecimal geneWidePThreshold1kgp) {
      this.instance.geneWidePThreshold1kgp(geneWidePThreshold1kgp);
      return this;
    }
    
    public MetabolomicsDto.Builder nPerGroup(List<BigDecimal> nPerGroup) {
      this.instance.nPerGroup(nPerGroup);
      return this;
    }
    
    public MetabolomicsDto.Builder boxplotGroupNames(List<String> boxplotGroupNames) {
      this.instance.boxplotGroupNames(boxplotGroupNames);
      return this;
    }
    
    public MetabolomicsDto.Builder adDiagnosisPValue(List<BigDecimal> adDiagnosisPValue) {
      this.instance.adDiagnosisPValue(adDiagnosisPValue);
      return this;
    }
    
    public MetabolomicsDto.Builder transposedBoxplotStats(List<List<BigDecimal>> transposedBoxplotStats) {
      this.instance.transposedBoxplotStats(transposedBoxplotStats);
      return this;
    }
    
    /**
    * returns a built MetabolomicsDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MetabolomicsDto build() {
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
  public static MetabolomicsDto.Builder builder() {
    return new MetabolomicsDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MetabolomicsDto.Builder toBuilder() {
    MetabolomicsDto.Builder builder = new MetabolomicsDto.Builder();
    return builder.copyOf(this);
  }

}

