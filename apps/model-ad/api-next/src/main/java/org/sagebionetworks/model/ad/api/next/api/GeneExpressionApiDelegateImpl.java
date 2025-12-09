package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.service.GeneExpressionService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class GeneExpressionApiDelegateImpl implements GeneExpressionApiDelegate {

  private final GeneExpressionService geneExpressionService;

  @Override
  public ResponseEntity<GeneExpressionsPageDto> getGeneExpressions(
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
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(ApiHelper.parseCommaDelimitedString(categories))
      .sortFields(sortFields)
      .sortOrders(sortOrders)
      .pageNumber(pageNumber)
      .pageSize(pageSize)
      .items(items != null ? ApiHelper.parseCommaDelimitedString(items) : null)
      .itemFilterType(itemFilterType)
      .build();

    String[] tissueAndSexCohort = extractTissueAndSexCohort(categories);
    String tissue = tissueAndSexCohort[0];
    String sexCohort = tissueAndSexCohort[1];

    GeneExpressionsPageDto results = geneExpressionService.loadGeneExpressions(
      query,
      tissue,
      sexCohort
    );
    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }

  /**
   * Validates that sortFields and sortOrders are both present and have matching element counts.
   * These parameters are required by the API contract.
   *
   * @param sortFields Comma-delimited sort field names (required)
   * @param sortOrders Comma-delimited sort orders (required)
   * @throws IllegalArgumentException if validation fails
   */
  private void validateSortParameters(String sortFields, String sortOrders) {
    boolean hasSortFields = StringUtils.hasText(sortFields);
    boolean hasSortOrders = StringUtils.hasText(sortOrders);

    // Both parameters are required
    if (!hasSortFields) {
      throw new IllegalArgumentException("sortFields is required");
    }

    if (!hasSortOrders) {
      throw new IllegalArgumentException("sortOrders is required");
    }

    // Validate they have the same number of elements
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

  /**
   * Extracts tissue and sex from comma-delimited categories string.
   * Expected format: "mainCategory,tissueCategory,sexCohortCategory" where:
   * - First value is the main category (e.g., "RNA - DIFFERENTIAL EXPRESSION")
   * - Second value is the tissue with prefix (e.g., "Tissue - Hemibrain")
   * - Third value is the sex_cohort with prefix (e.g., "Sex - Females & Males")
   *
   * @param categoriesString Comma-delimited category values
   * @return Array with [tissue, sex_cohort]
   */
  private String[] extractTissueAndSexCohort(String categoriesString) {
    List<String> categories = ApiHelper.parseCommaDelimitedString(categoriesString);

    if (categories.size() < 3) {
      throw new InvalidCategoryException(
        "Expected at least 3 category values, got: " + categories.size()
      );
    }

    String mainCategory = categories.get(0).trim();
    String tissueWithPrefix = categories.get(1).trim();
    String sexCohortWithPrefix = categories.get(2).trim();

    // Validate main category (case-insensitive check)
    if (
      !mainCategory.toUpperCase().contains("RNA") ||
      !mainCategory.toUpperCase().contains("DIFFERENTIAL")
    ) {
      throw new InvalidCategoryException(
        "Invalid main category: '" + mainCategory + "'. Expected RNA - DIFFERENTIAL EXPRESSION"
      );
    }

    // Extract tissue from "Tissue - Hemibrain"
    String tissue = extractValueAfterPrefix(tissueWithPrefix, "Tissue - ", "tissue");

    // Extract sex_cohort from "Sex - Females & Males"
    String sexCohort = extractValueAfterPrefix(sexCohortWithPrefix, "Sex - ", "sex_cohort");

    return new String[] { tissue, sexCohort };
  }

  private String extractValueAfterPrefix(String value, String prefix, String fieldName) {
    if (!value.startsWith(prefix)) {
      throw new InvalidCategoryException(
        "Invalid " + fieldName + " format: '" + value + "'. Expected format: '" + prefix + "...'"
      );
    }

    String extracted = value.substring(prefix.length()).trim();
    if (extracted.isEmpty()) {
      throw new InvalidCategoryException(
        "Invalid " + fieldName + ": '" + value + "'. Value after '" + prefix + "' must be non-empty"
      );
    }

    return extracted;
  }
}
