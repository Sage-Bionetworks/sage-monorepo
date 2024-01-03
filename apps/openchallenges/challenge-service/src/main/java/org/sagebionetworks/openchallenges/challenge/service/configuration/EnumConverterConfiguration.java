package org.sagebionetworks.openchallenges.challenge.service.configuration;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeInputDataTypeDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeInputDataTypeSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

  @Bean
  Converter<String, ChallengeCategoryDto> challengeCategoryConverter() {
    return new Converter<String, ChallengeCategoryDto>() {
      @Override
      public ChallengeCategoryDto convert(String source) {
        return ChallengeCategoryDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, ChallengeContributionRoleDto> challengeContributionRoleConverter() {
    return new Converter<String, ChallengeContributionRoleDto>() {
      @Override
      public ChallengeContributionRoleDto convert(String source) {
        return ChallengeContributionRoleDto.fromValue(source);
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
  Converter<String, ChallengeInputDataTypeDirectionDto> challengeInputDataTypeDirectionConverter() {
    return new Converter<String, ChallengeInputDataTypeDirectionDto>() {
      @Override
      public ChallengeInputDataTypeDirectionDto convert(String source) {
        return ChallengeInputDataTypeDirectionDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, ChallengeInputDataTypeSortDto> challengeInputDataTypeSortConverter() {
    return new Converter<String, ChallengeInputDataTypeSortDto>() {
      @Override
      public ChallengeInputDataTypeSortDto convert(String source) {
        return ChallengeInputDataTypeSortDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, ChallengePlatformDirectionDto> challengePlatformDirectionConverter() {
    return new Converter<String, ChallengePlatformDirectionDto>() {
      @Override
      public ChallengePlatformDirectionDto convert(String source) {
        return ChallengePlatformDirectionDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, ChallengePlatformSortDto> challengePlatformSortConverter() {
    return new Converter<String, ChallengePlatformSortDto>() {
      @Override
      public ChallengePlatformSortDto convert(String source) {
        return ChallengePlatformSortDto.fromValue(source);
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
