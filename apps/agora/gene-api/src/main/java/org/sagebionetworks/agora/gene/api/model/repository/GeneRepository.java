package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.GeneDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GeneRepository extends MongoRepository<GeneDocument, String> {}
