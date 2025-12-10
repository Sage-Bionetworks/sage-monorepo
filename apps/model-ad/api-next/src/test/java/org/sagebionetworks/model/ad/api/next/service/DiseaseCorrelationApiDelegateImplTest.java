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
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
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

  private static final String CLUSTER_A = "Cluster A";
  private static final String CLUSTER_B = "Cluster B";
  private static final String CLUSTER_C = "Cluster C";
  private static final String CLUSTER_D = "Cluster D";
  private static final int PAGE_NUMBER = 0;
  private static final int PAGE_SIZE = 100;

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
      .categories(List.of(ErrorConstants.DISEASE_CORRELATION_CATEGORY))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE)
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
      .categories(List.of("OTHER", CLUSTER_A))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE)
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query)).isInstanceOf(
      InvalidCategoryException.class
    );

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should validate sortFields and sortOrders have matching element counts")
  void shouldValidateSortFieldsAndSortOrdersHaveMatchingElementCounts() {
    DiseaseCorrelationSearchQueryDto query = buildQuery(CLUSTER_A)
      .sortFields(List.of("name", "age"))
      .sortOrders(List.of(1))
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("must have the same number of elements")
      .hasMessageContaining("Got 2 field(s) and 1 order(s)");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw exception when sortFields is null")
  void shouldThrowExceptionWhenSortFieldsIsNull() {
    DiseaseCorrelationSearchQueryDto query = buildQuery(CLUSTER_A)
      .sortFields(null)
      .sortOrders(List.of(1))
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("sortFields is required");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw exception when sortFields is empty")
  void shouldThrowExceptionWhenSortFieldsIsEmpty() {
    DiseaseCorrelationSearchQueryDto query = buildQuery(CLUSTER_A)
      .sortFields(List.of())
      .sortOrders(List.of(1))
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("sortFields is required");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw exception when sortOrders is null")
  void shouldThrowExceptionWhenSortOrdersIsNull() {
    DiseaseCorrelationSearchQueryDto query = buildQuery(CLUSTER_A)
      .sortFields(List.of("name"))
      .sortOrders(null)
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("sortOrders is required");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw exception when sortOrders is empty")
  void shouldThrowExceptionWhenSortOrdersIsEmpty() {
    DiseaseCorrelationSearchQueryDto query = buildQuery(CLUSTER_A)
      .sortFields(List.of("name"))
      .sortOrders(List.of())
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("sortOrders is required");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should accept valid matching sort parameters")
  void shouldAcceptValidMatchingSortParameters() {
    ObjectId objectId = new ObjectId();
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(buildDocument(objectId)));

    when(
      repository.findByClusterAndCompositeIdentifiers(eq(CLUSTER_A), anyList(), any(Pageable.class))
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = buildQuery(CLUSTER_A)
      .sortFields(List.of("name", "age"))
      .sortOrders(List.of(1, -1))
      .items(List.of("Model 1~10 weeks~Female"))
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .categories(List.of(ErrorConstants.DISEASE_CORRELATION_CATEGORY, CLUSTER_A))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    DiseaseCorrelationsPageDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getDiseaseCorrelations()).isEmpty();
    assertThat(body.getPage().getNumber()).isZero();
    assertThat(body.getPage().getSize()).isEqualTo(PAGE_SIZE);
    assertThat(body.getPage().getTotalElements()).isZero();

    assertResponseHeaders(response.getHeaders());
    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    ObjectId objectId = new ObjectId();
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(buildDocument(objectId)));

    when(
      repository.findByClusterAndCompositeIdentifiers(eq(CLUSTER_A), anyList(), any(Pageable.class))
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .categories(List.of(ErrorConstants.DISEASE_CORRELATION_CATEGORY, CLUSTER_A))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .items(List.of("Model 1~10 weeks~Female"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    DiseaseCorrelationsPageDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getDiseaseCorrelations()).hasSize(1);
    assertThat(body.getPage().getNumber()).isZero();
    assertThat(body.getPage().getTotalElements()).isEqualTo(1);

    var dto = body.getDiseaseCorrelations().get(0);
    assertThat(dto.getCompositeId()).contains("Model 1");
    assertThat(dto.getName()).isEqualTo("Model 1");
    assertThat(dto.getMatchedControl()).isEqualTo("Control A");
    assertThat(dto.getIFG()).isNotNull();
    assertThat(dto.getIFG().getCorrelation()).isEqualTo(BigDecimal.valueOf(0.87d));
    assertThat(dto.getSex().getValue()).isEqualTo("Female");

    verify(repository).findByClusterAndCompositeIdentifiers(
      eq(CLUSTER_A),
      anyList(),
      any(Pageable.class)
    );
  }

  @Test
  @DisplayName("should include cluster filter when exclude filter has no items")
  void shouldIncludeClusterFilterWhenExcludeFilterHasNoItems() {
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(buildDocument(new ObjectId())));
    when(repository.findByCluster(eq(CLUSTER_B), any(Pageable.class))).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .categories(List.of(ErrorConstants.DISEASE_CORRELATION_CATEGORY, CLUSTER_B))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDiseaseCorrelations()).hasSize(1);

    verify(repository).findByCluster(eq(CLUSTER_B), any(Pageable.class));
  }

  @Test
  @DisplayName("should throw bad request when items contain invalid composite identifier format")
  void shouldThrowBadRequestWhenItemsContainInvalidCompositeIdentifier() {
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .categories(List.of(ErrorConstants.DISEASE_CORRELATION_CATEGORY, CLUSTER_A))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .items(List.of("invalid")) // Missing tildes - only 1 part instead of 3
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE)
      .build();

    assertThatThrownBy(() -> delegate.getDiseaseCorrelations(query)).isInstanceOf(
      InvalidFilterException.class
    );

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should omit correlation data when values incomplete")
  void shouldOmitCorrelationDataWhenValuesIncomplete() {
    ObjectId objectId = new ObjectId();
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(
      List.of(buildDocumentWithPartialCorrelation(objectId))
    );

    when(
      repository.findByClusterAndCompositeIdentifiers(eq(CLUSTER_C), anyList(), any(Pageable.class))
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .categories(List.of(ErrorConstants.DISEASE_CORRELATION_CATEGORY, CLUSTER_C))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .items(List.of("Model 2~12 weeks~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDiseaseCorrelations()).hasSize(1);
    assertThat(response.getBody().getDiseaseCorrelations().get(0).getIFG()).isNull();
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    ObjectId includedId = new ObjectId();
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(buildDocument(includedId)));

    when(
      repository.findByClusterExcludingCompositeIdentifiers(
        eq(CLUSTER_D),
        anyList(),
        any(Pageable.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .categories(List.of(ErrorConstants.DISEASE_CORRELATION_CATEGORY, CLUSTER_D))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .items(List.of("ExcludedModel~8 weeks~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE)
      .build();

    ResponseEntity<DiseaseCorrelationsPageDto> response = delegate.getDiseaseCorrelations(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    DiseaseCorrelationsPageDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getDiseaseCorrelations()).hasSize(1);
    assertThat(body.getDiseaseCorrelations().get(0).getCompositeId()).contains("Model 1");

    verify(repository).findByClusterExcludingCompositeIdentifiers(
      eq(CLUSTER_D),
      anyList(),
      any(Pageable.class)
    );
  }

  private DiseaseCorrelationSearchQueryDto.Builder buildQuery(String cluster) {
    return DiseaseCorrelationSearchQueryDto.builder()
      .categories(List.of(ErrorConstants.DISEASE_CORRELATION_CATEGORY, cluster))
      .sortFields(List.of("name"))
      .sortOrders(List.of(1))
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(PAGE_NUMBER)
      .pageSize(PAGE_SIZE);
  }

  private void assertResponseHeaders(HttpHeaders headers) {
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
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
