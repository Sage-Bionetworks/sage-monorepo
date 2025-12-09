package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Gene Expression documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving gene expression data from the
 * rna_de_aggregate collection.
 */
@Repository
public interface GeneExpressionRepository
  extends MongoRepository<GeneExpressionDocument, ObjectId> {
  /**
   * Find all gene expressions for a specific tissue and sexCohort combination.
   *
   * @param tissue the tissue type
   * @param sexCohort the sex cohort
   * @param pageable pagination information
   * @return page of gene expression documents matching the tissue and sexCohort
   */
  Page<GeneExpressionDocument> findByTissueAndSexCohort(
    String tissue,
    String sexCohort,
    Pageable pageable
  );

  /**
   * Find gene expressions matching specific composite identifiers (ensembl_gene_id~name combinations).
   * Uses a custom MongoDB query to match exact combinations using $or with $and conditions.
   *
   * @param tissue the tissue type
   * @param sexCohort the sex cohort
   * @param compositeConditions array of composite conditions, each containing ensembl_gene_id and name
   * @param pageable pagination information
   * @return page of matching gene expression documents
   */
  @Query("{ 'tissue': ?0, 'sex_cohort': ?1, $or: ?2 }")
  Page<GeneExpressionDocument> findByTissueAndSexCohortAndCompositeIdentifiers(
    String tissue,
    String sexCohort,
    List<Map<String, Object>> compositeConditions,
    Pageable pageable
  );

  /**
   * Find gene expressions excluding specific composite identifiers (ensembl_gene_id~name combinations).
   * Uses a custom MongoDB query to exclude exact combinations using $nor with $and conditions.
   *
   * @param tissue the tissue type
   * @param sexCohort the sex cohort
   * @param compositeConditions array of composite conditions, each containing ensembl_gene_id and name
   * @param pageable pagination information
   * @return page of gene expression documents excluding the specified combinations
   */
  @Query("{ 'tissue': ?0, 'sex_cohort': ?1, $nor: ?2 }")
  Page<GeneExpressionDocument> findByTissueAndSexCohortExcludingCompositeIdentifiers(
    String tissue,
    String sexCohort,
    List<Map<String, Object>> compositeConditions,
    Pageable pageable
  );
}
