package org.sagebionetworks.agora.api.next.configuration;

import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * Custom converters for integer-based enums that are not handled by the generated
 * EnumConverterConfiguration. The OpenAPI Generator creates SortOrdersEnum with integer values
 * (1, -1), but only generates converters for string-based enums.
 *
 * <p>This file is NOT generated and will not be overwritten by OpenAPI Generator.
 */
@Configuration
public class IntegerEnumConverterConfiguration {

  @Bean
  Converter<String, NominatedTargetSearchQueryDto.SortOrdersEnum> nominatedTargetSortOrdersConverter() {
    return new Converter<String, NominatedTargetSearchQueryDto.SortOrdersEnum>() {
      @Override
      public NominatedTargetSearchQueryDto.SortOrdersEnum convert(String source) {
        return NominatedTargetSearchQueryDto.SortOrdersEnum.fromValue(Integer.parseInt(source));
      }
    };
  }
}
