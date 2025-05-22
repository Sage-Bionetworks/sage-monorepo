package org.sagebionetworks.agora.gene.api.model.repository;

import org.sagebionetworks.agora.gene.api.model.document.DifferentialExpressionProfileRnaDocument;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DifferentialExpressionProfileRnaRepositoryCustom {
  Page<DifferentialExpressionProfileRnaDocument> findAll(
    Pageable pageable,
    DifferentialExpressionProfileRnaSearchQueryDto query
  );
}
