package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.exception.ErrorConstants;
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
    GeneExpressionSearchQueryDto query
  ) {
    String[] tissueAndSex = extractTissueAndSex(query.getCategories());
    String tissue = tissueAndSex[0];
    String sex = tissueAndSex[1];

    GeneExpressionsPageDto results = geneExpressionService.loadGeneExpressions(query, tissue, sex);

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }

  /**
   * Extracts tissue and sex from categories array.
   * Expected format: [mainCategory, tissueCategory, sexCategory] where:
   * - categories[0] is the main category (e.g., "RNA - DIFFERENTIAL EXPRESSION")
   * - categories[1] is the tissue with prefix (e.g., "Tissue - Hemibrain")
   * - categories[2] is the sex with prefix (e.g., "Sex - Females & Males")
   *
   * @param categories Array of category values
   * @return Array with [tissue, sex]
   */
  private String[] extractTissueAndSex(List<String> categories) {
    if (categories == null || categories.size() < 3) {
      throw new InvalidCategoryException(
        "Expected at least 3 category values, got: " +
        (categories == null ? "null" : categories.size())
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
