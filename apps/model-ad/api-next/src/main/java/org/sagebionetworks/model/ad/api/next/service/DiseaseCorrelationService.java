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
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationIdentifier;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.DiseaseCorrelationRepository;
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
@CacheConfig(cacheNames = CacheNames.DISEASE_CORRELATION)
public class DiseaseCorrelationService {

  private final DiseaseCorrelationRepository repository;
  private final DiseaseCorrelationMapper diseaseCorrelationMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)" +
    ".buildCacheKey('diseaseCorrelation', #query.itemFilterType, #query.items, " +
    "#query.search, #cluster, #query.pageNumber, #query.pageSize, #query.sortFields, #query.sortOrders)"
  )
  public DiseaseCorrelationsPageDto loadDiseaseCorrelations(
    DiseaseCorrelationSearchQueryDto query,
    String cluster
  ) {
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );

    List<String> items = query.getItems() != null ? query.getItems() : List.of();

    String search = query.getSearch();

    // Build Sort from sortFields and sortOrders
    List<String> sortFields = ApiHelper.parseCommaDelimitedString(query.getSortFields());
    List<Integer> sortOrderIntegers = ApiHelper.parseCommaDelimitedIntegers(query.getSortOrders());
    Sort sort = buildSort(sortFields, sortOrderIntegers);

    PageRequest pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);

    boolean hasSearch = search != null && !search.trim().isEmpty();
    boolean hasItems = !items.isEmpty();
    boolean isExclude = effectiveFilter == ItemFilterTypeQueryDto.EXCLUDE;

    Page<DiseaseCorrelationDocument> page;

    if (isExclude && hasSearch) {
      String trimmedSearch = search.trim();
      page = hasItems
        ? fetchPageWithSearchAndExclusions(cluster, trimmedSearch, items, pageable)
        : fetchPageWithSearchOnly(cluster, trimmedSearch, pageable);
    } else if (!hasItems) {
      page = isExclude ? repository.findByCluster(cluster, pageable) : Page.empty(pageable);
    } else {
      List<Map<String, Object>> compositeConditions = buildCompositeConditions(
        parseIdentifiers(items, cluster)
      );
      page = isExclude
        ? repository.findByClusterExcludingCompositeIdentifiers(
          cluster,
          compositeConditions,
          pageable
        )
        : repository.findByClusterAndCompositeIdentifiers(cluster, compositeConditions, pageable);
    }

    List<DiseaseCorrelationDto> diseaseCorrelations = page
      .getContent()
      .stream()
      .map(diseaseCorrelationMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return DiseaseCorrelationsPageDto.builder()
      .diseaseCorrelations(diseaseCorrelations)
      .page(pageMetadata)
      .build();
  }

  private Page<DiseaseCorrelationDocument> fetchPageWithSearchOnly(
    String cluster,
    String trimmedSearch,
    PageRequest pageable
  ) {
    if (trimmedSearch.contains(",")) {
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      return repository.findByClusterAndNameInIgnoreCase(cluster, patterns, pageable);
    }

    return repository.findByClusterAndNameContaining(
      cluster,
      Pattern.quote(trimmedSearch),
      pageable
    );
  }

  private Page<DiseaseCorrelationDocument> fetchPageWithSearchAndExclusions(
    String cluster,
    String trimmedSearch,
    List<String> items,
    PageRequest pageable
  ) {
    List<Map<String, Object>> compositeConditions = buildCompositeConditions(
      parseIdentifiers(items, cluster)
    );

    if (trimmedSearch.contains(",")) {
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      return repository.findByClusterAndNameInIgnoreCaseExcludingCompositeIdentifiers(
        cluster,
        patterns,
        compositeConditions,
        pageable
      );
    }

    return repository.findByClusterAndNameContainingExcludingCompositeIdentifiers(
      cluster,
      Pattern.quote(trimmedSearch),
      compositeConditions,
      pageable
    );
  }

  /**
   * Parses composite identifiers from string items.
   *
   * @param items list of composite identifier strings
   * @param cluster cluster name for error logging
   * @return list of parsed identifiers
   * @throws InvalidFilterException if parsing fails
   */
  private List<DiseaseCorrelationIdentifier> parseIdentifiers(List<String> items, String cluster) {
    try {
      return items.stream().map(DiseaseCorrelationIdentifier::parse).toList();
    } catch (InvalidFilterException e) {
      log.error(
        "Failed to parse composite identifiers for cluster '{}': {}",
        cluster,
        e.getMessage()
      );
      throw e;
    }
  }

  /**
   * Builds MongoDB query conditions for composite identifiers.
   * Each condition represents a single name-age-sex combination wrapped in $and.
   *
   * @param identifiers list of parsed composite identifiers
   * @return list of MongoDB conditions for $or or $nor queries
   */
  private List<Map<String, Object>> buildCompositeConditions(
    List<DiseaseCorrelationIdentifier> identifiers
  ) {
    List<Map<String, Object>> conditions = new ArrayList<>();

    for (DiseaseCorrelationIdentifier identifier : identifiers) {
      // Each condition must match ALL three fields (name AND age AND sex)
      List<Map<String, Object>> andConditions = new ArrayList<>();
      andConditions.add(Map.of("name", identifier.getName()));
      andConditions.add(Map.of("age", identifier.getAge()));
      andConditions.add(Map.of("sex", identifier.getSex()));

      conditions.add(Map.of("$and", andConditions));
    }

    return conditions;
  }

  /**
   * Builds a Spring Data Sort object using the centralized SortHelper utility.
   * Applies Disease Correlation-specific field transformations (brain regions -> .correlation).
   *
   * @param sortFields list of field names to sort by
   * @param sortOrders list of sort directions (1 for ascending, -1 for descending)
   * @return Sort object for use in PageRequest
   * @throws IllegalArgumentException if arrays have mismatched lengths or invalid values
   */
  private Sort buildSort(List<String> sortFields, List<Integer> sortOrders) {
    return SortHelper.buildSort(sortFields, sortOrders, SortHelper.DISEASE_CORRELATION_TRANSFORMER);
  }
}
