package org.sagebionetworks.openchallenges.challenge.service.configuration;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDifficultyDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
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
  Converter<String, ChallengeDirectionDto> challengeDirectionConverter() {
    return new Converter<String, ChallengeDirectionDto>() {
      @Override
      public ChallengeDirectionDto convert(String source) {
        return ChallengeDirectionDto.fromValue(source);
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
  Converter<String, ChallengeSortDto> challengeSortConverter() {
    return new Converter<String, ChallengeSortDto>() {
      @Override
      public ChallengeSortDto convert(String source) {
        return ChallengeSortDto.fromValue(source);
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

  @Bean
  Converter<String, ChallengeSubmissionTypeDto> challengeSubmissionTypeConverter() {
    return new Converter<String, ChallengeSubmissionTypeDto>() {
      @Override
      public ChallengeSubmissionTypeDto convert(String source) {
        return ChallengeSubmissionTypeDto.fromValue(source);
      }
    };
  }
}
