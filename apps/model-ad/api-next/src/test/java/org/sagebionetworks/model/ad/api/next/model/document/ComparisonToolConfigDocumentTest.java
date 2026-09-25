package org.sagebionetworks.model.ad.api.next.model.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Pins the Spring Data conversion behavior that reads of the `ui_config` collection depend on.
 * Some filters store `values` as a bare string instead of an array (e.g. `"values": "PSEN1"` in
 * the marmoset config), while both this document class and the API contract declare it as a list.
 * Spring's ObjectToCollectionConverter wraps the lone string during the read; no mapping code
 * performs that normalization, so losing it would silently empty a filter panel.
 */
class ComparisonToolConfigDocumentTest {

  private MappingMongoConverter converter;

  @BeforeEach
  void setUp() {
    MongoMappingContext mappingContext = new MongoMappingContext();
    mappingContext.afterPropertiesSet();

    converter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);
    converter.afterPropertiesSet();
  }

  @Test
  @DisplayName("should read bare string filter values as a single-element list")
  void shouldReadBareStringFilterValuesAsSingleElementList() {
    // given
    Document source = configWithFilterValues("PSEN1");

    // when
    ComparisonToolConfigDocument result = converter.read(
      ComparisonToolConfigDocument.class,
      source
    );

    // then
    assertThat(filterValuesOf(result)).containsExactly("PSEN1");
  }

  @Test
  @DisplayName("should read array filter values as a list")
  void shouldReadArrayFilterValuesAsList() {
    // given
    Document source = configWithFilterValues(List.of("Familial AD", "Late Onset AD"));

    // when
    ComparisonToolConfigDocument result = converter.read(
      ComparisonToolConfigDocument.class,
      source
    );

    // then
    assertThat(filterValuesOf(result)).containsExactly("Familial AD", "Late Onset AD");
  }

  @Test
  @DisplayName("should read hierarchy fields including nested nouns")
  void shouldReadHierarchyFieldsIncludingNestedNouns() {
    // given
    Document source = new Document()
      .append("page", "Differential Expression")
      .append("dropdowns", List.of())
      .append("columns", List.of())
      .append("filters", List.of())
      .append("row_id_data_key", "composite_id")
      .append("parent_id_data_key", "rna_composite_id")
      .append("parent_noun", new Document().append("singular", "gene").append("plural", "genes"))
      .append(
        "view_noun",
        new Document().append("singular", "protein").append("plural", "proteins")
      );

    // when
    ComparisonToolConfigDocument result = converter.read(
      ComparisonToolConfigDocument.class,
      source
    );

    // then
    assertThat(result.getRowIdDataKey()).isEqualTo("composite_id");
    assertThat(result.getParentIdDataKey()).isEqualTo("rna_composite_id");
    assertThat(result.getParentNoun().getSingular()).isEqualTo("gene");
    assertThat(result.getParentNoun().getPlural()).isEqualTo("genes");
    assertThat(result.getViewNoun().getSingular()).isEqualTo("protein");
    assertThat(result.getViewNoun().getPlural()).isEqualTo("proteins");
  }

  @Test
  @DisplayName("should read hierarchy fields as null when the config omits them")
  void shouldReadHierarchyFieldsAsNullWhenConfigOmitsThem() {
    // given
    Document source = configWithFilterValues(List.of("Familial AD"));

    // when
    ComparisonToolConfigDocument result = converter.read(
      ComparisonToolConfigDocument.class,
      source
    );

    // then
    assertThat(result.getRowIdDataKey()).isNull();
    assertThat(result.getParentIdDataKey()).isNull();
    assertThat(result.getParentNoun()).isNull();
    assertThat(result.getViewNoun()).isNull();
  }

  private static Document configWithFilterValues(Object values) {
    Document filter = new Document()
      .append("name", "Modified Gene")
      .append("data_key", "modified_genes")
      .append("query_param_key", "modifiedGenes")
      .append("values", values);

    return new Document()
      .append("page", "Marmoset Model Overview")
      .append("dropdowns", List.of())
      .append("columns", List.of())
      .append("filters", List.of(filter));
  }

  private static List<String> filterValuesOf(ComparisonToolConfigDocument document) {
    assertThat(document.getFilters()).hasSize(1);
    return document.getFilters().get(0).getValues();
  }
}
