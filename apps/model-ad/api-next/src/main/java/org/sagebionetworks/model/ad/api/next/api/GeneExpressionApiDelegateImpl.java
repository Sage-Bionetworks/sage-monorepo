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

    String[] tissueAndSex = extractTissueAndSex(categories);
    String tissue = tissueAndSex[0];
    String sex = tissueAndSex[1];

    GeneExpressionsPageDto results = geneExpressionService.loadGeneExpressions(query, tissue, sex);

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
   * Extracts tissue and sex from comma-delimited categories string.
   * Expected format: "mainCategory,tissueCategory,sexCategory" where:
   * - First value is the main category (e.g., "RNA - DIFFERENTIAL EXPRESSION")
   * - Second value is the tissue with prefix (e.g., "Tissue - Hemibrain")
   * - Third value is the sex with prefix (e.g., "Sex - Females & Males")
   *
   * @param categoriesString Comma-delimited category values
   * @return Array with [tissue, sex]
   */
  private String[] extractTissueAndSex(String categoriesString) {
    List<String> categories = ApiHelper.parseCommaDelimitedString(categoriesString);

    if (categories.size() < 3) {
      throw new InvalidCategoryException(
        "Expected at least 3 category values, got: " + categories.size()
      );
    }

    String mainCategory = categories.get(0).trim();
    String tissueWithPrefix = categories.get(1).trim();
    String sexWithPrefix = categories.get(2).trim();

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

    // Extract sex from "Sex - Females & Males"
    String sex = extractValueAfterPrefix(sexWithPrefix, "Sex - ", "sex");

    return new String[] { tissue, sex };
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
