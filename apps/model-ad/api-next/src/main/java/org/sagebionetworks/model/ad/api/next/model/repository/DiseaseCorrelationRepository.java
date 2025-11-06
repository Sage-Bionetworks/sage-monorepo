package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
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
   * @return list of disease correlation documents matching the cluster
   */
  List<DiseaseCorrelationDocument> findByCluster(String cluster);

  /**
   * Find disease correlations for a specific cluster with IDs in the provided list.
   *
   * @param cluster the cluster identifier
   * @param ids the list of ObjectIds to include
   * @return list of disease correlation documents matching the cluster and IDs
   */
  List<DiseaseCorrelationDocument> findByClusterAndIdIn(String cluster, List<ObjectId> ids);

  /**
   * Find disease correlations for a specific cluster excluding IDs in the provided list.
   *
   * @param cluster the cluster identifier
   * @param ids the list of ObjectIds to exclude
   * @return list of disease correlation documents matching the cluster, excluding the specified IDs
   */
  List<DiseaseCorrelationDocument> findByClusterAndIdNotIn(String cluster, List<ObjectId> ids);
}
