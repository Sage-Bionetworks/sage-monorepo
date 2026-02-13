package org.sagebionetworks.agora.api.next.configuration;

import org.sagebionetworks.agora.api.next.model.dto.ComparisonToolConfigPageDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration(value = "org.sagebionetworks.agora.api.next.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.agora.api.next.configuration.EnumConverterConfiguration.comparisonToolConfigPageConverter")
    Converter<String, ComparisonToolConfigPageDto> comparisonToolConfigPageConverter() {
        return new Converter<String, ComparisonToolConfigPageDto>() {
            @Override
            public ComparisonToolConfigPageDto convert(String source) {
                return ComparisonToolConfigPageDto.fromValue(source);
            }
        };
    }

}
