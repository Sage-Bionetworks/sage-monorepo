package org.sagebionetworks.sage.monorepo.mcp.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sage-monorepo-mcp-server")
public class McpServerConfigData {

  private String welcomeMessage;
}
