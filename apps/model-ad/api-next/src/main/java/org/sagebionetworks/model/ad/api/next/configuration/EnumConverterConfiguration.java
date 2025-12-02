package org.sagebionetworks.model.ad.api.next.configuration;

import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexCohortDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration(value = "org.sagebionetworks.model.ad.api.next.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.model.ad.api.next.configuration.EnumConverterConfiguration.itemFilterTypeQueryConverter")
    Converter<String, ItemFilterTypeQueryDto> itemFilterTypeQueryConverter() {
        return new Converter<String, ItemFilterTypeQueryDto>() {
            @Override
            public ItemFilterTypeQueryDto convert(String source) {
                return ItemFilterTypeQueryDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.model.ad.api.next.configuration.EnumConverterConfiguration.sexCohortConverter")
    Converter<String, SexCohortDto> sexCohortConverter() {
        return new Converter<String, SexCohortDto>() {
            @Override
            public SexCohortDto convert(String source) {
                return SexCohortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.model.ad.api.next.configuration.EnumConverterConfiguration.sexConverter")
    Converter<String, SexDto> sexConverter() {
        return new Converter<String, SexDto>() {
            @Override
            public SexDto convert(String source) {
                return SexDto.fromValue(source);
            }
        };
    }

}
