package org.sagebionetworks.agora.gene.api.model.repository;

import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class RnaDifferentialExpressionRepositoryImpl
  implements RnaDifferentialExpressionRepositoryCustom {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<RnaDifferentialExpressionDocument> findByModelSorted(String model) {
    Query query = new Query();
    query.addCriteria(Criteria.where("model").is(model));
    query.with(Sort.by(Sort.Order.asc("hgnc_symbol"), Sort.Order.asc("tissue")));
    // query.limit(5);
    return mongoTemplate.find(query, RnaDifferentialExpressionDocument.class);
  }
}
