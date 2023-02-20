package org.sagebionetworks.openchallenges.app.config.data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch-config")
public class ElasticsearchConfigData {

  private String indexName;
  private String connectionUrl;
  private Integer connectTimeoutMs;
  private Integer socketTimeoutMs;
}
