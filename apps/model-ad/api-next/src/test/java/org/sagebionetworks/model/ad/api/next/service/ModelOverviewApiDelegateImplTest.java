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
import org.sagebionetworks.model.ad.api.next.exception.InvalidObjectIdException;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewLinkDocument;
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

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = delegate.getModelOverviews(query);

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
    ObjectId objectId = new ObjectId();
    ModelOverviewDocument document = buildDocument(objectId);
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(document), PageRequest.of(0, 100), 1);

    when(repository.findByIdIn(anyList(), any())).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of(objectId.toHexString()))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = delegate.getModelOverviews(query);

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
    assertThat(dto.getId()).isEqualTo(objectId.toHexString());
    assertThat(dto.getName()).isEqualTo("Model A");
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
  @DisplayName("should throw bad request when item contains invalid object id")
  void shouldThrowBadRequestWhenItemContainsInvalidObjectId() {
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of("not-an-id"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> delegate.getModelOverviews(query)).isInstanceOf(
      InvalidObjectIdException.class
    );

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    ObjectId objectId1 = new ObjectId();
    ObjectId objectId2 = new ObjectId();
    ModelOverviewDocument document1 = buildDocument(objectId1);
    ModelOverviewDocument document2 = buildDocument(objectId2);
    Page<ModelOverviewDocument> page = new PageImpl<>(
      List.of(document1, document2),
      PageRequest.of(0, 100),
      2
    );

    when(repository.findAll(any(PageRequest.class))).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = delegate.getModelOverviews(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getModelOverviews()).hasSize(2);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(2);

    verify(repository).findAll(any(PageRequest.class));
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    ObjectId excludedId = new ObjectId();
    ObjectId includedId = new ObjectId();
    ModelOverviewDocument includedDocument = buildDocument(includedId);
    Page<ModelOverviewDocument> page = new PageImpl<>(
      List.of(includedDocument),
      PageRequest.of(0, 100),
      1
    );

    when(repository.findByIdNotIn(anyList(), any())).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of(excludedId.toHexString()))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<ModelOverviewsPageDto> response = delegate.getModelOverviews(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getModelOverviews()).hasSize(1);
    ModelOverviewDto dto = response.getBody().getModelOverviews().get(0);
    assertThat(dto.getId()).isEqualTo(includedId.toHexString());

    verify(repository).findByIdNotIn(anyList(), any());
  }

  private ModelOverviewDocument buildDocument(ObjectId objectId) {
    ModelOverviewLinkDocument requiredLink = new ModelOverviewLinkDocument();
    requiredLink.setLinkText("Study");
    requiredLink.setLinkUrl("https://example.org/study");

    ModelOverviewLinkDocument optionalLink = new ModelOverviewLinkDocument();
    optionalLink.setLinkText("Gene Expression");
    optionalLink.setLinkUrl("https://example.org/gene");

    ModelOverviewDocument document = new ModelOverviewDocument();
    document.setId(objectId);
    document.setName("Model A");
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
