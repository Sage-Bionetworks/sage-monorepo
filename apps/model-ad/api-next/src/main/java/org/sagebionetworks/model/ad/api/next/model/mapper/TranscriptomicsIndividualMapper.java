package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.IndividualDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIndividualDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TranscriptomicsIndividualMapper {

  private final IndividualDataMapper individualDataMapper;

  public TranscriptomicsIndividualDto toDto(@Nullable TranscriptomicsIndividualDocument document) {
    if (document == null) {
      return null;
    }

    List<String> resultOrder = document.getResultOrder() == null
      ? List.of()
      : List.copyOf(document.getResultOrder());

    List<IndividualDataDto> data = document.getData() == null
      ? List.of()
      : document.getData().stream().map(individualDataMapper::toIndividualDataDto).toList();

    TranscriptomicsIndividualDto dto = new TranscriptomicsIndividualDto(
      document.getEnsemblGeneId(),
      document.getGeneSymbol(),
      document.getTissue(),
      document.getName(),
      document.getMatchedControl(),
      document.getUnits(),
      document.getAge(),
      document.getAgeNumeric(),
      resultOrder,
      data
    );

    dto.setModelGroup(document.getModelGroup());
    return dto;
  }
}
