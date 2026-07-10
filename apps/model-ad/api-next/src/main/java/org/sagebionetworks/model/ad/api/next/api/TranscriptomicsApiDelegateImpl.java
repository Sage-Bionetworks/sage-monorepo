package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.service.TranscriptomicsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptomicsApiDelegateImpl implements TranscriptomicsApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "categories",
    "items",
    "itemFilterType",
    "search",
    "biodomains",
    "modelType",
    "name",
    "sortFields",
    "sortOrders"
  );

  private final TranscriptomicsService transcriptomicsService;

  @Override
  public ResponseEntity<TranscriptomicsPageDto> getTranscriptomics(
    TranscriptomicsSearchQueryDto query
  ) {
    log.debug("Fetching transcriptomics data with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    String tissue = extractTissue(query.getCategories());

    TranscriptomicsPageDto results = transcriptomicsService.loadTranscriptomics(query, tissue);

    log.debug(
      "Successfully retrieved {} transcriptomics data",
      results.getTranscriptomics().size()
    );

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }

  /**
   * Extracts tissue from categories list.
   * Expected format: [mainCategory, tissueCategory] where:
   * - First value is the main category (e.g., "RNA - DIFFERENTIAL EXPRESSION")
   * - Second value is the tissue with prefix (e.g., "Tissue - Hemibrain")
   *
   * @param categories List of category values
   * @return String, the tissue
   */
  private String extractTissue(List<String> categories) {
    if (categories == null || categories.size() < 2) {
      throw new InvalidCategoryException(
        "Expected at least 2 category values, got: " + (categories == null ? 0 : categories.size())
      );
    }

    String mainCategory = categories.get(0).trim();
    String tissueWithPrefix = categories.get(1).trim();

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
    return extractValueAfterPrefix(tissueWithPrefix, "Tissue - ", "tissue");
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
