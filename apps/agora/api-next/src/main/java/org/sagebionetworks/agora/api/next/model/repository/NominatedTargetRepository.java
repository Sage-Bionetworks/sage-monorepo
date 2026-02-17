package org.sagebionetworks.agora.api.next.model.repository;

import org.bson.types.ObjectId;
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Nominated Target documents in MongoDB.
 *
 * <p>All queries are handled by the custom repository implementation. This interface extends both
 * MongoRepository for basic CRUD operations and CustomNominatedTargetRepository for complex filtering.
 */
@Repository
public interface NominatedTargetRepository
  extends MongoRepository<NominatedTargetDocument, ObjectId>, CustomNominatedTargetRepository {}
