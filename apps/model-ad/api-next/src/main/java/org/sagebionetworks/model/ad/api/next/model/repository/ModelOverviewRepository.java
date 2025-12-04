package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
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

  /**
   * Find model overviews by partial name match (case-insensitive), excluding specified names
   * (case-sensitive).
   *
   * @param nameSearch the partial name to search for (case-insensitive)
   * @param excludeNames the list of names to exclude (case-sensitive exact match)
   * @param pageable the pagination information
   * @return page of model overview documents with names containing the search term, excluding
   *     specified names
   */
  @Query("{ $and: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'name': { $nin: ?1 } } ] }")
  Page<ModelOverviewDocument> findByNameContainingIgnoreCaseAndNameNotIn(
    String nameSearch,
    List<String> excludeNames,
    Pageable pageable
  );

  /**
   * Find model overviews with exact name matches from a list (case-insensitive), excluding
   * specified names (case-sensitive).
   *
   * @param includeNamePatterns the list of case-insensitive regex patterns to match names
   * @param excludeNames the list of names to exclude (case-sensitive exact match)
   * @param pageable the pagination information
   * @return page of model overview documents with matching names, excluding specified names
   */
  @Query("{ $and: [ { 'name': { $in: ?0 } }, { 'name': { $nin: ?1 } } ] }")
  Page<ModelOverviewDocument> findByNameInIgnoreCaseAndNameNotIn(
    List<Pattern> includeNamePatterns,
    List<String> excludeNames,
    Pageable pageable
  );
}
