package org.sagebionetworks.sage.monorepo.mcp.server;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.List;
import org.sagebionetworks.sage.monorepo.mcp.server.config.McpServerConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
