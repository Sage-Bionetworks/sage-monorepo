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
import org.sagebionetworks.model.ad.api.next.api.TranscriptomicsIndividualApiDelegateImpl;
import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelIdentifierTypeDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.IndividualDataMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.TranscriptomicsIndividualMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.TranscriptomicsIndividualRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class TranscriptomicsIndividualApiDelegateImplTest {

  private static final String ENSEMBL_GENE_ID = "ENSG00000001";
  private static final String TISSUE = "brain";
  private static final String MODEL_NAME = "5XFAD";
  private static final String MODEL_GROUP = "AD";

  @Mock
  private TranscriptomicsIndividualRepository repository;

  private TranscriptomicsIndividualApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    // Mock the request context for validation
    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    TranscriptomicsIndividualService service = new TranscriptomicsIndividualService(
      repository,
      new TranscriptomicsIndividualMapper(new IndividualDataMapper())
    );
    delegate = new TranscriptomicsIndividualApiDelegateImpl(service);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should return empty list when no matching documents")
  void shouldReturnEmptyListWhenNoMatchingDocuments() {
    // given
    when(
      repository.findByEnsemblGeneIdAndNameAndTissue(ENSEMBL_GENE_ID, MODEL_NAME, TISSUE)
    ).thenReturn(List.of());

    TranscriptomicsIndividualFilterQueryDto query =
      TranscriptomicsIndividualFilterQueryDto.builder()
        .ensemblGeneId(ENSEMBL_GENE_ID)
        .modelIdentifier(MODEL_NAME)
        .modelIdentifierType(ModelIdentifierTypeDto.NAME)
        .tissue(TISSUE)
        .build();

    // when
    ResponseEntity<List<TranscriptomicsIndividualDto>> response =
      delegate.getTranscriptomicsIndividual(query);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
    assertThat(response.getHeaders().getCacheControl()).contains("no-cache");
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verify(repository).findByEnsemblGeneIdAndNameAndTissue(ENSEMBL_GENE_ID, MODEL_NAME, TISSUE);
  }

  @Test
  @DisplayName("should return transcriptomics individual data by name")
  void shouldReturnTranscriptomicsIndividualDataByName() {
    // given
    TranscriptomicsIndividualDocument doc = createTranscriptomicsIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      6
    );
    when(
      repository.findByEnsemblGeneIdAndNameAndTissue(ENSEMBL_GENE_ID, MODEL_NAME, TISSUE)
    ).thenReturn(List.of(doc));

    TranscriptomicsIndividualFilterQueryDto query =
      TranscriptomicsIndividualFilterQueryDto.builder()
        .ensemblGeneId(ENSEMBL_GENE_ID)
        .modelIdentifier(MODEL_NAME)
        .modelIdentifierType(ModelIdentifierTypeDto.NAME)
        .tissue(TISSUE)
        .build();

    // when
    ResponseEntity<List<TranscriptomicsIndividualDto>> response =
      delegate.getTranscriptomicsIndividual(query);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);

    TranscriptomicsIndividualDto result = response.getBody().get(0);
    assertThat(result.getEnsemblGeneId()).isEqualTo(ENSEMBL_GENE_ID);
    assertThat(result.getName()).isEqualTo(MODEL_NAME);
    assertThat(result.getTissue()).isEqualTo(TISSUE);
    assertThat(result.getAgeNumeric()).isEqualTo(6);

    verify(repository).findByEnsemblGeneIdAndNameAndTissue(ENSEMBL_GENE_ID, MODEL_NAME, TISSUE);
  }

  @Test
  @DisplayName("should return transcriptomics individual data by model_group")
  void shouldReturnTranscriptomicsIndividualDataByModelGroup() {
    // given
    TranscriptomicsIndividualDocument doc = createTranscriptomicsIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      MODEL_GROUP,
      6
    );
    when(
      repository.findByEnsemblGeneIdAndModelGroupAndTissue(ENSEMBL_GENE_ID, MODEL_GROUP, TISSUE)
    ).thenReturn(List.of(doc));

    TranscriptomicsIndividualFilterQueryDto query =
      TranscriptomicsIndividualFilterQueryDto.builder()
        .ensemblGeneId(ENSEMBL_GENE_ID)
        .modelIdentifier(MODEL_GROUP)
        .modelIdentifierType(ModelIdentifierTypeDto.MODEL_GROUP)
        .tissue(TISSUE)
        .build();

    // when
    ResponseEntity<List<TranscriptomicsIndividualDto>> response =
      delegate.getTranscriptomicsIndividual(query);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);

    TranscriptomicsIndividualDto result = response.getBody().get(0);
    assertThat(result.getEnsemblGeneId()).isEqualTo(ENSEMBL_GENE_ID);
    assertThat(result.getModelGroup()).isEqualTo(MODEL_GROUP);
    assertThat(result.getTissue()).isEqualTo(TISSUE);

    verify(repository).findByEnsemblGeneIdAndModelGroupAndTissue(
      ENSEMBL_GENE_ID,
      MODEL_GROUP,
      TISSUE
    );
  }

  @Test
  @DisplayName("should return multiple transcriptomics individual records sorted by age_numeric")
  void shouldReturnMultipleRecordsSortedByAgeNumeric() {
    // given
    TranscriptomicsIndividualDocument doc1 = createTranscriptomicsIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      12
    );
    TranscriptomicsIndividualDocument doc2 = createTranscriptomicsIndividualDocument(
      ENSEMBL_GENE_ID,
      TISSUE,
      MODEL_NAME,
      null,
      6
    );
    when(
      repository.findByEnsemblGeneIdAndNameAndTissue(ENSEMBL_GENE_ID, MODEL_NAME, TISSUE)
    ).thenReturn(List.of(doc1, doc2));

    TranscriptomicsIndividualFilterQueryDto query =
      TranscriptomicsIndividualFilterQueryDto.builder()
        .ensemblGeneId(ENSEMBL_GENE_ID)
        .modelIdentifier(MODEL_NAME)
        .modelIdentifierType(ModelIdentifierTypeDto.NAME)
        .tissue(TISSUE)
        .build();

    // when
    ResponseEntity<List<TranscriptomicsIndividualDto>> response =
      delegate.getTranscriptomicsIndividual(query);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody().get(0).getAgeNumeric()).isEqualTo(6);
    assertThat(response.getBody().get(1).getAgeNumeric()).isEqualTo(12);
  }

  @Test
  @DisplayName("should include cache control headers in response")
  void shouldIncludeCacheControlHeadersInResponse() {
    // given
    when(
      repository.findByEnsemblGeneIdAndNameAndTissue(ENSEMBL_GENE_ID, MODEL_NAME, TISSUE)
    ).thenReturn(List.of());

    TranscriptomicsIndividualFilterQueryDto query =
      TranscriptomicsIndividualFilterQueryDto.builder()
        .ensemblGeneId(ENSEMBL_GENE_ID)
        .modelIdentifier(MODEL_NAME)
        .modelIdentifierType(ModelIdentifierTypeDto.NAME)
        .tissue(TISSUE)
        .build();

    // when
    ResponseEntity<List<TranscriptomicsIndividualDto>> response =
      delegate.getTranscriptomicsIndividual(query);

    // then
    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).contains("no-cache");
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  private TranscriptomicsIndividualDocument createTranscriptomicsIndividualDocument(
    String ensemblGeneId,
    String tissue,
    String name,
    String modelGroup,
    Integer ageNumeric
  ) {
    TranscriptomicsIndividualDocument doc = new TranscriptomicsIndividualDocument();
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
