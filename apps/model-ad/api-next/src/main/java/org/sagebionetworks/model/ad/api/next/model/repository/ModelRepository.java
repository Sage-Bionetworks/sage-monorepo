package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.ModelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Model documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving model detail data from the model_details
 * collection.
 */
@Repository
public interface ModelRepository extends MongoRepository<ModelDocument, ObjectId> {
  /**
   * Find a model by its name.
   *
   * @param name the name of the model to find
   * @return an Optional containing the model if found, or empty if not found
   */
  Optional<ModelDocument> findByName(String name);
}
