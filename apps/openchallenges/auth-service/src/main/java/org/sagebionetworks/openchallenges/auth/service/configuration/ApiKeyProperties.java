package org.sagebionetworks.openchallenges.auth.service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openchallenges.auth.api-key")
public class ApiKeyProperties {

  /**
   * The prefix for API keys (e.g., oc_dev_, oc_stage_, oc_prod_)
   */
  private String prefix = "oc_dev_";

  /**
   * The length of the random part of the API key (characters after prefix)
   */
  private int length = 40;
}
