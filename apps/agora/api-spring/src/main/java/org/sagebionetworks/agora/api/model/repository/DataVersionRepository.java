package org.sagebionetworks.agora.api.model.repository;

import org.sagebionetworks.agora.api.model.entity.DataVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DataVersionRepository extends MongoRepository<DataVersion, String> {
  @Query("{}")
  DataVersion get();
}
