package org.sagebionetworks.qtl.api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.qtl.api.model.document.DataVersionDocument;
import org.sagebionetworks.qtl.api.model.dto.DataVersionDto;

class DataVersionMapperTest {

  private DataVersionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new DataVersionMapper();
  }

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    // when
    DataVersionDto result = mapper.toDto(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("should map document fields to dto")
  void shouldMapDocumentFieldsToDto() {
    // given
    DataVersionDocument document = new DataVersionDocument();
    document.setId(new ObjectId());
    document.setDataFile("syn12345678");
    document.setDataVersion("42");

    // when
    DataVersionDto result = mapper.toDto(document);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDataFile()).isEqualTo("syn12345678");
    assertThat(result.getDataVersion()).isEqualTo("42");
  }
}
