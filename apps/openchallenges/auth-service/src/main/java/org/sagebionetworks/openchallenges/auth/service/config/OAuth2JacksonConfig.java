package org.sagebionetworks.openchallenges.auth.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

/**
 * Jackson configuration to handle OAuth2 authorization server deserialization.
 * This configuration provides separate ObjectMappers for OAuth2 internal use
 * and clean API responses without type information.
 */
@Configuration
public class OAuth2JacksonConfig {

    /**
     * Primary ObjectMapper for API responses - clean JSON without type information.
     * This is the default ObjectMapper used for REST API responses.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Add JavaTimeModule to handle Java 8 time types like OffsetDateTime
        mapper.registerModule(new JavaTimeModule());
        
        return mapper;
    }

    /**
     * ObjectMapper specifically for OAuth2 authorization server internal use.
     * This allows proper deserialization of RegisteredClient settings stored in the database.
     * This ObjectMapper includes default typing which is needed for OAuth2 client/token settings.
     */
    @Bean
    @Qualifier("oauth2ObjectMapper")
    public ObjectMapper oauth2ObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Add Spring Security Jackson modules
        ClassLoader classLoader = OAuth2JacksonConfig.class.getClassLoader();
        mapper.registerModules(SecurityJackson2Modules.getModules(classLoader));
        
        // Add OAuth2 Authorization Server Jackson module
        mapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        
        // Enable default typing for proper Map deserialization with allowlisted types
        // This is needed for OAuth2 client/token settings but shouldn't affect API responses
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        );
        
        return mapper;
    }
}