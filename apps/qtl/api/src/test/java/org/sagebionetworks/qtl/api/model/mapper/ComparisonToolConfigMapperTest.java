package org.sagebionetworks.qtl.api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.sagebionetworks.qtl.api.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigColumnDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigFilterDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolPageDto;

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
    document.setPage("eQTLs");
    document.setDropdowns(List.of("dropdown1", "dropdown2"));
    document.setRowCount("100");
    document.setColumns(List.of());
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getPage()).isEqualTo(ComparisonToolPageDto.E_QTLS);
    assertThat(result.getDropdowns()).containsExactly("dropdown1", "dropdown2");
    assertThat(result.getRowCount()).isEqualTo("100");
    assertThat(result.getColumns()).isEmpty();
    assertThat(result.getFilters()).isEmpty();
  }

  @Test
  @DisplayName("should map hierarchy fields to dto")
  void shouldMapHierarchyFieldsToDto() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(List.of());
    document.setRowIdDataKey("composite_id");
    document.setParentIdDataKey("rna_composite_id");
    document.setParentNoun(
      ComparisonToolConfigDocument.ComparisonToolNoun.builder()
        .singular("gene")
        .plural("genes")
        .build()
    );
    document.setViewNoun(
      ComparisonToolConfigDocument.ComparisonToolNoun.builder()
        .singular("protein")
        .plural("proteins")
        .build()
    );

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getRowIdDataKey()).isEqualTo("composite_id");
    assertThat(result.getParentIdDataKey()).isEqualTo("rna_composite_id");
    assertThat(result.getParentNoun().getSingular()).isEqualTo("gene");
    assertThat(result.getParentNoun().getPlural()).isEqualTo("genes");
    assertThat(result.getViewNoun().getSingular()).isEqualTo("protein");
    assertThat(result.getViewNoun().getPlural()).isEqualTo("proteins");
  }

  @Test
  @DisplayName("should leave hierarchy fields null when document declares no hierarchy")
  void shouldLeaveHierarchyFieldsNullWhenDocumentDeclaresNoHierarchy() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getRowIdDataKey()).isNull();
    assertThat(result.getParentIdDataKey()).isNull();
    assertThat(result.getParentNoun()).isNull();
    assertThat(result.getViewNoun()).isNull();
  }

  @Test
  @DisplayName("should map columns with all required fields")
  void shouldMapColumnsWithAllRequiredFields() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .type("text")
        .dataKey("gene_symbol")
        .isExported(true)
        .isHidden(false)
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of(column));
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).hasSize(1);
    ComparisonToolConfigColumnDto columnDto = result.getColumns().get(0);
    assertThat(columnDto.getType()).isEqualTo(ComparisonToolConfigColumnDto.TypeEnum.TEXT);
    assertThat(columnDto.getDataKey()).isEqualTo("gene_symbol");
    assertThat(columnDto.getIsExported()).isTrue();
    assertThat(columnDto.getIsHidden()).isFalse();
  }

  @Test
  @DisplayName("should map columns with optional fields")
  void shouldMapColumnsWithOptionalFields() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .name("Gene")
        .type("link_internal")
        .dataKey("gene_symbol")
        .tooltip("Gene identifier")
        .sortTooltip("Sort by gene")
        .linkText("View details")
        .linkUrl("/genes/{id}")
        .isExported(true)
        .isHidden(false)
        .columnWidth(300)
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of(column));
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).hasSize(1);
    ComparisonToolConfigColumnDto columnDto = result.getColumns().get(0);
    assertThat(columnDto.getName()).isEqualTo("Gene");
    assertThat(columnDto.getType()).isEqualTo(ComparisonToolConfigColumnDto.TypeEnum.LINK_INTERNAL);
    assertThat(columnDto.getTooltip()).isEqualTo("Gene identifier");
    assertThat(columnDto.getSortTooltip()).isEqualTo("Sort by gene");
    assertThat(columnDto.getLinkText()).isEqualTo("View details");
    assertThat(columnDto.getLinkUrl()).isEqualTo("/genes/{id}");
    assertThat(columnDto.getColumnWidth()).isEqualTo(300);
  }

  @Test
  @DisplayName("should map column with null column width")
  void shouldMapColumnWithNullColumnWidth() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column =
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .type("text")
        .dataKey("gene_symbol")
        .isExported(true)
        .isHidden(false)
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of(column));
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns()).hasSize(1);
    assertThat(result.getColumns().get(0).getColumnWidth()).isNull();
  }

  @Test
  @DisplayName("should map filters with required fields")
  void shouldMapFiltersWithRequiredFields() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigFilter filter =
      ComparisonToolConfigDocument.ComparisonToolConfigFilter.builder()
        .name("Cell Class")
        .dataKey("cell_class")
        .queryParamKey("class")
        .values(List.of("Astrocyte", "Excitatory Neuron"))
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(List.of(filter));

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getFilters()).hasSize(1);
    ComparisonToolConfigFilterDto filterDto = result.getFilters().get(0);
    assertThat(filterDto.getName()).isEqualTo("Cell Class");
    assertThat(filterDto.getDataKey()).isEqualTo("cell_class");
    assertThat(filterDto.getQueryParamKey()).isEqualTo("class");
    assertThat(filterDto.getValues()).containsExactly("Astrocyte", "Excitatory Neuron");
  }

  @Test
  @DisplayName("should map filters with short name")
  void shouldMapFiltersWithShortName() {
    // given
    ComparisonToolConfigDocument.ComparisonToolConfigFilter filter =
      ComparisonToolConfigDocument.ComparisonToolConfigFilter.builder()
        .name("Cell Class")
        .dataKey("cell_class")
        .shortName("Class")
        .queryParamKey("class")
        .values(List.of("Astrocyte"))
        .build();

    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(List.of(filter));

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getFilters()).hasSize(1);
    assertThat(result.getFilters().get(0).getShortName()).isEqualTo("Class");
  }

  @Test
  @DisplayName("should throw data integrity exception for a missing columns list")
  void shouldThrowDataIntegrityExceptionForAMissingColumnsList() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(null);
    document.setFilters(List.of());

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing columns in comparison tool config record");
  }

  @Test
  @DisplayName("should throw data integrity exception for a missing filters list")
  void shouldThrowDataIntegrityExceptionForAMissingFiltersList() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(null);

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing filters in comparison tool config record");
  }

  @Test
  @DisplayName("should throw data integrity exception for a null column entry")
  void shouldThrowDataIntegrityExceptionForANullColumnEntry() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(
      Arrays.asList((ComparisonToolConfigDocument.ComparisonToolConfigColumn) null)
    );
    document.setFilters(List.of());

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing column in comparison tool config record");
  }

  @Test
  @DisplayName("should throw data integrity exception for a null filter entry")
  void shouldThrowDataIntegrityExceptionForANullFilterEntry() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(
      Arrays.asList((ComparisonToolConfigDocument.ComparisonToolConfigFilter) null)
    );

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing filter in comparison tool config record");
  }

  @Test
  @DisplayName("should throw data integrity exception for missing filter values")
  void shouldThrowDataIntegrityExceptionForMissingFilterValues() {
    assertMissingFilterField(
      ComparisonToolConfigDocument.ComparisonToolConfigFilter.builder()
        .name("Variant")
        .dataKey("variant")
        .queryParamKey("variant")
        .build(),
      "Missing filter values in comparison tool config record"
    );
  }

  @Test
  @DisplayName("should throw data integrity exception for a noun missing a form")
  void shouldThrowDataIntegrityExceptionForANounMissingAForm() {
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(List.of());
    document.setViewNoun(
      ComparisonToolConfigDocument.ComparisonToolNoun.builder().singular("eQTL").build()
    );

    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing noun plural in comparison tool config record");
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
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
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
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getPage()).isEqualTo(ComparisonToolPageDto.E_QTLS);
  }

  @Test
  @DisplayName("should map numeric column type")
  void shouldMapNumericColumnType() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of(column("numeric", "beta")));
    document.setFilters(List.of());

    // when
    ComparisonToolConfigDto result = mapper.toDto(document);

    // then
    assertThat(result.getColumns().get(0).getType()).isEqualTo(
      ComparisonToolConfigColumnDto.TypeEnum.NUMERIC
    );
  }

  @Test
  @DisplayName("should throw data integrity exception for an unknown page")
  void shouldThrowDataIntegrityExceptionForAnUnknownPage() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("Unknown Page");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(List.of());

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Unexpected page value 'Unknown Page' in comparison tool config record");
  }

  @Test
  @DisplayName("should throw data integrity exception for an unknown column type")
  void shouldThrowDataIntegrityExceptionForAnUnknownColumnType() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of(column("string", "adj_p_val")));
    document.setFilters(List.of());

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Unexpected column type value 'string' in comparison tool config record");
  }

  @Test
  @DisplayName("should throw data integrity exception for a missing column type")
  void shouldThrowDataIntegrityExceptionForAMissingColumnType() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of(column(null, "variant")));
    document.setFilters(List.of());

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing column type in comparison tool config record");
  }

  @Test
  @DisplayName("should throw data integrity exception for missing dropdowns")
  void shouldThrowDataIntegrityExceptionForMissingDropdowns() {
    // given
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setColumns(List.of());
    document.setFilters(List.of());

    // when & then
    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing dropdowns in comparison tool config record");
  }

  @Test
  @DisplayName("should throw data integrity exception for missing required column fields")
  void shouldThrowDataIntegrityExceptionForMissingRequiredColumnFields() {
    assertMissingColumnField(
      column("text", null),
      "Missing column data_key in comparison tool config record"
    );
    assertMissingColumnField(
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .type("text")
        .dataKey("variant")
        .isHidden(false)
        .build(),
      "Missing column is_exported in comparison tool config record"
    );
    assertMissingColumnField(
      ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
        .type("text")
        .dataKey("variant")
        .isExported(true)
        .build(),
      "Missing column is_hidden in comparison tool config record"
    );
  }

  @Test
  @DisplayName("should throw data integrity exception for missing required filter fields")
  void shouldThrowDataIntegrityExceptionForMissingRequiredFilterFields() {
    assertMissingFilterField(
      filter(null, "cell_class", "class"),
      "Missing filter name in comparison tool config record"
    );
    assertMissingFilterField(
      filter("Cell Class", null, "class"),
      "Missing filter data_key in comparison tool config record"
    );
    assertMissingFilterField(
      filter("Cell Class", "cell_class", null),
      "Missing filter query_param_key in comparison tool config record"
    );
  }

  private void assertMissingColumnField(
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column,
    String expectedMessage
  ) {
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of(column));
    document.setFilters(List.of());

    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage(expectedMessage);
  }

  private void assertMissingFilterField(
    ComparisonToolConfigDocument.ComparisonToolConfigFilter filter,
    String expectedMessage
  ) {
    ComparisonToolConfigDocument document = new ComparisonToolConfigDocument();
    document.setPage("eQTLs");
    document.setDropdowns(List.of());
    document.setColumns(List.of());
    document.setFilters(List.of(filter));

    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage(expectedMessage);
  }

  private static ComparisonToolConfigDocument.ComparisonToolConfigFilter filter(
    String name,
    String dataKey,
    String queryParamKey
  ) {
    return ComparisonToolConfigDocument.ComparisonToolConfigFilter.builder()
      .name(name)
      .dataKey(dataKey)
      .queryParamKey(queryParamKey)
      .values(List.of("Astrocyte"))
      .build();
  }

  private static ComparisonToolConfigDocument.ComparisonToolConfigColumn column(
    String type,
    String dataKey
  ) {
    return ComparisonToolConfigDocument.ComparisonToolConfigColumn.builder()
      .type(type)
      .dataKey(dataKey)
      .isExported(true)
      .isHidden(false)
      .build();
  }
}
