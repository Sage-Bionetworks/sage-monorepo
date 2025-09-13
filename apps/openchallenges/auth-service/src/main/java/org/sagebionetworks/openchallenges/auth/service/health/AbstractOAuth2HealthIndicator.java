package org.sagebionetworks.openchallenges.auth.service.health;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for OAuth2 provider health indicators.
 * 
 * <p>This base class provides common functionality for checking the availability
 * of OAuth2 discovery endpoints. It includes caching to avoid excessive requests
 * and provides detailed health information.
 * 
 * <p>The health check validates:
 * <ul>
 *   <li>OAuth2 discovery endpoint accessibility</li>
 *   <li>Response contains expected OAuth2 configuration</li>
 *   <li>Network connectivity to OAuth2 provider services</li>
 * </ul>
 * 
 * <p>Subclasses need to provide:
 * <ul>
 *   <li>Provider name for logging and health details</li>
 *   <li>Discovery URL for the OAuth2 provider</li>
 * </ul>
 */
@Slf4j
public abstract class AbstractOAuth2HealthIndicator extends AbstractHealthIndicator {

    // Cache duration for health check results (5 minutes)
    private static final Duration CACHE_DURATION = Duration.ofMinutes(5);
    
    // Connection and read timeout for HTTP requests (10 seconds each)
    private static final int TIMEOUT_MILLIS = 10000;

    private final RestTemplate restTemplate;
    private volatile CachedHealthResult cachedResult;

    /**
     * Creates a new OAuth2 health indicator.
     * Configures RestTemplate with appropriate timeouts for health checks.
     */
    protected AbstractOAuth2HealthIndicator() {
        // Configure RestTemplate with timeouts using SimpleClientHttpRequestFactory
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_MILLIS);
        factory.setReadTimeout(TIMEOUT_MILLIS);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Gets the provider name for logging and health details.
     * 
     * @return the OAuth2 provider name (e.g., "Google OAuth2", "Synapse OAuth2")
     */
    protected abstract String getProviderName();

    /**
     * Gets the OAuth2 discovery URL for the provider.
     * 
     * @return the discovery URL for OAuth2 configuration
     */
    protected abstract String getDiscoveryUrl();

    /**
     * Performs the actual health check for the OAuth2 provider.
     * 
     * @param builder the health builder to populate with results
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            // Check cache first to avoid excessive requests
            CachedHealthResult cached = this.cachedResult;
            if (cached != null && !cached.isExpired()) {
                log.debug("Using cached {} health result", getProviderName());
                if (cached.health.getStatus().getCode().equals("UP")) {
                    builder.up();
                } else {
                    builder.down();
                }
                builder.withDetails(cached.health.getDetails());
                return;
            }

            // Perform fresh health check
            log.debug("Performing fresh {} health check", getProviderName());
            Health result = performHealthCheck();
            
            // Cache the result
            this.cachedResult = new CachedHealthResult(result, Instant.now());
            
            // Apply result to builder
            if (result.getStatus().getCode().equals("UP")) {
                builder.up();
            } else {
                builder.down();
            }
            builder.withDetails(result.getDetails());
            
        } catch (Exception e) {
            log.error("Unexpected error during {} health check", getProviderName(), e);
            builder.down()
                   .withDetail("error", "Unexpected health check failure")
                   .withDetail("exception", e.getClass().getSimpleName())
                   .withDetail("message", e.getMessage());
        }
    }

    /**
     * Performs the actual HTTP request to check OAuth2 discovery endpoint.
     * 
     * @return Health result with detailed information
     */
    private Health performHealthCheck() {
        Instant startTime = Instant.now();
        String discoveryUrl = getDiscoveryUrl();
        String providerName = getProviderName();
        
        try {
            log.debug("Checking {} discovery endpoint: {}", providerName, discoveryUrl);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(discoveryUrl, Map.class);
            Duration responseTime = Duration.between(startTime, Instant.now());
            
            HttpStatusCode statusCode = response.getStatusCode();
            @SuppressWarnings("unchecked")
            Map<String, Object> body = response.getBody();
            
            if (statusCode.is2xxSuccessful() && body != null) {
                // Validate that this looks like an OAuth2 discovery document
                boolean hasRequiredFields = body.containsKey("authorization_endpoint") 
                                          && body.containsKey("token_endpoint")
                                          && body.containsKey("issuer");
                
                if (hasRequiredFields) {
                    log.debug("{} health check successful - response time: {}ms", 
                               providerName, responseTime.toMillis());
                    
                    return Health.up()
                                 .withDetail("provider", providerName)
                                 .withDetail("discoveryUrl", discoveryUrl)
                                 .withDetail("responseTimeMs", responseTime.toMillis())
                                 .withDetail("issuer", body.get("issuer"))
                                 .withDetail("checkTime", Instant.now().toString())
                                 .build();
                } else {
                    log.warn("{} discovery response missing required fields", providerName);
                    return Health.down()
                                 .withDetail("provider", providerName)
                                 .withDetail("discoveryUrl", discoveryUrl)
                                 .withDetail("error", "Invalid discovery document - missing required OAuth2 fields")
                                 .withDetail("responseTimeMs", responseTime.toMillis())
                                 .withDetail("checkTime", Instant.now().toString())
                                 .build();
                }
            } else {
                log.warn("{} discovery endpoint returned non-success status: {}", providerName, statusCode);
                return Health.down()
                             .withDetail("provider", providerName)
                             .withDetail("discoveryUrl", discoveryUrl)
                             .withDetail("error", "Non-success HTTP status")
                             .withDetail("httpStatus", statusCode.value())
                             .withDetail("responseTimeMs", responseTime.toMillis())
                             .withDetail("checkTime", Instant.now().toString())
                             .build();
            }
            
        } catch (RestClientException e) {
            Duration responseTime = Duration.between(startTime, Instant.now());
            log.warn("{} discovery endpoint unreachable", providerName, e);
            
            return Health.down()
                         .withDetail("provider", providerName)
                         .withDetail("discoveryUrl", discoveryUrl)
                         .withDetail("error", "Discovery endpoint unreachable")
                         .withDetail("exception", e.getClass().getSimpleName())
                         .withDetail("message", e.getMessage())
                         .withDetail("responseTimeMs", responseTime.toMillis())
                         .withDetail("checkTime", Instant.now().toString())
                         .build();
        }
    }

    /**
     * Cached health result with expiration timestamp.
     */
    private static class CachedHealthResult {
        final Health health;
        final Instant timestamp;

        CachedHealthResult(Health health, Instant timestamp) {
            this.health = health;
            this.timestamp = timestamp;
        }

        boolean isExpired() {
            return Duration.between(timestamp, Instant.now()).compareTo(CACHE_DURATION) > 0;
        }
    }
}
