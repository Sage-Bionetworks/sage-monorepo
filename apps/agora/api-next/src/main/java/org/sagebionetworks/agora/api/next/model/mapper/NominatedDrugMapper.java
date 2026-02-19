package org.sagebionetworks.agora.api.next.model.mapper;

import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class NominatedDrugMapper {

  public NominatedDrugDto toDto(@Nullable NominatedDrugDocument document) {
    if (document == null) {
      return null;
    }

    return new NominatedDrugDto(
      document.getCommonName(),
      document.getTotalNominations(),
      document.getYearFirstNominated(),
      document.getPrincipalInvestigators(),
      document.getPrograms()
    );
  }
}
