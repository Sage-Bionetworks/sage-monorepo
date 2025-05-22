package org.sagebionetworks.agora.gene.api.service;

import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfilesRnaPageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DifferentialExpressionProfileRnaService {

  private static final Logger logger = LoggerFactory.getLogger(
    DifferentialExpressionProfileRnaService.class
  );

  public DifferentialExpressionProfilesRnaPageDto listDifferentialExpressionProfilesRna(
    DifferentialExpressionProfileRnaSearchQueryDto query
  ) {
    logger.info("listDifferentialExpressionProfilesRna query: {}", query);

    return null;
  }
}
