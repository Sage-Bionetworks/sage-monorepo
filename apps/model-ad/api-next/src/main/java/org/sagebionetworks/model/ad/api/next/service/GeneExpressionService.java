package org.sagebionetworks.model.ad.api.next.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    "#tissue, #sexCohort, #query.pageNumber, #query.pageSize, #query.sortFields, #query.sortOrders)"
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

    List<String> sanitizedItems = ApiHelper.parseCommaDelimitedString(query.getItems());

    // Parse comma-delimited sortFields and sortOrders
    List<String> sortFieldsList = ApiHelper.parseCommaDelimitedString(query.getSortFields());
    List<Integer> sortOrdersList = ApiHelper.parseCommaDelimitedIntegers(query.getSortOrders());

    // Build Sort from sortFields and sortOrders
    Sort sort = buildSort(sortFieldsList, sortOrdersList);
    PageRequest pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);
    Page<GeneExpressionDocument> page;

    if (sanitizedItems.isEmpty()) {
      // No items specified - return all or empty based on filter type
      if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
        page = Page.empty(pageable);
      } else {
        page = repository.findByTissueAndSexCohort(tissue, sexCohort, pageable);
      }
    } else {
      // Parse composite identifiers and build MongoDB query conditions
      List<GeneExpressionIdentifier> identifiers;
      try {
        identifiers = sanitizedItems.stream().map(GeneExpressionIdentifier::parse).toList();
      } catch (InvalidFilterException e) {
        log.error(
          "Failed to parse composite identifiers for tissue '{}' and sex cohort '{}': {}",
          tissue,
          sexCohort,
          e.getMessage()
        );
        throw e;
      }

      List<Map<String, Object>> compositeConditions = buildCompositeConditions(identifiers);

      if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
        page = repository.findByTissueAndSexCohortAndCompositeIdentifiers(
          tissue,
          sexCohort,
          compositeConditions,
          pageable
        );
      } else {
        page = repository.findByTissueAndSexCohortExcludingCompositeIdentifiers(
          tissue,
          sexCohort,
          compositeConditions,
          pageable
        );
      }
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

  /**
   * Builds a Spring Data Sort object using the centralized SortHelper utility.
   * Applies Gene Expression-specific field transformations (time period fields -> .log2_fc).
   *
   * @param sortFields list of field names to sort by
   * @param sortOrders list of sort directions (1 for ascending, -1 for descending)
   * @return Sort object for use in PageRequest
   * @throws IllegalArgumentException if arrays have mismatched lengths or invalid values
   */
  private Sort buildSort(List<String> sortFields, List<Integer> sortOrders) {
    return SortHelper.buildSort(sortFields, sortOrders, SortHelper.GENE_EXPRESSION_TRANSFORMER);
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
}
