package org.sagebionetworks.model.ad.api.next.service;

import java.util.ArrayList;
import java.util.Comparator;
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
import org.springframework.data.domain.PageImpl;
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

    // Parse sort fields and orders early to detect age sorting
    List<String> sortFields = ApiHelper.parseCommaDelimitedString(query.getSortFields());
    List<Integer> sortOrderIntegers = ApiHelper.parseCommaDelimitedIntegers(query.getSortOrders());

    // Check if age is in the sort fields (requires custom sorting)
    boolean hasAgeSorting = sortFields.contains("age");

    // Build Sort from sortFields and sortOrders (skip age if custom sorting needed)
    Sort sort = hasAgeSorting
      ? buildSortWithoutAge(sortFields, sortOrderIntegers)
      : buildSort(sortFields, sortOrderIntegers);

    boolean hasSearch = search != null && !search.trim().isEmpty();
    boolean hasItems = !items.isEmpty();
    boolean isExclude = effectiveFilter == ItemFilterTypeQueryDto.EXCLUDE;

    Page<DiseaseCorrelationDocument> page;

    if (hasAgeSorting) {
      // When age sorting is needed, fetch ALL results, sort in-memory, then paginate
      PageRequest unsortedPageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
      Page<DiseaseCorrelationDocument> allResults;

      if (isExclude && hasSearch) {
        String trimmedSearch = search.trim();
        allResults = hasItems
          ? fetchPageWithSearchAndExclusions(cluster, trimmedSearch, items, unsortedPageable)
          : fetchPageWithSearchOnly(cluster, trimmedSearch, unsortedPageable);
      } else if (!hasItems) {
        allResults = isExclude
          ? repository.findByCluster(cluster, unsortedPageable)
          : Page.empty(unsortedPageable);
      } else {
        List<Map<String, Object>> compositeConditions = buildCompositeConditions(
          parseIdentifiers(items, cluster)
        );
        allResults = isExclude
          ? repository.findByClusterExcludingCompositeIdentifiers(
            cluster,
            compositeConditions,
            unsortedPageable
          )
          : repository.findByClusterAndCompositeIdentifiers(
            cluster,
            compositeConditions,
            unsortedPageable
          );
      }

      // Sort all results
      List<DiseaseCorrelationDocument> sortedContent = applySortToResults(
        allResults.getContent(),
        sortFields,
        sortOrderIntegers
      );

      // Create the requested page from sorted results
      page = createPageFromSortedList(sortedContent, query.getPageNumber(), query.getPageSize());
    } else {
      // Normal pagination with MongoDB sorting
      PageRequest pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);

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
    }

    List<DiseaseCorrelationDocument> content = page.getContent();

    List<DiseaseCorrelationDto> diseaseCorrelations = content
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

  /**
   * Builds a Spring Data Sort object excluding the age field.
   * Age will be sorted in-memory using numeric comparison.
   *
   * @param sortFields list of field names to sort by
   * @param sortOrders list of sort directions (1 for ascending, -1 for descending)
   * @return Sort object for use in PageRequest
   */
  private Sort buildSortWithoutAge(List<String> sortFields, List<Integer> sortOrders) {
    List<String> fieldsWithoutAge = new ArrayList<>();
    List<Integer> ordersWithoutAge = new ArrayList<>();

    for (int i = 0; i < sortFields.size(); i++) {
      if (!"age".equals(sortFields.get(i))) {
        fieldsWithoutAge.add(sortFields.get(i));
        ordersWithoutAge.add(sortOrders.get(i));
      }
    }

    if (fieldsWithoutAge.isEmpty()) {
      return Sort.unsorted();
    }

    return SortHelper.buildSort(
      fieldsWithoutAge,
      ordersWithoutAge,
      SortHelper.DISEASE_CORRELATION_TRANSFORMER
    );
  }

  /**
   * Applies in-memory sorting to the result list.
   * This is necessary for age sorting to work numerically instead of lexicographically.
   *
   * @param documents list of documents to sort
   * @param sortFields list of field names to sort by
   * @param sortOrders list of sort directions (1 for ascending, -1 for descending)
   * @return sorted list of documents
   */
  private List<DiseaseCorrelationDocument> applySortToResults(
    List<DiseaseCorrelationDocument> documents,
    List<String> sortFields,
    List<Integer> sortOrders
  ) {
    if (documents.isEmpty()) {
      return documents;
    }

    // Build a composite comparator that handles all sort fields
    Comparator<DiseaseCorrelationDocument> comparator = null;

    for (int i = 0; i < sortFields.size(); i++) {
      String field = sortFields.get(i);
      int order = sortOrders.get(i);

      Comparator<DiseaseCorrelationDocument> fieldComparator = createFieldComparator(field);

      // Reverse if descending
      if (order == -1) {
        fieldComparator = fieldComparator.reversed();
      }

      // Chain comparators
      comparator = (comparator == null)
        ? fieldComparator
        : comparator.thenComparing(fieldComparator);
    }

    return documents.stream().sorted(comparator).collect(Collectors.toList());
  }

  /**
   * Creates a comparator for a specific field.
   * Handles age field with numeric comparison by extracting the number from strings like "4 months".
   *
   * @param field the field name to compare
   * @return comparator for the field
   */
  private Comparator<DiseaseCorrelationDocument> createFieldComparator(String field) {
    return switch (field) {
      case "age" -> Comparator.comparingInt(this::extractNumericAge);
      case "name" -> Comparator.comparing(
        DiseaseCorrelationDocument::getName,
        String.CASE_INSENSITIVE_ORDER
      );
      case "sex" -> Comparator.comparing(
        DiseaseCorrelationDocument::getSex,
        String.CASE_INSENSITIVE_ORDER
      );
      case "cluster" -> Comparator.comparing(
        DiseaseCorrelationDocument::getCluster,
        String.CASE_INSENSITIVE_ORDER
      );
      case "model_type" -> Comparator.comparing(
        DiseaseCorrelationDocument::getModelType,
        String.CASE_INSENSITIVE_ORDER
      );
      case "matched_control" -> Comparator.comparing(
        DiseaseCorrelationDocument::getMatchedControl,
        String.CASE_INSENSITIVE_ORDER
      );
      // For brain regions and other fields, fall back to string comparison
      default -> Comparator.comparing(
        DiseaseCorrelationDocument::getName,
        String.CASE_INSENSITIVE_ORDER
      );
    };
  }

  /**
   * Extracts the numeric value from an age string like "4 months" or "12 months".
   * Returns the number portion for numeric comparison.
   *
   * @param document the document containing the age field
   * @return the numeric age value, or Integer.MAX_VALUE if parsing fails
   */
  private int extractNumericAge(DiseaseCorrelationDocument document) {
    String age = document.getAge();
    if (age == null) {
      return Integer.MAX_VALUE;
    }

    try {
      // Extract the first sequence of digits from the age string
      String numericPart = age.replaceAll("[^0-9].*$", "");
      if (numericPart.isEmpty()) {
        return Integer.MAX_VALUE;
      }
      return Integer.parseInt(numericPart);
    } catch (NumberFormatException e) {
      log.warn("Failed to parse numeric age from: {}", age);
      return Integer.MAX_VALUE;
    }
  }

  /**
   * Creates a Page object from a sorted list with proper pagination.
   * Extracts the requested page slice from the complete sorted list.
   *
   * @param sortedList the complete sorted list of documents
   * @param pageNumber the requested page number (0-indexed)
   * @param pageSize the size of each page
   * @return Page object containing the requested slice
   */
  private Page<DiseaseCorrelationDocument> createPageFromSortedList(
    List<DiseaseCorrelationDocument> sortedList,
    int pageNumber,
    int pageSize
  ) {
    int totalElements = sortedList.size();
    int startIndex = pageNumber * pageSize;
    int endIndex = Math.min(startIndex + pageSize, totalElements);

    // Handle out of bounds page requests
    if (startIndex >= totalElements) {
      return new PageImpl<>(List.of(), PageRequest.of(pageNumber, pageSize), totalElements);
    }

    List<DiseaseCorrelationDocument> pageContent = sortedList.subList(startIndex, endIndex);
    return new PageImpl<>(pageContent, PageRequest.of(pageNumber, pageSize), totalElements);
  }
}
