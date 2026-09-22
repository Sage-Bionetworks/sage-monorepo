package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsIndividualDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Proteomics Individual documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving lists of proteomics individual data from the
 * protein_de_individual collection.
 */
@Repository
public interface ProteomicsIndividualRepository
  extends MongoRepository<ProteomicsIndividualDocument, ObjectId> {
  /**
   * Find a list of proteomics individual data by uniqueId, model name, and tissue.
   *
   * @param uniqueId the concatenation of the Ensembl gene ID and the UniProt ID
   * @param name the name of the model
   * @param tissue the tissue type
   * @return a List containing the matching proteomics individual documents
   */
  List<ProteomicsIndividualDocument> findByUniqueIdAndNameAndTissue(
    String uniqueId,
    String name,
    String tissue
  );

  /**
   * Find a list of proteomics individual data by uniqueId, modelGroup, and tissue.
   *
   * @param uniqueId the concatenation of the Ensembl gene ID and the UniProt ID
   * @param modelGroup the group of the model
   * @param tissue the tissue type
   * @return a List containing the matching proteomics individual documents
   */
  List<ProteomicsIndividualDocument> findByUniqueIdAndModelGroupAndTissue(
    String uniqueId,
    String modelGroup,
    String tissue
  );
}
