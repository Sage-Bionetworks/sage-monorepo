package org.sagebionetworks.explorers;

import java.util.function.IntFunction;
import org.springframework.core.convert.converter.Converter;

/**
 * Builds Spring {@link Converter} instances that parse a query-string integer (e.g. {@code "1"} /
 * {@code "-1"}) into an OpenAPI-generated {@code SortOrdersEnum}.
 *
 * <p>OpenAPI Generator only emits {@link Converter} beans for string-based enums, so each app
 * must register its own converters for the integer-valued {@code SortOrdersEnum} types. The
 * conversion logic is identical across apps and enums; only the target type differs. Pass the
 * enum's generated {@code fromValue(Integer)} method reference to get a ready-to-register bean.
 *
 * <pre>{@code
 * @Bean
 * Converter<String, FooQueryDto.SortOrdersEnum> fooSortOrdersConverter() {
 *   return SortOrdersConverterFactory.from(FooQueryDto.SortOrdersEnum::fromValue);
 * }
 * }</pre>
 */
public final class SortOrdersConverterFactory {

  private SortOrdersConverterFactory() {}

  public static <E> Converter<String, E> from(IntFunction<E> fromValue) {
    return source -> fromValue.apply(Integer.parseInt(source));
  }
}
