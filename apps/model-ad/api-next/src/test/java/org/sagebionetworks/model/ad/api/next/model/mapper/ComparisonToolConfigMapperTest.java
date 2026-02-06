package org.sagebionetworks.model.ad.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigColumnDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigFilterDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolPageDto;

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
    document.setPage("Gene Expression");
    document.setDropdowns(List.of("dropdown1", "dropdown2"));
    document.setRowCount("100");
    document.setColumns(List.of());
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getPage()).isEqualTo(ComparisonToolPageDto.GENE_EXPRESSION);
    assertThat(result.getDropdowns()).containsExactly("dropdown1", "dropdown2");
    assertThat(result.getRowCount()).isEqualTo("100");
    assertThat(result.getColumns()).isEmpty();
    assertThat(result.getFilters()).isEmpty();
  }

  @Test
  @DisplayName("should map columns with all required fields")
  void shouldMapColumnsWithAllRequiredFields() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .type("text")
        .dataKey("geneSymbol")
        .isExported(true)
        .isHidden(false)
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Model Overview");
    document.setColumns(List.of(column));
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).hasSize(1);
    ComparisonToolConfigColumnDto columnDto = result.getColumns().get(0);
    assertThat(columnDto.getType()).isEqualTo(ComparisonToolConfigColumnDto.TypeEnum.TEXT);
    assertThat(columnDto.getDataKey()).isEqualTo("geneSymbol");
    assertThat(columnDto.getIsExported()).isTrue();
    assertThat(columnDto.getIsHidden()).isFalse();
  }

  @Test
  @DisplayName("should map columns with optional fields")
  void shouldMapColumnsWithOptionalFields() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .name("Gene Symbol")
        .type("link_internal")
        .dataKey("geneSymbol")
        .tooltip("Gene identifier")
        .sortTooltip("Sort by gene")
        .linkText("View details")
        .linkUrl("/genes/{id}")
        .isExported(true)
        .isHidden(false)
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Disease Correlation");
    document.setColumns(List.of(column));
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).hasSize(1);
    ComparisonToolConfigColumnDto columnDto = result.getColumns().get(0);
    assertThat(columnDto.getName()).isEqualTo("Gene Symbol");
    assertThat(columnDto.getType()).isEqualTo(ComparisonToolConfigColumnDto.TypeEnum.LINK_INTERNAL);
    assertThat(columnDto.getTooltip()).isEqualTo("Gene identifier");
    assertThat(columnDto.getSortTooltip()).isEqualTo("Sort by gene");
    assertThat(columnDto.getLinkText()).isEqualTo("View details");
    assertThat(columnDto.getLinkUrl()).isEqualTo("/genes/{id}");
  }

  @Test
  @DisplayName("should map filters with required fields")
  void shouldMapFiltersWithRequiredFields() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigFilter filter =
      ComparisonToolConfigDocument.ComparisonToolConfigFilter.builder()
        .name("Model Type")
        .dataKey("modelType")
        .queryParamKey("model_type")
        .values(List.of("5XFAD", "APOE4"))
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Gene Expression");
    document.setColumns(List.of());
    document.setFilters(List.of(filter));

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getFilters()).hasSize(1);
    ComparisonToolConfigFilterDto filterDto = result.getFilters().get(0);
    assertThat(filterDto.getName()).isEqualTo("Model Type");
    assertThat(filterDto.getDataKey()).isEqualTo("modelType");
    assertThat(filterDto.getQueryParamKey()).isEqualTo("model_type");
    assertThat(filterDto.getValues()).containsExactly("5XFAD", "APOE4");
  }

  @Test
  @DisplayName("should map filters with short name")
  void shouldMapFiltersWithShortName() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigFilter filter =
      ComparisonToolConfigDocument.ComparisonToolConfigFilter.builder()
        .name("Model Type")
        .dataKey("modelType")
        .shortName("Type")
        .queryParamKey("model_type")
        .values(List.of("5XFAD"))
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Model Overview");
    document.setColumns(List.of());
    document.setFilters(List.of(filter));

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getFilters()).hasSize(1);
    assertThat(result.getFilters().get(0).getShortName()).isEqualTo("Type");
  }

  @Test
  @DisplayName("should handle null columns list")
  void shouldHandleNullColumnsList() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Gene Expression");
    document.setColumns(null);
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).isEmpty();
  }

  @Test
  @DisplayName("should handle null filters list")
  void shouldHandleNullFiltersList() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Model Overview");
    document.setColumns(List.of());
    document.setFilters(null);

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getFilters()).isEmpty();
  }

  @Test
  @DisplayName("should map multiple columns and filters")
  void shouldMapMultipleColumnsAndFilters() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column1 =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .type("text")
        .dataKey("column1")
        .isExported(true)
        .isHidden(false)
        .build();

    ComparisonToolConfigDocument.ComparisonToolConfigColumn column2 =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .type("heat_map")
        .dataKey("column2")
        .isExported(false)
        .isHidden(true)
        .build();

    ComparisonToolConfigDocument.ComparisonToolConfigFilter filter1 =
      ComparisonToolConfigDocument.ComparisonToolConfigFilter.builder()
        .name("Filter 1")
        .dataKey("filter1")
        .queryParamKey("filter_1")
        .values(List.of("value1"))
        .build();

    ComparisonToolConfigDocument.ComparisonToolConfigFilter filter2 =
      ComparisonToolConfigDocument.ComparisonToolConfigFilter.builder()
        .name("Filter 2")
        .dataKey("filter2")
        .queryParamKey("filter_2")
        .values(List.of("value2"))
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Disease Correlation");
    document.setColumns(List.of(column1, column2));
    document.setFilters(List.of(filter1, filter2));

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).hasSize(2);
    assertThat(result.getFilters()).hasSize(2);
  }

  @Test
  @DisplayName("should convert page string to enum correctly")
  void shouldConvertPageStringToEnumCorrectly() {
    // given
    ComparisonToolConfigDocument doc1 = new ComparisonToolConfigDocument();
    doc1.setPage("Model Overview");
    doc1.setColumns(List.of());
    doc1.setFilters(List.of());

    ComparisonToolConfigDocument doc2 = new ComparisonToolConfigDocument();
    doc2.setPage("Gene Expression");
    doc2.setColumns(List.of());
    doc2.setFilters(List.of());

    ComparisonToolConfigDocument doc3 = new ComparisonToolConfigDocument();
    doc3.setPage("Disease Correlation");
    doc3.setColumns(List.of());
    doc3.setFilters(List.of());

    // when
    ComparisonToolConfigDto result1 = mapper.toDto(doc1);
    ComparisonToolConfigDto result2 = mapper.toDto(doc2);
    ComparisonToolConfigDto result3 = mapper.toDto(doc3);

    // then
    assertThat(result1.getPage()).isEqualTo(ComparisonToolPageDto.MODEL_OVERVIEW);
    assertThat(result2.getPage()).isEqualTo(ComparisonToolPageDto.GENE_EXPRESSION);
    assertThat(result3.getPage()).isEqualTo(ComparisonToolPageDto.DISEASE_CORRELATION);
  }
}
