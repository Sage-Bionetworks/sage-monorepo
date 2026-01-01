package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionsPageDto;
import org.sagebionetworks.model.ad.api.next.service.GeneExpressionService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeneExpressionApiDelegateImpl implements GeneExpressionApiDelegate {

  private final GeneExpressionService geneExpressionService;

  @Override
  public ResponseEntity<GeneExpressionsPageDto> getGeneExpressions(
    GeneExpressionSearchQueryDto query
  ) {
    log.debug("Fetching gene expressions for categories: {}", query.getCategories());

    String[] tissueAndSexCohort = extractTissueAndSexCohort(query.getCategories());
    String tissue = tissueAndSexCohort[0];
    String sexCohort = tissueAndSexCohort[1];

    GeneExpressionsPageDto results = geneExpressionService.loadGeneExpressions(
      query,
      tissue,
      sexCohort
    );

    log.debug("Successfully retrieved {} gene expressions", results.getGeneExpressions().size());
    ApiHelper.logHttpResponse(200);

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }

  /**
   * Extracts tissue and sex cohort from categories list.
   * Expected format: [mainCategory, tissueCategory, sexCohortCategory] where:
   * - First value is the main category (e.g., "RNA - DIFFERENTIAL EXPRESSION")
   * - Second value is the tissue with prefix (e.g., "Tissue - Hemibrain")
   * - Third value is the sex_cohort with prefix (e.g., "Sex - Females & Males")
   *
   * @param categories List of category values
   * @return Array with [tissue, sex_cohort]
   */
  private String[] extractTissueAndSexCohort(List<String> categories) {
    if (categories == null || categories.size() < 3) {
      throw new InvalidCategoryException(
        "Expected at least 3 category values, got: " + (categories == null ? 0 : categories.size())
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
