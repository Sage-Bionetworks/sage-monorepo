package org.sagebionetworks.openchallenges.image.service.configuration;

import org.sagebionetworks.openchallenges.image.service.model.dto.ImageSizeOptionDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

  @Bean
  Converter<String, ImageSizeOptionDto> imageSizeOptionConverter() {
    return new Converter<String, ImageSizeOptionDto>() {
      @Override
      public ImageSizeOptionDto convert(String source) {
        return ImageSizeOptionDto.fromValue(source);
      }
    };
  }
}
