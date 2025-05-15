package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpression;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RnaDifferentialExpressionRepository
  extends MongoRepository<RnaDifferentialExpression, String> {}
