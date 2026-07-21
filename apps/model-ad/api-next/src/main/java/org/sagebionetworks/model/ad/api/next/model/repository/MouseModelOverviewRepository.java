package org.sagebionetworks.model.ad.api.next.model.repository;

import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.MouseModelOverviewDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Mouse Model Overview documents in MongoDB.
 *
 * <p>All queries are handled by the custom repository implementation. This interface extends both
 * MongoRepository for basic CRUD operations and CustomMouseModelOverviewRepository for complex
 * filtering.
 */
@Repository
public interface MouseModelOverviewRepository
  extends
    MongoRepository<MouseModelOverviewDocument, ObjectId>, CustomMouseModelOverviewRepository {}
