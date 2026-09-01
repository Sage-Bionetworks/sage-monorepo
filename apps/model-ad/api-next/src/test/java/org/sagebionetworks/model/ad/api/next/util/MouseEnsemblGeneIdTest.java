package org.sagebionetworks.model.ad.api.next.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MouseEnsemblGeneIdTest {

  private static final String ENSA_MOUSE_ENSEMBL_GENE_ID = "ENSMUSG00000038619";

  @Test
  @DisplayName("should recognize a canonical mouse ensembl gene id")
  void shouldRecognizeCanonicalMouseEnsemblGeneId() {
    assertThat(MouseEnsemblGeneId.isFullId(ENSA_MOUSE_ENSEMBL_GENE_ID)).isTrue();
  }

  @Test
  @DisplayName("should recognize a lower-cased mouse ensembl gene id")
  void shouldRecognizeLowerCasedMouseEnsemblGeneId() {
    assertThat(MouseEnsemblGeneId.isFullId(ENSA_MOUSE_ENSEMBL_GENE_ID.toLowerCase())).isTrue();
  }

  @Test
  @DisplayName("should reject an id with too few digits")
  void shouldRejectIdWithTooFewDigits() {
    assertThat(MouseEnsemblGeneId.isFullId("ENSMUSG0000003861")).isFalse();
  }

  @Test
  @DisplayName("should reject an id with too many digits")
  void shouldRejectIdWithTooManyDigits() {
    assertThat(MouseEnsemblGeneId.isFullId("ENSMUSG000000386190")).isFalse();
  }

  @Test
  @DisplayName("should reject an id with a version suffix")
  void shouldRejectIdWithVersionSuffix() {
    assertThat(MouseEnsemblGeneId.isFullId(ENSA_MOUSE_ENSEMBL_GENE_ID + ".1")).isFalse();
  }

  @Test
  @DisplayName("should reject a human ensembl gene id")
  void shouldRejectHumanEnsemblGeneId() {
    assertThat(MouseEnsemblGeneId.isFullId("ENSG00000130203")).isFalse();
  }

  @Test
  @DisplayName("should reject a marmoset ensembl gene id")
  void shouldRejectMarmosetEnsemblGeneId() {
    assertThat(MouseEnsemblGeneId.isFullId("ENSCJAG00000003645")).isFalse();
  }

  @Test
  @DisplayName("should reject an id embedded in a longer term")
  void shouldRejectIdEmbeddedInLongerTerm() {
    assertThat(MouseEnsemblGeneId.isFullId("x" + ENSA_MOUSE_ENSEMBL_GENE_ID)).isFalse();
  }

  @Test
  @DisplayName("should reject null")
  void shouldRejectNull() {
    assertThat(MouseEnsemblGeneId.isFullId(null)).isFalse();
  }
}
