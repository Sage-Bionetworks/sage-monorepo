package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.model.dto.OverallScoresDistributionDto;
import org.sagebionetworks.agora.api.model.dto.ProteomicsDistributionDto;
import org.sagebionetworks.agora.api.model.dto.RnaDistributionDto;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Distributions
 */

@Schema(name = "Distribution", description = "Distributions")
@JsonTypeName("Distribution")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class DistributionDto {

  @Valid
  private List<@Valid RnaDistributionDto> rnaDifferentialExpression = new ArrayList<>();

  @Valid
  private List<@Valid ProteomicsDistributionDto> proteomicsLFQ = new ArrayList<>();

  @Valid
  private List<@Valid ProteomicsDistributionDto> proteomicsSRM = new ArrayList<>();

  @Valid
  private List<@Valid ProteomicsDistributionDto> proteomicsTMT = new ArrayList<>();

  @Valid
  private List<@Valid OverallScoresDistributionDto> overallScores = new ArrayList<>();

  public DistributionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DistributionDto(List<@Valid RnaDistributionDto> rnaDifferentialExpression, List<@Valid ProteomicsDistributionDto> proteomicsLFQ, List<@Valid ProteomicsDistributionDto> proteomicsSRM, List<@Valid ProteomicsDistributionDto> proteomicsTMT, List<@Valid OverallScoresDistributionDto> overallScores) {
    this.rnaDifferentialExpression = rnaDifferentialExpression;
    this.proteomicsLFQ = proteomicsLFQ;
    this.proteomicsSRM = proteomicsSRM;
    this.proteomicsTMT = proteomicsTMT;
    this.overallScores = overallScores;
  }

  public DistributionDto rnaDifferentialExpression(List<@Valid RnaDistributionDto> rnaDifferentialExpression) {
    this.rnaDifferentialExpression = rnaDifferentialExpression;
    return this;
  }

  public DistributionDto addRnaDifferentialExpressionItem(RnaDistributionDto rnaDifferentialExpressionItem) {
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
  @NotNull @Valid 
  @Schema(name = "rna_differential_expression", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rna_differential_expression")
  public List<@Valid RnaDistributionDto> getRnaDifferentialExpression() {
    return rnaDifferentialExpression;
  }

  public void setRnaDifferentialExpression(List<@Valid RnaDistributionDto> rnaDifferentialExpression) {
    this.rnaDifferentialExpression = rnaDifferentialExpression;
  }

  public DistributionDto proteomicsLFQ(List<@Valid ProteomicsDistributionDto> proteomicsLFQ) {
    this.proteomicsLFQ = proteomicsLFQ;
    return this;
  }

  public DistributionDto addProteomicsLFQItem(ProteomicsDistributionDto proteomicsLFQItem) {
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
  @NotNull @Valid 
  @Schema(name = "proteomics_LFQ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("proteomics_LFQ")
  public List<@Valid ProteomicsDistributionDto> getProteomicsLFQ() {
    return proteomicsLFQ;
  }

  public void setProteomicsLFQ(List<@Valid ProteomicsDistributionDto> proteomicsLFQ) {
    this.proteomicsLFQ = proteomicsLFQ;
  }

  public DistributionDto proteomicsSRM(List<@Valid ProteomicsDistributionDto> proteomicsSRM) {
    this.proteomicsSRM = proteomicsSRM;
    return this;
  }

  public DistributionDto addProteomicsSRMItem(ProteomicsDistributionDto proteomicsSRMItem) {
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
  @NotNull @Valid 
  @Schema(name = "proteomics_SRM", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("proteomics_SRM")
  public List<@Valid ProteomicsDistributionDto> getProteomicsSRM() {
    return proteomicsSRM;
  }

  public void setProteomicsSRM(List<@Valid ProteomicsDistributionDto> proteomicsSRM) {
    this.proteomicsSRM = proteomicsSRM;
  }

  public DistributionDto proteomicsTMT(List<@Valid ProteomicsDistributionDto> proteomicsTMT) {
    this.proteomicsTMT = proteomicsTMT;
    return this;
  }

  public DistributionDto addProteomicsTMTItem(ProteomicsDistributionDto proteomicsTMTItem) {
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
  @NotNull @Valid 
  @Schema(name = "proteomics_TMT", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("proteomics_TMT")
  public List<@Valid ProteomicsDistributionDto> getProteomicsTMT() {
    return proteomicsTMT;
  }

  public void setProteomicsTMT(List<@Valid ProteomicsDistributionDto> proteomicsTMT) {
    this.proteomicsTMT = proteomicsTMT;
  }

  public DistributionDto overallScores(List<@Valid OverallScoresDistributionDto> overallScores) {
    this.overallScores = overallScores;
    return this;
  }

  public DistributionDto addOverallScoresItem(OverallScoresDistributionDto overallScoresItem) {
    if (this.overallScores == null) {
      this.overallScores = new ArrayList<>();
    }
    this.overallScores.add(overallScoresItem);
    return this;
  }

  /**
   * Get overallScores
   * @return overallScores
  */
  @NotNull @Valid 
  @Schema(name = "overall_scores", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("overall_scores")
  public List<@Valid OverallScoresDistributionDto> getOverallScores() {
    return overallScores;
  }

  public void setOverallScores(List<@Valid OverallScoresDistributionDto> overallScores) {
    this.overallScores = overallScores;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DistributionDto distribution = (DistributionDto) o;
    return Objects.equals(this.rnaDifferentialExpression, distribution.rnaDifferentialExpression) &&
        Objects.equals(this.proteomicsLFQ, distribution.proteomicsLFQ) &&
        Objects.equals(this.proteomicsSRM, distribution.proteomicsSRM) &&
        Objects.equals(this.proteomicsTMT, distribution.proteomicsTMT) &&
        Objects.equals(this.overallScores, distribution.overallScores);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rnaDifferentialExpression, proteomicsLFQ, proteomicsSRM, proteomicsTMT, overallScores);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DistributionDto {\n");
    sb.append("    rnaDifferentialExpression: ").append(toIndentedString(rnaDifferentialExpression)).append("\n");
    sb.append("    proteomicsLFQ: ").append(toIndentedString(proteomicsLFQ)).append("\n");
    sb.append("    proteomicsSRM: ").append(toIndentedString(proteomicsSRM)).append("\n");
    sb.append("    proteomicsTMT: ").append(toIndentedString(proteomicsTMT)).append("\n");
    sb.append("    overallScores: ").append(toIndentedString(overallScores)).append("\n");
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

