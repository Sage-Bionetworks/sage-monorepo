package org.sagebionetworks.sage.monorepo.mcp.server;

import org.sagebionetworks.sage.monorepo.mcp.server.config.McpServerConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McpServerApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(McpServerApplication.class);

  private final McpServerConfigData mcpServerConfigData;

  public McpServerApplication(McpServerConfigData mcpServerConfigData) {
    this.mcpServerConfigData = mcpServerConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(McpServerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info(mcpServerConfigData.getWelcomeMessage());
  }
}
