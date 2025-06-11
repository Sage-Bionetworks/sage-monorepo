package org.sagebionetworks.sage.monorepo.mcp.server.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sage-monorepo-mcp-server")
public class McpServerConfig {

  private String welcomeMessage;
}
