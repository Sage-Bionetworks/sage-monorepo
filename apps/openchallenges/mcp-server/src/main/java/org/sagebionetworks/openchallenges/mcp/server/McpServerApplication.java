package org.sagebionetworks.openchallenges.mcp.server;

import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.mcp.server.config.McpServerConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

  @Bean
  public List<ToolCallback> toolCallbacks(
    ChallengeService challengeService,
    ChallengeAnalyticsService challengeAnalyticsService,
    ChallengePlatformService challengePlatformService,
    EdamConceptService edamConceptService,
    OrganizationService organizationService
  ) {
    return Arrays.asList(
      ToolCallbacks.from(
        challengeService,
        challengeAnalyticsService,
        challengePlatformService,
        edamConceptService,
        organizationService
      )
    );
  }
}
