package org.sagebionetworks.agora.api.next.model.repository;

import java.util.List;
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for Nominated Target queries with filtering and search.
 *
 * <p>This interface defines custom query methods that require MongoTemplate for complex filtering
 * logic that cannot be expressed using Spring Data's derived query methods.
 */
public interface CustomNominatedTargetRepository {
  /**
   * Find all nominated targets matching the given query criteria.
   *
   * @param pageable the pagination information
   * @param query the search query containing all filter criteria
   * @param items the sanitized list of item names (from query.items)
   * @return page of nominated target documents matching all criteria
   */
  Page<NominatedTargetDocument> findAll(
    Pageable pageable,
    NominatedTargetSearchQueryDto query,
    List<String> items
  );
}
