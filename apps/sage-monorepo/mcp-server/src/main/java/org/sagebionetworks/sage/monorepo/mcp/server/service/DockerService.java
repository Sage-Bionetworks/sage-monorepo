package org.sagebionetworks.sage.monorepo.mcp.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class DockerService {

  private static final Logger logger = LoggerFactory.getLogger(DockerService.class);

  @Tool(
    name = "smr_docker_system_prune_all",
    description = "Prune all unused Docker resources, including images, containers, volumes, and networks."
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
}
