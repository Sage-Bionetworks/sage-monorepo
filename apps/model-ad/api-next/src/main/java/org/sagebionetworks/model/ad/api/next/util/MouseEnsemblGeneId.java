package org.sagebionetworks.model.ad.api.next.util;

import java.util.regex.Pattern;

/**
 * Recognizes a complete Ensembl mouse gene ID.
 *
 * <p>Shared by the differential expression comparison tool repositories, which route a search term
 * to {@code ensembl_gene_id} when the term is a complete ID rather than a fragment.
 */
public final class MouseEnsemblGeneId {

  /**
   * A complete Ensembl mouse gene ID. Matched case-insensitively because every other matcher in the
   * filterbox is case-insensitive; without it a lower-cased ID would fall through to the gene
   * symbol path and return nothing.
   */
  private static final Pattern FULL_ID = Pattern.compile(
    "^ENSMUSG\\d{11}$",
    Pattern.CASE_INSENSITIVE
  );

  private MouseEnsemblGeneId() {
    // Utility class, prevent instantiation
  }

  /**
   * Reports whether the term is a complete Ensembl mouse gene ID, ignoring case.
   *
   * @param term the search term, may be null
   * @return true when the whole term is a complete Ensembl mouse gene ID
   */
  public static boolean isFullId(String term) {
    return term != null && FULL_ID.matcher(term).matches();
  }
}
