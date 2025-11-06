package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewLinkDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelOverviewRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ModelOverviewApiDelegateImplTest {

  @Mock
  private ModelOverviewRepository repository;

  private ModelOverviewApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    ModelOverviewQueryService queryService = new ModelOverviewQueryService(
      repository,
      new ModelOverviewMapper()
    );
    delegate = new ModelOverviewApiDelegateImpl(queryService);
  }

  @Test
  @DisplayName("should return empty list when include filter has no items")
  void shouldReturnEmptyListWhenIncludeFilterHasNoItems() {
    ResponseEntity<List<ModelOverviewDto>> response = delegate.getModelOverviews(
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
    ModelOverviewDocument document = buildDocument(objectId);

    when(repository.findByIdIn(anyList())).thenReturn(List.of(document));

    ResponseEntity<List<ModelOverviewDto>> response = delegate.getModelOverviews(
      List.of(objectId.toHexString()),
      ItemFilterTypeQueryDto.INCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    ModelOverviewDto dto = response.getBody().get(0);
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
    assertThatThrownBy(() ->
      delegate.getModelOverviews(List.of("not-an-id"), ItemFilterTypeQueryDto.INCLUDE)
    )
      .isInstanceOf(ResponseStatusException.class)
      .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
      .isEqualTo(HttpStatus.BAD_REQUEST);

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    ObjectId objectId1 = new ObjectId();
    ObjectId objectId2 = new ObjectId();
    ModelOverviewDocument document1 = buildDocument(objectId1);
    ModelOverviewDocument document2 = buildDocument(objectId2);

    when(repository.findAll()).thenReturn(List.of(document1, document2));

    ResponseEntity<List<ModelOverviewDto>> response = delegate.getModelOverviews(
      null,
      ItemFilterTypeQueryDto.EXCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);

    verify(repository).findAll();
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    ObjectId excludedId = new ObjectId();
    ObjectId includedId = new ObjectId();
    ModelOverviewDocument includedDocument = buildDocument(includedId);

    when(repository.findByIdNotIn(List.of(excludedId))).thenReturn(List.of(includedDocument));

    ResponseEntity<List<ModelOverviewDto>> response = delegate.getModelOverviews(
      List.of(excludedId.toHexString()),
      ItemFilterTypeQueryDto.EXCLUDE
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    ModelOverviewDto dto = response.getBody().get(0);
    assertThat(dto.getId()).isEqualTo(includedId.toHexString());

    verify(repository).findByIdNotIn(List.of(excludedId));
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
