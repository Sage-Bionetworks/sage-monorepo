package org.sagebionetworks.model.ad.api.next.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.document.SearchResultDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

@ExtendWith(MockitoExtension.class)
class ModelSearchRepositoryImplTest {

  private static final String MOUSE_COLLECTION = "model_details";
  private static final String MARMOSET_COLLECTION = "marmo_details";
  private static final String QUERY = "apoe";
  private static final int NAME_PRECEDENCE =
    ModelSearchRepositoryImpl.MatchField.NAME.getPrecedence();
  private static final int JAX_ID_PRECEDENCE =
    ModelSearchRepositoryImpl.MatchField.JAX_ID.getPrecedence();

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<SearchResultDocument> mouseResults;

  @Mock
  private AggregationResults<SearchResultDocument> marmosetResults;

  private ModelSearchRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new ModelSearchRepositoryImpl(mongoTemplate);
  }

  @Test
  @DisplayName("should aggregate both collections when both organisms are requested")
  void shouldAggregateBothCollectionsWhenBothOrganismsRequested() {
    stubAggregation(MOUSE_COLLECTION, mouseResults, List.of());
    stubAggregation(MARMOSET_COLLECTION, marmosetResults, List.of());

    repository.searchModels(QUERY, List.of(ModelOrganismDto.MOUSE, ModelOrganismDto.MARMOSET));

    verify(mongoTemplate).aggregate(
      any(Aggregation.class),
      eq(MOUSE_COLLECTION),
      eq(SearchResultDocument.class)
    );
    verify(mongoTemplate).aggregate(
      any(Aggregation.class),
      eq(MARMOSET_COLLECTION),
      eq(SearchResultDocument.class)
    );
  }

  @Test
  @DisplayName("should aggregate only the mouse collection when only mouse is requested")
  void shouldAggregateOnlyMouseCollectionWhenOnlyMouseRequested() {
    stubAggregation(MOUSE_COLLECTION, mouseResults, List.of());

    repository.searchModels(QUERY, List.of(ModelOrganismDto.MOUSE));

    verify(mongoTemplate).aggregate(
      any(Aggregation.class),
      eq(MOUSE_COLLECTION),
      eq(SearchResultDocument.class)
    );
    verify(mongoTemplate, never()).aggregate(
      any(Aggregation.class),
      eq(MARMOSET_COLLECTION),
      eq(SearchResultDocument.class)
    );
  }

  @Test
  @DisplayName("should aggregate only the marmoset collection when only marmoset is requested")
  void shouldAggregateOnlyMarmosetCollectionWhenOnlyMarmosetRequested() {
    stubAggregation(MARMOSET_COLLECTION, marmosetResults, List.of());

    repository.searchModels(QUERY, List.of(ModelOrganismDto.MARMOSET));

    verify(mongoTemplate).aggregate(
      any(Aggregation.class),
      eq(MARMOSET_COLLECTION),
      eq(SearchResultDocument.class)
    );
    verify(mongoTemplate, never()).aggregate(
      any(Aggregation.class),
      eq(MOUSE_COLLECTION),
      eq(SearchResultDocument.class)
    );
  }

  @Test
  @DisplayName("should sort merged results by precedence then id")
  void shouldSortMergedResultsByPrecedenceThenId() {
    stubAggregation(
      MOUSE_COLLECTION,
      mouseResults,
      List.of(
        document("3xTg-AD", JAX_ID_PRECEDENCE),
        document("Trem2R47H", NAME_PRECEDENCE),
        document("APOE4", NAME_PRECEDENCE)
      )
    );
    stubAggregation(
      MARMOSET_COLLECTION,
      marmosetResults,
      List.of(document("Presenilin 1", NAME_PRECEDENCE))
    );

    List<SearchResultDocument> results = repository.searchModels(
      QUERY,
      List.of(ModelOrganismDto.MOUSE, ModelOrganismDto.MARMOSET)
    );

    assertThat(results)
      .extracting(SearchResultDocument::getId)
      .as("precedence wins over id, and both collections interleave rather than concatenate")
      .containsExactly("APOE4", "Presenilin 1", "Trem2R47H", "3xTg-AD");
  }

  @Test
  @DisplayName("should not use $unionWith to combine the two collections")
  void shouldNotUseUnionWithStage() {
    stubAggregation(MOUSE_COLLECTION, mouseResults, List.of());
    stubAggregation(MARMOSET_COLLECTION, marmosetResults, List.of());

    repository.searchModels(QUERY, List.of(ModelOrganismDto.MOUSE, ModelOrganismDto.MARMOSET));

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate, times(2)).aggregate(
      aggregationCaptor.capture(),
      anyString(),
      eq(SearchResultDocument.class)
    );

    assertThat(aggregationCaptor.getAllValues()).allSatisfy(aggregation ->
      assertThat(aggregation.toString())
        .as("$unionWith is unsupported on DocumentDB, so the collections are merged in Java")
        .doesNotContain("$unionWith")
    );
  }

  @Test
  @DisplayName("should project precedence so the Java-side sort has a key")
  void shouldProjectPrecedenceForJavaSideSorting() {
    stubAggregation(MOUSE_COLLECTION, mouseResults, List.of());

    repository.searchModels(QUERY, List.of(ModelOrganismDto.MOUSE));

    assertThat(capturedPipeline(MOUSE_COLLECTION))
      .as("without this field Comparator.comparingInt would unbox null")
      .contains("$project")
      .contains("\"precedence\" : \"$match_info.precedence\"");
  }

  @Test
  @DisplayName("should escape regex metacharacters in the query")
  void shouldEscapeRegexMetacharactersInQuery() {
    stubAggregation(MOUSE_COLLECTION, mouseResults, List.of());

    repository.searchModels("Trem2*", List.of(ModelOrganismDto.MOUSE));

    assertThat(capturedPipeline(MOUSE_COLLECTION))
      .as("the query is passed through Pattern.quote before reaching $regexMatch")
      .contains("\\\\QTrem2*\\\\E");
  }

  private void stubAggregation(
    String collection,
    AggregationResults<SearchResultDocument> results,
    List<SearchResultDocument> documents
  ) {
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(collection),
        eq(SearchResultDocument.class)
      )
    ).thenReturn(results);
    when(results.getMappedResults()).thenReturn(documents);
  }

  private String capturedPipeline(String collection) {
    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(collection),
      eq(SearchResultDocument.class)
    );
    return aggregationCaptor.getValue().toString();
  }

  private SearchResultDocument document(String id, int precedence) {
    SearchResultDocument document = new SearchResultDocument();
    document.setId(id);
    document.setPrecedence(precedence);
    return document;
  }
}
