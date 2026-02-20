package org.sagebionetworks.agora.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NominatedTargetMapper {

  public NominatedTargetDto toDto(@Nullable NominatedTargetDocument document) {
    if (document == null) {
      return null;
    }

    List<String> nominatingTeams = document.getNominatingTeams() == null
      ? List.of()
      : List.copyOf(document.getNominatingTeams());
    List<String> cohortStudies = document.getCohortStudies() == null
      ? List.of()
      : List.copyOf(document.getCohortStudies());
    List<String> inputData = document.getInputData() == null
      ? List.of()
      : List.copyOf(document.getInputData());
    List<String> programs = document.getPrograms() == null
      ? List.of()
      : List.copyOf(document.getPrograms());

    return new NominatedTargetDto(
      document.getId() != null ? document.getId().toHexString() : null,
      document.getEnsemblGeneId(),
      document.getHgncSymbol(),
      document.getTotalNominations(),
      document.getInitialNomination(),
      nominatingTeams,
      cohortStudies,
      inputData,
      programs,
      document.getPharosClass()
    );
  }
}
