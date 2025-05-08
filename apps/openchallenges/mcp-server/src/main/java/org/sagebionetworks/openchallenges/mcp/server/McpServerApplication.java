// package org.sagebionetworks.openchallenges.mcp.server;

// import java.util.List;
// import org.springframework.ai.tool.ToolCallback;
// import org.springframework.ai.tool.ToolCallbacks;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;

// @SpringBootApplication
// public class McpServerApplication {

//   public static void main(String[] args) {
//     SpringApplication.run(McpServerApplication.class, args);
//   }

//   @Bean
//   public List<ToolCallback> ocTools(ResourceService resourceService) {
//     return List.of(ToolCallbacks.from(resourceService));
//   }
// }

package org.sagebionetworks.openchallenges.mcp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McpServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(McpServerApplication.class, args);
  }
}
