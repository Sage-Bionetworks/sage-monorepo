package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.sagebionetworks.model.ad.api.next.api.*;
import org.sagebionetworks.model.ad.api.next.exception.ErrorConstants;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.exception.InvalidObjectIdException;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument.CorrelationResult;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.DiseaseCorrelationRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DiseaseCorrelationApiDelegateImplTest {

  @Mock
  private DiseaseCorrelationRepository repository;

  private DiseaseCorrelationApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    DiseaseCorrelationService queryService = new DiseaseCorrelationService(
      repository,
      new DiseaseCorrelationMapper()
    );
    delegate = new DiseaseCorrelationApiDelegateImpl(queryService);
  }

  @Test
  @DisplayName("should throw bad request when category missing subcategory")
  void shouldThrowBadRequestWhenCategoryMissingSubcategory() {
    assertThatThrownBy(() ->
      delegate.getDiseaseCorrelations(
        List.of(ErrorConstants.SUPPORTED_CATEGORY),
        null,
        ItemFilterTypeQueryDto.INCLUDE
      )
    ).isInstanceOf(InvalidCategoryException.class);

    verifyNoInteractions(repository);
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
    ).isInstanceOf(InvalidCategoryException.class);

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return empty list when include filter has no items")
  void shouldReturnEmptyListWhenIncludeFilterHasNoItems() {
    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster A"),
      null,
      ItemFilterTypeQueryDto.INCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    ObjectId objectId = new ObjectId();
    DiseaseCorrelationDocument document = buildDocument(objectId);

    when(repository.findByClusterAndIdIn(eq("Cluster A"), anyList())).thenReturn(List.of(document));

    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster A"),
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

    verify(repository).findByClusterAndIdIn(eq("Cluster A"), eq(List.of(objectId)));
  }

  @Test
  @DisplayName("should include cluster filter when exclude filter has no items")
  void shouldIncludeClusterFilterWhenExcludeFilterHasNoItems() {
    ObjectId objectId = new ObjectId();
    when(repository.findByCluster("Cluster B")).thenReturn(List.of(buildDocument(objectId)));

    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster B"),
      List.of(),
      ItemFilterTypeQueryDto.EXCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    verify(repository).findByCluster("Cluster B");
  }

  @Test
  @DisplayName("should throw bad request when items contain invalid object id")
  void shouldThrowBadRequestWhenItemsContainInvalidObjectId() {
    assertThatThrownBy(() ->
      delegate.getDiseaseCorrelations(
        List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster A"),
        List.of("not-an-id"),
        ItemFilterTypeQueryDto.INCLUDE
      )
    ).isInstanceOf(InvalidObjectIdException.class);

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should omit correlation data when values incomplete")
  void shouldOmitCorrelationDataWhenValuesIncomplete() {
    ObjectId objectId = new ObjectId();
    DiseaseCorrelationDocument document = buildDocumentWithPartialCorrelation(objectId);

    when(repository.findByClusterAndIdIn("Cluster C", List.of(objectId))).thenReturn(
      List.of(document)
    );

    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster C"),
      List.of(objectId.toHexString()),
      ItemFilterTypeQueryDto.INCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    DiseaseCorrelationDto dto = response.getBody().get(0);
    assertThat(dto.getIFG()).isNull();
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    ObjectId excludedId = new ObjectId();
    ObjectId includedId = new ObjectId();
    DiseaseCorrelationDocument includedDocument = buildDocument(includedId);

    when(repository.findByClusterAndIdNotIn("Cluster D", List.of(excludedId))).thenReturn(
      List.of(includedDocument)
    );

    ResponseEntity<List<DiseaseCorrelationDto>> response = delegate.getDiseaseCorrelations(
      List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster D"),
      List.of(excludedId.toHexString()),
      ItemFilterTypeQueryDto.EXCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    DiseaseCorrelationDto dto = response.getBody().get(0);
    assertThat(dto.getId()).isEqualTo(includedId.toHexString());

    verify(repository).findByClusterAndIdNotIn("Cluster D", List.of(excludedId));
  }

  private DiseaseCorrelationDocument buildDocument(ObjectId objectId) {
    CorrelationResult correlation = new CorrelationResult();
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
    CorrelationResult correlation = new CorrelationResult();
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
