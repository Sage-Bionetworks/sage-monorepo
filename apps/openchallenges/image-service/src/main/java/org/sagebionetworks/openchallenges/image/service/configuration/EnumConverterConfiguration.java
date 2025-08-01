package org.sagebionetworks.openchallenges.image.service.configuration;

import org.sagebionetworks.openchallenges.image.service.model.dto.ImageAspectRatioDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageHeightDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration(value = "org.sagebionetworks.openchallenges.image.service.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.openchallenges.image.service.configuration.EnumConverterConfiguration.imageAspectRatioConverter")
    Converter<String, ImageAspectRatioDto> imageAspectRatioConverter() {
        return new Converter<String, ImageAspectRatioDto>() {
            @Override
            public ImageAspectRatioDto convert(String source) {
                return ImageAspectRatioDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.openchallenges.image.service.configuration.EnumConverterConfiguration.imageHeightConverter")
    Converter<String, ImageHeightDto> imageHeightConverter() {
        return new Converter<String, ImageHeightDto>() {
            @Override
            public ImageHeightDto convert(String source) {
                return ImageHeightDto.fromValue(source);
            }
        };
    }

}
