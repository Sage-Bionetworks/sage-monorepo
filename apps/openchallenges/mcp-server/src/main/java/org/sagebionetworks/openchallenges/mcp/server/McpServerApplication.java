package org.sagebionetworks.openchallenges.mcp.server;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.mcp.server.configuration.AppProperties;
import org.sagebionetworks.openchallenges.mcp.server.service.ChallengeAnalyticsService;
import org.sagebionetworks.openchallenges.mcp.server.service.ChallengeContributionService;
import org.sagebionetworks.openchallenges.mcp.server.service.ChallengePlatformService;
import org.sagebionetworks.openchallenges.mcp.server.service.ChallengeService;
import org.sagebionetworks.openchallenges.mcp.server.service.EdamConceptService;
import org.sagebionetworks.openchallenges.mcp.server.service.OrganizationService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class McpServerApplication implements CommandLineRunner {

  private final AppProperties appProperties;

  public static void main(String[] args) {
    SpringApplication.run(McpServerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(appProperties.welcomeMessage());
    log.info("OC API base URL: {}", appProperties.apiBaseUrl());
  }

  @Bean
  public List<ToolCallback> toolCallbacks(
    ChallengeService challengeService,
    ChallengeContributionService challengeContributionService,
    ChallengeAnalyticsService challengeAnalyticsService,
    ChallengePlatformService challengePlatformService,
    EdamConceptService edamConceptService,
    OrganizationService organizationService
  ) {
    return Arrays.asList(
      ToolCallbacks.from(
        challengeService,
        challengeContributionService,
        challengeAnalyticsService,
        challengePlatformService,
        edamConceptService,
        organizationService
      )
    );
  }
}
