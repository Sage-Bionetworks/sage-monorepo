package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.sagebionetworks.model.ad.api.next.util.ComparisonToolApiHelper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "diseaseCorrelation")
public class DiseaseCorrelationQueryService {

  private final MongoTemplate mongoTemplate;
  private final DiseaseCorrelationMapper diseaseCorrelationMapper;

  public DiseaseCorrelationQueryService(
    MongoTemplate mongoTemplate,
    DiseaseCorrelationMapper diseaseCorrelationMapper
  ) {
    this.mongoTemplate = mongoTemplate;
    this.diseaseCorrelationMapper = diseaseCorrelationMapper;
  }

  @Cacheable(
    key = "T(org.sagebionetworks.model.ad.api.next.util.ComparisonToolApiHelper)" +
    ".buildCacheKey('diseaseCorrelation', #filterType, #items, #cluster)"
  )
  public List<DiseaseCorrelationDto> loadDiseaseCorrelations(
    String cluster,
    List<String> items,
    ItemFilterTypeQueryDto filterType
  ) {
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      filterType,
      ItemFilterTypeQueryDto.INCLUDE
    );

    Query query = new Query();
    query.addCriteria(Criteria.where("cluster").is(cluster));

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

    List<DiseaseCorrelationDocument> documents = mongoTemplate.find(
      query,
      DiseaseCorrelationDocument.class
    );

    return documents
      .stream()
      .map(diseaseCorrelationMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
  }
}
