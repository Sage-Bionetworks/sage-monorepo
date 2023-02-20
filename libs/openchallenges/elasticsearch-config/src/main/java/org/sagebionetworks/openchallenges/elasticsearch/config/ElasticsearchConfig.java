package org.sagebionetworks.openchallenges.elasticsearch.config;

import java.util.Objects;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.sagebionetworks.openchallenges.app.config.data.ElasticsearchConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableElasticsearchRepositories(
    basePackages = "org.sagebionetworks.openchallenges.elasticsearch.index.client.repository")
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

  private final ElasticsearchConfigData elasticsearchConfigData;

  public ElasticsearchConfig(ElasticsearchConfigData elasticsearchConfigData) {
    this.elasticsearchConfigData = elasticsearchConfigData;
  }

  @Override
  @Bean
  public RestHighLevelClient elasticsearchClient() {
    UriComponents serverUri =
        UriComponentsBuilder.fromHttpUrl(elasticsearchConfigData.getConnectionUrl()).build();
    return new RestHighLevelClient(
        RestClient.builder(
                new HttpHost(
                    Objects.requireNonNull(serverUri.getHost()),
                    serverUri.getPort(),
                    serverUri.getScheme()))
            .setRequestConfigCallback(
                requestConfigBuildser ->
                    requestConfigBuildser
                        .setConnectTimeout(elasticsearchConfigData.getConnectTimeoutMs())
                        .setSocketTimeout(elasticsearchConfigData.getSocketTimeoutMs())));
  }

  public ElasticsearchOperations elasticsearchTemplate() {
    return new ElasticsearchRestTemplate(elasticsearchClient());
  }
}
