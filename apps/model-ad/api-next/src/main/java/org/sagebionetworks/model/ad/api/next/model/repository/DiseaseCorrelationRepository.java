package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Disease Correlation documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving disease correlation data from the
 * disease_correlation collection.
 */
@Repository
public interface DiseaseCorrelationRepository
  extends MongoRepository<DiseaseCorrelationDocument, ObjectId> {
  /**
   * Find all disease correlations for a specific cluster.
   *
   * @param cluster the cluster identifier
   * @param pageable pagination information
   * @return page of disease correlation documents matching the cluster
   */
  Page<DiseaseCorrelationDocument> findByCluster(String cluster, Pageable pageable);

  /**
   * Find disease correlations matching specific composite identifiers (name-age-sex combinations).
   * Uses a custom MongoDB query to match exact combinations using $or with $and conditions.
   *
   * @param cluster the cluster identifier
   * @param compositeConditions array of composite conditions, each containing name, age, and sex
   * @param pageable pagination information
   * @return page of matching disease correlation documents
   */
  @Query("{ 'cluster': ?0, $or: ?1 }")
  Page<DiseaseCorrelationDocument> findByClusterAndCompositeIdentifiers(
    String cluster,
    List<Map<String, Object>> compositeConditions,
    Pageable pageable
  );

  /**
   * Find disease correlations excluding specific composite identifiers (name-age-sex combinations).
   * Uses a custom MongoDB query to exclude exact combinations using $nor with $and conditions.
   *
   * @param cluster the cluster identifier
   * @param compositeConditions array of composite conditions, each containing name, age, and sex
   * @param pageable pagination information
   * @return page of disease correlation documents excluding the specified combinations
   */
  @Query("{ 'cluster': ?0, $nor: ?1 }")
  Page<DiseaseCorrelationDocument> findByClusterExcludingCompositeIdentifiers(
    String cluster,
    List<Map<String, Object>> compositeConditions,
    Pageable pageable
  );
}
