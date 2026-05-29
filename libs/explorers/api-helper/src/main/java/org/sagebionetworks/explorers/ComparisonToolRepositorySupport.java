package org.sagebionetworks.explorers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
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
 *   $addFields (prerequisites bundled with requested computed sort fields)
 *   $addFields (computed sort fields, from {@link #getComputedSortFieldExpressions()})
 *   $sort       (field names resolved via {@link #getSortFieldAliases()} and the computed map)
 *   $skip / $limit
 * </pre>
 *
 * <p><strong>Field-name convention:</strong> sort field names ({@link Pageable#getSort()} order
 * properties, and the keys of {@link #getComputedSortFieldExpressions()} /
 * {@link #getSortFieldAliases()}) must be the names stored in MongoDB, NOT the Java POJO
 * property names. The {@code $sort} stage is emitted as a raw {@code Document} and bypasses
 * Spring Data's {@link org.springframework.data.mongodb.core.convert.QueryMapper}, so
 * {@code @Field}-aliased property names are not auto-translated — pass the document-side name
 * directly.
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
   * Declarative filter configuration for this comparison-tool repository.
   *
   * <p>Subclasses override to declare their data filters, item filter, and search filter. Used by
   * {@link #buildCtMatchCriteria}.
   *
   * <p>Default implementation throws {@link UnsupportedOperationException}; subclasses that use
   * {@link #buildCtMatchCriteria} must override.
   */
  protected <Q> CtFilterConfig<Q> getFilterConfig() {
    throw new UnsupportedOperationException("Subclasses that use buildCtMatchCriteria must override getFilterConfig()");
  }

  /**
   * Map of sort field → computed sort field (expression + prerequisites).
   *
   * <p>When a user sorts by a key in this map, the prerequisites (if any) are injected first,
   * then an {@code $addFields: {<field>_sort: <expr>}} stage is injected, and the {@code $sort}
   * uses {@code <field>_sort} instead of the raw field. Use this for case-insensitive string sort
   * (via {@link #toLowerExpr(String)}), array-to-string reduction (via
   * {@link #arrayToLoweredStringExpr(String)}), nested-field unwrapping, or any case where the raw
   * field isn't directly sortable.
   *
   * <p>For expressions that reference computed fields, bundle the prerequisite stages via
   * {@link ComputedSortField#withPrerequisite(AggregationOperation)}.
   */
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
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
   * Assembles and executes the standard CT pipeline. Subclasses build {@code matchCriteria} and
   * pass it in along with the {@link Pageable}.
   */
  protected final Page<T> executePagedAggregation(Criteria matchCriteria, Pageable pageable) {
    try {
      List<AggregationOperation> operations = new ArrayList<>();
      operations.add(Aggregation.match(matchCriteria));

      Map<String, ComputedSortField> computedFields = getComputedSortFieldExpressions();
      Map<String, String> aliases = getSortFieldAliases();

      // Inject prerequisites for requested sort fields
      List<AggregationOperation> prerequisites = buildPrerequisites(
        pageable.getSort(),
        computedFields
      );
      operations.addAll(prerequisites);

      // Inject computed sort fields
      AggregationOperation computedSort = buildComputedSortFields(
        pageable.getSort(),
        computedFields
      );
      if (computedSort != null) {
        operations.add(computedSort);
      }

      // Build and add sort operation
      AggregationOperation sort = buildSortOperation(pageable.getSort(), computedFields, aliases);
      if (sort != null) {
        operations.add(sort);
      }

      long skipCount = (long) pageable.getPageNumber() * pageable.getPageSize();
      operations.add(Aggregation.skip(skipCount));
      operations.add(Aggregation.limit(pageable.getPageSize()));

      Aggregation aggregation = Aggregation.newAggregation(operations);
      log.debug("Executing aggregation on collection {}: {}", getCollectionName(), aggregation);
      AggregationResults<T> results = mongoTemplate.aggregate(
        aggregation,
        getCollectionName(),
        getDocumentClass()
      );

      long total = mongoTemplate.count(new Query(matchCriteria), getCollectionName());
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

  /**
   * Builds {@link Criteria} for comparison-tool filtering using declarative configuration.
   *
   * <p>Applies in order:
   *
   * <ol>
   *   <li><strong>Base criteria</strong> — required filters (cluster, tissue, sex_cohort, etc.)
   *   <li><strong>Data filters</strong> — field-level filters (age, model_type, etc.) with
   *       {@code $in} matching
   *   <li><strong>Item filter</strong> — row-level identifier matching (INCLUDE/EXCLUDE):
   *       <ul>
   *         <li>Empty items + INCLUDE → impossible condition (return empty)
   *         <li>Empty items + EXCLUDE → no-op (return all)
   *         <li>Simple item filter → {@code field $in items} or {@code field $nin items}
   *         <li>Composite item filter → parse each item, combine with {@code $or}/{@code $nor}
   *       </ul>
   *   <li><strong>Search filter</strong> — text search (EXCLUDE-only):
   *       <ul>
   *         <li>Comma-separated → exact match (case-insensitive)
   *         <li>Single term → partial regex match (case-insensitive)
   *       </ul>
   * </ol>
   *
   * <p>All criteria are combined with {@code $and}.
   *
   * @param query the query DTO
   * @param items the list of item identifiers
   * @param isInclude true if itemFilterType is INCLUDE, false if EXCLUDE
   * @param search the search string
   * @param config the filter configuration
   * @param baseCriteria required base filters (e.g., cluster, tissue)
   * @param <Q> the query DTO type
   * @return the combined match criteria
   */
  protected <Q> Criteria buildCtMatchCriteria(
    Q query,
    List<String> items,
    boolean isInclude,
    String search,
    CtFilterConfig<Q> config,
    Criteria... baseCriteria
  ) {
    List<Criteria> allCriteria = new ArrayList<>();

    // 1. Add base criteria (required filters)
    allCriteria.addAll(List.of(baseCriteria));

    // 2. Add data filters
    for (DataFilterDef<Q> dataFilter : config.dataFilters()) {
      List<?> values = dataFilter.accessor().apply(query);
      if (values != null && !values.isEmpty()) {
        allCriteria.add(Criteria.where(dataFilter.mongoField()).in(values));
      }
    }

    // 3. Add item filter
    addItemFilterCriteria(items, isInclude, config.itemFilter(), allCriteria);

    // 4. Add search filter (only in EXCLUDE mode)
    if (!isInclude && search != null && !search.trim().isEmpty()) {
      String trimmedSearch = search.trim();
      Criteria searchCriteria = buildSearchCriteria(config.searchFilter().field(), trimmedSearch);
      allCriteria.add(searchCriteria);
    }

    // Combine all with AND; return empty Criteria (match-all) when no filters are active
    if (allCriteria.isEmpty()) {
      return new Criteria();
    }
    return new Criteria().andOperator(allCriteria.toArray(new Criteria[0]));
  }

  /**
   * Builds search criteria for the given field and search term.
   *
   * <p>Default implementation:
   *
   * <ul>
   *   <li>Comma-separated search → exact match (case-insensitive) via
   *       {@link ApiHelper#createCaseInsensitiveFullMatchPatterns}
   *   <li>Single term → partial match (case-insensitive regex)
   * </ul>
   *
   * <p>Subclasses can override to provide custom search logic (e.g. fallback fields or
   * multi-field matching). The {@code field} parameter can be ignored by overrides that manage
   * their own field names.
   *
   * @param field the MongoDB field to search against
   * @param trimmedSearch the trimmed search string (never null or blank)
   * @return the search criteria
   */
  protected Criteria buildSearchCriteria(String field, String trimmedSearch) {
    if (trimmedSearch.contains(",")) {
      // Comma-separated list: exact match (case-insensitive)
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      return Criteria.where(field).in(patterns);
    } else {
      // Single term: partial match (case-insensitive)
      String regex = Pattern.quote(trimmedSearch);
      return Criteria.where(field).regex(regex, "i");
    }
  }

  private static void addItemFilterCriteria(
    List<String> items,
    boolean isInclude,
    ItemFilterDef itemFilter,
    List<Criteria> allCriteria
  ) {
    if (items.isEmpty()) {
      // For INCLUDE mode with empty items, add impossible condition (return empty)
      if (isInclude) {
        allCriteria.add(Criteria.where("_id").is(null));
      }
      // For EXCLUDE mode with empty items, no filtering needed (return all)
      return;
    }

    switch (itemFilter) {
      case ItemFilterDef.Simple simple -> {
        if (isInclude) {
          allCriteria.add(Criteria.where(simple.field()).in(items));
        } else {
          allCriteria.add(Criteria.where(simple.field()).nin(items));
        }
      }
      case ItemFilterDef.Composite composite -> {
        // Parse each item into a Criteria
        List<Criteria> itemCriteriaList = new ArrayList<>();
        for (String item : items) {
          itemCriteriaList.add(composite.parser().apply(item));
        }

        // Combine with $or (INCLUDE) or $nor (EXCLUDE)
        if (isInclude) {
          // Match ANY of the composite identifiers ($or)
          allCriteria.add(new Criteria().orOperator(itemCriteriaList.toArray(new Criteria[0])));
        } else {
          // Exclude ALL of the composite identifiers ($nor)
          allCriteria.add(new Criteria().norOperator(itemCriteriaList.toArray(new Criteria[0])));
        }
      }
    }
  }

  private List<AggregationOperation> buildPrerequisites(
    Sort sort,
    Map<String, ComputedSortField> computedFields
  ) {
    if (sort.isUnsorted() || computedFields.isEmpty()) {
      return List.of();
    }

    Set<AggregationOperation> seen = new LinkedHashSet<>();
    for (Sort.Order order : sort) {
      ComputedSortField field = computedFields.get(order.getProperty());
      if (field != null) {
        seen.addAll(field.prerequisites());
      }
    }
    return new ArrayList<>(seen);
  }

  private AggregationOperation buildComputedSortFields(
    Sort sort,
    Map<String, ComputedSortField> computedFields
  ) {
    if (sort.isUnsorted() || computedFields.isEmpty()) {
      return null;
    }

    Document fields = new Document();
    for (Sort.Order order : sort) {
      ComputedSortField field = computedFields.get(order.getProperty());
      if (field != null) {
        fields.append(order.getProperty() + "_sort", field.expression());
      }
    }

    if (fields.isEmpty()) {
      return null;
    }
    return context -> new Document("$addFields", fields);
  }

  private AggregationOperation buildSortOperation(
    Sort sort,
    Map<String, ComputedSortField> computed,
    Map<String, String> aliases
  ) {
    if (sort.isUnsorted()) {
      return null;
    }

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
