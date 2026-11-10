package org.sagebionetworks.qtl.api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument.GenomicLocation;
import org.sagebionetworks.qtl.api.model.dto.EffectDirectionDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlDto;
import org.sagebionetworks.qtl.api.model.dto.GenomicLocationDto;

class EqtlMapperTest {

  private final EqtlMapper mapper = new EqtlMapper();

  @Test
  @DisplayName("should map every field and build the composite id when the record is complete")
  void shouldMapEveryFieldAndBuildTheCompositeIdWhenTheRecordIsComplete() {
    // when
    EqtlDto dto = mapper.toDto(completeDocument());

    // then
    assertThat(dto.getCompositeId()).isEqualTo("rs170094~ENSG00000136267~astro");
    assertThat(dto.getVariant()).isEqualTo("rs170094");
    assertThat(dto.getVariantLocation()).isEqualTo(
      GenomicLocationDto.builder().chr("7").start(14889039).end(14889040).build()
    );
    assertThat(dto.getEffectDirection()).isEqualTo(EffectDirectionDto.UP);
    assertThat(dto.getGeneSymbol()).isEqualTo("DGKB");
    assertThat(dto.getEnsemblGeneId()).isEqualTo("ENSG00000136267");
    assertThat(dto.getGeneLocation()).isEqualTo(
      GenomicLocationDto.builder().chr("7").start(14145049).end(14974777).build()
    );
    assertThat(dto.getBeta()).isEqualTo(new BigDecimal("0.53"));
    assertThat(dto.getAdjPVal()).isEqualTo("1.16E-140");
    assertThat(dto.getAdjPValNumeric()).isEqualTo(new BigDecimal("-139.935"));
    assertThat(dto.getMaf()).isEqualTo(new BigDecimal("0.21"));
    assertThat(dto.getMinorAllele()).isEqualTo("A");
    assertThat(dto.getReferenceAllele()).isEqualTo("G");
    assertThat(dto.getCellTypeId()).isEqualTo("astro");
    assertThat(dto.getCellClass()).isEqualTo("Astrocyte");
    assertThat(dto.getCellClassShort()).isEqualTo("Astro");
    assertThat(dto.getCellSubclass()).isEqualTo("Astrocyte");
    assertThat(dto.getCellSubclassShort()).isEqualTo("Astro");
  }

  @Test
  @DisplayName("should throw data integrity exception when the effect direction is unknown")
  void shouldThrowDataIntegrityExceptionWhenTheEffectDirectionIsUnknown() {
    // given
    EqtlDocument document = completeDocument();
    document.setEffectDirection("Sideways");

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Unexpected effect_direction value 'Sideways' in eQTL record");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("missingFields")
  @DisplayName("should throw data integrity exception when a required field is missing")
  void shouldThrowDataIntegrityExceptionWhenARequiredFieldIsMissing(
    String fieldName,
    Consumer<EqtlDocument> clearField
  ) {
    // given
    EqtlDocument document = completeDocument();
    clearField.accept(document);

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing " + fieldName + " in eQTL record");
  }

  private static Stream<Arguments> missingFields() {
    return Stream.of(
      missing("variant", document -> document.setVariant(null)),
      missing("variant_location", document -> document.setVariantLocation(null)),
      missing("variant_location chr", document -> document.getVariantLocation().setChr(null)),
      missing("effect_direction", document -> document.setEffectDirection(null)),
      missing("gene_symbol", document -> document.setGeneSymbol(null)),
      missing("ensembl_gene_id", document -> document.setEnsemblGeneId(null)),
      missing("gene_location", document -> document.setGeneLocation(null)),
      missing("gene_location start", document -> document.getGeneLocation().setStart(null)),
      missing("gene_location end", document -> document.getGeneLocation().setEnd(null)),
      missing("beta", document -> document.setBeta(null)),
      missing("adj_p_val", document -> document.setAdjPVal(null)),
      missing("adj_p_val_numeric", document -> document.setAdjPValNumeric(null)),
      missing("maf", document -> document.setMaf(null)),
      missing("minor_allele", document -> document.setMinorAllele(null)),
      missing("reference_allele", document -> document.setReferenceAllele(null)),
      missing("cell_type_id", document -> document.setCellTypeId(null)),
      missing("cell_class", document -> document.setCellClass(null)),
      missing("cell_class_short", document -> document.setCellClassShort(null)),
      missing("cell_subclass", document -> document.setCellSubclass(null)),
      missing("cell_subclass_short", document -> document.setCellSubclassShort(null))
    );
  }

  private static Arguments missing(String fieldName, Consumer<EqtlDocument> clearField) {
    return Arguments.of(fieldName, clearField);
  }

  private static EqtlDocument completeDocument() {
    EqtlDocument document = new EqtlDocument();
    document.setVariant("rs170094");
    document.setVariantLocation(
      GenomicLocation.builder().chr("7").start(14889039).end(14889040).build()
    );
    document.setEffectDirection("Up");
    document.setGeneSymbol("DGKB");
    document.setEnsemblGeneId("ENSG00000136267");
    document.setGeneLocation(
      GenomicLocation.builder().chr("7").start(14145049).end(14974777).build()
    );
    document.setBeta(new BigDecimal("0.53"));
    document.setAdjPVal("1.16E-140");
    document.setAdjPValNumeric(new BigDecimal("-139.935"));
    document.setMaf(new BigDecimal("0.21"));
    document.setMinorAllele("A");
    document.setReferenceAllele("G");
    document.setCellTypeId("astro");
    document.setCellClass("Astrocyte");
    document.setCellClassShort("Astro");
    document.setCellSubclass("Astrocyte");
    document.setCellSubclassShort("Astro");
    return document;
  }
}
