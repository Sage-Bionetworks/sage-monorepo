package org.sagebionetworks.explorers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * One of a comparison tool's identity spaces — the space its rows are identified in (derived from
 * its item filter via {@link #fromItemFilter}), or the space their parents are identified in
 * ({@link ComparisonToolRepositorySupport#getParentIdSpace()}).
 *
 * <p>An identity space knows two things: how to turn a client-supplied item token back into
 * {@link Criteria} over stored fields, and how to build the token itself as an aggregation
 * expression ({@link #tokenExpression()}), which is what lets rows be grouped by parent. No
 * composite token is stored in MongoDB, so both directions are derived at request time.
 *
 * <p>Two variants:
 *
 * <ul>
 *   <li>{@link Stored} — the token <em>is</em> the value of a single stored field
 *   <li>{@link Composite} — the token encodes several stored fields, joined by {@link #DELIMITER}
 * </ul>
 */
public sealed interface ItemIdSpaceDef {
  /**
   * Separator joining the parts of a composite item token. An identifier DTO backing a composite
   * space must join its parts with this same character, or {@link #tokenExpression()} will build
   * tokens that disagree with the ones the DTO renders and parses.
   */
  String DELIMITER = "~";

  /**
   * Rendered in place of a blank constituent field — null, missing, empty, or whitespace only —
   * when building a composite token.
   *
   * <p>{@code $concat} yields {@code null} when any argument is null, which would collapse every
   * row with an absent field into one group, so {@link Composite#tokenExpression()} guards each
   * part with this fallback. The value is what Java renders for a null field, so a token emitted
   * here agrees with the one an identifier DTO builds for the same row.
   *
   * <p>An empty or whitespace-only part is guarded for a second reason: an identifier DTO rejects a
   * blank part outright, so emitting one verbatim would build a token the DTO throws on, failing an
   * entire request over one malformed document.
   *
   * <p>The guard only makes the token well-formed; round-tripping it is the identifier DTO's job.
   * Its {@code parse()} must read this value back as a null part, and its {@code toCriteria()} must
   * match a blank field for that part. An identifier DTO that parses it as a literal string instead
   * looks for the string {@code "null"}, and rows grouped under the fallback are silently absent
   * from a parent-scoped fetch.
   */
  String MISSING_PART = "null";

  /**
   * An identity space whose token is the value of one stored field.
   *
   * <p>Unlike a composite token, the field is not guarded with {@link #MISSING_PART}, so a row whose
   * field is null or missing has a null token and no item names it. As a parent space, such a row
   * has no parent.
   *
   * @param field the MongoDB field holding the identity (e.g. {@code "ensembl_gene_id"})
   */
  static ItemIdSpaceDef stored(String field) {
    return new Stored(field);
  }

  /**
   * An identity space whose token encodes several stored fields.
   *
   * @param fields the constituent MongoDB fields, in token order — may be empty, in which case
   *     {@link #tokenExpression()} is unavailable (see its contract)
   * @param parser parses a token back into {@link Criteria}, typically a method reference to an
   *     identifier DTO's {@code toCriteria()}
   */
  static ItemIdSpaceDef composite(List<String> fields, Function<String, Criteria> parser) {
    return new Composite(List.copyOf(fields), parser);
  }

  /**
   * Derives the identity space an {@link ItemFilterDef} matches items in. This is a CT's row space:
   * its row identity is already declared by the item filter in its {@link CtFilterConfig}, so no
   * subclass declares a row space of its own.
   */
  static ItemIdSpaceDef fromItemFilter(ItemFilterDef itemFilter) {
    return switch (itemFilter) {
      case ItemFilterDef.Simple(String field) -> stored(field);
      case ItemFilterDef.Composite(Function<String, Criteria> parser) -> composite(
        List.of(),
        parser
      );
    };
  }

  /** Criteria matching the single row (or rows) identified by {@code item}. */
  Criteria toCriteria(String item);

  /** Criteria matching any of {@code items} — the INCLUDE direction. */
  Criteria criteriaForAny(Collection<String> items);

  /** Criteria matching none of {@code items} — the EXCLUDE direction. */
  Criteria criteriaForNone(Collection<String> items);

  /**
   * An aggregation expression evaluating to this space's token for the row being processed, for use
   * as a {@code $group} key or an {@code $addFields} value.
   *
   * @throws IllegalStateException when the space's constituent field names are unknown
   */
  Object tokenExpression();

  /**
   * Identity space backed by a single stored field.
   *
   * @param field the MongoDB field holding the identity
   */
  record Stored(String field) implements ItemIdSpaceDef {
    @Override
    public Criteria toCriteria(String item) {
      return Criteria.where(field).is(item);
    }

    @Override
    public Criteria criteriaForAny(Collection<String> items) {
      return Criteria.where(field).in(items);
    }

    @Override
    public Criteria criteriaForNone(Collection<String> items) {
      return Criteria.where(field).nin(items);
    }

    @Override
    public Object tokenExpression() {
      return ApiHelper.buildPathReadExpr(field);
    }
  }

  /**
   * Identity space backed by several stored fields joined by {@link #DELIMITER}.
   *
   * @param fields the constituent MongoDB fields, in token order
   * @param parser parses a token back into {@link Criteria}
   */
  record Composite(List<String> fields, Function<String, Criteria> parser)
    implements ItemIdSpaceDef {
    @Override
    public Criteria toCriteria(String item) {
      return parser.apply(item);
    }

    /**
     * {@inheritDoc}
     *
     * <p>An empty {@code $or} is rejected by MongoDB, so empty items yield
     * {@link ApiHelper#matchNothing()} instead.
     */
    @Override
    public Criteria criteriaForAny(Collection<String> items) {
      if (items.isEmpty()) {
        return ApiHelper.matchNothing();
      }
      return new Criteria().orOperator(parseAll(items));
    }

    /**
     * {@inheritDoc}
     *
     * <p>An empty {@code $nor} is rejected by MongoDB, so empty items yield match-all criteria —
     * excluding nothing excludes nothing.
     */
    @Override
    public Criteria criteriaForNone(Collection<String> items) {
      if (items.isEmpty()) {
        return new Criteria();
      }
      return new Criteria().norOperator(parseAll(items));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Available only when the constituent field names were declared. A space derived from an
     * {@link ItemFilterDef.Composite} carries a parser but no field list, because the parser alone
     * cannot say which fields it reads.
     *
     * <p>Every part is guarded with {@link #MISSING_PART}, since a single null field would
     * otherwise make {@code $concat} yield {@code null} for the whole token, and a blank one would
     * build a token no identifier DTO accepts.
     */
    @Override
    public Object tokenExpression() {
      if (fields.isEmpty()) {
        throw new IllegalStateException(
          "Cannot build a token expression for a composite identity space with no declared fields." +
          " Declare the constituent field names via ItemIdSpaceDef.composite(fields, parser)."
        );
      }

      List<Object> parts = new ArrayList<>();
      for (String field : fields) {
        if (!parts.isEmpty()) {
          parts.add(DELIMITER);
        }
        parts.add(guardBlank(ApiHelper.buildPathReadExpr(field)));
      }
      return new Document("$concat", parts);
    }

    /**
     * Substitutes {@link #MISSING_PART} for a blank value of {@code fieldRead}, matching the
     * emptiness rule an identifier DTO applies when it parses a part: trimmed and non-empty.
     *
     * <p>The {@code $ifNull} is what lets {@code $trim} run: it rejects a non-string input, so an
     * absent field has to become {@code ""} before it reaches the trim.
     */
    private static Document guardBlank(Object fieldRead) {
      Document trimmed = new Document(
        "$trim",
        new Document("input", new Document("$ifNull", List.of(fieldRead, "")))
      );
      return new Document(
        "$cond",
        List.of(new Document("$eq", List.of(trimmed, "")), MISSING_PART, fieldRead)
      );
    }

    private Criteria[] parseAll(Collection<String> items) {
      return items.stream().map(parser).toArray(Criteria[]::new);
    }
  }
}
