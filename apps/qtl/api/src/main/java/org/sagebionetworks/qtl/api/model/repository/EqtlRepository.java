package org.sagebionetworks.qtl.api.model.repository;

import org.bson.types.ObjectId;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for eQTL documents in MongoDB.
 *
 * <p>All queries are handled by the custom repository implementation. This interface extends both
 * MongoRepository for basic CRUD operations and CustomEqtlRepository for complex filtering.
 */
@Repository
public interface EqtlRepository
  extends MongoRepository<EqtlDocument, ObjectId>, CustomEqtlRepository {}
