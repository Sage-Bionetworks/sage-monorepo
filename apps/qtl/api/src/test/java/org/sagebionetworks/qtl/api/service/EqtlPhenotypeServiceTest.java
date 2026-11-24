package org.sagebionetworks.qtl.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.qtl.api.exception.EqtlPhenotypeNotFoundException;
import org.sagebionetworks.qtl.api.model.document.EqtlPhenotypeDocument;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeQueryDto;
import org.sagebionetworks.qtl.api.model.mapper.EqtlPhenotypeMapper;
import org.sagebionetworks.qtl.api.model.repository.EqtlPhenotypeRepository;

@ExtendWith(MockitoExtension.class)
class EqtlPhenotypeServiceTest {

  private static final String VARIANT = "rs2120461";
  private static final String ENSEMBL_GENE_ID = "ENSG00000142599";
  private static final String CELL_TYPE_ID = "astro";

  private static final EqtlPhenotypeQueryDto QUERY = EqtlPhenotypeQueryDto.builder()
    .variant(VARIANT)
    .ensemblGeneId(ENSEMBL_GENE_ID)
    .cellTypeId(CELL_TYPE_ID)
    .build();

  @Mock
  private EqtlPhenotypeRepository repository;

  @Mock
  private EqtlPhenotypeMapper mapper;

  private EqtlPhenotypeService service;

  @BeforeEach
  void setUp() {
    service = new EqtlPhenotypeService(repository, mapper);
  }

  @Test
  @DisplayName("should return the mapped phenotype when a document matches the eQTL")
  void shouldReturnTheMappedPhenotypeWhenADocumentMatchesTheEqtl() {
    // given
    EqtlPhenotypeDocument document = new EqtlPhenotypeDocument();
    EqtlPhenotypeDto dto = EqtlPhenotypeDto.builder().variant(VARIANT).build();
    when(
      repository.findByVariantAndEnsemblGeneIdAndCellTypeId(VARIANT, ENSEMBL_GENE_ID, CELL_TYPE_ID)
    ).thenReturn(Optional.of(document));
    when(mapper.toDto(document)).thenReturn(dto);

    // when
    EqtlPhenotypeDto result = service.getEqtlPhenotype(QUERY);

    // then
    assertThat(result).isSameAs(dto);
  }

  @Test
  @DisplayName("should throw not found naming the eQTL when no document matches it")
  void shouldThrowNotFoundNamingTheEqtlWhenNoDocumentMatchesIt() {
    // given
    when(
      repository.findByVariantAndEnsemblGeneIdAndCellTypeId(VARIANT, ENSEMBL_GENE_ID, CELL_TYPE_ID)
    ).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> service.getEqtlPhenotype(QUERY))
      .isInstanceOf(EqtlPhenotypeNotFoundException.class)
      .hasMessage(
        "eQTL phenotype not found for variant: rs2120461, ensemblGeneId: ENSG00000142599, " +
        "cellTypeId: astro"
      );
  }
}
