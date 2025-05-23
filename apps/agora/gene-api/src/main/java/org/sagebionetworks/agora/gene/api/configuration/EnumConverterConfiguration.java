package org.sagebionetworks.agora.gene.api.configuration;

import org.sagebionetworks.agora.gene.api.model.dto.ProteinDifferentialExpressionProfileProfilingMethodDto;
import org.sagebionetworks.agora.gene.api.model.dto.ProteinDifferentialExpressionProfileSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileModelDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.SortDirectionDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.proteinDifferentialExpressionProfileProfilingMethodConverter")
    Converter<String, ProteinDifferentialExpressionProfileProfilingMethodDto> proteinDifferentialExpressionProfileProfilingMethodConverter() {
        return new Converter<String, ProteinDifferentialExpressionProfileProfilingMethodDto>() {
            @Override
            public ProteinDifferentialExpressionProfileProfilingMethodDto convert(String source) {
                return ProteinDifferentialExpressionProfileProfilingMethodDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.proteinDifferentialExpressionProfileSortConverter")
    Converter<String, ProteinDifferentialExpressionProfileSortDto> proteinDifferentialExpressionProfileSortConverter() {
        return new Converter<String, ProteinDifferentialExpressionProfileSortDto>() {
            @Override
            public ProteinDifferentialExpressionProfileSortDto convert(String source) {
                return ProteinDifferentialExpressionProfileSortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.rnaDifferentialExpressionProfileModelConverter")
    Converter<String, RnaDifferentialExpressionProfileModelDto> rnaDifferentialExpressionProfileModelConverter() {
        return new Converter<String, RnaDifferentialExpressionProfileModelDto>() {
            @Override
            public RnaDifferentialExpressionProfileModelDto convert(String source) {
                return RnaDifferentialExpressionProfileModelDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.agora.gene.api.configuration.EnumConverterConfiguration.rnaDifferentialExpressionProfileSortConverter")
    Converter<String, RnaDifferentialExpressionProfileSortDto> rnaDifferentialExpressionProfileSortConverter() {
        return new Converter<String, RnaDifferentialExpressionProfileSortDto>() {
            @Override
            public RnaDifferentialExpressionProfileSortDto convert(String source) {
                return RnaDifferentialExpressionProfileSortDto.fromValue(source);
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
