package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class ComparisonToolRepositorySupportTest {

  private static final String COLLECTION = "test_collection";

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<TestDocument> aggregationResults;

  @Test
  @DisplayName("should produce a match, sort, skip, limit pipeline with no hooks overridden")
  void shouldProduceMinimalPipeline() {
    BareRepo repo = new BareRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline).contains("$match").contains("$sort").contains("$skip").contains("$limit");
    // isEmpty stage is always present; no computed-sort _sort addFields expected
    assertThat(pipeline).contains("name_isEmpty").doesNotContain("name_sort");
    assertThat(pipeline).contains("\"name\" : 1");
  }

  @Test
  @DisplayName(
    "should resolve sort field aliases to the aliased name without a computed-sort $addFields"
  )
  void shouldResolveSortFieldAliases() {
    AliasingRepo repo = new AliasingRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("age")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline).contains("\"age_numeric\" : -1");
    // isEmpty stage present but no computed-sort alias _sort addFields
    assertThat(pipeline).contains("age_isEmpty").doesNotContain("age_sort");
  }

  @Test
  @DisplayName(
    "should inject $addFields and route sort through _sort when computed expression is configured"
  )
  void shouldInjectComputedSortField() {
    ComputedRepo repo = new ComputedRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline).contains("$addFields").contains("name_sort").contains("$toLower");
    assertThat(pipeline).contains("\"name_sort\" : 1");
  }

  @Test
  @DisplayName(
    "should skip computed-sort $addFields when the sort does not include any computed field"
  )
  void shouldSkipComputedAddFieldsWhenUnused() {
    ComputedRepo repo = new ComputedRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("hgnc_symbol")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    // isEmpty stage is still present, but no computed-sort _sort addFields
    assertThat(pipeline).contains("hgnc_symbol_isEmpty").doesNotContain("name_sort");
    assertThat(pipeline).contains("\"hgnc_symbol\" : 1");
  }

  @Test
  @DisplayName("should add prerequisites before computed-sort step when using ComputedSortField")
  void shouldAddPrerequisitesForComputedSortFields() {
    PrerequisiteRepo repo = new PrerequisiteRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("gene_symbol")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline)
      .as("$cond computation for display_gene_symbol should appear")
      .contains("$cond");
    assertThat(pipeline)
      .as("$toLower over display_gene_symbol drives the gene_symbol_sort key")
      .contains("display_gene_symbol")
      .contains("\"gene_symbol_sort\" : 1");
  }

  @Test
  @DisplayName(
    "should use $let/$getField/$type and cover null, empty string, and empty array when sort alias contains spaces"
  )
  void shouldUseGetFieldWhenSortAliasContainsSpaces() {
    SpacedAliasRepo repo = new SpacedAliasRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("4 months")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline)
      .as("isEmpty key should have spaces replaced with underscores")
      .contains("4_months_isEmpty");
    assertThat(pipeline)
      .as("$sort should use the nested alias path directly")
      .contains("4 months.log2_fc");
    assertThat(pipeline)
      .as("isEmpty expression must use $let to bind $$val to the $getField result")
      .contains("$let");
    assertThat(pipeline)
      .as("isEmpty expression must use $getField for spaced field names, not $-prefix expression syntax")
      .contains("$getField");
    assertThat(pipeline)
      .as("isEmpty expression must use $type for null/missing and cover empty string and empty array")
      .contains("$type").contains("missing")
      .contains("$isArray").contains("$size");
  }

  @Test
  @DisplayName("should throw UnsupportedOperationException when getFilterConfig is not overridden")
  void shouldThrowWhenGetFilterConfigNotOverridden() {
    BareRepo repo = new BareRepo(mongoTemplate);

    assertThatThrownBy(repo::getFilterConfig)
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("getFilterConfig");
  }

  @Test
  @DisplayName("should count using mongoTemplate.count, not aggregation")
  void shouldCountWithoutAggregation() {
    BareRepo repo = new BareRepo(mongoTemplate);
    stubMongoTemplate(42L);

    Page<TestDocument> page = repo.run(new Criteria(), PageRequest.of(0, 10));

    assertThat(page.getTotalElements()).isEqualTo(42L);
  }

  private void stubMongoTemplate(long total) {
    stubMongoTemplate(total, List.of());
  }

  private void stubMongoTemplate(long total, List<TestDocument> rows) {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION))).thenReturn(total);
    when(
      mongoTemplate.aggregate(any(Aggregation.class), eq(COLLECTION), eq(TestDocument.class))
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(rows);
  }

  private static List<TestDocument> documents(int count) {
    return Stream.generate(TestDocument::new).limit(count).toList();
  }

  private Aggregation capturePipeline() {
    ArgumentCaptor<Aggregation> captor = ArgumentCaptor.forClass(Aggregation.class);
    org.mockito.Mockito.verify(mongoTemplate).aggregate(
      captor.capture(),
      eq(COLLECTION),
      eq(TestDocument.class)
    );
    return captor.getValue();
  }

  /** Marker document type used by all test subclasses. */
  private static final class TestDocument {}

  /** Subclass with no hooks overridden — exercises the minimal pipeline. */
  private static final class BareRepo extends ComparisonToolRepositorySupport<TestDocument> {

    BareRepo(MongoTemplate mongoTemplate) {
      super(mongoTemplate);
    }

    @Override
    protected String getCollectionName() {
      return COLLECTION;
    }

    @Override
    protected Class<TestDocument> getDocumentClass() {
      return TestDocument.class;
    }

    Page<TestDocument> run(Criteria criteria, Pageable pageable) {
      return executePagedAggregation(criteria, pageable, false, null);
    }

    Page<TestDocument> run(
      Criteria criteria,
      Pageable pageable,
      boolean isInclude,
      Integer remainingBudget
    ) {
      return executePagedAggregation(criteria, pageable, isInclude, remainingBudget);
    }

    // expose for testing
    @Override
    public <Q> CtFilterConfig<Q> getFilterConfig() {
      return super.getFilterConfig();
    }
  }

  /** Subclass that exercises {@link #getSortFieldAliases()}. */
  private static final class AliasingRepo extends ComparisonToolRepositorySupport<TestDocument> {

    AliasingRepo(MongoTemplate mongoTemplate) {
      super(mongoTemplate);
    }

    @Override
    protected String getCollectionName() {
      return COLLECTION;
    }

    @Override
    protected Class<TestDocument> getDocumentClass() {
      return TestDocument.class;
    }

    @Override
    protected Map<String, String> getSortFieldAliases() {
      return Map.of("age", "age_numeric");
    }

    Page<TestDocument> run(Criteria criteria, Pageable pageable) {
      return executePagedAggregation(criteria, pageable, false, null);
    }
  }

  /** Subclass that exercises a sort alias whose key and value both contain spaces. */
  private static final class SpacedAliasRepo extends ComparisonToolRepositorySupport<TestDocument> {

    SpacedAliasRepo(MongoTemplate mongoTemplate) {
      super(mongoTemplate);
    }

    @Override
    protected String getCollectionName() {
      return COLLECTION;
    }

    @Override
    protected Class<TestDocument> getDocumentClass() {
      return TestDocument.class;
    }

    @Override
    protected Map<String, String> getSortFieldAliases() {
      return Map.of("4 months", "4 months.log2_fc");
    }

    Page<TestDocument> run(Criteria criteria, Pageable pageable) {
      return executePagedAggregation(criteria, pageable, false, null);
    }
  }

  /** Subclass that exercises {@link #getComputedSortFieldExpressions()}. */
  private static final class ComputedRepo extends ComparisonToolRepositorySupport<TestDocument> {

    ComputedRepo(MongoTemplate mongoTemplate) {
      super(mongoTemplate);
    }

    @Override
    protected String getCollectionName() {
      return COLLECTION;
    }

    @Override
    protected Class<TestDocument> getDocumentClass() {
      return TestDocument.class;
    }

    @Override
    protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
      return Map.of("name", ComputedSortField.of(new Document("$toLower", "$name")));
    }

    Page<TestDocument> run(Criteria criteria, Pageable pageable) {
      return executePagedAggregation(criteria, pageable, false, null);
    }
  }

  /** Subclass that exercises {@link ComputedSortField} with prerequisites. */
  private static final class PrerequisiteRepo
    extends ComparisonToolRepositorySupport<TestDocument> {

    PrerequisiteRepo(MongoTemplate mongoTemplate) {
      super(mongoTemplate);
    }

    @Override
    protected String getCollectionName() {
      return COLLECTION;
    }

    @Override
    protected Class<TestDocument> getDocumentClass() {
      return TestDocument.class;
    }

    @Override
    protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
      java.util.List<Object> eqArgs = new java.util.ArrayList<>();
      eqArgs.add("$gene_symbol");
      eqArgs.add(null);
      java.util.List<Object> condArgs = new java.util.ArrayList<>();
      condArgs.add(new Document("$eq", eqArgs));
      condArgs.add("$ensembl_gene_id");
      condArgs.add("$gene_symbol");
      Document addFields = new Document(
        "$addFields",
        new Document("display_gene_symbol", new Document("$cond", condArgs))
      );
      AggregationOperation prereq = context -> addFields;

      return Map.of(
        "gene_symbol",
        ComputedSortField.of(new Document("$toLower", "$display_gene_symbol")).withPrerequisite(prereq)
      );
    }

    Page<TestDocument> run(Criteria criteria, Pageable pageable) {
      return executePagedAggregation(criteria, pageable, false, null);
    }
  }

  @Nested
  @DisplayName("null-last sort")
  class NullLastSort {

    @Test
    @DisplayName("should include _isEmpty $addFields stage before $sort when sort is provided")
    void shouldIncludeIsEmptyAddFieldsBeforeSortWhenSortIsProvided() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(0L);

      Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
      repo.run(new Criteria(), pageable);

      String pipeline = capturePipeline().toString();
      assertThat(pipeline).contains("name_isEmpty").contains("$or").contains("$eq");

      int isEmptyIdx = pipeline.indexOf("name_isEmpty");
      int sortIdx = pipeline.indexOf("\"$sort\"");
      assertThat(isEmptyIdx).isLessThan(sortIdx);
    }

    @Test
    @DisplayName("should include _isEmpty: 1 key in $sort doc alongside the resolved sort key")
    void shouldIncludeIsEmptyKeyInSortDocWhenSortIsProvided() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(0L);

      Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
      repo.run(new Criteria(), pageable);

      String pipeline = capturePipeline().toString();
      assertThat(pipeline).contains("\"name_isEmpty\" : 1").contains("\"name\" : 1");
    }

    @Test
    @DisplayName("should not include _isEmpty stage when sort is unsorted")
    void shouldNotIncludeIsEmptyStageWhenSortIsUnsorted() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(0L);

      Pageable pageable = PageRequest.of(0, 10);
      repo.run(new Criteria(), pageable);

      String pipeline = capturePipeline().toString();
      assertThat(pipeline).doesNotContain("_isEmpty");
    }

    @Test
    @DisplayName(
      "should use original field name for _isEmpty when sort resolves through a computed alias"
    )
    void shouldUseOriginalFieldNameForIsEmptyWhenSortResolvesToComputedAlias() {
      ComputedRepo repo = new ComputedRepo(mongoTemplate);
      stubMongoTemplate(0L);

      Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
      repo.run(new Criteria(), pageable);

      String pipeline = capturePipeline().toString();
      assertThat(pipeline).contains("\"name_isEmpty\" : 1");
      assertThat(pipeline).contains("\"name_sort\" : 1");
    }
  }

  @Test
  @DisplayName("should append _id as final tiebreaker in sort document for deterministic pagination")
  void shouldAppendIdTiebreakerInSortDocument() {
    BareRepo repo = new BareRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline).contains("\"_id\" : 1");
    int nameIdx = pipeline.indexOf("\"name\" : 1");
    int idIdx = pipeline.indexOf("\"_id\" : 1");
    assertThat(idIdx).as("_id should appear after the user-requested sort field").isGreaterThan(nameIdx);
  }

  @Test
  @DisplayName("should append _id once, after all sort keys, when multiple sort orders are requested")
  void shouldAppendIdTiebreakerOnceAfterAllSortKeysForMultiSort() {
    ComputedRepo repo = new ComputedRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(
      0,
      10,
      Sort.by(Sort.Order.asc("name"), Sort.Order.desc("hgnc_symbol"))
    );
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline)
      .as("_id must be appended exactly once, not per sort order")
      .containsOnlyOnce("\"_id\" : 1");

    int computedIdx = pipeline.indexOf("\"name_sort\" : 1");
    int plainIdx = pipeline.indexOf("\"hgnc_symbol\" : -1");
    int idIdx = pipeline.indexOf("\"_id\" : 1");
    assertThat(idIdx)
      .as("_id must rank below the computed first sort key")
      .isGreaterThan(computedIdx);
    assertThat(idIdx)
      .as("_id must rank below the second sort key so it only breaks full ties")
      .isGreaterThan(plainIdx);
  }

  @Test
  @DisplayName("should preserve the caller's descending _id direction when sorting by _id")
  void shouldPreserveCallerIdSortWhenSortingById() {
    BareRepo repo = new BareRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(
      0,
      10,
      Sort.by(Sort.Order.desc("_id"), Sort.Order.asc("name"))
    );
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline)
      .as("the caller's descending _id direction must not be overwritten by the tiebreaker")
      .contains("\"_id\" : -1")
      .doesNotContain("\"_id\" : 1");
  }

  @Test
  @DisplayName("should not include _id tiebreaker when no sort is requested")
  void shouldNotIncludeIdTiebreakerWhenUnsorted() {
    BareRepo repo = new BareRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10);
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline).doesNotContain("\"_id\" : 1");
  }

  @Nested
  @DisplayName("remaining row budget")
  class RemainingRowBudget {

    private static final int PAGE_SIZE = 10;
    private static final int REMAINING_BUDGET = 25;

    @Test
    @DisplayName("should emit a single $limit of the budget and no $skip on an exclude query")
    void shouldLimitToBudgetWithoutSkipWhenExcluding() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(0L);

      Pageable pageable = PageRequest.of(2, PAGE_SIZE, Sort.by(Sort.Order.asc("name")));
      repo.run(new Criteria(), pageable, false, REMAINING_BUDGET);

      String pipeline = capturePipeline().toString();
      assertThat(pipeline).doesNotContain("$skip");
      assertThat(pipeline).containsOnlyOnce("$limit").contains(String.valueOf(REMAINING_BUDGET));
    }

    @Test
    @DisplayName("should keep the full match count so callers can detect truncation")
    void shouldKeepFullCountWhenBudgetApplies() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(400L);

      Page<TestDocument> page = repo.run(
        new Criteria(),
        PageRequest.of(0, PAGE_SIZE),
        false,
        REMAINING_BUDGET
      );

      assertThat(page.getTotalElements()).isEqualTo(400L);
    }

    @Test
    @DisplayName("should keep the full match count when a budgeted query targets a later page")
    void shouldKeepFullCountWhenBudgetAppliesBeyondFirstPage() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(REMAINING_BUDGET, documents(REMAINING_BUDGET));

      Page<TestDocument> page = repo.run(
        new Criteria(),
        PageRequest.of(2, PAGE_SIZE),
        false,
        REMAINING_BUDGET
      );

      assertThat(page.getTotalElements()).isEqualTo(REMAINING_BUDGET);
    }

    @Test
    @DisplayName("should report the returned row count as the page size when the budget applies")
    void shouldReportReturnedRowCountAsPageSizeWhenBudgetApplies() {
      BareRepo repo = new BareRepo(mongoTemplate);
      // More rows than a page holds: the budget spans pages, and a future budget counted in
      // parent entities could return several rows per unit of budget.
      int returnedRows = PAGE_SIZE + 2;
      stubMongoTemplate(400L, documents(returnedRows));

      Page<TestDocument> page = repo.run(
        new Criteria(),
        PageRequest.of(2, PAGE_SIZE),
        false,
        REMAINING_BUDGET
      );

      assertThat(page.getNumber()).isZero();
      assertThat(page.getSize()).isEqualTo(returnedRows);
    }

    @Test
    @DisplayName("should report the requested page number and size when the budget does not apply")
    void shouldReportRequestedPageShapeWhenBudgetDoesNotApply() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(400L, documents(PAGE_SIZE));

      Page<TestDocument> page = repo.run(new Criteria(), PageRequest.of(2, PAGE_SIZE), false, null);

      assertThat(page.getNumber()).isEqualTo(2);
      assertThat(page.getSize()).isEqualTo(PAGE_SIZE);
    }

    @Test
    @DisplayName("should ignore the budget and paginate normally on an include query")
    void shouldIgnoreBudgetWhenIncluding() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(0L);

      Pageable pageable = PageRequest.of(2, PAGE_SIZE, Sort.by(Sort.Order.asc("name")));
      repo.run(new Criteria(), pageable, true, REMAINING_BUDGET);

      String pipeline = capturePipeline().toString();
      assertThat(pipeline).contains("$skip").contains("$limit");
      assertThat(pipeline).doesNotContain(String.valueOf(REMAINING_BUDGET));
    }

    @Test
    @DisplayName("should paginate normally when the budget is null")
    void shouldPaginateNormallyWhenBudgetIsNull() {
      BareRepo repo = new BareRepo(mongoTemplate);
      stubMongoTemplate(0L);

      Pageable pageable = PageRequest.of(2, PAGE_SIZE, Sort.by(Sort.Order.asc("name")));
      repo.run(new Criteria(), pageable, false, null);

      String pipeline = capturePipeline().toString();
      assertThat(pipeline).contains("$skip").contains("$limit");
    }
  }
}
