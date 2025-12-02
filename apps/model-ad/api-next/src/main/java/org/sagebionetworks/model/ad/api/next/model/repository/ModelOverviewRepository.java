package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Model Overview documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving model overview data from the model_overview
 * collection.
 */
@Repository
public interface ModelOverviewRepository extends MongoRepository<ModelOverviewDocument, ObjectId> {
  /**
   * Find model overviews with IDs in the provided list.
   *
   * @param ids the list of ObjectIds to include
   * @param pageable the pagination information
   * @return page of model overview documents with matching IDs
   */
  Page<ModelOverviewDocument> findByIdIn(List<ObjectId> ids, Pageable pageable);

  /**
   * Find model overviews excluding IDs in the provided list.
   *
   * @param ids the list of ObjectIds to exclude
   * @param pageable the pagination information
   * @return page of model overview documents excluding the specified IDs
   */
  Page<ModelOverviewDocument> findByIdNotIn(List<ObjectId> ids, Pageable pageable);
}
