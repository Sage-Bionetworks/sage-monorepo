package org.sagebionetworks.agora.api.next.model.repository;

import org.bson.types.ObjectId;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for NominatedDrug documents in MongoDB.
 *
 * <p>Provides type-safe query methods for retrieving nominated drug data from the nominateddrugs
 * collection.
 */
@Repository
public interface NominatedDrugRepository
  extends MongoRepository<NominatedDrugDocument, ObjectId>, CustomNominatedDrugRepository {}
