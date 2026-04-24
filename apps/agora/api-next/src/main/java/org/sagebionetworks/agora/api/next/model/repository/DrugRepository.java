package org.sagebionetworks.agora.api.next.model.repository;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.sagebionetworks.agora.api.next.model.document.DrugDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugRepository extends MongoRepository<DrugDocument, ObjectId> {

  Optional<DrugDocument> findByChemblId(String chemblId);
}
