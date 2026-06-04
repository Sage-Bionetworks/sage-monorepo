package org.sagebionetworks.explorers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Declarative filter configuration for a comparison-tool repository.
 *
 * <p>Defines:
 *
 * <ul>
 *   <li><strong>Data filters</strong> — field-level filters (age, model_type, etc.) applied with
 *       {@code $in}
 *   <li><strong>Item filter</strong> — row-level identifier matching (INCLUDE/EXCLUDE), either
 *       simple (single field) or composite (multi-field AND-clauses)
 *   <li><strong>Search filter</strong> — text search on a designated field (EXCLUDE-only)
 * </ul>
 *
 * <p>Consumed by {@link ComparisonToolRepositorySupport#buildCtMatchCriteria}.
 *
 * @param dataFilters list of data filter definitions
 * @param itemFilter item filter definition (simple or composite)
 * @param searchFilter search filter definition
 * @param <Q> the query DTO type
 */
public record CtFilterConfig<Q>(
  List<DataFilterDef<Q>> dataFilters,
  ItemFilterDef itemFilter,
  SearchFilterDef searchFilter
) {
  public static <Q> Builder<Q> builder() {
    return new Builder<>();
  }

  /**
   * Builder for {@link CtFilterConfig}.
   *
   * @param <Q> the query DTO type
   */
  public static final class Builder<Q> {

    private final List<DataFilterDef<Q>> dataFilters = new ArrayList<>();
    private ItemFilterDef itemFilter;
    private SearchFilterDef searchFilter;

    private Builder() {}

    /**
     * Adds a data filter.
     *
     * @param mongoField the MongoDB field name to filter on
     * @param accessor function extracting the filter values from the query DTO
     * @return this builder
     */
    public Builder<Q> dataFilter(String mongoField, Function<Q, List<?>> accessor) {
      this.dataFilters.add(new DataFilterDef<>(mongoField, accessor));
      return this;
    }

    /**
     * Sets the item filter to a simple single-field matcher.
     *
     * @param field the MongoDB field name to match items by
     * @return this builder
     */
    public Builder<Q> simpleItemFilter(String field) {
      this.itemFilter = new ItemFilterDef.Simple(field);
      return this;
    }

    /**
     * Sets the item filter to a composite multi-field matcher.
     *
     * @param parser function that parses an item string and returns a Criteria matching that item
     *     (typically a method reference to an identifier DTO's {@code toCriteria()} method)
     * @return this builder
     */
    public Builder<Q> compositeItemFilter(Function<String, Criteria> parser) {
      this.itemFilter = new ItemFilterDef.Composite(parser);
      return this;
    }

    /**
     * Sets the search filter field.
     *
     * @param field the MongoDB field name to search against
     * @return this builder
     */
    public Builder<Q> searchFilter(String field) {
      this.searchFilter = new SearchFilterDef(field);
      return this;
    }

    /**
     * Builds the {@link CtFilterConfig}.
     *
     * @return the configured filter config
     * @throws IllegalStateException if itemFilter or searchFilter is not set
     */
    public CtFilterConfig<Q> build() {
      if (itemFilter == null) {
        throw new IllegalStateException("itemFilter must be set");
      }
      if (searchFilter == null) {
        throw new IllegalStateException("searchFilter must be set");
      }
      return new CtFilterConfig<>(
        List.copyOf(dataFilters),
        itemFilter,
        searchFilter
      );
    }
  }
}
