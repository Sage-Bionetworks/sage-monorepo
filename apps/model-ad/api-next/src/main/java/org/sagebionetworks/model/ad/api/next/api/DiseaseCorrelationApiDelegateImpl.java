package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.exception.ErrorConstants;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.service.DiseaseCorrelationService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class DiseaseCorrelationApiDelegateImpl implements DiseaseCorrelationApiDelegate {

  private final DiseaseCorrelationService diseaseCorrelationService;

  @Override
  public ResponseEntity<DiseaseCorrelationsPageDto> getDiseaseCorrelations(
    String categories,
    String sortFields,
    String sortOrders,
    Integer pageNumber,
    Integer pageSize,
    String search,
    String items,
    ItemFilterTypeQueryDto itemFilterType
  ) {
    validateSortParameters(sortFields, sortOrders);

    // Build DTO from HTTP request parameters for easier handling
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .categories(ApiHelper.parseCommaDelimitedString(categories))
      .sortFields(sortFields)
      .sortOrders(sortOrders)
      .pageNumber(pageNumber)
      .pageSize(pageSize)
      .items(items != null ? ApiHelper.parseCommaDelimitedString(items) : null)
      .itemFilterType(itemFilterType)
      .build();

    // Categories format: "cluster1,cluster2"
    String cluster = extractCluster(categories);

    DiseaseCorrelationsPageDto results = diseaseCorrelationService.loadDiseaseCorrelations(
      query,
      cluster
    );

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }

  /**
   * Validates that sortFields and sortOrders are both present or both absent,
   * and that they have matching element counts.
   *
   * @param sortFields Comma-delimited sort field names
   * @param sortOrders Comma-delimited sort orders
   * @throws IllegalArgumentException if validation fails
   */
  private void validateSortParameters(String sortFields, String sortOrders) {
    boolean hasSortFields = StringUtils.hasText(sortFields);
    boolean hasSortOrders = StringUtils.hasText(sortOrders);

    // Both must be present or both must be absent
    if (hasSortFields != hasSortOrders) {
      throw new IllegalArgumentException(
        "Both sortFields and sortOrders must be provided together, or both must be omitted"
      );
    }

    // If both are present, validate they have the same number of elements
    if (hasSortFields) {
      int fieldCount = sortFields.split(",", -1).length;
      int orderCount = sortOrders.split(",", -1).length;

      if (fieldCount != orderCount) {
        throw new IllegalArgumentException(
          String.format(
            "sortFields and sortOrders must have the same number of elements. " +
            "Got %d field(s) and %d order(s)",
            fieldCount,
            orderCount
          )
        );
      }
    }
  }

  /**
   * Extracts cluster from comma-delimited categories string.
   * Expected format: "mainCategory,clusterCategory" where:
   * - First value is the main category (e.g., "CONSENSUS NETWORK MODULES")
   * - Second value is the cluster category (e.g., "Consensus Cluster A - ECM Organization")
   *
   * @param categoriesString Comma-delimited category values
   * @return cluster name
   */
  private String extractCluster(String categoriesString) {
    List<String> categories = ApiHelper.parseCommaDelimitedString(categoriesString);

    if (categories.size() != 2) {
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
