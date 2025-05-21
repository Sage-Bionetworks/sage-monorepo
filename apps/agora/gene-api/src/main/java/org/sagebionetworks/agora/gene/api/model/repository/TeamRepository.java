package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.TeamDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamRepository extends MongoRepository<TeamDocument, String> {}
