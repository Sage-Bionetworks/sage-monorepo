package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for Disease Correlation queries with filtering and search.
 *
 * <p>This interface defines custom query methods that require MongoTemplate for complex filtering
 * logic that cannot be expressed using Spring Data's derived query methods.
 */
public interface CustomDiseaseCorrelationRepository {

  /**
   * Find all disease correlations matching the given query criteria.
   *
   * @param pageable the pagination information
   * @param query the search query containing all filter criteria
   * @param items the sanitized list of composite identifiers (from query.items)
   * @param cluster the cluster filter value extracted from categories
   * @return page of disease correlation documents matching all criteria
   */
  Page<DiseaseCorrelationDocument> findAll(
    Pageable pageable,
    DiseaseCorrelationSearchQueryDto query,
    List<String> items,
    String cluster
  );
}
