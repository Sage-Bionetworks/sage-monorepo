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
    "#cluster, #query.pageNumber, #query.pageSize, #query.sortFields, #query.sortOrders)"
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

    // Build Sort from sortFields and sortOrders
    List<String> sortFields = ApiHelper.parseCommaDelimitedString(query.getSortFields());
    List<Integer> sortOrderIntegers = ApiHelper.parseCommaDelimitedIntegers(query.getSortOrders());
    Sort sort = buildSort(sortFields, sortOrderIntegers);
    PageRequest pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);
    Page<DiseaseCorrelationDocument> page;

    if (items.isEmpty()) {
      // No items specified - return all or empty based on filter type
      if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
        page = Page.empty(pageable);
      } else {
        page = repository.findByCluster(cluster, pageable);
      }
    } else {
      // Parse composite identifiers and build MongoDB query conditions
      List<DiseaseCorrelationIdentifier> identifiers;
      try {
        identifiers = items.stream().map(DiseaseCorrelationIdentifier::parse).toList();
      } catch (InvalidFilterException e) {
        log.error(
          "Failed to parse composite identifiers for cluster '{}': {}",
          cluster,
          e.getMessage()
        );
        throw e;
      }

      List<Map<String, Object>> compositeConditions = buildCompositeConditions(identifiers);

      if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
        page = repository.findByClusterAndCompositeIdentifiers(
          cluster,
          compositeConditions,
          pageable
        );
      } else {
        page = repository.findByClusterExcludingCompositeIdentifiers(
          cluster,
          compositeConditions,
          pageable
        );
      }
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
