package org.sagebionetworks.qtl.api.model.mapper;

import java.util.List;
import java.util.function.Function;
import org.sagebionetworks.qtl.api.model.document.EqtlPhenotypeDocument;
import org.sagebionetworks.qtl.api.model.document.EqtlPhenotypeDocument.CellClassEffectSize;
import org.sagebionetworks.qtl.api.model.document.EqtlPhenotypeDocument.GenotypeExpressionSummary;
import org.sagebionetworks.qtl.api.model.dto.CellClassEffectSizeDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeDto;
import org.sagebionetworks.qtl.api.model.dto.GenotypeExpressionSummaryDto;
import org.sagebionetworks.qtl.api.util.RequiredFieldValidator;
import org.springframework.stereotype.Component;

@Component
public class EqtlPhenotypeMapper {

  private static final String RECORD_TYPE = "eQTL phenotype record";

  private static final RequiredFieldValidator REQUIRED_FIELDS = new RequiredFieldValidator(
    RECORD_TYPE
  );

  public EqtlPhenotypeDto toDto(EqtlPhenotypeDocument document) {
    return EqtlPhenotypeDto.builder()
      .variant(REQUIRED_FIELDS.require(document.getVariant(), "variant"))
      .ensemblGeneId(REQUIRED_FIELDS.require(document.getEnsemblGeneId(), "ensembl_gene_id"))
      .cellTypeId(REQUIRED_FIELDS.require(document.getCellTypeId(), "cell_type_id"))
      .expressionSummaries(
        mapEach(
          document.getExpressionSummaries(),
          "expression_summaries",
          this::toGenotypeExpressionSummaryDto
        )
      )
      .effectSizeCellClassData(
        mapEach(
          document.getEffectSizeCellClassData(),
          "effect_size_cell_class_data",
          this::toCellClassEffectSizeDto
        )
      )
      .build();
  }

  private GenotypeExpressionSummaryDto toGenotypeExpressionSummaryDto(
    GenotypeExpressionSummary summary
  ) {
    String fieldPrefix = "expression_summaries ";
    return GenotypeExpressionSummaryDto.builder()
      .genotype(REQUIRED_FIELDS.require(summary.getGenotype(), fieldPrefix + "genotype"))
      .min(REQUIRED_FIELDS.require(summary.getMin(), fieldPrefix + "min"))
      .firstQuartile(
        REQUIRED_FIELDS.require(summary.getFirstQuartile(), fieldPrefix + "first_quartile")
      )
      .median(REQUIRED_FIELDS.require(summary.getMedian(), fieldPrefix + "median"))
      .thirdQuartile(
        REQUIRED_FIELDS.require(summary.getThirdQuartile(), fieldPrefix + "third_quartile")
      )
      .max(REQUIRED_FIELDS.require(summary.getMax(), fieldPrefix + "max"))
      .build();
  }

  private CellClassEffectSizeDto toCellClassEffectSizeDto(CellClassEffectSize effectSize) {
    String fieldPrefix = "effect_size_cell_class_data ";
    return CellClassEffectSizeDto.builder()
      .cellTypeId(REQUIRED_FIELDS.require(effectSize.getCellTypeId(), fieldPrefix + "cell_type_id"))
      .cellClass(REQUIRED_FIELDS.require(effectSize.getCellClass(), fieldPrefix + "cell_class"))
      .cellClassShort(
        REQUIRED_FIELDS.require(effectSize.getCellClassShort(), fieldPrefix + "cell_class_short")
      )
      .value(REQUIRED_FIELDS.require(effectSize.getValue(), fieldPrefix + "value"))
      .ciLeft(REQUIRED_FIELDS.require(effectSize.getCiLeft(), fieldPrefix + "ci_left"))
      .ciRight(REQUIRED_FIELDS.require(effectSize.getCiRight(), fieldPrefix + "ci_right"))
      .build();
  }

  private static <T, R> List<R> mapEach(List<T> values, String fieldName, Function<T, R> mapper) {
    return REQUIRED_FIELDS.require(values, fieldName)
      .stream()
      .map(value -> mapper.apply(REQUIRED_FIELDS.require(value, fieldName + " entry")))
      .toList();
  }
}
