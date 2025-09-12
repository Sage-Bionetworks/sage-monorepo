package org.sagebionetworks.openchallenges.auth.service.health;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.configuration.AppProperties;
import org.springframework.stereotype.Component;

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
public class GoogleOAuth2HealthIndicator extends AbstractOAuth2HealthIndicator {

  private final AppProperties appProperties;

  /**
   * Creates a new Google OAuth2 health indicator.
   *
   * @param appProperties the application properties containing OAuth2 configuration
   */
  public GoogleOAuth2HealthIndicator(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  /**
   * Gets the provider name for logging and health details.
   *
   * @return the OAuth2 provider name
   */
  @Override
  protected String getProviderName() {
    return "Google OAuth2";
  }

  /**
   * Gets the OAuth2 discovery URL for Google.
   *
   * @return the discovery URL for Google OAuth2 configuration
   */
  @Override
  protected String getDiscoveryUrl() {
    return appProperties.getOauth2().getGoogle().getDiscoveryUrl();
  }
}
