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

  @Tool(
    name = "smr_observability_docker_start",
    description = "Start the Observability stack with Docker."
  )
  public void dockerStart() {
    logger.info("Starting the Observability stack with Docker");
    try {
      // Start the Observability stack using nx serve-detach
      Process startProcess = new ProcessBuilder(
        "bash",
        "-c",
        "cd /workspaces/sage-monorepo && nx serve-detach observability-apex"
      ).start();

      int startExitCode = startProcess.waitFor();

      if (startExitCode == 0) {
        logger.info("Successfully started the Observability stack");
      } else {
        logger.error("Failed to start the Observability stack, exit code: {}", startExitCode);
        // Capture error output for better debugging
        java.io.BufferedReader errorReader = new java.io.BufferedReader(
          new java.io.InputStreamReader(startProcess.getErrorStream())
        );
        String errorOutput = errorReader.lines().collect(java.util.stream.Collectors.joining("\n"));
        logger.error("Error output: {}", errorOutput);
      }
    } catch (Exception e) {
      logger.error("Error during Observability stack startup", e);
    }
  }

  @Tool(
    name = "smr_observability_build_images",
    description = "Build Docker images for the Observability stack."
  )
  public void buildImages() {
    logger.info("Building Docker images for the Observability stack");
    try {
      // Build the Observability stack images
      Process buildProcess = new ProcessBuilder(
        "bash",
        "-c",
        "cd /workspaces/sage-monorepo && nx run-many --target=build-image --projects=observability-* --parallel=3"
      ).start();

      // Capture and log standard output in real-time for visibility
      java.io.BufferedReader outputReader = new java.io.BufferedReader(
        new java.io.InputStreamReader(buildProcess.getInputStream())
      );

      String line;
      while ((line = outputReader.readLine()) != null) {
        logger.info("Build output: {}", line);
      }

      int buildExitCode = buildProcess.waitFor();

      if (buildExitCode == 0) {
        logger.info("Successfully built Observability stack Docker images");
      } else {
        logger.error(
          "Failed to build Observability stack Docker images, exit code: {}",
          buildExitCode
        );
        // Capture error output for better debugging
        java.io.BufferedReader errorReader = new java.io.BufferedReader(
          new java.io.InputStreamReader(buildProcess.getErrorStream())
        );
        String errorOutput = errorReader.lines().collect(java.util.stream.Collectors.joining("\n"));
        logger.error("Error output: {}", errorOutput);
      }
    } catch (Exception e) {
      logger.error("Error during Observability stack Docker image build", e);
    }
  }
}
