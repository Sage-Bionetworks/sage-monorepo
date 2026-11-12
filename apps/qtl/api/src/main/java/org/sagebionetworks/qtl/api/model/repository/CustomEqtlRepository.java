package org.sagebionetworks.qtl.api.model.repository;

import java.util.List;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for eQTL queries with filtering and search.
 *
 * <p>This interface defines custom query methods that require MongoTemplate for complex filtering
 * logic that cannot be expressed using Spring Data's derived query methods.
 */
public interface CustomEqtlRepository {
  /**
   * Find all eQTLs matching the given query criteria.
   *
   * @param pageable the pagination information
   * @param query the search query containing all filter criteria
   * @param items the sanitized list of composite identifiers (from query.items)
   * @return page of eQTL documents matching all criteria
   */
  Page<EqtlDocument> findAll(Pageable pageable, EqtlSearchQueryDto query, List<String> items);
}
