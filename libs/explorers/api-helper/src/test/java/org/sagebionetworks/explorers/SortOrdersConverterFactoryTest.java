package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class SortOrdersConverterFactoryTest {

  @Test
  @DisplayName("should convert positive integer string to the corresponding enum constant")
  void shouldConvertPositiveIntegerString() {
    Converter<String, SampleSortOrdersEnum> converter = SortOrdersConverterFactory.from(
      SampleSortOrdersEnum::fromValue
    );

    assertThat(converter.convert("1")).isEqualTo(SampleSortOrdersEnum.ASC);
  }

  @Test
  @DisplayName("should convert negative integer string to the corresponding enum constant")
  void shouldConvertNegativeIntegerString() {
    Converter<String, SampleSortOrdersEnum> converter = SortOrdersConverterFactory.from(
      SampleSortOrdersEnum::fromValue
    );

    assertThat(converter.convert("-1")).isEqualTo(SampleSortOrdersEnum.DESC);
  }

  @Test
  @DisplayName("should throw NumberFormatException when input is not parseable as integer")
  void shouldThrowWhenInputIsNotInteger() {
    Converter<String, SampleSortOrdersEnum> converter = SortOrdersConverterFactory.from(
      SampleSortOrdersEnum::fromValue
    );

    assertThatThrownBy(() -> converter.convert("abc")).isInstanceOf(NumberFormatException.class);
  }

  @Test
  @DisplayName("should propagate IllegalArgumentException when integer is unknown to the enum")
  void shouldPropagateUnknownValueException() {
    Converter<String, SampleSortOrdersEnum> converter = SortOrdersConverterFactory.from(
      SampleSortOrdersEnum::fromValue
    );

    assertThatThrownBy(() -> converter.convert("99"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Unexpected value '99'");
  }

  /**
   * Mirrors the OpenAPI-generated {@code SortOrdersEnum} shape: an integer-valued enum exposing
   * a static {@code fromValue(Integer)} that throws {@link IllegalArgumentException} on unknown
   * inputs.
   */
  private enum SampleSortOrdersEnum {
    ASC(1),
    DESC(-1);

    private final Integer value;

    SampleSortOrdersEnum(Integer value) {
      this.value = value;
    }

    public static SampleSortOrdersEnum fromValue(Integer value) {
      for (SampleSortOrdersEnum e : values()) {
        if (e.value.equals(value)) {
          return e;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }
}
