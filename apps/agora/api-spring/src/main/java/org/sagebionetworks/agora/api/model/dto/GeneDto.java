package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sagebionetworks.agora.api.model.dto.BioDomainsDto;
import org.sagebionetworks.agora.api.model.dto.DruggabilityDto;
import org.sagebionetworks.agora.api.model.dto.EnsemblInfoDto;
import org.sagebionetworks.agora.api.model.dto.ExperimentalValidationDto;
import org.sagebionetworks.agora.api.model.dto.MedianExpressionDto;
import org.sagebionetworks.agora.api.model.dto.NeuropathologicCorrelationDto;
import org.sagebionetworks.agora.api.model.dto.OverallScoresDto;
import org.sagebionetworks.agora.api.model.dto.ProteinDifferentialExpressionDto;
import org.sagebionetworks.agora.api.model.dto.RnaDifferentialExpressionDto;
import org.sagebionetworks.agora.api.model.dto.SimilarGenesNetworkDto;
import org.sagebionetworks.agora.api.model.dto.TargetNominationDto;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Gene
 */

@Schema(name = "Gene", description = "Gene")
@JsonTypeName("Gene")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class GeneDto {

  private String id;

  private String ensemblGeneId;

  private String name;

  private String summary;

  private String hgncSymbol;

  @Valid
  private List<String> alias = new ArrayList<>();

  private Boolean isIgap;

  private Boolean isEqtl;

  private Boolean isAnyRnaChangedInAdBrain;

  private Boolean rnaBrainChangeStudied;

  private Boolean isAnyProteinChangedInAdBrain;

  private Boolean proteinBrainChangeStudied;

  @Valid
  private List<@Valid TargetNominationDto> targetNominations;

  @Valid
  private List<@Valid MedianExpressionDto> medianExpression = new ArrayList<>();

  @Valid
  private List<@Valid DruggabilityDto> druggability = new ArrayList<>();

  private Integer totalNominations = null;

  private Boolean isAdi;

  private Boolean isTep;

  private String resourceUrl = null;

  @Valid
  private List<@Valid RnaDifferentialExpressionDto> rnaDifferentialExpression;

  @Valid
  private List<@Valid ProteinDifferentialExpressionDto> proteomicsLFQ;

  @Valid
  private List<@Valid ProteinDifferentialExpressionDto> proteomicsSRM;

  @Valid
  private List<@Valid ProteinDifferentialExpressionDto> proteomicsTMT;

  @Valid
  private Map<String, Object> metabolomics;

  private OverallScoresDto overallScores;

  @Valid
  private List<@Valid NeuropathologicCorrelationDto> neuropathologicCorrelations;

  @Valid
  private List<@Valid ExperimentalValidationDto> experimentalValidation;

  @Valid
  private Map<String, Object> links;

  private SimilarGenesNetworkDto similarGenesNetwork;

  private String abModalityDisplayValue = null;

  private String safetyRatingDisplayValue = null;

  private String smDruggabilityDisplayValue = null;

  private String pharosClassDisplayValue = null;

  private String isAnyRnaChangedInAdBrainDisplayValue = null;

  private String isAnyProteinChangedInAdBrainDisplayValue = null;

  private Boolean nominatedTargetDisplayValue = null;

  private Integer initialNominationDisplayValue = null;

  private String teamsDisplayValue = null;

  private String studyDisplayValue = null;

  private String programsDisplayValue = null;

  private String inputDataDisplayValue = null;

  private BioDomainsDto bioDomains;

  private EnsemblInfoDto ensemblInfo;

  public GeneDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneDto(String id, String ensemblGeneId, String name, String summary, String hgncSymbol, List<String> alias, Boolean isIgap, Boolean isEqtl, Boolean isAnyRnaChangedInAdBrain, Boolean rnaBrainChangeStudied, Boolean isAnyProteinChangedInAdBrain, Boolean proteinBrainChangeStudied, List<@Valid TargetNominationDto> targetNominations, List<@Valid MedianExpressionDto> medianExpression, List<@Valid DruggabilityDto> druggability, Integer totalNominations, EnsemblInfoDto ensemblInfo) {
    this.id = id;
    this.ensemblGeneId = ensemblGeneId;
    this.name = name;
    this.summary = summary;
    this.hgncSymbol = hgncSymbol;
    this.alias = alias;
    this.isIgap = isIgap;
    this.isEqtl = isEqtl;
    this.isAnyRnaChangedInAdBrain = isAnyRnaChangedInAdBrain;
    this.rnaBrainChangeStudied = rnaBrainChangeStudied;
    this.isAnyProteinChangedInAdBrain = isAnyProteinChangedInAdBrain;
    this.proteinBrainChangeStudied = proteinBrainChangeStudied;
    this.targetNominations = targetNominations;
    this.medianExpression = medianExpression;
    this.druggability = druggability;
    this.totalNominations = totalNominations;
    this.ensemblInfo = ensemblInfo;
  }

  public GeneDto id(String id) {
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

  public GeneDto ensemblGeneId(String ensemblGeneId) {
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

  public GeneDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GeneDto summary(String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * Get summary
   * @return summary
  */
  @NotNull 
  @Schema(name = "summary", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("summary")
  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public GeneDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * Get hgncSymbol
   * @return hgncSymbol
  */
  @NotNull 
  @Schema(name = "hgnc_symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  public GeneDto alias(List<String> alias) {
    this.alias = alias;
    return this;
  }

  public GeneDto addAliasItem(String aliasItem) {
    if (this.alias == null) {
      this.alias = new ArrayList<>();
    }
    this.alias.add(aliasItem);
    return this;
  }

  /**
   * Get alias
   * @return alias
  */
  @NotNull 
  @Schema(name = "alias", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("alias")
  public List<String> getAlias() {
    return alias;
  }

  public void setAlias(List<String> alias) {
    this.alias = alias;
  }

  public GeneDto isIgap(Boolean isIgap) {
    this.isIgap = isIgap;
    return this;
  }

  /**
   * Get isIgap
   * @return isIgap
  */
  @NotNull 
  @Schema(name = "is_igap", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("is_igap")
  public Boolean getIsIgap() {
    return isIgap;
  }

  public void setIsIgap(Boolean isIgap) {
    this.isIgap = isIgap;
  }

  public GeneDto isEqtl(Boolean isEqtl) {
    this.isEqtl = isEqtl;
    return this;
  }

  /**
   * Get isEqtl
   * @return isEqtl
  */
  @NotNull 
  @Schema(name = "is_eqtl", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("is_eqtl")
  public Boolean getIsEqtl() {
    return isEqtl;
  }

  public void setIsEqtl(Boolean isEqtl) {
    this.isEqtl = isEqtl;
  }

  public GeneDto isAnyRnaChangedInAdBrain(Boolean isAnyRnaChangedInAdBrain) {
    this.isAnyRnaChangedInAdBrain = isAnyRnaChangedInAdBrain;
    return this;
  }

  /**
   * Get isAnyRnaChangedInAdBrain
   * @return isAnyRnaChangedInAdBrain
  */
  @NotNull 
  @Schema(name = "is_any_rna_changed_in_ad_brain", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("is_any_rna_changed_in_ad_brain")
  public Boolean getIsAnyRnaChangedInAdBrain() {
    return isAnyRnaChangedInAdBrain;
  }

  public void setIsAnyRnaChangedInAdBrain(Boolean isAnyRnaChangedInAdBrain) {
    this.isAnyRnaChangedInAdBrain = isAnyRnaChangedInAdBrain;
  }

  public GeneDto rnaBrainChangeStudied(Boolean rnaBrainChangeStudied) {
    this.rnaBrainChangeStudied = rnaBrainChangeStudied;
    return this;
  }

  /**
   * Get rnaBrainChangeStudied
   * @return rnaBrainChangeStudied
  */
  @NotNull 
  @Schema(name = "rna_brain_change_studied", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rna_brain_change_studied")
  public Boolean getRnaBrainChangeStudied() {
    return rnaBrainChangeStudied;
  }

  public void setRnaBrainChangeStudied(Boolean rnaBrainChangeStudied) {
    this.rnaBrainChangeStudied = rnaBrainChangeStudied;
  }

  public GeneDto isAnyProteinChangedInAdBrain(Boolean isAnyProteinChangedInAdBrain) {
    this.isAnyProteinChangedInAdBrain = isAnyProteinChangedInAdBrain;
    return this;
  }

  /**
   * Get isAnyProteinChangedInAdBrain
   * @return isAnyProteinChangedInAdBrain
  */
  @NotNull 
  @Schema(name = "is_any_protein_changed_in_ad_brain", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("is_any_protein_changed_in_ad_brain")
  public Boolean getIsAnyProteinChangedInAdBrain() {
    return isAnyProteinChangedInAdBrain;
  }

  public void setIsAnyProteinChangedInAdBrain(Boolean isAnyProteinChangedInAdBrain) {
    this.isAnyProteinChangedInAdBrain = isAnyProteinChangedInAdBrain;
  }

  public GeneDto proteinBrainChangeStudied(Boolean proteinBrainChangeStudied) {
    this.proteinBrainChangeStudied = proteinBrainChangeStudied;
    return this;
  }

  /**
   * Get proteinBrainChangeStudied
   * @return proteinBrainChangeStudied
  */
  @NotNull 
  @Schema(name = "protein_brain_change_studied", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("protein_brain_change_studied")
  public Boolean getProteinBrainChangeStudied() {
    return proteinBrainChangeStudied;
  }

  public void setProteinBrainChangeStudied(Boolean proteinBrainChangeStudied) {
    this.proteinBrainChangeStudied = proteinBrainChangeStudied;
  }

  public GeneDto targetNominations(List<@Valid TargetNominationDto> targetNominations) {
    this.targetNominations = targetNominations;
    return this;
  }

  public GeneDto addTargetNominationsItem(TargetNominationDto targetNominationsItem) {
    if (this.targetNominations == null) {
      this.targetNominations = new ArrayList<>();
    }
    this.targetNominations.add(targetNominationsItem);
    return this;
  }

  /**
   * Get targetNominations
   * @return targetNominations
  */
  @NotNull @Valid 
  @Schema(name = "target_nominations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("target_nominations")
  public List<@Valid TargetNominationDto> getTargetNominations() {
    return targetNominations;
  }

  public void setTargetNominations(List<@Valid TargetNominationDto> targetNominations) {
    this.targetNominations = targetNominations;
  }

  public GeneDto medianExpression(List<@Valid MedianExpressionDto> medianExpression) {
    this.medianExpression = medianExpression;
    return this;
  }

  public GeneDto addMedianExpressionItem(MedianExpressionDto medianExpressionItem) {
    if (this.medianExpression == null) {
      this.medianExpression = new ArrayList<>();
    }
    this.medianExpression.add(medianExpressionItem);
    return this;
  }

  /**
   * Get medianExpression
   * @return medianExpression
  */
  @NotNull @Valid 
  @Schema(name = "median_expression", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("median_expression")
  public List<@Valid MedianExpressionDto> getMedianExpression() {
    return medianExpression;
  }

  public void setMedianExpression(List<@Valid MedianExpressionDto> medianExpression) {
    this.medianExpression = medianExpression;
  }

  public GeneDto druggability(List<@Valid DruggabilityDto> druggability) {
    this.druggability = druggability;
    return this;
  }

  public GeneDto addDruggabilityItem(DruggabilityDto druggabilityItem) {
    if (this.druggability == null) {
      this.druggability = new ArrayList<>();
    }
    this.druggability.add(druggabilityItem);
    return this;
  }

  /**
   * Get druggability
   * @return druggability
  */
  @NotNull @Valid 
  @Schema(name = "druggability", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("druggability")
  public List<@Valid DruggabilityDto> getDruggability() {
    return druggability;
  }

  public void setDruggability(List<@Valid DruggabilityDto> druggability) {
    this.druggability = druggability;
  }

  public GeneDto totalNominations(Integer totalNominations) {
    this.totalNominations = totalNominations;
    return this;
  }

  /**
   * Get totalNominations
   * @return totalNominations
  */
  @NotNull 
  @Schema(name = "total_nominations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total_nominations")
  public Integer getTotalNominations() {
    return totalNominations;
  }

  public void setTotalNominations(Integer totalNominations) {
    this.totalNominations = totalNominations;
  }

  public GeneDto isAdi(Boolean isAdi) {
    this.isAdi = isAdi;
    return this;
  }

  /**
   * Get isAdi
   * @return isAdi
  */
  
  @Schema(name = "is_adi", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_adi")
  public Boolean getIsAdi() {
    return isAdi;
  }

  public void setIsAdi(Boolean isAdi) {
    this.isAdi = isAdi;
  }

  public GeneDto isTep(Boolean isTep) {
    this.isTep = isTep;
    return this;
  }

  /**
   * Get isTep
   * @return isTep
  */
  
  @Schema(name = "is_tep", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_tep")
  public Boolean getIsTep() {
    return isTep;
  }

  public void setIsTep(Boolean isTep) {
    this.isTep = isTep;
  }

  public GeneDto resourceUrl(String resourceUrl) {
    this.resourceUrl = resourceUrl;
    return this;
  }

  /**
   * Get resourceUrl
   * @return resourceUrl
  */
  
  @Schema(name = "resource_url", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resource_url")
  public String getResourceUrl() {
    return resourceUrl;
  }

  public void setResourceUrl(String resourceUrl) {
    this.resourceUrl = resourceUrl;
  }

  public GeneDto rnaDifferentialExpression(List<@Valid RnaDifferentialExpressionDto> rnaDifferentialExpression) {
    this.rnaDifferentialExpression = rnaDifferentialExpression;
    return this;
  }

  public GeneDto addRnaDifferentialExpressionItem(RnaDifferentialExpressionDto rnaDifferentialExpressionItem) {
    if (this.rnaDifferentialExpression == null) {
      this.rnaDifferentialExpression = new ArrayList<>();
    }
    this.rnaDifferentialExpression.add(rnaDifferentialExpressionItem);
    return this;
  }

  /**
   * Get rnaDifferentialExpression
   * @return rnaDifferentialExpression
  */
  @Valid 
  @Schema(name = "rna_differential_expression", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rna_differential_expression")
  public List<@Valid RnaDifferentialExpressionDto> getRnaDifferentialExpression() {
    return rnaDifferentialExpression;
  }

  public void setRnaDifferentialExpression(List<@Valid RnaDifferentialExpressionDto> rnaDifferentialExpression) {
    this.rnaDifferentialExpression = rnaDifferentialExpression;
  }

  public GeneDto proteomicsLFQ(List<@Valid ProteinDifferentialExpressionDto> proteomicsLFQ) {
    this.proteomicsLFQ = proteomicsLFQ;
    return this;
  }

  public GeneDto addProteomicsLFQItem(ProteinDifferentialExpressionDto proteomicsLFQItem) {
    if (this.proteomicsLFQ == null) {
      this.proteomicsLFQ = new ArrayList<>();
    }
    this.proteomicsLFQ.add(proteomicsLFQItem);
    return this;
  }

  /**
   * Get proteomicsLFQ
   * @return proteomicsLFQ
  */
  @Valid 
  @Schema(name = "proteomics_LFQ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("proteomics_LFQ")
  public List<@Valid ProteinDifferentialExpressionDto> getProteomicsLFQ() {
    return proteomicsLFQ;
  }

  public void setProteomicsLFQ(List<@Valid ProteinDifferentialExpressionDto> proteomicsLFQ) {
    this.proteomicsLFQ = proteomicsLFQ;
  }

  public GeneDto proteomicsSRM(List<@Valid ProteinDifferentialExpressionDto> proteomicsSRM) {
    this.proteomicsSRM = proteomicsSRM;
    return this;
  }

  public GeneDto addProteomicsSRMItem(ProteinDifferentialExpressionDto proteomicsSRMItem) {
    if (this.proteomicsSRM == null) {
      this.proteomicsSRM = new ArrayList<>();
    }
    this.proteomicsSRM.add(proteomicsSRMItem);
    return this;
  }

  /**
   * Get proteomicsSRM
   * @return proteomicsSRM
  */
  @Valid 
  @Schema(name = "proteomics_SRM", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("proteomics_SRM")
  public List<@Valid ProteinDifferentialExpressionDto> getProteomicsSRM() {
    return proteomicsSRM;
  }

  public void setProteomicsSRM(List<@Valid ProteinDifferentialExpressionDto> proteomicsSRM) {
    this.proteomicsSRM = proteomicsSRM;
  }

  public GeneDto proteomicsTMT(List<@Valid ProteinDifferentialExpressionDto> proteomicsTMT) {
    this.proteomicsTMT = proteomicsTMT;
    return this;
  }

  public GeneDto addProteomicsTMTItem(ProteinDifferentialExpressionDto proteomicsTMTItem) {
    if (this.proteomicsTMT == null) {
      this.proteomicsTMT = new ArrayList<>();
    }
    this.proteomicsTMT.add(proteomicsTMTItem);
    return this;
  }

  /**
   * Get proteomicsTMT
   * @return proteomicsTMT
  */
  @Valid 
  @Schema(name = "proteomics_TMT", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("proteomics_TMT")
  public List<@Valid ProteinDifferentialExpressionDto> getProteomicsTMT() {
    return proteomicsTMT;
  }

  public void setProteomicsTMT(List<@Valid ProteinDifferentialExpressionDto> proteomicsTMT) {
    this.proteomicsTMT = proteomicsTMT;
  }

  public GeneDto metabolomics(Map<String, Object> metabolomics) {
    this.metabolomics = metabolomics;
    return this;
  }

  public GeneDto putMetabolomicsItem(String key, Object metabolomicsItem) {
    if (this.metabolomics == null) {
      this.metabolomics = new HashMap<>();
    }
    this.metabolomics.put(key, metabolomicsItem);
    return this;
  }

  /**
   * Get metabolomics
   * @return metabolomics
  */
  
  @Schema(name = "metabolomics", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("metabolomics")
  public Map<String, Object> getMetabolomics() {
    return metabolomics;
  }

  public void setMetabolomics(Map<String, Object> metabolomics) {
    this.metabolomics = metabolomics;
  }

  public GeneDto overallScores(OverallScoresDto overallScores) {
    this.overallScores = overallScores;
    return this;
  }

  /**
   * Get overallScores
   * @return overallScores
  */
  @Valid 
  @Schema(name = "overall_scores", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("overall_scores")
  public OverallScoresDto getOverallScores() {
    return overallScores;
  }

  public void setOverallScores(OverallScoresDto overallScores) {
    this.overallScores = overallScores;
  }

  public GeneDto neuropathologicCorrelations(List<@Valid NeuropathologicCorrelationDto> neuropathologicCorrelations) {
    this.neuropathologicCorrelations = neuropathologicCorrelations;
    return this;
  }

  public GeneDto addNeuropathologicCorrelationsItem(NeuropathologicCorrelationDto neuropathologicCorrelationsItem) {
    if (this.neuropathologicCorrelations == null) {
      this.neuropathologicCorrelations = new ArrayList<>();
    }
    this.neuropathologicCorrelations.add(neuropathologicCorrelationsItem);
    return this;
  }

  /**
   * Get neuropathologicCorrelations
   * @return neuropathologicCorrelations
  */
  @Valid 
  @Schema(name = "neuropathologic_correlations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("neuropathologic_correlations")
  public List<@Valid NeuropathologicCorrelationDto> getNeuropathologicCorrelations() {
    return neuropathologicCorrelations;
  }

  public void setNeuropathologicCorrelations(List<@Valid NeuropathologicCorrelationDto> neuropathologicCorrelations) {
    this.neuropathologicCorrelations = neuropathologicCorrelations;
  }

  public GeneDto experimentalValidation(List<@Valid ExperimentalValidationDto> experimentalValidation) {
    this.experimentalValidation = experimentalValidation;
    return this;
  }

  public GeneDto addExperimentalValidationItem(ExperimentalValidationDto experimentalValidationItem) {
    if (this.experimentalValidation == null) {
      this.experimentalValidation = new ArrayList<>();
    }
    this.experimentalValidation.add(experimentalValidationItem);
    return this;
  }

  /**
   * Get experimentalValidation
   * @return experimentalValidation
  */
  @Valid 
  @Schema(name = "experimental_validation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("experimental_validation")
  public List<@Valid ExperimentalValidationDto> getExperimentalValidation() {
    return experimentalValidation;
  }

  public void setExperimentalValidation(List<@Valid ExperimentalValidationDto> experimentalValidation) {
    this.experimentalValidation = experimentalValidation;
  }

  public GeneDto links(Map<String, Object> links) {
    this.links = links;
    return this;
  }

  public GeneDto putLinksItem(String key, Object linksItem) {
    if (this.links == null) {
      this.links = new HashMap<>();
    }
    this.links.put(key, linksItem);
    return this;
  }

  /**
   * Get links
   * @return links
  */
  
  @Schema(name = "links", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("links")
  public Map<String, Object> getLinks() {
    return links;
  }

  public void setLinks(Map<String, Object> links) {
    this.links = links;
  }

  public GeneDto similarGenesNetwork(SimilarGenesNetworkDto similarGenesNetwork) {
    this.similarGenesNetwork = similarGenesNetwork;
    return this;
  }

  /**
   * Get similarGenesNetwork
   * @return similarGenesNetwork
  */
  @Valid 
  @Schema(name = "similar_genes_network", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("similar_genes_network")
  public SimilarGenesNetworkDto getSimilarGenesNetwork() {
    return similarGenesNetwork;
  }

  public void setSimilarGenesNetwork(SimilarGenesNetworkDto similarGenesNetwork) {
    this.similarGenesNetwork = similarGenesNetwork;
  }

  public GeneDto abModalityDisplayValue(String abModalityDisplayValue) {
    this.abModalityDisplayValue = abModalityDisplayValue;
    return this;
  }

  /**
   * Get abModalityDisplayValue
   * @return abModalityDisplayValue
  */
  
  @Schema(name = "ab_modality_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ab_modality_display_value")
  public String getAbModalityDisplayValue() {
    return abModalityDisplayValue;
  }

  public void setAbModalityDisplayValue(String abModalityDisplayValue) {
    this.abModalityDisplayValue = abModalityDisplayValue;
  }

  public GeneDto safetyRatingDisplayValue(String safetyRatingDisplayValue) {
    this.safetyRatingDisplayValue = safetyRatingDisplayValue;
    return this;
  }

  /**
   * Get safetyRatingDisplayValue
   * @return safetyRatingDisplayValue
  */
  
  @Schema(name = "safety_rating_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("safety_rating_display_value")
  public String getSafetyRatingDisplayValue() {
    return safetyRatingDisplayValue;
  }

  public void setSafetyRatingDisplayValue(String safetyRatingDisplayValue) {
    this.safetyRatingDisplayValue = safetyRatingDisplayValue;
  }

  public GeneDto smDruggabilityDisplayValue(String smDruggabilityDisplayValue) {
    this.smDruggabilityDisplayValue = smDruggabilityDisplayValue;
    return this;
  }

  /**
   * Get smDruggabilityDisplayValue
   * @return smDruggabilityDisplayValue
  */
  
  @Schema(name = "sm_druggability_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sm_druggability_display_value")
  public String getSmDruggabilityDisplayValue() {
    return smDruggabilityDisplayValue;
  }

  public void setSmDruggabilityDisplayValue(String smDruggabilityDisplayValue) {
    this.smDruggabilityDisplayValue = smDruggabilityDisplayValue;
  }

  public GeneDto pharosClassDisplayValue(String pharosClassDisplayValue) {
    this.pharosClassDisplayValue = pharosClassDisplayValue;
    return this;
  }

  /**
   * Get pharosClassDisplayValue
   * @return pharosClassDisplayValue
  */
  
  @Schema(name = "pharos_class_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pharos_class_display_value")
  public String getPharosClassDisplayValue() {
    return pharosClassDisplayValue;
  }

  public void setPharosClassDisplayValue(String pharosClassDisplayValue) {
    this.pharosClassDisplayValue = pharosClassDisplayValue;
  }

  public GeneDto isAnyRnaChangedInAdBrainDisplayValue(String isAnyRnaChangedInAdBrainDisplayValue) {
    this.isAnyRnaChangedInAdBrainDisplayValue = isAnyRnaChangedInAdBrainDisplayValue;
    return this;
  }

  /**
   * Get isAnyRnaChangedInAdBrainDisplayValue
   * @return isAnyRnaChangedInAdBrainDisplayValue
  */
  
  @Schema(name = "is_any_rna_changed_in_ad_brain_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_any_rna_changed_in_ad_brain_display_value")
  public String getIsAnyRnaChangedInAdBrainDisplayValue() {
    return isAnyRnaChangedInAdBrainDisplayValue;
  }

  public void setIsAnyRnaChangedInAdBrainDisplayValue(String isAnyRnaChangedInAdBrainDisplayValue) {
    this.isAnyRnaChangedInAdBrainDisplayValue = isAnyRnaChangedInAdBrainDisplayValue;
  }

  public GeneDto isAnyProteinChangedInAdBrainDisplayValue(String isAnyProteinChangedInAdBrainDisplayValue) {
    this.isAnyProteinChangedInAdBrainDisplayValue = isAnyProteinChangedInAdBrainDisplayValue;
    return this;
  }

  /**
   * Get isAnyProteinChangedInAdBrainDisplayValue
   * @return isAnyProteinChangedInAdBrainDisplayValue
  */
  
  @Schema(name = "is_any_protein_changed_in_ad_brain_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_any_protein_changed_in_ad_brain_display_value")
  public String getIsAnyProteinChangedInAdBrainDisplayValue() {
    return isAnyProteinChangedInAdBrainDisplayValue;
  }

  public void setIsAnyProteinChangedInAdBrainDisplayValue(String isAnyProteinChangedInAdBrainDisplayValue) {
    this.isAnyProteinChangedInAdBrainDisplayValue = isAnyProteinChangedInAdBrainDisplayValue;
  }

  public GeneDto nominatedTargetDisplayValue(Boolean nominatedTargetDisplayValue) {
    this.nominatedTargetDisplayValue = nominatedTargetDisplayValue;
    return this;
  }

  /**
   * Get nominatedTargetDisplayValue
   * @return nominatedTargetDisplayValue
  */
  
  @Schema(name = "nominated_target_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nominated_target_display_value")
  public Boolean getNominatedTargetDisplayValue() {
    return nominatedTargetDisplayValue;
  }

  public void setNominatedTargetDisplayValue(Boolean nominatedTargetDisplayValue) {
    this.nominatedTargetDisplayValue = nominatedTargetDisplayValue;
  }

  public GeneDto initialNominationDisplayValue(Integer initialNominationDisplayValue) {
    this.initialNominationDisplayValue = initialNominationDisplayValue;
    return this;
  }

  /**
   * Get initialNominationDisplayValue
   * @return initialNominationDisplayValue
  */
  
  @Schema(name = "initial_nomination_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("initial_nomination_display_value")
  public Integer getInitialNominationDisplayValue() {
    return initialNominationDisplayValue;
  }

  public void setInitialNominationDisplayValue(Integer initialNominationDisplayValue) {
    this.initialNominationDisplayValue = initialNominationDisplayValue;
  }

  public GeneDto teamsDisplayValue(String teamsDisplayValue) {
    this.teamsDisplayValue = teamsDisplayValue;
    return this;
  }

  /**
   * Get teamsDisplayValue
   * @return teamsDisplayValue
  */
  
  @Schema(name = "teams_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("teams_display_value")
  public String getTeamsDisplayValue() {
    return teamsDisplayValue;
  }

  public void setTeamsDisplayValue(String teamsDisplayValue) {
    this.teamsDisplayValue = teamsDisplayValue;
  }

  public GeneDto studyDisplayValue(String studyDisplayValue) {
    this.studyDisplayValue = studyDisplayValue;
    return this;
  }

  /**
   * Get studyDisplayValue
   * @return studyDisplayValue
  */
  
  @Schema(name = "study_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("study_display_value")
  public String getStudyDisplayValue() {
    return studyDisplayValue;
  }

  public void setStudyDisplayValue(String studyDisplayValue) {
    this.studyDisplayValue = studyDisplayValue;
  }

  public GeneDto programsDisplayValue(String programsDisplayValue) {
    this.programsDisplayValue = programsDisplayValue;
    return this;
  }

  /**
   * Get programsDisplayValue
   * @return programsDisplayValue
  */
  
  @Schema(name = "programs_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("programs_display_value")
  public String getProgramsDisplayValue() {
    return programsDisplayValue;
  }

  public void setProgramsDisplayValue(String programsDisplayValue) {
    this.programsDisplayValue = programsDisplayValue;
  }

  public GeneDto inputDataDisplayValue(String inputDataDisplayValue) {
    this.inputDataDisplayValue = inputDataDisplayValue;
    return this;
  }

  /**
   * Get inputDataDisplayValue
   * @return inputDataDisplayValue
  */
  
  @Schema(name = "input_data_display_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("input_data_display_value")
  public String getInputDataDisplayValue() {
    return inputDataDisplayValue;
  }

  public void setInputDataDisplayValue(String inputDataDisplayValue) {
    this.inputDataDisplayValue = inputDataDisplayValue;
  }

  public GeneDto bioDomains(BioDomainsDto bioDomains) {
    this.bioDomains = bioDomains;
    return this;
  }

  /**
   * Get bioDomains
   * @return bioDomains
  */
  @Valid 
  @Schema(name = "bio_domains", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bio_domains")
  public BioDomainsDto getBioDomains() {
    return bioDomains;
  }

  public void setBioDomains(BioDomainsDto bioDomains) {
    this.bioDomains = bioDomains;
  }

  public GeneDto ensemblInfo(EnsemblInfoDto ensemblInfo) {
    this.ensemblInfo = ensemblInfo;
    return this;
  }

  /**
   * Get ensemblInfo
   * @return ensemblInfo
  */
  @NotNull @Valid 
  @Schema(name = "ensembl_info", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_info")
  public EnsemblInfoDto getEnsemblInfo() {
    return ensemblInfo;
  }

  public void setEnsemblInfo(EnsemblInfoDto ensemblInfo) {
    this.ensemblInfo = ensemblInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeneDto gene = (GeneDto) o;
    return Objects.equals(this.id, gene.id) &&
        Objects.equals(this.ensemblGeneId, gene.ensemblGeneId) &&
        Objects.equals(this.name, gene.name) &&
        Objects.equals(this.summary, gene.summary) &&
        Objects.equals(this.hgncSymbol, gene.hgncSymbol) &&
        Objects.equals(this.alias, gene.alias) &&
        Objects.equals(this.isIgap, gene.isIgap) &&
        Objects.equals(this.isEqtl, gene.isEqtl) &&
        Objects.equals(this.isAnyRnaChangedInAdBrain, gene.isAnyRnaChangedInAdBrain) &&
        Objects.equals(this.rnaBrainChangeStudied, gene.rnaBrainChangeStudied) &&
        Objects.equals(this.isAnyProteinChangedInAdBrain, gene.isAnyProteinChangedInAdBrain) &&
        Objects.equals(this.proteinBrainChangeStudied, gene.proteinBrainChangeStudied) &&
        Objects.equals(this.targetNominations, gene.targetNominations) &&
        Objects.equals(this.medianExpression, gene.medianExpression) &&
        Objects.equals(this.druggability, gene.druggability) &&
        Objects.equals(this.totalNominations, gene.totalNominations) &&
        Objects.equals(this.isAdi, gene.isAdi) &&
        Objects.equals(this.isTep, gene.isTep) &&
        Objects.equals(this.resourceUrl, gene.resourceUrl) &&
        Objects.equals(this.rnaDifferentialExpression, gene.rnaDifferentialExpression) &&
        Objects.equals(this.proteomicsLFQ, gene.proteomicsLFQ) &&
        Objects.equals(this.proteomicsSRM, gene.proteomicsSRM) &&
        Objects.equals(this.proteomicsTMT, gene.proteomicsTMT) &&
        Objects.equals(this.metabolomics, gene.metabolomics) &&
        Objects.equals(this.overallScores, gene.overallScores) &&
        Objects.equals(this.neuropathologicCorrelations, gene.neuropathologicCorrelations) &&
        Objects.equals(this.experimentalValidation, gene.experimentalValidation) &&
        Objects.equals(this.links, gene.links) &&
        Objects.equals(this.similarGenesNetwork, gene.similarGenesNetwork) &&
        Objects.equals(this.abModalityDisplayValue, gene.abModalityDisplayValue) &&
        Objects.equals(this.safetyRatingDisplayValue, gene.safetyRatingDisplayValue) &&
        Objects.equals(this.smDruggabilityDisplayValue, gene.smDruggabilityDisplayValue) &&
        Objects.equals(this.pharosClassDisplayValue, gene.pharosClassDisplayValue) &&
        Objects.equals(this.isAnyRnaChangedInAdBrainDisplayValue, gene.isAnyRnaChangedInAdBrainDisplayValue) &&
        Objects.equals(this.isAnyProteinChangedInAdBrainDisplayValue, gene.isAnyProteinChangedInAdBrainDisplayValue) &&
        Objects.equals(this.nominatedTargetDisplayValue, gene.nominatedTargetDisplayValue) &&
        Objects.equals(this.initialNominationDisplayValue, gene.initialNominationDisplayValue) &&
        Objects.equals(this.teamsDisplayValue, gene.teamsDisplayValue) &&
        Objects.equals(this.studyDisplayValue, gene.studyDisplayValue) &&
        Objects.equals(this.programsDisplayValue, gene.programsDisplayValue) &&
        Objects.equals(this.inputDataDisplayValue, gene.inputDataDisplayValue) &&
        Objects.equals(this.bioDomains, gene.bioDomains) &&
        Objects.equals(this.ensemblInfo, gene.ensemblInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ensemblGeneId, name, summary, hgncSymbol, alias, isIgap, isEqtl, isAnyRnaChangedInAdBrain, rnaBrainChangeStudied, isAnyProteinChangedInAdBrain, proteinBrainChangeStudied, targetNominations, medianExpression, druggability, totalNominations, isAdi, isTep, resourceUrl, rnaDifferentialExpression, proteomicsLFQ, proteomicsSRM, proteomicsTMT, metabolomics, overallScores, neuropathologicCorrelations, experimentalValidation, links, similarGenesNetwork, abModalityDisplayValue, safetyRatingDisplayValue, smDruggabilityDisplayValue, pharosClassDisplayValue, isAnyRnaChangedInAdBrainDisplayValue, isAnyProteinChangedInAdBrainDisplayValue, nominatedTargetDisplayValue, initialNominationDisplayValue, teamsDisplayValue, studyDisplayValue, programsDisplayValue, inputDataDisplayValue, bioDomains, ensemblInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
    sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
    sb.append("    isIgap: ").append(toIndentedString(isIgap)).append("\n");
    sb.append("    isEqtl: ").append(toIndentedString(isEqtl)).append("\n");
    sb.append("    isAnyRnaChangedInAdBrain: ").append(toIndentedString(isAnyRnaChangedInAdBrain)).append("\n");
    sb.append("    rnaBrainChangeStudied: ").append(toIndentedString(rnaBrainChangeStudied)).append("\n");
    sb.append("    isAnyProteinChangedInAdBrain: ").append(toIndentedString(isAnyProteinChangedInAdBrain)).append("\n");
    sb.append("    proteinBrainChangeStudied: ").append(toIndentedString(proteinBrainChangeStudied)).append("\n");
    sb.append("    targetNominations: ").append(toIndentedString(targetNominations)).append("\n");
    sb.append("    medianExpression: ").append(toIndentedString(medianExpression)).append("\n");
    sb.append("    druggability: ").append(toIndentedString(druggability)).append("\n");
    sb.append("    totalNominations: ").append(toIndentedString(totalNominations)).append("\n");
    sb.append("    isAdi: ").append(toIndentedString(isAdi)).append("\n");
    sb.append("    isTep: ").append(toIndentedString(isTep)).append("\n");
    sb.append("    resourceUrl: ").append(toIndentedString(resourceUrl)).append("\n");
    sb.append("    rnaDifferentialExpression: ").append(toIndentedString(rnaDifferentialExpression)).append("\n");
    sb.append("    proteomicsLFQ: ").append(toIndentedString(proteomicsLFQ)).append("\n");
    sb.append("    proteomicsSRM: ").append(toIndentedString(proteomicsSRM)).append("\n");
    sb.append("    proteomicsTMT: ").append(toIndentedString(proteomicsTMT)).append("\n");
    sb.append("    metabolomics: ").append(toIndentedString(metabolomics)).append("\n");
    sb.append("    overallScores: ").append(toIndentedString(overallScores)).append("\n");
    sb.append("    neuropathologicCorrelations: ").append(toIndentedString(neuropathologicCorrelations)).append("\n");
    sb.append("    experimentalValidation: ").append(toIndentedString(experimentalValidation)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    similarGenesNetwork: ").append(toIndentedString(similarGenesNetwork)).append("\n");
    sb.append("    abModalityDisplayValue: ").append(toIndentedString(abModalityDisplayValue)).append("\n");
    sb.append("    safetyRatingDisplayValue: ").append(toIndentedString(safetyRatingDisplayValue)).append("\n");
    sb.append("    smDruggabilityDisplayValue: ").append(toIndentedString(smDruggabilityDisplayValue)).append("\n");
    sb.append("    pharosClassDisplayValue: ").append(toIndentedString(pharosClassDisplayValue)).append("\n");
    sb.append("    isAnyRnaChangedInAdBrainDisplayValue: ").append(toIndentedString(isAnyRnaChangedInAdBrainDisplayValue)).append("\n");
    sb.append("    isAnyProteinChangedInAdBrainDisplayValue: ").append(toIndentedString(isAnyProteinChangedInAdBrainDisplayValue)).append("\n");
    sb.append("    nominatedTargetDisplayValue: ").append(toIndentedString(nominatedTargetDisplayValue)).append("\n");
    sb.append("    initialNominationDisplayValue: ").append(toIndentedString(initialNominationDisplayValue)).append("\n");
    sb.append("    teamsDisplayValue: ").append(toIndentedString(teamsDisplayValue)).append("\n");
    sb.append("    studyDisplayValue: ").append(toIndentedString(studyDisplayValue)).append("\n");
    sb.append("    programsDisplayValue: ").append(toIndentedString(programsDisplayValue)).append("\n");
    sb.append("    inputDataDisplayValue: ").append(toIndentedString(inputDataDisplayValue)).append("\n");
    sb.append("    bioDomains: ").append(toIndentedString(bioDomains)).append("\n");
    sb.append("    ensemblInfo: ").append(toIndentedString(ensemblInfo)).append("\n");
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

