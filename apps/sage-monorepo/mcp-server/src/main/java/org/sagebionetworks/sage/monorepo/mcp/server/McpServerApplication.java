package org.sagebionetworks.sage.monorepo.mcp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.sagebionetworks.sage.monorepo.mcp.server.configuration.McpServerConfig;
import org.sagebionetworks.sage.monorepo.mcp.server.service.DockerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(McpServerApplication.class);

  private final McpServerConfig mcpServerConfigData;

  public McpServerApplication(McpServerConfig mcpServerConfigData) {
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
  public List<ToolCallback> toolCallbacks(DockerService dockerService) {
    return Arrays.asList(ToolCallbacks.from(dockerService));
  }

  @Bean
  public List<McpServerFeatures.SyncResourceSpecification> myResources() {
    logger.info("calling myResources()");
    var systemInfoResource = new McpSchema.Resource(
      "custom://resource",
      "name",
      "description",
      "mime-type",
      null
    );
    var resourceSpecification = new McpServerFeatures.SyncResourceSpecification(
      systemInfoResource,
      (exchange, request) -> {
        try {
          var systemInfo = Map.of();
          String jsonContent = new ObjectMapper().writeValueAsString(systemInfo);
          return new McpSchema.ReadResourceResult(
            List.of(
              new McpSchema.TextResourceContents(request.uri(), "application/json", jsonContent)
            )
          );
        } catch (Exception e) {
          throw new RuntimeException("Failed to generate system info", e);
        }
      }
    );

    return List.of(resourceSpecification);
  }

  @Bean
  public List<McpServerFeatures.SyncPromptSpecification> myPrompts() {
    var prompt = new McpSchema.Prompt(
      "greeting",
      "A friendly greeting prompt",
      List.of(new McpSchema.PromptArgument("name", "The name to greet", true))
    );

    var promptSpecification = new McpServerFeatures.SyncPromptSpecification(
      prompt,
      (exchange, getPromptRequest) -> {
        String nameArgument = (String) getPromptRequest.arguments().get("name");
        if (nameArgument == null) {
          nameArgument = "friend";
        }
        var userMessage = new PromptMessage(
          Role.USER,
          new TextContent("Hello " + nameArgument + "! How can I assist you today?")
        );
        return new GetPromptResult("A personalized greeting message", List.of(userMessage));
      }
    );

    return List.of(promptSpecification);
  }
}
