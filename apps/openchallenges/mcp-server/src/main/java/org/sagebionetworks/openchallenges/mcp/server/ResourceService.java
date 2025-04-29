package org.sagebionetworks.openchallenges.mcp.server;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.openchallenges.mcp.server.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

  private static final Logger log = LoggerFactory.getLogger(ResourceService.class);
  private final List<Resource> resources = new ArrayList<>();

  @PostConstruct
  public void init() {
    resources.addAll(
      List.of(
        new Resource("OpenChallenges Web App", "https://openchallenges.io/"),
        new Resource("API Docs", "https://openchallenges.io/api-docs"),
        new Resource("Zipkin", "https://openchallenges.io/zipkin/")
      )
    );
    log.info("Initialized {} resources", resources.size());
  }

  @Tool(name = "oc_get_resources", description = "Get a list of OpenChallenges resources")
  public List<Resource> getResources() {
    log.info("Fetching all resources");
    return resources;
  }

  @Tool(name = "oc_get_resource", description = "Get a single OpenChallenges resource by name")
  public Resource getResource(String name) {
    log.info("Fetching resource by name: {}", name);
    return resources
      .stream()
      .filter(resource -> resource.name().equalsIgnoreCase(name))
      .findFirst()
      .orElse(null);
  }
}
