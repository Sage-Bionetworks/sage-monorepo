package org.sagebionetworks.amp.als.dataset.service.configuration;

import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDirectionDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetSortDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.amp.als.dataset.service.configuration.EnumConverterConfiguration.datasetDirectionConverter")
    Converter<String, DatasetDirectionDto> datasetDirectionConverter() {
        return new Converter<String, DatasetDirectionDto>() {
            @Override
            public DatasetDirectionDto convert(String source) {
                return DatasetDirectionDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.amp.als.dataset.service.configuration.EnumConverterConfiguration.datasetSortConverter")
    Converter<String, DatasetSortDto> datasetSortConverter() {
        return new Converter<String, DatasetSortDto>() {
            @Override
            public DatasetSortDto convert(String source) {
                return DatasetSortDto.fromValue(source);
            }
        };
    }

}
