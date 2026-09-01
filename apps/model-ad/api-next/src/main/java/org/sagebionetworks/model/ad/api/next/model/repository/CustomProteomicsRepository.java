package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for Proteomics queries with filtering and search.
 *
 * <p>This interface defines custom query methods that require MongoTemplate for complex filtering
 * logic that cannot be expressed using Spring Data's derived query methods.
 */
public interface CustomProteomicsRepository {
  /**
   * Find all proteomics data matching the given query criteria.
   *
   * @param pageable the pagination information
   * @param query the search query containing all filter criteria
   * @param items the sanitized list of composite identifiers (from query.items)
   * @param tissue the tissue filter value extracted from categories
   * @return page of proteomics documents matching all criteria
   */
  Page<ProteomicsDocument> findAll(
    Pageable pageable,
    ProteomicsSearchQueryDto query,
    List<String> items,
    String tissue
  );
}
