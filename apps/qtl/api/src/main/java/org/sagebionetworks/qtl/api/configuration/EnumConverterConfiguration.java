package org.sagebionetworks.qtl.api.configuration;

import org.sagebionetworks.qtl.api.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.qtl.api.model.dto.EffectDirectionDto;
import org.sagebionetworks.qtl.api.model.dto.ItemFilterTypeQueryDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration(value = "org.sagebionetworks.qtl.api.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.qtl.api.configuration.EnumConverterConfiguration.comparisonToolPageConverter")
    Converter<String, ComparisonToolPageDto> comparisonToolPageConverter() {
        return new Converter<String, ComparisonToolPageDto>() {
            @Override
            public ComparisonToolPageDto convert(String source) {
                return ComparisonToolPageDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.qtl.api.configuration.EnumConverterConfiguration.effectDirectionConverter")
    Converter<String, EffectDirectionDto> effectDirectionConverter() {
        return new Converter<String, EffectDirectionDto>() {
            @Override
            public EffectDirectionDto convert(String source) {
                return EffectDirectionDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.qtl.api.configuration.EnumConverterConfiguration.itemFilterTypeQueryConverter")
    Converter<String, ItemFilterTypeQueryDto> itemFilterTypeQueryConverter() {
        return new Converter<String, ItemFilterTypeQueryDto>() {
            @Override
            public ItemFilterTypeQueryDto convert(String source) {
                return ItemFilterTypeQueryDto.fromValue(source);
            }
        };
    }

}
