package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.OverallScoresDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OverallScoresRepository extends MongoRepository<OverallScoresDocument, String> {}
