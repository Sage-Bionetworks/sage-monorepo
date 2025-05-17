package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.BioDomainsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BioDomainsRepository extends MongoRepository<BioDomainsDocument, String> {}
