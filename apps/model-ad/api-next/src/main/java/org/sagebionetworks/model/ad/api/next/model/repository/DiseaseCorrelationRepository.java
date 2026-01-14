package org.sagebionetworks.model.ad.api.next.model.repository;

import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Disease Correlation documents in MongoDB.
 *
 * <p>All queries are handled by the custom repository implementation. This interface extends both
 * MongoRepository for basic CRUD operations and CustomDiseaseCorrelationRepository for complex filtering.
 */
@Repository
public interface DiseaseCorrelationRepository
  extends MongoRepository<DiseaseCorrelationDocument, ObjectId>, CustomDiseaseCorrelationRepository {}
