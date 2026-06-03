package org.sagebionetworks.explorers;

import java.util.function.Function;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Defines how to filter comparison-tool rows by a list of item identifiers.
 *
 * <p>Two variants:
 *
 * <ul>
 *   <li>{@link Simple} — items are matched by a single field using {@code $in}/{@code $nin}
 *   <li>{@link Composite} — items are parsed into multi-field AND-clauses combined via
 *       {@code $or}/{@code $nor}
 * </ul>
 */
public sealed interface ItemFilterDef {

  /**
   * Simple item filter: match items by a single field.
   *
   * <p>INCLUDE mode: {@code field $in items}<br>
   * EXCLUDE mode: {@code field $nin items}
   *
   * @param field the MongoDB field name to match (e.g. "ensembl_gene_id")
   */
  record Simple(String field) implements ItemFilterDef {}

  /**
   * Composite item filter: parse each item string into a multi-field Criteria.
   *
   * <p>The parser function is typically a method reference to an identifier DTO's
   * {@code toCriteria()} method (e.g. {@code DiseaseCorrelationIdentifier::toCriteria}).
   *
   * <p>INCLUDE mode: {@code $or: [parser(item1), parser(item2), ...]}<br>
   * EXCLUDE mode: {@code $nor: [parser(item1), parser(item2), ...]}
   *
   * @param parser function that parses an item string and returns a Criteria matching that item
   */
  record Composite(Function<String, Criteria> parser) implements ItemFilterDef {}
}
