package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for complex Model Overview queries.
 *
 * <p>This interface defines custom query methods that require MongoTemplate for complex filtering
 * logic that cannot be expressed using Spring Data's derived query methods.
 */
public interface ModelOverviewRepositoryCustom {

  /**
   * Find model overviews with complex filtering including data filters, name filtering, and search
   * criteria.
   *
   * @param items the list of names to include or exclude
   * @param search the search term for name filtering (optional)
   * @param filterType whether to INCLUDE or EXCLUDE the specified items
   * @param availableData filter by available data types (OR within field)
   * @param center filter by center link text (OR within field)
   * @param modelType filter by model type (OR within field)
   * @param modifiedGenes filter by modified genes (OR within field)
   * @param pageable the pagination information
   * @return page of model overview documents matching all criteria (AND between fields)
   */
  Page<ModelOverviewDocument> findWithFilters(
    List<String> items,
    String search,
    ItemFilterTypeQueryDto filterType,
    List<String> availableData,
    List<String> center,
    List<String> modelType,
    List<String> modifiedGenes,
    Pageable pageable
  );
}
