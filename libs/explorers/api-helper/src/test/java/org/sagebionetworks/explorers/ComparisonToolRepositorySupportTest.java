package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
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
    assertThat(pipeline).doesNotContain("$addFields");
    assertThat(pipeline).contains("\"name\" : 1");
  }

  @Test
  @DisplayName("should resolve sort field aliases to the aliased name (no $addFields)")
  void shouldResolveSortFieldAliases() {
    AliasingRepo repo = new AliasingRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("age")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline).contains("\"age_numeric\" : -1");
    assertThat(pipeline).doesNotContain("$addFields");
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
  @DisplayName("should skip $addFields when the sort doesn't include any computed field")
  void shouldSkipComputedAddFieldsWhenUnused() {
    ComputedRepo repo = new ComputedRepo(mongoTemplate);
    stubMongoTemplate(0L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("hgnc_symbol")));
    repo.run(new Criteria(), pageable);

    String pipeline = capturePipeline().toString();
    assertThat(pipeline).doesNotContain("$addFields").doesNotContain("name_sort");
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
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION))).thenReturn(total);
    when(mongoTemplate.aggregate(any(Aggregation.class), eq(COLLECTION), eq(TestDocument.class)))
      .thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());
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
      return executePagedAggregation(criteria, pageable);
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
      return executePagedAggregation(criteria, pageable);
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
      return Map.of("name", ComputedSortField.of(toLowerExpr("name")));
    }

    Page<TestDocument> run(Criteria criteria, Pageable pageable) {
      return executePagedAggregation(criteria, pageable);
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
        ComputedSortField.of(toLowerExpr("display_gene_symbol")).withPrerequisite(prereq)
      );
    }

    Page<TestDocument> run(Criteria criteria, Pageable pageable) {
      return executePagedAggregation(criteria, pageable);
    }
  }
}
