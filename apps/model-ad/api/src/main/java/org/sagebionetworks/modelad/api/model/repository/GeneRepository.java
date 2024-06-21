package org.sagebionetworks.modelad.api.model.repository;

import org.sagebionetworks.modelad.api.model.document.GeneDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GeneRepository extends MongoRepository<GeneDocument, String> {}
