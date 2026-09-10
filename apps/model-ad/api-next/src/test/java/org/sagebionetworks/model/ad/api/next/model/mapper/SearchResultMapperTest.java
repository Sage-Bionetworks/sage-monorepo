package org.sagebionetworks.model.ad.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.document.SearchResultDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SearchResultDto;

class SearchResultMapperTest {

  private SearchResultMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new SearchResultMapper();
  }

  @Test
  @DisplayName("should map document fields to dto when organism is mouse")
  void shouldMapDocumentFieldsToDtoWhenOrganismIsMouse() {
    // given
    SearchResultDocument document = buildDocument(
      "Abca7*V1599M",
      "jax_id",
      "34233",
      ModelOrganismDto.MOUSE
    );

    // when
    SearchResultDto dto = mapper.toDto(document);

    // then
    assertThat(dto.getId()).isEqualTo("Abca7*V1599M");
    assertThat(dto.getMatchField()).isEqualTo("jax_id");
    assertThat(dto.getMatchValue()).isEqualTo("34233");
    assertThat(dto.getModelOrganism()).isEqualTo(ModelOrganismDto.MOUSE);
  }

  @Test
  @DisplayName("should map organism string to enum when organism is marmoset")
  void shouldMapOrganismStringToEnumWhenOrganismIsMarmoset() {
    // given
    SearchResultDocument document = buildDocument(
      "Marmoset1",
      "name",
      "Marmoset1",
      ModelOrganismDto.MARMOSET
    );

    // when
    SearchResultDto dto = mapper.toDto(document);

    // then
    assertThat(dto.getModelOrganism()).isEqualTo(ModelOrganismDto.MARMOSET);
  }

  private SearchResultDocument buildDocument(
    String id,
    String matchField,
    String matchValue,
    ModelOrganismDto organism
  ) {
    SearchResultDocument document = new SearchResultDocument();
    document.setId(id);
    document.setMatchField(matchField);
    document.setMatchValue(matchValue);
    document.setModelOrganism(organism.getValue());
    document.setPrecedence(3);
    return document;
  }
}
