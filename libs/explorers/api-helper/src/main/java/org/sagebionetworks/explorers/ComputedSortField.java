package org.sagebionetworks.explorers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

/**
 * Bundles a MongoDB sort expression with its prerequisite pipeline stages.
 *
 * <p>When a sort expression references a field that must be computed by an earlier aggregation
 * stage, declare the prerequisite alongside the expression via
 * {@link #withPrerequisite(AggregationOperation)}. The base class automatically emits prerequisites
 * before the computed-sort {@code $addFields} stage, only when the user's sort actually requests
 * that field.
 *
 * @param expression the MongoDB expression that produces a sortable scalar (e.g.
 *     {@code new Document("$toLower", "$field")})
 * @param prerequisites aggregation stages that must run before this expression is evaluated
 */
public record ComputedSortField(Object expression, List<AggregationOperation> prerequisites) {

  /**
   * Creates a computed sort field with no prerequisites.
   *
   * @param expression the MongoDB expression
   * @return a computed sort field
   */
  public static ComputedSortField of(Object expression) {
    return new ComputedSortField(expression, List.of());
  }

  /**
   * Returns a new computed sort field with an additional prerequisite.
   *
   * @param prereq the prerequisite stage to add
   * @return a new computed sort field with the prerequisite appended
   */
  public ComputedSortField withPrerequisite(AggregationOperation prereq) {
    List<AggregationOperation> newPrereqs = new ArrayList<>(prerequisites);
    newPrereqs.add(prereq);
    return new ComputedSortField(expression, List.copyOf(newPrereqs));
  }
}
