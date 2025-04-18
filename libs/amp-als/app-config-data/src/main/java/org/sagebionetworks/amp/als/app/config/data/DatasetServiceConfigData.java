package org.sagebionetworks.amp.als.app.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "amp-als-dataset-service")
public class DatasetServiceConfigData {

  private String welcomeMessage;
  private boolean isDeployedOnAws;
}
