package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.IndividualDataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProteomicsIndividualMapper {

  private final IndividualDataMapper individualDataMapper;

  public ProteomicsIndividualDto toDto(@Nullable ProteomicsIndividualDocument document) {
    if (document == null) {
      return null;
    }

    List<String> resultOrder = document.getResultOrder() == null
      ? List.of()
      : List.copyOf(document.getResultOrder());

    List<IndividualDataDto> data = document.getData() == null
      ? List.of()
      : document.getData().stream().map(individualDataMapper::toIndividualDataDto).toList();

    ProteomicsIndividualDto dto = new ProteomicsIndividualDto(
      document.getEnsemblGeneId(),
      document.getGeneSymbol(),
      document.getUniprotid(),
      document.getUniqueId(),
      document.getDisplaySymbol(),
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
