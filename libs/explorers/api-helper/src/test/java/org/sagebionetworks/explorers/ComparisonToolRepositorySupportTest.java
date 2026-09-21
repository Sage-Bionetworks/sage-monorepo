package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
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
  private static final String ROW_ID_FIELD = "unique_id";
  private static final String PARENT_ID_FIELD = "rna_composite_id";

  /** The stored fields a composite parent token is built from, in token order. */
  private static final List<String> PARENT_ID_FIELDS = List.of(
    "ensembl_gene_id",
    "model_name",
    "sex"
  );

  /** A nullable, object-valued column, aliased to its numeric sub-field for sorting. */
  private static final String SPACED_SORT_FIELD = "4 months";
  private static final String SPACED_SORT_PATH = "4 months.log2_fc";
  private static final String SPACED_SORT_FLAG = "4_months_isEmpty";

  /**
   * The parent selection's internal keys, mirrored from {@link ComparisonToolRepositorySupport}.
   */
  private static final String PARENT_TOKEN_FIELD = "__ctparent";
  private static final String SORT_KEY_ALIAS = "__ctsk0";

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<TestDocument> aggregationResults;

  @Mock
  private AggregationResults<Document> parentSelectionResults;

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

  /** The row identity space every parent-aware fixture below declares. */
  @SuppressWarnings("unchecked")
  private static <Q> CtFilterConfig<Q> rowIdFilterConfig() {
    return (CtFilterConfig<Q>) CtFilterConfig.<Object>builder()
      .simpleItemFilter(ROW_ID_FIELD)
      .searchFilter(ROW_ID_FIELD)
      .build();
  }

  /**
   * Stands in for an identifier DTO's {@code toCriteria()}: splits a parent token back across the
   * fields it was built from.
   */
  private static Criteria parseParentToken(String token) {
    String[] parts = token.split(Pattern.quote(ItemIdSpaceDef.DELIMITER));
    Criteria[] clauses = new Criteria[PARENT_ID_FIELDS.size()];
    for (int i = 0; i < clauses.length; i++) {
      clauses[i] = Criteria.where(PARENT_ID_FIELDS.get(i)).is(parts[i]);
    }
    return new Criteria().andOperator(clauses);
  }

  private Aggregation capturePipeline() {
    ArgumentCaptor<Aggregation> captor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
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

  /** Subclass whose rows are their own parents — the default for every existing CT. */
  private static final class SelfParentedRepo
    extends ComparisonToolRepositorySupport<TestDocument> {

    SelfParentedRepo(MongoTemplate mongoTemplate) {
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
    protected <Q> CtFilterConfig<Q> getFilterConfig() {
      return rowIdFilterConfig();
    }

    CtPage<TestDocument> run(Criteria criteria, Pageable pageable, CtQueryOptions options) {
      return executePagedAggregation(criteria, pageable, options);
    }

    CtPage<TestDocument> run(
      Criteria criteria,
      Pageable pageable,
      boolean isInclude,
      Integer remainingBudget
    ) {
      return executePagedAggregation(criteria, pageable, isInclude, remainingBudget);
    }
  }

  /** Subclass whose rows roll up to parents identified in a separate space. */
  private static final class ParentAwareRepo extends ComparisonToolRepositorySupport<TestDocument> {

    ParentAwareRepo(MongoTemplate mongoTemplate) {
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
    protected <Q> CtFilterConfig<Q> getFilterConfig() {
      return rowIdFilterConfig();
    }

    @Override
    protected ItemIdSpaceDef getParentIdSpace() {
      return ItemIdSpaceDef.stored(PARENT_ID_FIELD);
    }

    @Override
    protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
      return Map.of("name", ComputedSortField.of(new Document("$toLower", "$name")));
    }

    CtPage<TestDocument> run(Criteria criteria, Pageable pageable, CtQueryOptions options) {
      return executePagedAggregation(criteria, pageable, options);
    }
  }

  /**
   * Subclass whose rows roll up to parents identified by a composite token, sorting on a spaced,
   * nullable column. Exercises the {@code $concat} token shape and the aliasing and isEmpty paths
   * the parent selection depends on, none of which {@link ParentAwareRepo}'s single stored field
   * reaches.
   */
  private static final class CompositeParentRepo
    extends ComparisonToolRepositorySupport<TestDocument> {

    CompositeParentRepo(MongoTemplate mongoTemplate) {
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
    protected <Q> CtFilterConfig<Q> getFilterConfig() {
      return rowIdFilterConfig();
    }

    @Override
    protected ItemIdSpaceDef getParentIdSpace() {
      return ItemIdSpaceDef.composite(
        PARENT_ID_FIELDS,
        ComparisonToolRepositorySupportTest::parseParentToken
      );
    }

    @Override
    protected Map<String, String> getSortFieldAliases() {
      return Map.of(SPACED_SORT_FIELD, SPACED_SORT_PATH);
    }

    CtPage<TestDocument> run(Criteria criteria, Pageable pageable, CtQueryOptions options) {
      return executePagedAggregation(criteria, pageable, options);
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
    @DisplayName("should emit no $limit at all when the budget is exhausted")
    void shouldEmitNoLimitWhenBudgetIsExhausted() {
      SelfParentedRepo repo = new SelfParentedRepo(mongoTemplate);
      stubMongoTemplate(0L);

      repo.run(new Criteria(), PageRequest.of(0, PAGE_SIZE), false, 0);

      assertThat(capturePipeline().toString())
        .as("{$limit: 0} is rejected by the server, and there is no row to cap anyway")
        .doesNotContain("$limit")
        .doesNotContain("$skip");
    }

    @Test
    @DisplayName("should keep the full match count when the budget is exhausted")
    void shouldKeepFullCountWhenBudgetIsExhausted() {
      SelfParentedRepo repo = new SelfParentedRepo(mongoTemplate);
      stubMongoTemplate(400L);

      Page<TestDocument> page = repo.run(new Criteria(), PageRequest.of(0, PAGE_SIZE), false, 0);

      assertThat(page.getTotalElements())
        .as("an exhausted budget still reports how much the caller is missing")
        .isEqualTo(400L);
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

  @Nested
  @DisplayName("prebudgeted parents")
  class PrebudgetedParents {

    private static final int PAGE_SIZE = 10;
    private static final List<String> PARENT_IDS = List.of("ENSG1~5xFAD~Female");

    private final Pageable pageable = PageRequest.of(
      0,
      PAGE_SIZE,
      Sort.by(Sort.Order.asc("name"))
    );

    private CtQueryOptions options(boolean isInclude, List<String> prebudgetedParentIds) {
      return new CtQueryOptions(isInclude, null, prebudgetedParentIds, false);
    }

    @Test
    @DisplayName("should leave the flag unset when no prebudgeted parents are given")
    void shouldLeaveFlagUnsetWhenNoPrebudgetedParentsAreGiven() {
      ParentAwareRepo repo = new ParentAwareRepo(mongoTemplate);
      stubMongoTemplate(0L);

      CtPage<TestDocument> page = repo.run(new Criteria(), pageable, options(false, List.of()));

      assertThat(page.getHasRowsForPrebudgetedParents()).isNull();
      verify(mongoTemplate, never()).exists(any(Query.class), anyString());
    }

    @Test
    @DisplayName("should leave the flag unset on an include query even with prebudgeted parents")
    void shouldLeaveFlagUnsetWhenIncluding() {
      ParentAwareRepo repo = new ParentAwareRepo(mongoTemplate);
      stubMongoTemplate(0L);

      CtPage<TestDocument> page = repo.run(new Criteria(), pageable, options(true, PARENT_IDS));

      assertThat(page.getHasRowsForPrebudgetedParents()).isNull();
      verify(mongoTemplate, never()).exists(any(Query.class), anyString());
    }

    @Test
    @DisplayName("should report true when a matching row belongs to a prebudgeted parent")
    void shouldReportTrueWhenMatchingRowBelongsToPrebudgetedParent() {
      ParentAwareRepo repo = new ParentAwareRepo(mongoTemplate);
      stubMongoTemplate(0L);
      when(mongoTemplate.exists(any(Query.class), eq(COLLECTION))).thenReturn(true);

      CtPage<TestDocument> page = repo.run(new Criteria(), pageable, options(false, PARENT_IDS));

      assertThat(page.getHasRowsForPrebudgetedParents()).isTrue();
    }

    @Test
    @DisplayName("should report false when no matching row belongs to a prebudgeted parent")
    void shouldReportFalseWhenNoMatchingRowBelongsToPrebudgetedParent() {
      ParentAwareRepo repo = new ParentAwareRepo(mongoTemplate);
      stubMongoTemplate(0L);
      when(mongoTemplate.exists(any(Query.class), eq(COLLECTION))).thenReturn(false);

      CtPage<TestDocument> page = repo.run(new Criteria(), pageable, options(false, PARENT_IDS));

      assertThat(page.getHasRowsForPrebudgetedParents()).isFalse();
    }

    @Test
    @DisplayName("should probe the parent identity space alongside the request's match criteria")
    void shouldProbeParentIdentitySpaceAlongsideMatchCriteria() {
      ParentAwareRepo repo = new ParentAwareRepo(mongoTemplate);
      stubMongoTemplate(0L);
      when(mongoTemplate.exists(any(Query.class), eq(COLLECTION))).thenReturn(true);

      Criteria matchCriteria = Criteria.where("tissue").is("brain");
      repo.run(matchCriteria, pageable, options(false, PARENT_IDS));

      String queryStr = captureExistsQuery().getQueryObject().toString();
      assertThat(queryStr)
        .contains(PARENT_ID_FIELD)
        .contains("$in")
        .contains(PARENT_IDS.get(0))
        .contains("tissue")
        .doesNotContain(ROW_ID_FIELD);
    }

    @Test
    @DisplayName("should probe stored fields only, as the probe bypasses the aggregation pipeline")
    void shouldProbeStoredFieldsOnly() {
      ParentAwareRepo repo = new ParentAwareRepo(mongoTemplate);
      stubMongoTemplate(0L);
      when(mongoTemplate.exists(any(Query.class), eq(COLLECTION))).thenReturn(true);

      repo.run(new Criteria(), pageable, options(false, PARENT_IDS));

      // The sorted pipeline computes name_sort and name_isEmpty; neither exists as a stored field,
      // so a probe referencing them would silently match nothing.
      assertThat(capturePipeline().toString()).contains("name_sort").contains("name_isEmpty");
      assertThat(captureExistsQuery().getQueryObject().toString())
        .doesNotContain("_sort")
        .doesNotContain("_isEmpty");
    }

    @Test
    @DisplayName("should probe case-insensitively so parent tokens match regardless of casing")
    void shouldProbeCaseInsensitively() {
      ParentAwareRepo repo = new ParentAwareRepo(mongoTemplate);
      stubMongoTemplate(0L);
      when(mongoTemplate.exists(any(Query.class), eq(COLLECTION))).thenReturn(true);

      repo.run(new Criteria(), pageable, options(false, PARENT_IDS));

      assertThat(captureExistsQuery().getCollation()).isPresent();
    }

    @Test
    @DisplayName("should probe the row identity space when the comparison tool is self-parented")
    void shouldProbeRowIdentitySpaceWhenSelfParented() {
      SelfParentedRepo repo = new SelfParentedRepo(mongoTemplate);
      stubMongoTemplate(0L);
      when(mongoTemplate.exists(any(Query.class), eq(COLLECTION))).thenReturn(true);

      repo.run(new Criteria(), pageable, options(false, PARENT_IDS));

      assertThat(captureExistsQuery().getQueryObject().toString())
        .contains(ROW_ID_FIELD)
        .doesNotContain(PARENT_ID_FIELD);
    }

    private Query captureExistsQuery() {
      ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
      verify(mongoTemplate).exists(captor.capture(), eq(COLLECTION));
      return captor.getValue();
    }
  }

  @Nested
  @DisplayName("parent-aware capping")
  class ParentAwareCapping {

    private static final int PAGE_SIZE = 10;

    private static final List<String> PREBUDGETED = List.of(
      "ENSG1~5xFAD~Female",
      "ENSG2~5xFAD~Male"
    );

    /** The parents the selection is stubbed to return, in the order the aggregation would. */
    private static final List<String> SELECTED = List.of(
      "ENSG3~5xFAD~Female",
      "ENSG4~Trem2~Male",
      "ENSG5~Trem2~Female"
    );

    /** A budget exactly spent by {@link #SELECTED}, so a short selection cannot pass unnoticed. */
    private static final int BUDGET = SELECTED.size();

    /** The pipeline stages DocumentDB supports, which the deployed stack runs on. */
    private static final Set<String> DOCUMENT_DB_STAGES = Set.of(
      "$addFields",
      "$group",
      "$limit",
      "$match",
      "$sort"
    );

    private static final List<String> ALL_ADMITTED = Stream.concat(
      PREBUDGETED.stream(),
      SELECTED.stream()
    ).toList();

    private final Criteria matchCriteria = Criteria.where("tissue").is("brain");

    private final Pageable pageable = PageRequest.of(
      0,
      PAGE_SIZE,
      Sort.by(Sort.Order.asc(SPACED_SORT_FIELD))
    );

    private CtQueryOptions options(int remainingBudget, List<String> prebudgetedParentIds) {
      return new CtQueryOptions(false, remainingBudget, prebudgetedParentIds, false);
    }

    @Test
    @DisplayName("should exclude the prebudgeted parents when selecting new ones")
    void shouldExcludePrebudgetedParentsWhenSelectingNewOnes() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      Criteria expected = new Criteria()
        .andOperator(matchCriteria, parentsCriteria(PREBUDGETED, Criteria::norOperator));
      assertThat(stageNamed(parentSelectionStages(), "$match"))
        .as("the budget buys new parents, so a prebudgeted one cannot consume any of it")
        .isEqualTo(expected.getCriteriaObject());
    }

    @Test
    @DisplayName("should select against the request's own criteria when no parents are prebudgeted")
    void shouldSelectAgainstRequestCriteriaWhenNoParentsArePrebudgeted() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, List.of()));

      assertThat(stageNamed(parentSelectionStages(), "$match"))
        .isEqualTo(matchCriteria.getCriteriaObject());
    }

    @Test
    @DisplayName("should group rows by the bare parent token rather than by a field tuple")
    void shouldGroupRowsByTheBareParentToken() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      List<Document> stages = parentSelectionStages();
      assertThat(stageNamed(stages, "$group"))
        .as("a tuple key would leave the inherited _id tiebreaker sorting an object")
        .containsEntry("_id", "$" + PARENT_TOKEN_FIELD);

      Document tokenExpression = parentTokenFields(stages).get(PARENT_TOKEN_FIELD, Document.class);
      assertThat(tokenExpression.getList("$concat", Object.class))
        .as("one part per field, with a delimiter between each pair")
        .hasSize(2 * PARENT_ID_FIELDS.size() - 1);
      assertThat(tokenExpression.toString())
        .as("a null field would otherwise collapse every affected parent into one group")
        .contains("$ifNull");
    }

    @Test
    @DisplayName("should read each spaced sort path into a safe alias before grouping")
    void shouldReadSpacedSortPathIntoSafeAliasBeforeGrouping() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      List<Document> stages = parentSelectionStages();
      assertThat(parentTokenFields(stages).get(SORT_KEY_ALIAS, Document.class))
        .as("a dotted, spaced path is unreadable both as a $group key and as a $first argument")
        .containsKey("$getField");
      assertThat(stageNamed(stages, "$group")).containsEntry(
        SORT_KEY_ALIAS,
        new Document("$first", "$" + SORT_KEY_ALIAS)
      );
    }

    @Test
    @DisplayName("should order parents by every key the row sort names, in the same order")
    void shouldOrderParentsByEveryKeyTheRowSortNames() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      List<Document> stages = parentSelectionStages();
      assertThat(sortBeforeGroup(stages).keySet())
        .containsExactly(SPACED_SORT_FLAG, SPACED_SORT_PATH, "_id");
      assertThat(sortAfterGroup(stages).keySet())
        .as("each row sort key maps to the key it was captured under, in the same order")
        .containsExactly(SPACED_SORT_FLAG, SORT_KEY_ALIAS, "_id");
    }

    @Test
    @DisplayName("should capture the isEmpty flag so parents with an empty sort value rank last")
    void shouldCaptureIsEmptyFlagSoParentsWithAnEmptySortValueRankLast() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      List<Document> stages = parentSelectionStages();
      assertThat(stageNamed(stages, "$group"))
        .as("capture the resolved path alone and an empty-valued parent heads the selection")
        .containsEntry(SPACED_SORT_FLAG, new Document("$first", "$" + SPACED_SORT_FLAG));
      assertThat(sortAfterGroup(stages))
        .as("the flag sorts ascending for parents exactly as it does for rows")
        .containsEntry(SPACED_SORT_FLAG, 1);
    }

    @Test
    @DisplayName("should select parents with DocumentDB-supported stages only")
    void shouldSelectParentsWithDocumentDbSupportedStagesOnly() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      assertThat(parentSelectionStages().stream().flatMap(stage -> stage.keySet().stream()))
        .as("the deployed stack runs DocumentDB, which has no $setWindowFields and no $facet")
        .isSubsetOf(DOCUMENT_DB_STAGES);
    }

    @Test
    @DisplayName("should limit the selection to the remaining budget")
    void shouldLimitTheSelectionToTheRemainingBudget() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      assertThat(lastStage(parentSelectionStages())).isEqualTo(
        new Document("$limit", (long) BUDGET)
      );
    }

    @Test
    @DisplayName("should admit the budgeted parents on top of the prebudgeted ones")
    void shouldAdmitBudgetedParentsOnTopOfPrebudgetedOnes() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      assertThat(admittedParentsClause(rowStages()))
        .as("a budget of %d behind %d prebudgeted parents admits %d more, not fewer", BUDGET,
          PREBUDGETED.size(), BUDGET)
        .isEqualTo(parentsClause(ALL_ADMITTED, Criteria::orOperator));
    }

    @Test
    @DisplayName("should return every row of an admitted parent rather than one page of them")
    void shouldReturnEveryRowOfAnAdmittedParent() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, PageRequest.of(2, PAGE_SIZE, pageable.getSort()), options(
        BUDGET,
        PREBUDGETED
      ));

      assertThat(rowStages())
        .as("the admitted parents already bound the result, so nothing is skipped or capped")
        .noneMatch(stage -> stage.containsKey("$skip") || stage.containsKey("$limit"));
    }

    @Test
    @DisplayName("should keep the full match count so callers can still derive their skip count")
    void shouldKeepFullMatchCountWhenCappingParents() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(400L, documents(12));
      stubParentSelection(SELECTED);

      CtPage<TestDocument> page = repo.run(matchCriteria, pageable, options(BUDGET, PREBUDGETED));

      assertThat(page.getTotalElements()).isEqualTo(400L);
      assertThat(captureCountQuery().getQueryObject())
        .as("the count is of the whole match set, not of the admitted parents' rows")
        .isEqualTo(matchCriteria.getCriteriaObject());
    }

    @Test
    @DisplayName("should admit the prebudgeted parents alone when the budget is zero")
    void shouldAdmitPrebudgetedParentsAloneWhenBudgetIsZero() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);

      repo.run(matchCriteria, pageable, options(0, PREBUDGETED));

      verifyNoParentSelection();
      assertThat(admittedParentsClause(rowStages())).isEqualTo(
        parentsClause(PREBUDGETED, Criteria::orOperator)
      );
    }

    @Test
    @DisplayName("should return the prebudgeted parents' rows unpaged when the budget is zero")
    void shouldReturnPrebudgetedParentsRowsUnpagedWhenBudgetIsZero() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      int freeRows = PAGE_SIZE + 2;
      stubMongoTemplate(400L, documents(freeRows));

      CtPage<TestDocument> page = repo.run(
        matchCriteria,
        PageRequest.of(2, PAGE_SIZE, pageable.getSort()),
        options(0, PREBUDGETED)
      );

      assertThat(rowStages())
        .as("a child of a parent already accounted for costs no budget, so none is skipped or cut")
        .noneMatch(stage -> stage.containsKey("$skip") || stage.containsKey("$limit"));
      assertThat(page.getContent())
        .as("an exhausted budget still returns free children, beyond the page the caller asked for")
        .hasSize(freeRows);
    }

    @Test
    @DisplayName("should match nothing when the budget is zero and no parents are prebudgeted")
    void shouldMatchNothingWhenBudgetIsZeroAndNoParentsArePrebudgeted() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);

      repo.run(matchCriteria, pageable, options(0, List.of()));

      verifyNoParentSelection();
      assertThat(admittedParentsClause(rowStages()))
        .as("an exhausted budget with nothing prebudgeted admits no parent, so returns no row")
        .isEqualTo(ApiHelper.matchNothing().getCriteriaObject());
    }

    @Test
    @DisplayName("should order the selection by the parent token alone when unsorted")
    void shouldOrderSelectionByParentTokenAloneWhenUnsorted() {
      CompositeParentRepo repo = new CompositeParentRepo(mongoTemplate);
      stubMongoTemplate(0L);
      stubParentSelection(SELECTED);

      repo.run(matchCriteria, PageRequest.of(0, PAGE_SIZE), options(BUDGET, List.of()));

      List<Document> stages = parentSelectionStages();
      assertThat(stages.stream().filter(stage -> stage.containsKey("$sort")).toList())
        .as("an unsorted request has no row order to reproduce")
        .containsExactly(new Document("$sort", new Document("_id", 1)));
      assertThat(stageNamed(stages, "$group"))
        .as("nothing to carry per parent, so the group holds its key alone")
        .isEqualTo(new Document("_id", "$" + PARENT_TOKEN_FIELD));
      assertThat(rowStages())
        .as("whole parents come back even unsorted; the budget never falls back to capping rows")
        .noneMatch(stage -> stage.containsKey("$limit"));
    }

    @Test
    @DisplayName("should cap rows with a bare $limit when the comparison tool is self-parented")
    void shouldCapRowsWithBareLimitWhenSelfParented() {
      SelfParentedRepo repo = new SelfParentedRepo(mongoTemplate);
      stubMongoTemplate(0L);

      repo.run(matchCriteria, pageable, options(BUDGET, List.of()));

      verifyNoParentSelection();
      List<Document> stages = rowStages();
      assertThat(stages.get(0).get("$match", Document.class))
        .as("a self-parented CT's criteria are untouched by the budget")
        .isEqualTo(matchCriteria.getCriteriaObject());
      assertThat(lastStage(stages)).isEqualTo(new Document("$limit", (long) BUDGET));
      assertThat(stages).noneMatch(stage -> stage.containsKey("$skip"));
    }

    @Test
    @DisplayName("should admit the prebudgeted parents when a self-parented budget is exhausted")
    void shouldAdmitPrebudgetedParentsWhenSelfParentedBudgetIsExhausted() {
      SelfParentedRepo repo = new SelfParentedRepo(mongoTemplate);
      stubMongoTemplate(0L);

      repo.run(matchCriteria, pageable, options(0, PREBUDGETED));

      verifyNoParentSelection();
      List<Document> stages = rowStages();
      Document rowIdClause = admittedParentsClause(stages).get(ROW_ID_FIELD, Document.class);
      @SuppressWarnings("unchecked")
      Collection<Object> admittedIds = (Collection<Object>) rowIdClause.get("$in");
      assertThat(admittedIds)
        .as("an exhausted budget frees children of accounted-for parents whether or not the CT has"
          + " a parent space of its own")
        .containsExactly(PREBUDGETED.toArray());
      assertThat(stages)
        .as("{$limit: 0} is rejected by the server, and free children are unpaged besides")
        .noneMatch(stage -> stage.containsKey("$limit") || stage.containsKey("$skip"));
    }

    private void stubParentSelection(List<String> parentTokens) {
      when(
        mongoTemplate.aggregate(any(Aggregation.class), eq(COLLECTION), eq(Document.class))
      ).thenReturn(parentSelectionResults);
      when(parentSelectionResults.getMappedResults()).thenReturn(
        parentTokens.stream().map(token -> new Document("_id", token)).toList()
      );
    }

    private void verifyNoParentSelection() {
      verify(mongoTemplate, never()).aggregate(
        any(Aggregation.class),
        anyString(),
        eq(Document.class)
      );
    }

    private List<Document> parentSelectionStages() {
      ArgumentCaptor<Aggregation> captor = ArgumentCaptor.forClass(Aggregation.class);
      verify(mongoTemplate).aggregate(captor.capture(), eq(COLLECTION), eq(Document.class));
      return captor.getValue().toPipeline(Aggregation.DEFAULT_CONTEXT);
    }

    private List<Document> rowStages() {
      return capturePipeline().toPipeline(Aggregation.DEFAULT_CONTEXT);
    }

    private Query captureCountQuery() {
      ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
      verify(mongoTemplate).count(captor.capture(), eq(COLLECTION));
      return captor.getValue();
    }

    /** The criteria an identity space builds for {@code tokens}. */
    private Criteria parentsCriteria(
      List<String> tokens,
      BiFunction<Criteria, Criteria[], Criteria> combine
    ) {
      Criteria[] parsed = tokens
        .stream()
        .map(ComparisonToolRepositorySupportTest::parseParentToken)
        .toArray(Criteria[]::new);
      return combine.apply(new Criteria(), parsed);
    }

    /** Those criteria as the pipeline renders them. */
    private Document parentsClause(
      List<String> tokens,
      BiFunction<Criteria, Criteria[], Criteria> combine
    ) {
      return parentsCriteria(tokens, combine).getCriteriaObject();
    }

    /** The clause naming the admitted parents in the row pipeline's {@code $match}. */
    private Document admittedParentsClause(List<Document> stages) {
      List<Document> andClauses = stageNamed(stages, "$match").getList("$and", Document.class);
      assertThat(andClauses)
        .as("the row $match ANDs the caller's criteria with the admitted parents")
        .hasSize(2);
      return andClauses.get(1);
    }

    private Document parentTokenFields(List<Document> stages) {
      return stages
        .stream()
        .map(stage -> stage.get("$addFields", Document.class))
        .filter(fields -> fields != null && fields.containsKey(PARENT_TOKEN_FIELD))
        .findFirst()
        .orElseThrow(() ->
          new AssertionError("no $addFields stage materialises " + PARENT_TOKEN_FIELD)
        );
    }

    private Document sortBeforeGroup(List<Document> stages) {
      return sortStageAt(stages, indexOfStage(stages, "$group") - 1, "before");
    }

    private Document sortAfterGroup(List<Document> stages) {
      return sortStageAt(stages, indexOfStage(stages, "$group") + 1, "after");
    }

    private Document sortStageAt(List<Document> stages, int index, String position) {
      Document stage = stages.get(index);
      assertThat(stage).as("the stage %s $group must be a $sort", position).containsKey("$sort");
      return stage.get("$sort", Document.class);
    }

    private Document stageNamed(List<Document> stages, String name) {
      return stages.get(indexOfStage(stages, name)).get(name, Document.class);
    }

    private Document lastStage(List<Document> stages) {
      return stages.get(stages.size() - 1);
    }

    private int indexOfStage(List<Document> stages, String name) {
      for (int i = 0; i < stages.size(); i++) {
        if (stages.get(i).containsKey(name)) {
          return i;
        }
      }
      throw new AssertionError("no " + name + " stage in " + stages);
    }
  }
}
