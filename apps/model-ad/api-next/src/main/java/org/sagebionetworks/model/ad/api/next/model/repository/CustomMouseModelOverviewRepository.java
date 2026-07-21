package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.MouseModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for Mouse Model Overview queries with filtering and search.
 *
 * <p>This interface defines custom query methods that require MongoTemplate for complex filtering
 * logic that cannot be expressed using Spring Data's derived query methods.
 */
public interface CustomMouseModelOverviewRepository {
  /**
   * Find all mouse model overviews matching the given query criteria.
   *
   * @param pageable the pagination information
   * @param query the search query containing all filter criteria
   * @param items the sanitized list of item names (from query.items)
   * @return page of mouse model overview documents matching all criteria
   */
  Page<MouseModelOverviewDocument> findAll(
    Pageable pageable,
    MouseModelOverviewSearchQueryDto query,
    List<String> items
  );
}
