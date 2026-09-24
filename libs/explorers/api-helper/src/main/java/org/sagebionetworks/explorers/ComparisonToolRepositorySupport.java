package org.sagebionetworks.explorers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;

/**
 * Base class for comparison-tool repository implementations backed by MongoDB aggregation.
 *
 * <p>Subclasses build per-CT {@link Criteria} (filters, search, identifier matching) and delegate
 * pipeline assembly + execution to
 * {@link #executePagedAggregation(Criteria, Pageable, boolean, Integer)}. The pipeline shape is
 * uniform across CTs:
 *
 * <pre>
 *   $match
 *   $addFields (prerequisites bundled with requested computed sort fields)
 *   $addFields (computed sort fields, from {@link #getComputedSortFieldExpressions()})
 *   $sort       (field names resolved via {@link #getSortFieldAliases()} and the computed map)
 *   $limit (row budget) or $skip / $limit
 * </pre>
 *
 * <p>A budgeted request on a <strong>parent-aware</strong> CT — one overriding
 * {@link #getParentItemFilter()} — spends its budget on parents rather than rows, which takes a
 * second query. That query, the <em>parent selection</em>, picks the parents in the request's own
 * sort order; the shape above then runs with its {@code $match} narrowed to those parents' rows
 * and neither {@code $skip} nor {@code $limit}. See
 * {@link #executePagedAggregation(Criteria, Pageable, CtQueryOptions)} and {@link #selectParents}.
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

  private static final Collation CASE_INSENSITIVE = Collation.of("en").strength(2);

  private static final String ID_FIELD = "_id";

  /**
   * Field the parent selection materializes the parent token into before its {@code $group}.
   */
  private static final String PARENT_TOKEN_FIELD = "__ct_parent_token";

  /**
   * Prefix of the safe alias each resolved sort path is read into for the parent selection's
   * {@code $group}.
   */
  private static final String SORT_KEY_ALIAS_PREFIX = "__ct_sort_key_";

  /**
   * Field the parent selection's {@code $group} captures each parent's leading row {@code _id}
   * under, since the group's own {@code _id} is the parent token.
   */
  private static final String LEADING_ROW_ID_FIELD = "__ct_leading_row_id";

  protected final MongoTemplate mongoTemplate;

  protected ComparisonToolRepositorySupport(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  protected abstract String getCollectionName();

  protected abstract Class<T> getDocumentClass();

  /**
   * Declarative filter configuration for this comparison-tool repository.
   *
   * <p>Subclasses override to declare their data filters, item filter, and search filter. Pass the
   * return value as the {@code config} argument to {@link #buildCtMatchCriteria}.
   *
   * <p>Default implementation throws {@link UnsupportedOperationException}; subclasses that use
   * {@link #buildCtMatchCriteria} must override.
   */
  protected <Q> CtFilterConfig<Q> getFilterConfig() {
    throw new UnsupportedOperationException(
      "Subclasses that use buildCtMatchCriteria must override getFilterConfig()"
    );
  }

  /**
   * Map of sort field → computed sort field (expression + prerequisites).
   *
   * <p>When a user sorts by a key in this map, the prerequisites (if any) are injected first,
   * then an {@code $addFields: {<field>_sort: <expr>}} stage is injected, and the {@code $sort}
   * uses {@code <field>_sort} instead of the raw field. Use this for array-to-string reduction
   * (via {@link #arrayToStringExpr(String)}), computed fallback fields, or any case where
   * the raw field isn't directly sortable.
   *
   * <p>Case-insensitive string sorting is handled by pipeline-level collation and does not require
   * a computed sort field. For nested fields or companion numeric fields, use
   * {@link #getSortFieldAliases()} instead.
   *
   * <p>For expressions that reference computed fields, bundle the prerequisite stages via
   * {@link ComputedSortField#withPrerequisite(AggregationOperation)}.
   */
  protected Map<String, ComputedSortField> getComputedSortFieldExpressions() {
    return Map.of();
  }

  /**
   * Map of sort field name → aliased document path for a direct {@code $sort} rename (no
   * {@code $addFields} stage). Two cases require an entry:
   *
   * <ol>
   *   <li><strong>Companion-field redirect</strong> — a separate field already on the document
   *       carries the sortable value (e.g. {@code "age" → "age_numeric"}).
   *   <li><strong>Nested-object columns</strong> — any column whose document value is an object
   *       (rather than a scalar) must be aliased to the specific sub-field to sort on (e.g.
   *       {@code "CBE" → "CBE.correlation"}, {@code "4 months" → "4 months.log2_fc"}). Without the
   *       alias, {@code $sort} operates on the full object and produces undefined ordering. This
   *       applies to any object-valued column — heatmap modules, time-point buckets, or similar.
   * </ol>
   *
   * <p>Subclass overrides must stay in sync with the document schema: whenever a new object-valued
   * column is added, add the corresponding alias here.
   */
  protected Map<String, String> getSortFieldAliases() {
    return Map.of();
  }

  /**
   * The item filter this CT's rows are identified by — the one {@code items} values are matched
   * against unless the request asks for the parent space. Always the item filter in
   * {@link #getFilterConfig()}.
   */
  private ItemFilterDef getRowItemFilter() {
    return getFilterConfig().itemFilter();
  }

  /**
   * The item filter this CT's row <em>parents</em> are identified by, or {@code null} when rows are
   * their own parents.
   *
   * <p>Overriding this is what makes a CT <strong>parent-aware</strong>: several rows then roll up
   * to one parent, so {@code itemIdSpace: parent} matches {@code items} against the parent token
   * and a budget caps distinct parents rather than rows. Leaving it {@code null} keeps a CT
   * <strong>self-parented</strong> — the parent space resolves back to the row space implied by
   * the item filter, and a budget caps rows, since each row is its own parent.
   *
   * <p>The default is {@code null} rather than the row space so that a {@code null} check can tell
   * a parent-aware CT from a self-parented one.
   */
  @Nullable
  protected ItemFilterDef getParentItemFilter() {
    return null;
  }

  private ItemFilterDef resolveParentItemFilter() {
    return Objects.requireNonNullElseGet(getParentItemFilter(), this::getRowItemFilter);
  }

  /**
   * Assembles and executes the standard CT pipeline. Subclasses build {@code matchCriteria} and
   * pass it in along with the {@link Pageable}.
   *
   * <p>When {@code remainingBudget} is non-null and {@code isInclude} is false, the pipeline emits
   * a single {@code $limit} derived from the budget and no {@code $skip} -- the caller gets
   * matching rows in sort order from across all pages rather than only the page it is displaying.
   * The budget is ignored on INCLUDE queries, which already ask for a known set of items, and when
   * it is null. A budget of zero returns no row through this overload, which names no prebudgeted
   * parents whose rows would be free.
   *
   * <p>A budgeted result is deliberately <strong>not</strong> shaped like a page: it is returned
   * over {@link Pageable#unpaged(Sort)}, so {@link Page#getNumber()} is 0 and
   * {@link Page#getSize()} is the number of rows actually returned. Reporting the requested page
   * number and size instead would misstate the result, and would make {@link PageImpl} rewrite the
   * total whenever the requested offset exceeded the true match count.
   *
   * <p>The total element count is unaffected by the budget, so callers can detect truncation by
   * comparing {@link Page#getTotalElements()} against the number of returned rows. Callers must not
   * assume the number of returned rows equals the budget: the budget caps rows today, but it is
   * defined as "how much the caller can still accept", and a future revision may count something
   * else (e.g. distinct parent entities, each of which can contribute several rows).
   *
   * @param matchCriteria the assembled match criteria
   * @param pageable pagination and sort; pagination is ignored when the budget applies
   * @param isInclude true if itemFilterType is INCLUDE, false if EXCLUDE
   * @param remainingBudget how many more rows the caller can accept, or null for normal pagination
   */
  protected final CtPage<T> executePagedAggregation(
    Criteria matchCriteria,
    Pageable pageable,
    boolean isInclude,
    Integer remainingBudget
  ) {
    return executePagedAggregation(
      matchCriteria,
      pageable,
      CtQueryOptions.rowSpace(isInclude, remainingBudget)
    );
  }

  /**
   * Assembles and executes the standard CT pipeline for a request carrying
   * {@link CtQueryOptions}. Budgeting and paging behave exactly as documented on
   * {@link #executePagedAggregation(Criteria, Pageable, boolean, Integer)}; this overload
   * additionally answers {@code hasRowsForPrebudgetedParents} on the returned {@link CtPage}.
   *
   * <p>On a <strong>parent-aware</strong> CT a budget caps distinct parents instead of rows, so a
   * budgeted request first selects the parents it admits (see
   * {@link #buildAdmittedParentsCriteria}) and then runs the row pipeline over their rows alone,
   * unpaged: every row of an admitted parent is wanted. The total count still comes from the
   * caller's unnarrowed criteria, so a caller's skip count stays
   * {@code totalElements - rows.length} exactly as in the row-capped case.
   *
   * <p>A budget of zero admits only the prebudgeted parents on <em>every</em> CT, since there is
   * nothing to select and so no parent token is needed. On a self-parented CT that is usually no
   * rows, since an exclude request already removes the rows the caller holds through
   * {@code items}.
   *
   * @param matchCriteria the assembled match criteria
   * @param pageable pagination and sort; pagination is ignored when the budget applies
   * @param options the request's include/exclude, budget, prebudgeted parents, and item id space
   */
  protected final CtPage<T> executePagedAggregation(
    Criteria matchCriteria,
    Pageable pageable,
    CtQueryOptions options
  ) {
    try {
      BudgetMode budgetMode = resolveBudgetMode(options);
      // When the budget caps parents, first run a separate query to pick the admitted parents, then
      // narrow the match to their rows. Otherwise use the match criteria as-is.
      Criteria rowCriteria = budgetMode == BudgetMode.CAPS_PARENTS
        ? buildAdmittedParentsCriteria(matchCriteria, pageable.getSort(), options)
        : matchCriteria;

      List<AggregationOperation> operations = new ArrayList<>();
      operations.add(Aggregation.match(rowCriteria));

      Map<String, ComputedSortField> computedFields = getComputedSortFieldExpressions();
      Map<String, String> aliases = getSortFieldAliases();

      operations.addAll(buildSortPrepStages(pageable.getSort(), computedFields, aliases));

      // Build and add sort operation
      AggregationOperation sort = buildSortOperation(pageable.getSort(), computedFields, aliases);
      if (sort != null) {
        operations.add(sort);
      }

      operations.addAll(
        switch (budgetMode) {
          case NONE -> List.of(
            Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
            Aggregation.limit(pageable.getPageSize())
          );
          case CAPS_ROWS -> List.of(Aggregation.limit(options.remainingBudget()));
          // No $skip or $limit: the $match already names the admitted parents, and every row of
          // each is wanted
          case CAPS_PARENTS -> List.of();
        }
      );

      // Permits disk spillover when the $sort working set exceeds 100MB; only activates
      // when needed -- required for deep pagination on large collections
      Aggregation aggregation = Aggregation.newAggregation(operations).withOptions(
        AggregationOptions.builder().allowDiskUse(true).collation(CASE_INSENSITIVE).build()
      );
      log.debug("Executing aggregation on collection {}: {}", getCollectionName(), aggregation);
      AggregationResults<T> results = mongoTemplate.aggregate(
        aggregation,
        getCollectionName(),
        getDocumentClass()
      );

      long total = mongoTemplate.count(
        new Query(matchCriteria).collation(CASE_INSENSITIVE),
        getCollectionName()
      );
      Pageable resultPageable = budgetMode == BudgetMode.NONE
        ? pageable
        : Pageable.unpaged(pageable.getSort());
      return new CtPage<>(
        results.getMappedResults(),
        resultPageable,
        total,
        findRowsForPrebudgetedParents(matchCriteria, options)
      );
    } catch (Exception e) {
      log.error("Error executing aggregation on collection {}", getCollectionName(), e);
      throw e;
    }
  }

  /** What a request's budget caps, which decides how its row pipeline ends. */
  private enum BudgetMode {
    /** No budget applies: the requested page, by {@code $skip} / {@code $limit}. */
    NONE,
    /** The budget caps rows with a bare {@code $limit}, from across all pages. */
    CAPS_ROWS,
    /**
     * The budget caps distinct parents: the {@code $match} names the admitted ones, and every row
     * of each is returned, so there is no {@code $skip} and no {@code $limit}.
     */
    CAPS_PARENTS,
  }

  /**
   * A budget applies only to an EXCLUDE request that sets one. Above zero it caps rows unless the
   * CT is parent-aware, since selecting parents needs a parent token and a self-parented CT
   * identified by a composite item filter has none. At zero there is nothing to select, so every
   * CT admits the prebudgeted parents alone.
   */
  private BudgetMode resolveBudgetMode(CtQueryOptions options) {
    if (options.isInclude() || options.remainingBudget() == null) {
      return BudgetMode.NONE;
    }
    if (options.remainingBudget() > 0 && getParentItemFilter() == null) {
      return BudgetMode.CAPS_ROWS;
    }
    return BudgetMode.CAPS_PARENTS;
  }

  /**
   * Answers whether any row in the match set belongs to one of {@code prebudgetedParentIds}.
   * Only the whole match set can answer that, so no single page can.
   *
   * <p>Returns {@code null} when the question was not asked: no prebudgeted parents, or an INCLUDE
   * request. An INCLUDE already names the items the caller holds, so a prebudgeted parent list
   * cannot tell it anything it does not know.
   *
   * <p>Deliberately a third Mongo call rather than part of the existing two. The aggregation sees
   * one page, {@code count(matchCriteria)} carries no parent clause and is needed as-is for
   * {@code totalElements}, and folding the answer into the pipeline would want {@code $facet},
   * which DocumentDB does not support. Being a plain query, it reads <strong>stored fields
   * only</strong> — the same constraint that shapes {@link #buildSearchCriteria} overrides, since
   * computed {@code $addFields} values do not exist outside the aggregation.
   */
  @Nullable
  private Boolean findRowsForPrebudgetedParents(Criteria matchCriteria, CtQueryOptions options) {
    List<String> prebudgetedParentIds = options.prebudgetedParentIds();
    if (options.isInclude() || prebudgetedParentIds.isEmpty()) {
      return null;
    }

    Criteria criteria = new Criteria()
      .andOperator(matchCriteria, resolveParentItemFilter().criteriaForAny(prebudgetedParentIds));
    return mongoTemplate.exists(
      new Query(criteria).collation(CASE_INSENSITIVE),
      getCollectionName()
    );
  }

  /**
   * Narrows {@code matchCriteria} to the rows of the parents a budgeted request admits: the ones
   * the caller has already budgeted for, plus up to {@code remainingBudget} more, selected in the
   * request's own sort order by {@link #selectParents}.
   *
   * <p>A budget of zero admits no new parent — there is nothing to select, and {@code $limit: 0}
   * is illegal — so it skips the selection and matches the prebudgeted parents alone. With
   * no prebudgeted parents either, the criteria match nothing rather than everything, which is what
   * an exhausted budget means:
   * {@link ItemFilterDef#criteriaForAny(java.util.Collection) criteriaForAny} of no parents
   * matches no rows under either item filter variant.
   *
   * <p>A row with no parent token is never admitted, yet still counts towards
   * {@code totalElements}, so the caller sees it as truncated however large the budget.
   */
  private Criteria buildAdmittedParentsCriteria(
    Criteria matchCriteria,
    Sort sort,
    CtQueryOptions options
  ) {
    Set<String> admitted = new LinkedHashSet<>(options.prebudgetedParentIds());
    if (options.remainingBudget() > 0) {
      admitted.addAll(selectParents(matchCriteria, sort, options));
    }
    return new Criteria()
      .andOperator(matchCriteria, resolveParentItemFilter().criteriaForAny(admitted));
  }

  /**
   * Selects the first {@code remainingBudget} parents in the request's sort order, among those the
   * caller has not already budgeted for.
   *
   * <p>Excluding the prebudgeted parents is what makes the budget mean "new parents". Without it a
   * prebudgeted parent high in the sort order would consume part of the budget, even though its
   * children are already returned.
   *
   * <p>The pipeline replays the row pipeline's sort stages, materializes the parent token plus a
   * safe alias per resolved sort path, drops rows with no parent token, sorts rows, collapses them
   * to one document per parent keeping its leading row's sort values, and re-applies the same order
   * to the parents. It is a query of its own because DocumentDB has no {@code $setWindowFields},
   * so per-parent admission cannot be expressed inside the row pipeline.
   */
  private List<String> selectParents(Criteria matchCriteria, Sort sort, CtQueryOptions options) {
    // Exclude the rows of prebudgeted parents
    ItemFilterDef parentItemFilter = resolveParentItemFilter();
    List<String> prebudgeted = options.prebudgetedParentIds();
    Criteria selectionCriteria = prebudgeted.isEmpty()
      ? matchCriteria
      : new Criteria().andOperator(matchCriteria, parentItemFilter.criteriaForNone(prebudgeted));

    // Build the row pipeline's sort, plus a safe alias for each sort path
    Map<String, ComputedSortField> computedFields = getComputedSortFieldExpressions();
    Map<String, String> aliases = getSortFieldAliases();
    Document rowSortDoc = buildSortDoc(sort, computedFields, aliases);
    Map<String, String> sortKeyAliases = buildSortKeyAliases(sort, rowSortDoc);

    // Match rows and add the same sort prep fields as the row pipeline
    List<AggregationOperation> operations = new ArrayList<>();
    operations.add(Aggregation.match(selectionCriteria));
    operations.addAll(buildSortPrepStages(sort, computedFields, aliases));

    // Add each row's parent token and aliased sort values
    Document tokenFields = buildParentTokenFields(sortKeyAliases, parentItemFilter);
    operations.add(context -> new Document("$addFields", tokenFields));
    // A null token names no parent a caller could ask for, so it must not take a budget slot.
    // Only a stored space can emit one: a composite token guards every part with MISSING_PART.
    operations.add(Aggregation.match(Criteria.where(PARENT_TOKEN_FIELD).ne(null)));

    // Sort rows so each parent's first row is its leading row
    if (rowSortDoc != null) {
      operations.add(sortStage(rowSortDoc));
    }

    // Collapse to one document per parent, keeping its leading row's sort values
    Document parentSortDoc = buildParentSortDoc(rowSortDoc, sortKeyAliases);
    Document groupDoc = buildParentGroupDoc(parentSortDoc);
    operations.add(context -> new Document("$group", groupDoc));

    // Rank parents in the same order and take the budget
    operations.add(sortStage(parentSortDoc));
    operations.add(Aggregation.limit(options.remainingBudget()));

    Aggregation aggregation = Aggregation.newAggregation(operations).withOptions(
      AggregationOptions.builder().allowDiskUse(true).collation(CASE_INSENSITIVE).build()
    );
    log.debug("Selecting parents on collection {}: {}", getCollectionName(), aggregation);
    return mongoTemplate
      .aggregate(aggregation, getCollectionName(), Document.class)
      .getMappedResults()
      .stream()
      .map(parent -> parent.getString(ID_FIELD))
      .toList();
  }

  /**
   * The {@code $addFields} stage the parent selection groups and sorts on: the parent token, plus
   * each resolved sort path read into its alias. The paths need aliasing because they contain dots
   * and spaces ({@code 4 months.log2_fc}), neither of which a {@code $group} output key nor
   * {@code $first: "$<path>"} can express.
   */
  private static Document buildParentTokenFields(
    Map<String, String> sortKeyAliases,
    ItemFilterDef parentItemFilter
  ) {
    Document fields = new Document(PARENT_TOKEN_FIELD, parentItemFilter.tokenExpression());
    sortKeyAliases.forEach((path, alias) -> fields.append(alias, ApiHelper.buildPathReadExpr(path))
    );
    return fields;
  }

  /**
   * Maps each resolved sort path in {@code rowSortDoc} to the alias the parent selection reads it
   * into. The isEmpty flag keys and {@code _id} are left out on purpose: the flags are already
   * space- and dot-normalized by {@link ApiHelper#isEmptyFlagKey}, so they can be captured under
   * their own names, and {@code _id} needs no read since {@code $first} can take it directly.
   */
  private static Map<String, String> buildSortKeyAliases(Sort sort, @Nullable Document rowSortDoc) {
    if (rowSortDoc == null) {
      return Map.of();
    }

    Set<String> flagKeys = new LinkedHashSet<>();
    sort.forEach(order -> flagKeys.add(ApiHelper.isEmptyFlagKey(order.getProperty())));

    Map<String, String> sortKeyAliases = new LinkedHashMap<>();
    for (String key : rowSortDoc.keySet()) {
      if (!ID_FIELD.equals(key) && !flagKeys.contains(key)) {
        sortKeyAliases.put(key, SORT_KEY_ALIAS_PREFIX + sortKeyAliases.size());
      }
    }
    return sortKeyAliases;
  }

  /**
   * The parent selection's post-{@code $group} sort: the row sort document with every key replaced
   * by the key it was captured under, so parents come out ordered exactly as their leading rows
   * are.
   *
   * <p>The isEmpty flags are part of that. Reproduce only the resolved paths and a parent whose
   * sort value is null or empty floats to the head of the selection while its rows sit at the tail
   * of the result, so the admitted parents are not the ones the user sees.
   *
   * <p>So is the row sort's {@code _id} tiebreaker. After the {@code $group}, {@code _id} holds the
   * parent token, so sorting on it would break a tie on every real sort key by token while rows
   * break it by row id. A tie straddling the budget would then admit a different parent than the
   * row order puts first. The leading row's {@code _id} is sorted on instead, and since it is
   * unique per parent, no tie is left for the token to break.
   *
   * <p>An unsorted request has no row order to reproduce, so parents are ordered by their token,
   * which still makes the admitted set deterministic.
   */
  private static Document buildParentSortDoc(
    @Nullable Document rowSortDoc,
    Map<String, String> sortKeyAliases
  ) {
    if (rowSortDoc == null) {
      return new Document(ID_FIELD, 1);
    }

    Document parentSortDoc = new Document();
    rowSortDoc.forEach((key, direction) -> {
      String parentKey = ID_FIELD.equals(key)
        ? LEADING_ROW_ID_FIELD
        : sortKeyAliases.getOrDefault(key, key);
      parentSortDoc.append(parentKey, direction);
    });
    return parentSortDoc;
  }

  /**
   * The parent selection's {@code $group}: one document per parent, keyed by the parent token and
   * carrying its leading row's value for every key {@code parentSortDoc} names.
   *
   * <p>The key is deliberately the bare token rather than a tuple of the constituent fields, so an
   * unsorted selection orders parents by comparing strings, and each selected parent reads back as
   * the token a caller would send.
   */
  private static Document buildParentGroupDoc(Document parentSortDoc) {
    Document group = new Document(ID_FIELD, "$" + PARENT_TOKEN_FIELD);
    parentSortDoc.forEach((key, direction) -> {
      if (LEADING_ROW_ID_FIELD.equals(key)) {
        group.append(key, new Document("$first", "$" + ID_FIELD));
      } else if (!ID_FIELD.equals(key)) {
        group.append(key, new Document("$first", "$" + key));
      }
    });
    return group;
  }

  /**
   * Reduces an array field into a NUL-separated string for sorting. Avoids MongoDB's "parallel
   * arrays" limitation. Case-insensitive comparison is handled by pipeline-level collation.
   */
  protected static Object arrayToStringExpr(String arrayField) {
    Document reduce = new Document(
      "$reduce",
      new Document()
        .append("input", "$" + arrayField)
        .append("initialValue", "")
        .append("in", new Document("$concat", List.of("$$value", "\u0000", "$$this")))
    );
    return reduce;
  }

  /**
   * Builds {@link Criteria} for comparison-tool filtering using declarative configuration.
   *
   * <p>Applies in order:
   *
   * <ol>
   *   <li><strong>Base criteria</strong> — required filters (cluster, tissue, etc.)
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
    return buildCtMatchCriteria(
      query,
      items,
      CtQueryOptions.rowSpace(isInclude, null),
      search,
      config,
      baseCriteria
    );
  }

  /**
   * Builds {@link Criteria} for comparison-tool filtering, honoring the item id space the request
   * asked for. Filtering is otherwise identical to
   * {@link #buildCtMatchCriteria(Object, List, boolean, String, CtFilterConfig, Criteria...)}.
   *
   * <p>{@code items} are matched against <strong>exactly one</strong> item filter, never an
   * {@code OR} of both: the parent item filter ({@link #getParentItemFilter()}, falling back to the
   * row item filter on a self-parented CT) when {@link CtQueryOptions#matchParentIdSpace()} is set,
   * and the row item filter otherwise.
   *
   * @param query the query DTO
   * @param items the list of item identifiers
   * @param options the request's include/exclude and item id space
   * @param search the search string
   * @param config the filter configuration
   * @param baseCriteria required base filters (e.g., cluster, tissue)
   * @param <Q> the query DTO type
   * @return the combined match criteria
   */
  protected <Q> Criteria buildCtMatchCriteria(
    Q query,
    List<String> items,
    CtQueryOptions options,
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
    addItemFilterCriteria(items, options, allCriteria);

    // 4. Add search filter (only in EXCLUDE mode)
    if (!options.isInclude() && search != null && !search.trim().isEmpty()) {
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
   *       {@link ApiHelper#createCaseInsensitiveFullMatchPatterns(String)}
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

  private void addItemFilterCriteria(
    List<String> items,
    CtQueryOptions options,
    List<Criteria> allCriteria
  ) {
    if (items.isEmpty()) {
      // For INCLUDE mode with empty items, match nothing (return empty)
      if (options.isInclude()) {
        allCriteria.add(ApiHelper.matchNothing());
      }
      // For EXCLUDE mode with empty items, no filtering needed (return all)
      return;
    }

    ItemFilterDef itemFilter = options.matchParentIdSpace()
      ? resolveParentItemFilter()
      : getRowItemFilter();
    allCriteria.add(
      options.isInclude() ? itemFilter.criteriaForAny(items) : itemFilter.criteriaForNone(items)
    );
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

  /**
   * The {@code $addFields} stages that must precede {@code $sort}, in order. Shared by the row
   * pipeline and the parent selection so the two cannot order their results differently.
   */
  private List<AggregationOperation> buildSortPrepStages(
    Sort sort,
    Map<String, ComputedSortField> computedFields,
    Map<String, String> aliases
  ) {
    List<AggregationOperation> operations = new ArrayList<>();

    // Inject prerequisites for requested sort fields
    List<AggregationOperation> prerequisites = buildPrerequisites(sort, computedFields);
    operations.addAll(prerequisites);

    // Inject computed sort fields
    AggregationOperation computedSort = buildComputedSortFields(sort, computedFields);
    if (computedSort != null) {
      operations.add(computedSort);
    }

    // Inject empty-flag fields so null/empty values always sort last regardless of direction
    AggregationOperation emptyFlags = ApiHelper.buildEmptyFlagFields(sort, aliases);
    if (emptyFlags != null) {
      operations.add(emptyFlags);
    }

    return operations;
  }

  private AggregationOperation buildSortOperation(
    Sort sort,
    Map<String, ComputedSortField> computed,
    Map<String, String> aliases
  ) {
    Document sortDoc = buildSortDoc(sort, computed, aliases);
    return sortDoc == null ? null : sortStage(sortDoc);
  }

  @Nullable
  private Document buildSortDoc(
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
        log.debug("Resolved sort alias: '{}' -> '{}'", field, resolved);
      } else {
        resolved = field;
      }
      sortDoc.append(ApiHelper.isEmptyFlagKey(field), 1);
      sortDoc.append(resolved, order.isAscending() ? 1 : -1);
    }
    // Break ties on _id so equal sort values keep a stable order across pages. Skip when the
    // caller already sorts by _id -- re-appending would overwrite their direction.
    if (!sortDoc.containsKey(ID_FIELD)) {
      sortDoc.append(ID_FIELD, 1);
    }

    return sortDoc;
  }

  private static AggregationOperation sortStage(Document sortDoc) {
    return context -> new Document("$sort", sortDoc);
  }
}
