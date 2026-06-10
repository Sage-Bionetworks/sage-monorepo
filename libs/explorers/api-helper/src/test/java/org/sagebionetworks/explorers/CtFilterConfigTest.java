package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CtFilterConfigTest {

  @Test
  @DisplayName("should throw IllegalStateException when itemFilter is not set")
  void shouldThrowWhenItemFilterNotSet() {
    assertThatThrownBy(() ->
      CtFilterConfig.builder()
        .searchFilter("name")
        .build()
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("itemFilter must be set");
  }

  @Test
  @DisplayName("should throw IllegalStateException when searchFilter is not set")
  void shouldThrowWhenSearchFilterNotSet() {
    assertThatThrownBy(() ->
      CtFilterConfig.builder()
        .simpleItemFilter("id")
        .build()
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("searchFilter must be set");
  }
}
