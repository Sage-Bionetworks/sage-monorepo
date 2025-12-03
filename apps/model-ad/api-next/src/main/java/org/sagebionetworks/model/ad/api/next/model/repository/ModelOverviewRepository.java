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
   * Find model overviews with names in the provided list.
   *
   * @param names the list of names to include
   * @param pageable the pagination information
   * @return page of model overview documents with matching names
   */
  Page<ModelOverviewDocument> findByNameIn(List<String> names, Pageable pageable);

  /**
   * Find model overviews excluding names in the provided list.
   *
   * @param names the list of names to exclude
   * @param pageable the pagination information
   * @return page of model overview documents excluding the specified names
   */
  Page<ModelOverviewDocument> findByNameNotIn(List<String> names, Pageable pageable);
}
