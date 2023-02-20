package org.sagebionetworks.openchallenges.elasticsearch.index.client.service;

import java.util.List;
import org.sagebionetworks.openchallenges.elasticsearch.model.index.IndexModel;

public interface ElasticsearchIndexClient<T extends IndexModel> {

  List<String> save(List<T> documents);
}
