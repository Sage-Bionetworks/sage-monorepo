package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionProfileDocument;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RnaDifferentialExpressionProfileRepositoryCustom {
  Page<RnaDifferentialExpressionProfileDocument> findAll(
    Pageable pageable,
    RnaDifferentialExpressionProfileSearchQueryDto query
  );
}
