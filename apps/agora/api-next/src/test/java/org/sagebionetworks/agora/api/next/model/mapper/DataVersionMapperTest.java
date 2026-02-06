package org.sagebionetworks.agora.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.agora.api.next.model.document.DataVersionDocument;
import org.sagebionetworks.agora.api.next.model.dto.DataVersionDto;

class DataVersionMapperTest {

  private DataVersionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new DataVersionMapper();
  }

  @Test
  @DisplayName("should map document to dto when all fields are present")
  void shouldMapDocumentToDtoWhenAllFieldsArePresent() {
    // given
    DataVersionDocument document = new DataVersionDocument();
    document.setId(new ObjectId());
    document.setDataFile("syn000456");
    document.setDataVersion("2.5.0");
    document.setTeamImagesId("syn987654");

    // when
    DataVersionDto result = mapper.toDto(document);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDataFile()).isEqualTo("syn000456");
    assertThat(result.getDataVersion()).isEqualTo("2.5.0");
    assertThat(result.getTeamImagesId()).isEqualTo("syn987654");
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
  @DisplayName("should map document with null fields to dto")
  void shouldMapDocumentWithNullFieldsToDto() {
    // given
    DataVersionDocument document = new DataVersionDocument();
    document.setId(new ObjectId());
    document.setDataFile(null);
    document.setDataVersion(null);
    document.setTeamImagesId(null);

    // when
    DataVersionDto result = mapper.toDto(document);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDataFile()).isNull();
    assertThat(result.getDataVersion()).isNull();
    assertThat(result.getTeamImagesId()).isNull();
  }

  @Test
  @DisplayName("should map document with partial fields to dto")
  void shouldMapDocumentWithPartialFieldsToDto() {
    // given
    DataVersionDocument document = new DataVersionDocument();
    document.setId(new ObjectId());
    document.setDataFile("syn000456");
    document.setDataVersion("1.0.0");
    // teamImagesId is not set

    // when
    DataVersionDto result = mapper.toDto(document);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDataFile()).isEqualTo("syn000456");
    assertThat(result.getDataVersion()).isEqualTo("1.0.0");
    assertThat(result.getTeamImagesId()).isNull();
  }
}
