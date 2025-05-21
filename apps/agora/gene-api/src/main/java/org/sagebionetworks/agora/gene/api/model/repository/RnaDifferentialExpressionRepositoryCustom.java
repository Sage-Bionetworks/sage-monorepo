package org.sagebionetworks.agora.gene.api.model.repository;

import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionDocument;

public interface RnaDifferentialExpressionRepositoryCustom {
  List<RnaDifferentialExpressionDocument> findByModelSorted(String model);
}
