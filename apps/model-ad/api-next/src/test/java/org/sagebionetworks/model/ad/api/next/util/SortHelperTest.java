package org.sagebionetworks.model.ad.api.next.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class SortHelperTest {

  @Nested
  @DisplayName("buildSort")
  class BuildSortTests {

    @Test
    @DisplayName("should build sort with ascending order")
    void shouldBuildSortWithAscending() {
      List<String> fields = Arrays.asList("name", "age");
      List<Integer> orders = Arrays.asList(1, 1);

      Sort result = SortHelper.buildSort(fields, orders, SortHelper.NO_TRANSFORM);

      assertThat(result.isSorted()).isTrue();
      assertThat(result.getOrderFor("name")).isNotNull();
      assertThat(result.getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
      assertThat(result.getOrderFor("age")).isNotNull();
      assertThat(result.getOrderFor("age").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("should build sort with descending order")
    void shouldBuildSortWithDescending() {
      List<String> fields = Arrays.asList("name");
      List<Integer> orders = Arrays.asList(-1);

      Sort result = SortHelper.buildSort(fields, orders, SortHelper.NO_TRANSFORM);

      assertThat(result.getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("should build sort with mixed ascending and descending")
    void shouldBuildSortWithMixedOrders() {
      List<String> fields = Arrays.asList("name", "age", "sex");
      List<Integer> orders = Arrays.asList(1, -1, 1);

      Sort result = SortHelper.buildSort(fields, orders, SortHelper.NO_TRANSFORM);

      assertThat(result.getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
      assertThat(result.getOrderFor("age").getDirection()).isEqualTo(Sort.Direction.DESC);
      assertThat(result.getOrderFor("sex").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("should apply nullsLast to all orders")
    void shouldApplyNullsLast() {
      List<String> fields = Arrays.asList("name");
      List<Integer> orders = Arrays.asList(1);

      Sort result = SortHelper.buildSort(fields, orders, SortHelper.NO_TRANSFORM);

      assertThat(result.getOrderFor("name").getNullHandling()).isEqualTo(
        Sort.NullHandling.NULLS_LAST
      );
    }

    @Test
    @DisplayName("should return unsorted when fields are null")
    void shouldReturnUnsortedWhenFieldsNull() {
      Sort result = SortHelper.buildSort(null, Arrays.asList(1), SortHelper.NO_TRANSFORM);

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should return unsorted when fields are empty")
    void shouldReturnUnsortedWhenFieldsEmpty() {
      Sort result = SortHelper.buildSort(
        Collections.emptyList(),
        Collections.emptyList(),
        SortHelper.NO_TRANSFORM
      );

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should return unsorted when orders are null")
    void shouldReturnUnsortedWhenOrdersNull() {
      Sort result = SortHelper.buildSort(Arrays.asList("name"), null, SortHelper.NO_TRANSFORM);

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should return unsorted when orders are empty")
    void shouldReturnUnsortedWhenOrdersEmpty() {
      Sort result = SortHelper.buildSort(
        Arrays.asList("name"),
        Collections.emptyList(),
        SortHelper.NO_TRANSFORM
      );

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should throw exception when arrays have mismatched lengths")
    void shouldThrowOnMismatchedLengths() {
      List<String> fields = Arrays.asList("name", "age");
      List<Integer> orders = Arrays.asList(1);

      assertThatThrownBy(() -> SortHelper.buildSort(fields, orders, SortHelper.NO_TRANSFORM))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("sortFields and sortOrders must have the same length");
    }

    @Test
    @DisplayName("should throw exception when order is not 1 or -1")
    void shouldThrowOnInvalidOrder() {
      List<String> fields = Arrays.asList("name");
      List<Integer> orders = Arrays.asList(2);

      assertThatThrownBy(() -> SortHelper.buildSort(fields, orders, SortHelper.NO_TRANSFORM))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("sortOrders must contain only 1 (ascending) or -1 (descending)");
    }

    @Test
    @DisplayName("should throw exception when order is 0")
    void shouldThrowWhenOrderIsZero() {
      List<String> fields = Arrays.asList("name");
      List<Integer> orders = Arrays.asList(0);

      assertThatThrownBy(() -> SortHelper.buildSort(fields, orders, SortHelper.NO_TRANSFORM))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Got: 0");
    }

    @Test
    @DisplayName("should apply field transformer to each field")
    void shouldApplyFieldTransformer() {
      List<String> fields = Arrays.asList("name", "age");
      List<Integer> orders = Arrays.asList(1, -1);

      SortHelper.FieldTransformer transformer = field -> field + "_transformed";

      Sort result = SortHelper.buildSort(fields, orders, transformer);

      assertThat(result.getOrderFor("name_transformed")).isNotNull();
      assertThat(result.getOrderFor("age_transformed")).isNotNull();
      assertThat(result.getOrderFor("name")).isNull();
      assertThat(result.getOrderFor("age")).isNull();
    }
  }

  @Nested
  @DisplayName("Gene Expression Transformer")
  class GeneExpressionTransformerTests {

    @Test
    @DisplayName("should append .log2_fc to time period fields")
    void shouldTransformTimePeriodFields() {
      String result = SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("4 months");

      assertThat(result).isEqualTo("4 months.log2_fc");
    }

    @Test
    @DisplayName("should handle single month time periods")
    void shouldHandleSingleMonth() {
      String result = SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("1 month");

      assertThat(result).isEqualTo("1 month.log2_fc");
    }

    @Test
    @DisplayName("should handle multiple digit months")
    void shouldHandleMultipleDigitMonths() {
      String result = SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("12 months");

      assertThat(result).isEqualTo("12 months.log2_fc");
    }

    @Test
    @DisplayName("should not transform non-time-period fields")
    void shouldNotTransformNonTimePeriodFields() {
      assertThat(SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("gene_symbol")).isEqualTo(
        "gene_symbol"
      );
      assertThat(SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("name")).isEqualTo("name");
      assertThat(SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("tissue")).isEqualTo("tissue");
    }

    @Test
    @DisplayName("should not transform fields with months but wrong format")
    void shouldNotTransformWrongFormat() {
      assertThat(SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("months4")).isEqualTo("months4");
      assertThat(SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("4months")).isEqualTo("4months");
      assertThat(SortHelper.GENE_EXPRESSION_TRANSFORMER.transform("month 4")).isEqualTo("month 4");
    }

    @Test
    @DisplayName("should integrate with buildSort for gene expression sorting")
    void shouldIntegrateWithBuildSort() {
      List<String> fields = Arrays.asList("gene_symbol", "4 months", "name");
      List<Integer> orders = Arrays.asList(1, -1, 1);

      Sort result = SortHelper.buildSort(fields, orders, SortHelper.GENE_EXPRESSION_TRANSFORMER);

      assertThat(result.getOrderFor("gene_symbol")).isNotNull();
      assertThat(result.getOrderFor("4 months.log2_fc")).isNotNull();
      assertThat(result.getOrderFor("name")).isNotNull();
    }
  }

  @Nested
  @DisplayName("Disease Correlation Transformer")
  class DiseaseCorrelationTransformerTests {

    @Test
    @DisplayName("should append .correlation to brain region fields")
    void shouldTransformBrainRegionFields() {
      String[] brainRegions = { "CBE", "DLPFC", "FP", "IFG", "PHG", "STG", "TCX" };

      for (String region : brainRegions) {
        String result = SortHelper.DISEASE_CORRELATION_TRANSFORMER.transform(region);
        assertThat(result).isEqualTo(region + ".correlation");
      }
    }

    @Test
    @DisplayName("should not transform non-brain-region fields")
    void shouldNotTransformNonBrainRegionFields() {
      assertThat(SortHelper.DISEASE_CORRELATION_TRANSFORMER.transform("name")).isEqualTo("name");
      assertThat(SortHelper.DISEASE_CORRELATION_TRANSFORMER.transform("age")).isEqualTo("age");
      assertThat(SortHelper.DISEASE_CORRELATION_TRANSFORMER.transform("sex")).isEqualTo("sex");
    }

    @Test
    @DisplayName("should not transform lowercase brain regions")
    void shouldNotTransformLowercaseBrainRegions() {
      assertThat(SortHelper.DISEASE_CORRELATION_TRANSFORMER.transform("phg")).isEqualTo("phg");
      assertThat(SortHelper.DISEASE_CORRELATION_TRANSFORMER.transform("dlpfc")).isEqualTo("dlpfc");
    }

    @Test
    @DisplayName("should not transform partial matches")
    void shouldNotTransformPartialMatches() {
      assertThat(SortHelper.DISEASE_CORRELATION_TRANSFORMER.transform("PHGA")).isEqualTo("PHGA");
      assertThat(SortHelper.DISEASE_CORRELATION_TRANSFORMER.transform("APHG")).isEqualTo("APHG");
    }

    @Test
    @DisplayName("should integrate with buildSort for disease correlation sorting")
    void shouldIntegrateWithBuildSort() {
      List<String> fields = Arrays.asList("name", "PHG", "age");
      List<Integer> orders = Arrays.asList(1, -1, 1);

      Sort result = SortHelper.buildSort(
        fields,
        orders,
        SortHelper.DISEASE_CORRELATION_TRANSFORMER
      );

      assertThat(result.getOrderFor("name")).isNotNull();
      assertThat(result.getOrderFor("PHG.correlation")).isNotNull();
      assertThat(result.getOrderFor("age")).isNotNull();
    }
  }

  @Nested
  @DisplayName("No Transform")
  class NoTransformTests {

    @Test
    @DisplayName("should return field unchanged")
    void shouldReturnFieldUnchanged() {
      assertThat(SortHelper.NO_TRANSFORM.transform("any_field")).isEqualTo("any_field");
      assertThat(SortHelper.NO_TRANSFORM.transform("4 months")).isEqualTo("4 months");
      assertThat(SortHelper.NO_TRANSFORM.transform("PHG")).isEqualTo("PHG");
    }
  }
}
