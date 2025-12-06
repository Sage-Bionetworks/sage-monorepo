package org.sagebionetworks.model.ad.api.next.model.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;

class GeneExpressionIdentifierTest {

  @Test
  @DisplayName("should parse valid composite identifier")
  void shouldParseValidCompositeIdentifier() {
    String compositeId = "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)";

    GeneExpressionIdentifier result = GeneExpressionIdentifier.parse(compositeId);

    assertThat(result.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000001");
    assertThat(result.getName()).isEqualTo("5xFAD (Jax/IU/Pitt)");
  }

  @Test
  @DisplayName("should parse composite identifier with spaces in name")
  void shouldParseCompositeIdentifierWithSpacesInName() {
    String compositeId = "ENSMUSG00000000002~APOE4 Humanized";

    GeneExpressionIdentifier result = GeneExpressionIdentifier.parse(compositeId);

    assertThat(result.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000002");
    assertThat(result.getName()).isEqualTo("APOE4 Humanized");
  }

  @Test
  @DisplayName("should trim whitespace from parts")
  void shouldTrimWhitespaceFromParts() {
    String compositeId = " ENSMUSG00000000001 ~ 5xFAD ";

    GeneExpressionIdentifier result = GeneExpressionIdentifier.parse(compositeId);

    assertThat(result.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000001");
    assertThat(result.getName()).isEqualTo("5xFAD");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is null")
  void shouldThrowExceptionWhenCompositeIdentifierIsNull() {
    assertThatThrownBy(() -> GeneExpressionIdentifier.parse(null))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is empty")
  void shouldThrowExceptionWhenCompositeIdentifierIsEmpty() {
    assertThatThrownBy(() -> GeneExpressionIdentifier.parse(""))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is blank")
  void shouldThrowExceptionWhenCompositeIdentifierIsBlank() {
    assertThatThrownBy(() -> GeneExpressionIdentifier.parse("   "))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has wrong format - too few parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasTooFewParts() {
    assertThatThrownBy(() -> GeneExpressionIdentifier.parse("ENSMUSG00000000001"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format")
      .hasMessageContaining("Expected format: 'ensembl_gene_id~name'");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has too many parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasTooManyParts() {
    assertThatThrownBy(() -> GeneExpressionIdentifier.parse("ENSMUSG00000000001~5xFAD~Extra"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has empty parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasEmptyParts() {
    assertThatThrownBy(() -> GeneExpressionIdentifier.parse("ENSMUSG00000000001~"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (ensembl_gene_id, name) must be non-empty");
  }

  @Test
  @DisplayName("should throw exception when ensembl gene id is empty after trimming")
  void shouldThrowExceptionWhenEnsemblGeneIdIsEmpty() {
    assertThatThrownBy(() -> GeneExpressionIdentifier.parse("   ~5xFAD"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (ensembl_gene_id, name) must be non-empty");
  }

  @Test
  @DisplayName("should throw exception when name is empty after trimming")
  void shouldThrowExceptionWhenNameIsEmpty() {
    assertThatThrownBy(() -> GeneExpressionIdentifier.parse("ENSMUSG00000000001~   "))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (ensembl_gene_id, name) must be non-empty");
  }

  @Test
  @DisplayName("should convert identifier back to composite string")
  void shouldConvertIdentifierBackToCompositeString() {
    GeneExpressionIdentifier identifier = GeneExpressionIdentifier.builder()
      .ensemblGeneId("ENSMUSG00000000001")
      .name("5xFAD (Jax/IU/Pitt)")
      .build();

    String result = identifier.toCompositeId();

    assertThat(result).isEqualTo("ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)");
  }

  @Test
  @DisplayName("should round-trip parse and convert back")
  void shouldRoundTripParseAndConvertBack() {
    String original = "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)";

    GeneExpressionIdentifier identifier = GeneExpressionIdentifier.parse(original);
    String result = identifier.toCompositeId();

    assertThat(result).isEqualTo(original);
  }

  @Test
  @DisplayName("should handle special characters in name")
  void shouldHandleSpecialCharactersInName() {
    String compositeId = "ENSMUSG00000000001~Model-123 (Test/Lab)";

    GeneExpressionIdentifier result = GeneExpressionIdentifier.parse(compositeId);

    assertThat(result.getName()).isEqualTo("Model-123 (Test/Lab)");
    assertThat(result.toCompositeId()).isEqualTo(compositeId);
  }
}
