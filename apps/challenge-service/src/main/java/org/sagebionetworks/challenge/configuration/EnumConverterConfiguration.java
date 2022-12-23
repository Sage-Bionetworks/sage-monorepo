package org.sagebionetworks.challenge.configuration;

import org.sagebionetworks.challenge.model.dto.ChallengeDifficultyDto;
import org.sagebionetworks.challenge.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

  @Bean
  Converter<String, ChallengeDifficultyDto> challengeDifficultyConverter() {
    return new Converter<String, ChallengeDifficultyDto>() {
      @Override
      public ChallengeDifficultyDto convert(String source) {
        return ChallengeDifficultyDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, ChallengeIncentiveDto> challengeIncentiveConverter() {
    return new Converter<String, ChallengeIncentiveDto>() {
      @Override
      public ChallengeIncentiveDto convert(String source) {
        return ChallengeIncentiveDto.fromValue(source);
      }
    };
  }

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
