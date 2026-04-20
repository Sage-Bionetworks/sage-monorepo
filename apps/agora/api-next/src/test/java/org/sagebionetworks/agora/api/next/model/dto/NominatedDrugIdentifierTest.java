package org.sagebionetworks.agora.api.next.model.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.agora.api.next.exception.InvalidFilterException;

class NominatedDrugIdentifierTest {

  @Test
  @DisplayName("should parse valid composite identifier")
  void shouldParseValidCompositeIdentifier() {
    String compositeId = "CHEMBL2105758~Donepezil";

    NominatedDrugIdentifier result = NominatedDrugIdentifier.parse(compositeId);

    assertThat(result.getChemblId()).isEqualTo("CHEMBL2105758");
    assertThat(result.getCombinedWith()).isEqualTo("Donepezil");
  }

  @Test
  @DisplayName("should parse composite identifier with null combined_with")
  void shouldParseCompositeIdentifierWithNullCombinedWith() {
    String compositeId = "CHEMBL2105758~null";

    NominatedDrugIdentifier result = NominatedDrugIdentifier.parse(compositeId);

    assertThat(result.getChemblId()).isEqualTo("CHEMBL2105758");
    assertThat(result.getCombinedWith()).isNull();
  }

  @Test
  @DisplayName("should trim whitespace from parts")
  void shouldTrimWhitespaceFromParts() {
    String compositeId = " CHEMBL2105758 ~ Donepezil ";

    NominatedDrugIdentifier result = NominatedDrugIdentifier.parse(compositeId);

    assertThat(result.getChemblId()).isEqualTo("CHEMBL2105758");
    assertThat(result.getCombinedWith()).isEqualTo("Donepezil");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is null")
  void shouldThrowExceptionWhenCompositeIdentifierIsNull() {
    assertThatThrownBy(() -> NominatedDrugIdentifier.parse(null))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is empty")
  void shouldThrowExceptionWhenCompositeIdentifierIsEmpty() {
    assertThatThrownBy(() -> NominatedDrugIdentifier.parse(""))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has wrong format")
  void shouldThrowExceptionWhenCompositeIdentifierHasWrongFormat() {
    assertThatThrownBy(() -> NominatedDrugIdentifier.parse("CHEMBL2105758"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format")
      .hasMessageContaining("Expected format: 'chembl_id~combined_with'");
  }

  @Test
  @DisplayName("should throw exception when chembl_id is empty")
  void shouldThrowExceptionWhenChemblIdIsEmpty() {
    assertThatThrownBy(() -> NominatedDrugIdentifier.parse("~Donepezil"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("chembl_id must be non-empty");
  }

  @Test
  @DisplayName("should convert identifier back to composite string")
  void shouldConvertIdentifierBackToCompositeString() {
    NominatedDrugIdentifier identifier = NominatedDrugIdentifier.builder()
      .chemblId("CHEMBL2105758")
      .combinedWith(null)
      .build();

    String result = identifier.toCompositeId();

    assertThat(result).isEqualTo("CHEMBL2105758~null");
  }

  @Test
  @DisplayName("should round-trip parse and convert composite identifier")
  void shouldRoundTripParseAndConvertCompositeIdentifier() {
    String original = "CHEMBL1023~Donepezil";

    NominatedDrugIdentifier parsed = NominatedDrugIdentifier.parse(original);
    String result = parsed.toCompositeId();

    assertThat(result).isEqualTo(original);
  }
}
