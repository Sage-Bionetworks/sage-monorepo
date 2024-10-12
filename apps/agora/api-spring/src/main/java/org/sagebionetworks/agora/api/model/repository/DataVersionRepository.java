package org.sagebionetworks.agora.api.model.repository;

import org.sagebionetworks.agora.api.model.entity.DataVersionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DataVersionRepository extends MongoRepository<DataVersionEntity, String> {
  @Query("{}")
  DataVersionEntity get();
}
