package org.sagebionetworks.openchallenges.auth.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service for generating OAuth2 tokens using Spring OAuth2 Authorization Server.
 * 
 * This service creates JWT tokens that are compatible with the OAuth2 Authorization Server
 * and can be validated using the JWK Set endpoint.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2TokenGeneratorService {

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final RegisteredClientRepository registeredClientRepository;

    /**
     * Generate an access token for the given user using OAuth2 Authorization Server.
     *
     * @param user The user for whom to generate the token
     * @return The generated JWT access token
     */
    public String generateAccessToken(User user) {
        log.debug("Generating OAuth2 access token for user: {}", user.getUsername());

        // Get the registered client (configured in OAuth2 Authorization Server)
        RegisteredClient registeredClient = registeredClientRepository.findByClientId("openchallenges-client");
        if (registeredClient == null) {
            throw new RuntimeException("Registered client 'openchallenges-client' not found");
        }

        // Create authentication object for the user
        Authentication authentication = createUserAuthentication(user);

        // Build OAuth2 token context
        OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(authentication)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizedScopes(registeredClient.getScopes())
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrant(authentication)
                .build();

        // Generate the token
        OAuth2Token token = tokenGenerator.generate(tokenContext);
        if (token == null || !(token instanceof OAuth2AccessToken)) {
            throw new RuntimeException("Failed to generate OAuth2 access token");
        }

        OAuth2AccessToken accessToken = (OAuth2AccessToken) token;
        log.debug("Successfully generated OAuth2 access token for user: {}", user.getUsername());
        return accessToken.getTokenValue();
    }

    /**
     * Generate a refresh token for the given user using OAuth2 Authorization Server.
     *
     * @param user The user for whom to generate the refresh token
     * @return The generated refresh token
     */
    public String generateRefreshToken(User user) {
        log.debug("Generating OAuth2 refresh token for user: {}", user.getUsername());

        // Get the registered client
        RegisteredClient registeredClient = registeredClientRepository.findByClientId("openchallenges-client");
        if (registeredClient == null) {
            throw new RuntimeException("Registered client 'openchallenges-client' not found");
        }

        // Create authentication object for the user
        Authentication authentication = createUserAuthentication(user);

        // Build OAuth2 token context for refresh token
        OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(authentication)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizedScopes(registeredClient.getScopes())
                .tokenType(OAuth2TokenType.REFRESH_TOKEN)
                .authorizationGrant(authentication)
                .build();

        // Generate the refresh token
        OAuth2Token token = tokenGenerator.generate(tokenContext);
        if (token == null || !(token instanceof OAuth2RefreshToken)) {
            throw new RuntimeException("Failed to generate OAuth2 refresh token");
        }

        OAuth2RefreshToken refreshToken = (OAuth2RefreshToken) token;
        log.debug("Successfully generated OAuth2 refresh token for user: {}", user.getUsername());
        return refreshToken.getTokenValue();
    }

    /**
     * Get access token expiration time in seconds.
     *
     * @return Access token expiration in seconds
     */
    public long getAccessTokenExpirationSeconds() {
        RegisteredClient registeredClient = registeredClientRepository.findByClientId("openchallenges-client");
        if (registeredClient != null && registeredClient.getTokenSettings() != null) {
            return registeredClient.getTokenSettings().getAccessTokenTimeToLive().toSeconds();
        }
        return 3600; // Default 1 hour
    }

    /**
     * Create Spring Security Authentication object for the user.
     */
    private Authentication createUserAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null, // No credentials needed for token generation
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase()))
        );
    }

    /**
     * Create additional claims to be included in the JWT token.
     */
}
