package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionIndividualDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Gene Expression Individual documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving lits of gene expression individual data from
 * the rna_de_individual collection.
 */
@Repository
public interface GeneExpressionIndividualRepository
  extends MongoRepository<GeneExpressionIndividualDocument, ObjectId> {
  /**
   * Find a list of gene expression individual data by ensemblGeneId, model name, and tissue.
   *
   * @param ensemblGeneId the Ensembl gene ID
   * @param name the name of the model
   * @param tissue the tissue type
   * @return a List containing the matching gene expression individual documents
   */
  List<GeneExpressionIndividualDocument> findByEnsemblGeneIdAndNameAndTissue(
    String ensemblGeneId,
    String name,
    String tissue
  );

  /**
   * Find a list of gene expression individual data by ensemblGeneId, modelGroup, and tissue.
   *
   * @param ensemblGeneId the Ensembl gene ID
   * @param modelGroup the group of the model
   * @param tissue the tissue type
   * @return a List containing the matching gene expression individual documents
   */
  List<GeneExpressionIndividualDocument> findByEnsemblGeneIdAndModelGroupAndTissue(
    String ensemblGeneId,
    String modelGroup,
    String tissue
  );
}
