package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.DataVersionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Data Version documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving data version information from the dataversion
 * collection.
 */
@Repository
public interface DataVersionRepository extends MongoRepository<DataVersionDocument, ObjectId> {
  /**
   * Find the first data version document in the collection.
   *
   * @return optional containing the first data version document if exists
   */
  Optional<DataVersionDocument> findFirstBy();
}
