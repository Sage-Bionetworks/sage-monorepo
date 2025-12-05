package org.sagebionetworks.model.ad.api.next.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.DataIntegrityException;
import org.sagebionetworks.model.ad.api.next.model.dto.SexCohortDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;

class EnumConverterTest {

  @Test
  @DisplayName("should convert valid string to enum")
  void shouldConvertValidStringToEnum() {
    SexDto result = EnumConverter.toEnum("Female", SexDto::fromValue, "sex", "test record");

    assertThat(result).isEqualTo(SexDto.FEMALE);
  }

  @Test
  @DisplayName("should throw exception when value is null")
  void shouldThrowExceptionWhenValueIsNull() {
    assertThatThrownBy(() -> EnumConverter.toEnum(null, SexDto::fromValue, "sex", "test record"))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing sex value in test record");
  }

  @Test
  @DisplayName("should throw exception when value is invalid")
  void shouldThrowExceptionWhenValueIsInvalid() {
    assertThatThrownBy(() ->
      EnumConverter.toEnum("Invalid", SexDto::fromValue, "sex", "test record")
    )
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Unexpected sex value 'Invalid' in test record")
      .hasCauseInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("should handle different enum types")
  void shouldHandleDifferentEnumTypes() {
    // Using another enum type from the codebase
    var result = EnumConverter.toEnum(
      "include",
      org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto::fromValue,
      "filter type",
      "query"
    );

    assertThat(result).isEqualTo(
      org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto.INCLUDE
    );
  }

  @Test
  @DisplayName("should convert to SexDto using convenience method")
  void shouldConvertToSexDtoUsingConvenienceMethod() {
    SexDto result = EnumConverter.toSexDto("Male", "test record");

    assertThat(result).isEqualTo(SexDto.MALE);
  }

  @Test
  @DisplayName("should convert to SexCohortDto using convenience method")
  void shouldConvertToSexCohortDtoUsingConvenienceMethod() {
    SexCohortDto result = EnumConverter.toSexCohortDto("Females & Males", "test record");

    assertThat(result).isEqualTo(SexCohortDto.FEMALES_MALES);
  }

  @Test
  @DisplayName("should throw exception for null value in toSexDto")
  void shouldThrowExceptionForNullValueInToSexDto() {
    assertThatThrownBy(() -> EnumConverter.toSexDto(null, "test record"))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing sex value in test record");
  }
}
