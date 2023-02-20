package org.sagebionetworks.openchallenges.elasticsearch.index.client.service;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.openchallenges.elasticsearch.index.client.repository.ChallengeElasticsearchIndexRepository;
import org.sagebionetworks.openchallenges.elasticsearch.model.index.ChallengeIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
    name = "openchallenges-elasticsearch.is-repository",
    havingValue = "true",
    matchIfMissing = true)
public class ChallengeElasticsearchRepositoryIndexClient
    implements ElasticsearchIndexClient<ChallengeIndexModel> {

  private static final Logger LOG =
      LoggerFactory.getLogger(ChallengeElasticsearchRepositoryIndexClient.class);

  private final ChallengeElasticsearchIndexRepository challengeElasticsearchIndexRepository;

  public ChallengeElasticsearchRepositoryIndexClient(
      ChallengeElasticsearchIndexRepository challengeElasticsearchIndexRepository) {
    this.challengeElasticsearchIndexRepository = challengeElasticsearchIndexRepository;
  }

  @Override
  public List<String> save(List<ChallengeIndexModel> documents) {
    List<ChallengeIndexModel> repositoryResponse =
        (List<ChallengeIndexModel>) challengeElasticsearchIndexRepository.saveAll(documents);
    List<String> documentIds =
        repositoryResponse.stream().map(ChallengeIndexModel::getId).collect(Collectors.toList());
    LOG.info(
        "Documents indexed successfully with type: {} and ids: {}",
        ChallengeIndexModel.class.getName(),
        documentIds);
    return documentIds;
  }
}
