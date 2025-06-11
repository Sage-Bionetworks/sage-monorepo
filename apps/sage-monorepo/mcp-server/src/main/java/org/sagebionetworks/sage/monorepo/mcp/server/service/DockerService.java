package org.sagebionetworks.sage.monorepo.mcp.server.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class DockerService {

  private static final Logger logger = LoggerFactory.getLogger(DockerService.class);

  /**
   * Map of product names to their corresponding apex container names.
   * This provides a centralized lookup for determining which top-level container
   * should be started for a given product.
   */
  private static final Map<String, String> PRODUCT_TO_APEX_CONTAINER = Map.ofEntries(
    Map.entry("agora", "agora-apex"),
    Map.entry("amp-als", "amp-als-apex"),
    Map.entry("observability", "observability-apex"),
    Map.entry("openchallenges", "openchallenges-apex")
  );

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
  public void productBuildImages(
    @ToolParam(description = "The product name to build images for.") String productName
  ) {
    String productNameLower = productName.toLowerCase();

    // Validate product name
    if (!isValidProduct(productNameLower)) {
      logger.error("Unknown product: {}. Please specify a valid product name.", productName);
      return;
    }

    logger.info("Building Docker images for the {} product", productName);
    try {
      // Build the product images
      String command =
        "cd /workspaces/sage-monorepo && nx run-many --target=build-image " +
        "--projects=" +
        productNameLower +
        "-* --parallel=3";
      Process buildProcess = new ProcessBuilder("bash", "-c", command).start();

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

  /**
   * List all available products in the monorepo.
   *
   * @return A string containing all available product names
   */
  @Tool(name = "smr_list_products", description = "List all available products in the monorepo.")
  public String listProducts() {
    StringBuilder result = new StringBuilder("Available products in the monorepo: ");

    // Convert to list, sort, and join with commas
    String productList = PRODUCT_TO_APEX_CONTAINER.keySet()
      .stream()
      .sorted()
      .collect(java.util.stream.Collectors.joining(", "));

    result.append(productList);
    return result.toString();
  }

  /**
   * Check if the given product name exists in the monorepo.
   *
   * @param productName The product name to check
   *
   * @return true if the product exists, false otherwise
   */
  private boolean isValidProduct(String productName) {
    return PRODUCT_TO_APEX_CONTAINER.containsKey(productName.toLowerCase());
  }

  @Tool(name = "smr_product_docker_start", description = "Start the product stack with Docker.")
  public void productDockerStart(
    @ToolParam(description = "The product name to start with Docker.") String productName
  ) {
    String productNameLower = productName.toLowerCase();

    // Validate product name
    if (!isValidProduct(productNameLower)) {
      logger.error("Unknown product: {}. Please specify a valid product name.", productName);
      return;
    }

    String apexContainer = PRODUCT_TO_APEX_CONTAINER.getOrDefault(
      productNameLower,
      productNameLower + "-apex"
    );

    logger.info(
      "Starting the {} stack with Docker (apex container: {})",
      productName,
      apexContainer
    );
    try {
      // Start the product stack using nx serve-detach
      String command = "cd /workspaces/sage-monorepo && nx serve-detach " + apexContainer;
      Process startProcess = new ProcessBuilder("bash", "-c", command).start();

      int startExitCode = startProcess.waitFor();

      if (startExitCode == 0) {
        logger.info("Successfully started the {} stack", productName);
      } else {
        logger.error("Failed to start the {} stack, exit code: {}", productName, startExitCode);
        // Capture error output for better debugging
        java.io.BufferedReader errorReader = new java.io.BufferedReader(
          new java.io.InputStreamReader(startProcess.getErrorStream())
        );
        String errorOutput = errorReader.lines().collect(java.util.stream.Collectors.joining("\n"));
        logger.error("Error output: {}", errorOutput);
      }
    } catch (Exception e) {
      logger.error("Error during {} stack startup", productName, e);
    }
  }

  @Tool(name = "smr_product_docker_remove", description = "Remove the product stack with Docker.")
  public void productDockerRemove(
    @ToolParam(description = "The product stack to remove with Docker.") String productName
  ) {
    String productNameLower = productName.toLowerCase();

    // Validate product name
    if (!isValidProduct(productNameLower)) {
      logger.error("Unknown product: {}. Please specify a valid product name.", productName);
      return;
    }

    logger.info("Removing the {} stack with Docker", productName);
    try {
      // First, get the container IDs
      Process findProcess = new ProcessBuilder(
        "docker",
        "ps",
        "--all",
        "--filter",
        "name=^" + productNameLower + "-*",
        "--format",
        "{{.ID}}"
      ).start();

      // Wait for the find process to complete
      int findExitCode = findProcess.waitFor();
      if (findExitCode != 0) {
        logger.error("Failed to find the containers, exit code: {}", findExitCode);
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

      if (!containerIds.isEmpty()) {
        // Remove all containers in a single command
        logger.info("Removing container IDs: {}", containerIds);

        String command = "docker rm --force " + containerIds;
        Process rmProcess = new ProcessBuilder("bash", "-c", command).start();
        int rmExitCode = rmProcess.waitFor();

        if (rmExitCode == 0) {
          logger.info("Successfully removed all {} containers", productName);
        } else {
          logger.error("Failed to remove containers, exit code: {}", rmExitCode);
          // Capture error output for better debugging
          java.io.BufferedReader errorReader = new java.io.BufferedReader(
            new java.io.InputStreamReader(rmProcess.getErrorStream())
          );
          String errorOutput = errorReader
            .lines()
            .collect(java.util.stream.Collectors.joining("\n"));
          logger.error("Error output: {}", errorOutput);
        }
      } else {
        logger.info("No {} containers found to remove", productName);
      }

      logger.info("Docker remove operation completed");
    } catch (Exception e) {
      logger.error("Error during Docker remove", e);
    }
  }
}
