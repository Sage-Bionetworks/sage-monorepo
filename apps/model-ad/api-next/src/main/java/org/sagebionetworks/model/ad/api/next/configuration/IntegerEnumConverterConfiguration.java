package org.sagebionetworks.model.ad.api.next.configuration;

import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
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
    return new Converter<String, ModelOverviewSearchQueryDto.SortOrdersEnum>() {
      @Override
      public ModelOverviewSearchQueryDto.SortOrdersEnum convert(String source) {
        return ModelOverviewSearchQueryDto.SortOrdersEnum.fromValue(Integer.parseInt(source));
      }
    };
  }

  @Bean
  Converter<
    String,
    DiseaseCorrelationSearchQueryDto.SortOrdersEnum
  > diseaseCorrelationSortOrdersConverter() {
    return new Converter<String, DiseaseCorrelationSearchQueryDto.SortOrdersEnum>() {
      @Override
      public DiseaseCorrelationSearchQueryDto.SortOrdersEnum convert(String source) {
        return DiseaseCorrelationSearchQueryDto.SortOrdersEnum.fromValue(Integer.parseInt(source));
      }
    };
  }

  @Bean
  Converter<
    String,
    GeneExpressionSearchQueryDto.SortOrdersEnum
  > geneExpressionSortOrdersConverter() {
    return new Converter<String, GeneExpressionSearchQueryDto.SortOrdersEnum>() {
      @Override
      public GeneExpressionSearchQueryDto.SortOrdersEnum convert(String source) {
        return GeneExpressionSearchQueryDto.SortOrdersEnum.fromValue(Integer.parseInt(source));
      }
    };
  }
}
