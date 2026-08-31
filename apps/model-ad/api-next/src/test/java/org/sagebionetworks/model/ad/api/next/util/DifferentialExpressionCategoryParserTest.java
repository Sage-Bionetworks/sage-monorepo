package org.sagebionetworks.model.ad.api.next.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;

class DifferentialExpressionCategoryParserTest {

  private static final String RNA_TOKEN = "RNA";
  private static final String PROTEIN_TOKEN = "PROTEIN";
  private static final String RNA_CATEGORY = "RNA - DIFFERENTIAL EXPRESSION";
  private static final String PROTEIN_CATEGORY = "PROTEIN - DIFFERENTIAL EXPRESSION";
  private static final String TISSUE = "Hemibrain";
  private static final String TISSUE_CATEGORY = "Tissue - " + TISSUE;

  @Test
  @DisplayName("should extract the tissue for the transcriptomics modality")
  void shouldExtractTissueForTranscriptomicsModality() {
    String result = DifferentialExpressionCategoryParser.extractTissue(
      List.of(RNA_CATEGORY, TISSUE_CATEGORY),
      RNA_TOKEN
    );

    assertThat(result).isEqualTo(TISSUE);
  }

  @Test
  @DisplayName("should extract the tissue for the proteomics modality")
  void shouldExtractTissueForProteomicsModality() {
    String result = DifferentialExpressionCategoryParser.extractTissue(
      List.of(PROTEIN_CATEGORY, TISSUE_CATEGORY),
      PROTEIN_TOKEN
    );

    assertThat(result).isEqualTo(TISSUE);
  }

  @Test
  @DisplayName("should extract a tissue whose name contains spaces")
  void shouldExtractTissueWhoseNameContainsSpaces() {
    String result = DifferentialExpressionCategoryParser.extractTissue(
      List.of(RNA_CATEGORY, "Tissue - Cerebral Cortex"),
      RNA_TOKEN
    );

    assertThat(result).isEqualTo("Cerebral Cortex");
  }

  @Test
  @DisplayName("should keep a separator that appears inside the tissue name")
  void shouldKeepSeparatorInsideTissueName() {
    String result = DifferentialExpressionCategoryParser.extractTissue(
      List.of(RNA_CATEGORY, "Tissue - Hemibrain - Left"),
      RNA_TOKEN
    );

    assertThat(result).isEqualTo("Hemibrain - Left");
  }

  @Test
  @DisplayName("should trim surrounding whitespace from both category values")
  void shouldTrimSurroundingWhitespaceFromBothCategoryValues() {
    String result = DifferentialExpressionCategoryParser.extractTissue(
      List.of("  " + RNA_CATEGORY + "  ", "  " + TISSUE_CATEGORY + "  "),
      RNA_TOKEN
    );

    assertThat(result).isEqualTo(TISSUE);
  }

  @Test
  @DisplayName("should match the main category regardless of case")
  void shouldMatchMainCategoryRegardlessOfCase() {
    String result = DifferentialExpressionCategoryParser.extractTissue(
      List.of("rna - differential expression", TISSUE_CATEGORY),
      RNA_TOKEN
    );

    assertThat(result).isEqualTo(TISSUE);
  }

  @Test
  @DisplayName("should match a modality token given in lower case")
  void shouldMatchModalityTokenGivenInLowerCase() {
    String result = DifferentialExpressionCategoryParser.extractTissue(
      List.of(PROTEIN_CATEGORY, TISSUE_CATEGORY),
      PROTEIN_TOKEN.toLowerCase()
    );

    assertThat(result).isEqualTo(TISSUE);
  }

  @Test
  @DisplayName("should ignore category values beyond the first two")
  void shouldIgnoreCategoryValuesBeyondFirstTwo() {
    String result = DifferentialExpressionCategoryParser.extractTissue(
      List.of(RNA_CATEGORY, TISSUE_CATEGORY, "Sex - Female"),
      RNA_TOKEN
    );

    assertThat(result).isEqualTo(TISSUE);
  }

  @Test
  @DisplayName("should throw exception when categories is null")
  void shouldThrowExceptionWhenCategoriesIsNull() {
    assertThatThrownBy(() -> DifferentialExpressionCategoryParser.extractTissue(null, RNA_TOKEN))
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessage("Expected at least 2 category values, got: 0");
  }

  @Test
  @DisplayName("should throw exception when categories is empty")
  void shouldThrowExceptionWhenCategoriesIsEmpty() {
    assertThatThrownBy(() ->
      DifferentialExpressionCategoryParser.extractTissue(Collections.emptyList(), RNA_TOKEN)
    )
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessage("Expected at least 2 category values, got: 0");
  }

  @Test
  @DisplayName("should throw exception when the tissue category is missing")
  void shouldThrowExceptionWhenTissueCategoryIsMissing() {
    assertThatThrownBy(() ->
      DifferentialExpressionCategoryParser.extractTissue(List.of(RNA_CATEGORY), RNA_TOKEN)
    )
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessage("Expected at least 2 category values, got: 1");
  }

  @Test
  @DisplayName("should throw exception when the main category is for another modality")
  void shouldThrowExceptionWhenMainCategoryIsForAnotherModality() {
    assertThatThrownBy(() ->
      DifferentialExpressionCategoryParser.extractTissue(
        List.of(RNA_CATEGORY, TISSUE_CATEGORY),
        PROTEIN_TOKEN
      )
    )
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessage(
        "Invalid main category: '" + RNA_CATEGORY + "'. Expected PROTEIN - DIFFERENTIAL EXPRESSION"
      );
  }

  @Test
  @DisplayName("should throw exception when the main category is not differential expression")
  void shouldThrowExceptionWhenMainCategoryIsNotDifferentialExpression() {
    assertThatThrownBy(() ->
      DifferentialExpressionCategoryParser.extractTissue(
        List.of("RNA - EXPRESSION", TISSUE_CATEGORY),
        RNA_TOKEN
      )
    )
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Invalid main category: 'RNA - EXPRESSION'");
  }

  @Test
  @DisplayName("should throw exception when the tissue category is missing its prefix")
  void shouldThrowExceptionWhenTissueCategoryIsMissingItsPrefix() {
    assertThatThrownBy(() ->
      DifferentialExpressionCategoryParser.extractTissue(List.of(RNA_CATEGORY, TISSUE), RNA_TOKEN)
    )
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessage("Invalid tissue format: '" + TISSUE + "'. Expected format: 'Tissue - ...'");
  }

  @Test
  @DisplayName("should throw exception when the tissue prefix is not capitalized")
  void shouldThrowExceptionWhenTissuePrefixIsNotCapitalized() {
    assertThatThrownBy(() ->
      DifferentialExpressionCategoryParser.extractTissue(
        List.of(RNA_CATEGORY, "tissue - " + TISSUE),
        RNA_TOKEN
      )
    )
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Invalid tissue format");
  }

  @Test
  @DisplayName("should throw exception when the tissue category carries no value")
  void shouldThrowExceptionWhenTissueCategoryCarriesNoValue() {
    assertThatThrownBy(() ->
      DifferentialExpressionCategoryParser.extractTissue(
        List.of(RNA_CATEGORY, "Tissue -    "),
        RNA_TOKEN
      )
    )
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessage("Invalid tissue format: 'Tissue -'. Expected format: 'Tissue - ...'");
  }
}
