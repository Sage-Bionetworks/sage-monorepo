package org.sagebionetworks.agora.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NominatedDrugMapper {

  public NominatedDrugDto toDto(@Nullable NominatedDrugDocument document) {
    if (document == null) {
      return null;
    }

    List<String> principalInvestigators = document.getPrincipalInvestigators() == null
      ? List.of()
      : List.copyOf(document.getPrincipalInvestigators());
    List<String> programs = document.getPrograms() == null
      ? List.of()
      : List.copyOf(document.getPrograms());

    return new NominatedDrugDto(
      document.getCommonName(),
      document.getTotalNominations(),
      document.getYearFirstNominated(),
      principalInvestigators,
      programs
    );
  }
}
