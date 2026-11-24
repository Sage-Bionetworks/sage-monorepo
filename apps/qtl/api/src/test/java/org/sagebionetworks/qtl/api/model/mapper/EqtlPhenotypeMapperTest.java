package org.sagebionetworks.qtl.api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.sagebionetworks.qtl.api.model.document.EqtlPhenotypeDocument;
import org.sagebionetworks.qtl.api.model.document.EqtlPhenotypeDocument.CellClassEffectSize;
import org.sagebionetworks.qtl.api.model.document.EqtlPhenotypeDocument.GenotypeExpressionSummary;
import org.sagebionetworks.qtl.api.model.dto.CellClassEffectSizeDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeDto;
import org.sagebionetworks.qtl.api.model.dto.GenotypeExpressionSummaryDto;

class EqtlPhenotypeMapperTest {

  private static final String VARIANT = "rs2120461";
  private static final String ENSEMBL_GENE_ID = "ENSG00000142599";
  private static final String CELL_TYPE_ID = "astro";
  private static final String HETEROZYGOUS_GENOTYPE = "CT";
  private static final String ASTROCYTE_CLASS_ID = "class_astro";
  private static final String ASTROCYTE = "Astrocyte";
  private static final String ASTROCYTE_SHORT = "Astro";

  private final EqtlPhenotypeMapper mapper = new EqtlPhenotypeMapper();

  @Test
  @DisplayName("should map every field of the stored phenotype")
  void shouldMapEveryFieldOfTheStoredPhenotype() {
    // when
    EqtlPhenotypeDto dto = mapper.toDto(createDocument());

    // then
    assertThat(dto.getVariant()).isEqualTo(VARIANT);
    assertThat(dto.getEnsemblGeneId()).isEqualTo(ENSEMBL_GENE_ID);
    assertThat(dto.getCellTypeId()).isEqualTo(CELL_TYPE_ID);
    assertThat(dto.getExpressionSummaries()).containsExactly(
      GenotypeExpressionSummaryDto.builder()
        .genotype(HETEROZYGOUS_GENOTYPE)
        .min(new BigDecimal("-2.2889"))
        .firstQuartile(new BigDecimal("-0.5532"))
        .median(new BigDecimal("-0.004"))
        .thirdQuartile(new BigDecimal("0.6039"))
        .max(new BigDecimal("2.3396"))
        .build()
    );
    assertThat(dto.getEffectSizeCellClassData()).containsExactly(
      CellClassEffectSizeDto.builder()
        .cellTypeId(ASTROCYTE_CLASS_ID)
        .cellClass(ASTROCYTE)
        .cellClassShort(ASTROCYTE_SHORT)
        .value(new BigDecimal("0.442711"))
        .ciLeft(new BigDecimal("0.137115"))
        .ciRight(new BigDecimal("0.748307"))
        .build()
    );
  }

  @Test
  @DisplayName("should report a data integrity error when a list is missing")
  void shouldReportADataIntegrityErrorWhenAListIsMissing() {
    // given
    EqtlPhenotypeDocument document = createDocument();
    document.setEffectSizeCellClassData(null);

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing effect_size_cell_class_data in eQTL phenotype record");
  }

  @Test
  @DisplayName("should report a data integrity error when a list entry is null")
  void shouldReportADataIntegrityErrorWhenAListEntryIsNull() {
    // given
    EqtlPhenotypeDocument document = createDocument();
    List<GenotypeExpressionSummary> summaries = new ArrayList<>();
    summaries.add(null);
    document.setExpressionSummaries(summaries);

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing expression_summaries entry in eQTL phenotype record");
  }

  @Test
  @DisplayName("should report a data integrity error when a plotted value is missing")
  void shouldReportADataIntegrityErrorWhenAPlottedValueIsMissing() {
    // given
    EqtlPhenotypeDocument document = createDocument();
    document.getEffectSizeCellClassData().get(0).setCiRight(null);

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing effect_size_cell_class_data ci_right in eQTL phenotype record");
  }

  private static EqtlPhenotypeDocument createDocument() {
    GenotypeExpressionSummary summary = new GenotypeExpressionSummary();
    summary.setGenotype(HETEROZYGOUS_GENOTYPE);
    summary.setMin(new BigDecimal("-2.2889"));
    summary.setFirstQuartile(new BigDecimal("-0.5532"));
    summary.setMedian(new BigDecimal("-0.004"));
    summary.setThirdQuartile(new BigDecimal("0.6039"));
    summary.setMax(new BigDecimal("2.3396"));

    CellClassEffectSize classEffectSize = new CellClassEffectSize();
    classEffectSize.setCellTypeId(ASTROCYTE_CLASS_ID);
    classEffectSize.setCellClass(ASTROCYTE);
    classEffectSize.setCellClassShort(ASTROCYTE_SHORT);
    classEffectSize.setValue(new BigDecimal("0.442711"));
    classEffectSize.setCiLeft(new BigDecimal("0.137115"));
    classEffectSize.setCiRight(new BigDecimal("0.748307"));

    EqtlPhenotypeDocument document = new EqtlPhenotypeDocument();
    document.setVariant(VARIANT);
    document.setEnsemblGeneId(ENSEMBL_GENE_ID);
    document.setCellTypeId(CELL_TYPE_ID);
    document.setExpressionSummaries(List.of(summary));
    document.setEffectSizeCellClassData(List.of(classEffectSize));
    return document;
  }
}
