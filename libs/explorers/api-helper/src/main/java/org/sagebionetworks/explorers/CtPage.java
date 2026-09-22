package org.sagebionetworks.explorers;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/**
 * A comparison-tool page: an ordinary {@link PageImpl} plus the CT-specific
 * {@code hasRowsForPrebudgetedParents} flag.
 *
 * <p>Extends {@link PageImpl} rather than wrapping it so that widening
 * {@link ComparisonToolRepositorySupport#executePagedAggregation} to return this type costs the
 * existing repositories nothing: they keep returning it through a {@link Page}-declared interface
 * method, and only the ones whose callers need the flag narrow their declared return type.
 *
 * @param <T> the MongoDB document type
 */
public final class CtPage<T> extends PageImpl<T> {

  @Nullable
  private final Boolean hasRowsForPrebudgetedParents;

  public CtPage(
    List<T> content,
    Pageable pageable,
    long total,
    @Nullable Boolean hasRowsForPrebudgetedParents
  ) {
    super(content, pageable, total);
    this.hasRowsForPrebudgetedParents = hasRowsForPrebudgetedParents;
  }

  /**
   * Whether any row in this request's whole match set, not just the returned page, belongs to one
   * of the parents the caller has already budgeted for.
   *
   * <p>{@code null} when the question was not asked: see
   * {@link ComparisonToolRepositorySupport#executePagedAggregation(
   * org.springframework.data.mongodb.core.query.Criteria, Pageable, CtQueryOptions)}.
   */
  @Nullable
  public Boolean getHasRowsForPrebudgetedParents() {
    return hasRowsForPrebudgetedParents;
  }
}
