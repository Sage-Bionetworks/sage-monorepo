package org.sagebionetworks.agora.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.ModalityDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugIdentifier;
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

    ModalityDto modalityDto = document.getModality() != null
      ? ModalityDto.fromValue(document.getModality())
      : null;

    NominatedDrugDto dto = new NominatedDrugDto(
      getCompositeId(document),
      document.getCommonName(),
      document.getTotalNominations(),
      document.getCombinedWith(),
      document.getInitialNomination(),
      principalInvestigators,
      programs,
      modalityDto,
      document.getYearOfFirstApproval(),
      document.getMaximumClinicalTrialPhase()
    );
    dto.setChemblId(document.getChemblId());
    return dto;
  }

  private String getCompositeId(NominatedDrugDocument document) {
    return NominatedDrugIdentifier.builder()
      .chemblId(document.getChemblId())
      .combinedWith(document.getCombinedWith())
      .build()
      .toCompositeId();
  }
}
