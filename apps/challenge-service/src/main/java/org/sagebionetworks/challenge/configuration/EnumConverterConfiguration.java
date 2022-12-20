package org.sagebionetworks.challenge.configuration;

import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

  @Bean
  Converter<String, ChallengeStatusDto> challengeStatusConverter() {
    return new Converter<String, ChallengeStatusDto>() {
      @Override
      public ChallengeStatusDto convert(String source) {
        return ChallengeStatusDto.fromValue(source);
      }
    };
  }
}
