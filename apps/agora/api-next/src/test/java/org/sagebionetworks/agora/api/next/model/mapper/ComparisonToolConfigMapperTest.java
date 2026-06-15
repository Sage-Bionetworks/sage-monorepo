package org.sagebionetworks.agora.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.agora.api.next.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.agora.api.next.model.dto.ComparisonToolConfigColumnDto;
import org.sagebionetworks.agora.api.next.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.agora.api.next.model.dto.ComparisonToolConfigPageDto;

class ComparisonToolConfigMapperTest {

  private ComparisonToolConfigMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ComparisonToolConfigMapper();
  }

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    // when
    ComparisonToolConfigDto result = mapper.toDto(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("should map basic document fields to dto")
  void shouldMapBasicDocumentFieldsToDto() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setId(new ObjectId());
    document.setPage("Nominated Targets");
    document.setDropdowns(List.of("dropdown1", "dropdown2"));
    document.setRowCount("100");
    document.setColumns(List.of());
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getPage()).isEqualTo(ComparisonToolConfigPageDto.NOMINATED_TARGETS);
    assertThat(result.getDropdowns()).containsExactly("dropdown1", "dropdown2");
    assertThat(result.getRowCount()).isEqualTo("100");
    assertThat(result.getColumns()).isEmpty();
    assertThat(result.getFilters()).isEmpty();
  }

  @Test
  @DisplayName("should map columns with optional fields including column width")
  void shouldMapColumnsWithOptionalFieldsIncludingColumnWidth() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .name("Nominating Teams")
        .type("text")
        .dataKey("nominating_teams")
        .sortTooltip("Sort by Nominating Teams value")
        .isExported(true)
        .isHidden(false)
        .columnWidth(300)
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Nominated Targets");
    document.setColumns(List.of(column));
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).hasSize(1);
    ComparisonToolConfigColumnDto columnDto = result.getColumns().get(0);
    assertThat(columnDto.getName()).isEqualTo("Nominating Teams");
    assertThat(columnDto.getType()).isEqualTo(ComparisonToolConfigColumnDto.TypeEnum.TEXT);
    assertThat(columnDto.getDataKey()).isEqualTo("nominating_teams");
    assertThat(columnDto.getSortTooltip()).isEqualTo("Sort by Nominating Teams value");
    assertThat(columnDto.getIsExported()).isTrue();
    assertThat(columnDto.getIsHidden()).isFalse();
    assertThat(columnDto.getColumnWidth()).isEqualTo(300);
  }

  @Test
  @DisplayName("should map column with null column width")
  void shouldMapColumnWithNullColumnWidth() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .type("text")
        .dataKey("nominating_teams")
        .isExported(true)
        .isHidden(false)
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Nominated Drugs");
    document.setColumns(List.of(column));
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).hasSize(1);
    assertThat(result.getColumns().get(0).getColumnWidth()).isNull();
  }
}
