package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsIndividualDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Transcriptomics Individual documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving lists of transcriptomics individual data from
 * the rna_de_individual collection.
 */
@Repository
public interface TranscriptomicsIndividualRepository
  extends MongoRepository<TranscriptomicsIndividualDocument, ObjectId> {
  /**
   * Find a list of transcriptomics individual data by ensemblGeneId, model name, and tissue.
   *
   * @param ensemblGeneId the Ensembl gene ID
   * @param name the name of the model
   * @param tissue the tissue type
   * @return a List containing the matching transcriptomics individual documents
   */
  List<TranscriptomicsIndividualDocument> findByEnsemblGeneIdAndNameAndTissue(
    String ensemblGeneId,
    String name,
    String tissue
  );

  /**
   * Find a list of transcriptomics individual data by ensemblGeneId, modelGroup, and tissue.
   *
   * @param ensemblGeneId the Ensembl gene ID
   * @param modelGroup the group of the model
   * @param tissue the tissue type
   * @return a List containing the matching transcriptomics individual documents
   */
  List<TranscriptomicsIndividualDocument> findByEnsemblGeneIdAndModelGroupAndTissue(
    String ensemblGeneId,
    String modelGroup,
    String tissue
  );
}
