package org.sagebionetworks.qtl.api.model.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sagebionetworks.qtl.api.exception.InvalidFilterException;

class EqtlIdentifierTest {

  private static final String COMPOSITE_ID = "rs12655812~ENSG00000250891~astro";

  @Test
  @DisplayName("should parse the variant, Ensembl gene ID and cell type ID when the id is valid")
  void shouldParseTheVariantEnsemblGeneIdAndCellTypeIdWhenTheIdIsValid() {
    // when
    EqtlIdentifier identifier = EqtlIdentifier.parse(COMPOSITE_ID);

    // then
    assertThat(identifier.getVariant()).isEqualTo("rs12655812");
    assertThat(identifier.getEnsemblGeneId()).isEqualTo("ENSG00000250891");
    assertThat(identifier.getCellTypeId()).isEqualTo("astro");
  }

  @Test
  @DisplayName("should trim whitespace around each part")
  void shouldTrimWhitespaceAroundEachPart() {
    // when
    EqtlIdentifier identifier = EqtlIdentifier.parse(" rs12655812 ~ ENSG00000250891 ~ astro ");

    // then
    assertThat(identifier.toCompositeId()).isEqualTo(COMPOSITE_ID);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = { "   " })
  @DisplayName("should throw invalid filter exception when the id is null or blank")
  void shouldThrowInvalidFilterExceptionWhenTheIdIsNullOrBlank(String compositeId) {
    // when & then
    assertThatThrownBy(() -> EqtlIdentifier.parse(compositeId))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessage("Composite identifier cannot be null or empty");
  }

  @ParameterizedTest
  @ValueSource(strings = { "rs12655812~ENSG00000250891", "rs12655812~ENSG00000250891~astro~x" })
  @DisplayName("should throw invalid filter exception when the id does not have three parts")
  void shouldThrowInvalidFilterExceptionWhenTheIdDoesNotHaveThreeParts(String compositeId) {
    // when & then
    assertThatThrownBy(() -> EqtlIdentifier.parse(compositeId))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageStartingWith("Invalid composite identifier format: '" + compositeId + "'");
  }

  @ParameterizedTest
  @ValueSource(
    strings = { "~ENSG00000250891~astro", "rs12655812~ ~astro", "rs12655812~ENSG00000250891~" }
  )
  @DisplayName("should throw invalid filter exception when a part is empty")
  void shouldThrowInvalidFilterExceptionWhenAPartIsEmpty(String compositeId) {
    // when & then
    assertThatThrownBy(() -> EqtlIdentifier.parse(compositeId))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (variant, ensembl_gene_id, cell_type_id) must be non-empty");
  }

  @Test
  @DisplayName("should render the same id it parsed")
  void shouldRenderTheSameIdItParsed() {
    // when
    String compositeId = EqtlIdentifier.parse(COMPOSITE_ID).toCompositeId();

    // then
    assertThat(compositeId).isEqualTo(COMPOSITE_ID);
  }

  @Test
  @DisplayName("should match all three fields when converted to criteria")
  void shouldMatchAllThreeFieldsWhenConvertedToCriteria() {
    // when
    Document criteria = EqtlIdentifier.parse(COMPOSITE_ID).toCriteria().getCriteriaObject();

    // then
    assertThat(criteria.toJson()).isEqualTo(
      Document.parse(
        "{\"$and\": [{\"variant\": \"rs12655812\"}, " +
        "{\"ensembl_gene_id\": \"ENSG00000250891\"}, {\"cell_type_id\": \"astro\"}]}"
      ).toJson()
    );
  }
}
