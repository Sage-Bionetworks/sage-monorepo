package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ModelOverviewRepositoryCustomImpl implements ModelOverviewRepositoryCustom {

  private final MongoTemplate mongoTemplate;

  @Override
  public Page<ModelOverviewDocument> findWithFilters(
    List<String> items,
    String search,
    ItemFilterTypeQueryDto filterType,
    List<String> availableData,
    List<String> center,
    List<String> modelType,
    List<String> modifiedGenes,
    Pageable pageable
  ) {
    Query mongoQuery = new Query();
    List<Criteria> andCriteria = new ArrayList<>();

    // Add data filters (AND between fields, OR within field)
    addDataFilterCriteria(availableData, center, modelType, modifiedGenes, andCriteria);

    // Add name filtering (items + itemFilterType)
    addNameFilterCriteria(items, filterType, andCriteria);

    // Add search filtering (only when itemFilterType is EXCLUDE)
    addSearchFilterCriteria(search, filterType, andCriteria);

    // Combine all criteria with AND
    if (!andCriteria.isEmpty()) {
      mongoQuery.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
    }

    mongoQuery.with(pageable);

    // Execute query
    List<ModelOverviewDocument> results = mongoTemplate.find(
      mongoQuery,
      ModelOverviewDocument.class
    );

    // Count total for pagination (without limit/skip)
    long total = mongoTemplate.count(
      Query.of(mongoQuery).limit(-1).skip(-1),
      ModelOverviewDocument.class
    );

    return new PageImpl<>(results, pageable, total);
  }

  private void addDataFilterCriteria(
    List<String> availableData,
    List<String> center,
    List<String> modelType,
    List<String> modifiedGenes,
    List<Criteria> andCriteria
  ) {
    // available_data: array field - use $in (matches if array contains any value)
    if (availableData != null && !availableData.isEmpty()) {
      andCriteria.add(Criteria.where("available_data").in(availableData));
    }

    // center: nested field - query center.link_text
    if (center != null && !center.isEmpty()) {
      andCriteria.add(Criteria.where("center.link_text").in(center));
    }

    // model_type: string field - use $in (matches if value equals any)
    if (modelType != null && !modelType.isEmpty()) {
      andCriteria.add(Criteria.where("model_type").in(modelType));
    }

    // modified_genes: array field - use $in (matches if array contains any value)
    if (modifiedGenes != null && !modifiedGenes.isEmpty()) {
      andCriteria.add(Criteria.where("modified_genes").in(modifiedGenes));
    }
  }

  private void addNameFilterCriteria(
    List<String> items,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    if (items.isEmpty()) {
      return; // No name filtering needed
    }

    if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
      andCriteria.add(Criteria.where("name").in(items));
    } else {
      andCriteria.add(Criteria.where("name").nin(items));
    }
  }

  private void addSearchFilterCriteria(
    String search,
    ItemFilterTypeQueryDto filterType,
    List<Criteria> andCriteria
  ) {
    // Search only applies when itemFilterType is EXCLUDE
    if (
      filterType != ItemFilterTypeQueryDto.EXCLUDE || search == null || search.trim().isEmpty()
    ) {
      return;
    }

    String trimmedSearch = search.trim();
    if (trimmedSearch.contains(",")) {
      // Comma-separated list: case-insensitive full matches
      List<Pattern> patterns = ApiHelper.createCaseInsensitiveFullMatchPatterns(trimmedSearch);
      andCriteria.add(Criteria.where("name").in(patterns));
    } else {
      // Single term: case-insensitive partial match
      String quotedSearch = Pattern.quote(trimmedSearch);
      andCriteria.add(Criteria.where("name").regex(quotedSearch, "i"));
    }
  }
}
