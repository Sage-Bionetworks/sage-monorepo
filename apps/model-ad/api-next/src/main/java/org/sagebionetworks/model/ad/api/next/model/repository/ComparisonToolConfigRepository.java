package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.ComparisonToolConfigDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ComparisonToolConfig documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving comparison tool config data from the ui_config
 * collection.
 */
@Repository
public interface ComparisonToolConfigRepository
  extends MongoRepository<ComparisonToolConfigDocument, ObjectId> {
  /**
   * Find comparison tool configs by page.
   *
   * @param page the comparison tool page to find
   * @return the list of comparison tool configs for the specified page
   */
  List<ComparisonToolConfigDocument> findByPage(String page);
}
