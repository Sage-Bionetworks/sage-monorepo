package org.sagebionetworks.agora.gene.api.model.repository;

import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.DifferentialExpressionProfileRnaDocument;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSortDto;
import org.sagebionetworks.agora.gene.api.model.dto.SortDirectionDto;
import org.sagebionetworks.agora.gene.api.model.mapper.RnaModelMapper;
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
public class DifferentialExpressionProfileRnaRepositoryImpl
  implements DifferentialExpressionProfileRnaRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(
    DifferentialExpressionProfileRnaRepositoryImpl.class
  );

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public Page<DifferentialExpressionProfileRnaDocument> findAll(
    Pageable pageable,
    DifferentialExpressionProfileRnaSearchQueryDto query
  ) {
    Criteria criteria = new Criteria();
    List<Criteria> criteriaList = new ArrayList<>();

    // Filter by model
    // if (query.getModel() != null) {
    //   String storedModelValue = RnaModelMapper.mapToStoredValue(query.getModel());
    //   if (storedModelValue != null) {
    //     criteriaList.add(Criteria.where("model").is(storedModelValue));
    //   }
    // }

    // Filter by search terms (e.g., gene name or description field)
    // if (query.getSearchTerms() != null && !query.getSearchTerms().isBlank()) {
    //   criteriaList.add(
    //     new Criteria()
    //       .orOperator(
    //         Criteria.where("gene").regex("^" + query.getSearchTerms(), "i"),
    //         Criteria.where("description").regex("^" + query.getSearchTerms(), "i")
    //       )
    //   );
    // }

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

    List<DifferentialExpressionProfileRnaDocument> results = mongoTemplate.find(
      mongoQuery,
      DifferentialExpressionProfileRnaDocument.class
    );

    long total = mongoTemplate.count(
      mongoQuery.skip(-1).limit(-1),
      DifferentialExpressionProfileRnaDocument.class
    );

    return new PageImpl<>(results, pageable, total);
  }

  // TODO: Use meaningful options
  private String mapSortField(DifferentialExpressionProfileRnaSortDto sortDto) {
    return switch (sortDto) {
      case CREATED -> "hgnc_symbol";
      case RELEVANCE -> "hgnc_symbol"; // assuming you rank by a "score" field
      default -> "hgnc_symbol";
    };
  }
}
