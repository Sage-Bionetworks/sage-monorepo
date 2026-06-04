package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Tests for {@link ComparisonToolRepositorySupport#buildCtMatchCriteria}.
 *
 * <p>Uses a minimal test repository to exercise the filter-building logic without full MongoDB
 * infrastructure.
 */
class BuildCtMatchCriteriaTest {

  @Nested
  @DisplayName("Data filters")
  class DataFilters {

    @Test
    @DisplayName("should add $in criteria for non-empty data filter values")
    void shouldAddDataFilterCriteria() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of("value1", "value2"), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .dataFilter("field1", TestQuery::getField1)
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(query, List.of(), false, null, config);

      assertThat(result.getCriteriaObject().toString()).contains("field1").contains("value1");
    }

    @Test
    @DisplayName("should skip data filters when values are null or empty")
    void shouldSkipEmptyDataFilters() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(null, List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .dataFilter("field1", TestQuery::getField1)
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(query, List.of(), false, null, config);

      assertThat(result.getCriteriaObject().toString()).doesNotContain("field1");
    }

    @Test
    @DisplayName("should combine multiple data filters with AND")
    void shouldCombineMultipleDataFilters() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of("a"), List.of("b"));

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .dataFilter("field1", TestQuery::getField1)
        .dataFilter("field2", TestQuery::getField2)
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(query, List.of(), false, null, config);

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("field1").contains("field2");
    }
  }

  @Nested
  @DisplayName("Simple item filter")
  class SimpleItemFilter {

    @Test
    @DisplayName("should use $in for INCLUDE mode with non-empty items")
    void shouldUseInForIncludeMode() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("hgnc_symbol")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(
        query,
        List.of("APOE", "APP"),
        true,
        null,
        config
      );

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("hgnc_symbol").contains("$in").contains("APOE");
    }

    @Test
    @DisplayName("should use $nin for EXCLUDE mode with non-empty items")
    void shouldUseNinForExcludeMode() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("hgnc_symbol")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(
        query,
        List.of("APOE", "APP"),
        false,
        null,
        config
      );

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("hgnc_symbol").contains("$nin").contains("APOE");
    }

    @Test
    @DisplayName("should add impossible condition for empty items in INCLUDE mode")
    void shouldAddImpossibleConditionForEmptyInclude() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("hgnc_symbol")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(query, List.of(), true, null, config);

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("_id").contains("null");
    }

    @Test
    @DisplayName("should skip item filter for empty items in EXCLUDE mode")
    void shouldSkipFilterForEmptyExclude() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("hgnc_symbol")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(query, List.of(), false, null, config);

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).doesNotContain("hgnc_symbol").doesNotContain("_id");
    }
  }

  @Nested
  @DisplayName("Composite item filter")
  class CompositeItemFilter {

    @Test
    @DisplayName("should parse items and combine with $or for INCLUDE mode")
    void shouldUseOrForIncludeMode() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .compositeItemFilter(item -> {
          String[] parts = item.split("~");
          return new Criteria()
            .andOperator(
              Criteria.where("field1").is(parts[0]),
              Criteria.where("field2").is(parts[1])
            );
        })
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(
        query,
        List.of("a~b", "c~d"),
        true,
        null,
        config
      );

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("$or").contains("field1").contains("field2");
    }

    @Test
    @DisplayName("should parse items and combine with $nor for EXCLUDE mode")
    void shouldUseNorForExcludeMode() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .compositeItemFilter(item -> {
          String[] parts = item.split("~");
          return new Criteria()
            .andOperator(
              Criteria.where("field1").is(parts[0]),
              Criteria.where("field2").is(parts[1])
            );
        })
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(
        query,
        List.of("a~b", "c~d"),
        false,
        null,
        config
      );

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("$nor").contains("field1").contains("field2");
    }
  }

  @Nested
  @DisplayName("Search filter")
  class SearchFilter {

    @Test
    @DisplayName("should skip search in INCLUDE mode")
    void shouldSkipSearchInIncludeMode() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(query, List.of(), true, "test", config);

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).doesNotContain("name").doesNotContain("test");
    }

    @Test
    @DisplayName("should use regex for single term search in EXCLUDE mode")
    void shouldUseRegexForSingleTermSearch() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(query, List.of(), false, "test", config);

      String criteriaStr = result.getCriteriaObject().toString();
      // The Criteria contains "name" field with regex matching
      assertThat(criteriaStr).contains("name");
      assertThat(criteriaStr).isNotEmpty();
    }

    @Test
    @DisplayName("should use exact match patterns for comma-separated search")
    void shouldUseExactMatchForCommaSeparatedSearch() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(query, List.of(), false, "a,b,c", config);

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("name").contains("$in");
    }

    @Test
    @DisplayName("should skip search when search string is null or blank")
    void shouldSkipBlankSearch() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      Criteria result1 = repo.buildCtMatchCriteria(query, List.of(), false, null, config);
      Criteria result2 = repo.buildCtMatchCriteria(query, List.of(), false, "  ", config);

      assertThat(result1.getCriteriaObject().toString()).doesNotContain("name");
      assertThat(result2.getCriteriaObject().toString()).doesNotContain("name");
    }
  }

  @Nested
  @DisplayName("Empty criteria")
  class EmptyCriteria {

    @Test
    @DisplayName("should return match-all criteria when no filters are active")
    void shouldReturnMatchAllWhenNoFiltersActive() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(null, null);

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .dataFilter("field1", TestQuery::getField1)
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      // No data filters, no items (EXCLUDE), no search — allCriteria would be empty
      Criteria result = repo.buildCtMatchCriteria(query, List.of(), false, null, config);

      // Empty Criteria produces {} which matches all documents (no $and with empty array)
      assertThat(result.getCriteriaObject().isEmpty()).isTrue();
    }
  }

  @Nested
  @DisplayName("Base criteria")
  class BaseCriteria {

    @Test
    @DisplayName("should include all base criteria in result")
    void shouldIncludeBaseCriteria() {
      TestRepo repo = new TestRepo();
      TestQuery query = new TestQuery(List.of(), List.of());

      CtFilterConfig<TestQuery> config = CtFilterConfig.<TestQuery>builder()
        .simpleItemFilter("id")
        .searchFilter("name")
        .build();

      Criteria result = repo.buildCtMatchCriteria(
        query,
        List.of(),
        false,
        null,
        config,
        Criteria.where("cluster").is("c1"),
        Criteria.where("tissue").is("brain")
      );

      String criteriaStr = result.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("cluster").contains("c1").contains("tissue").contains("brain");
    }
  }

  /** Test query DTO. */
  @Data
  @AllArgsConstructor
  private static class TestQuery {

    List<String> field1;
    List<String> field2;
  }

  /** Minimal test repository. */
  private static class TestRepo extends ComparisonToolRepositorySupport<Object> {

    TestRepo() {
      super(null);
    }

    @Override
    protected String getCollectionName() {
      return "test";
    }

    @Override
    protected Class<Object> getDocumentClass() {
      return Object.class;
    }

    // Expose protected method for testing
    @Override
    public <Q> Criteria buildCtMatchCriteria(
      Q query,
      List<String> items,
      boolean isInclude,
      String search,
      CtFilterConfig<Q> config,
      Criteria... baseCriteria
    ) {
      return super.buildCtMatchCriteria(query, items, isInclude, search, config, baseCriteria);
    }
  }
}
