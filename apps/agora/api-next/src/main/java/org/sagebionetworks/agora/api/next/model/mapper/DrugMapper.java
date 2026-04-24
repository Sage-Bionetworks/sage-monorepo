package org.sagebionetworks.agora.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.agora.api.next.model.document.DrugDocument;
import org.sagebionetworks.agora.api.next.model.document.DrugNominationEvidenceDocument;
import org.sagebionetworks.agora.api.next.model.document.LinkedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.DrugDto;
import org.sagebionetworks.agora.api.next.model.dto.LinkedTargetDto;
import org.sagebionetworks.agora.api.next.model.dto.ModalityDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugEvidenceDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DrugMapper {

  public DrugDto toDto(@Nullable DrugDocument document) {
    if (document == null) {
      return null;
    }

    List<String> aliases = document.getAliases() == null
      ? List.of()
      : List.copyOf(document.getAliases());

    List<String> mechanismsOfAction = document.getMechanismsOfAction() == null
      ? List.of()
      : List.copyOf(document.getMechanismsOfAction());

    ModalityDto modalityDto = document.getModality() != null
      ? ModalityDto.fromValue(document.getModality())
      : null;

    List<LinkedTargetDto> linkedTargets = document.getLinkedTargets() == null
      ? List.of()
      : document.getLinkedTargets().stream().map(this::toLinkedTargetDto).toList();

    List<NominatedDrugEvidenceDto> drugNominations = document.getDrugNominations() == null
      ? List.of()
      : document.getDrugNominations().stream().map(this::toEvidenceDto).toList();

    return new DrugDto(
      document.getCommonName(),
      document.getDescription(),
      document.getIupacId(),
      document.getChemblId(),
      document.getDrugBankId(),
      aliases,
      modalityDto,
      document.getYearOfFirstApproval(),
      document.getMaximumClinicalTrialPhase(),
      linkedTargets,
      mechanismsOfAction,
      drugNominations
    );
  }

  private LinkedTargetDto toLinkedTargetDto(LinkedTargetDocument document) {
    return new LinkedTargetDto(document.getEnsemblGeneId(), document.getHgncSymbol());
  }

  private NominatedDrugEvidenceDto toEvidenceDto(DrugNominationEvidenceDocument document) {
    return new NominatedDrugEvidenceDto(
      document.getGrantNumber(),
      document.getContactPi(),
      document.getCombinedWithCommonName(),
      document.getCombinedWithChemblId(),
      document.getEvidence(),
      document.getDataUsed(),
      document.getAdMoa(),
      document.getReference(),
      document.getComputationalValidationStatus(),
      document.getComputationalValidationResults(),
      document.getExperimentalValidationStatus(),
      document.getExperimentalValidationResults(),
      document.getAdditionalEvidence(),
      document.getContributors(),
      document.getInitialNomination(),
      document.getProgram()
    );
  }
}
