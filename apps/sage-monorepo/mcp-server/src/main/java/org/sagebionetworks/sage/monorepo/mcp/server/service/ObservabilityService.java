package org.sagebionetworks.sage.monorepo.mcp.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class ObservabilityService {

  private static final Logger logger = LoggerFactory.getLogger(ObservabilityService.class);

  @Tool(
    name = "smr_observability_docker_stop",
    description = "Stop the Observability stack with Docker."
  )
  public void dockerStop() {
    logger.info("Stopping the Observability stack with Docker");
    try {
      // First, get the running container IDs
      Process findProcess = new ProcessBuilder(
        "docker",
        "ps",
        "--filter",
        "name=^/observability-",
        "--format",
        "{{.ID}}"
      ).start();

      // Wait for the find process to complete
      int findExitCode = findProcess.waitFor();
      if (findExitCode != 0) {
        logger.error("Failed to find observability containers, exit code: {}", findExitCode);
        return;
      }

      // Read the output (container IDs) and collect them
      java.io.BufferedReader reader = new java.io.BufferedReader(
        new java.io.InputStreamReader(findProcess.getInputStream())
      );
      String containerIds = reader
        .lines()
        .map(String::trim)
        .filter(id -> !id.isEmpty())
        .collect(java.util.stream.Collectors.joining(" "));

      logger.debug("Container ids: {}", containerIds);

      if (!containerIds.isEmpty()) {
        // Stop all containers in a single command
        Process stopProcess = new ProcessBuilder("docker", "stop", containerIds).start();
        int stopExitCode = stopProcess.waitFor();

        if (stopExitCode == 0) {
          logger.info("Successfully stopped all observability containers");
        } else {
          logger.error("Failed to stop containers, exit code: {}", stopExitCode);
        }
      } else {
        logger.info("No observability containers found to stop");
      }

      logger.info("Docker stop operation completed");
    } catch (Exception e) {
      logger.error("Error during Docker stop", e);
    }
  }
}
