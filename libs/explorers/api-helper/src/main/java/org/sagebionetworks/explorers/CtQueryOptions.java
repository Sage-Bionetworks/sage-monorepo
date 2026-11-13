package org.sagebionetworks.explorers;

import java.util.List;
import org.springframework.lang.Nullable;

/**
 * The per-request options a comparison-tool query is shaped by, beyond its {@link
 * org.springframework.data.mongodb.core.query.Criteria} and {@link
 * org.springframework.data.domain.Pageable}.
 *
 * <p>Built once in a repository's {@code findAll} and passed to both
 * {@link ComparisonToolRepositorySupport#buildCtMatchCriteria(Object, List, CtQueryOptions, String,
 * CtFilterConfig, org.springframework.data.mongodb.core.query.Criteria...)} and
 * {@link ComparisonToolRepositorySupport#executePagedAggregation(
 * org.springframework.data.mongodb.core.query.Criteria,
 * org.springframework.data.domain.Pageable, CtQueryOptions)}, so the two cannot disagree about the
 * request.
 *
 * @param isInclude true when {@code itemFilterType} is INCLUDE, false when EXCLUDE
 * @param remainingBudget how much the caller can still accept, or null for normal pagination
 * @param prebudgetedParentIds parent tokens the caller has already budgeted for, whose further
 *     children are therefore free; never null (an absent value is normalized to an empty list)
 * @param matchParentIdSpace true when {@code items} are matched against the parent item filter
 *     rather than the row item filter. Exactly one is matched, never both
 * @param selectedRowId the row token of the row the caller has selected, or null when the request
 *     doesn't ask whether that row is still in the results; a blank value is normalized to null
 */
public record CtQueryOptions(
  boolean isInclude,
  @Nullable Integer remainingBudget,
  List<String> prebudgetedParentIds,
  boolean matchParentIdSpace,
  @Nullable String selectedRowId
) {
  public CtQueryOptions {
    prebudgetedParentIds = ApiHelper.sanitizeItems(prebudgetedParentIds);
    selectedRowId = selectedRowId == null || selectedRowId.isBlank() ? null : selectedRowId;
  }

  /** Options that don't ask about a selected row. */
  public CtQueryOptions(
    boolean isInclude,
    @Nullable Integer remainingBudget,
    List<String> prebudgetedParentIds,
    boolean matchParentIdSpace
  ) {
    this(isInclude, remainingBudget, prebudgetedParentIds, matchParentIdSpace, null);
  }

  /** These options, also asking whether the given row is still in the results. */
  public CtQueryOptions withSelectedRowId(@Nullable String rowId) {
    return new CtQueryOptions(
      isInclude,
      remainingBudget,
      prebudgetedParentIds,
      matchParentIdSpace,
      rowId
    );
  }

  /**
   * Options for a request with no parent-awareness: {@code items} matched against the row identity
   * space, no prebudgeted parents. This is what the {@code boolean isInclude} overloads on
   * {@link ComparisonToolRepositorySupport} build.
   */
  public static CtQueryOptions rowSpace(boolean isInclude, @Nullable Integer remainingBudget) {
    return new CtQueryOptions(isInclude, remainingBudget, List.of(), false);
  }
}
