package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

class ApiHelperTest {

  @Nested
  @DisplayName("validateSortParameters")
  class ValidateSortParameters {

    @Test
    @DisplayName("should throw when sortFields has more elements than sortOrders")
    void shouldThrowWhenSortFieldsHasMoreElements() {
      assertThatThrownBy(() -> ApiHelper.validateSortParameters(List.of("name", "age"), List.of(1)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
          "sortFields and sortOrders must have the same number of elements. Got 2 field(s) and 1 order(s)"
        );
    }

    @Test
    @DisplayName("should throw when sortOrders has more elements than sortFields")
    void shouldThrowWhenSortOrdersHasMoreElements() {
      assertThatThrownBy(() -> ApiHelper.validateSortParameters(List.of("name"), List.of(1, -1, 1)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
          "sortFields and sortOrders must have the same number of elements. Got 1 field(s) and 3 order(s)"
        );
    }

    @Test
    @DisplayName("should not throw when sortFields and sortOrders have matching lengths")
    void shouldNotThrowWhenLengthsMatch() {
      assertThatCode(() ->
        ApiHelper.validateSortParameters(List.of("name", "age", "sex"), List.of(1, -1, 1))
      ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should not throw with single field and single order")
    void shouldNotThrowWithSingleFieldAndOrder() {
      assertThatCode(() -> ApiHelper.validateSortParameters(List.of("name"), List.of(1))
      ).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("createSort")
  class CreateSort {

    @Test
    @DisplayName("should create ascending sort for positive order")
    void shouldCreateAscendingSortForPositiveOrder() {
      Sort result = ApiHelper.createSort(List.of("name"), List.of(1));

      assertThat(result.isSorted()).isTrue();
      Sort.Order order = result.getOrderFor("name");
      assertThat(order).isNotNull();
      assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("should create descending sort for negative order")
    void shouldCreateDescendingSortForNegativeOrder() {
      Sort result = ApiHelper.createSort(List.of("name"), List.of(-1));

      assertThat(result.isSorted()).isTrue();
      Sort.Order order = result.getOrderFor("name");
      assertThat(order).isNotNull();
      assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("should create ascending sort for zero order")
    void shouldCreateAscendingSortForZeroOrder() {
      Sort result = ApiHelper.createSort(List.of("name"), List.of(0));

      assertThat(result.isSorted()).isTrue();
      Sort.Order order = result.getOrderFor("name");
      assertThat(order).isNotNull();
      assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("should create ascending sort for null order value")
    void shouldCreateAscendingSortForNullOrderValue() {
      List<Integer> orders = new java.util.ArrayList<>();
      orders.add(null);
      Sort result = ApiHelper.createSort(List.of("name"), orders);

      assertThat(result.isSorted()).isTrue();
      Sort.Order order = result.getOrderFor("name");
      assertThat(order).isNotNull();
      assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("should create multi-field sort with correct order")
    void shouldCreateMultiFieldSortWithCorrectOrder() {
      Sort result = ApiHelper.createSort(List.of("name", "age", "sex"), List.of(1, -1, 1));

      assertThat(result.isSorted()).isTrue();

      Sort.Order nameOrder = result.getOrderFor("name");
      assertThat(nameOrder).isNotNull();
      assertThat(nameOrder.getDirection()).isEqualTo(Sort.Direction.ASC);

      Sort.Order ageOrder = result.getOrderFor("age");
      assertThat(ageOrder).isNotNull();
      assertThat(ageOrder.getDirection()).isEqualTo(Sort.Direction.DESC);

      Sort.Order sexOrder = result.getOrderFor("sex");
      assertThat(sexOrder).isNotNull();
      assertThat(sexOrder.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("should preserve field order in multi-field sort")
    void shouldPreserveFieldOrderInMultiFieldSort() {
      Sort result = ApiHelper.createSort(List.of("age", "name"), List.of(-1, 1));

      List<Sort.Order> orders = result.toList();
      assertThat(orders).hasSize(2);
      assertThat(orders.get(0).getProperty()).isEqualTo("age");
      assertThat(orders.get(1).getProperty()).isEqualTo("name");
    }

    @Test
    @DisplayName("should handle large positive order as ascending")
    void shouldHandleLargePositiveOrderAsAscending() {
      Sort result = ApiHelper.createSort(List.of("name"), List.of(100));

      Sort.Order order = result.getOrderFor("name");
      assertThat(order).isNotNull();
      assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("should handle large negative order as descending")
    void shouldHandleLargeNegativeOrderAsDescending() {
      Sort result = ApiHelper.createSort(List.of("name"), List.of(-100));

      Sort.Order order = result.getOrderFor("name");
      assertThat(order).isNotNull();
      assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("should throw when sortFields and sortOrders have mismatched lengths")
    void shouldThrowWhenLengthsMismatch() {
      assertThatThrownBy(() -> ApiHelper.createSort(List.of("name", "age"), List.of(1)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("sortFields and sortOrders must have the same number of elements");
    }
  }

  @Nested
  @DisplayName("buildCacheKey")
  class BuildCacheKey {

    @Test
    @DisplayName("should append toString of filterType so any enum or string works as input")
    void shouldAppendToStringOfFilterType() {
      String key = ApiHelper.buildCacheKey("scope", SampleEnum.INCLUDE, List.of("a", "b"));

      assertThat(key).isEqualTo("scope-include-[a, b]");
    }

    @Test
    @DisplayName("should use literal 'null' when filterType is null")
    void shouldUseLiteralNullWhenFilterTypeIsNull() {
      String key = ApiHelper.buildCacheKey("scope", null, List.of("a"));

      assertThat(key).isEqualTo("scope-null-[a]");
    }

    @Test
    @DisplayName("should append extra parts using Objects.toString")
    void shouldAppendExtraParts() {
      String key = ApiHelper.buildCacheKey("scope", SampleEnum.EXCLUDE, List.of("a"), 1, null, "x");

      assertThat(key).isEqualTo("scope-exclude-[a]-1-null-x");
    }

    @Test
    @DisplayName("should serialize empty items list to []")
    void shouldSerializeEmptyItemsList() {
      String key = ApiHelper.buildCacheKey("scope", SampleEnum.INCLUDE, List.of());

      assertThat(key).isEqualTo("scope-include-[]");
    }

    /**
     * Mirrors the shape of OpenAPI-generated filter-type enums (lowercase {@code value}
     * exposed by both {@code getValue()} and {@code toString()}). Confirms that swapping
     * the original {@code ItemFilterTypeQueryDto} parameter for {@code Object} produces
     * byte-identical cache keys.
     */
    private enum SampleEnum {
      INCLUDE("include"),
      EXCLUDE("exclude");

      private final String value;

      SampleEnum(String value) {
        this.value = value;
      }

      @Override
      public String toString() {
        return String.valueOf(value);
      }
    }
  }

  @Nested
  @DisplayName("buildEmptyFlagFields")
  class BuildEmptyFlagFields {

    @Test
    @DisplayName("should return null when sort is unsorted")
    void shouldReturnNullWhenSortIsUnsorted() {
      assertThat(ApiHelper.buildEmptyFlagFields(Sort.unsorted())).isNull();
    }

    @Test
    @DisplayName(
      "should emit _isEmpty flag covering null, empty string, and empty array when sort has one field"
    )
    void shouldEmitIsEmptyFlagWhenSortHasOneField() {
      AggregationOperation op = ApiHelper.buildEmptyFlagFields(Sort.by(Sort.Order.asc("name")));

      assertThat(op).isNotNull();
      String doc = op.toPipelineStages(Aggregation.DEFAULT_CONTEXT).get(0).toJson();
      assertThat(doc)
        .contains("name_isEmpty")
        .contains("$or")
        .contains("$eq")
        .contains("$isArray") // empty-array branch
        .contains("$size");
    }

    @Test
    @DisplayName("should emit _isEmpty flags for all fields when sort has multiple fields")
    void shouldEmitIsEmptyFlagsWhenSortHasMultipleFields() {
      AggregationOperation op = ApiHelper.buildEmptyFlagFields(
        Sort.by(Sort.Order.asc("name"), Sort.Order.desc("age"))
      );

      assertThat(op).isNotNull();
      String doc = op.toPipelineStages(Aggregation.DEFAULT_CONTEXT).get(0).toJson();
      assertThat(doc).contains("name_isEmpty").contains("age_isEmpty");
    }

    @Test
    @DisplayName("should produce the same _isEmpty expression when sort direction differs")
    void shouldProduceSameExpressionWhenSortDirectionDiffers() {
      AggregationOperation asc = ApiHelper.buildEmptyFlagFields(Sort.by(Sort.Order.asc("name")));
      AggregationOperation desc = ApiHelper.buildEmptyFlagFields(Sort.by(Sort.Order.desc("name")));

      assertThat(asc).isNotNull();
      assertThat(desc).isNotNull();
      assertThat(asc.toPipelineStages(Aggregation.DEFAULT_CONTEXT).get(0).toJson()).isEqualTo(
        desc.toPipelineStages(Aggregation.DEFAULT_CONTEXT).get(0).toJson()
      );
    }

    @Test
    @DisplayName("should throw when a spaced alias resolves to a path with more than one dot")
    void shouldThrowWhenSpacedAliasHasMoreThanOneDot() {
      Map<String, String> aliases = Map.of("4 months", "4 months.nested.value");
      assertThatThrownBy(() ->
        ApiHelper.buildEmptyFlagFields(Sort.by(Sort.Order.asc("4 months")), aliases)
      )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("4 months.nested.value")
        .hasMessageContaining("one level of nesting");
    }

    @Test
    @DisplayName("should replace dots with underscores in isEmpty flag key for dotted sort fields")
    void shouldReplaceDotsWithUnderscoresInIsEmptyFlagKeyForDottedSortFields() {
      AggregationOperation op = ApiHelper.buildEmptyFlagFields(
        Sort.by(Sort.Order.asc("name.link_text"))
      );

      assertThat(op).isNotNull();
      String doc = op.toPipelineStages(Aggregation.DEFAULT_CONTEXT).get(0).toJson();
      assertThat(doc)
        .as("dots in sort field name should be replaced with underscores in flag key")
        .contains("name_link_text_isEmpty")
        .doesNotContain("name.link_text_isEmpty");
    }

    @Test
    @DisplayName("should use aliased path in isEmpty expression when aliases map is provided")
    void shouldUseAliasedPathInIsEmptyExpressionWhenAliasesMapIsProvided() {
      Map<String, String> aliases = Map.of("4 months", "4 months.log2_fc");
      AggregationOperation op = ApiHelper.buildEmptyFlagFields(
        Sort.by(Sort.Order.asc("4 months")),
        aliases
      );

      assertThat(op).isNotNull();
      String doc = op.toPipelineStages(Aggregation.DEFAULT_CONTEXT).get(0).toJson();
      assertThat(doc)
        .as("flag name should use the sort key with spaces replaced by underscores")
        .contains("4_months_isEmpty");
      assertThat(doc)
        .as("isEmpty expression should use $let to bind $$val to the $getField access expression")
        .contains("$let");
      assertThat(doc)
        .as("isEmpty expression should use $getField to navigate the spaced parent field")
        .contains("$getField");
      assertThat(doc)
        .as("isEmpty expression should reference log2_fc as the child field")
        .contains("log2_fc");
      assertThat(doc)
        .as(
          "isEmpty expression should use $type for null/missing and cover empty string and empty array"
        )
        .contains("$type")
        .contains("missing")
        .contains("$isArray")
        .contains("$size");
    }
  }

  @Nested
  @DisplayName("splitSearchTerms")
  class SplitSearchTerms {

    @Test
    @DisplayName("should return the single term when search has no comma")
    void shouldReturnSingleTermWhenSearchHasNoComma() {
      assertThat(ApiHelper.splitSearchTerms("APOE")).containsExactly("APOE");
    }

    @Test
    @DisplayName("should trim each term when terms have surrounding whitespace")
    void shouldTrimEachTermWhenTermsHaveSurroundingWhitespace() {
      assertThat(ApiHelper.splitSearchTerms(" APOE , TREM2 ")).containsExactly("APOE", "TREM2");
    }

    @Test
    @DisplayName("should drop blank terms when search has empty segments")
    void shouldDropBlankTermsWhenSearchHasEmptySegments() {
      assertThat(ApiHelper.splitSearchTerms("APOE,, ,TREM2")).containsExactly("APOE", "TREM2");
    }

    @Test
    @DisplayName("should return empty list when search contains only commas")
    void shouldReturnEmptyListWhenSearchContainsOnlyCommas() {
      assertThat(ApiHelper.splitSearchTerms(",,")).isEmpty();
    }
  }

  @Nested
  @DisplayName("createCaseInsensitiveFullMatchPatterns")
  class CreateCaseInsensitiveFullMatchPatterns {

    @Test
    @DisplayName("should anchor and quote each term when given a comma-separated string")
    void shouldAnchorAndQuoteEachTermWhenGivenCommaSeparatedString() {
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns("APOE, TREM2");

      assertThat(patterns)
        .extracting(Pattern::pattern)
        .containsExactly("^\\QAPOE\\E$", "^\\QTREM2\\E$");
      assertThat(patterns).allSatisfy(pattern ->
        assertThat(pattern.flags() & Pattern.CASE_INSENSITIVE).isNotZero()
      );
    }

    @Test
    @DisplayName("should match the full term regardless of case when the pattern is applied")
    void shouldMatchFullTermRegardlessOfCaseWhenPatternIsApplied() {
      Pattern pattern = ApiHelper.createCaseInsensitiveFullMatchPatterns(List.of("APOE")).get(0);

      assertThat(pattern.matcher("apoe").matches()).isTrue();
      assertThat(pattern.matcher("APOE4").matches()).isFalse();
    }

    @Test
    @DisplayName("should quote regex metacharacters when a term contains them")
    void shouldQuoteRegexMetacharactersWhenTermContainsThem() {
      Pattern pattern = ApiHelper.createCaseInsensitiveFullMatchPatterns(
        List.of("Abca7*V1599M")
      ).get(0);

      assertThat(pattern.matcher("Abca7*V1599M").matches()).isTrue();
      assertThat(pattern.matcher("Abca77V1599M").matches()).isFalse();
    }

    @Test
    @DisplayName("should produce the same patterns for both overloads when terms are equivalent")
    void shouldProduceSamePatternsForBothOverloadsWhenTermsAreEquivalent() {
      List<String> fromString = ApiHelper.createCaseInsensitiveFullMatchPatterns(" APOE , TREM2 ")
        .stream()
        .map(Pattern::pattern)
        .toList();
      List<String> fromList = ApiHelper.createCaseInsensitiveFullMatchPatterns(
        List.of("APOE", "TREM2")
      )
        .stream()
        .map(Pattern::pattern)
        .toList();

      assertThat(fromString).isEqualTo(fromList);
    }

    @Test
    @DisplayName("should return empty list when the string contains only commas")
    void shouldReturnEmptyListWhenStringContainsOnlyCommas() {
      assertThat(ApiHelper.createCaseInsensitiveFullMatchPatterns(",,")).isEmpty();
    }
  }

  @Nested
  @DisplayName("equalsAnyIgnoringCase")
  class EqualsAnyIgnoringCase {

    @Test
    @DisplayName("should match the field against a full-match pattern for every term")
    void shouldMatchFieldAgainstFullMatchPatternForEveryTerm() {
      Criteria criteria = ApiHelper.equalsAnyIgnoringCase("gene_symbol", List.of("APOE", "TREM2"));

      List<Pattern> patterns = (List<Pattern>) criteria
        .getCriteriaObject()
        .get("gene_symbol", Document.class)
        .get("$in");
      assertThat(patterns)
        .extracting(Pattern::pattern)
        .containsExactly("^\\QAPOE\\E$", "^\\QTREM2\\E$");
      assertThat(patterns).allSatisfy(pattern ->
        assertThat(pattern.flags() & Pattern.CASE_INSENSITIVE).isNotZero()
      );
    }

    @Test
    @DisplayName("should match nothing when the term list is empty")
    void shouldMatchNothingWhenTermListIsEmpty() {
      Criteria criteria = ApiHelper.equalsAnyIgnoringCase("gene_symbol", List.of());

      assertThat(criteria.getCriteriaObject()).isEqualTo(
        new Document("gene_symbol", new Document("$in", List.of()))
      );
    }
  }
}
