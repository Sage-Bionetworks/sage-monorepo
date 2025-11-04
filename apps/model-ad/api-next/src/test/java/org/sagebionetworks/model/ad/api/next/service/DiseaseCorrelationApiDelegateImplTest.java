package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.document.CorrelationResultDocument;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DiseaseCorrelationApiDelegateImplTest {

  @Mock
  private MongoTemplate mongoTemplate;

  private DiseaseCorrelationApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    DiseaseCorrelationQueryService queryService = new DiseaseCorrelationQueryService(
      mongoTemplate,
      new DiseaseCorrelationMapper()
    );
    delegate = new DiseaseCorrelationApiDelegateImpl(queryService);
  }

  @Test
  @DisplayName("should throw bad request when category missing subcategory")
  void shouldThrowBadRequestWhenCategoryMissingSubcategory() {
    assertThatThrownBy(() ->
      delegate.getDiseaseCorrelations(
        List.of(DiseaseCorrelationApiDelegateImpl.SUPPORTED_CATEGORY),
        null,
        ItemFilterTypeQueryDto.INCLUDE
      )
    )
      .isInstanceOf(ResponseStatusException.class)
      .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
      .isEqualTo(HttpStatus.BAD_REQUEST);

    verifyNoInteractions(mongoTemplate);
  }

  @Test
  @DisplayName("should throw bad request when category unsupported")
  void shouldThrowBadRequestWhenCategoryUnsupported() {
    assertThatThrownBy(() ->
      delegate.getDiseaseCorrelations(
        List.of("OTHER", "Cluster A"),
        null,
        ItemFilterTypeQueryDto.INCLUDE
      )
    )
      .isInstanceOf(ResponseStatusException.class)
      .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
      .isEqualTo(HttpStatus.BAD_REQUEST);

    verifyNoInteractions(mongoTemplate);
  }

  @Test
  @DisplayName("should return empty list when include filter has no items")
  void shouldReturnEmptyListWhenIncludeFilterHasNoItems() {
    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(DiseaseCorrelationApiDelegateImpl.SUPPORTED_CATEGORY, "Cluster A"),
      null,
      ItemFilterTypeQueryDto.INCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isEqualTo(0);
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verifyNoInteractions(mongoTemplate);
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    ObjectId objectId = new ObjectId();
    DiseaseCorrelationDocument document = buildDocument(objectId);

    when(mongoTemplate.find(any(Query.class), eq(DiseaseCorrelationDocument.class))).thenReturn(
      List.of(document)
    );

    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(DiseaseCorrelationApiDelegateImpl.SUPPORTED_CATEGORY, "Cluster A"),
      List.of(objectId.toHexString()),
      ItemFilterTypeQueryDto.INCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    DiseaseCorrelationDto dto = response.getBody().get(0);
    assertThat(dto.getId()).isEqualTo(objectId.toHexString());
    assertThat(dto.getName()).isEqualTo("Model 1");
    assertThat(dto.getMatchedControl()).isEqualTo("Control A");
    assertThat(dto.getIFG()).isNotNull();
    assertThat(dto.getIFG().getCorrelation()).isEqualTo(BigDecimal.valueOf(0.87d));
    assertThat(dto.getSex().getValue()).isEqualTo("Female");

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(DiseaseCorrelationDocument.class));
    Document queryDocument = queryCaptor.getValue().getQueryObject();
    Document expectedQuery = new Document("cluster", "Cluster A").append(
      "_id",
      new Document("$in", List.of(objectId))
    );
    assertThat(queryDocument).isEqualTo(expectedQuery);
  }

  @Test
  @DisplayName("should include cluster filter when exclude filter has no items")
  void shouldIncludeClusterFilterWhenExcludeFilterHasNoItems() {
    ObjectId objectId = new ObjectId();
    when(mongoTemplate.find(any(Query.class), eq(DiseaseCorrelationDocument.class))).thenReturn(
      List.of(buildDocument(objectId))
    );

    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(DiseaseCorrelationApiDelegateImpl.SUPPORTED_CATEGORY, "Cluster B"),
      List.of(),
      ItemFilterTypeQueryDto.EXCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(DiseaseCorrelationDocument.class));
    Document queryDocument = queryCaptor.getValue().getQueryObject();
    Document expectedQuery = new Document("cluster", "Cluster B");
    assertThat(queryDocument).isEqualTo(expectedQuery);
  }

  @Test
  @DisplayName("should throw bad request when items contain invalid object id")
  void shouldThrowBadRequestWhenItemsContainInvalidObjectId() {
    assertThatThrownBy(() ->
      delegate.getDiseaseCorrelations(
        List.of(DiseaseCorrelationApiDelegateImpl.SUPPORTED_CATEGORY, "Cluster A"),
        List.of("not-an-id"),
        ItemFilterTypeQueryDto.INCLUDE
      )
    )
      .isInstanceOf(ResponseStatusException.class)
      .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
      .isEqualTo(HttpStatus.BAD_REQUEST);

    verifyNoInteractions(mongoTemplate);
  }

  @Test
  @DisplayName("should omit correlation data when values incomplete")
  void shouldOmitCorrelationDataWhenValuesIncomplete() {
    ObjectId objectId = new ObjectId();
    DiseaseCorrelationDocument document = buildDocumentWithPartialCorrelation(objectId);

    when(mongoTemplate.find(any(Query.class), eq(DiseaseCorrelationDocument.class))).thenReturn(
      List.of(document)
    );

    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(DiseaseCorrelationApiDelegateImpl.SUPPORTED_CATEGORY, "Cluster C"),
      List.of(objectId.toHexString()),
      ItemFilterTypeQueryDto.INCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    DiseaseCorrelationDto dto = response.getBody().get(0);
    assertThat(dto.getIFG()).isNull();
  }

  private DiseaseCorrelationDocument buildDocument(ObjectId objectId) {
    CorrelationResultDocument correlation = new CorrelationResultDocument();
    correlation.setCorrelation(0.87d);
    correlation.setAdjustedPvalue(0.01d);

    DiseaseCorrelationDocument document = new DiseaseCorrelationDocument();
    document.setId(objectId);
    document.setName("Model 1");
    document.setMatchedControl("Control A");
    document.setModelType("Type X");
    document.setModifiedGenes(List.of("Gene 1", "Gene 2"));
    document.setCluster("Cluster A");
    document.setAge("10 weeks");
    document.setSex("Female");
    document.setIfg(correlation);
    return document;
  }

  private DiseaseCorrelationDocument buildDocumentWithPartialCorrelation(ObjectId objectId) {
    CorrelationResultDocument correlation = new CorrelationResultDocument();
    correlation.setCorrelation(0.5d);

    DiseaseCorrelationDocument document = new DiseaseCorrelationDocument();
    document.setId(objectId);
    document.setName("Model 2");
    document.setMatchedControl("Control B");
    document.setModelType("Type Y");
    document.setModifiedGenes(List.of("Gene 3"));
    document.setCluster("Cluster C");
    document.setAge("12 weeks");
    document.setSex("Male");
    document.setIfg(correlation);
    return document;
  }
}
