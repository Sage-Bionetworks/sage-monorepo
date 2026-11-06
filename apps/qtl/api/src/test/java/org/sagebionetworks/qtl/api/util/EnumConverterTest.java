package org.sagebionetworks.qtl.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigColumnDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolPageDto;

class EnumConverterTest {

  @Test
  @DisplayName("should convert valid string to enum")
  void shouldConvertValidStringToEnum() {
    ComparisonToolConfigColumnDto.TypeEnum result = EnumConverter.toEnum(
      "numeric",
      ComparisonToolConfigColumnDto.TypeEnum::fromValue,
      "column type",
      "test record"
    );

    assertThat(result).isEqualTo(ComparisonToolConfigColumnDto.TypeEnum.NUMERIC);
  }

  @Test
  @DisplayName("should throw exception when value is null")
  void shouldThrowExceptionWhenValueIsNull() {
    assertThatThrownBy(() ->
      EnumConverter.toEnum(
        null,
        ComparisonToolConfigColumnDto.TypeEnum::fromValue,
        "column type",
        "test record"
      )
    )
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing column type value in test record");
  }

  @Test
  @DisplayName("should throw exception when value is invalid")
  void shouldThrowExceptionWhenValueIsInvalid() {
    assertThatThrownBy(() ->
      EnumConverter.toEnum(
        "string",
        ComparisonToolConfigColumnDto.TypeEnum::fromValue,
        "column type",
        "test record"
      )
    )
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Unexpected column type value 'string' in test record")
      .hasCauseInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("should handle different enum types")
  void shouldHandleDifferentEnumTypes() {
    ComparisonToolPageDto result = EnumConverter.toEnum(
      "eQTLs",
      ComparisonToolPageDto::fromValue,
      "page",
      "test record"
    );

    assertThat(result).isEqualTo(ComparisonToolPageDto.E_QTLS);
  }
}
