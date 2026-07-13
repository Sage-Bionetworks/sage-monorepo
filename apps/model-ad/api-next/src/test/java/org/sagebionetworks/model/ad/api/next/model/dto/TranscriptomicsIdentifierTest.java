package org.sagebionetworks.model.ad.api.next.model.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.springframework.data.mongodb.core.query.Criteria;

class TranscriptomicsIdentifierTest {

  @Test
  @DisplayName("should parse valid composite identifier")
  void shouldParseValidCompositeIdentifier() {
    String compositeId = "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female";

    TranscriptomicsIdentifier result = TranscriptomicsIdentifier.parse(compositeId);

    assertThat(result.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000001");
    assertThat(result.getName()).isEqualTo("5xFAD (Jax/IU/Pitt)");
    assertThat(result.getSex()).isEqualTo("Female");
  }

  @Test
  @DisplayName("should parse composite identifier with spaces in name")
  void shouldParseCompositeIdentifierWithSpacesInName() {
    String compositeId = "ENSMUSG00000000002~APOE4 Humanized~Male";

    TranscriptomicsIdentifier result = TranscriptomicsIdentifier.parse(compositeId);

    assertThat(result.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000002");
    assertThat(result.getName()).isEqualTo("APOE4 Humanized");
    assertThat(result.getSex()).isEqualTo("Male");
  }

  @Test
  @DisplayName("should trim whitespace from parts")
  void shouldTrimWhitespaceFromParts() {
    String compositeId = " ENSMUSG00000000001 ~ 5xFAD ~ Female ";

    TranscriptomicsIdentifier result = TranscriptomicsIdentifier.parse(compositeId);

    assertThat(result.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000001");
    assertThat(result.getName()).isEqualTo("5xFAD");
    assertThat(result.getSex()).isEqualTo("Female");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is null")
  void shouldThrowExceptionWhenCompositeIdentifierIsNull() {
    assertThatThrownBy(() -> TranscriptomicsIdentifier.parse(null))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is empty")
  void shouldThrowExceptionWhenCompositeIdentifierIsEmpty() {
    assertThatThrownBy(() -> TranscriptomicsIdentifier.parse(""))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is blank")
  void shouldThrowExceptionWhenCompositeIdentifierIsBlank() {
    assertThatThrownBy(() -> TranscriptomicsIdentifier.parse("   "))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Composite identifier cannot be null or empty");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has wrong format - too few parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasTooFewParts() {
    assertThatThrownBy(() -> TranscriptomicsIdentifier.parse("ENSMUSG00000000001~5xFAD"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format")
      .hasMessageContaining("Expected format: 'ensembl_gene_id~name~sex'");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has too many parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasTooManyParts() {
    assertThatThrownBy(() ->
      TranscriptomicsIdentifier.parse("ENSMUSG00000000001~5xFAD~Female~Extra")
    )
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format");
  }

  @Test
  @DisplayName("should throw exception when composite identifier has empty parts")
  void shouldThrowExceptionWhenCompositeIdentifierHasEmptyParts() {
    assertThatThrownBy(() -> TranscriptomicsIdentifier.parse("ENSMUSG00000000001~5xFAD~"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (ensembl_gene_id, name, sex) must be non-empty");
  }

  @Test
  @DisplayName("should throw exception when ensembl gene id is empty after trimming")
  void shouldThrowExceptionWhenEnsemblGeneIdIsEmpty() {
    assertThatThrownBy(() -> TranscriptomicsIdentifier.parse("   ~5xFAD~Female"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (ensembl_gene_id, name, sex) must be non-empty");
  }

  @Test
  @DisplayName("should throw exception when name is empty after trimming")
  void shouldThrowExceptionWhenNameIsEmpty() {
    assertThatThrownBy(() -> TranscriptomicsIdentifier.parse("ENSMUSG00000000001~   ~Female"))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (ensembl_gene_id, name, sex) must be non-empty");
  }

  @Test
  @DisplayName("should throw exception when sex is empty after trimming")
  void shouldThrowExceptionWhenSexIsEmpty() {
    assertThatThrownBy(() -> TranscriptomicsIdentifier.parse("ENSMUSG00000000001~5xFAD~   "))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("All parts (ensembl_gene_id, name, sex) must be non-empty");
  }

  @Test
  @DisplayName("should convert identifier back to composite string")
  void shouldConvertIdentifierBackToCompositeString() {
    TranscriptomicsIdentifier identifier = TranscriptomicsIdentifier.builder()
      .ensemblGeneId("ENSMUSG00000000001")
      .name("5xFAD (Jax/IU/Pitt)")
      .sex("Female")
      .build();

    String result = identifier.toCompositeId();

    assertThat(result).isEqualTo("ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female");
  }

  @Test
  @DisplayName("should round-trip parse and convert back")
  void shouldRoundTripParseAndConvertBack() {
    String original = "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female";

    TranscriptomicsIdentifier identifier = TranscriptomicsIdentifier.parse(original);
    String result = identifier.toCompositeId();

    assertThat(result).isEqualTo(original);
  }

  @Test
  @DisplayName("should handle special characters in name")
  void shouldHandleSpecialCharactersInName() {
    String compositeId = "ENSMUSG00000000001~Model-123 (Test/Lab)~Male";

    TranscriptomicsIdentifier result = TranscriptomicsIdentifier.parse(compositeId);

    assertThat(result.getName()).isEqualTo("Model-123 (Test/Lab)");
    assertThat(result.getSex()).isEqualTo("Male");
    assertThat(result.toCompositeId()).isEqualTo(compositeId);
  }

  @Test
  @DisplayName("should build correct criteria from identifier")
  void shouldBuildCorrectCriteriaFromIdentifier() {
    TranscriptomicsIdentifier identifier = TranscriptomicsIdentifier.builder()
      .ensemblGeneId("ENSMUSG00000000001")
      .name("5xFAD")
      .sex("Female")
      .build();

    Criteria result = identifier.toCriteria();

    List<Document> andClauses = result.getCriteriaObject().getList("$and", Document.class);
    assertThat(andClauses).hasSize(3);
    assertThat(andClauses.get(0)).isEqualTo(new Document("ensembl_gene_id", "ENSMUSG00000000001"));
    assertThat(andClauses.get(1)).isEqualTo(new Document("name.link_text", "5xFAD"));
    // MG-1004: sex matches singular or plural source data (Female / Females)
    assertThat(andClauses.get(2)).isEqualTo(
      new Document("sex", new Document("$in", List.of("Female", "Females")))
    );
  }

  // TODO(MG-1004): remove this test once rna_de_aggregate stores singular Female/Male
  @Test
  @DisplayName("should match plural source sex values for male identifier")
  void shouldMatchPluralSourceSexValuesForMale() {
    TranscriptomicsIdentifier identifier = TranscriptomicsIdentifier.builder()
      .ensemblGeneId("ENSMUSG00000000001")
      .name("5xFAD")
      .sex("Male")
      .build();

    List<Document> andClauses = identifier
      .toCriteria()
      .getCriteriaObject()
      .getList("$and", Document.class);

    assertThat(andClauses.get(2)).isEqualTo(
      new Document("sex", new Document("$in", List.of("Male", "Males")))
    );
  }

  @Test
  @DisplayName("should build criteria that distinguishes identifiers differing only by sex")
  void shouldBuildDifferentCriteriaWhenOnlySexDiffers() {
    TranscriptomicsIdentifier female = TranscriptomicsIdentifier.builder()
      .ensemblGeneId("ENSMUSG00000000001")
      .name("5xFAD")
      .sex("Female")
      .build();
    TranscriptomicsIdentifier male = TranscriptomicsIdentifier.builder()
      .ensemblGeneId("ENSMUSG00000000001")
      .name("5xFAD")
      .sex("Male")
      .build();

    assertThat(female.toCriteria().getCriteriaObject()).isNotEqualTo(
      male.toCriteria().getCriteriaObject()
    );
  }
}
