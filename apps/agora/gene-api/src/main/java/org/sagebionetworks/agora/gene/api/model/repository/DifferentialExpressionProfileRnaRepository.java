package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.DifferentialExpressionProfileRnaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DifferentialExpressionProfileRnaRepository
  extends
    MongoRepository<DifferentialExpressionProfileRnaDocument, String>,
    DifferentialExpressionProfileRnaRepositoryCustom {}
