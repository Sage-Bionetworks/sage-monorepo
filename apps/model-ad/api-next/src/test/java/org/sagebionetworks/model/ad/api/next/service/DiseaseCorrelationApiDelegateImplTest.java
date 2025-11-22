package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import org.sagebionetworks.model.ad.api.next.api.DiseaseCorrelationApiDelegateImpl;
import org.sagebionetworks.model.ad.api.next.exception.ErrorConstants;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.exception.InvalidObjectIdException;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument.CorrelationResult;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.DiseaseCorrelationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .category(List.of(ErrorConstants.SUPPORTED_CATEGORY))
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query)).isInstanceOf(
      InvalidCategoryException.class
    );

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw bad request when category unsupported")
  void shouldThrowBadRequestWhenCategoryUnsupported() {
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .category(List.of("OTHER", "Cluster A"))
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query)).isInstanceOf(
      InvalidCategoryException.class
    );

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .category(List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster A"))
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDiseaseCorrelations()).isEmpty();
    assertThat(response.getBody().getPage().getNumber()).isZero();
    assertThat(response.getBody().getPage().getSize()).isEqualTo(100);
    assertThat(response.getBody().getPage().getTotalElements()).isZero();

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
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(document));

    when(
      repository.findByClusterAndIdIn(eq("Cluster A"), anyList(), any(Pageable.class))
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .category(List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster A"))
      .items(List.of(objectId.toHexString()))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDiseaseCorrelations()).hasSize(1);
    assertThat(response.getBody().getPage().getNumber()).isZero();
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(1);

    var dto = response.getBody().getDiseaseCorrelations().get(0);
    assertThat(dto.getId()).isEqualTo(objectId.toHexString());
    assertThat(dto.getName()).isEqualTo("Model 1");
    assertThat(dto.getMatchedControl()).isEqualTo("Control A");
    assertThat(dto.getIFG()).isNotNull();
    assertThat(dto.getIFG().getCorrelation()).isEqualTo(BigDecimal.valueOf(0.87d));
    assertThat(dto.getSex().getValue()).isEqualTo("Female");

    verify(repository).findByClusterAndIdIn(
      eq("Cluster A"),
      eq(List.of(objectId)),
      any(Pageable.class)
    );
  }

  @Test
  @DisplayName("should include cluster filter when exclude filter has no items")
  void shouldIncludeClusterFilterWhenExcludeFilterHasNoItems() {
    ObjectId objectId = new ObjectId();
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(buildDocument(objectId)));

    when(repository.findByCluster(eq("Cluster B"), any(Pageable.class))).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .category(List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster B"))
      .items(List.of())
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDiseaseCorrelations()).hasSize(1);

    verify(repository).findByCluster(eq("Cluster B"), any(Pageable.class));
  }

  @Test
  @DisplayName("should throw bad request when items contain invalid object id")
  void shouldThrowBadRequestWhenItemsContainInvalidObjectId() {
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .category(List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster A"))
      .items(List.of("not-an-id"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query)).isInstanceOf(
      InvalidObjectIdException.class
    );

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should omit correlation data when values incomplete")
  void shouldOmitCorrelationDataWhenValuesIncomplete() {
    ObjectId objectId = new ObjectId();
    DiseaseCorrelationDocument document = buildDocumentWithPartialCorrelation(objectId);
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(document));

    when(
      repository.findByClusterAndIdIn(eq("Cluster C"), anyList(), any(Pageable.class))
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .category(List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster C"))
      .items(List.of(objectId.toHexString()))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDiseaseCorrelations()).hasSize(1);
    var dto = response.getBody().getDiseaseCorrelations().get(0);
    assertThat(dto.getIFG()).isNull();
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    ObjectId excludedId = new ObjectId();
    ObjectId includedId = new ObjectId();
    DiseaseCorrelationDocument includedDocument = buildDocument(includedId);
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(includedDocument));

    when(
      repository.findByClusterAndIdNotIn(eq("Cluster D"), anyList(), any(Pageable.class))
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .category(List.of(ErrorConstants.SUPPORTED_CATEGORY, "Cluster D"))
      .items(List.of(excludedId.toHexString()))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDiseaseCorrelations()).hasSize(1);
    var dto = response.getBody().getDiseaseCorrelations().get(0);
    assertThat(dto.getId()).isEqualTo(includedId.toHexString());

    verify(repository).findByClusterAndIdNotIn(
      eq("Cluster D"),
      eq(List.of(excludedId)),
      any(Pageable.class)
    );
  }

  private DiseaseCorrelationDocument buildDocument(ObjectId objectId) {
    CorrelationResult correlation = CorrelationResult.builder()
      .correlation(0.87d)
      .adjustedPvalue(0.01d)
      .build();

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
    CorrelationResult correlation = CorrelationResult.builder().correlation(0.5d).build();

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
