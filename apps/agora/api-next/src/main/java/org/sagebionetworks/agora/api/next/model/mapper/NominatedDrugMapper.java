package org.sagebionetworks.agora.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.agora.api.next.model.document.LinkDocument;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.LinkDto;
import org.sagebionetworks.agora.api.next.model.dto.ModalityDto;
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

    LinkDto combinedWithDto = mapLink(document.getCombinedWith());
    ModalityDto modalityDto = document.getModality() != null
      ? ModalityDto.fromValue(document.getModality())
      : null;

    NominatedDrugDto dto = new NominatedDrugDto(
      document.getCommonName(),
      document.getTotalNominations(),
      combinedWithDto,
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

  private LinkDto mapLink(@Nullable LinkDocument linkDocument) {
    if (linkDocument == null) {
      return null;
    }
    return new LinkDto()
      .linkText(linkDocument.getLinkText())
      .linkUrl(linkDocument.getLinkUrl());
  }
}
