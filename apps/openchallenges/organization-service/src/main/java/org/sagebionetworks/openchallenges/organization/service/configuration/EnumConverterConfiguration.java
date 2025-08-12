package org.sagebionetworks.openchallenges.organization.service.configuration;

import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationCategoryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDirectionDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSortDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration(value = "org.sagebionetworks.openchallenges.organization.service.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.openchallenges.organization.service.configuration.EnumConverterConfiguration.challengeParticipationRoleConverter")
    Converter<String, ChallengeParticipationRoleDto> challengeParticipationRoleConverter() {
        return new Converter<String, ChallengeParticipationRoleDto>() {
            @Override
            public ChallengeParticipationRoleDto convert(String source) {
                return ChallengeParticipationRoleDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.openchallenges.organization.service.configuration.EnumConverterConfiguration.organizationCategoryConverter")
    Converter<String, OrganizationCategoryDto> organizationCategoryConverter() {
        return new Converter<String, OrganizationCategoryDto>() {
            @Override
            public OrganizationCategoryDto convert(String source) {
                return OrganizationCategoryDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.openchallenges.organization.service.configuration.EnumConverterConfiguration.organizationDirectionConverter")
    Converter<String, OrganizationDirectionDto> organizationDirectionConverter() {
        return new Converter<String, OrganizationDirectionDto>() {
            @Override
            public OrganizationDirectionDto convert(String source) {
                return OrganizationDirectionDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.openchallenges.organization.service.configuration.EnumConverterConfiguration.organizationSortConverter")
    Converter<String, OrganizationSortDto> organizationSortConverter() {
        return new Converter<String, OrganizationSortDto>() {
            @Override
            public OrganizationSortDto convert(String source) {
                return OrganizationSortDto.fromValue(source);
            }
        };
    }

}
