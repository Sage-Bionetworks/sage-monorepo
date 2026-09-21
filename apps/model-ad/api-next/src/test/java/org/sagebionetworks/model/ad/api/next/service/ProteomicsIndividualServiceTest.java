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
import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelIdentifierTypeDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.IndividualDataMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.ProteomicsIndividualMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ProteomicsIndividualRepository;

@ExtendWith(MockitoExtension.class)
class ProteomicsIndividualServiceTest {

  @Mock
  private ProteomicsIndividualRepository repository;

  private ProteomicsIndividualService service;
  private ProteomicsIndividualMapper mapper;

  private static final String UNIQUE_ID = "ENSMUSG00000000001P27144";
  private static final String TISSUE = "Hemibrain";
  private static final String MODEL_NAME = "LOAD2";
  private static final String MODEL_GROUP = "LOAD";

  @BeforeEach
  void setUp() {
    mapper = new ProteomicsIndividualMapper(new IndividualDataMapper());
    service = new ProteomicsIndividualService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty list when no documents found for name query")
  void shouldReturnEmptyListWhenNoDocumentsFoundForNameQuery() {
    // given
    when(
      repository.findByUniqueIdAndNameAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of());

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    List<ProteomicsIndividualDto> result = service.getProteomicsIndividual(query);

    // then
    assertThat(result).isEmpty();
    verify(repository).findByUniqueIdAndNameAndTissue(UNIQUE_ID, MODEL_NAME, TISSUE);
  }

  @Test
  @DisplayName("should return empty list when no documents found for model_group query")
  void shouldReturnEmptyListWhenNoDocumentsFoundForModelGroupQuery() {
    // given
    when(
      repository.findByUniqueIdAndModelGroupAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of());

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_GROUP)
      .modelIdentifierType(ModelIdentifierTypeDto.MODEL_GROUP)
      .tissue(TISSUE)
      .build();

    // when
    List<ProteomicsIndividualDto> result = service.getProteomicsIndividual(query);

    // then
    assertThat(result).isEmpty();
    verify(repository).findByUniqueIdAndModelGroupAndTissue(UNIQUE_ID, MODEL_GROUP, TISSUE);
  }

  @Test
  @DisplayName("should return documents when found by name")
  void shouldReturnDocumentsWhenFoundByName() {
    // given
    ProteomicsIndividualDocument doc = createProteomicsIndividualDocument(
      UNIQUE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      4
    );
    when(
      repository.findByUniqueIdAndNameAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of(doc));

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    List<ProteomicsIndividualDto> result = service.getProteomicsIndividual(query);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUniqueId()).isEqualTo(UNIQUE_ID);
    assertThat(result.get(0).getName()).isEqualTo(MODEL_NAME);
    assertThat(result.get(0).getTissue()).isEqualTo(TISSUE);
    verify(repository).findByUniqueIdAndNameAndTissue(UNIQUE_ID, MODEL_NAME, TISSUE);
  }

  @Test
  @DisplayName("should return documents when found by model_group")
  void shouldReturnDocumentsWhenFoundByModelGroup() {
    // given
    ProteomicsIndividualDocument doc = createProteomicsIndividualDocument(
      UNIQUE_ID,
      TISSUE,
      MODEL_NAME,
      MODEL_GROUP,
      4
    );
    when(
      repository.findByUniqueIdAndModelGroupAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of(doc));

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_GROUP)
      .modelIdentifierType(ModelIdentifierTypeDto.MODEL_GROUP)
      .tissue(TISSUE)
      .build();

    // when
    List<ProteomicsIndividualDto> result = service.getProteomicsIndividual(query);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUniqueId()).isEqualTo(UNIQUE_ID);
    assertThat(result.get(0).getModelGroup()).isEqualTo(MODEL_GROUP);
    assertThat(result.get(0).getTissue()).isEqualTo(TISSUE);
    verify(repository).findByUniqueIdAndModelGroupAndTissue(UNIQUE_ID, MODEL_GROUP, TISSUE);
  }

  @Test
  @DisplayName("should sort results by age_numeric in ascending order")
  void shouldSortResultsByAgeNumericAscending() {
    // given
    ProteomicsIndividualDocument doc1 = createProteomicsIndividualDocument(
      UNIQUE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      12
    );
    ProteomicsIndividualDocument doc2 = createProteomicsIndividualDocument(
      UNIQUE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      4
    );
    ProteomicsIndividualDocument doc3 = createProteomicsIndividualDocument(
      UNIQUE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      18
    );

    when(
      repository.findByUniqueIdAndNameAndTissue(anyString(), anyString(), anyString())
    ).thenReturn(List.of(doc1, doc2, doc3));

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    List<ProteomicsIndividualDto> result = service.getProteomicsIndividual(query);

    // then
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getAgeNumeric()).isEqualTo(4);
    assertThat(result.get(1).getAgeNumeric()).isEqualTo(12);
    assertThat(result.get(2).getAgeNumeric()).isEqualTo(18);
  }

  @Test
  @DisplayName("should throw IllegalArgumentException for invalid modelIdentifierType")
  void shouldThrowExceptionForInvalidModelIdentifierType() {
    // given
    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier("INVALID")
      .modelIdentifierType(null)
      .tissue(TISSUE)
      .build();

    // when/then
    assertThatThrownBy(() -> service.getProteomicsIndividual(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Invalid modelIdentifierType");
  }

  private ProteomicsIndividualDocument createProteomicsIndividualDocument(
    String uniqueId,
    String tissue,
    String name,
    String modelGroup,
    Integer ageNumeric
  ) {
    ProteomicsIndividualDocument doc = new ProteomicsIndividualDocument();
    doc.setId(new ObjectId());
    doc.setEnsemblGeneId("ENSMUSG00000000001");
    doc.setGeneSymbol("Gnai3");
    doc.setUniprotid("P27144");
    doc.setUniqueId(uniqueId);
    doc.setDisplaySymbol("Gnai3 (P27144)");
    doc.setTissue(tissue);
    doc.setName(name);
    doc.setModelGroup(modelGroup);
    doc.setMatchedControl("LOAD1");
    doc.setUnits("Log2 Counts per Million");
    doc.setAge(ageNumeric + " months");
    doc.setAgeNumeric(ageNumeric);
    doc.setResultOrder(List.of("LOAD1", "LOAD2"));
    doc.setData(List.of(createIndividualData("sample1", "LOAD2", "Female", 10.5)));
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
