package org.sagebionetworks.openchallenges.mcp.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openchallenges-mcp-server")
public class McpServerConfigData {

  private String welcomeMessage;
  private String apiBaseUrl;
}
