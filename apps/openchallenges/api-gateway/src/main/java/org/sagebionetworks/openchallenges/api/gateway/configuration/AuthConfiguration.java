package org.sagebionetworks.openchallenges.api.gateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for authentication settings.
 */
@Configuration
@ConfigurationProperties(prefix = "openchallenges.auth")
public class AuthConfiguration {

  private String serviceUrl;
  private String realm = "OpenChallenges"; // Default fallback

  public String getServiceUrl() {
    return serviceUrl;
  }

  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  public String getRealm() {
    return realm;
  }

  public void setRealm(String realm) {
    this.realm = realm;
  }
}
