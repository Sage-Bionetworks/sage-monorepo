package org.sagebionetworks.agora.api.next.configuration;

import org.springframework.data.mongodb.core.query.Collation;

/**
 * Shared Collation constants for Mongo/DocumentDB queries.
 *
 * <p>The {@code locale}/{@code strength} tuple here must match the collation defined on
 * backing indexes in the data loader (agora-data-manager) byte-for-byte — any drift
 * silently degrades the query to a collection scan with in-memory collation comparison.
 * Collation also requires DocumentDB Planner V3 (default on DocDB 8.0); never force a
 * hint that picks v1/v2 or the collation index is bypassed.
 *
 * <p><b>Side effect to know:</b> applying collation to an aggregation or query is
 * pipeline-wide and affects every string comparison in scope — not just sort. That
 * includes {@code Criteria.where(field).in(...)} matching. Callers MUST ensure the
 * fields they filter on contain canonical-cased values (controlled vocabularies), or
 * matching widens after collation is applied. MongoDB {@code $regex} is an exception:
 * it ignores collation case-insensitivity, so the regex {@code "i"} flag is still
 * required for case-insensitive partial-match search.
 */
public final class MongoCollations {

  private MongoCollations() {
    // Utility class, prevent instantiation
  }

  /**
   * Case-insensitive (accent-sensitive) English collation.
   *
   * <p>Per MongoDB docs, strength {@code 2} performs comparisons up to secondary
   * differences (base characters + diacritics) — case differences are ignored. This
   * mirrors the previous {@code $toLower}-based sort semantics for ASCII data and is
   * the value MongoDB's own
   * <a href="https://www.mongodb.com/docs/manual/core/index-case-insensitive/">case-insensitive
   * index docs</a> use in their example.
   *
   * <p>Strength {@code 3} (MongoDB's default) is the case-sensitive tertiary level —
   * do not use it here.
   */
  public static final Collation EN_CI = Collation.of("en").strength(2);
}
