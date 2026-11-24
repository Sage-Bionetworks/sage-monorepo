package org.sagebionetworks.qtl.api.model.repository;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.sagebionetworks.qtl.api.model.document.EqtlPhenotypeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for eQTL phenotype documents in the eqtl_phenotype collection.
 */
@Repository
public interface EqtlPhenotypeRepository extends MongoRepository<EqtlPhenotypeDocument, ObjectId> {
  /**
   * Find the phenotype document of a single eQTL.
   *
   * @param variant the variant ID
   * @param ensemblGeneId the Ensembl gene ID
   * @param cellTypeId the cell type ID of the eQTL's cell subclass
   * @return the matching document, if any
   */
  Optional<EqtlPhenotypeDocument> findByVariantAndEnsemblGeneIdAndCellTypeId(
    String variant,
    String ensemblGeneId,
    String cellTypeId
  );
}
