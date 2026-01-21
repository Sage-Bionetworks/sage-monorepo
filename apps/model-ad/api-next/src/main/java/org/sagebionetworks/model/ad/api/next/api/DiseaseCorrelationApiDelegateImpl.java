package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.exception.ErrorConstants;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
import org.sagebionetworks.model.ad.api.next.service.DiseaseCorrelationService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiseaseCorrelationApiDelegateImpl implements DiseaseCorrelationApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "categories",
    "items",
    "itemFilterType",
    "search",
    "age",
    "modelType",
    "modifiedGenes",
    "name",
    "sex",
    "sortFields",
    "sortOrders"
  );

  private final DiseaseCorrelationService diseaseCorrelationService;

  @Override
  public ResponseEntity<DiseaseCorrelationsPageDto> getDiseaseCorrelations(
    DiseaseCorrelationSearchQueryDto query
  ) {
    log.debug("Fetching disease correlations with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    String cluster = extractCluster(query.getCategories());

    DiseaseCorrelationsPageDto results = diseaseCorrelationService.loadDiseaseCorrelations(
      query,
      cluster
    );

    log.debug("Successfully retrieved {} disease correlations", results.getDiseaseCorrelations().size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }

  /**
   * Extracts cluster from categories list.
   * Expected format: List of 2 items where:
   * - First value is the main category (e.g., "CONSENSUS NETWORK MODULES")
   * - Second value is the cluster category (e.g., "Consensus Cluster A - ECM Organization")
   *
   * @param categories List of category values
   * @return cluster name
   */
  private String extractCluster(List<String> categories) {
    if (categories == null || categories.size() != 2) {
      throw new InvalidCategoryException(ErrorConstants.CATEGORY_REQUIREMENT_MESSAGE);
    }

    String topLevelCategory = categories.get(0);
    String subCategory = categories.get(1);

    if (!ErrorConstants.DISEASE_CORRELATION_CATEGORY.equals(topLevelCategory)) {
      throw new InvalidCategoryException(
        topLevelCategory,
        ErrorConstants.DISEASE_CORRELATION_CATEGORY
      );
    }

    if (!StringUtils.hasText(subCategory)) {
      throw new InvalidCategoryException(ErrorConstants.CATEGORY_REQUIREMENT_MESSAGE);
    }

    return subCategory;
  }
}
