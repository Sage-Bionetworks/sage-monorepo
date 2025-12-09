package org.sagebionetworks.model.ad.api.next.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelOverviewRepository;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.sagebionetworks.model.ad.api.next.util.SortHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.MODEL_OVERVIEW)
public class ModelOverviewService {

  private final ModelOverviewRepository repository;
  private final ModelOverviewMapper modelOverviewMapper;

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ApiHelper)" +
    ".buildCacheKey('modelOverview', #query.itemFilterType, " +
    "#query.items, #query.search, #query.pageNumber, #query.pageSize, #query.sortFields, #query.sortOrders)"
  )
  public ModelOverviewsPageDto loadModelOverviews(ModelOverviewSearchQueryDto query) {
    List<String> items = query.getItems() != null ? query.getItems() : List.of();
    String search = query.getSearch();
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );

    int effectivePageNumber = Objects.requireNonNullElse(query.getPageNumber(), 0);
    int effectivePageSize = Objects.requireNonNullElse(query.getPageSize(), 100);

    // Build Sort from sortFields and sortOrders
    List<String> sortFields = ApiHelper.parseCommaDelimitedString(query.getSortFields());
    List<Integer> sortOrderIntegers = ApiHelper.parseCommaDelimitedIntegers(query.getSortOrders());
    Sort sort = buildSort(sortFields, sortOrderIntegers);
    Pageable pageable = PageRequest.of(effectivePageNumber, effectivePageSize, sort);
    Page<ModelOverviewDocument> page;

    if (
      effectiveFilter == ItemFilterTypeQueryDto.EXCLUDE &&
      search != null &&
      !search.trim().isEmpty()
    ) {
      page = fetchPageWithSearch(search.trim(), items, pageable);
    } else {
      if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
        page = items.isEmpty() ? Page.empty(pageable) : repository.findByNameIn(items, pageable);
      } else {
        page = items.isEmpty()
          ? repository.findAll(pageable)
          : repository.findByNameNotIn(items, pageable);
      }
    }

    List<ModelOverviewDto> dtos = page
      .getContent()
      .stream()
      .map(modelOverviewMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    PageMetadataDto pageMetadata = PageMetadataDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();

    return ModelOverviewsPageDto.builder().modelOverviews(dtos).page(pageMetadata).build();
  }

  private Page<ModelOverviewDocument> fetchPageWithSearch(
    String trimmedSearch,
    List<String> excludeNames,
    Pageable pageable
  ) {
    if (trimmedSearch.contains(",")) {
      List<Pattern> patterns = createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      return repository.findByNameInIgnoreCaseAndNameNotIn(patterns, excludeNames, pageable);
    }

    return repository.findByNameContainingIgnoreCaseAndNameNotIn(
      Pattern.quote(trimmedSearch),
      excludeNames,
      pageable
    );
  }

  private List<Pattern> createCaseInsensitiveFullMatchPatterns(String commaSeparatedNames) {
    return Arrays.stream(commaSeparatedNames.split(","))
      .map(String::trim)
      .filter(s -> !s.isEmpty())
      .map(name -> Pattern.compile("^" + Pattern.quote(name) + "$", Pattern.CASE_INSENSITIVE))
      .toList();
  }

  /**
   * Builds a Spring Data Sort object using the centralized SortHelper utility.
   * No field transformations needed for Model Overview (uses fields as-is).
   *
   * @param sortFields list of field names to sort by
   * @param sortOrders list of sort directions (1 for ascending, -1 for descending)
   * @return Sort object for use in PageRequest
   * @throws IllegalArgumentException if arrays have mismatched lengths or invalid values
   */
  private Sort buildSort(List<String> sortFields, List<Integer> sortOrders) {
    return SortHelper.buildSort(sortFields, sortOrders, SortHelper.NO_TRANSFORM);
  }
}
