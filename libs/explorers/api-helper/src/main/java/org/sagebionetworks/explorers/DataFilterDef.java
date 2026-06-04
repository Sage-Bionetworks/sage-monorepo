package org.sagebionetworks.explorers;

import java.util.List;
import java.util.function.Function;

/**
 * Defines a single data filter for a comparison-tool query.
 *
 * <p>A data filter applies {@code $in} matching on a MongoDB field when the accessor returns a
 * non-empty list from the query DTO.
 *
 * @param mongoField the MongoDB field name to filter on (e.g. "age", "model_type")
 * @param accessor function extracting the filter values from the query DTO
 * @param <Q> the query DTO type
 */
public record DataFilterDef<Q>(String mongoField, Function<Q, List<?>> accessor) {}
