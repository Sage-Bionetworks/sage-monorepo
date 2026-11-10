package org.sagebionetworks.qtl.api.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.sagebionetworks.qtl.api.model.dto.ItemFilterTypeQueryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Asserts the conditions and sort keys this repository configures. How the shared base class
 * wraps them (the top-level {@code $and}, its empty-value flags, the {@code _id} tiebreaker) is
 * covered by the base class's own tests, so these tests read the conditions out of that wrapping
 * rather than comparing whole stages.
 */
@ExtendWith(MockitoExtension.class)
class CustomEqtlRepositoryImplTest {

  private static final String COLLECTION = "eqtl_results";
  private static final String PIN = "rs12655812~ENSG00000250891~astro";
  private static final String PIN_CRITERIA =
    "{\"$and\": [{\"variant\": \"rs12655812\"}, " +
    "{\"ensembl_gene_id\": \"ENSG00000250891\"}, {\"cell_type_id\": \"astro\"}]}";
  private static final List<String> SEARCH_FIELDS = List.of(
    "variant",
    "gene_symbol",
    "ensembl_gene_id"
  );
  private static final Pageable FIRST_PAGE = PageRequest.of(0, 10);

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<EqtlDocument> aggregationResults;

  private CustomEqtlRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomEqtlRepositoryImpl(mongoTemplate);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());
    when(
      mongoTemplate.aggregate(any(Aggregation.class), eq(COLLECTION), eq(EqtlDocument.class))
    ).thenReturn(aggregationResults);
  }

  @Test
  @DisplayName("should filter each query filter with $in on its stored field")
  void shouldFilterEachQueryFilterWithInOnItsStoredField() {
    // given
    EqtlSearchQueryDto query = excludeQuery();
    query.setVariant(List.of("rs170094"));
    query.setGene(List.of("DGKB"));
    query.setCellClass(List.of("Astrocyte"));
    query.setCellSubclass(List.of("Astrocyte"));

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of());

    // then
    assertThat(matchConditions(pipeline)).containsExactlyInAnyOrder(
      json("{\"variant\": {\"$in\": [\"rs170094\"]}}"),
      json("{\"gene_symbol\": {\"$in\": [\"DGKB\"]}}"),
      json("{\"cell_class\": {\"$in\": [\"Astrocyte\"]}}"),
      json("{\"cell_subclass\": {\"$in\": [\"Astrocyte\"]}}")
    );
  }

  @Test
  @DisplayName("should match only the pinned rows when the filter type is include")
  void shouldMatchOnlyThePinnedRowsWhenTheFilterTypeIsInclude() {
    // given
    EqtlSearchQueryDto query = new EqtlSearchQueryDto();
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of(PIN));

    // then
    assertThat(matchConditions(pipeline)).containsExactly(
      json("{\"$or\": [" + PIN_CRITERIA + "]}")
    );
  }

  @Test
  @DisplayName("should treat a missing filter type as include")
  void shouldTreatAMissingFilterTypeAsInclude() {
    // given
    EqtlSearchQueryDto query = new EqtlSearchQueryDto();

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of(PIN));

    // then
    assertThat(matchConditions(pipeline)).containsExactly(
      json("{\"$or\": [" + PIN_CRITERIA + "]}")
    );
  }

  @Test
  @DisplayName("should match nothing when the filter type is include and nothing is pinned")
  void shouldMatchNothingWhenTheFilterTypeIsIncludeAndNothingIsPinned() {
    // given
    EqtlSearchQueryDto query = new EqtlSearchQueryDto();

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of());

    // then
    assertThat(matchConditions(pipeline)).containsExactly(matchNothing());
  }

  @Test
  @DisplayName("should leave out the pinned rows when the filter type is exclude")
  void shouldLeaveOutThePinnedRowsWhenTheFilterTypeIsExclude() {
    // given
    EqtlSearchQueryDto query = excludeQuery();

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of(PIN));

    // then
    assertThat(matchConditions(pipeline)).containsExactly(
      json("{\"$nor\": [" + PIN_CRITERIA + "]}")
    );
  }

  @Test
  @DisplayName("should partially match a single search term on variant, gene symbol and Ensembl ID")
  void shouldPartiallyMatchASingleSearchTermOnVariantGeneSymbolAndEnsemblId() {
    // given
    EqtlSearchQueryDto query = excludeQuery();
    query.setSearch("dgk");

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of());

    // then
    assertThat(matchConditions(pipeline)).containsExactly(
      searchOnEveryField(
        "{\"$regularExpression\": {\"pattern\": \"\\\\Qdgk\\\\E\", \"options\": \"i\"}}"
      )
    );
  }

  @Test
  @DisplayName("should fully match each term of a comma separated search on all three fields")
  void shouldFullyMatchEachTermOfACommaSeparatedSearchOnAllThreeFields() {
    // given
    EqtlSearchQueryDto query = excludeQuery();
    query.setSearch("rs170094,DGKB");

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of());

    // then
    assertThat(matchConditions(pipeline)).containsExactly(
      searchOnEveryField(
        "{\"$in\": [" +
        "{\"$regularExpression\": {\"pattern\": \"^\\\\Qrs170094\\\\E$\", \"options\": \"i\"}}, " +
        "{\"$regularExpression\": {\"pattern\": \"^\\\\QDGKB\\\\E$\", \"options\": \"i\"}}]}"
      )
    );
  }

  @Test
  @DisplayName("should match nothing when the search is only commas")
  void shouldMatchNothingWhenTheSearchIsOnlyCommas() {
    // given
    EqtlSearchQueryDto query = excludeQuery();
    query.setSearch(",,");

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of());

    // then
    assertThat(matchConditions(pipeline)).containsExactly(matchNothing());
  }

  @Test
  @DisplayName("should sort the adjusted p-value by its numeric companion field")
  void shouldSortTheAdjustedPValueByItsNumericCompanionField() {
    // given
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("adj_p_val")));

    // when
    List<Document> pipeline = runAndCapturePipeline(pageable, excludeQuery(), List.of());

    // then
    Document sort = (Document) stageValue(pipeline, "$sort");
    assertThat(sort).containsEntry("adj_p_val_numeric", -1).doesNotContainKey("adj_p_val");
  }

  @Test
  @DisplayName("should skip to the requested page")
  void shouldSkipToTheRequestedPage() {
    // given
    Pageable pageable = PageRequest.of(2, 25);

    // when
    List<Document> pipeline = runAndCapturePipeline(pageable, excludeQuery(), List.of());

    // then
    assertThat(stageValue(pipeline, "$skip")).isEqualTo(50L);
    assertThat(stageValue(pipeline, "$limit")).isEqualTo(25L);
  }

  @Test
  @DisplayName("should limit to the remaining budget and ignore paging when the budget is set")
  void shouldLimitToTheRemainingBudgetAndIgnorePagingWhenTheBudgetIsSet() {
    // given
    EqtlSearchQueryDto query = excludeQuery();
    query.setRemainingBudget(2);

    // when
    List<Document> pipeline = runAndCapturePipeline(PageRequest.of(3, 10), query, List.of());

    // then
    assertThat(stageValue(pipeline, "$limit")).isEqualTo(2L);
    assertThat(pipeline).noneMatch(document -> document.containsKey("$skip"));
  }

  @Test
  @DisplayName("should count with the same criteria as the page query")
  void shouldCountWithTheSameCriteriaAsThePageQuery() {
    // given
    EqtlSearchQueryDto query = excludeQuery();
    query.setCellClass(List.of("Astrocyte"));

    // when
    List<Document> pipeline = runAndCapturePipeline(FIRST_PAGE, query, List.of(PIN));

    // then
    ArgumentCaptor<Query> countCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).count(countCaptor.capture(), eq(COLLECTION));
    assertThat(countCaptor.getValue().getQueryObject().toJson()).isEqualTo(
      ((Document) stageValue(pipeline, "$match")).toJson()
    );
  }

  private static EqtlSearchQueryDto excludeQuery() {
    EqtlSearchQueryDto query = new EqtlSearchQueryDto();
    query.setItemFilterType(ItemFilterTypeQueryDto.EXCLUDE);
    return query;
  }

  private List<Document> runAndCapturePipeline(
    Pageable pageable,
    EqtlSearchQueryDto query,
    List<String> items
  ) {
    repository.findAll(pageable, query, items);
    ArgumentCaptor<Aggregation> captor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(captor.capture(), eq(COLLECTION), eq(EqtlDocument.class));
    return captor.getValue().toPipeline(Aggregation.DEFAULT_CONTEXT);
  }

  /**
   * The conditions inside the {@code $match} stage, as JSON. Regex values are compiled patterns,
   * which don't compare equal as objects, so conditions are compared by their JSON.
   */
  @SuppressWarnings("unchecked")
  private static List<String> matchConditions(List<Document> pipeline) {
    Document match = (Document) stageValue(pipeline, "$match");
    List<Document> conditions = match.containsKey("$and")
      ? (List<Document>) match.get("$and")
      : List.of(match);
    return conditions.stream().map(Document::toJson).toList();
  }

  private static String searchOnEveryField(String fieldMatchJson) {
    return json(
      "{\"$or\": [" +
      String.join(
        ", ",
        SEARCH_FIELDS.stream().map(field -> "{\"" + field + "\": " + fieldMatchJson + "}").toList()
      ) +
      "]}"
    );
  }

  private static String matchNothing() {
    return ApiHelper.matchNothing().getCriteriaObject().toJson();
  }

  private static String json(String json) {
    return Document.parse(json).toJson();
  }

  private static Object stageValue(List<Document> pipeline, String operator) {
    return pipeline
      .stream()
      .filter(document -> document.containsKey(operator))
      .findFirst()
      .orElseThrow(() -> new AssertionError("No " + operator + " stage in " + pipeline))
      .get(operator);
  }
}
