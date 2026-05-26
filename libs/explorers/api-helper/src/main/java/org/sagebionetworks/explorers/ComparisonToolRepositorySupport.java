package org.sagebionetworks.explorers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Base class for comparison-tool repository implementations backed by MongoDB aggregation.
 *
 * <p>Subclasses build per-CT {@link Criteria} (filters, search, identifier matching) and delegate
 * pipeline assembly + execution to {@link #executePagedAggregation(Criteria, Pageable)}. The
 * pipeline shape is uniform across CTs:
 *
 * <pre>
 *   $match
 *   $addFields (extra pre-sort computations, from {@link #getExtraSortFieldComputations(Sort)})
 *   $addFields (computed sort fields, from {@link #getComputedSortFieldExpressions()})
 *   $sort       (field names resolved via {@link #getSortFieldAliases()} and the computed map)
 *   $skip / $limit
 * </pre>
 *
 * @param <T> the MongoDB document type returned by this repository
 */
@Slf4j
public abstract class ComparisonToolRepositorySupport<T> {

  protected final MongoTemplate mongoTemplate;

  protected ComparisonToolRepositorySupport(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  protected abstract String getCollectionName();

  protected abstract Class<T> getDocumentClass();

  /**
   * Map of sort field → MongoDB expression that produces a sortable scalar.
   *
   * <p>When a user sorts by a key in this map, an {@code $addFields: {<field>_sort: <expr>}} stage
   * is injected and the {@code $sort} uses {@code <field>_sort} instead of the raw field. Use this
   * for case-insensitive string sort (via {@link #toLowerExpr(String)}), array-to-string reduction
   * (via {@link #arrayToLoweredStringExpr(String)}), nested-field unwrapping, or any case where
   * the raw field isn't directly sortable.
   */
  protected Map<String, Object> getComputedSortFieldExpressions() {
    return Map.of();
  }

  /**
   * Map of sort field → aliased field name for a direct {@code $sort} rename (no {@code $addFields}
   * stage). Use when an alternate field is already present on the document (e.g.
   * {@code "age" → "age_numeric"} for a numeric companion to a string column).
   */
  protected Map<String, String> getSortFieldAliases() {
    return Map.of();
  }

  /**
   * Extra {@code $addFields} stages contributed before the standard computed-sort step.
   *
   * <p>Use when a computed sort expression references a field that itself must be computed first
   * (e.g. Transcriptomics' {@code display_gene_symbol} fallback, which is later lower-cased into
   * {@code gene_symbol_sort}). The subclass typically inspects {@code sort} to decide whether the
   * stage is needed for the requested ordering.
   */
  @SuppressWarnings("java:S1172") // sort is unused in the default; subclasses use it for inspection
  protected List<AggregationOperation> getExtraSortFieldComputations(Sort sort) {
    return List.of();
  }

  /**
   * Assembles and executes the standard CT pipeline. Subclasses build {@code matchCriteria} and
   * pass it in along with the {@link Pageable}.
   */
  protected final Page<T> executePagedAggregation(Criteria matchCriteria, Pageable pageable) {
    try {
      long total = mongoTemplate.count(new Query(matchCriteria), getCollectionName());

      List<AggregationOperation> operations = new ArrayList<>();
      operations.add(Aggregation.match(matchCriteria));
      operations.addAll(getExtraSortFieldComputations(pageable.getSort()));

      AggregationOperation computedSort = buildComputedSortFields(pageable.getSort());
      if (computedSort != null) {
        operations.add(computedSort);
      }

      AggregationOperation sort = buildSortOperation(pageable.getSort());
      if (sort != null) {
        operations.add(sort);
      }

      long skipCount = (long) pageable.getPageNumber() * pageable.getPageSize();
      operations.add(Aggregation.skip(skipCount));
      operations.add(Aggregation.limit(pageable.getPageSize()));

      Aggregation aggregation = Aggregation.newAggregation(operations);
      AggregationResults<T> results = mongoTemplate.aggregate(
        aggregation,
        getCollectionName(),
        getDocumentClass()
      );

      return new PageImpl<>(results.getMappedResults(), pageable, total);
    } catch (Exception e) {
      log.error("Error executing aggregation on collection {}", getCollectionName(), e);
      throw e;
    }
  }

  /** Convenience: {@code $toLower("$" + fieldPath)} as a MongoDB expression Document. */
  protected static Object toLowerExpr(String fieldPath) {
    return new Document("$toLower", "$" + fieldPath);
  }

  /**
   * Convenience: reduces an array field into a single lowercased string with a NUL separator,
   * producing a stable lexicographic sort key. Avoids MongoDB's "parallel arrays" limitation.
   */
  protected static Object arrayToLoweredStringExpr(String arrayField) {
    Document reduce = new Document(
      "$reduce",
      new Document()
        .append("input", "$" + arrayField)
        .append("initialValue", "")
        .append("in", new Document("$concat", List.of("$$value", "\u0000", "$$this")))
    );
    return new Document("$toLower", reduce);
  }

  private AggregationOperation buildComputedSortFields(Sort sort) {
    Map<String, Object> expressions = getComputedSortFieldExpressions();
    if (sort.isUnsorted() || expressions.isEmpty()) {
      return null;
    }

    Document fields = new Document();
    for (Sort.Order order : sort) {
      Object expression = expressions.get(order.getProperty());
      if (expression != null) {
        fields.append(order.getProperty() + "_sort", expression);
      }
    }

    if (fields.isEmpty()) {
      return null;
    }
    return context -> new Document("$addFields", fields);
  }

  private AggregationOperation buildSortOperation(Sort sort) {
    if (sort.isUnsorted()) {
      return null;
    }

    Map<String, Object> computed = getComputedSortFieldExpressions();
    Map<String, String> aliases = getSortFieldAliases();

    Document sortDoc = new Document();
    for (Sort.Order order : sort) {
      String field = order.getProperty();
      String resolved;
      if (computed.containsKey(field)) {
        resolved = field + "_sort";
      } else if (aliases.containsKey(field)) {
        resolved = aliases.get(field);
      } else {
        resolved = field;
      }
      sortDoc.append(resolved, order.isAscending() ? 1 : -1);
    }

    return context -> new Document("$sort", sortDoc);
  }
}
