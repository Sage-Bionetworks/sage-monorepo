package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/**
 * Custom repository interface for Gene Expression queries with computed field sorting.
 *
 * <p>This interface defines custom query methods that require MongoTemplate for aggregation
 * pipelines that compute a display_gene_symbol field for proper sorting when gene_symbol
 * is null or blank.
 */
public interface CustomGeneExpressionRepository {

  /**
   * Find gene expressions with sorting that handles gene_symbol fallback to ensembl_gene_id.
   * When sorting by gene_symbol, uses a computed field that falls back to ensembl_gene_id
   * when gene_symbol is null or blank.
   *
   * @param tissue the tissue type
   * @param sexCohort the sex cohort
   * @param compositeConditions conditions for $or/$nor queries (can be null)
   * @param isExclude true for $nor (exclude), false for $or (include)
   * @param geneSymbolSearch regex pattern for gene symbol search (can be null)
   * @param geneSymbolPatterns list of patterns for exact gene symbol matches (can be null)
   * @param pageable pagination and sorting information
   * @return page of gene expression documents with proper sorting
   */
  Page<GeneExpressionDocument> findWithComputedSort(
    String tissue,
    String sexCohort,
    @Nullable List<Map<String, Object>> compositeConditions,
    boolean isExclude,
    @Nullable String geneSymbolSearch,
    @Nullable List<Pattern> geneSymbolPatterns,
    Pageable pageable
  );
}
