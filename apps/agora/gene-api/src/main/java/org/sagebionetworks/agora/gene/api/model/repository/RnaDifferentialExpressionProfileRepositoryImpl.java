package org.sagebionetworks.agora.gene.api.model.repository;

import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionProfileDocument;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.SortDirectionDto;
import org.sagebionetworks.agora.gene.api.model.mapper.RnaDifferentialExpressionProfileModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RnaDifferentialExpressionProfileRepositoryImpl
  implements RnaDifferentialExpressionProfileRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(
    RnaDifferentialExpressionProfileRepositoryImpl.class
  );

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public Page<RnaDifferentialExpressionProfileDocument> findAll(
    Pageable pageable,
    RnaDifferentialExpressionProfileSearchQueryDto query
  ) {
    Criteria criteria = new Criteria();
    List<Criteria> criteriaList = new ArrayList<>();

    // Filter by model
    if (query.getModel() != null) {
      String storedModelValue = RnaDifferentialExpressionProfileModelMapper.mapToStoredValue(
        query.getModel()
      );
      if (storedModelValue != null) {
        criteriaList.add(Criteria.where("model").is(storedModelValue));
      }
    }

    // Filter by search term
    if (query.getSearchTerm() != null && !query.getSearchTerm().isBlank()) {
      criteriaList.add(Criteria.where("hgnc_symbol").regex(query.getSearchTerm(), "i"));
    }

    if (!criteriaList.isEmpty()) {
      criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    Query mongoQuery = new Query(criteria);

    // Sorting
    if (query.getSort() != null) {
      String sortField = mapSortField(query.getSort());
      Sort.Direction direction = (query.getDirection() == SortDirectionDto.DESC)
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
      mongoQuery.with(Sort.by(direction, sortField));
    }

    // Pagination
    mongoQuery.with(pageable);

    List<RnaDifferentialExpressionProfileDocument> results = mongoTemplate.find(
      mongoQuery,
      RnaDifferentialExpressionProfileDocument.class
    );

    long total = mongoTemplate.count(
      mongoQuery.skip(-1).limit(-1),
      RnaDifferentialExpressionProfileDocument.class
    );

    return new PageImpl<>(results, pageable, total);
  }

  private String mapSortField(RnaDifferentialExpressionProfileSortDto sortDto) {
    return switch (sortDto) {
      case HGNC_SYMBOL -> RnaDifferentialExpressionProfileSortDto.HGNC_SYMBOL.getValue();
      default -> RnaDifferentialExpressionProfileSortDto.HGNC_SYMBOL.getValue();
    };
  }
}
