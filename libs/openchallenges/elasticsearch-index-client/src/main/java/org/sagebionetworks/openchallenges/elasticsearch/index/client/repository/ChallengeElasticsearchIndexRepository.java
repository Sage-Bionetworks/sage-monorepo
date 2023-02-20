package org.sagebionetworks.openchallenges.elasticsearch.index.client.repository;

import org.sagebionetworks.openchallenges.elasticsearch.model.index.ChallengeIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeElasticsearchIndexRepository
    extends ElasticsearchRepository<ChallengeIndexModel, String> {}
