package org.sagebionetworks.openchallenges.elasticsearch.index.client.service;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.openchallenges.app.config.data.ElasticsearchConfigData;
import org.sagebionetworks.openchallenges.elasticsearch.index.client.util.ElasticsearchIndexUtil;
import org.sagebionetworks.openchallenges.elasticsearch.model.index.ChallengeIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "openchallenges-elasticsearch.is-repository", havingValue = "false")
public class ChallengeElasticsearchIndexClient
    implements ElasticsearchIndexClient<ChallengeIndexModel> {

  private static final Logger LOG =
      LoggerFactory.getLogger(ChallengeElasticsearchIndexClient.class);

  private final ElasticsearchConfigData elasticsearchConfigData;

  private final ElasticsearchOperations elasticsearchOperations;

  private final ElasticsearchIndexUtil<ChallengeIndexModel> elasticsearchIndexUtil;

  public ChallengeElasticsearchIndexClient(
      ElasticsearchConfigData elasticsearchConfigData,
      ElasticsearchOperations elasticsearchOperations,
      ElasticsearchIndexUtil<ChallengeIndexModel> elasticsearchIndexUtil) {
    this.elasticsearchConfigData = elasticsearchConfigData;
    this.elasticsearchOperations = elasticsearchOperations;
    this.elasticsearchIndexUtil = elasticsearchIndexUtil;
  }

  @Override
  public List<String> save(List<ChallengeIndexModel> documents) {
    List<IndexQuery> indexQueries = elasticsearchIndexUtil.getIndexQueries(documents);
    List<String> documentIds =
        elasticsearchOperations
            .bulkIndex(indexQueries, IndexCoordinates.of(elasticsearchConfigData.getIndexName()))
            .stream()
            .map(indexedObjectInformation -> indexedObjectInformation.getId())
            .collect(Collectors.toList());
    LOG.info(
        "Documents indexed successfully with type: {} and ids: {}",
        ChallengeIndexModel.class.getName(),
        documentIds);
    return documentIds;
  }
}
