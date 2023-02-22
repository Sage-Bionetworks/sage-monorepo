package org.sagebionetworks.openchallenges.organization.service.configuration;

import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDirectionDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSortDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

  @Bean
  Converter<String, OrganizationDirectionDto> organizationDirectionConverter() {
    return new Converter<String, OrganizationDirectionDto>() {
      @Override
      public OrganizationDirectionDto convert(String source) {
        return OrganizationDirectionDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, OrganizationSortDto> organizationSortConverter() {
    return new Converter<String, OrganizationSortDto>() {
      @Override
      public OrganizationSortDto convert(String source) {
        return OrganizationSortDto.fromValue(source);
      }
    };
  }
}
