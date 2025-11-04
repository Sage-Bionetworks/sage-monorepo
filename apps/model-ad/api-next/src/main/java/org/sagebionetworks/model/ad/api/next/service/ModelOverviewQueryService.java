package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.util.ComparisonToolApiHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "modelOverview")
public class ModelOverviewQueryService {

  private final MongoTemplate mongoTemplate;
  private final ModelOverviewMapper modelOverviewMapper;

  public ModelOverviewQueryService(
    MongoTemplate mongoTemplate,
    ModelOverviewMapper modelOverviewMapper
  ) {
    this.mongoTemplate = mongoTemplate;
    this.modelOverviewMapper = modelOverviewMapper;
  }

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ComparisonToolApiHelper)" +
    ".buildCacheKey('modelOverview', #filterType, #items)"
  )
  public List<ModelOverviewDto> loadModelOverviews(
    List<String> items,
    ItemFilterTypeQueryDto filterType
  ) {
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      filterType,
      ItemFilterTypeQueryDto.INCLUDE
    );

    Query query = new Query();
    if (!items.isEmpty()) {
      List<ObjectId> objectIds = ComparisonToolApiHelper.parseObjectIds(items);
      Criteria idCriteria = Criteria.where("_id");
      if (effectiveFilter == ItemFilterTypeQueryDto.INCLUDE) {
        idCriteria.in(objectIds);
      } else {
        idCriteria.nin(objectIds);
      }
      query.addCriteria(idCriteria);
    }

    List<ModelOverviewDocument> documents = mongoTemplate.find(query, ModelOverviewDocument.class);
    return documents
      .stream()
      .map(modelOverviewMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
  }
}
