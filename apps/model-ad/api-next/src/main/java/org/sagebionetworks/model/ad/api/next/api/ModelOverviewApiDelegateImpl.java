package org.sagebionetworks.model.ad.api.next.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.service.ModelOverviewService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class ModelOverviewApiDelegateImpl implements ModelOverviewApiDelegate {

  private final ModelOverviewService modelOverviewService;

  @Override
  public ResponseEntity<ModelOverviewsPageDto> getModelOverviews(
    String sortFields,
    String sortOrders,
    Integer pageNumber,
    Integer pageSize,
    String items,
    ItemFilterTypeQueryDto itemFilterType,
    String search
  ) {
    validateSortParameters(sortFields, sortOrders);

    // Build DTO from HTTP request parameters for easier handling
    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .sortFields(sortFields)
      .sortOrders(sortOrders)
      .pageNumber(pageNumber)
      .pageSize(pageSize)
      .items(items != null ? ApiHelper.parseCommaDelimitedString(items) : null)
      .itemFilterType(itemFilterType)
      .search(search)
      .build();

    ModelOverviewsPageDto page = modelOverviewService.loadModelOverviews(query);

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
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
}
