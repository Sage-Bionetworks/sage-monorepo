package org.sagebionetworks.openchallenges.auth.service.service;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
public class OAuth2ConfigurationService {

    private final String googleClientId;
    private final String googleClientSecret;
    private final String synapseClientId;
    private final String synapseClientSecret;
    private final String baseUrl;

    public OAuth2ConfigurationService(
            @Value("${app.security.oauth2.google.client-id:}") String googleClientId,
            @Value("${app.security.oauth2.google.client-secret:}") String googleClientSecret,
            @Value("${app.security.oauth2.synapse.client-id:}") String synapseClientId,
            @Value("${app.security.oauth2.synapse.client-secret:}") String synapseClientSecret,
            @Value("${app.base-url:http://localhost:8085}") String baseUrl) {
        
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
        this.synapseClientId = synapseClientId;
        this.synapseClientSecret = synapseClientSecret;
        this.baseUrl = baseUrl;
        
        log.info("OAuth2 Configuration Service initialized for base URL: {}", baseUrl);
        log.info("Google OAuth2 enabled: {}", !googleClientId.isEmpty());
        log.info("Synapse OAuth2 enabled: {}", !synapseClientId.isEmpty());
    }

    /**
     * Get OAuth2 authorization URL for provider
     */
    public String getAuthorizationUrl(ExternalAccount.Provider provider, String state) {
        if (!isProviderConfigured(provider)) {
            throw new IllegalArgumentException("OAuth2 provider not configured: " + provider);
        }

        return switch (provider) {
            case google -> String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&scope=%s&response_type=code&state=%s",
                googleClientId, 
                getRedirectUri(provider), 
                "openid email profile", 
                state
            );
            case synapse -> String.format(
                "https://signin.synapse.org/oauth2/authorize?client_id=%s&redirect_uri=%s&scope=%s&response_type=code&state=%s",
                synapseClientId, 
                getRedirectUri(provider), 
                "openid email profile", 
                state
            );
        };
    }

    /**
     * Get redirect URI for provider
     */
    public String getRedirectUri(ExternalAccount.Provider provider) {
        return String.format("%s/api/v1/auth/oauth2/callback/%s", 
                baseUrl.replaceAll("/$", ""), provider.name().toLowerCase());
    }

    /**
     * Check if provider is configured
     */
    public boolean isProviderConfigured(ExternalAccount.Provider provider) {
        return switch (provider) {
            case google -> !googleClientId.isEmpty() && !googleClientSecret.isEmpty();
            case synapse -> !synapseClientId.isEmpty() && !synapseClientSecret.isEmpty();
        };
    }

    /**
     * Get provider configuration details
     */
    public OAuth2ProviderConfig getProviderConfig(ExternalAccount.Provider provider) {
        if (!isProviderConfigured(provider)) {
            return null;
        }

        return switch (provider) {
            case google -> OAuth2ProviderConfig.builder()
                    .provider(provider)
                    .clientId(googleClientId)
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://oauth2.googleapis.com/token")
                    .userInfoUri("https://www.googleapis.com/oauth2/v1/userinfo")
                    .redirectUri(getRedirectUri(provider))
                    .scopes(java.util.Set.of("openid", "email", "profile"))
                    .build();
            case synapse -> OAuth2ProviderConfig.builder()
                    .provider(provider)
                    .clientId(synapseClientId)
                    .authorizationUri("https://signin.synapse.org/oauth2/authorize")
                    .tokenUri("https://signin.synapse.org/oauth2/token")
                    .userInfoUri("https://repo-prod.prod.sagebase.org/auth/v1/oauth2/userinfo")
                    .redirectUri(getRedirectUri(provider))
                    .scopes(java.util.Set.of("openid", "email", "profile"))
                    .build();
        };
    }

    /**
     * Get all configured providers
     */
    public Map<ExternalAccount.Provider, OAuth2ProviderConfig> getAllConfiguredProviders() {
        Map<ExternalAccount.Provider, OAuth2ProviderConfig> configMap = new HashMap<>();
        
        OAuth2ProviderConfig googleConfig = getProviderConfig(ExternalAccount.Provider.google);
        if (googleConfig != null) {
            configMap.put(ExternalAccount.Provider.google, googleConfig);
        }
        
        OAuth2ProviderConfig synapseConfig = getProviderConfig(ExternalAccount.Provider.synapse);
        if (synapseConfig != null) {
            configMap.put(ExternalAccount.Provider.synapse, synapseConfig);
        }
        
        return configMap;
    }

    /**
     * Get user info endpoint for provider
     */
    public String getUserInfoEndpoint(ExternalAccount.Provider provider) {
        OAuth2ProviderConfig config = getProviderConfig(provider);
        return config != null ? config.getUserInfoUri() : null;
    }

    /**
     * Get token endpoint for provider
     */
    public String getTokenEndpoint(ExternalAccount.Provider provider) {
        OAuth2ProviderConfig config = getProviderConfig(provider);
        return config != null ? config.getTokenUri() : null;
    }

    /**
     * Get client ID for provider
     */
    public String getClientId(ExternalAccount.Provider provider) {
        return switch (provider) {
            case google -> googleClientId;
            case synapse -> synapseClientId;
        };
    }

    /**
     * Get client secret for provider
     */
    public String getClientSecret(ExternalAccount.Provider provider) {
        return switch (provider) {
            case google -> googleClientSecret;
            case synapse -> synapseClientSecret;
        };
    }

    /**
     * OAuth2 Provider Configuration
     */
    @lombok.Builder
    @lombok.Data
    public static class OAuth2ProviderConfig {
        private ExternalAccount.Provider provider;
        private String clientId;
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String redirectUri;
        private java.util.Set<String> scopes;
    }
}
