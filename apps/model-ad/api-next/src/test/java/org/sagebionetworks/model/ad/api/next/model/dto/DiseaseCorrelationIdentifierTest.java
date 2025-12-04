package org.sagebionetworks.model.ad.api.next.model.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;

class DiseaseCorrelationIdentifierTest {

  @Test
  @DisplayName("should parse valid composite identifier")
  void shouldParseValidCompositeIdentifier() {
    String compositeId = "APOE4~4 months~Female";

    DiseaseCorrelationIdentifier result = DiseaseCorrelationIdentifier.parse(compositeId);

    assertThat(result.getName()).isEqualTo("APOE4");
    assertThat(result.getAge()).isEqualTo("4 months");
    assertThat(result.getSex()).isEqualTo("Female");
  }

  @Test
  @DisplayName("should parse composite identifier with spaces in name")
  void shouldParseCompositeIdentifierWithSpacesInName() {
    String compositeId = "5xFAD (IU/Jax/Pitt)~12 months~Male";

    DiseaseCorrelationIdentifier result = DiseaseCorrelationIdentifier.parse(compositeId);

    assertThat(result.getName()).isEqualTo("5xFAD (IU/Jax/Pitt)");
    assertThat(result.getAge()).isEqualTo("12 months");
    assertThat(result.getSex()).isEqualTo("Male");
  }

  @Test
  @DisplayName("should trim whitespace from parts")
  void shouldTrimWhitespaceFromParts() {
    String compositeId = " APOE4 ~ 4 months ~ Female ";

    DiseaseCorrelationIdentifier result = DiseaseCorrelationIdentifier.parse(compositeId);

    assertThat(result.getName()).isEqualTo("APOE4");
    assertThat(result.getAge()).isEqualTo("4 months");
    assertThat(result.getSex()).isEqualTo("Female");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is null")
  void shouldThrowExceptionWhenCompositeIdentifierIsNull() {
    assertThatThrownBy(() -> DiseaseCorrelationIdentifier.parse(null))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is empty")
  void shouldThrowExceptionWhenCompositeIdentifierIsEmpty() {
    assertThatThrownBy(() -> DiseaseCorrelationIdentifier.parse(""))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has wrong format")
  void shouldThrowExceptionWhenCompositeIdentifierHasWrongFormat() {
    assertThatThrownBy(() -> DiseaseCorrelationIdentifier.parse("APOE4~4 months"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format")
      .hasMessageContaining("Expected format: 'name~age~sex'");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has empty parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasEmptyParts() {
    assertThatThrownBy(() -> DiseaseCorrelationIdentifier.parse("APOE4~~Female"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (name, age, sex) must be non-empty");
  }

  @Test
  @DisplayName("should convert identifier back to composite string")
  void shouldConvertIdentifierBackToCompositeString() {
    DiseaseCorrelationIdentifier identifier = DiseaseCorrelationIdentifier.builder()
      .name("APOE4")
      .age("4 months")
      .sex("Female")
      .build();

    String result = identifier.toCompositeString();

    assertThat(result).isEqualTo("APOE4~4 months~Female");
  }
}
