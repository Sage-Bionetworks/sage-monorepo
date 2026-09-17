package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.api.ProteomicsIndividualApiDelegateImpl;
import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelIdentifierTypeDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.IndividualDataMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.ProteomicsIndividualMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ProteomicsIndividualRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class ProteomicsIndividualApiDelegateImplTest {

  private static final String UNIQUE_ID = "ENSMUSG00000000001P27144";
  private static final String TISSUE = "Hemibrain";
  private static final String MODEL_NAME = "LOAD2";
  private static final String MODEL_GROUP = "LOAD";

  @Mock
  private ProteomicsIndividualRepository repository;

  private ProteomicsIndividualApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    // Mock the request context for validation
    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    ProteomicsIndividualService service = new ProteomicsIndividualService(
      repository,
      new ProteomicsIndividualMapper(new IndividualDataMapper())
    );
    delegate = new ProteomicsIndividualApiDelegateImpl(service);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should return empty list when no matching documents")
  void shouldReturnEmptyListWhenNoMatchingDocuments() {
    // given
    when(repository.findByUniqueIdAndNameAndTissue(UNIQUE_ID, MODEL_NAME, TISSUE)).thenReturn(
      List.of()
    );

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    ResponseEntity<List<ProteomicsIndividualDto>> response = delegate.getProteomicsIndividual(
      query
    );

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
    assertThat(response.getHeaders().getCacheControl()).contains("no-cache");
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verify(repository).findByUniqueIdAndNameAndTissue(UNIQUE_ID, MODEL_NAME, TISSUE);
  }

  @Test
  @DisplayName("should return proteomics individual data by name")
  void shouldReturnProteomicsIndividualDataByName() {
    // given
    ProteomicsIndividualDocument doc = createProteomicsIndividualDocument(
      UNIQUE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      4
    );
    when(repository.findByUniqueIdAndNameAndTissue(UNIQUE_ID, MODEL_NAME, TISSUE)).thenReturn(
      List.of(doc)
    );

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    ResponseEntity<List<ProteomicsIndividualDto>> response = delegate.getProteomicsIndividual(
      query
    );

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);

    ProteomicsIndividualDto result = response.getBody().get(0);
    assertThat(result.getUniqueId()).isEqualTo(UNIQUE_ID);
    assertThat(result.getName()).isEqualTo(MODEL_NAME);
    assertThat(result.getTissue()).isEqualTo(TISSUE);
    assertThat(result.getAgeNumeric()).isEqualTo(4);

    verify(repository).findByUniqueIdAndNameAndTissue(UNIQUE_ID, MODEL_NAME, TISSUE);
  }

  @Test
  @DisplayName("should return proteomics individual data by model_group")
  void shouldReturnProteomicsIndividualDataByModelGroup() {
    // given
    ProteomicsIndividualDocument doc = createProteomicsIndividualDocument(
      UNIQUE_ID,
      TISSUE,
      MODEL_NAME,
      MODEL_GROUP,
      4
    );
    when(
      repository.findByUniqueIdAndModelGroupAndTissue(UNIQUE_ID, MODEL_GROUP, TISSUE)
    ).thenReturn(List.of(doc));

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_GROUP)
      .modelIdentifierType(ModelIdentifierTypeDto.MODEL_GROUP)
      .tissue(TISSUE)
      .build();

    // when
    ResponseEntity<List<ProteomicsIndividualDto>> response = delegate.getProteomicsIndividual(
      query
    );

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);

    ProteomicsIndividualDto result = response.getBody().get(0);
    assertThat(result.getUniqueId()).isEqualTo(UNIQUE_ID);
    assertThat(result.getModelGroup()).isEqualTo(MODEL_GROUP);
    assertThat(result.getTissue()).isEqualTo(TISSUE);

    verify(repository).findByUniqueIdAndModelGroupAndTissue(UNIQUE_ID, MODEL_GROUP, TISSUE);
  }

  @Test
  @DisplayName("should return multiple proteomics individual records sorted by age_numeric")
  void shouldReturnMultipleRecordsSortedByAgeNumeric() {
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
    when(repository.findByUniqueIdAndNameAndTissue(UNIQUE_ID, MODEL_NAME, TISSUE)).thenReturn(
      List.of(doc1, doc2)
    );

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    ResponseEntity<List<ProteomicsIndividualDto>> response = delegate.getProteomicsIndividual(
      query
    );

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody().get(0).getAgeNumeric()).isEqualTo(4);
    assertThat(response.getBody().get(1).getAgeNumeric()).isEqualTo(12);
  }

  @Test
  @DisplayName("should include cache control headers in response")
  void shouldIncludeCacheControlHeadersInResponse() {
    // given
    when(repository.findByUniqueIdAndNameAndTissue(UNIQUE_ID, MODEL_NAME, TISSUE)).thenReturn(
      List.of()
    );

    ProteomicsIndividualFilterQueryDto query = ProteomicsIndividualFilterQueryDto.builder()
      .uniqueId(UNIQUE_ID)
      .modelIdentifier(MODEL_NAME)
      .modelIdentifierType(ModelIdentifierTypeDto.NAME)
      .tissue(TISSUE)
      .build();

    // when
    ResponseEntity<List<ProteomicsIndividualDto>> response = delegate.getProteomicsIndividual(
      query
    );

    // then
    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).contains("no-cache");
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
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
