package org.sagebionetworks.explorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.function.Function;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;

class ItemFilterDefTest {

  private static final String STORED_FIELD = "unique_id";
  private static final String SPACED_FIELD = "4 months.log2_fc";
  private static final List<String> COMPOSITE_FIELDS = List.of(
    "ensembl_gene_id",
    "name.link_text",
    "sex"
  );

  /** Parses "a~b~c" the way the product identifier DTOs do, so tokens round-trip. */
  private static final Function<String, Criteria> PARSER = item -> {
    String[] parts = item.split(ItemFilterDef.DELIMITER, -1);
    return new Criteria()
      .andOperator(
        Criteria.where(COMPOSITE_FIELDS.get(0)).is(parts[0]),
        Criteria.where(COMPOSITE_FIELDS.get(1)).is(parts[1]),
        Criteria.where(COMPOSITE_FIELDS.get(2)).is(parts[2])
      );
  };

  @Nested
  @DisplayName("Simple item filter")
  class SimpleItemFilter {

    @Test
    @DisplayName("should match a single item by equality on the stored field")
    void shouldMatchSingleItemByEquality() {
      Criteria criteria = new ItemFilterDef.Simple(STORED_FIELD).toCriteria("P1");

      assertThat(criteria.getCriteriaObject().toString()).contains(STORED_FIELD).contains("P1");
    }

    @Test
    @DisplayName("should use $in for the include direction")
    void shouldUseInForIncludeDirection() {
      Criteria criteria = new ItemFilterDef.Simple(STORED_FIELD).criteriaForAny(List.of("P1", "P2"));

      assertThat(criteria.getCriteriaObject().toString())
        .contains(STORED_FIELD)
        .contains("$in")
        .contains("P1");
    }

    @Test
    @DisplayName("should use $nin for the exclude direction")
    void shouldUseNinForExcludeDirection() {
      Criteria criteria = new ItemFilterDef.Simple(STORED_FIELD).criteriaForNone(List.of("P1", "P2"));

      assertThat(criteria.getCriteriaObject().toString())
        .contains(STORED_FIELD)
        .contains("$nin")
        .contains("P1");
    }

    @Test
    @DisplayName("should build the token as a plain field read")
    void shouldBuildTokenAsPlainFieldRead() {
      assertThat(new ItemFilterDef.Simple(STORED_FIELD).tokenExpression())
        .isEqualTo("$" + STORED_FIELD);
    }

    @Test
    @DisplayName("should build the token via $getField when the field name contains a space")
    void shouldBuildTokenViaGetFieldWhenFieldNameContainsSpace() {
      assertThat(new ItemFilterDef.Simple(SPACED_FIELD).tokenExpression().toString())
        .contains("$getField")
        .contains("log2_fc");
    }
  }

  @Nested
  @DisplayName("Composite item filter")
  class CompositeItemFilter {

    @Test
    @DisplayName("should delegate a single item to the configured parser")
    void shouldDelegateSingleItemToParser() {
      Criteria criteria = new ItemFilterDef.Composite(COMPOSITE_FIELDS, PARSER).toCriteria(
        "ENSG1~5xFAD~Female"
      );

      String criteriaStr = criteria.getCriteriaObject().toString();
      assertThat(criteriaStr).contains("ENSG1").contains("5xFAD").contains("Female");
    }

    @Test
    @DisplayName("should combine parsed items with $or for the include direction")
    void shouldUseOrForIncludeDirection() {
      Criteria criteria = new ItemFilterDef.Composite(COMPOSITE_FIELDS, PARSER).criteriaForAny(
        List.of("ENSG1~5xFAD~Female", "ENSG2~5xFAD~Male")
      );

      assertThat(criteria.getCriteriaObject().toString())
        .contains("$or")
        .contains("ENSG1")
        .contains("ENSG2");
    }

    @Test
    @DisplayName("should combine parsed items with $nor for the exclude direction")
    void shouldUseNorForExcludeDirection() {
      Criteria criteria = new ItemFilterDef.Composite(COMPOSITE_FIELDS, PARSER).criteriaForNone(
        List.of("ENSG1~5xFAD~Female")
      );

      assertThat(criteria.getCriteriaObject().toString()).contains("$nor").contains("ENSG1");
    }

    @Test
    @DisplayName("should match nothing rather than emit an empty $or when items are empty")
    void shouldMatchNothingWhenIncludingEmptyItems() {
      Criteria criteria = new ItemFilterDef.Composite(COMPOSITE_FIELDS, PARSER).criteriaForAny(
        List.of()
      );

      assertThat(criteria.getCriteriaObject())
        .isEqualTo(ApiHelper.matchNothing().getCriteriaObject());
    }

    @Test
    @DisplayName("should match everything rather than emit an empty $nor when items are empty")
    void shouldMatchEverythingWhenExcludingEmptyItems() {
      Criteria criteria = new ItemFilterDef.Composite(COMPOSITE_FIELDS, PARSER).criteriaForNone(
        List.of()
      );

      assertThat(criteria.getCriteriaObject()).isEmpty();
    }

    @Test
    @DisplayName("should build the token as a delimiter-separated $concat over its fields")
    void shouldBuildTokenAsDelimitedConcat() {
      Object token = new ItemFilterDef.Composite(COMPOSITE_FIELDS, PARSER).tokenExpression();

      assertThat(token).isEqualTo(
        new Document(
          "$concat",
          List.of(
            guarded("$ensembl_gene_id"),
            ItemFilterDef.DELIMITER,
            guarded("$name.link_text"),
            ItemFilterDef.DELIMITER,
            guarded("$sex")
          )
        )
      );
    }

    @Test
    @DisplayName("should guard every $concat part against a blank field")
    void shouldGuardEveryConcatPartAgainstBlankField() {
      // One unguarded part is enough for $concat to yield null, which would collapse every row with
      // an absent field into a single group.
      Document token = (Document) new ItemFilterDef.Composite(
        COMPOSITE_FIELDS,
        PARSER
      ).tokenExpression();

      List<Document> guards = token
        .getList("$concat", Object.class)
        .stream()
        .filter(Document.class::isInstance)
        .map(Document.class::cast)
        .toList();

      assertThat(guards).hasSize(COMPOSITE_FIELDS.size()).allSatisfy(guard ->
        assertThat(guard.getList("$cond", Object.class))
          .element(1)
          .isEqualTo(ItemFilterDef.MISSING_PART)
      );
    }

    @Test
    @DisplayName("should substitute the sentinel for a null, empty, or whitespace-only part")
    void shouldSubstituteSentinelForBlankPart() {
      // "null" is spelled out rather than read from MISSING_PART because its value is the contract:
      // it has to match what an identifier DTO renders for a null part, so changing the constant
      // has to fail a test.
      Document token = (Document) new ItemFilterDef.Composite(
        COMPOSITE_FIELDS,
        PARSER
      ).tokenExpression();

      assertThat(token.getList("$concat", Object.class).get(0))
        .as("$ifNull absorbs a null or absent field and $trim empties a whitespace-only one, so all"
          + " three reach the sentinel branch -- the emptiness rule an identifier DTO applies")
        .isEqualTo(
          Document.parse(
            """
            { "$cond": [
                { "$eq": [
                    { "$trim": { "input": { "$ifNull": ["$ensembl_gene_id", ""] } } },
                    ""
                ] },
                "null",
                "$ensembl_gene_id"
            ] }
            """
          )
        );
    }

    private Document guarded(Object fieldRead) {
      Document trimmed = new Document(
        "$trim",
        new Document("input", new Document("$ifNull", List.of(fieldRead, "")))
      );
      return new Document(
        "$cond",
        List.of(new Document("$eq", List.of(trimmed, "")), ItemFilterDef.MISSING_PART, fieldRead)
      );
    }

    @Test
    @DisplayName("should reject a token expression when no constituent fields were declared")
    void shouldRejectTokenExpressionWhenFieldsAreUndeclared() {
      // A parser cannot say which fields it reads, so a CT that needs parent tokens has to declare
      // them explicitly via new ItemFilterDef.Composite(fields, parser).
      ItemFilterDef filter = new ItemFilterDef.Composite(PARSER);

      assertThatThrownBy(filter::tokenExpression)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("no declared fields");
    }
  }
}
