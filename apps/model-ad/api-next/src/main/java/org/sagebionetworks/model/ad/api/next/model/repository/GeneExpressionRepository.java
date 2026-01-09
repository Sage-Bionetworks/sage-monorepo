package org.sagebionetworks.model.ad.api.next.model.repository;

import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Gene Expression documents in MongoDB.
 *
 * <p>All queries are handled by the custom repository implementation. This interface extends both
 * MongoRepository for basic CRUD operations and CustomGeneExpressionRepository for complex filtering.
 */
@Repository
public interface GeneExpressionRepository
  extends MongoRepository<GeneExpressionDocument, ObjectId>, CustomGeneExpressionRepository {}
