package org.sagebionetworks.model.ad.api.next.model.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.springframework.data.mongodb.core.query.Criteria;

class ProteomicsIdentifierTest {

  private static final String UNIQUE_ID = "ENSMUSG00000000001P27144";

  @Test
  @DisplayName("should parse valid composite identifier")
  void shouldParseValidCompositeIdentifier() {
    ProteomicsIdentifier result = ProteomicsIdentifier.parse(UNIQUE_ID + "~LOAD2~Female");

    assertThat(result.getUniqueId()).isEqualTo(UNIQUE_ID);
    assertThat(result.getName()).isEqualTo("LOAD2");
    assertThat(result.getSex()).isEqualTo("Female");
  }

  @Test
  @DisplayName("should parse composite identifier with spaces in name")
  void shouldParseCompositeIdentifierWithSpacesInName() {
    ProteomicsIdentifier result = ProteomicsIdentifier.parse(
      UNIQUE_ID + "~5xFAD (Jax/IU/Pitt)~Male"
    );

    assertThat(result.getName()).isEqualTo("5xFAD (Jax/IU/Pitt)");
    assertThat(result.getSex()).isEqualTo("Male");
  }

  @Test
  @DisplayName("should trim whitespace from parts")
  void shouldTrimWhitespaceFromParts() {
    ProteomicsIdentifier result = ProteomicsIdentifier.parse(
      " " + UNIQUE_ID + " ~ LOAD2 ~ Female "
    );

    assertThat(result.getUniqueId()).isEqualTo(UNIQUE_ID);
    assertThat(result.getName()).isEqualTo("LOAD2");
    assertThat(result.getSex()).isEqualTo("Female");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is null")
  void shouldThrowExceptionWhenCompositeIdentifierIsNull() {
    assertThatThrownBy(() -> ProteomicsIdentifier.parse(null))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is blank")
  void shouldThrowExceptionWhenCompositeIdentifierIsBlank() {
    assertThatThrownBy(() -> ProteomicsIdentifier.parse("   "))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has too few parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasTooFewParts() {
    assertThatThrownBy(() -> ProteomicsIdentifier.parse(UNIQUE_ID + "~LOAD2"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format")
      .hasMessageContaining("Expected format: 'unique_id~name~sex'");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has too many parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasTooManyParts() {
    assertThatThrownBy(() -> ProteomicsIdentifier.parse(UNIQUE_ID + "~LOAD2~Female~Extra"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format");
  }

  @Test
  @DisplayName("should throw exception when unique id is empty after trimming")
  void shouldThrowExceptionWhenUniqueIdIsEmpty() {
    assertThatThrownBy(() -> ProteomicsIdentifier.parse("   ~LOAD2~Female"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (unique_id, name, sex) must be non-empty");
  }

  @Test
  @DisplayName("should throw exception when name is empty after trimming")
  void shouldThrowExceptionWhenNameIsEmpty() {
    assertThatThrownBy(() -> ProteomicsIdentifier.parse(UNIQUE_ID + "~   ~Female"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (unique_id, name, sex) must be non-empty");
  }

  @Test
  @DisplayName("should throw exception when sex is empty after trimming")
  void shouldThrowExceptionWhenSexIsEmpty() {
    assertThatThrownBy(() -> ProteomicsIdentifier.parse(UNIQUE_ID + "~LOAD2~"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (unique_id, name, sex) must be non-empty");
  }

  @Test
  @DisplayName("should round-trip parse and convert back")
  void shouldRoundTripParseAndConvertBack() {
    String original = UNIQUE_ID + "~5xFAD (Jax/IU/Pitt)~Female";

    String result = ProteomicsIdentifier.parse(original).toCompositeId();

    assertThat(result).isEqualTo(original);
  }

  @Test
  @DisplayName("should convert identifier back to composite string")
  void shouldConvertIdentifierBackToCompositeString() {
    ProteomicsIdentifier identifier = ProteomicsIdentifier.builder()
      .uniqueId(UNIQUE_ID)
      .name("LOAD2")
      .sex("Female")
      .build();

    assertThat(identifier.toCompositeId()).isEqualTo(UNIQUE_ID + "~LOAD2~Female");
  }

  @Test
  @DisplayName("should build correct criteria from identifier")
  void shouldBuildCorrectCriteriaFromIdentifier() {
    ProteomicsIdentifier identifier = ProteomicsIdentifier.builder()
      .uniqueId(UNIQUE_ID)
      .name("LOAD2")
      .sex("Female")
      .build();

    Criteria result = identifier.toCriteria();

    List<Document> andClauses = result.getCriteriaObject().getList("$and", Document.class);
    assertThat(andClauses).hasSize(3);
    assertThat(andClauses.get(0)).isEqualTo(new Document("unique_id", UNIQUE_ID));
    assertThat(andClauses.get(1)).isEqualTo(new Document("name.link_text", "LOAD2"));
    assertThat(andClauses.get(2)).isEqualTo(new Document("sex", "Female"));
  }

  @Test
  @DisplayName("should build criteria that distinguishes identifiers differing only by sex")
  void shouldBuildDifferentCriteriaWhenOnlySexDiffers() {
    ProteomicsIdentifier female = ProteomicsIdentifier.builder()
      .uniqueId(UNIQUE_ID)
      .name("LOAD2")
      .sex("Female")
      .build();
    ProteomicsIdentifier male = ProteomicsIdentifier.builder()
      .uniqueId(UNIQUE_ID)
      .name("LOAD2")
      .sex("Male")
      .build();

    assertThat(female.toCriteria().getCriteriaObject()).isNotEqualTo(
      male.toCriteria().getCriteriaObject()
    );
  }
}
