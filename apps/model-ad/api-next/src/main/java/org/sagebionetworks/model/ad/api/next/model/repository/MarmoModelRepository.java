package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.MarmoModelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for marmoset model documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving marmoset model detail data from the
 * marmo_details collection.
 */
@Repository
public interface MarmoModelRepository extends MongoRepository<MarmoModelDocument, ObjectId> {
  /**
   * Find a marmoset model by its name.
   *
   * @param name the name of the model to find
   * @return an Optional containing the model if found, or empty if not found
   */
  Optional<MarmoModelDocument> findByName(String name);
}
