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
import org.sagebionetworks.agora.api.model.dto.GCTGeneNominationsDto;
import org.sagebionetworks.agora.api.model.dto.GCTGeneTissueDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GCT Gene
 */

@Schema(name = "GCTGene", description = "GCT Gene")
@JsonTypeName("GCTGene")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GCTGeneDto {

  private String ensemblGeneId;

  private String hgncSymbol;

  private @Nullable String uniprotid = null;

  private @Nullable String uid = null;

  private @Nullable String searchString = null;

  @Valid
  private @Nullable List<String> searchArray;

  @Valid
  private List<@Valid GCTGeneTissueDto> tissues = new ArrayList<>();

  private @Nullable GCTGeneNominationsDto nominations;

  @Valid
  private @Nullable List<BigDecimal> associations;

  private BigDecimal targetRiskScore = null;

  private BigDecimal geneticsScore = null;

  private BigDecimal multiOmicsScore = null;

  @Valid
  private @Nullable List<String> biodomains;

  private @Nullable Boolean pinned = null;

  @Valid
  private @Nullable List<String> targetEnablingResources;

  public GCTGeneDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GCTGeneDto(String ensemblGeneId, String hgncSymbol, List<@Valid GCTGeneTissueDto> tissues, BigDecimal targetRiskScore, BigDecimal geneticsScore, BigDecimal multiOmicsScore) {
    this.ensemblGeneId = ensemblGeneId;
    this.hgncSymbol = hgncSymbol;
    this.tissues = tissues;
    this.targetRiskScore = targetRiskScore;
    this.geneticsScore = geneticsScore;
    this.multiOmicsScore = multiOmicsScore;
  }

  public GCTGeneDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * Ensembl gene identifier
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", description = "Ensembl gene identifier", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public GCTGeneDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * HGNC gene symbol
   * @return hgncSymbol
   */
  @NotNull 
  @Schema(name = "hgnc_symbol", description = "HGNC gene symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  public GCTGeneDto uniprotid(@Nullable String uniprotid) {
    this.uniprotid = uniprotid;
    return this;
  }

  /**
   * UniProt identifier
   * @return uniprotid
   */
  
  @Schema(name = "uniprotid", description = "UniProt identifier", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uniprotid")
  public @Nullable String getUniprotid() {
    return uniprotid;
  }

  public void setUniprotid(@Nullable String uniprotid) {
    this.uniprotid = uniprotid;
  }

  public GCTGeneDto uid(@Nullable String uid) {
    this.uid = uid;
    return this;
  }

  /**
   * Unique identifier
   * @return uid
   */
  
  @Schema(name = "uid", description = "Unique identifier", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uid")
  public @Nullable String getUid() {
    return uid;
  }

  public void setUid(@Nullable String uid) {
    this.uid = uid;
  }

  public GCTGeneDto searchString(@Nullable String searchString) {
    this.searchString = searchString;
    return this;
  }

  /**
   * Search string
   * @return searchString
   */
  
  @Schema(name = "search_string", description = "Search string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search_string")
  public @Nullable String getSearchString() {
    return searchString;
  }

  public void setSearchString(@Nullable String searchString) {
    this.searchString = searchString;
  }

  public GCTGeneDto searchArray(@Nullable List<String> searchArray) {
    this.searchArray = searchArray;
    return this;
  }

  public GCTGeneDto addSearchArrayItem(String searchArrayItem) {
    if (this.searchArray == null) {
      this.searchArray = new ArrayList<>();
    }
    this.searchArray.add(searchArrayItem);
    return this;
  }

  /**
   * Array of search terms
   * @return searchArray
   */
  
  @Schema(name = "search_array", description = "Array of search terms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("search_array")
  public @Nullable List<String> getSearchArray() {
    return searchArray;
  }

  public void setSearchArray(@Nullable List<String> searchArray) {
    this.searchArray = searchArray;
  }

  public GCTGeneDto tissues(List<@Valid GCTGeneTissueDto> tissues) {
    this.tissues = tissues;
    return this;
  }

  public GCTGeneDto addTissuesItem(GCTGeneTissueDto tissuesItem) {
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
  public List<@Valid GCTGeneTissueDto> getTissues() {
    return tissues;
  }

  public void setTissues(List<@Valid GCTGeneTissueDto> tissues) {
    this.tissues = tissues;
  }

  public GCTGeneDto nominations(@Nullable GCTGeneNominationsDto nominations) {
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
  public @Nullable GCTGeneNominationsDto getNominations() {
    return nominations;
  }

  public void setNominations(@Nullable GCTGeneNominationsDto nominations) {
    this.nominations = nominations;
  }

  public GCTGeneDto associations(@Nullable List<BigDecimal> associations) {
    this.associations = associations;
    return this;
  }

  public GCTGeneDto addAssociationsItem(BigDecimal associationsItem) {
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
  public @Nullable List<BigDecimal> getAssociations() {
    return associations;
  }

  public void setAssociations(@Nullable List<BigDecimal> associations) {
    this.associations = associations;
  }

  public GCTGeneDto targetRiskScore(BigDecimal targetRiskScore) {
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

  public GCTGeneDto geneticsScore(BigDecimal geneticsScore) {
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

  public GCTGeneDto multiOmicsScore(BigDecimal multiOmicsScore) {
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

  public GCTGeneDto biodomains(@Nullable List<String> biodomains) {
    this.biodomains = biodomains;
    return this;
  }

  public GCTGeneDto addBiodomainsItem(String biodomainsItem) {
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
  public @Nullable List<String> getBiodomains() {
    return biodomains;
  }

  public void setBiodomains(@Nullable List<String> biodomains) {
    this.biodomains = biodomains;
  }

  public GCTGeneDto pinned(@Nullable Boolean pinned) {
    this.pinned = pinned;
    return this;
  }

  /**
   * Whether the gene is pinned
   * @return pinned
   */
  
  @Schema(name = "pinned", description = "Whether the gene is pinned", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pinned")
  public @Nullable Boolean getPinned() {
    return pinned;
  }

  public void setPinned(@Nullable Boolean pinned) {
    this.pinned = pinned;
  }

  public GCTGeneDto targetEnablingResources(@Nullable List<String> targetEnablingResources) {
    this.targetEnablingResources = targetEnablingResources;
    return this;
  }

  public GCTGeneDto addTargetEnablingResourcesItem(String targetEnablingResourcesItem) {
    if (this.targetEnablingResources == null) {
      this.targetEnablingResources = new ArrayList<>();
    }
    this.targetEnablingResources.add(targetEnablingResourcesItem);
    return this;
  }

  /**
   * Target enabling resources
   * @return targetEnablingResources
   */
  
  @Schema(name = "target_enabling_resources", description = "Target enabling resources", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("target_enabling_resources")
  public @Nullable List<String> getTargetEnablingResources() {
    return targetEnablingResources;
  }

  public void setTargetEnablingResources(@Nullable List<String> targetEnablingResources) {
    this.targetEnablingResources = targetEnablingResources;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GCTGeneDto gcTGene = (GCTGeneDto) o;
    return Objects.equals(this.ensemblGeneId, gcTGene.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, gcTGene.hgncSymbol) &&
        Objects.equals(this.uniprotid, gcTGene.uniprotid) &&
        Objects.equals(this.uid, gcTGene.uid) &&
        Objects.equals(this.searchString, gcTGene.searchString) &&
        Objects.equals(this.searchArray, gcTGene.searchArray) &&
        Objects.equals(this.tissues, gcTGene.tissues) &&
        Objects.equals(this.nominations, gcTGene.nominations) &&
        Objects.equals(this.associations, gcTGene.associations) &&
        Objects.equals(this.targetRiskScore, gcTGene.targetRiskScore) &&
        Objects.equals(this.geneticsScore, gcTGene.geneticsScore) &&
        Objects.equals(this.multiOmicsScore, gcTGene.multiOmicsScore) &&
        Objects.equals(this.biodomains, gcTGene.biodomains) &&
        Objects.equals(this.pinned, gcTGene.pinned) &&
        Objects.equals(this.targetEnablingResources, gcTGene.targetEnablingResources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, hgncSymbol, uniprotid, uid, searchString, searchArray, tissues, nominations, associations, targetRiskScore, geneticsScore, multiOmicsScore, biodomains, pinned, targetEnablingResources);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GCTGeneDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    uniprotid: ").append(toIndentedString(uniprotid)).append("\n");
    sb.append("    uid: ").append(toIndentedString(uid)).append("\n");
    sb.append("    searchString: ").append(toIndentedString(searchString)).append("\n");
    sb.append("    searchArray: ").append(toIndentedString(searchArray)).append("\n");
    sb.append("    tissues: ").append(toIndentedString(tissues)).append("\n");
    sb.append("    nominations: ").append(toIndentedString(nominations)).append("\n");
    sb.append("    associations: ").append(toIndentedString(associations)).append("\n");
    sb.append("    targetRiskScore: ").append(toIndentedString(targetRiskScore)).append("\n");
    sb.append("    geneticsScore: ").append(toIndentedString(geneticsScore)).append("\n");
    sb.append("    multiOmicsScore: ").append(toIndentedString(multiOmicsScore)).append("\n");
    sb.append("    biodomains: ").append(toIndentedString(biodomains)).append("\n");
    sb.append("    pinned: ").append(toIndentedString(pinned)).append("\n");
    sb.append("    targetEnablingResources: ").append(toIndentedString(targetEnablingResources)).append("\n");
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

    private GCTGeneDto instance;

    public Builder() {
      this(new GCTGeneDto());
    }

    protected Builder(GCTGeneDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GCTGeneDto value) { 
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      this.instance.setUniprotid(value.uniprotid);
      this.instance.setUid(value.uid);
      this.instance.setSearchString(value.searchString);
      this.instance.setSearchArray(value.searchArray);
      this.instance.setTissues(value.tissues);
      this.instance.setNominations(value.nominations);
      this.instance.setAssociations(value.associations);
      this.instance.setTargetRiskScore(value.targetRiskScore);
      this.instance.setGeneticsScore(value.geneticsScore);
      this.instance.setMultiOmicsScore(value.multiOmicsScore);
      this.instance.setBiodomains(value.biodomains);
      this.instance.setPinned(value.pinned);
      this.instance.setTargetEnablingResources(value.targetEnablingResources);
      return this;
    }

    public GCTGeneDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public GCTGeneDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    public GCTGeneDto.Builder uniprotid(String uniprotid) {
      this.instance.uniprotid(uniprotid);
      return this;
    }
    
    public GCTGeneDto.Builder uid(String uid) {
      this.instance.uid(uid);
      return this;
    }
    
    public GCTGeneDto.Builder searchString(String searchString) {
      this.instance.searchString(searchString);
      return this;
    }
    
    public GCTGeneDto.Builder searchArray(List<String> searchArray) {
      this.instance.searchArray(searchArray);
      return this;
    }
    
    public GCTGeneDto.Builder tissues(List<GCTGeneTissueDto> tissues) {
      this.instance.tissues(tissues);
      return this;
    }
    
    public GCTGeneDto.Builder nominations(GCTGeneNominationsDto nominations) {
      this.instance.nominations(nominations);
      return this;
    }
    
    public GCTGeneDto.Builder associations(List<BigDecimal> associations) {
      this.instance.associations(associations);
      return this;
    }
    
    public GCTGeneDto.Builder targetRiskScore(BigDecimal targetRiskScore) {
      this.instance.targetRiskScore(targetRiskScore);
      return this;
    }
    
    public GCTGeneDto.Builder geneticsScore(BigDecimal geneticsScore) {
      this.instance.geneticsScore(geneticsScore);
      return this;
    }
    
    public GCTGeneDto.Builder multiOmicsScore(BigDecimal multiOmicsScore) {
      this.instance.multiOmicsScore(multiOmicsScore);
      return this;
    }
    
    public GCTGeneDto.Builder biodomains(List<String> biodomains) {
      this.instance.biodomains(biodomains);
      return this;
    }
    
    public GCTGeneDto.Builder pinned(Boolean pinned) {
      this.instance.pinned(pinned);
      return this;
    }
    
    public GCTGeneDto.Builder targetEnablingResources(List<String> targetEnablingResources) {
      this.instance.targetEnablingResources(targetEnablingResources);
      return this;
    }
    
    /**
    * returns a built GCTGeneDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GCTGeneDto build() {
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
  public static GCTGeneDto.Builder builder() {
    return new GCTGeneDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GCTGeneDto.Builder toBuilder() {
    GCTGeneDto.Builder builder = new GCTGeneDto.Builder();
    return builder.copyOf(this);
  }

}

