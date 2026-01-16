package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelIdentifierTypeDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.GeneExpressionIndividualMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.IndividualDataMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.GeneExpressionIndividualRepository;

@ExtendWith(MockitoExtension.class)
class GeneExpressionIndividualServiceTest {

  @Mock
  private GeneExpressionIndividualRepository repository;

  private GeneExpressionIndividualService service;
  private GeneExpressionIndividualMapper mapper;

  private static final String ENSEMBL_GENE_ID = "ENSG00000001";
  private static final String TISSUE = "brain";
  private static final String MODEL_NAME = "5XFAD";
  private static final String MODEL_GROUP = "AD";

  @BeforeEach
  void setUp() {
    mapper = new GeneExpressionIndividualMapper(new IndividualDataMapper());
    service = new GeneExpressionIndividualService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty list when no documents found for name query")
  void shouldReturnEmptyListWhenNoDocumentsFoundForNameQuery() {
    // given
    when(
      repository.findByEnsemblGeneIdAndNameAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of());

    GeneExpressionIndividualFilterQueryDto query = GeneExpressionIndividualFilterQueryDto.builder()
      .ensemblGeneId(ENSEMBL_GENE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    List<GeneExpressionIndividualDto> result = service.getGeneExpressionIndividual(query);

    // then
    assertThat(result).isEmpty();
    verify(repository).findByEnsemblGeneIdAndNameAndTissue(ENSEMBL_GENE_ID, MODEL_NAME, TISSUE);
  }

  @Test
  @DisplayName("should return empty list when no documents found for model_group query")
  void shouldReturnEmptyListWhenNoDocumentsFoundForModelGroupQuery() {
    // given
    when(
      repository.findByEnsemblGeneIdAndModelGroupAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of());

    GeneExpressionIndividualFilterQueryDto query = GeneExpressionIndividualFilterQueryDto.builder()
      .ensemblGeneId(ENSEMBL_GENE_ID)
      .modelIdentifier(MODEL_GROUP)
      .modelIdentifierType(ModelIdentifierTypeDto.MODEL_GROUP)
      .tissue(TISSUE)
      .build();

    // when
    List<GeneExpressionIndividualDto> result = service.getGeneExpressionIndividual(query);

    // then
    assertThat(result).isEmpty();
    verify(repository).findByEnsemblGeneIdAndModelGroupAndTissue(
      ENSEMBL_GENE_ID,
      MODEL_GROUP,
      TISSUE
    );
  }

  @Test
  @DisplayName("should return documents when found by name")
  void shouldReturnDocumentsWhenFoundByName() {
    // given
    GeneExpressionIndividualDocument doc = createGeneExpressionIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      6
    );
    when(
      repository.findByEnsemblGeneIdAndNameAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of(doc));

    GeneExpressionIndividualFilterQueryDto query = GeneExpressionIndividualFilterQueryDto.builder()
      .ensemblGeneId(ENSEMBL_GENE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    List<GeneExpressionIndividualDto> result = service.getGeneExpressionIndividual(query);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEnsemblGeneId()).isEqualTo(ENSEMBL_GENE_ID);
    assertThat(result.get(0).getName()).isEqualTo(MODEL_NAME);
    assertThat(result.get(0).getTissue()).isEqualTo(TISSUE);
    verify(repository).findByEnsemblGeneIdAndNameAndTissue(ENSEMBL_GENE_ID, MODEL_NAME, TISSUE);
  }

  @Test
  @DisplayName("should return documents when found by model_group")
  void shouldReturnDocumentsWhenFoundByModelGroup() {
    // given
    GeneExpressionIndividualDocument doc = createGeneExpressionIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      MODEL_GROUP,
      6
    );
    when(
      repository.findByEnsemblGeneIdAndModelGroupAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of(doc));

    GeneExpressionIndividualFilterQueryDto query = GeneExpressionIndividualFilterQueryDto.builder()
      .ensemblGeneId(ENSEMBL_GENE_ID)
      .modelIdentifier(MODEL_GROUP)
      .modelIdentifierType(ModelIdentifierTypeDto.MODEL_GROUP)
      .tissue(TISSUE)
      .build();

    // when
    List<GeneExpressionIndividualDto> result = service.getGeneExpressionIndividual(query);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEnsemblGeneId()).isEqualTo(ENSEMBL_GENE_ID);
    assertThat(result.get(0).getModelGroup()).isEqualTo(MODEL_GROUP);
    assertThat(result.get(0).getTissue()).isEqualTo(TISSUE);
    verify(repository).findByEnsemblGeneIdAndModelGroupAndTissue(
      ENSEMBL_GENE_ID,
      MODEL_GROUP,
      TISSUE
    );
  }

  @Test
  @DisplayName("should sort results by age_numeric in ascending order")
  void shouldSortResultsByAgeNumericAscending() {
    // given
    GeneExpressionIndividualDocument doc1 = createGeneExpressionIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      12
    );
    GeneExpressionIndividualDocument doc2 = createGeneExpressionIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      6
    );
    GeneExpressionIndividualDocument doc3 = createGeneExpressionIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      18
    );

    when(
      repository.findByEnsemblGeneIdAndNameAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of(doc1, doc2, doc3));

    GeneExpressionIndividualFilterQueryDto query = GeneExpressionIndividualFilterQueryDto.builder()
      .ensemblGeneId(ENSEMBL_GENE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    List<GeneExpressionIndividualDto> result = service.getGeneExpressionIndividual(query);

    // then
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getAgeNumeric()).isEqualTo(6);
    assertThat(result.get(1).getAgeNumeric()).isEqualTo(12);
    assertThat(result.get(2).getAgeNumeric()).isEqualTo(18);
  }

  @Test
  @DisplayName("should throw IllegalArgumentException for invalid modelIdentifierType")
  void shouldThrowExceptionForInvalidModelIdentifierType() {
    // given
    GeneExpressionIndividualFilterQueryDto query = GeneExpressionIndividualFilterQueryDto.builder()
      .ensemblGeneId(ENSEMBL_GENE_ID)
      .modelIdentifier("INVALID")
      .modelIdentifierType(null)
      .tissue(TISSUE)
      .build();

    // when/then
    assertThatThrownBy(() -> service.getGeneExpressionIndividual(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Invalid modelIdentifierType");
  }

  private GeneExpressionIndividualDocument createGeneExpressionIndividualDocument(
    String ensemblGeneId,
    String tissue,
    String name,
    String modelGroup,
    Integer ageNumeric
  ) {
    GeneExpressionIndividualDocument doc = new GeneExpressionIndividualDocument();
    doc.setId(new ObjectId());
    doc.setEnsemblGeneId(ensemblGeneId);
    doc.setGeneSymbol("GENE1");
    doc.setTissue(tissue);
    doc.setName(name);
    doc.setModelGroup(modelGroup);
    doc.setMatchedControl("Control1");
    doc.setUnits("TPM");
    doc.setAge(ageNumeric + "mo");
    doc.setAgeNumeric(ageNumeric);
    doc.setResultOrder(List.of("sample1", "sample2"));
    doc.setData(List.of(createIndividualData("sample1", "WT", "Female", 10.5)));
    return doc;
  }

  private IndividualData createIndividualData(
    String individualId,
    String genotype,
    String sex,
    Double value
  ) {
    return IndividualData.builder()
      .individualId(individualId)
      .genotype(genotype)
      .sex(sex)
      .value(BigDecimal.valueOf(value))
      .build();
  }
}
