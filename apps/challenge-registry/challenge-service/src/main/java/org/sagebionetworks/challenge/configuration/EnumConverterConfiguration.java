package org.sagebionetworks.challenge.configuration;

import org.sagebionetworks.challenge.model.dto.ChallengeDifficultyDto;
import org.sagebionetworks.challenge.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.challenge.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.sagebionetworks.challenge.model.dto.ChallengeSubmissionTypeDto;
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

  @Bean
  Converter<String, ChallengeSubmissionTypeDto> challengeSubmissionTypeConverter() {
    return new Converter<String, ChallengeSubmissionTypeDto>() {
      @Override
      public ChallengeSubmissionTypeDto convert(String source) {
        return ChallengeSubmissionTypeDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, ChallengeSearchQueryDto.SortEnum> challengeSearchQuerySortTypeConverter() {
    return new Converter<String, ChallengeSearchQueryDto.SortEnum>() {
      @Override
      public ChallengeSearchQueryDto.SortEnum convert(String source) {
        return ChallengeSearchQueryDto.SortEnum.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, ChallengeSearchQueryDto.DirectionEnum>
      challengeSearchQueryDirectionTypeConverter() {
    return new Converter<String, ChallengeSearchQueryDto.DirectionEnum>() {
      @Override
      public ChallengeSearchQueryDto.DirectionEnum convert(String source) {
        return ChallengeSearchQueryDto.DirectionEnum.fromValue(source);
      }
    };
  }
}
