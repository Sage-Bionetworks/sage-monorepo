package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.explorers.ComparisonToolRepositorySupport;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Custom repository implementation backed by the shared CT aggregation pipeline.
 *
 * <p>ModelOverview has no case-insensitive sort, sort-field aliases, or computed sort fields,
 * so it overrides only the two abstract collection/document hooks. The pipeline scaffold
 * (count, $match, $sort, $skip, $limit) lives in {@link ComparisonToolRepositorySupport}.
 */
@Repository
@Slf4j
public class CustomModelOverviewRepositoryImpl
  extends ComparisonToolRepositorySupport<ModelOverviewDocument>
  implements CustomModelOverviewRepository {

  private static final String COLLECTION_NAME = "model_overview";

  public CustomModelOverviewRepositoryImpl(MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  protected Class<ModelOverviewDocument> getDocumentClass() {
    return ModelOverviewDocument.class;
  }

  @Override
  public Page<ModelOverviewDocument> findAll(
    Pageable pageable,
    ModelOverviewSearchQueryDto query,
    List<String> items
  ) {
    ItemFilterTypeQueryDto filterType = Objects.requireNonNullElse(
      query.getItemFilterType(),
      ItemFilterTypeQueryDto.INCLUDE
    );
    String search = query.getSearch();

    Criteria matchCriteria = buildMatchCriteria(query, items, filterType, search);
    return executePagedAggregation(matchCriteria, pageable);
  }

  private Criteria buildMatchCriteria(
    ModelOverviewSearchQueryDto query,
    List<String> items,
    ItemFilterTypeQueryDto filterType,
    String search
  ) {
    List<Criteria> andCriteria = new ArrayList<>();

    // Add data filters (AND between fields, OR within field)
    addDataFilterCriteria(
      query.getAvailableData(),
      query.getCenter(),
      query.getModelType(),
      query.getModifiedGenes(),
      andCriteria
    );

    // Add name filtering (items + itemFilterType)
    addNameFilterCriteria(items, filterType, andCriteria);

    // Add search filtering (only when itemFilterType is EXCLUDE)
    addSearchFilterCriteria(search, filterType, andCriteria);

    if (andCriteria.isEmpty()) {
      return new Criteria();
    }
    return new Criteria().andOperator(andCriteria.toArray(new Criteria[0]));
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

    // center: string field - use $in (matches if value equals any)
    if (center != null && !center.isEmpty()) {
      andCriteria.add(Criteria.where("center").in(center));
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
      // For INCLUDE mode with empty items, add impossible condition to return empty results
      if (filterType == ItemFilterTypeQueryDto.INCLUDE) {
        andCriteria.add(Criteria.where("_id").is(null));
      }
      // For EXCLUDE mode with empty items, no filtering needed (return all)
      return;
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
