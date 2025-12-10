package org.sagebionetworks.model.ad.api.next.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.GeneExpressionMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.GeneExpressionRepository;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.sagebionetworks.model.ad.api.next.util.SortHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.GENE_EXPRESSION)
public class GeneExpressionService {

  private final GeneExpressionRepository repository;
  private final GeneExpressionMapper geneExpressionMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)" +
    ".buildCacheKey('geneExpression', #query.itemFilterType, #query.items, " +
    "#query.search, #tissue, #sexCohort, #query.pageNumber, #query.pageSize, " +
    "#query.sortFields, #query.sortOrders)"
  )
  public GeneExpressionsPageDto loadGeneExpressions(
    GeneExpressionSearchQueryDto query,
    String tissue,
    String sexCohort
  ) {
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );

    List<String> items = ApiHelper.sanitizeItems(query.getItems());
    String search = query.getSearch();

    // Build Sort from sortFields and sortOrders
    List<String> sortFields = ApiHelper.sanitizeItems(query.getSortFields());
    List<Integer> sortOrders = query.getSortOrders();
    Sort sort = buildSort(sortFields, sortOrders);
    PageRequest pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);

    boolean hasSearch = search != null && !search.trim().isEmpty();
    boolean hasItems = !items.isEmpty();
    boolean isExclude = effectiveFilter == ItemFilterTypeQueryDto.EXCLUDE;

    Page<GeneExpressionDocument> page;

    if (isExclude && hasSearch) {
      String trimmedSearch = search.trim();
      page = hasItems
        ? fetchPageWithSearchAndExclusions(tissue, sexCohort, trimmedSearch, items, pageable)
        : fetchPageWithSearchOnly(tissue, sexCohort, trimmedSearch, pageable);
    } else if (!hasItems) {
      page = isExclude
        ? repository.findByTissueAndSexCohort(tissue, sexCohort, pageable)
        : Page.empty(pageable);
    } else {
      List<Map<String, Object>> compositeConditions = buildCompositeConditions(
        parseIdentifiers(items, tissue, sexCohort)
      );
      page = isExclude
        ? repository.findByTissueAndSexCohortExcludingCompositeIdentifiers(
          tissue,
          sexCohort,
          compositeConditions,
          pageable
        )
        : repository.findByTissueAndSexCohortAndCompositeIdentifiers(
          tissue,
          sexCohort,
          compositeConditions,
          pageable
        );
    }

    List<GeneExpressionDto> geneExpressions = page
      .getContent()
      .stream()
      .map(geneExpressionMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return GeneExpressionsPageDto.builder()
      .geneExpressions(geneExpressions)
      .page(pageMetadata)
      .build();
  }

  private Page<GeneExpressionDocument> fetchPageWithSearchOnly(
    String tissue,
    String sexCohort,
    String trimmedSearch,
    PageRequest pageable
  ) {
    if (trimmedSearch.contains(",")) {
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      return repository.findByTissueAndSexCohortAndGeneSymbolInIgnoreCase(
        tissue,
        sexCohort,
        patterns,
        pageable
      );
    }

    return repository.findByTissueAndSexCohortAndGeneSymbolContaining(
      tissue,
      sexCohort,
      Pattern.quote(trimmedSearch),
      pageable
    );
  }

  private Page<GeneExpressionDocument> fetchPageWithSearchAndExclusions(
    String tissue,
    String sexCohort,
    String trimmedSearch,
    List<String> items,
    PageRequest pageable
  ) {
    List<Map<String, Object>> compositeConditions = buildCompositeConditions(
      parseIdentifiers(items, tissue, sexCohort)
    );

    if (trimmedSearch.contains(",")) {
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      return repository.findByTissueAndSexCohortAndGeneSymbolInIgnoreCaseExcludingCompositeIdentifiers(
        tissue,
        sexCohort,
        patterns,
        compositeConditions,
        pageable
      );
    }

    return repository.findByTissueAndSexCohortAndGeneSymbolContainingExcludingCompositeIdentifiers(
      tissue,
      sexCohort,
      Pattern.quote(trimmedSearch),
      compositeConditions,
      pageable
    );
  }

  /**
   * Parses composite identifiers from string items.
   *
   * @param items list of composite identifier strings
   * @param tissue tissue name for error logging
   * @param sexCohort sex cohort for error logging
   * @return list of parsed identifiers
   * @throws InvalidFilterException if parsing fails
   */
  private List<GeneExpressionIdentifier> parseIdentifiers(
    List<String> items,
    String tissue,
    String sexCohort
  ) {
    try {
      return items.stream().map(GeneExpressionIdentifier::parse).toList();
    } catch (InvalidFilterException e) {
      log.error(
        "Failed to parse composite identifiers for tissue '{}' and sex cohort '{}': {}",
        tissue,
        sexCohort,
        e.getMessage()
      );
      throw e;
    }
  }

  /**
   * Builds MongoDB query conditions for composite identifiers.
   * Each condition represents a single ensembl_gene_id-name combination wrapped in $and.
   *
   * @param identifiers list of parsed composite identifiers
   * @return list of MongoDB conditions for $or or $nor queries
   */
  private List<Map<String, Object>> buildCompositeConditions(
    List<GeneExpressionIdentifier> identifiers
  ) {
    List<Map<String, Object>> conditions = new ArrayList<>();

    for (GeneExpressionIdentifier identifier : identifiers) {
      // Each condition must match both fields (ensembl_gene_id AND name)
      List<Map<String, Object>> andConditions = new ArrayList<>();
      andConditions.add(Map.of("ensembl_gene_id", identifier.getEnsemblGeneId()));
      andConditions.add(Map.of("name", identifier.getName()));

      conditions.add(Map.of("$and", andConditions));
    }

    return conditions;
  }

  /**
   * Builds a Spring Data Sort object using the centralized SortHelper utility.
   *
   * @param sortFields list of field names to sort by
   * @param sortOrders list of sort orders (1 for ascending, -1 for descending)
   * @return Sort object, or Sort.unsorted() if no valid sort parameters
   */
  private Sort buildSort(List<String> sortFields, List<Integer> sortOrders) {
    return SortHelper.buildSort(sortFields, sortOrders, SortHelper.GENE_EXPRESSION_TRANSFORMER);
  }
}
