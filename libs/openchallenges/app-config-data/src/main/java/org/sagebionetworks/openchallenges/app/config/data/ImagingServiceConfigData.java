package org.sagebionetworks.openchallenges.app.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openchallenges-imaging-service")
public class ImagingServiceConfigData {

  private String welcomeMessage;
}
