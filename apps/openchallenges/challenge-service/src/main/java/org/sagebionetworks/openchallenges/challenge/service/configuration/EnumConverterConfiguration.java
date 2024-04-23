package org.sagebionetworks.openchallenges.challenge.service.configuration;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptDirectionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptSortDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamSectionDto;
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

  @Bean
  Converter<String, EdamConceptDirectionDto> edamConceptDirectionConverter() {
    return new Converter<String, EdamConceptDirectionDto>() {
      @Override
      public EdamConceptDirectionDto convert(String source) {
        return EdamConceptDirectionDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, EdamConceptSortDto> edamConceptSortConverter() {
    return new Converter<String, EdamConceptSortDto>() {
      @Override
      public EdamConceptSortDto convert(String source) {
        return EdamConceptSortDto.fromValue(source);
      }
    };
  }

  @Bean
  Converter<String, EdamSectionDto> edamSectionConverter() {
    return new Converter<String, EdamSectionDto>() {
      @Override
      public EdamSectionDto convert(String source) {
        return EdamSectionDto.fromValue(source);
      }
    };
  }
}
