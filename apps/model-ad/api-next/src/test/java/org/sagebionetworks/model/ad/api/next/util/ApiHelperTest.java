package org.sagebionetworks.model.ad.api.next.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

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
    @DisplayName("should return unsorted when sortFields is null")
    void shouldReturnUnsortedWhenSortFieldsNull() {
      Sort result = ApiHelper.createSort(null, List.of(1));

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should return unsorted when sortFields is empty")
    void shouldReturnUnsortedWhenSortFieldsEmpty() {
      Sort result = ApiHelper.createSort(List.of(), List.of(1));

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should return unsorted when sortOrders is null")
    void shouldReturnUnsortedWhenSortOrdersNull() {
      Sort result = ApiHelper.createSort(List.of("name"), null);

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should return unsorted when sortOrders is empty")
    void shouldReturnUnsortedWhenSortOrdersEmpty() {
      Sort result = ApiHelper.createSort(List.of("name"), List.of());

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should return unsorted when both are null")
    void shouldReturnUnsortedWhenBothNull() {
      Sort result = ApiHelper.createSort(null, null);

      assertThat(result.isUnsorted()).isTrue();
    }

    @Test
    @DisplayName("should return unsorted when both are empty")
    void shouldReturnUnsortedWhenBothEmpty() {
      Sort result = ApiHelper.createSort(List.of(), List.of());

      assertThat(result.isUnsorted()).isTrue();
    }

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
  }
}
