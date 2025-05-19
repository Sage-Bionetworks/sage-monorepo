package org.sagebionetworks.openchallenges.mcp.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties
public class McpServerConfigData {

  private Welcome welcome;
  private Api api;

  @Data
  public static class Welcome {

    private String message;
  }

  @Data
  public static class Api {

    private Base base;

    @Data
    public static class Base {

      private String url;
    }
  }
}
