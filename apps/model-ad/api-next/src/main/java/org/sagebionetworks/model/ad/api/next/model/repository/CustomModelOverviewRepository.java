package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for Model Overview queries with filtering and search.
 *
 * <p>This interface defines custom query methods that require MongoTemplate for complex filtering
 * logic that cannot be expressed using Spring Data's derived query methods.
 */
public interface CustomModelOverviewRepository {

  /**
   * Find all model overviews matching the given query criteria.
   *
   * @param pageable the pagination information
   * @param query the search query containing all filter criteria
   * @param items the sanitized list of item names (from query.items)
   * @return page of model overview documents matching all criteria
   */
  Page<ModelOverviewDocument> findAll(
    Pageable pageable,
    ModelOverviewSearchQueryDto query,
    List<String> items
  );
}
