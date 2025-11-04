package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewLinkDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelOverviewMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ModelOverviewApiDelegateImplTest {

  @Mock
  private MongoTemplate mongoTemplate;

  private ModelOverviewApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    ModelOverviewQueryService queryService = new ModelOverviewQueryService(
      mongoTemplate,
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
    assertThat(headers.getExpires()).isEqualTo(0);
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verifyNoInteractions(mongoTemplate);
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    ObjectId objectId = new ObjectId();
    ModelOverviewDocument document = buildDocument(objectId);

    when(mongoTemplate.find(any(Query.class), eq(ModelOverviewDocument.class))).thenReturn(
      List.of(document)
    );

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

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(ModelOverviewDocument.class));
    Document queryDocument = queryCaptor.getValue().getQueryObject();
    Document expectedQuery = new Document("_id", new Document("$in", List.of(objectId)));
    assertThat(queryDocument).isEqualTo(expectedQuery);

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

    verifyNoInteractions(mongoTemplate);
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
