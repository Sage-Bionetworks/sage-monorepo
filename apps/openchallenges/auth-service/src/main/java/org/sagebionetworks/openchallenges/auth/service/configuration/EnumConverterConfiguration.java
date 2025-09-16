package org.sagebionetworks.openchallenges.auth.service.configuration;

import org.sagebionetworks.openchallenges.auth.service.model.dto.AuthScopeDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.UserRoleDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration(value = "org.sagebionetworks.openchallenges.auth.service.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.openchallenges.auth.service.configuration.EnumConverterConfiguration.authScopeConverter")
    Converter<String, AuthScopeDto> authScopeConverter() {
        return new Converter<String, AuthScopeDto>() {
            @Override
            public AuthScopeDto convert(String source) {
                return AuthScopeDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.openchallenges.auth.service.configuration.EnumConverterConfiguration.userRoleConverter")
    Converter<String, UserRoleDto> userRoleConverter() {
        return new Converter<String, UserRoleDto>() {
            @Override
            public UserRoleDto convert(String source) {
                return UserRoleDto.fromValue(source);
            }
        };
    }

}
