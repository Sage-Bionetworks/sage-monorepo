package org.sagebionetworks.openchallenges.auth.service.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.configuration.AppProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Health indicator for Synapse OAuth2 provider.
 *
 * <p>This health indicator checks the availability of Synapse's OAuth2 discovery endpoint
 * to verify that the OAuth2 provider is accessible and operational. It includes caching
 * to avoid excessive requests and provides detailed health information.
 *
 * <p>The health check validates:
 * <ul>
 *   <li>Synapse OAuth2 discovery endpoint accessibility</li>
 *   <li>Response contains expected OAuth2 configuration</li>
 *   <li>Network connectivity to Synapse services</li>
 * </ul>
 *
 * <p>This health indicator is only enabled when the Synapse OAuth2 provider is enabled
 * via the {@code app.oauth2.synapse.enabled} property.
 */
@Slf4j
@Component("synapseOAuth2")
@ConditionalOnProperty(name = "app.oauth2.synapse.enabled", havingValue = "true")
@RequiredArgsConstructor
public class SynapseOAuth2HealthIndicator extends AbstractOAuth2HealthIndicator {

  private final AppProperties appProperties;

  /**
   * Gets the provider name for logging and health details.
   *
   * @return the OAuth2 provider name
   */
  @Override
  protected String getProviderName() {
    return "Synapse OAuth2";
  }

  /**
   * Gets the OAuth2 discovery URL for Synapse.
   *
   * @return the discovery URL for Synapse OAuth2 configuration
   */
  @Override
  protected String getDiscoveryUrl() {
    return appProperties.getOauth2().getSynapse().getDiscoveryUrl();
  }
}
