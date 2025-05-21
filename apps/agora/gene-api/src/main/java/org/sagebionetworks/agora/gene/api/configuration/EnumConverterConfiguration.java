package org.sagebionetworks.agora.gene.api.configuration;

import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileProteinProfilingMethodDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileProteinSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaModelDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.SortDirectionDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.differentialExpressionProfileProteinProfilingMethodConverter")
    Converter<String, DifferentialExpressionProfileProteinProfilingMethodDto> differentialExpressionProfileProteinProfilingMethodConverter() {
        return new Converter<String, DifferentialExpressionProfileProteinProfilingMethodDto>() {
            @Override
            public DifferentialExpressionProfileProteinProfilingMethodDto convert(String source) {
                return DifferentialExpressionProfileProteinProfilingMethodDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.differentialExpressionProfileProteinSortConverter")
    Converter<String, DifferentialExpressionProfileProteinSortDto> differentialExpressionProfileProteinSortConverter() {
        return new Converter<String, DifferentialExpressionProfileProteinSortDto>() {
            @Override
            public DifferentialExpressionProfileProteinSortDto convert(String source) {
                return DifferentialExpressionProfileProteinSortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.differentialExpressionProfileRnaModelConverter")
    Converter<String, DifferentialExpressionProfileRnaModelDto> differentialExpressionProfileRnaModelConverter() {
        return new Converter<String, DifferentialExpressionProfileRnaModelDto>() {
            @Override
            public DifferentialExpressionProfileRnaModelDto convert(String source) {
                return DifferentialExpressionProfileRnaModelDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.differentialExpressionProfileRnaSortConverter")
    Converter<String, DifferentialExpressionProfileRnaSortDto> differentialExpressionProfileRnaSortConverter() {
        return new Converter<String, DifferentialExpressionProfileRnaSortDto>() {
            @Override
            public DifferentialExpressionProfileRnaSortDto convert(String source) {
                return DifferentialExpressionProfileRnaSortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.sortDirectionConverter")
    Converter<String, SortDirectionDto> sortDirectionConverter() {
        return new Converter<String, SortDirectionDto>() {
            @Override
            public SortDirectionDto convert(String source) {
                return SortDirectionDto.fromValue(source);
            }
        };
    }

}
