package org.sagebionetworks.model.ad.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.document.FoldChangeResult;
import org.sagebionetworks.model.ad.api.next.model.dto.FoldChangeResultDto;

class FoldChangeMapperTest {

  private FoldChangeMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new FoldChangeMapper();
  }

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    assertThat(mapper.toNullableDto(null)).isNull();
  }

  @Test
  @DisplayName("should convert values to big decimal when both are present")
  void shouldConvertValuesToBigDecimalWhenBothArePresent() {
    FoldChangeResult document = FoldChangeResult.builder()
      .log2Fc(0.01167d)
      .adjPVal(0.7812d)
      .build();

    FoldChangeResultDto dto = mapper.toNullableDto(document);

    assertThat(dto).isNotNull();
    assertThat(dto.getLog2Fc()).isEqualTo(BigDecimal.valueOf(0.01167d));
    assertThat(dto.getAdjPVal()).isEqualTo(BigDecimal.valueOf(0.7812d));
  }

  @Test
  @DisplayName("should preserve negative fold change values")
  void shouldPreserveNegativeFoldChangeValues() {
    FoldChangeResult document = FoldChangeResult.builder().log2Fc(-2.5d).adjPVal(0.0001d).build();

    FoldChangeResultDto dto = mapper.toNullableDto(document);

    assertThat(dto).isNotNull();
    assertThat(dto.getLog2Fc()).isEqualTo(BigDecimal.valueOf(-2.5d));
  }

  @Test
  @DisplayName("should return null when log2 fold change is missing")
  void shouldReturnNullWhenLog2FoldChangeIsMissing() {
    FoldChangeResult document = FoldChangeResult.builder().adjPVal(0.7812d).build();

    assertThat(mapper.toNullableDto(document)).isNull();
  }

  @Test
  @DisplayName("should return null when adjusted p-value is missing")
  void shouldReturnNullWhenAdjustedPvalueIsMissing() {
    FoldChangeResult document = FoldChangeResult.builder().log2Fc(0.5d).build();

    assertThat(mapper.toNullableDto(document)).isNull();
  }
}
