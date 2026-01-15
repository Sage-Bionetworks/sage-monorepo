package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.IndividualDataDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeneExpressionIndividualMapper {

  private final IndividualDataMapper individualDataMapper;

  public GeneExpressionIndividualDto toDto(@Nullable GeneExpressionIndividualDocument document) {
    if (document == null) {
      return null;
    }

    List<String> resultOrder = document.getResultOrder() == null
      ? List.of()
      : List.copyOf(document.getResultOrder());

    List<IndividualDataDto> data = document.getData() == null
      ? List.of()
      : document.getData().stream().map(individualDataMapper::toIndividualDataDto).toList();

    GeneExpressionIndividualDto dto = new GeneExpressionIndividualDto(
      document.getEnsemblGeneId(),
      document.getTissue(),
      document.getName(),
      document.getMatchedControl(),
      document.getUnits(),
      document.getAge(),
      document.getAgeNumeric(),
      resultOrder,
      data
    );

    dto.setGeneSymbol(document.getGeneSymbol());
    dto.setModelGroup(document.getModelGroup());
    return dto;
  }
}
