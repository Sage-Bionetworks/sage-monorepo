package org.sagebionetworks.openchallenges.auth.service.health;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Health indicator for Google OAuth2 provider.
 * 
 * <p>This health indicator checks the availability of Google's OAuth2 discovery endpoint
 * to verify that the OAuth2 provider is accessible and operational. It includes caching
 * to avoid excessive requests and provides detailed health information.
 * 
 * <p>The health check validates:
 * <ul>
 *   <li>Google OAuth2 discovery endpoint accessibility</li>
 *   <li>Response contains expected OAuth2 configuration</li>
 *   <li>Network connectivity to Google services</li>
 * </ul>
 */
@Slf4j
@Component("googleOAuth2")
public class GoogleOAuth2HealthIndicator extends AbstractHealthIndicator {

    // Google OAuth2 discovery endpoint - well-known configuration URL
    private static final String GOOGLE_DISCOVERY_URL = "https://accounts.google.com/.well-known/openid-configuration";
    
    // Cache duration for health check results (5 minutes)
    private static final Duration CACHE_DURATION = Duration.ofMinutes(5);
    
    // Connection and read timeout for HTTP requests (10 seconds each)
    private static final int TIMEOUT_MILLIS = 10000;

    private final RestTemplate restTemplate;
    private volatile CachedHealthResult cachedResult;

    /**
     * Creates a new Google OAuth2 health indicator.
     * Configures RestTemplate with appropriate timeouts for health checks.
     */
    public GoogleOAuth2HealthIndicator() {
        // Configure RestTemplate with timeouts using SimpleClientHttpRequestFactory
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_MILLIS);
        factory.setReadTimeout(TIMEOUT_MILLIS);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Performs the actual health check for Google OAuth2 provider.
     * 
     * @param builder the health builder to populate with results
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            // Check cache first to avoid excessive requests
            CachedHealthResult cached = this.cachedResult;
            if (cached != null && !cached.isExpired()) {
                log.debug("Using cached Google OAuth2 health result");
                if (cached.health.getStatus().getCode().equals("UP")) {
                    builder.up();
                } else {
                    builder.down();
                }
                builder.withDetails(cached.health.getDetails());
                return;
            }

            // Perform fresh health check
            log.debug("Performing fresh Google OAuth2 health check");
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
            log.error("Unexpected error during Google OAuth2 health check", e);
            builder.down()
                   .withDetail("error", "Unexpected health check failure")
                   .withDetail("exception", e.getClass().getSimpleName())
                   .withDetail("message", e.getMessage());
        }
    }

    /**
     * Performs the actual HTTP request to check Google OAuth2 discovery endpoint.
     * 
     * @return Health result with detailed information
     */
    private Health performHealthCheck() {
        Instant startTime = Instant.now();
        
        try {
            log.debug("Checking Google OAuth2 discovery endpoint: {}", GOOGLE_DISCOVERY_URL);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(GOOGLE_DISCOVERY_URL, Map.class);
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
                    log.debug("Google OAuth2 health check successful - response time: {}ms", 
                               responseTime.toMillis());
                    
                    return Health.up()
                                 .withDetail("provider", "Google OAuth2")
                                 .withDetail("discoveryUrl", GOOGLE_DISCOVERY_URL)
                                 .withDetail("responseTimeMs", responseTime.toMillis())
                                 .withDetail("issuer", body.get("issuer"))
                                 .withDetail("checkTime", Instant.now().toString())
                                 .build();
                } else {
                    log.warn("Google OAuth2 discovery response missing required fields");
                    return Health.down()
                                 .withDetail("provider", "Google OAuth2")
                                 .withDetail("discoveryUrl", GOOGLE_DISCOVERY_URL)
                                 .withDetail("error", "Invalid discovery document - missing required OAuth2 fields")
                                 .withDetail("responseTimeMs", responseTime.toMillis())
                                 .withDetail("checkTime", Instant.now().toString())
                                 .build();
                }
            } else {
                log.warn("Google OAuth2 discovery endpoint returned non-success status: {}", statusCode);
                return Health.down()
                             .withDetail("provider", "Google OAuth2")
                             .withDetail("discoveryUrl", GOOGLE_DISCOVERY_URL)
                             .withDetail("error", "Non-success HTTP status")
                             .withDetail("httpStatus", statusCode.value())
                             .withDetail("responseTimeMs", responseTime.toMillis())
                             .withDetail("checkTime", Instant.now().toString())
                             .build();
            }
            
        } catch (RestClientException e) {
            Duration responseTime = Duration.between(startTime, Instant.now());
            log.warn("Google OAuth2 discovery endpoint unreachable", e);
            
            return Health.down()
                         .withDetail("provider", "Google OAuth2")
                         .withDetail("discoveryUrl", GOOGLE_DISCOVERY_URL)
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
