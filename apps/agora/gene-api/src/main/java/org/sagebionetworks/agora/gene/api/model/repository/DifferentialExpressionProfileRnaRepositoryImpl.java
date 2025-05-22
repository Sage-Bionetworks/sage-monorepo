package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.DifferentialExpressionProfileRnaDocument;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSearchQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class DifferentialExpressionProfileRnaRepositoryImpl
  implements DifferentialExpressionProfileRnaRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(
    DifferentialExpressionProfileRnaRepositoryImpl.class
  );

  @Override
  public Page<DifferentialExpressionProfileRnaDocument> findAll(
    Pageable pageable,
    DifferentialExpressionProfileRnaSearchQueryDto query
  ) {
    return null;
  }
}
