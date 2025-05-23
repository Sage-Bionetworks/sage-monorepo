package org.sagebionetworks.agora.gene.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.gene.api.model.dto.NominationsDto;
import org.sagebionetworks.agora.gene.api.model.dto.TissueDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A RNA differential expression profile.
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

@Schema(name = "RnaDifferentialExpressionProfile", description = "A RNA differential expression profile.")
@JsonTypeName("RnaDifferentialExpressionProfile")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public class RnaDifferentialExpressionProfileDto {

  private String ensemblGeneId;

  private String hgncSymbol;

  private BigDecimal targetRiskScore = null;

  private BigDecimal geneticsScore = null;

  private BigDecimal multiOmicsScore = null;

  @Valid
  private List<@Valid TissueDto> tissues = new ArrayList<>();

  private @Nullable NominationsDto nominations;

  @Valid
  private @Nullable List<BigDecimal> associations;

  @Valid
  private @Nullable List<String> biodomains;

  public RnaDifferentialExpressionProfileDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RnaDifferentialExpressionProfileDto(String ensemblGeneId, String hgncSymbol, BigDecimal targetRiskScore, BigDecimal geneticsScore, BigDecimal multiOmicsScore, List<@Valid TissueDto> tissues) {
    this.ensemblGeneId = ensemblGeneId;
    this.hgncSymbol = hgncSymbol;
    this.targetRiskScore = targetRiskScore;
    this.geneticsScore = geneticsScore;
    this.multiOmicsScore = multiOmicsScore;
    this.tissues = tissues;
  }

  public RnaDifferentialExpressionProfileDto ensemblGeneId(String ensemblGeneId) {
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

  public RnaDifferentialExpressionProfileDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * The HGNC gene symbol.
   * @return hgncSymbol
   */
  @NotNull @Size(min = 0) 
  @Schema(name = "hgnc_symbol", example = "TP53", description = "The HGNC gene symbol.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  public RnaDifferentialExpressionProfileDto targetRiskScore(BigDecimal targetRiskScore) {
    this.targetRiskScore = targetRiskScore;
    return this;
  }

  /**
   * Target risk score
   * @return targetRiskScore
   */
  @NotNull @Valid 
  @Schema(name = "target_risk_score", description = "Target risk score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("target_risk_score")
  public BigDecimal getTargetRiskScore() {
    return targetRiskScore;
  }

  public void setTargetRiskScore(BigDecimal targetRiskScore) {
    this.targetRiskScore = targetRiskScore;
  }

  public RnaDifferentialExpressionProfileDto geneticsScore(BigDecimal geneticsScore) {
    this.geneticsScore = geneticsScore;
    return this;
  }

  /**
   * Genetics score
   * @return geneticsScore
   */
  @NotNull @Valid 
  @Schema(name = "genetics_score", description = "Genetics score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("genetics_score")
  public BigDecimal getGeneticsScore() {
    return geneticsScore;
  }

  public void setGeneticsScore(BigDecimal geneticsScore) {
    this.geneticsScore = geneticsScore;
  }

  public RnaDifferentialExpressionProfileDto multiOmicsScore(BigDecimal multiOmicsScore) {
    this.multiOmicsScore = multiOmicsScore;
    return this;
  }

  /**
   * Multi-omics score
   * @return multiOmicsScore
   */
  @NotNull @Valid 
  @Schema(name = "multi_omics_score", description = "Multi-omics score", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("multi_omics_score")
  public BigDecimal getMultiOmicsScore() {
    return multiOmicsScore;
  }

  public void setMultiOmicsScore(BigDecimal multiOmicsScore) {
    this.multiOmicsScore = multiOmicsScore;
  }

  public RnaDifferentialExpressionProfileDto tissues(List<@Valid TissueDto> tissues) {
    this.tissues = tissues;
    return this;
  }

  public RnaDifferentialExpressionProfileDto addTissuesItem(TissueDto tissuesItem) {
    if (this.tissues == null) {
      this.tissues = new ArrayList<>();
    }
    this.tissues.add(tissuesItem);
    return this;
  }

  /**
   * Array of gene tissues
   * @return tissues
   */
  @NotNull @Valid 
  @Schema(name = "tissues", description = "Array of gene tissues", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tissues")
  public List<@Valid TissueDto> getTissues() {
    return tissues;
  }

  public void setTissues(List<@Valid TissueDto> tissues) {
    this.tissues = tissues;
  }

  public RnaDifferentialExpressionProfileDto nominations(NominationsDto nominations) {
    this.nominations = nominations;
    return this;
  }

  /**
   * Get nominations
   * @return nominations
   */
  @Valid 
  @Schema(name = "nominations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nominations")
  public NominationsDto getNominations() {
    return nominations;
  }

  public void setNominations(NominationsDto nominations) {
    this.nominations = nominations;
  }

  public RnaDifferentialExpressionProfileDto associations(List<BigDecimal> associations) {
    this.associations = associations;
    return this;
  }

  public RnaDifferentialExpressionProfileDto addAssociationsItem(BigDecimal associationsItem) {
    if (this.associations == null) {
      this.associations = new ArrayList<>();
    }
    this.associations.add(associationsItem);
    return this;
  }

  /**
   * Array of association values
   * @return associations
   */
  @Valid 
  @Schema(name = "associations", description = "Array of association values", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("associations")
  public List<BigDecimal> getAssociations() {
    return associations;
  }

  public void setAssociations(List<BigDecimal> associations) {
    this.associations = associations;
  }

  public RnaDifferentialExpressionProfileDto biodomains(List<String> biodomains) {
    this.biodomains = biodomains;
    return this;
  }

  public RnaDifferentialExpressionProfileDto addBiodomainsItem(String biodomainsItem) {
    if (this.biodomains == null) {
      this.biodomains = new ArrayList<>();
    }
    this.biodomains.add(biodomainsItem);
    return this;
  }

  /**
   * Array of biological domains
   * @return biodomains
   */
  
  @Schema(name = "biodomains", description = "Array of biological domains", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("biodomains")
  public List<String> getBiodomains() {
    return biodomains;
  }

  public void setBiodomains(List<String> biodomains) {
    this.biodomains = biodomains;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RnaDifferentialExpressionProfileDto rnaDifferentialExpressionProfile = (RnaDifferentialExpressionProfileDto) o;
    return Objects.equals(this.ensemblGeneId, rnaDifferentialExpressionProfile.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, rnaDifferentialExpressionProfile.hgncSymbol) &&
        Objects.equals(this.targetRiskScore, rnaDifferentialExpressionProfile.targetRiskScore) &&
        Objects.equals(this.geneticsScore, rnaDifferentialExpressionProfile.geneticsScore) &&
        Objects.equals(this.multiOmicsScore, rnaDifferentialExpressionProfile.multiOmicsScore) &&
        Objects.equals(this.tissues, rnaDifferentialExpressionProfile.tissues) &&
        Objects.equals(this.nominations, rnaDifferentialExpressionProfile.nominations) &&
        Objects.equals(this.associations, rnaDifferentialExpressionProfile.associations) &&
        Objects.equals(this.biodomains, rnaDifferentialExpressionProfile.biodomains);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, hgncSymbol, targetRiskScore, geneticsScore, multiOmicsScore, tissues, nominations, associations, biodomains);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RnaDifferentialExpressionProfileDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    targetRiskScore: ").append(toIndentedString(targetRiskScore)).append("\n");
    sb.append("    geneticsScore: ").append(toIndentedString(geneticsScore)).append("\n");
    sb.append("    multiOmicsScore: ").append(toIndentedString(multiOmicsScore)).append("\n");
    sb.append("    tissues: ").append(toIndentedString(tissues)).append("\n");
    sb.append("    nominations: ").append(toIndentedString(nominations)).append("\n");
    sb.append("    associations: ").append(toIndentedString(associations)).append("\n");
    sb.append("    biodomains: ").append(toIndentedString(biodomains)).append("\n");
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

    private RnaDifferentialExpressionProfileDto instance;

    public Builder() {
      this(new RnaDifferentialExpressionProfileDto());
    }

    protected Builder(RnaDifferentialExpressionProfileDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(RnaDifferentialExpressionProfileDto value) { 
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setTargetRiskScore(value.targetRiskScore);
      this.instance.setGeneticsScore(value.geneticsScore);
      this.instance.setMultiOmicsScore(value.multiOmicsScore);
      this.instance.setTissues(value.tissues);
      this.instance.setNominations(value.nominations);
      this.instance.setAssociations(value.associations);
      this.instance.setBiodomains(value.biodomains);
      return this;
    }

    public RnaDifferentialExpressionProfileDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder targetRiskScore(BigDecimal targetRiskScore) {
      this.instance.targetRiskScore(targetRiskScore);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder geneticsScore(BigDecimal geneticsScore) {
      this.instance.geneticsScore(geneticsScore);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder multiOmicsScore(BigDecimal multiOmicsScore) {
      this.instance.multiOmicsScore(multiOmicsScore);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder tissues(List<TissueDto> tissues) {
      this.instance.tissues(tissues);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder nominations(NominationsDto nominations) {
      this.instance.nominations(nominations);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder associations(List<BigDecimal> associations) {
      this.instance.associations(associations);
      return this;
    }
    
    public RnaDifferentialExpressionProfileDto.Builder biodomains(List<String> biodomains) {
      this.instance.biodomains(biodomains);
      return this;
    }
    
    /**
    * returns a built RnaDifferentialExpressionProfileDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public RnaDifferentialExpressionProfileDto build() {
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
  public static RnaDifferentialExpressionProfileDto.Builder builder() {
    return new RnaDifferentialExpressionProfileDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public RnaDifferentialExpressionProfileDto.Builder toBuilder() {
    RnaDifferentialExpressionProfileDto.Builder builder = new RnaDifferentialExpressionProfileDto.Builder();
    return builder.copyOf(this);
  }

}

