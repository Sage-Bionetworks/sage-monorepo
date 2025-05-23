package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RnaDifferentialExpressionProfileRepository
  extends
    MongoRepository<RnaDifferentialExpressionProfileDocument, String>,
    RnaDifferentialExpressionProfileRepositoryCustom {}
