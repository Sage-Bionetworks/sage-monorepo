package org.sagebionetworks.model.ad.api.next.util;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;

/**
 * Parses the category values sent by the Differential Expression comparison tool.
 *
 * <p>Every modality served by that tool uses the same two-value shape:
 * {@code [<MODALITY> - DIFFERENTIAL EXPRESSION, Tissue - <tissue>]}, where the modality token is
 * {@code RNA} for transcriptomics and {@code PROTEIN} for proteomics. Other Model-AD comparison
 * tools use different category contracts and are not served by this class.
 */
public final class DifferentialExpressionCategoryParser {

  private static final int MINIMUM_CATEGORIES = 2;
  private static final String DIFFERENTIAL_TOKEN = "DIFFERENTIAL";
  private static final String TISSUE_PREFIX = "Tissue - ";

  private DifferentialExpressionCategoryParser() {
    // Utility class, prevent instantiation
  }

  /**
   * Extracts the tissue from a Differential Expression categories list.
   * Expected format: [mainCategory, tissueCategory] where:
   * - First value is the main category (e.g., "RNA - DIFFERENTIAL EXPRESSION")
   * - Second value is the tissue with prefix (e.g., "Tissue - Hemibrain")
   *
   * @param categories List of category values
   * @param modalityToken the modality expected in the main category (e.g., "RNA", "PROTEIN")
   * @return String, the tissue
   * @throws InvalidCategoryException if the categories do not match the expected shape
   */
  public static String extractTissue(List<String> categories, String modalityToken) {
    if (categories == null || categories.size() < MINIMUM_CATEGORIES) {
      throw new InvalidCategoryException(
        "Expected at least " +
        MINIMUM_CATEGORIES +
        " category values, got: " +
        (categories == null ? 0 : categories.size())
      );
    }

    String mainCategory = categories.get(0).trim();
    String tissueWithPrefix = categories.get(1).trim();

    // Validate main category (case-insensitive check)
    if (
      !mainCategory.toUpperCase().contains(modalityToken.toUpperCase()) ||
      !mainCategory.toUpperCase().contains(DIFFERENTIAL_TOKEN)
    ) {
      throw new InvalidCategoryException(
        "Invalid main category: '" +
        mainCategory +
        "'. Expected " +
        modalityToken +
        " - DIFFERENTIAL EXPRESSION"
      );
    }

    // Extract tissue from "Tissue - Hemibrain"
    return extractValueAfterPrefix(tissueWithPrefix, TISSUE_PREFIX, "tissue");
  }

  private static String extractValueAfterPrefix(String value, String prefix, String fieldName) {
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
