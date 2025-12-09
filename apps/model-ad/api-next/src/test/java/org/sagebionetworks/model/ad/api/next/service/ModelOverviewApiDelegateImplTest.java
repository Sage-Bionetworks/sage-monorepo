package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.api.*;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument.ModelOverviewLink;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelOverviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ModelOverviewApiDelegateImplTest {

  @Mock
  private ModelOverviewRepository repository;

  private ModelOverviewApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    ModelOverviewService queryService = new ModelOverviewService(
      repository,
      new ModelOverviewMapper()
    );
    delegate = new ModelOverviewApiDelegateImpl(queryService);
  }

  /**
   * Helper to call delegate with DTO by converting to individual parameters.
   */
  private ResponseEntity<ModelOverviewsPageDto> callDelegate(ModelOverviewSearchQueryDto query) {
    String items = query.getItems() != null ? String.join(",", query.getItems()) : null;
    return delegate.getModelOverviews(
      query.getSortFields(),
      query.getSortOrders(),
      query.getPageNumber(),
      query.getPageSize(),
      items,
      query.getItemFilterType(),
      query.getSearch()
    );
  }

  @Test
  @DisplayName("should validate sortFields and sortOrders have matching element counts")
  void shouldValidateSortFieldsAndSortOrdersHaveMatchingElementCounts() {
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("name,modelType")
      .sortOrders("1")
      .items(List.of("Model A"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> callDelegate(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("must have the same number of elements")
      .hasMessageContaining("Got 2 field(s) and 1 order(s)");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw exception when sortFields is null")
  void shouldThrowExceptionWhenSortFieldsIsNull() {
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields(null)
      .sortOrders("1")
      .items(List.of("Model A"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> callDelegate(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("sortFields is required");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw exception when sortFields is empty")
  void shouldThrowExceptionWhenSortFieldsIsEmpty() {
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("")
      .sortOrders("1")
      .items(List.of("Model A"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> callDelegate(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("sortFields is required");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw exception when sortOrders is null")
  void shouldThrowExceptionWhenSortOrdersIsNull() {
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("name")
      .sortOrders(null)
      .items(List.of("Model A"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> callDelegate(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("sortOrders is required");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw exception when sortOrders is empty")
  void shouldThrowExceptionWhenSortOrdersIsEmpty() {
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("name")
      .sortOrders("")
      .items(List.of("Model A"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> callDelegate(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("sortOrders is required");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should accept valid matching sort parameters")
  void shouldAcceptValidMatchingSortParameters() {
    String modelName = "Model A";
    ModelOverviewDocument document = buildDocument(modelName);
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(document), PageRequest.of(0, 100), 1);

    when(repository.findByNameIn(anyList(), any())).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("name,modelType")
      .sortOrders("1,-1")
      .items(List.of(modelName))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = callDelegate(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("name")
      .sortOrders("1")
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = callDelegate(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getModelOverviews()).isEmpty();
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
    String modelName = "Model A";
    ModelOverviewDocument document = buildDocument(modelName);
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(document), PageRequest.of(0, 100), 1);

    when(repository.findByNameIn(anyList(), any())).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("name")
      .sortOrders("1")
      .items(List.of(modelName))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = callDelegate(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getModelOverviews()).hasSize(1);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(1);
    assertThat(response.getBody().getPage().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getPage().getNumber()).isZero();
    assertThat(response.getBody().getPage().getSize()).isEqualTo(100);
    assertThat(response.getBody().getPage().getHasNext()).isFalse();
    assertThat(response.getBody().getPage().getHasPrevious()).isFalse();

    ModelOverviewDto dto = response.getBody().getModelOverviews().get(0);
    assertThat(dto.getName()).isEqualTo(modelName);
    assertThat(dto.getModelType()).isEqualTo("Late Onset AD");
    assertThat(dto.getMatchedControls()).containsExactly("Control 1");
    assertThat(dto.getAvailableData()).containsExactly(
      ModelOverviewDto.AvailableDataEnum.GENE_EXPRESSION,
      ModelOverviewDto.AvailableDataEnum.PATHOLOGY
    );
    assertThat(dto.getStudyData().getLinkUrl()).isEqualTo("https://example.org/study");
    assertThat(dto.getGeneExpression()).isNotNull();

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    String modelName1 = "Model A";
    String modelName2 = "Model B";
    ModelOverviewDocument document1 = buildDocument(modelName1);
    ModelOverviewDocument document2 = buildDocument(modelName2);
    Page<ModelOverviewDocument> page = new PageImpl<>(
      List.of(document1, document2),
      PageRequest.of(0, 100),
      2
    );

    when(repository.findAll(any(PageRequest.class))).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("name")
      .sortOrders("1")
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = callDelegate(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getModelOverviews()).hasSize(2);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(2);

    verify(repository).findAll(any(PageRequest.class));
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    String excludedName = "Excluded Model";
    String includedName = "Included Model";
    ModelOverviewDocument includedDocument = buildDocument(includedName);
    Page<ModelOverviewDocument> page = new PageImpl<>(
      List.of(includedDocument),
      PageRequest.of(0, 100),
      1
    );

    when(repository.findByNameNotIn(anyList(), any())).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields("name")
      .sortOrders("1")
      .items(List.of(excludedName))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = callDelegate(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getModelOverviews()).hasSize(1);
    ModelOverviewDto dto = response.getBody().getModelOverviews().get(0);
    assertThat(dto.getName()).isEqualTo(includedName);

    verify(repository).findByNameNotIn(anyList(), any());
  }

  private ModelOverviewDocument buildDocument(String name) {
    ModelOverviewLink requiredLink = ModelOverviewLink.builder()
      .linkText("Study")
      .linkUrl("https://example.org/study")
      .build();

    ModelOverviewLink optionalLink = ModelOverviewLink.builder()
      .linkText("Gene Expression")
      .linkUrl("https://example.org/gene")
      .build();

    ModelOverviewDocument document = new ModelOverviewDocument();
    document.setId(new ObjectId());
    document.setName(name);
    document.setModelType("Late Onset AD");
    document.setMatchedControls(List.of("Control 1"));
    document.setGeneExpression(optionalLink);
    document.setDiseaseCorrelation(optionalLink);
    document.setBiomarkers(optionalLink);
    document.setPathology(optionalLink);
    document.setStudyData(requiredLink);
    document.setJaxStrain(requiredLink);
    document.setCenter(requiredLink);
    document.setModifiedGenes(List.of("Gene 1"));
    document.setAvailableData(List.of("Gene Expression", "Pathology"));
    return document;
  }
}
