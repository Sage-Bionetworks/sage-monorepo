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
 *     children are therefore free; never null (an absent value is normalised to an empty list)
 * @param matchParentIdSpace true when {@code items} are matched against the parent identity space
 *     rather than the row identity space. Exactly one space is matched, never both
 */
public record CtQueryOptions(
  boolean isInclude,
  @Nullable Integer remainingBudget,
  List<String> prebudgetedParentIds,
  boolean matchParentIdSpace
) {
  public CtQueryOptions {
    prebudgetedParentIds = ApiHelper.sanitizeItems(prebudgetedParentIds);
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
