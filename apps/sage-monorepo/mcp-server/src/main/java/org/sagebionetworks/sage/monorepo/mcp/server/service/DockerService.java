package org.sagebionetworks.sage.monorepo.mcp.server.service;

import io.micrometer.common.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class DockerService {

  private static final Logger logger = LoggerFactory.getLogger(DockerService.class);

  @Tool(
    name = "smr_docker_system_prune_all",
    description = """
    Prune all unused Docker resources, including images, containers, volumes, and networks.
    """
  )
  public void dockerSystemPruneAll() {
    logger.info("Pruning all unused Docker resources");
    try {
      Process process = new ProcessBuilder("docker", "system", "prune", "--all", "--force").start();
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        logger.info("Docker system prune completed successfully");
      } else {
        logger.error("Docker system prune failed with exit code: {}", exitCode);
      }
    } catch (Exception e) {
      logger.error("Error during Docker system prune", e);
    }
  }

  @Tool(name = "smr_product_build_images", description = "Build Docker images for a product.")
  public void buildImages(
    @ToolParam(description = "The product name to build images for.") String productName
  ) {
    logger.info("Building Docker images for the {} product", productName);
    try {
      // Build the product product images
      Process buildProcess = new ProcessBuilder(
        "bash",
        "-c",
        "cd /workspaces/sage-monorepo && nx run-many --target=build-image --projects=" +
        productName.toLowerCase() +
        "-* --parallel=3"
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
        logger.info("Successfully built {} product Docker images", productName);
      } else {
        logger.error(
          "Failed to build {} product Docker images, exit code: {}",
          productName,
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
      logger.error("Error during {} product Docker image build", productName, e);
    }
  }
}
