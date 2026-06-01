package org.sagebionetworks.model.ad.api.next.configuration;

import org.sagebionetworks.explorers.SortOrdersConverterFactory;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
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
  Converter<String, ModelOverviewSearchQueryDto.SortOrdersEnum> modelOverviewSortOrdersConverter() {
    return SortOrdersConverterFactory.from(ModelOverviewSearchQueryDto.SortOrdersEnum::fromValue);
  }

  @Bean
  Converter<
    String,
    DiseaseCorrelationSearchQueryDto.SortOrdersEnum
  > diseaseCorrelationSortOrdersConverter() {
    return SortOrdersConverterFactory.from(
      DiseaseCorrelationSearchQueryDto.SortOrdersEnum::fromValue
    );
  }

  @Bean
  Converter<
    String,
    TranscriptomicsSearchQueryDto.SortOrdersEnum
  > transcriptomicsSortOrdersConverter() {
    return SortOrdersConverterFactory.from(TranscriptomicsSearchQueryDto.SortOrdersEnum::fromValue);
  }
}
