package org.sagebionetworks.openchallenges.auth.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

/**
 * Jackson configuration to handle OAuth2 authorization server deserialization.
 * This configuration addresses Jackson security restrictions when deserializing
 * OAuth2 client settings and token settings from the database.
 */
@Configuration
public class OAuth2JacksonConfig {

    /**
     * Configure ObjectMapper with OAuth2 authorization server Jackson modules.
     * This allows proper deserialization of RegisteredClient settings stored in the database.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Add Spring Security Jackson modules
        ClassLoader classLoader = OAuth2JacksonConfig.class.getClassLoader();
        mapper.registerModules(SecurityJackson2Modules.getModules(classLoader));
        
        // Add OAuth2 Authorization Server Jackson module
        mapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        
        // Enable default typing for proper Map deserialization with allowlisted types
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        );
        
        return mapper;
    }
}
