package org.sagebionetworks.openchallenges.app.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch-config")
public class ElasticsearchConfigData {

  private String indexName;
  private String connectionUrl;
  private Integer connectTimeoutMs;
  private Integer socketTimeoutMs;
}
